package com.example.famtracker.presentation.home

import android.annotation.SuppressLint
import android.content.Context
import cafe.adriel.voyager.core.model.ScreenModel
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.osmdroid.util.GeoPoint

class HomeViewModel : ScreenModel {

    private val _mapState = MutableStateFlow(MapState())
    val mapState = _mapState.asStateFlow()

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null

    /**
     * Mulai update lokasi.
     * Penting: PASTIKAN sudah cek permission di UI sebelum panggil ini.
     */
    @SuppressLint("MissingPermission")
    fun startLocationUpdates(context: Context) {
        // Kalau sudah pernah start, jangan ulangi lagi
        if (fusedLocationClient != null) return

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L
        )
            .setMinUpdateIntervalMillis(3000L)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val lastLocation = locationResult.lastLocation ?: return
                val newPoint = GeoPoint(lastLocation.latitude, lastLocation.longitude)

                _mapState.update { it.copy(centerLocation = newPoint) }
            }
        }

        try {
            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                context.mainLooper
            )
        } catch (e: SecurityException) {
            // Bersihin state supaya bisa di-restart dengan benar
            fusedLocationClient = null
            locationCallback = null
            // (Optional) kirim event ke UI kalau suatu saat kamu pakai event lagi
        }
    }

    // Dipanggil saat user mengklik tombol "My Location" / recenter
    fun enableFollowMode() {
        _mapState.update { state ->
            // bisa saja kamu tambahin: zoomLevel default, dll
            state.copy(isFollowMode = true)
        }
    }


    // Dipanggil saat user menggeser peta secara manual
    fun disableFollowMode() {
        if (_mapState.value.isFollowMode) {
            _mapState.update { it.copy(isFollowMode = false) }
        }
    }

    override fun onDispose() {
        locationCallback?.let { callback ->
            fusedLocationClient?.removeLocationUpdates(callback)
        }
        fusedLocationClient = null
        locationCallback = null
        super.onDispose()
    }
}