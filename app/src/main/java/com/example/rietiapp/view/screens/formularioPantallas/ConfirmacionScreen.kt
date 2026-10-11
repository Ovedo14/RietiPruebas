package com.example.rietiapp.view.screens.formularioPantallas

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rietiapp.view.components.RietiButton
import com.example.rietiapp.viewmodel.ReporteViewModel

@Composable
fun ConfirmacionScreen(
    viewModel: ReporteViewModel,
    onInicioClick: () -> Unit
) {
    val uiState = viewModel.uiState
    val esSeguimiento = uiState.modalidad == "SEGUIMIENTO"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "¡Gracias por ayudar a proteger los derechos de niñas, niños y adolescentes!",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tu reporte fue recibido correctamente.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // MOSTRAR EL FOLIO ÚNICAMENTE SI LA MODALIDAD ES SEGUIMIENTO
        if (esSeguimiento) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Folio asignado",
                        style = MaterialTheme.typography.labelMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = uiState.folio.ifBlank { "Generando folio..." },
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Conserva este folio para futuras consultas y seguimiento.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            // MENSAJE PARA REPORTE ANÓNIMO
            Text(
                text = "Al haber elegido un reporte anónimo, la información no fue vinculada a ningún dato personal de contacto.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        RietiButton(
            text = "Regresar al inicio",
            onClick = onInicioClick
        )
    }
}