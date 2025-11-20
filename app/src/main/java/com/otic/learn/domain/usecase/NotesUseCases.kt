package com.otic.learn.domain.usecase

import com.otic.learn.domain.model.Note
import com.otic.learn.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class NotesUseCases @Inject constructor(
    val observeNotes: ObserveNotes,
    val getNoteById: GetNoteById,
    val upsertNote: UpsertNote,
    val deleteNote: DeleteNote
)

class ObserveNotes @Inject constructor(
    private val notesRepository: NotesRepository
) {
    operator fun invoke(studentId: String): Flow<List<Note>> {
        return notesRepository.observeNotes(studentId)
    }
}

class GetNoteById @Inject constructor(
    private val notesRepository: NotesRepository
) {
    suspend operator fun invoke(studentId: String, noteId: String): Note? {
        return notesRepository.getNoteById(studentId, noteId)
    }
}

class UpsertNote @Inject constructor(
    private val notesRepository: NotesRepository
) {
    suspend operator fun invoke(note: Note) {
        notesRepository.upsertNote(note)
    }
}

class DeleteNote @Inject constructor(
    private val notesRepository: NotesRepository
) {
    suspend operator fun invoke(studentId: String, noteId: String) {
        notesRepository.deleteNote(studentId, noteId)
    }
}
