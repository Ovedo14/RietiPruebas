package com.example.rietiapp.view.screens.formularioPantallas

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.rietiapp.view.screens.formularioPantallas.pasos.Paso1Informacion
import com.example.rietiapp.view.screens.formularioPantallas.pasos.Paso2Ubicacion
import com.example.rietiapp.viewmodel.ReporteViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(
    viewModel: ReporteViewModel,
    onEnviarClick: () -> Unit
) {
    var pasoActual by remember { mutableIntStateOf(0) } // 0 = Paso 1, 1 = Paso 2
    val uiState = viewModel.uiState
    val context = LocalContext.current

    // --- VALIDACIONES POR PASO ---
    // Paso 1: Actividad seleccionada y descripción de al menos 15 caracteres
    val paso1Valido = uiState.actividadSeleccionada.isNotBlank() && uiState.descripcion.trim().length >= 15

    // Paso 2: Ubicación seleccionada en mapa y aceptación del aviso
    val paso2Valido = uiState.latitud != 0.0 && uiState.longitud != 0.0 && uiState.aceptoAviso

    // Control para activar el botón "Continuar" / "Enviar"
    val puedeAvanzarOEnviar = when (pasoActual) {
        0 -> paso1Valido
        1 -> paso2Valido
        else -> false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(8.dp)
        ) {
            Text(
                text = "Reportar caso",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Barra de progreso dividida en 2 pasos
                    LinearProgressIndicator(
                        progress = { (pasoActual + 1) / 2f },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Paso ${pasoActual + 1} de 2",
                        style = MaterialTheme.typography.labelLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Alterna solo entre Paso 1 y Paso 2
                    when (pasoActual) {
                        0 -> Paso1Informacion(
                            cantidadSeleccionada = uiState.cantidadSeleccionada,
                            onCantidadChange = { valor ->
                                viewModel.updateState { it.copy(cantidadSeleccionada = valor) }
                            },
                            edadSeleccionada = uiState.edadSeleccionada,
                            onEdadSelected = { valor ->
                                viewModel.updateState { it.copy(edadSeleccionada = valor) }
                            },
                            generoSeleccionado = uiState.generoSeleccionado,
                            onGeneroSelected = { valor ->
                                viewModel.updateState { it.copy(generoSeleccionado = valor) }
                            },
                            actividadSeleccionada = uiState.actividadSeleccionada,
                            onActividadSelected = { valor ->
                                viewModel.updateState { it.copy(actividadSeleccionada = valor) }
                            },
                            hora = uiState.hora,
                            onHoraSelected = { valor ->
                                viewModel.updateState { it.copy(hora = valor) }
                            },
                            riesgo = uiState.riesgo,
                            onRiesgoChange = { valor ->
                                viewModel.updateState { it.copy(riesgo = valor) }
                            },
                            descripcion = uiState.descripcion,
                            onDescripcionChange = { valor ->
                                viewModel.updateState { it.copy(descripcion = valor) }
                            }
                        )

                        1 -> Column {
                            Paso2Ubicacion(
                                referencias = uiState.referencias,
                                onReferenciasChange = { valor ->
                                    viewModel.updateState { it.copy(referencias = valor) }
                                },
                                fotosUris = uiState.fotosUris,
                                onFotosChange = { uris ->
                                    viewModel.updateState { it.copy(fotosUris = uris) }
                                },
                                ubicacionDireccion = uiState.direccion,
                                onUbicacionChange = { lat, lng, dir, municipioDetectado ->
                                    viewModel.resolverYActualizarUbicacion(lat, lng, dir, municipioDetectado)
                                }
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // --- AVISO DE PRIVACIDAD EN EL PASO 2 (UBICACIÓN Y ENVÍO) ---
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Checkbox(
                                    checked = uiState.aceptoAviso,
                                    onCheckedChange = { nuevoEstado ->
                                        viewModel.updateState { it.copy(aceptoAviso = nuevoEstado) }
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "He leído y acepto el Aviso de Privacidad *",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botones de navegación
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (pasoActual > 0) {
                            OutlinedButton(
                                onClick = { pasoActual-- },
                                enabled = !uiState.isLoading,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Atrás")
                            }
                        }

                        Button(
                            onClick = {
                                if (pasoActual == 0) {
                                    pasoActual = 1
                                } else {
                                    // Petición POST al backend
                                    viewModel.enviarReporteConEvidencias(context) {
                                        onEnviarClick()
                                    }
                                }
                            },
                            enabled = puedeAvanzarOEnviar && !uiState.isLoading,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(if (pasoActual == 1) "Enviar Reporte" else "Continuar")
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal de error si la llamada HTTP falla
    if (uiState.error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.updateState { it.copy(error = null) } },
            title = { Text("Atención") },
            text = { Text(uiState.error ?: "Ocurrió un error inesperado.") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.updateState { it.copy(error = null) } }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}