package com.example.famtracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.famtracker.data.local.dao.UserDao
import com.example.famtracker.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}