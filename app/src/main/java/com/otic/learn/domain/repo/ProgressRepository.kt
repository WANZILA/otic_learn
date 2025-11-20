package com.otic.learn.domain.repo

import com.otic.learn.domain.model.Progress
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    fun observeProgress(): Flow<Map<String, Progress>>
    suspend fun completeLesson(courseId: String, lessonId: String)
}
