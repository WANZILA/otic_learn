package com.otic.learn.presentation.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.otic.learn.domain.repo.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class RootViewModel @Inject constructor(
    authRepo: AuthRepository
) : ViewModel() {
    val user = authRepo.observeUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), authRepo.currentUser)
}
