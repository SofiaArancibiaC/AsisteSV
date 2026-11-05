package com.js.smartvolt.asiste.ui.components

import android.content.Context
import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.CopyrightOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

@Composable
fun OsmMapView(
    faenaLat: Double,
    faenaLng: Double,
    radioMetros: Double,
    usuarioLat: Double?,
    usuarioLng: Double?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(18.0)
            controller.setCenter(GeoPoint(faenaLat, faenaLng))
        }
    }

    // Overlays creados una sola vez (evita parpadeo)
    val overlayRadio = remember(mapView) { Polygon() }
    val overlayFaena = remember(mapView) {
        Marker(mapView).apply {
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Faena"
        }
    }
    val overlayUsuario = remember(mapView) {
        Marker(mapView).apply {
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Tu ubicación"
        }
    }

    DisposableEffect(lifecycleOwner) {
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

    // Solo recentra cuando cambia la posición del usuario
    LaunchedEffect(usuarioLat, usuarioLng) {
        if (usuarioLat != null && usuarioLng != null) {
            mapView.controller.animateTo(GeoPoint(usuarioLat, usuarioLng))
        }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            mapView.apply {
                overlays.add(CopyrightOverlay(context))   // atribución obligatoria de OSM
                overlays.add(overlayRadio)
                overlays.add(overlayFaena)
                overlays.add(overlayUsuario)
            }
        },
        update = { map ->
            val centro = GeoPoint(faenaLat, faenaLng)

            overlayFaena.position = centro
            overlayRadio.points = Polygon.pointsAsCircle(centro, radioMetros)
            overlayRadio.fillPaint.color = Color.argb(45, 0, 160, 80)
            overlayRadio.outlinePaint.color = Color.argb(200, 0, 160, 80)
            overlayRadio.outlinePaint.strokeWidth = 3f

            if (usuarioLat != null && usuarioLng != null) {
                overlayUsuario.position = GeoPoint(usuarioLat, usuarioLng)
                overlayUsuario.isEnabled = true
            } else {
                overlayUsuario.isEnabled = false
            }

            map.invalidate()
        }
    )
}