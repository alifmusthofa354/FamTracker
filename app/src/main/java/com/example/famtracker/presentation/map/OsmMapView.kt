package com.example.famtracker.presentation.map

import android.view.MotionEvent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.example.famtracker.presentation.home.MapState
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun OsmMapView(
    modifier: Modifier = Modifier,
    mapState: MapState,
    onMapTouched: () -> Unit // Callback saat user menggeser peta
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Pastikan kita selalu pakai versi terbaru dari callback
    val currentOnMapTouched = rememberUpdatedState(onMapTouched)

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            // Deteksi sentuhan untuk mematikan follow mode
            setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_MOVE) {
                    currentOnMapTouched.value()
                }
                false
            }
        }
    }

    val myMarker = remember(mapView) {
        Marker(mapView).apply {
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Saya"
            mapView.overlays.add(this)
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    // Pindahkan logika animasi kamera ke sini agar tidak dipanggil berulang-ulang saat recomposition
    // hanya karena state lain berubah.
    LaunchedEffect(mapState.centerLocation, mapState.isFollowMode) {
        if (mapState.isFollowMode) {
            mapState.centerLocation?.let { location ->
                mapView.controller.animateTo(location)
                mapView.controller.setZoom(mapState.zoomLevel)
            }
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier,
        update = { view ->
            // Update visual marker saja (ringan)
            mapState.centerLocation?.let { location ->
                myMarker.position = location
                view.invalidate()
            }
        }
    )
}