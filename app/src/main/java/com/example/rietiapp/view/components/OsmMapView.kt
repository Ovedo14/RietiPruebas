package com.example.rietiapp.view.components

import android.content.Context
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

val ESRI_WORLD_STREET_MAP = object : OnlineTileSourceBase(
    "EsriWorldStreetMap",
    0, 19, 256, "",
    arrayOf("https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/")
) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        return baseUrl + MapTileIndex.getZoom(pMapTileIndex) + "/" + MapTileIndex.getY(pMapTileIndex) + "/" + MapTileIndex.getX(pMapTileIndex)
    }
}
@Composable
fun OsmMapView(
    selectedGeoPoint: GeoPoint?,
    onLocationSelected: (GeoPoint) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    DisposableEffect(context) {
        try {
            context.cacheDir.resolve("osmdroid").deleteRecursively()
            context.filesDir.resolve("osmdroid").deleteRecursively()
            context.getDatabasePath("osmdroid.db")?.delete()
        } catch (_: Exception) {}

        Configuration.getInstance().userAgentValue = "RietiAppMobile/1.0"
        Configuration.getInstance().load(context, context.getSharedPreferences("osm_pref", Context.MODE_PRIVATE))
        onDispose { }
    }

    AndroidView(
        modifier = modifier.clipToBounds(),
        factory = { ctx ->
            Configuration.getInstance().userAgentValue = "RietiAppMobile/1.0"
            Configuration.getInstance().load(ctx, ctx.getSharedPreferences("osm_pref", Context.MODE_PRIVATE))

            MapView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                setTileSource(ESRI_WORLD_STREET_MAP)
                setMultiTouchControls(true)

                controller.setZoom(15.0)
                controller.setCenter(GeoPoint(19.4326, -99.1332))

                setOnTouchListener { view, event ->
                    when (event.actionMasked) {
                        android.view.MotionEvent.ACTION_DOWN,
                        android.view.MotionEvent.ACTION_POINTER_DOWN -> {
                            view.parent.requestDisallowInterceptTouchEvent(true)
                        }
                        android.view.MotionEvent.ACTION_UP,
                        android.view.MotionEvent.ACTION_CANCEL -> {
                            view.parent.requestDisallowInterceptTouchEvent(false)
                        }
                    }
                    false
                }

                val receiver = object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                        onLocationSelected(p)
                        return true
                    }

                    override fun longPressHelper(p: GeoPoint): Boolean {
                        return false
                    }
                }
                overlays.add(MapEventsOverlay(receiver))

                onResume()
            }
        },
        update = { mapView ->
            mapView.overlays.removeAll { it is Marker }

            if (selectedGeoPoint != null) {
                val marker = Marker(mapView).apply {
                    position = selectedGeoPoint
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Ubicación seleccionada"
                }
                mapView.overlays.add(marker)
                mapView.controller.animateTo(selectedGeoPoint)
            }

            mapView.requestLayout()
            mapView.invalidate()
        }
    )
}