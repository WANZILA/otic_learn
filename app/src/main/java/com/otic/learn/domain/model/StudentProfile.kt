package com.otic.learn.domain.model

import java.time.LocalDate

data class StudentProfile(
    val userId: String,
    val firstName: String?,
    val lastName: String?,
    val phoneNumber: String?,
    val location: String?,
    val dateOfBirth: LocalDate?,
    val bio: String?,
    val jobTitle: String?,
    val company: String?,
    val yearsOfExperience: Int?,
    val specializations: List<String>,
    val website: String?,
    val github: String?,
    val linkedin: String?,
    val twitter: String?,
    val facebook: String?,
    val language: String,
    val timezone: String,
    val emailNotificationsEnabled: Boolean,
    val browserNotificationsEnabled: Boolean,
    val marketingEmailsEnabled: Boolean,
    val memberSinceEpochMillis: Long,
    val profileVisibility: ProfileVisibility
)

enum class ProfileVisibility {
    PUBLIC,
    PRIVATE
}
