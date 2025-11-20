package com.otic.learn.domain.usecase

import com.otic.learn.domain.model.User
import com.otic.learn.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class AuthUseCases @Inject constructor(
    val observeCurrentUser: ObserveCurrentUser,
    val getCurrentUser: GetCurrentUser,
    val signInWithEmail: SignInWithEmail,
    val signOut: SignOut
)

class ObserveCurrentUser @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<User?> = authRepository.currentUserFlow
}

class GetCurrentUser @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): User? = authRepository.getCurrentUser()
}

class SignInWithEmail @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): User =
        authRepository.signInWithEmailAndPassword(email, password)
}

class SignOut @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() {
        authRepository.signOut()
    }
}
