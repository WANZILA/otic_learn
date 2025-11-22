package com.otic.learn.presentation.messages

data class ChatUiState(
    val isLoading: Boolean = true,
    val messages: List<ChatMessageItem> = emptyList(),
    val inputText: String = "",
    val errorMessage: String? = null
)

data class ChatMessageItem(
    val id: String,
    val text: String,
    val isMine: Boolean
)
