package com.otic.learn.domain.usecase

import com.otic.learn.domain.model.User
import com.otic.learn.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<User?> = authRepository.currentUserFlow
}
