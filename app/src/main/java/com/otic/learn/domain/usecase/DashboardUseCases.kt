package com.otic.learn.domain.usecase

import com.otic.learn.domain.model.Course
import com.otic.learn.domain.model.Enrollment
import com.otic.learn.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class DashboardUseCases @Inject constructor(
    val observeStudentCourses: ObserveStudentCourses
)

/**
 * For now, the dashboard just needs the list of enrolled courses for a student.
 * Later we can add more (stats, recommended courses, etc.).
 */
class ObserveStudentCourses @Inject constructor(
    private val courseRepository: CourseRepository
) {
    operator fun invoke(studentId: String): Flow<List<Pair<Course, Enrollment>>> {
        return courseRepository.observeEnrolledCourses(studentId)
    }
}
