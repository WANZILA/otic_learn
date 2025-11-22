package com.otic.learn.presentation.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

@HiltViewModel
class AiAssistantViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AiAssistantUiState())
    val uiState: StateFlow<AiAssistantUiState> = _uiState.asStateFlow()

    fun onModeSelected(mode: AiMode) {
        if (mode == _uiState.value.mode) return
        _uiState.update {
            it.copy(
                mode = mode,
                messages = emptyList(),    // clear chat when switching mode
                inputText = "",
                isThinking = false,
                errorMessage = null
            )
        }
    }

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun onSendClicked() {
        val state = _uiState.value
        val prompt = state.inputText.trim()
        if (prompt.isEmpty() || state.isThinking) return

        val userMessage = AiChatMessage(
            id = UUID.randomUUID().toString(),
            text = prompt,
            isFromUser = true
        )

        _uiState.update {
            it.copy(
                messages = it.messages + userMessage,
                inputText = "",
                isThinking = true
            )
        }

        viewModelScope.launch {
            try {
                // Fake AI thinking delay for demo
                delay(600)

                val replyText = when (state.mode) {
                    AiMode.TUTOR -> buildTutorReply(prompt)
                    AiMode.FLASHCARDS -> buildFlashcardReply(prompt)
                }

                val aiMessage = AiChatMessage(
                    id = UUID.randomUUID().toString(),
                    text = replyText,
                    isFromUser = false
                )

                _uiState.update {
                    it.copy(
                        messages = it.messages + aiMessage,
                        isThinking = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isThinking = false,
                        errorMessage = e.message ?: "AI assistant error"
                    )
                }
            }
        }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // --- Fake AI logic for MVP demo ---

    private fun buildTutorReply(prompt: String): String {
        return """
            Great question! Here's how you can think about it:

            • You asked: "$prompt"
            • Step 1: Break the topic into smaller parts.
            • Step 2: Focus on the most important concept first.
            • Step 3: Practice with 1–2 small examples.

            (Demo mode: this is a scripted reply. Later we’ll connect it to a real AI backend.)
        """.trimIndent()
    }

    private fun buildFlashcardReply(prompt: String): String {
        return """
            Here are some practice flashcards based on: "$prompt"

            Q1: What is the main idea of this topic?
            A1: Summarize it in one or two sentences.

            Q2: Why is this concept important?
            A2: Describe a real situation where you would use it.

            Q3: What is one common mistake students make here?
            A3: Explain the mistake and how to avoid it.

            (Demo mode: later we'll generate real flashcards from your content.)
        """.trimIndent()
    }
}
