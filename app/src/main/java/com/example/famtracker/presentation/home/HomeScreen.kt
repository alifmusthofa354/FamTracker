package com.example.famtracker.presentation.home

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import com.example.famtracker.R
import com.example.famtracker.presentation.map.OsmMapView

class HomeScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = getScreenModel<HomeViewModel>()
        val mapState by viewModel.mapState.collectAsState()
        val context = LocalContext.current

        // State untuk menyimpan status izin saat ini
        var isLocationPermissionGranted by remember {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            )
        }

        // Launcher untuk request permission
        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            isLocationPermissionGranted = isGranted
            if (isGranted) {
                viewModel.startLocationUpdates()
            }
        }

        // Efek samping: Jika izin sudah ada saat pertama kali screen dibuka, langsung start location
        LaunchedEffect(Unit) {
            if (isLocationPermissionGranted) {
                viewModel.startLocationUpdates()
            } else {
                // Jika belum, coba minta izin (opsional: bisa dipicu tombol biar lebih sopan)
                // permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(title = { Text(stringResource(R.string.app_name)) })
            },
            floatingActionButton = {
                if (!mapState.isFollowMode) {
                    FloatingActionButton(
                        onClick = { viewModel.enableFollowMode() },
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = stringResource(R.string.recenter),
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
                OsmMapView(
                    modifier = Modifier.fillMaxSize(),
                    mapState = mapState,
                    onMapTouched = { viewModel.disableFollowMode() }
                )

                // UI Overlay jika izin belum diberikan
                if (!isLocationPermissionGranted) {
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
                                    Text(text = stringResource(R.string.permission_rationale))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                        }
                                    ) {
                                        Text(stringResource(R.string.grant_permission))
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