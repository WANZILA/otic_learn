package com.otic.learn.domain.model

enum class EnrollmentStatus {
    ACTIVE,
    COMPLETED
}

data class Enrollment(
    val id: String,
    val courseId: String,
    val studentId: String,
    val status: EnrollmentStatus,
    val startDateMillis: Long,
    val endDateMillis: Long?,
    val percentComplete: Int,
    val lastAccessedAtMillis: Long?
)
