package com.example.famtracker.domain.repository

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getLocationUpdates(intervalMillis: Long): Flow<Location>
    suspend fun getCurrentLocation(): Location?
}