package com.otic.learn.presentation.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.otic.learn.presentation.ai.AiAssistantScreen
import com.otic.learn.presentation.courses.CourseDetailScreen
import com.otic.learn.presentation.courses.CoursesScreen
import com.otic.learn.presentation.dashboard.DashboardScreen
import com.otic.learn.presentation.messages.ChatScreen
import com.otic.learn.presentation.messages.MessagesScreen
import com.otic.learn.presentation.notes.NotesScreen
import com.otic.learn.presentation.notifications.NotificationsScreen

@Composable
fun StudentHomeScreen(
    navController: NavHostController = rememberNavController()
) {
    Scaffold(
        topBar = {
            OticTopNavBar(
                navController = navController
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            StudentNavHost(navController = navController)
        }
    }
}

@Composable
private fun OticTopNavBar(
    navController: NavHostController
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Surface(
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Title row
            Text(
                text = "Otic Learn",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            // Tabs row (Dashboard / My Notes / Messages)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StudentDestination.all.forEach { dest ->
                    val selected = currentRoute == dest.route

                    Card(
                        onClick = {
                            if (currentRoute != dest.route) {
                                navController.navigate(dest.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        modifier = Modifier.height(32.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = if (selected) 4.dp else 0.dp
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dest.label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (selected) {
                                    MaterialTheme.colorScheme.onPrimary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                })


                        }
                    }
                }
            }
        }
    }
}
@Composable
private fun StudentNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = StudentDestination.Dashboard.route
    ) {
        composable(StudentDestination.Dashboard.route) {
            // DashboardViewModel is injected inside DashboardScreen via hiltViewModel()
            DashboardScreen()
        }
        composable(StudentDestination.Notes.route) {
            // NotesViewModel is injected inside NotesScreen via hiltViewModel()
            NotesScreen()
        }

        composable(StudentDestination.Messages.route) {
            MessagesScreen(
                onOpenThread = { threadId ->
                    navController.navigate("messages/$threadId")
                }
            )
        }
        composable("messages/{threadId}") {
            ChatScreen()
        }

        composable(StudentDestination.Notifications.route) {
            NotificationsScreen()
        }

        composable(StudentDestination.AiAssistant.route) { AiAssistantScreen() }

        composable(StudentDestination.Courses.route) {
            CoursesScreen(
                onOpenCourse = { courseId ->
                    navController.navigate("courses/$courseId")
                }
            )
        }
        composable("courses/{courseId}") {
            CourseDetailScreen()
        }

    }
}
