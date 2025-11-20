package com.otic.learn.presentation.notes

data class NotesUiState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val notes: List<NoteItem> = emptyList(), // filtered notes
    val errorMessage: String? = null
)

data class NoteItem(
    val id: String,
    val title: String,
    val contentPreview: String,
    val category: String?,
    val priorityLabel: String,
    val isPinned: Boolean
)
