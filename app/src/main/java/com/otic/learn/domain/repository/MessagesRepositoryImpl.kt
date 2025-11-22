package com.otic.learn.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.otic.learn.domain.model.Message
import com.otic.learn.domain.model.MessageThread
import com.otic.learn.domain.repository.MessagesRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

@Singleton
class MessagesRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : MessagesRepository {

    private val threadsCollection = "messageThreads"
    private val messagesCollection = "messages"

    override fun observeThreadsForStudent(
        studentId: String
    ): Flow<List<MessageThread>> = callbackFlow {
        val query = firestore.collection(threadsCollection)
            .whereEqualTo("studentId", studentId)
            .orderBy("updatedAtMillis")

        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val docs = snapshot?.documents ?: emptyList()

            launch {
                val threads = docs.mapNotNull { it.toMessageThread() }
                trySend(threads).isSuccess
            }
        }

        awaitClose { registration.remove() }
    }

    override fun observeMessagesForThread(
        threadId: String
    ): Flow<List<Message>> = callbackFlow {
        val query = firestore.collection(messagesCollection)
            .whereEqualTo("threadId", threadId)
            .orderBy("createdAtMillis")

        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val docs = snapshot?.documents ?: emptyList()

            launch {
                val messages = docs.mapNotNull { it.toMessage() }
                trySend(messages).isSuccess
            }
        }

        awaitClose { registration.remove() }
    }

    override suspend fun sendMessage(
        threadId: String,
        senderId: String,
        text: String
    ) {
        val now = System.currentTimeMillis()

        // Create message
        val messageId = UUID.randomUUID().toString()
        val messageData = mapOf(
            "threadId" to threadId,
            "senderId" to senderId,
            "text" to text,
            "createdAtMillis" to now
        )

        val batch = firestore.batch()

        val messageRef = firestore.collection(messagesCollection).document(messageId)
        batch.set(messageRef, messageData)

        // Update thread metadata
        val threadRef = firestore.collection(threadsCollection).document(threadId)
        batch.update(
            threadRef,
            mapOf(
                "lastMessagePreview" to text.take(80),
                "updatedAtMillis" to now,
                // For now we just increment unread count for student;
                // later we can adjust depending on senderId.
                "unreadCountForStudent" to com.google.firebase.firestore.FieldValue.increment(1)
            )
        )

        batch.commit().await()
    }

    override suspend fun createOrGetThread(
        studentId: String,
        instructorId: String
    ): MessageThread {
        val query = firestore.collection(threadsCollection)
            .whereEqualTo("studentId", studentId)
            .whereEqualTo("instructorId", instructorId)
            .limit(1)
            .get()
            .await()

        val existing = query.documents.firstOrNull()?.toMessageThread()
        if (existing != null) return existing

        // Create new thread
        val now = System.currentTimeMillis()
        val threadId = UUID.randomUUID().toString()
        val data = mapOf(
            "studentId" to studentId,
            "instructorId" to instructorId,
            "lastMessagePreview" to "",
            "unreadCountForStudent" to 0L,
            "updatedAtMillis" to now
        )

        firestore.collection(threadsCollection)
            .document(threadId)
            .set(data)
            .await()

        return MessageThread(
            id = threadId,
            studentId = studentId,
            instructorId = instructorId,
            lastMessagePreview = "",
            unreadCountForStudent = 0,
            updatedAtMillis = now
        )
    }
}

// ---- mapping helpers ----

private fun DocumentSnapshot.toMessageThread(): MessageThread? {
    val studentId = getString("studentId") ?: return null
    val instructorId = getString("instructorId") ?: return null

    val lastMessagePreview = getString("lastMessagePreview")
    val unreadCountForStudent = (getLong("unreadCountForStudent") ?: 0L).toInt()
    val updatedAtMillis = getLong("updatedAtMillis") ?: 0L

    return MessageThread(
        id = id,
        studentId = studentId,
        instructorId = instructorId,
        lastMessagePreview = lastMessagePreview,
        unreadCountForStudent = unreadCountForStudent,
        updatedAtMillis = updatedAtMillis
    )
}

private fun DocumentSnapshot.toMessage(): Message? {
    val threadId = getString("threadId") ?: return null
    val senderId = getString("senderId") ?: return null
    val text = getString("text") ?: ""

    val createdAtMillis = getLong("createdAtMillis") ?: 0L

    return Message(
        id = id,
        threadId = threadId,
        senderId = senderId,
        text = text,
        createdAtMillis = createdAtMillis
    )
}
