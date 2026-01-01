package com.example.famtracker.data.repository

import com.example.famtracker.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override suspend fun login(email: String, pass: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, pass).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(email: String, pass: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, pass).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun logout() {
        auth.signOut()
    }
}