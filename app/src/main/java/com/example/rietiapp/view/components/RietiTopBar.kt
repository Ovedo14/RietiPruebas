package com.example.rietiapp.view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RietiTopBar(
    mostrarAtras: Boolean,
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = { 
            // Título vacío para que luzca minimalista como en la imagen
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent, // Fondo transparente
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        navigationIcon = {
            if (mostrarAtras) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar"
                    )
                }
            }
        },
        actions = {
            if (mostrarAtras) {
                // Icono de 3 puntos (MoreVert) de ejemplo para igualar la imagen
                IconButton(onClick = { /* TODO: Opciones */ }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Opciones"
                    )
                }
            } else {
                // Icono de perfil de ejemplo para la pantalla principal
                IconButton(onClick = { /* TODO: Perfil */ }) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Perfil"
                    )
                }
            }
        }
    )
}