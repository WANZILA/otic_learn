package com.otic.learn.domain.usecase

import com.otic.learn.domain.model.Course
import com.otic.learn.domain.model.Enrollment
import com.otic.learn.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveEnrolledCoursesUseCase @Inject constructor(
    private val courseRepository: CourseRepository
) {
    operator fun invoke(studentId: String): Flow<List<Pair<Course, Enrollment>>> {
        return courseRepository.observeEnrolledCourses(studentId)
    }
}
