package com.example.famtracker.presentation.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.famtracker.presentation.home.MapState
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun OsmMapView(
    modifier: Modifier = Modifier,
    mapState: MapState // Menerima state dari ViewModel
) {
    // 1. Dapatkan context dan lifecycle owner
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 2. Buat dan "ingat" instance MapView
    // `remember` memastikan MapView hanya dibuat SATU KALI dan tidak dibuat ulang saat recomposition
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
        }
    }

    // 3. Tangani lifecycle dengan DisposableEffect
    // Blok ini akan dijalankan saat komposable pertama kali dimasukkan ke dalam komposisi
    // dan `onDispose` akan dipanggil saat komposable dihapus.
    DisposableEffect(lifecycleOwner) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                mapView.onResume() // Resume peta saat aplikasi aktif
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                mapView.onPause() // Pause peta saat aplikasi di background untuk hemat baterai
            }
        }

        // Tambahkan observer ke lifecycle
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        // `onDispose` adalah blok pembersihan. SANGAT PENTING!
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver) // Hapus observer
            mapView.onDetach() // Lepaskan resource MapView untuk mencegah memory leak
        }
    }

    // 4. Gunakan instance MapView yang sudah diingat di AndroidView
    AndroidView(
        factory = { mapView },
        modifier = modifier,
        update = { view ->
            // TAMBAHKAN PEMERIKSAAN NULL DI SINI
            mapState.centerLocation?.let { location ->
                val controller = view.controller
                controller.setZoom(mapState.zoomLevel)
                controller.setCenter(location)

                // Update marker
                view.overlays.clear()
                val marker = Marker(view)
                marker.position = location
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                marker.title = "Lokasi Saya"
                view.overlays.add(marker)

                // Perbarui tampilan peta
                view.invalidate()
            }
        }
    )
}