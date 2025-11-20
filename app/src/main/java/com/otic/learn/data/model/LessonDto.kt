package com.otic.learn.data.model

data class LessonDto(
    val title: String = "",
    val content: String = "",
    val videoUrl: String? = null,
    val order: Int = 0
)
