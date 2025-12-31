package com.example.famtracker.data.local.mapper

import com.example.famtracker.data.local.entity.UserEntity
import com.example.famtracker.domain.model.User

fun UserEntity.toDomain(): User {
    return User(
        id = id,
        name = name,
        email = email
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        email = email,
        createdAt = System.currentTimeMillis()
    )
}

