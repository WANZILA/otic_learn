package com.otic.learn.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otic.learn.domain.model.EnrollmentStatus
import com.otic.learn.domain.usecase.AuthUseCases
import com.otic.learn.domain.usecase.DashboardUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val dashboardUseCases: DashboardUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var coursesJob: Job? = null

    init {
        observeCurrentUserAndCourses()
    }

    private fun observeCurrentUserAndCourses() {
        viewModelScope.launch {
            authUseCases.observeCurrentUser().collect { user ->
                if (user == null) {
                    // No logged-in user; clear state
                    _uiState.update {
                        DashboardUiState(
                            isLoading = false,
                            studentName = null,
                            courses = emptyList(),
                            errorMessage = "Not logged in"
                        )
                    }
                    coursesJob?.cancel()
                    return@collect
                }

                // Update name
                _uiState.update { state ->
                    state.copy(
                        studentName = user.displayName ?: user.email,
                        isLoading = true,
                        errorMessage = null
                    )
                }

                // Observe courses for this student
                coursesJob?.cancel()
                coursesJob = viewModelScope.launch {
                    dashboardUseCases.observeStudentCourses(user.id)
                        .collect { pairs ->
                            val items = pairs.map { (course, enrollment) ->
                                DashboardCourseItem(
                                    courseId = course.id,
                                    title = course.title,
                                    category = course.category,
                                    level = course.level,
                                    progressPercent = enrollment.percentComplete,
                                    statusLabel = when (enrollment.status) {
                                        EnrollmentStatus.ACTIVE -> "In Progress"
                                        EnrollmentStatus.COMPLETED -> "Completed"
                                    }
                                )
                            }

                            _uiState.update { state ->
                                state.copy(
                                    isLoading = false,
                                    courses = items,
                                    errorMessage = null
                                )
                            }
                        }
                }
            }
        }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
