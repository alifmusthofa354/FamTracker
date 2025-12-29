package com.example.famtracker.domain.repository

import com.example.famtracker.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUser(userId: String): Result<User>
    fun getUserFlow(userId: String): Flow<User>
    suspend fun updateUser(user: User): Result<Unit>
}