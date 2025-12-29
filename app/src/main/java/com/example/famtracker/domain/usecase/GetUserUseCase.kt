package com.example.famtracker.domain.usecase

import com.example.famtracker.domain.model.User
import com.example.famtracker.domain.repository.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<User> {
        return repository.getUser(userId)
    }
}