package com.otic.learn.domain.model

data class Course(
    val id: String,
    val title: String,
    val category: String?,
    val coverImageUrl: String?,
    val level: String?,              // e.g. "Beginner", "Intermediate"
    val shortDescription: String?,
    val instructorId: String,
    val createdAtMillis: Long
)
