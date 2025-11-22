package com.otic.learn.presentation.messages

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otic.learn.domain.usecase.AuthUseCases
import com.otic.learn.domain.usecase.MessagesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val messagesUseCases: MessagesUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val threadId: String = savedStateHandle["threadId"] ?: ""

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null
    private var messagesJob: Job? = null

    init {
        viewModelScope.launch {
            authUseCases.observeCurrentUser().collect { user ->
                currentUserId = user?.id

                if (user == null || threadId.isBlank()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Missing user or thread"
                        )
                    }
                    messagesJob?.cancel()
                    return@collect
                }

                observeMessages()
            }
        }
    }

    private fun observeMessages() {
        messagesJob?.cancel()
        messagesJob = viewModelScope.launch {
            messagesUseCases.observeMessagesForThread(threadId)
                .collect { messages ->
                    val myId = currentUserId
                    val items = messages.map { msg ->
                        ChatMessageItem(
                            id = msg.id,
                            text = msg.text,
                            isMine = (myId != null && msg.senderId == myId)
                        )
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            messages = items
                        )
                    }
                }
        }
    }

    fun onInputChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun onSendClicked() {
        val text = _uiState.value.inputText.trim()
        val senderId = currentUserId
        if (text.isEmpty() || senderId == null || threadId.isBlank()) return

        _uiState.update { it.copy(inputText = "") }

        viewModelScope.launch {
            try {
                messagesUseCases.sendMessage(
                    threadId = threadId,
                    senderId = senderId,
                    text = text
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Failed to send message")
                }
            }
        }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
