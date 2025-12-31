package com.example.famtracker.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.example.famtracker.domain.repository.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val client: FusedLocationProviderClient,
    @ApplicationContext private val context: Context
) : LocationRepository {

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(intervalMillis: Long): Flow<Location> {
        return callbackFlow {
            // Check permission logic could also be double-checked here or assumed handled by UI
            // For safety, we wrap in try-catch
            
            val request = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                intervalMillis
            ).build()

            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let { location ->
                        trySend(location)
                    }
                }
            }

            try {
                client.requestLocationUpdates(
                    request,
                    callback,
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                close(e) // Close flow if permission missing
            }

            awaitClose {
                client.removeLocationUpdates(callback)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location? {
        return try {
            client.lastLocation.await()
        } catch (e: Exception) {
            null
        }
    }
}