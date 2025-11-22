package com.otic.learn.presentation.courses

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CoursesScreen(
    viewModel: CoursesViewModel = hiltViewModel(),
    onOpenCourse: (courseId: String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Courses",
                style = MaterialTheme.typography.headlineMedium
            )

            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                label = { Text("Search courses") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            CategoryRow(
                categories = state.categories,
                selected = state.selectedCategory,
                onSelected = viewModel::onCategorySelected
            )

            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (!state.isLoading && state.courses.isEmpty()) {
                Text(
                    text = "No courses found.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.courses) { course ->
                        CourseCard(course, onOpenCourse)
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryRow(
    categories: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        categories.forEach { cat ->
            AssistChip(
                onClick = { onSelected(cat) },
                label = { Text(cat) },
                leadingIcon = if (cat == selected) ({ Text("•") }) else null
            )
        }
    }
}

@Composable
private fun CourseCard(
    course: CourseItem,
    onOpenCourse: (String) -> Unit
) {
    Card(
        onClick = { onOpenCourse(course.id) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = course.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            course.category?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            course.level?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            course.shortDescription?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
