package com.otic.learn.presentation.ai

data class AiAssistantUiState(
    val mode: AiMode = AiMode.TUTOR,
    val messages: List<AiChatMessage> = emptyList(),
    val inputText: String = "",
    val isThinking: Boolean = false,
    val errorMessage: String? = null
)

enum class AiMode {
    TUTOR,
    FLASHCARDS
}

data class AiChatMessage(
    val id: String,
    val text: String,
    val isFromUser: Boolean
)
