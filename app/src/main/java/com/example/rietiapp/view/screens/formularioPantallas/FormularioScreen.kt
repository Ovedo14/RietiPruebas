package com.example.rietiapp.view.screens.formularioPantallas

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rietiapp.view.screens.formularioPantallas.pasos.Paso1Informacion
import com.example.rietiapp.view.screens.formularioPantallas.pasos.Paso2Ubicacion
import com.example.rietiapp.view.screens.formularioPantallas.pasos.Paso3Resumen
import com.example.rietiapp.viewmodel.ReporteViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioScreen(
    viewModel: ReporteViewModel,
    onEnviarClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var pasoActual by remember { mutableIntStateOf(0) }
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    LaunchedEffect(pasoActual) {
        scrollState.scrollTo(0)
    }

    val bgColor = MaterialTheme.colorScheme.primary // Fondo superior

    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Top Bar Custom
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Volver",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Text(
                    text = "Reportar Caso",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Contenedor principal estilo Bottom Sheet
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .shadow(16.dp, RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .imePadding()
                        .padding(24.dp)
                ) {
                    // Barra de progreso redondeada
                    LinearProgressIndicator(
                        progress = { (pasoActual + 1) / 3f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Paso ${pasoActual + 1} de 3",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Contenido del paso actual
                    when (pasoActual) {
                        0 -> Paso1Informacion(
                            cantidadSeleccionada = uiState.cantidadSeleccionada,
                            onCantidadChange = { valor -> viewModel.updateState { it.copy(cantidadSeleccionada = valor) } },
                            edadSeleccionada = uiState.edadSeleccionada,
                            onEdadSelected = { valor -> viewModel.updateState { it.copy(edadSeleccionada = valor) } },
                            generoSeleccionado = uiState.generoSeleccionado,
                            onGeneroSelected = { valor -> viewModel.updateState { it.copy(generoSeleccionado = valor) } },
                            actividadSeleccionada = uiState.actividadSeleccionada,
                            onActividadSelected = { valor -> viewModel.updateState { it.copy(actividadSeleccionada = valor) } },
                            hora = uiState.hora,
                            onHoraSelected = { valor -> viewModel.updateState { it.copy(hora = valor) } },
                            riesgo = uiState.riesgo,
                            onRiesgoChange = { valor -> viewModel.updateState { it.copy(riesgo = valor) } },
                            descripcion = uiState.descripcion,
                            onDescripcionChange = { valor -> viewModel.updateState { it.copy(descripcion = valor) } }
                        )
                        1 -> Paso2Ubicacion(
                            referencias = uiState.referencias,
                            onReferenciasChange = { valor -> viewModel.updateState { it.copy(referencias = valor) } },
                            fotosUris = uiState.fotosUris,
                            onFotosChange = { uris -> viewModel.updateState { it.copy(fotosUris = uris) } },
                            ubicacionDireccion = uiState.direccion,
                            onUbicacionChange = { lat, lng, dir, municipioDetectado ->
                                viewModel.resolverYActualizarUbicacion(lat, lng, dir, municipioDetectado)
                            }
                        )
                        2 -> Paso3Resumen(
                            cantidad = uiState.cantidadSeleccionada,
                            edadSeleccionada = uiState.edadSeleccionada,
                            generoSeleccionado = uiState.generoSeleccionado,
                            actividadSeleccionada = uiState.actividadSeleccionada,
                            hora = uiState.hora,
                            riesgo = uiState.riesgo,
                            descripcion = uiState.descripcion,
                            fotosUris = uiState.fotosUris,
                            ubicacionDireccion = uiState.direccion
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Botones de acción
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (pasoActual > 0) {
                            OutlinedButton(
                                onClick = { pasoActual-- },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Atrás", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = {
                                if (pasoActual < 2) {
                                    pasoActual++
                                } else {
                                    viewModel.enviarReporteConEvidencias(context) {
                                        onEnviarClick()
                                    }
                                }
                            },
                            enabled = !uiState.isLoading,
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 3.dp
                                )
                            } else {
                                Text(
                                    text = if (pasoActual == 2) "Enviar" else "Siguiente",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
