package com.example.famtracker.presentation.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

class  HomeViewModel: ScreenModel {

    // StateFlow untuk menyimpan state peta. Ini reaktif dan aman untuk UI.
    private val _mapState = MutableStateFlow(MapState())
    val mapState: StateFlow<MapState> = _mapState

    // Fungsi untuk memperbarui lokasi tengah peta
    // Nantinya bisa dipanggil dari Use Case (misalnya dari GPS atau data server)
    fun updateMapCenter(newLocation: GeoPoint) {
        screenModelScope.launch {
            _mapState.value = _mapState.value.copy(centerLocation = newLocation)
        }
    }

    // Fungsi untuk memperbarui zoom level
    fun updateZoomLevel(newZoomLevel: Double) {
        screenModelScope.launch {
            _mapState.value = _mapState.value.copy(zoomLevel = newZoomLevel)
        }
    }
}