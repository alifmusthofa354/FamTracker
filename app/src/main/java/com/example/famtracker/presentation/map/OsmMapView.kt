package com.example.famtracker.presentation.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.famtracker.presentation.home.MapState
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun OsmMapView(
    modifier: Modifier = Modifier,
    mapState: MapState // Menerima state dari ViewModel
) {
    AndroidView(
        factory = { context ->
            // Blok factory dijalankan sekali untuk membuat View
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
            }
        },
        modifier = modifier,
        update = { mapView ->
            // Blok update dijalankan setiap kali `mapState` berubah
            // Di sinilah reaktivitas terjadi!
            val controller = mapView.controller
            controller.setZoom(mapState.zoomLevel)
            controller.setCenter(mapState.centerLocation)
            // Contoh: Tambahkan atau update marker berdasarkan state
            mapView.overlays.clear()
            val marker = Marker(mapView)
            marker.position = mapState.centerLocation
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = "Lokasi Pusat"
            mapView.overlays.add(marker)

            // Perbarui tampilan peta
            mapView.invalidate()
        }
    )
}