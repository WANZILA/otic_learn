package com.otic.learn.presentation.courses

data class CourseDetailUiState(
    val isLoading: Boolean = true,
    val title: String = "",
    val category: String? = null,
    val level: String? = null,
    val shortDescription: String? = null,
    val isEnrolled: Boolean = false,
    val isEnrolling: Boolean = false,
    val errorMessage: String? = null
)
