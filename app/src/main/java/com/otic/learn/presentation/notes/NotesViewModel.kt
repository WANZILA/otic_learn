package com.otic.learn.presentation.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otic.learn.domain.model.Note
import com.otic.learn.domain.model.NotePriority
import com.otic.learn.domain.usecase.AuthUseCases
import com.otic.learn.domain.usecase.NotesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val notesUseCases: NotesUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    private var notesJob: Job? = null
    private var currentStudentId: String? = null
    private var rawNotes: List<Note> = emptyList()

    init {
        observeUserAndNotes()
    }

    private fun observeUserAndNotes() {
        viewModelScope.launch {
            authUseCases.observeCurrentUser().collect { user ->
                if (user == null) {
                    _uiState.update {
                        NotesUiState(
                            isLoading = false,
                            notes = emptyList(),
                            errorMessage = "Not logged in"
                        )
                    }
                    notesJob?.cancel()
                    currentStudentId = null
                    return@collect
                }

                currentStudentId = user.id
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                notesJob?.cancel()
                notesJob = viewModelScope.launch {
                    notesUseCases.observeNotes(user.id).collect { notes ->
                        rawNotes = notes
                        applyFilter()
                    }
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilter()
    }

    private fun applyFilter() {
        val query = _uiState.value.searchQuery.trim().lowercase()
        val filtered = rawNotes
            .filter { note ->
                if (query.isEmpty()) return@filter true
                note.title.lowercase().contains(query) ||
                        note.content.lowercase().contains(query) ||
                        (note.category ?: "").lowercase().contains(query)
            }
            .sortedWith(
                compareByDescending<Note> { it.isPinned }
                    .thenByDescending { it.updatedAtMillis }
            )
            .map { it.toItem() }

        _uiState.update { it.copy(isLoading = false, notes = filtered) }
    }

    private fun Note.toItem(): NoteItem {
        val preview = if (content.length > 80) {
            content.take(80) + "…"
        } else {
            content
        }

        val priorityLabel = when (priority) {
            NotePriority.LOW -> "Low"
            NotePriority.MEDIUM -> "Medium"
            NotePriority.HIGH -> "High"
        }

        return NoteItem(
            id = id,
            title = title,
            contentPreview = preview,
            category = category,
            priorityLabel = priorityLabel,
            isPinned = isPinned
        )
    }

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * Temporary helper: create a sample note so we can test end-to-end.
     * Later we will replace this with a proper Add/Edit Note screen.
     */
    fun onAddSampleNoteClicked() {
        val studentId = currentStudentId ?: return

        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val newNote = Note(
                id = UUID.randomUUID().toString(),
                studentId = studentId,
                title = "Sample note ${rawNotes.size + 1}",
                content = "This is a sample note created for testing the My Notes screen.",
                category = "General",
                priority = NotePriority.MEDIUM,
                isPinned = false,
                createdAtMillis = now,
                updatedAtMillis = now
            )

            notesUseCases.upsertNote(newNote)
        }
    }
}
