package com.otic.learn.domain.model

data class Course(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String? = null,
    val lessonCount: Int = 0
)
