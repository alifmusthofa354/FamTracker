package com.example.famtracker.presentation.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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

    /**
     * Mendapatkan lokasi terakhir pengguna dan memindahkan peta ke lokasi tersebut.
     * @param context Diperlukan untuk membuat FusedLocationProviderClient.
     */
    fun getCurrentLocationAndCenterMap(context: Context) {
        // 1. Periksa izin terlebih dahulu
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Jika izin belum diberikan, jangan lakukan apa-apa.
            // UI akan menangani permintaan izin.
            return
        }

        // 2. Buat instance FusedLocationProviderClient
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        // 3. Gunakan coroutine untuk mendapatkan lokasi secara asynchronous
        screenModelScope.launch {
            try {
                // `await()` adalah fungsi ekstensi dari kotlinx-coroutines-play-services
                // yang mengubah Task menjadi coroutine yang bisa di-await.
                val lastLocation = fusedLocationClient.lastLocation.await()

                if (lastLocation != null) {
                    // Jika lokasi ditemukan, perbarui state peta
                    val userLocation = GeoPoint(lastLocation.latitude, lastLocation.longitude)
                    updateMapCenter(userLocation)
                } else {
                    // Jika lokasi null, beri tahu pengguna
                    Toast.makeText(context, "Tidak dapat menemukan lokasi. Pastikan GPS aktif.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Tangani error lainnya
                Toast.makeText(context, "Gagal mendapatkan lokasi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}