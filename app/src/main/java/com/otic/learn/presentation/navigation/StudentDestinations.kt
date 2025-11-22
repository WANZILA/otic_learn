package com.otic.learn.presentation.navigation

sealed class StudentDestination(val route: String, val label: String) {
    object Dashboard : StudentDestination("dashboard", "Dashboard")
    object Notes : StudentDestination("notes", "My Notes")

    object Messages : StudentDestination("messages", "Messages")
    object Notifications : StudentDestination("notifications", "Notifications")

    object AiAssistant : StudentDestination("ai", "AI Tutor")

    object Courses : StudentDestination("courses", "Courses")


    companion object {
        val all = listOf(Dashboard, Courses, Notes, Messages, Notifications, AiAssistant)
    }

}
