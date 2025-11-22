package com.otic.learn.domain.usecase

import com.otic.learn.domain.model.Notification
import com.otic.learn.domain.repository.NotificationsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class NotificationsUseCases @Inject constructor(
    val observeNotifications: ObserveNotifications,
    val markAsRead: MarkNotificationAsRead
)

class ObserveNotifications @Inject constructor(
    private val repo: NotificationsRepository
) {
    operator fun invoke(userId: String): Flow<List<Notification>> =
        repo.observeNotifications(userId)
}

class MarkNotificationAsRead @Inject constructor(
    private val repo: NotificationsRepository
) {
    suspend operator fun invoke(notificationId: String) {
        repo.markAsRead(notificationId)
    }
}
