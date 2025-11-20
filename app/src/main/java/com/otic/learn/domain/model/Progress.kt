package com.otic.learn.domain.model

data class Progress(
    val courseId: String,
    val completedLessonIds: List<String> = emptyList(),
    val lastLessonId: String? = null
)
