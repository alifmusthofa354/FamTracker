package com.example.famtracker.presentation.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.google.android.gms.location.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

class HomeViewModel : ScreenModel {

    // StateFlow untuk menyimpan state peta
    private val _mapState = MutableStateFlow(MapState())
    val mapState: StateFlow<MapState> = _mapState

    // Client untuk mengakses layanan lokasi
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    // Callback untuk menerima pembaruan lokasi
    private lateinit var locationCallback: LocationCallback

    /**
     * Memulai pembaruan lokasi secara real-time.
     * Fungsi ini sebaiknya dipanggil sekali saat screen pertama kali dibuka.
     */
    fun startLocationUpdates(context: Context) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        // Periksa izin terlebih dahulu
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Jika izin belum diberikan, jangan lakukan apa-apa.
            // UI akan menangani permintaan izin.
            return
        }

        // Definisikan permintaan lokasi (akurasi, interval, dll.)
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000) // Update setiap 10 detik
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(5000) // Interval minimum antar update
            .setMaxUpdateDelayMillis(15000) // Maksimum delay
            .build()

        // Definisikan callback yang akan dipanggil setiap kali lokasi berubah
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    // Setiap kali lokasi baru diterima, perbarui state peta
                    val newLocation = GeoPoint(it.latitude, it.longitude)
                    _mapState.value = _mapState.value.copy(centerLocation = newLocation)
                }
            }
        }

        try {
            // Mulai meminta pembaruan lokasi
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null // Looper
            )
        } catch (e: SecurityException) {
            // Seharusnya tidak terjadi karena kita sudah cek izin
            Toast.makeText(context, "Error: Izin lokasi ditolak.", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * SANGAT PENTING: Hentikan pembaruan lokasi saat ViewModel dihancurkan
     * untuk mencegah pemborosan baterai.
     */
    override fun onDispose() {
        super.onDispose()
        if (::fusedLocationClient.isInitialized && ::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}