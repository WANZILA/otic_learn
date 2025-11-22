package com.otic.learn.presentation.messages

data class MessagesUiState(
    val isLoading: Boolean = true,
    val threads: List<ThreadItem> = emptyList(),
    val errorMessage: String? = null
)

data class ThreadItem(
    val threadId: String,
    val instructorId: String,
    val instructorName: String,      // for now we’ll show instructorId as name
    val lastMessagePreview: String?,
    val unreadCount: Int
)
