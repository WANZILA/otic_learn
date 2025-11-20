package com.otic.learn.domain.repo

import com.otic.learn.domain.model.Course
import com.otic.learn.domain.model.Lesson
import kotlinx.coroutines.flow.Flow

interface CoursesRepository {
    fun observeCourses(): Flow<List<Course>>
    fun observeLessons(courseId: String): Flow<List<Lesson>>
    suspend fun getLesson(courseId: String, lessonId: String): Lesson?
}
