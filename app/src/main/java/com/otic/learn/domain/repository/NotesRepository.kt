package com.otic.learn.domain.repository

import com.otic.learn.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {

    fun observeNotes(studentId: String): Flow<List<Note>>

    suspend fun getNoteById(
        studentId: String,
        noteId: String
    ): Note?

    suspend fun upsertNote(note: Note)

    suspend fun deleteNote(
        studentId: String,
        noteId: String
    )
}
