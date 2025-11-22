package com.otic.learn.data.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.otic.learn.domain.model.Notification
import com.otic.learn.domain.model.NotificationType
import com.otic.learn.domain.repository.NotificationsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Singleton
class NotificationsRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : NotificationsRepository {

    private val collection = "notifications"

    override fun observeNotifications(userId: String): Flow<List<Notification>> = callbackFlow {
        val query = firestore.collection(collection)
            .whereEqualTo("userId", userId)
            .orderBy("createdAtMillis")

        val registration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            val docs = snapshot?.documents ?: emptyList()

            launch {
                val notifications = docs.mapNotNull { it.toNotification() }
                trySend(notifications).isSuccess
            }
        }

        awaitClose { registration.remove() }
    }

    override suspend fun markAsRead(notificationId: String) {
        firestore.collection(collection)
            .document(notificationId)
            .update("isRead", true)
            .await()
    }
}

// ---- mapping helper ----

private fun DocumentSnapshot.toNotification(): Notification? {
    val userId = getString("userId") ?: return null
    val title = getString("title") ?: return null
    val body = getString("body") ?: ""

    val typeString = getString("type") ?: "SYSTEM"
    val type = runCatching { NotificationType.valueOf(typeString) }
        .getOrElse { NotificationType.SYSTEM }

    val isRead = getBoolean("isRead") ?: false
    val createdAtMillis = getLong("createdAtMillis") ?: 0L
    val targetType = getString("targetType")
    val targetId = getString("targetId")

    return Notification(
        id = id,
        userId = userId,
        title = title,
        body = body,
        type = type,
        isRead = isRead,
        createdAtMillis = createdAtMillis,
        targetType = targetType,
        targetId = targetId
    )
}
