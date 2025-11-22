package com.otic.learn.presentation.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otic.learn.domain.model.Course
import com.otic.learn.domain.usecase.CourseCatalogUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CoursesViewModel @Inject constructor(
    private val catalogUseCases: CourseCatalogUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoursesUiState())
    val uiState: StateFlow<CoursesUiState> = _uiState.asStateFlow()

    private var rawCourses: List<Course> = emptyList()

    init {
        observeCourses()
    }

    private fun observeCourses() {
        viewModelScope.launch {
            catalogUseCases.observeAllCourses().collect { list ->
                rawCourses = list
                val cats = list.mapNotNull { it.category }
                    .distinct()
                    .sorted()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        categories = listOf("All") + cats
                    )
                }

                applyFilter()
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilter()
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        applyFilter()
    }

    private fun applyFilter() {
        val state = _uiState.value
        val q = state.searchQuery.trim().lowercase()
        val cat = state.selectedCategory

        val filtered = rawCourses
            .filter { course ->
                val categoryMatches =
                    (cat == "All") || (course.category == cat)

                if (!categoryMatches) return@filter false
                if (q.isEmpty()) return@filter true

                val haystack = (course.title + " " + (course.shortDescription ?: ""))
                    .lowercase()

                haystack.contains(q)
            }
            .sortedBy { it.title }
            .map { it.toItem() }

        _uiState.update { it.copy(courses = filtered) }
    }

    private fun Course.toItem(): CourseItem =
        CourseItem(
            id = id,
            title = title,
            category = category,
            level = level,
            shortDescription = shortDescription
        )
}
