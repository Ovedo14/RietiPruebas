package com.example.rietiapp.view.components

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri

@Composable
fun PeligroInmediatoCard() {

    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "¿La niña, niño o adolescente se encuentra en peligro inmediato?",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Si existe un riesgo inminente para su vida, integridad o seguridad, comunícate de inmediato con los servicios de respuesta del C5."
            )

            // Tarjeta del C5 (Centro de Control, Comando, Comunicación, Cómputo y Calidad)
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Centro de Mando C5 (Emergencias)",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = "911",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = "tel:911".toUri()
                            }
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Llamar al C5")
                    }
                }
            }

            HorizontalDivider()

            Text(
                text = "IMPORTANTE",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "El formulario RIETI es una herramienta de registro institucional y no sustituye la atención inmediata de emergencias del C5 ni las denuncias ante la Procuraduría de Protección de NNyA."
            )

            HorizontalDivider()

            Text(
                text = "Si la situación es de observación o seguimiento sin riesgo inminente, puedes continuar completando el reporte en la aplicación."
            )
        }
    }
}