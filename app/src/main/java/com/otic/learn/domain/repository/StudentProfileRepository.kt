package com.otic.learn.domain.repository

import com.otic.learn.domain.model.StudentProfile
import kotlinx.coroutines.flow.Flow

interface StudentProfileRepository {

    fun observeProfile(userId: String): Flow<StudentProfile?>

    suspend fun getProfile(userId: String): StudentProfile?

    suspend fun upsertProfile(profile: StudentProfile)
}
