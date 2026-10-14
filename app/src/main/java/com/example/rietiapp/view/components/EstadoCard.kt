package com.example.rietiapp.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EstadoCard(
    numero: String,
    titulo: String,
    activo: Boolean
) {
    val bgColor = if (activo) androidx.compose.material3.MaterialTheme.colorScheme.surface else androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
    val shadowAlpha = if (activo) 0.3f else 0.0f
    val iconBgColor = if (activo) androidx.compose.material3.MaterialTheme.colorScheme.primary else androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
    val iconTextColor = if (activo) androidx.compose.material3.MaterialTheme.colorScheme.onPrimary else androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
    val titleColor = if (activo) androidx.compose.material3.MaterialTheme.colorScheme.onSurface else androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .shadow(if (activo) 6.dp else 0.dp, RoundedCornerShape(16.dp), spotColor = androidx.compose.material3.MaterialTheme.colorScheme.primary.copy(alpha = shadowAlpha)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Número circular
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = numero,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = iconTextColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = titulo,
                fontSize = 16.sp,
                fontWeight = if (activo) FontWeight.Bold else FontWeight.Medium,
                color = titleColor
            )
        }
    }
}
