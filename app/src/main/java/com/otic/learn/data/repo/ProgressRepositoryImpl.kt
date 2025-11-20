package com.otic.learn.data.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.otic.learn.data.model.ProgressDto
import com.otic.learn.domain.model.Progress
import com.otic.learn.domain.repo.ProgressRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProgressRepositoryImpl(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ProgressRepository {

    private fun uid() = auth.currentUser?.uid ?: throw IllegalStateException("No user")

    override fun observeProgress(): Flow<Map<String, Progress>> = callbackFlow {
        val sub = db.collection("users").document(uid())
            .collection("progress")
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener
                val map = snap.documents.associate { doc ->
                    val dto = doc.toObject<ProgressDto>() ?: ProgressDto()
                    doc.id to Progress(
                        courseId = doc.id,
                        completedLessonIds = dto.completedLessonIds,
                        lastLessonId = dto.lastLessonId
                    )
                }
                trySend(map)
            }
        awaitClose { sub.remove() }
    }

    override suspend fun completeLesson(courseId: String, lessonId: String) {
        val ref = db.collection("users").document(uid())
            .collection("progress").document(courseId)

        db.runTransaction { tx ->
            val current = tx.get(ref).toObject<ProgressDto>() ?: ProgressDto()
            val updated = current.completedLessonIds.toMutableSet().apply { add(lessonId) }.toList()
            tx.set(ref, ProgressDto(completedLessonIds = updated, lastLessonId = lessonId))
        }.await()
    }
}
