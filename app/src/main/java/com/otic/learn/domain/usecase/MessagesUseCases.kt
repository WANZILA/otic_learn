package com.otic.learn.domain.usecase

import com.otic.learn.domain.model.Message
import com.otic.learn.domain.model.MessageThread
import com.otic.learn.domain.repository.MessagesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class MessagesUseCases @Inject constructor(
    val observeThreadsForStudent: ObserveThreadsForStudent,
    val observeMessagesForThread: ObserveMessagesForThread,
    val sendMessage: SendMessage,
    val createOrGetThread: CreateOrGetThread
)

class ObserveThreadsForStudent @Inject constructor(
    private val repo: MessagesRepository
) {
    operator fun invoke(studentId: String): Flow<List<MessageThread>> =
        repo.observeThreadsForStudent(studentId)
}

class ObserveMessagesForThread @Inject constructor(
    private val repo: MessagesRepository
) {
    operator fun invoke(threadId: String): Flow<List<Message>> =
        repo.observeMessagesForThread(threadId)
}

class SendMessage @Inject constructor(
    private val repo: MessagesRepository
) {
    suspend operator fun invoke(
        threadId: String,
        senderId: String,
        text: String
    ) {
        repo.sendMessage(threadId, senderId, text)
    }
}

class CreateOrGetThread @Inject constructor(
    private val repo: MessagesRepository
) {
    suspend operator fun invoke(
        studentId: String,
        instructorId: String
    ): MessageThread = repo.createOrGetThread(studentId, instructorId)
}
