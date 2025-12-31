package com.example.famtracker.presentation.home

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.example.famtracker.presentation.map.OsmMapView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

import cafe.adriel.voyager.hilt.getScreenModel

class HomeScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
    @Composable
    override fun Content() {
        // Gunakan getScreenModel() agar Hilt meng-inject constructor HomeViewModel
        val viewModel = getScreenModel<HomeViewModel>()
        val mapState by viewModel.mapState.collectAsState()
        val context = LocalContext.current

        val locationPermissionState = rememberPermissionState(
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        // Mulai update lokasi jika izin diberikan
        LaunchedEffect(locationPermissionState.status.isGranted) {
            if (locationPermissionState.status.isGranted) {
                // Tidak perlu kirim context lagi -> Aman dari Memory Leak!
                viewModel.startLocationUpdates()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(title = { Text("FamTracker") })
            },
            floatingActionButton = {
                // Tombol muncul hanya jika user sedang menggeser peta manual (Follow Mode Off)
                if (!mapState.isFollowMode) {
                    FloatingActionButton(
                        onClick = { viewModel.enableFollowMode() },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Recenter",
                            tint = Color.White
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                // Tampilan Peta
                OsmMapView(
                    modifier = Modifier.fillMaxSize(),
                    mapState = mapState,
                    onMapTouched = { viewModel.disableFollowMode() }
                )

                // Overlay jika izin belum diberikan
                if (!locationPermissionState.status.isGranted) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.Black.copy(alpha = 0.4f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Card {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    val text = if (locationPermissionState.status.shouldShowRationale) {
                                        "Izin lokasi diperlukan untuk melacak posisi Anda secara real-time."
                                    } else {
                                        "Aplikasi memerlukan izin lokasi untuk berfungsi."
                                    }
                                    Text(text = text)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { locationPermissionState.launchPermissionRequest() }
                                    ) {
                                        Text("Berikan Izin Lokasi")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}