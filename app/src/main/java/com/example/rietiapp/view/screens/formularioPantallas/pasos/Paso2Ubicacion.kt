package com.example.rietiapp.view.screens.formularioPantallas.pasos

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rietiapp.view.components.OsmMapView
import com.example.rietiapp.viewmodel.UbicacionViewModel

@Composable
fun Paso2Ubicacion(
    fotosUris: List<Uri>,
    onFotosChange: (List<Uri>) -> Unit,
    modifier: Modifier = Modifier,
    referencias: String = "",
    onReferenciasChange: (String) -> Unit = {},
    ubicacionDireccion: String = "",
    onUbicacionChange: (lat: Double, lng: Double, dir: String, municipio: String) -> Unit = { _, _, _, _ -> },
    ubicacionViewModel: UbicacionViewModel = viewModel()
) {
    val context = LocalContext.current
    val estadoUbicacion = ubicacionViewModel.estadoUbicacion

    val solicitarUbicacionActual: () -> Unit = {
        ubicacionViewModel.solicitarUbicacionActual(context, onUbicacionChange)
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
    }

    Column(
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
                text = "Proporciona la ubicación aproximada donde observaste la situación.",
                style = MaterialTheme.typography.bodyMedium
            )

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
                            text = "Selecciona tu ubicación en el mapa (toca un punto):",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        OsmMapView(
                            selectedGeoPoint = estadoUbicacion.geoPoint,
                            onLocationSelected = { geoPoint ->
                                ubicacionViewModel.seleccionarUbicacion(context, geoPoint, onUbicacionChange)
                            },
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
                            contentDescription = "Mi ubicación actual"
                        )
                    }
                }
            }

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
                        if (estadoUbicacion.estaCargandoDireccion) {
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
                            val direccionMostrar = ubicacionDireccion.ifBlank {
                                estadoUbicacion.direccion.ifBlank {
                                    "Toca un punto en el mapa para fijar la ubicación"
                                }
                            }
                            Text(
                                text = direccionMostrar,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (direccionMostrar != "Toca un punto en el mapa para fijar la ubicación") {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                } else {
                                    MaterialTheme.colorScheme.secondary
                                }
                            )
                        }
                    }
                }
            }

            OutlinedTextField(
                value = referencias,
                onValueChange = onReferenciasChange,
                label = { Text("Referencias del lugar (opcional)") },
                placeholder = { Text("Ej. Esquina con Av. Principal, frente a la tienda") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 3
            )

            SeccFotosReferencia(
                fotosUris = fotosUris,
                onFotosChange = onFotosChange
            )
        }
    }
}