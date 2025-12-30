package com.example.famtracker.presentation.home

import org.osmdroid.util.GeoPoint

// Data class yang mendeskripsikan semua state yang relevan untuk peta
data class MapState(
    val centerLocation: GeoPoint = GeoPoint(-6.200000, 106.816666),
    val zoomLevel: Double = 15.0 // Gunakan Double
)