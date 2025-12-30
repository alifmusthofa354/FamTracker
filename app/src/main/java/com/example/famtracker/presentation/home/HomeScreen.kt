package com.example.famtracker.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.model.rememberScreenModel
import com.example.famtracker.presentation.map.OsmMapView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.osmdroid.util.GeoPoint

class HomeScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
    @Composable
    override fun Content() {

        val viewModel = rememberScreenModel { HomeViewModel() }
        val mapState by viewModel.mapState.collectAsState()
        val context = LocalContext.current

        // 1. Kelola status izin lokasi
        val locationPermissionState = rememberPermissionState(
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("FamTracker (OSM)") }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                OsmMapView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    mapState = mapState
                )

                // 2. Tampilkan pesan jika izin belum diberikan
                if (!locationPermissionState.status.isGranted) {
                    Text(
                        text = "Izin lokasi diperlukan untuk fitur 'My Location'.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // 3. Tombol "My Location"
                Button(
                    onClick = {
                        if (locationPermissionState.status.isGranted) {
                            // Jika izin sudah diberikan, dapatkan lokasi
                            viewModel.getCurrentLocationAndCenterMap(context)
                        } else {
                            // Jika belum, minta izin kepada pengguna
                            locationPermissionState.launchPermissionRequest()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("My Location")
                }
            }
        }
    }
}