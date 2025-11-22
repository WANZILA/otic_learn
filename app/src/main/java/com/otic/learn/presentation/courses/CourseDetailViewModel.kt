package com.otic.learn.presentation.courses

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otic.learn.domain.usecase.AuthUseCases
import com.otic.learn.domain.usecase.CourseCatalogUseCases
import com.otic.learn.domain.usecase.CourseDetailUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CourseDetailViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val catalogUseCases: CourseCatalogUseCases,
    private val detailUseCases: CourseDetailUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val courseId: String = savedStateHandle["courseId"] ?: ""

    private val _uiState = MutableStateFlow(CourseDetailUiState())
    val uiState: StateFlow<CourseDetailUiState> = _uiState.asStateFlow()

    private var enrollmentJob: Job? = null
    private var currentStudentId: String? = null

    init {
        loadCourseAndEnrollment()
    }

    private fun loadCourseAndEnrollment() {
        viewModelScope.launch {
            authUseCases.observeCurrentUser().collect { user ->
                if (user == null || courseId.isBlank()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Missing user or course"
                        )
                    }
                    enrollmentJob?.cancel()
                    currentStudentId = null
                    return@collect
                }

                currentStudentId = user.id

                // Observe course details from catalog
                viewModelScope.launch {
                    catalogUseCases.observeAllCourses().collect { courses ->
                        val course = courses.firstOrNull { it.id == courseId }
                        if (course != null) {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    title = course.title,
                                    category = course.category,
                                    level = course.level,
                                    shortDescription = course.shortDescription
                                )
                            }
                        }
                    }
                }

                // Observe enrollment status for this course
                enrollmentJob?.cancel()
                enrollmentJob = viewModelScope.launch {
                    detailUseCases.observeEnrollment(user.id, courseId)
                        .collect { enrollment ->
                            _uiState.update {
                                it.copy(
                                    isEnrolled = enrollment != null
                                )
                            }
                        }
                }
            }
        }
    }

    fun onEnrollClicked() {
        val studentId = currentStudentId ?: return
        if (courseId.isBlank()) return
        if (_uiState.value.isEnrolled) return

        _uiState.update { it.copy(isEnrolling = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                detailUseCases.enrollInCourse(studentId, courseId)
                _uiState.update { it.copy(isEnrolling = false) }
                // Dashboard will auto-update via its own observers
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isEnrolling = false,
                        errorMessage = e.message ?: "Failed to enroll"
                    )
                }
            }
        }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
