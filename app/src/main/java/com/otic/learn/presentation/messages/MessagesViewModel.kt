package com.otic.learn.presentation.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otic.learn.domain.usecase.AuthUseCases
import com.otic.learn.domain.usecase.MessagesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val messagesUseCases: MessagesUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(MessagesUiState())
    val uiState: StateFlow<MessagesUiState> = _uiState.asStateFlow()

    private var threadsJob: Job? = null
    private var currentStudentId: String? = null

    init {
        observeUserAndThreads()
    }

    private fun observeUserAndThreads() {
        viewModelScope.launch {
            authUseCases.observeCurrentUser().collect { user ->
                if (user == null) {
                    _uiState.update {
                        MessagesUiState(
                            isLoading = false,
                            threads = emptyList(),
                            errorMessage = "Not logged in"
                        )
                    }
                    threadsJob?.cancel()
                    currentStudentId = null
                    return@collect
                }

                currentStudentId = user.id
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                threadsJob?.cancel()
                threadsJob = viewModelScope.launch {
                    messagesUseCases.observeThreadsForStudent(user.id).collect { threads ->
                        val items = threads
                            .sortedByDescending { it.updatedAtMillis }
                            .map { t ->
                                ThreadItem(
                                    threadId = t.id,
                                    instructorId = t.instructorId,
                                    instructorName = t.instructorId, // temp; later map from instructors collection
                                    lastMessagePreview = t.lastMessagePreview,
                                    unreadCount = t.unreadCountForStudent
                                )
                            }

                        _uiState.update {
                            it.copy(isLoading = false, threads = items)
                        }
                    }
                }
            }
        }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     * TEMP for testing:
     * Creates a fake instructor thread + sends a first message.
     */
    fun onCreateSampleThreadClicked() {
        val studentId = currentStudentId ?: return

        viewModelScope.launch {
            val fakeInstructorId = "instructor_${UUID.randomUUID().toString().take(6)}"
            val thread = messagesUseCases.createOrGetThread(studentId, fakeInstructorId)
            messagesUseCases.sendMessage(
                threadId = thread.id,
                senderId = studentId,
                text = "Hello instructor! (sample message)"
            )
        }
    }
}
