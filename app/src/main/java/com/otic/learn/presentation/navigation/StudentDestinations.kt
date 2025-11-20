package com.otic.learn.presentation.navigation

sealed class StudentDestination(val route: String, val label: String) {
    object Dashboard : StudentDestination("dashboard", "Dashboard")
    object Notes : StudentDestination("notes", "My Notes")

    companion object {
        val all = listOf(Dashboard, Notes)
    }
}
