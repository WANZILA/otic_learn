package com.otic.learn.presentation.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otic.learn.domain.model.Notification
import com.otic.learn.domain.model.NotificationType
import com.otic.learn.domain.usecase.AuthUseCases
import com.otic.learn.domain.usecase.NotificationsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val notificationsUseCases: NotificationsUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    private var currentUserId: String? = null
    private var notificationsJob: Job? = null
    private var rawNotifications: List<Notification> = emptyList()

    init {
        observeUserAndNotifications()
    }

    private fun observeUserAndNotifications() {
        viewModelScope.launch {
            authUseCases.observeCurrentUser().collect { user ->
                if (user == null) {
                    _uiState.update {
                        NotificationsUiState(
                            isLoading = false,
                            notifications = emptyList(),
                            errorMessage = "Not logged in"
                        )
                    }
                    notificationsJob?.cancel()
                    currentUserId = null
                    return@collect
                }

                currentUserId = user.id
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                notificationsJob?.cancel()
                notificationsJob = viewModelScope.launch {
                    notificationsUseCases.observeNotifications(user.id)
                        .collect { list ->
                            rawNotifications = list
                            applyFilterAndSearch()
                        }
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilterAndSearch()
    }

    fun onFilterSelected(filter: NotificationFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
        applyFilterAndSearch()
    }

    private fun applyFilterAndSearch() {
        val state = _uiState.value
        val query = state.searchQuery.trim().lowercase()
        val filter = state.selectedFilter

        val filtered = rawNotifications
            .filter { notification ->
                val typeMatches = when (filter) {
                    NotificationFilter.ALL -> true
                    NotificationFilter.MESSAGE -> notification.type == NotificationType.MESSAGE
                    NotificationFilter.COURSE -> notification.type == NotificationType.COURSE
                    NotificationFilter.FORUM -> notification.type == NotificationType.FORUM
                    NotificationFilter.SYSTEM -> notification.type == NotificationType.SYSTEM
                }

                if (!typeMatches) return@filter false
                if (query.isEmpty()) return@filter true

                val haystack = (notification.title + " " + notification.body).lowercase()
                haystack.contains(query)
            }
            .sortedWith(
                compareBy<Notification> { it.isRead }       // unread first
                    .thenByDescending { it.createdAtMillis } // newest first within that
            )
            .map { it.toItem() }

        _uiState.update {
            it.copy(
                isLoading = false,
                notifications = filtered
            )
        }
    }

    private fun Notification.toItem(): NotificationItem {
        val preview = if (body.length > 80) body.take(80) + "…" else body
        val typeLabel = when (type) {
            NotificationType.MESSAGE -> "Message"
            NotificationType.FORUM -> "Forum"
            NotificationType.COURSE -> "Course"
            NotificationType.SYSTEM -> "System"
        }

        return NotificationItem(
            id = id,
            title = title,
            bodyPreview = preview,
            typeLabel = typeLabel,
            isRead = isRead
        )
    }

    fun onNotificationClicked(id: String) {
        viewModelScope.launch {
            try {
                notificationsUseCases.markAsRead(id)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.message ?: "Failed to update notification")
                }
            }
        }
    }

    fun onErrorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
