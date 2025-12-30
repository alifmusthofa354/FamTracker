package com.example.famtracker.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.model.rememberScreenModel
import com.example.famtracker.presentation.map.OsmMapView
import org.osmdroid.util.GeoPoint

class HomeScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        // 1. Dapatkan instance HomeViewModel
        val viewModel = rememberScreenModel { HomeViewModel() }

        // 2. Kumpulkan state dari ViewModel sebagai State yang bisa di-observe oleh Compose
        val mapState by viewModel.mapState.collectAsState()

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
                // 3. Tampilkan komponen peta dan teruskan state-nya
                OsmMapView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    mapState = mapState
                )

                // 4. Contoh interaksi untuk menguji reaktivitas
                Button(
                    onClick = {
                        // Panggil fungsi di ViewModel untuk mengubah state
                        // Ini akan memicu blok `update` di OsmMapView
                        viewModel.updateMapCenter(GeoPoint(-6.9175, 107.6191)) // Pindah ke Bandung
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Pindah ke Bandung")
                }
            }
        }
    }
}