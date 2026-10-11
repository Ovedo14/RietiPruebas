package com.example.rietiapp.view.screens.formularioPantallas

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rietiapp.view.components.RietiSelectableCard
import com.example.rietiapp.viewmodel.ReporteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipoReporteScreen(
    viewModel: ReporteViewModel = viewModel(),
    onContinuarClick: () -> Unit
) {
    val uiState = viewModel.uiState

    // Cargar la selección previa si el usuario ya la había guardado en el ViewModel
    var seleccion by remember {
        mutableStateOf(if (uiState.modalidad == "SEGUIMIENTO") "seguimiento" else "anonimo")
    }

    var correo by remember { mutableStateOf(uiState.correo) }

    val esFormularioValido = when (seleccion) {
        "anonimo" -> true
        "seguimiento" -> esCorreoValido(correo)
        else -> false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .imePadding()
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Puedes realizar tu reporte de forma anónima o proporcionar un correo electrónico para recibir seguimiento.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Opción: Anónimo
        RietiSelectableCard(
            titulo = "Reporte anónimo",
            descripcion = "No se solicitarán datos de contacto.",
            selected = seleccion == "anonimo",
            onClick = {
                seleccion = "anonimo"
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Opción: Seguimiento
        RietiSelectableCard(
            titulo = "Reporte con seguimiento",
            descripcion = "Recibe actualizaciones del reporte por correo.",
            selected = seleccion == "seguimiento",
            onClick = {
                seleccion = "seguimiento"
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de correo electrónico condicional
        if (seleccion == "seguimiento") {
            val correoInvalido = correo.isNotBlank() && !esCorreoValido(correo)
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo electrónico") },
                isError = correoInvalido,
                supportingText = {
                    if (correoInvalido) {
                        Text(
                            text = "Ingresa un correo electrónico válido (ej. usuario@gmail.com)",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val modalidadValida = if (seleccion == "seguimiento") "SEGUIMIENTO" else "ANONIMO"
                val correoValido = if (seleccion == "seguimiento") correo.trim() else ""

                viewModel.updateState { estadoActual ->
                    estadoActual.copy(
                        modalidad = modalidadValida,
                        correo = correoValido
                    )
                }
                onContinuarClick()
            },
            enabled = esFormularioValido,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continuar")
        }
    }
}

// Función de validación de sintaxis de correo
fun esCorreoValido(correo: String): Boolean {
    return correo.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()
}