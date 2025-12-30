package com.example.famtracker.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.model.rememberScreenModel
import com.example.famtracker.presentation.map.OsmMapView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

class HomeScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
    @Composable
    override fun Content() {

        val viewModel = rememberScreenModel { HomeViewModel() }
        val mapState by viewModel.mapState.collectAsState()
        val context = LocalContext.current

        // Kelola status izin lokasi
        val locationPermissionState = rememberPermissionState(
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )

        // LaunchedEffect untuk memulai lokasi otomatis saat izin diberikan
        LaunchedEffect(locationPermissionState.status) {
            if (locationPermissionState.status.isGranted) {
                // Jika izin diberikan, mulai pembaruan lokasi
                viewModel.startLocationUpdates(context)
            }
        }

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
                // Tampilkan peta
                OsmMapView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    mapState = mapState
                )

                // Tampilkan pesan jika izin belum diberikan
                if (!locationPermissionState.status.isGranted) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (locationPermissionState.status.shouldShowRationale) {
                            Text(
                                text = "Izin lokasi diperlukan agar aplikasi dapat melacak posisi Anda secara real-time. Mohon berikan izin.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            Text(
                                text = "Aplikasi memerlukan izin lokasi untuk berfungsi.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                            Text("Berikan Izin Lokasi")
                        }
                    }
                }
            }
        }
    }
}