package com.otic.learn.presentation.dashboard

data class DashboardUiState(
    val isLoading: Boolean = true,
    val studentName: String? = null,
    val courses: List<DashboardCourseItem> = emptyList(),
    val errorMessage: String? = null
)

data class DashboardCourseItem(
    val courseId: String,
    val title: String,
    val category: String?,
    val level: String?,
    val progressPercent: Int,
    val statusLabel: String // e.g. "In Progress", "Completed"
)
