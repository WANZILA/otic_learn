package com.otic.learn.domain.repository

import com.otic.learn.domain.model.Message
import com.otic.learn.domain.model.MessageThread
import kotlinx.coroutines.flow.Flow

interface MessagesRepository {

    fun observeThreadsForStudent(
        studentId: String
    ): Flow<List<MessageThread>>

    fun observeMessagesForThread(
        threadId: String
    ): Flow<List<Message>>

    suspend fun sendMessage(
        threadId: String,
        senderId: String,
        text: String
    )

    suspend fun createOrGetThread(
        studentId: String,
        instructorId: String
    ): MessageThread
}
