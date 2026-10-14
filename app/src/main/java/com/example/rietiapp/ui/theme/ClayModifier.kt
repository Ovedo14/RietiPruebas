package com.example.rietiapp.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Modificador personalizado para aplicar el efecto Claymorphism real,
 * basado en el estilo flotante 3D (inflado + sobras profundas).
 */
fun Modifier.clay(
    backgroundColor: Color,
    cornerRadius: Dp = 32.dp, // Mucho más redondeado por defecto
    elevation: Dp = 16.dp, // Sombras exteriores más amplias
    pressed: Boolean = false
) = this.composed {
    this.drawBehind {
        val cornerRadiusPx = cornerRadius.toPx()
        val elevationPx = elevation.toPx()

        drawIntoCanvas { canvas ->
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            frameworkPaint.isAntiAlias = true

            // 1. Sombras Exteriores (Drop Shadows)
            if (!pressed) {
                // Sombra oscura y profunda abajo a la derecha (Da sensación de flotar)
                frameworkPaint.color = backgroundColor.toArgb()
                frameworkPaint.setShadowLayer(
                    elevationPx * 1.2f,
                    elevationPx / 1.5f,
                    elevationPx / 1.5f,
                    android.graphics.Color.argb(30, 0, 0, 0) // Sombra suave pero notable
                )
                canvas.drawRoundRect(
                    0f, 0f, size.width, size.height,
                    cornerRadiusPx, cornerRadiusPx, paint
                )
            }

            // 2. Dibujar la forma sólida principal
            frameworkPaint.clearShadowLayer()
            frameworkPaint.color = backgroundColor.toArgb()
            canvas.drawRoundRect(
                0f, 0f, size.width, size.height,
                cornerRadiusPx, cornerRadiusPx, paint
            )

            // Preparar el path de recorte para las sombras/brillos interiores
            val path = Path().apply {
                addRoundRect(
                    RoundRect(
                        left = 0f,
                        top = 0f,
                        right = size.width,
                        bottom = size.height,
                        cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                    )
                )
            }

            // 3. Sombras Interiores (Inner Shadows) - El corazón del Claymorphism
            clipPath(path) {
                frameworkPaint.style = android.graphics.Paint.Style.STROKE
                frameworkPaint.strokeWidth = elevationPx * 1.5f // Trazo grueso para el inner shadow
                frameworkPaint.color = android.graphics.Color.TRANSPARENT

                if (!pressed) {
                    // a) Sombra oscura interior abajo-derecha (Efecto inflado 3D)
                    frameworkPaint.setShadowLayer(
                        elevationPx * 0.8f,
                        -elevationPx / 3,
                        -elevationPx / 3,
                        android.graphics.Color.argb(30, 0, 0, 0) // Sombra oscura muy sutil
                    )
                    canvas.drawRoundRect(
                        0f, 0f, size.width, size.height,
                        cornerRadiusPx, cornerRadiusPx, paint
                    )

                    // b) Brillo intenso interior arriba-izquierda (Luz incidiendo)
                    frameworkPaint.setShadowLayer(
                        elevationPx * 1.2f,
                        elevationPx / 3,
                        elevationPx / 3,
                        android.graphics.Color.argb(200, 255, 255, 255) // Blanco muy brillante
                    )
                    canvas.drawRoundRect(
                        0f, 0f, size.width, size.height,
                        cornerRadiusPx, cornerRadiusPx, paint
                    )
                } else {
                    // Estado Presionado (Hundido)
                    frameworkPaint.setShadowLayer(
                        elevationPx,
                        elevationPx / 2,
                        elevationPx / 2,
                        android.graphics.Color.argb(60, 0, 0, 0)
                    )
                    canvas.drawRoundRect(
                        0f, 0f, size.width, size.height,
                        cornerRadiusPx, cornerRadiusPx, paint
                    )

                    frameworkPaint.setShadowLayer(
                        elevationPx,
                        -elevationPx / 2,
                        -elevationPx / 2,
                        android.graphics.Color.WHITE
                    )
                    canvas.drawRoundRect(
                        0f, 0f, size.width, size.height,
                        cornerRadiusPx, cornerRadiusPx, paint
                    )
                }
            }
        }
    }
}