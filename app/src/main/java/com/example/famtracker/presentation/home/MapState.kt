package com.example.famtracker.presentation.home

import org.osmdroid.util.GeoPoint

data class MapState(
    // Kita beri nilai default null, karena akan segera ditimpa oleh lokasi GPS
    val centerLocation: GeoPoint? = null,
    val zoomLevel: Int = 15
)