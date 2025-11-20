package com.otic.learn.domain.model

data class Certificate(
    val id: String,
    val studentId: String,
    val courseId: String,
    val issuedAtMillis: Long,
    val downloadUrl: String?
)
