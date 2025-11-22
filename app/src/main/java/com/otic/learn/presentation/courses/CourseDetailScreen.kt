package com.otic.learn.presentation.courses

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CourseDetailScreen(
    viewModel: CourseDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onErrorShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (!state.isLoading) {
                CourseDetailBottomBar(
                    isEnrolled = state.isEnrolled,
                    isEnrolling = state.isEnrolling,
                    onEnrollClick = viewModel::onEnrollClicked
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = padding.calculateTopPadding() + 8.dp,
                    bottom = padding.calculateBottomPadding() + 72.dp
                )
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = state.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        state.category?.let {
                            AssistChip(
                                onClick = { },
                                label = { Text(it) }
                            )
                        }
                        state.level?.let {
                            AssistChip(
                                onClick = { },
                                label = { Text(it) }
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = state.shortDescription ?: "No description available yet.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // Later: add syllabus, instructor info, what you'll learn, etc.
                }
            }
        }
    }
}

@Composable
private fun CourseDetailBottomBar(
    isEnrolled: Boolean,
    isEnrolling: Boolean,
    onEnrollClick: () -> Unit
) {
    Surface(tonalElevation = 4.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isEnrolled) {
                Text(
                    text = "You’re enrolled in this course.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                // Later: add "Continue learning" button
            } else {
                Text(
                    text = "Ready to start this course?",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = onEnrollClick,
                    enabled = !isEnrolling
                ) {
                    if (isEnrolling) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Enrolling…")
                    } else {
                        Text("Enroll")
                    }
                }
            }
        }
    }
}
