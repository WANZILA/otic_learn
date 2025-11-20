package com.otic.learn.domain.model

enum class NotificationType {
    MESSAGE,
    FORUM,
    COURSE,
    SYSTEM
}

data class Notification(
    val id: String,
    val userId: String,
    val title: String,
    val body: String,
    val type: NotificationType,
    val isRead: Boolean,
    val createdAtMillis: Long,
    val targetType: String?,    // e.g. "course", "thread"
    val targetId: String?
)
