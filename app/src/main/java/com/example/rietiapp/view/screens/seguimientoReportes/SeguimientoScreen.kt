package com.example.rietiapp.view.screens.seguimientoReportes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rietiapp.viewmodel.SeguimientoViewModel

@Composable
fun SeguimientoScreen(
    viewModel: SeguimientoViewModel, // 🟢 1. Recibe el ViewModel
    onConsultarClick: () -> Unit
) {
    val uiState = viewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Seguimiento de reporte",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = "Ingresa el folio de tu reporte",
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = uiState.folioInput,
            onValueChange = { viewModel.onFolioInputChange(it.uppercase()) }, // 🟢 Forzar mayúsculas
            label = { Text("Folio") },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters, // 🟢 Teclado en mayúsculas
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = { viewModel.buscarReporte(onExito = onConsultarClick) } // 🟢 Buscar desde el teclado
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                // 🟢 4. LLAMA A LA API DE AWS Y SOLO SI ES EXITOSO NAVEGA
                viewModel.buscarReporte(onExito = onConsultarClick)
            },
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Consultar")
            }
        }
    }
}