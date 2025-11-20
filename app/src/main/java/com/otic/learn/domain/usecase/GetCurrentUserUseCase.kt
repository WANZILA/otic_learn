package com.otic.learn.domain.usecase

import com.otic.learn.domain.model.User
import com.otic.learn.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): User? = authRepository.getCurrentUser()
}
