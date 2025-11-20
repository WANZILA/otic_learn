package com.otic.learn.domain.repository

import com.otic.learn.domain.model.Course
import com.otic.learn.domain.model.Enrollment
import kotlinx.coroutines.flow.Flow

interface CourseRepository {

    fun observeEnrolledCourses(
        studentId: String
    ): Flow<List<Pair<Course, Enrollment>>>
}
