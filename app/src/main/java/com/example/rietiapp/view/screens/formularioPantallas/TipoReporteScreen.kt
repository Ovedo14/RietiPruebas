package com.example.rietiapp.view.screens.formularioPantallas

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rietiapp.viewmodel.ReporteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipoReporteScreen(
    viewModel: ReporteViewModel = viewModel(),
    onContinuarClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val uiState = viewModel.uiState
    var seleccion by remember { mutableStateOf(if (uiState.modalidad == "SEGUIMIENTO") "seguimiento" else "anonimo") }
    var correo by remember { mutableStateOf(uiState.correo) }
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        scrollState.scrollTo(0)
    }

    val esFormularioValido = when (seleccion) {
        "anonimo" -> true
        "seguimiento" -> esCorreoValido(correo)
        else -> false
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Custom
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
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    text = "Tipo de Reporte",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val minHeight = maxHeight
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .heightIn(min = minHeight)
                        .verticalScroll(scrollState)
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .imePadding()
                ) {
                    Text(
                        text = "Puedes realizar tu reporte de forma anónima o proporcionar un correo electrónico para recibir seguimiento.",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Opción Anónimo
                    ModernSelectableCard(
                        title = "Reporte anónimo",
                        subtitle = "No se solicitarán datos de contacto.",
                        selected = seleccion == "anonimo",
                        onClick = { seleccion = "anonimo" }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Opción Seguimiento
                    ModernSelectableCard(
                        title = "Reporte con seguimiento",
                        subtitle = "Recibe actualizaciones por correo.",
                        selected = seleccion == "seguimiento",
                        onClick = { seleccion = "seguimiento" }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Campo de correo
                    if (seleccion == "seguimiento") {
                        val correoInvalido = correo.isNotBlank() && !esCorreoValido(correo)
                        Text(
                            text = "Correo Electrónico",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = correo,
                            onValueChange = { correo = it },
                            placeholder = { Text("ej. usuario@gmail.com") },
                            isError = correoInvalido,
                            supportingText = {
                                if (correoInvalido) {
                                    Text(
                                        text = "Ingresa un correo electrónico válido",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedContainerColor = MaterialTheme.colorScheme.surface,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface
                            )
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(
                                elevation = if (esFormularioValido) 8.dp else 0.dp,
                                shape = RoundedCornerShape(16.dp),
                                spotColor = MaterialTheme.colorScheme.primary
                            ),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text("Continuar", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun ModernSelectableCard(title: String, subtitle: String, selected: Boolean, onClick: () -> Unit) {
    val bgColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent
    val shadowColor = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.3f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = shadowColor, ambientColor = shadowColor)
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(
            imageVector = if (selected) Icons.Rounded.CheckCircle else Icons.Rounded.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(28.dp)
        )
    }
}

fun esCorreoValido(correo: String): Boolean {
    return correo.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches()
}
