package com.example.rietiapp.view.screens.seguimientoReportes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rietiapp.view.components.EstadoCard
import com.example.rietiapp.viewmodel.SeguimientoViewModel

@Composable
fun ConsultaReporteScreen(
    viewModel: SeguimientoViewModel
) {
    val uiState = viewModel.uiState
    val reporte = uiState.reporteEncontrado


    if (reporte == null) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Estado de tu reporte",
            style = MaterialTheme.typography.headlineMedium
        )

        // Estado Actual
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Estado actual", style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = reporte.estado,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = uiState.descripcionEstatus)
            }
        }

        // Detalles del Folio
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Folio", style = MaterialTheme.typography.labelLarge)
                Text(text = reporte.folio, style = MaterialTheme.typography.titleMedium)

                HorizontalDivider()

                Text(
                    text = "Fecha de recepción",
                    style = MaterialTheme.typography.labelLarge
                )

                Text(text = reporte.fechaFormateada)

                HorizontalDivider()

                Text("Municipio de atención", style = MaterialTheme.typography.labelLarge)
                Text(text = reporte.municipio.ifBlank { "No asignado" })
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Progreso del reporte", style = MaterialTheme.typography.titleMedium)

        // Tarjetas activadas según el nivel calculado por el ViewModel
        EstadoCard(numero = "1", titulo = "Recibido", activo = uiState.nivelProgreso >= 1)
        EstadoCard(numero = "2", titulo = "En revisión", activo = uiState.nivelProgreso >= 2)
        EstadoCard(numero = "3", titulo = "En seguimiento", activo = uiState.nivelProgreso >= 3)
        EstadoCard(numero = "4", titulo = "Canalizado", activo = uiState.nivelProgreso >= 4)
        EstadoCard(numero = "5", titulo = "Concluido", activo = uiState.nivelProgreso >= 5)
    }
}