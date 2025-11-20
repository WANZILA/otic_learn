package com.otic.learn.domain.repository

import com.otic.learn.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val currentUserFlow: Flow<User?>

    suspend fun getCurrentUser(): User?

    suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): User

    suspend fun signOut()
}
