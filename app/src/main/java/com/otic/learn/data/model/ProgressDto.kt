package com.otic.learn.data.model

data class ProgressDto(
    val completedLessonIds: List<String> = emptyList(),
    val lastLessonId: String? = null
)
