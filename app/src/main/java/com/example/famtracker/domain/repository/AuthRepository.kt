package com.example.famtracker.domain.repository

import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?
    
    suspend fun login(email: String, pass: String): Result<FirebaseUser>
    suspend fun register(email: String, pass: String): Result<FirebaseUser>
    fun logout()
}