package com.otic.learn.domain.usecase.auth

import com.otic.learn.domain.repo.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String) =
        repo.login(email, password)
}
