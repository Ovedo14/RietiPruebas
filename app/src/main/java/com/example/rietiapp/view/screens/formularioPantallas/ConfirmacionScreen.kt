package com.example.rietiapp.view.screens.formularioPantallas

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rietiapp.viewmodel.ReporteViewModel

@Composable
fun ConfirmacionScreen(
    viewModel: ReporteViewModel,
    onInicioClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val uiState = viewModel.uiState

    // Obtenemos el folio generado del ViewModel
    val folio = uiState.folio.ifBlank { "Sin Folio" }

    // Evaluamos si el reporte fue registrado de forma anónima
    val esAnonimo = uiState.modalidad == "ANONIMO" || uiState.correo.isBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.weight(0.3f))

        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Éxito",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(72.dp)
        )

        Text(
            text = "¡Reporte Registrado!",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )

        // Mensaje de agradecimiento por proteger los derechos de la infancia
        Text(
            text = "Gracias por ayudar a proteger los derechos e integridad de las niñas, niños y adolescentes.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = "Guarda tu número de folio para dar seguimiento al estado de tu reporte dentro de la aplicación.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Tarjeta con Folio + Botones Compactos de Copiar y Compartir
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "FOLIO DE SEGUIMIENTO",
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    text = folio,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 1. Botón Copiar al Portapapeles (Compacto)
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(folio))
                            Toast.makeText(
                                context,
                                "Folio copiado al portapapeles",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp), // 🟢 Padding interno reducido
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copiar",
                            modifier = Modifier.size(16.dp) // 🟢 Ícono más pequeño
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Copiar",
                            style = MaterialTheme.typography.labelMedium // 🟢 Texto más pequeño
                        )
                    }

                    // 2. Botón Compartir (Compacto)
                    Button(
                        onClick = {
                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Hola, guardo mi folio de seguimiento para mi reporte en RIETI: $folio"
                                )
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Compartir Folio")
                            context.startActivity(shareIntent)
                        },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp), // 🟢 Padding interno reducido
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartir",
                            modifier = Modifier.size(16.dp) // 🟢 Ícono más pequeño
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Compartir",
                            style = MaterialTheme.typography.labelMedium // 🟢 Texto más pequeño
                        )
                    }
                }
            }
        }

        // Si NO es anónimo mostramos el aviso de correo
        if (!esAnonimo) {
            Text(
                text = "Enviaremos actualizaciones sobre el seguimiento de este reporte al correo registrado: ${uiState.correo}",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onInicioClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al Inicio")
        }
    }
}