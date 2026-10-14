package com.example.rietiapp.view.screens.seguimientoReportes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rietiapp.view.components.EstadoCard
import com.example.rietiapp.viewmodel.SeguimientoViewModel

@Composable
fun ConsultaReporteScreen(
    viewModel: SeguimientoViewModel,
    onBackClick: () -> Unit
) {
    val uiState = viewModel.uiState
    val reporte = uiState.reporteEncontrado

    if (reporte == null) return

    val scrollState = rememberScrollState()
    androidx.compose.runtime.LaunchedEffect(Unit) {
        scrollState.scrollTo(0)
    }

    val bgColor = MaterialTheme.colorScheme.primary

    Box(modifier = Modifier.fillMaxSize().background(bgColor)) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Header
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
                    text = "Estado del Reporte",
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
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // Tarjeta Estado Actual
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text("ESTADO ACTUAL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = reporte.estado,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = uiState.descripcionEstatus, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    // Detalles
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = Color.LightGray),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            DetalleItem(label = "Folio", value = reporte.folio)
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            DetalleItem(label = "Fecha de recepción", value = reporte.fechaFormateada)
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            DetalleItem(label = "Municipio de atención", value = reporte.municipio.ifBlank { "No asignado" })
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Progreso del reporte", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)

                    // Tarjetas de progreso
                    EstadoCard(numero = "1", titulo = "Recibido", activo = uiState.nivelProgreso >= 1)
                    EstadoCard(numero = "2", titulo = "En revisión", activo = uiState.nivelProgreso >= 2)
                    EstadoCard(numero = "3", titulo = "En seguimiento", activo = uiState.nivelProgreso >= 3)
                    EstadoCard(numero = "4", titulo = "Canalizado", activo = uiState.nivelProgreso >= 4)
                    EstadoCard(numero = "5", titulo = "Concluido", activo = uiState.nivelProgreso >= 5)
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun DetalleItem(label: String, value: String) {
    Column {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}
