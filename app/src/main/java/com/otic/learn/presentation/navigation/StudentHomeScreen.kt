package com.otic.learn.presentation.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.otic.learn.presentation.dashboard.DashboardScreen
import com.otic.learn.presentation.notes.NotesScreen

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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Otic Learn",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.weight(1f))

            StudentDestination.all.forEach { dest ->
                val selected = currentRoute == dest.route
                val color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                Text(
                    text = dest.label,
                    color = color,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .clickable {
                            if (currentRoute != dest.route) {
                                navController.navigate(dest.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                )
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
    }
}
