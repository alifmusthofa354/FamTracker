package com.example.famtracker.presentation.home

import android.util.Log
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.example.famtracker.domain.repository.LocationRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ScreenModel {

    private val _mapState = MutableStateFlow(MapState())
    val mapState = _mapState.asStateFlow()

    private var locationJob: Job? = null

    /**
     * Mulai update lokasi.
     * Permission harus sudah di-handle oleh UI sebelum memanggil fungsi ini.
     */
    fun startLocationUpdates() {
        // Cegah double subscription
        if (locationJob?.isActive == true) return

        locationJob = locationRepository.getLocationUpdates(5000L)
            .onEach { location ->
                val newPoint = GeoPoint(location.latitude, location.longitude)
                _mapState.update { 
                    it.copy(centerLocation = newPoint) 
                }
            }
            .catch { e ->
                Log.e("HomeViewModel", "Error getting location updates: ${e.message}")
            }
            .launchIn(screenModelScope)
    }

    fun stopLocationUpdates() {
        locationJob?.cancel()
        locationJob = null
    }

    // Dipanggil saat user mengklik tombol "My Location" / recenter
    fun enableFollowMode() {
        _mapState.update { state ->
            state.copy(isFollowMode = true)
        }
        // Jika belum tracking, mulai tracking
        if (locationJob?.isActive != true) {
            startLocationUpdates()
        }
    }

    // Dipanggil saat user menggeser peta secara manual
    fun disableFollowMode() {
        if (_mapState.value.isFollowMode) {
            _mapState.update { it.copy(isFollowMode = false) }
        }
    }
    
    // Voyager ScreenModel onDispose (mirip ViewModel onCleared)
    override fun onDispose() {
        stopLocationUpdates()
        super.onDispose()
    }
}