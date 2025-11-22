package com.otic.learn.presentation.notifications

data class NotificationsUiState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedFilter: NotificationFilter = NotificationFilter.ALL,
    val notifications: List<NotificationItem> = emptyList(),
    val errorMessage: String? = null
)

enum class NotificationFilter {
    ALL,
    MESSAGE,
    COURSE,
    FORUM,
    SYSTEM
}

data class NotificationItem(
    val id: String,
    val title: String,
    val bodyPreview: String,
    val typeLabel: String,
    val isRead: Boolean
)
