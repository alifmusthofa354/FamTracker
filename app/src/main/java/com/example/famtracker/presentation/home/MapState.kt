package com.example.famtracker.presentation.home

import org.osmdroid.util.GeoPoint

data class MapState(
    val centerLocation: GeoPoint? = null,
    val zoomLevel: Double = 15.0,
    val isFollowMode: Boolean = true // Jika true, peta otomatis mengikuti pergerakan GPS
)