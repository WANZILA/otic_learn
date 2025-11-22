package com.otic.learn.presentation.courses

data class CoursesUiState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val categories: List<String> = listOf("All"),
    val courses: List<CourseItem> = emptyList(),
    val errorMessage: String? = null
)

data class CourseItem(
    val id: String,
    val title: String,
    val category: String?,
    val level: String?,
    val shortDescription: String?
)
