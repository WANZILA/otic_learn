package com.otic.learn.domain.usecase

import com.otic.learn.domain.model.Course
import com.otic.learn.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class CourseCatalogUseCases @Inject constructor(
    val observeAllCourses: ObserveAllCourses
)

class ObserveAllCourses @Inject constructor(
    private val courseRepository: CourseRepository
) {
    operator fun invoke(): Flow<List<Course>> {
        return courseRepository.observeAllCourses()
    }
}
