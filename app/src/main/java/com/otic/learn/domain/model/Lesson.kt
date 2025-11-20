package com.otic.learn.domain.model

data class Lesson(
    val id: String,
    val courseId: String,
    val title: String,
    val content: String,
    val videoUrl: String? = null,
    val order: Int = 0
)
