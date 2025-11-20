package com.otic.learn.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.otic.learn.domain.model.Note
import com.otic.learn.domain.model.NotePriority
import com.otic.learn.domain.repository.NotesRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Singleton
class NotesRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : NotesRepository {

    private val notesCollection = "notes"

    override fun observeNotes(studentId: String): Flow<List<Note>> = callbackFlow {
        val query = firestore.collection(notesCollection)
            .whereEqualTo("studentId", studentId)
            .orderBy("createdAtMillis")

        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val docs = snapshot?.documents ?: emptyList()

            launch {
                val notes = docs.mapNotNull { it.toNote() }
                trySend(notes).isSuccess
            }
        }

        awaitClose { registration.remove() }
    }

    override suspend fun getNoteById(studentId: String, noteId: String): Note? {
        val doc = firestore.collection(notesCollection)
            .document(noteId)
            .get()
            .await()

        val note = doc.toNote()
        // extra safety: ensure it belongs to this student
        return if (note?.studentId == studentId) note else null
    }

    override suspend fun upsertNote(note: Note) {
        val data = mapOf(
            "studentId" to note.studentId,
            "title" to note.title,
            "content" to note.content,
            "category" to note.category,
            "priority" to note.priority.name,
            "isPinned" to note.isPinned,
            "createdAtMillis" to note.createdAtMillis,
            "updatedAtMillis" to note.updatedAtMillis
        )

        firestore.collection(notesCollection)
            .document(note.id)
            .set(data)
            .await()
    }

    override suspend fun deleteNote(studentId: String, noteId: String) {
        val docRef = firestore.collection(notesCollection).document(noteId)
        val snapshot = docRef.get().await()
        val ownerId = snapshot.getString("studentId")
        if (ownerId == studentId) {
            docRef.delete().await()
        }
    }
}

// ---- mapping helper ----

private fun DocumentSnapshot.toNote(): Note? {
    val studentId = getString("studentId") ?: return null
    val title = getString("title") ?: return null
    val content = getString("content") ?: ""

    val category = getString("category")
    val priorityString = getString("priority") ?: "MEDIUM"
    val priority = runCatching { NotePriority.valueOf(priorityString) }
        .getOrElse { NotePriority.MEDIUM }

    val isPinned = getBoolean("isPinned") ?: false
    val createdAtMillis = getLong("createdAtMillis") ?: 0L
    val updatedAtMillis = getLong("updatedAtMillis") ?: createdAtMillis

    return Note(
        id = id,
        studentId = studentId,
        title = title,
        content = content,
        category = category,
        priority = priority,
        isPinned = isPinned,
        createdAtMillis = createdAtMillis,
        updatedAtMillis = updatedAtMillis
    )
}
