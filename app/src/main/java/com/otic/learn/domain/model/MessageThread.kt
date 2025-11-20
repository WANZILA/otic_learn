package com.otic.learn.domain.model

data class MessageThread(
    val id: String,
    val studentId: String,
    val instructorId: String,
    val lastMessagePreview: String?,
    val unreadCountForStudent: Int,
    val updatedAtMillis: Long
)

data class Message(
    val id: String,
    val threadId: String,
    val senderId: String,
    val text: String,
    val createdAtMillis: Long
)
