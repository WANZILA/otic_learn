package com.otic.learn.domain.repository

import com.otic.learn.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationsRepository {

    fun observeNotifications(
        userId: String
    ): Flow<List<Notification>>

    suspend fun markAsRead(
        notificationId: String
    )
}
