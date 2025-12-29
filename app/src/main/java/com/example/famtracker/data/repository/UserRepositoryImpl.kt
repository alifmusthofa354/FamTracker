package com.example.famtracker.data.repository

import com.example.famtracker.data.local.dao.UserDao
import com.example.famtracker.data.local.mapper.toDomain
import com.example.famtracker.data.local.mapper.toEntity
import com.example.famtracker.domain.model.User
import com.example.famtracker.domain.repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun getUser(userId: String): Result<User> {
        return try {
            // Try to get from local database first
            val localUser = userDao.getUserById(userId)

            if (localUser != null) {
                Result.success(localUser.toDomain())
            } else {
                // Simulate API call
                delay(1000)
                val user = User(
                    id = userId,
                    name = "John Doe",
                    email = "john.doe@example.com"
                )
                // Save to database
                userDao.insertUser(user.toEntity())
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getUserFlow(userId: String): Flow<User> {
        return userDao.getUserByIdFlow(userId).map { entity ->
            entity?.toDomain() ?: User(
                id = userId,
                name = "Unknown",
                email = "unknown@example.com"
            )
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        return try {
            delay(500)
            userDao.updateUser(user.toEntity())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}