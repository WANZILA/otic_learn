package com.otic.learn.domain.model

data class User(
    val id: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val role: UserRole
)

enum class UserRole {
    STUDENT,
    INSTRUCTOR,
    ADMIN
}
