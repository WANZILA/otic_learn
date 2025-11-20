package com.otic.learn.domain.model

enum class NotePriority {
    LOW,
    MEDIUM,
    HIGH
}

data class Note(
    val id: String,
    val studentId: String,
    val title: String,
    val content: String,
    val category: String?,
    val priority: NotePriority,
    val isPinned: Boolean,
    val createdAtMillis: Long,
    val updatedAtMillis: Long
)
