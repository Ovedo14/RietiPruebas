package com.example.rietiapp.view.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rietiapp.ui.theme.clay

@Composable
fun RietiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = Color.Unspecified,
    icon: @Composable (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val bgColor = if (backgroundColor == Color.Unspecified) MaterialTheme.colorScheme.tertiary else backgroundColor

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(32.dp))
            .clay(
                backgroundColor = bgColor,
                cornerRadius = 32.dp,
                elevation = 6.dp,
                pressed = isPressed && enabled
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Quitamos el ripple por defecto para que luzca el clay
                enabled = enabled,
                onClick = onClick
            )
            .padding(vertical = 16.dp, horizontal = 24.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            if (icon != null) {
                icon()
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(12.dp))
            }
            Text(
                text = text,
                color = if (enabled) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onTertiary.copy(alpha = 0.5f),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}