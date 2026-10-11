package com.example.rietiapp.view.screens.formularioPantallas.pasos

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

/**
 * Componente ejecutable del Paso 2 del formulario de reporte.
 * Muestra un mapa interactivo basado en OpenStreetMap/osmdroid para la selección de ubicación geográfica,
 * resuelve la geocodificación inversa mediante Geocoder de Android, permite adjuntar imágenes
 * de evidencia e ingresar referencias del lugar.
 *
 * @param fotosUris Lista de [Uri]s locales con las fotografías seleccionadas.
 * @param onFotosChange Callback al modificar la lista de fotos.
 * @param modifier Modificador composable opcional.
 * @param ubicacionDireccion Dirección del lugar resuelta por geocodificación.
 * @param onUbicacionChange Callback invocado al seleccionar un punto en el mapa con latitud, longitud, dirección y municipio.
 * @param referencias Texto con referencias adicionales ingresadas por el usuario.
 * @param onReferenciasChange Callback al editar las referencias del lugar.
 */
@Composable
fun Paso2Ubicacion(
    fotosUris: List<Uri>,
    onFotosChange: (List<Uri>) -> Unit,
    modifier: Modifier = Modifier,
    ubicacionDireccion: String = "",
    onUbicacionChange: (lat: Double, lng: Double, direccion: String, municipio: String) -> Unit = { _, _, _, _ -> },
    referencias: String = "",
    onReferenciasChange: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var estaCargandoDireccion by remember { mutableStateOf(false) }

    val seleccionarUbicacion: (GeoPoint) -> Unit = { geoPoint ->
        selectedPoint = geoPoint
        estaCargandoDireccion = true
        coroutineScope.launch {
            val (direccion, municipio) = obtenerDireccionYMunicipio(context, geoPoint)
            estaCargandoDireccion = false
            onUbicacionChange(geoPoint.latitude, geoPoint.longitude, direccion, municipio)
        }
    }

    val solicitarUbicacionActual: () -> Unit = {
        obtenerUbicacionActual(context) { geoPoint ->
            seleccionarUbicacion(geoPoint)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            solicitarUbicacionActual()
        }
    }

    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if ((hasFine || hasCoarse) && ubicacionDireccion.isBlank()) {
            solicitarUbicacionActual()
        }
    }

    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "2. ¿Dónde ocurrió?",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Toca un punto en el mapa para marcar el lugar del reporte, o usa el botón de GPS si estás en el sitio.",
                style = MaterialTheme.typography.bodyMedium
            )

            // MAPA INTERACTIVO
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = "Toca cualquier punto del mapa para fijar el lugar:",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        OsmMapView(
                            selectedGeoPoint = selectedPoint,
                            onLocationSelected = seleccionarUbicacion,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }

                    SmallFloatingActionButton(
                        onClick = {
                            val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

                            if (hasFine || hasCoarse) {
                                solicitarUbicacionActual()
                            } else {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp),
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Usar mi ubicación actual"
                        )
                    }
                }
            }

            // TARJETA DE DIRECCIÓN DETECTADA
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Ubicación",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ubicación seleccionada:",
                            style = MaterialTheme.typography.labelMedium
                        )
                        if (estaCargandoDireccion) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                                Text(
                                    text = "Obteniendo dirección...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        } else {
                            Text(
                                text = if (ubicacionDireccion.isNotBlank()) ubicacionDireccion else "Ninguna (Toca un punto en el mapa)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (ubicacionDireccion.isNotBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            if (ubicacionDireccion.isBlank()) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = "Para avanzar al siguiente paso es necesario marcar un punto en el mapa.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            OutlinedTextField(
                value = referencias,
                onValueChange = onReferenciasChange,
                label = { Text("Referencias del lugar (opcional)") },
                placeholder = { Text("Ej. Entre calle A y B, frente al parque, fachada color azul...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3,
                singleLine = false,
                supportingText = {
                    Text("Detalles visuales para localizar el punto exacto.")
                }
            )

            SeccFotosReferencia(
                fotosUris = fotosUris,
                onFotosChange = onFotosChange
            )
        }
    }
}

private val ESRI_WORLD_STREET_MAP = object : OnlineTileSourceBase(
    "EsriWorldStreetMap",
    0, 19, 256, "",
    arrayOf("https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/")
) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        return baseUrl + MapTileIndex.getZoom(pMapTileIndex) + "/" + MapTileIndex.getY(pMapTileIndex) + "/" + MapTileIndex.getX(pMapTileIndex)
    }
}

/**
 * Componente Composable que integra la vista nativa de mapa de [MapView] de osmdroid en Jetpack Compose.
 *
 * @param selectedGeoPoint Coordenadas [GeoPoint] seleccionadas para desplegar un marcador.
 * @param onLocationSelected Callback al realizar tap sobre una ubicación en el mapa.
 * @param modifier Modificador composable opcional.
 */
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

/**
 * Obtiene la última ubicación GPS o de red conocida del dispositivo mediante [LocationManager].
 */
private fun obtenerUbicacionActual(
    context: Context,
    onUbicacionObtenida: (GeoPoint) -> Unit
) {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager ?: return
    val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    if (hasFine || hasCoarse) {
        val lastGps = try { locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER) } catch (_: Exception) { null }
        val lastNetwork = try { locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) } catch (_: Exception) { null }
        val bestLocation = lastGps ?: lastNetwork

        if (bestLocation != null) {
            onUbicacionObtenida(GeoPoint(bestLocation.latitude, bestLocation.longitude))
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val provider = if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                LocationManager.GPS_PROVIDER
            } else LocationManager.NETWORK_PROVIDER
            try {
                locationManager.getCurrentLocation(
                    provider,
                    null,
                    context.mainExecutor
                ) { location ->
                    if (location != null) {
                        onUbicacionObtenida(GeoPoint(location.latitude, location.longitude))
                    }
                }
            } catch (_: Exception) {}
        }
    }
}

/**
 * Resuelve la dirección y el municipio a partir de coordenadas geográficas utilizando [android.location.Geocoder].
 */
private suspend fun obtenerDireccionYMunicipio(
    context: Context,
    geoPoint: GeoPoint
): Pair<String, String> = withContext(Dispatchers.IO) {
    var direccion = ""
    var municipio = ""

    try {
        val geocoder = android.location.Geocoder(context, java.util.Locale.getDefault())
        @Suppress("DEPRECATION")
        val addresses = geocoder.getFromLocation(geoPoint.latitude, geoPoint.longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            val addr = addresses[0]
            val street = addr.thoroughfare ?: addr.getFeatureName() ?: ""
            val subLocality = addr.subLocality ?: addr.subAdminArea ?: ""
            val locality = addr.locality ?: ""
            val adminArea = addr.adminArea ?: ""

            municipio = addr.subAdminArea ?: addr.locality ?: ""

            val parts = listOf(street, subLocality, locality, adminArea).filter { it.isNotBlank() }
            if (parts.isNotEmpty()) {
                direccion = parts.joinToString(", ")
            } else {
                direccion = addr.getAddressLine(0) ?: ""
            }
        }
    } catch (_: Exception) {}

    if (direccion.isBlank()) {
        direccion = "Lat: %.4f, Lng: %.4f".format(geoPoint.latitude, geoPoint.longitude)
    }

    return@withContext Pair(direccion, municipio)
}
