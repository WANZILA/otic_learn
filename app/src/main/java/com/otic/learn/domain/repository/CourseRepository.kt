package com.otic.learn.domain.repository

import com.otic.learn.domain.model.Course
import com.otic.learn.domain.model.Enrollment
import kotlinx.coroutines.flow.Flow

interface CourseRepository {

    fun observeEnrolledCourses(
        studentId: String
    ): Flow<List<Pair<Course, Enrollment>>>

    fun observeAllCourses(): Flow<List<Course>>

    // NEW: enroll student in a course
    suspend fun enroll(studentId: String, courseId: String)

    // NEW: check if student is already enrolled
    fun observeEnrollment(studentId: String, courseId: String): Flow<Enrollment?>
}
