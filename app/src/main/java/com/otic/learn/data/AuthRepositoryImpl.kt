package com.otic.learn.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.otic.learn.domain.model.User
import com.otic.learn.domain.model.UserRole
import com.otic.learn.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val currentUserFlow: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toDomainUser())
        }

        // emit initial value
        trySend(firebaseAuth.currentUser?.toDomainUser())

        firebaseAuth.addAuthStateListener(listener)

        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

    override suspend fun getCurrentUser(): User? {
        return firebaseAuth.currentUser?.toDomainUser()
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String
    ): User {
        val result = firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .await()

        val firebaseUser = result.user
            ?: throw IllegalStateException("User is null after sign-in")

        return firebaseUser.toDomainUser()
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }
}

private fun FirebaseUser.toDomainUser(): User {
    return User(
        id = uid,
        email = email.orEmpty(),
        displayName = displayName,
        photoUrl = photoUrl?.toString(),
        // For now we assume every login in this app is a STUDENT.
        // Later we can read a custom claim or Firestore document to set role.
        role = UserRole.STUDENT
    )
}
