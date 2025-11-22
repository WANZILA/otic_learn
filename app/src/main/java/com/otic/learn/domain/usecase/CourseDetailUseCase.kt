package com.otic.learn.domain.usecase

import com.otic.learn.domain.model.Enrollment
import com.otic.learn.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class CourseDetailUseCases @Inject constructor(
    val observeEnrollment: ObserveEnrollment,
    val enrollInCourse: EnrollInCourse
)

class ObserveEnrollment @Inject constructor(
    private val repo: CourseRepository
) {
    operator fun invoke(studentId: String, courseId: String): Flow<Enrollment?> =
        repo.observeEnrollment(studentId, courseId)
}

class EnrollInCourse @Inject constructor(
    private val repo: CourseRepository
) {
    suspend operator fun invoke(studentId: String, courseId: String) {
        repo.enroll(studentId, courseId)
    }
}
