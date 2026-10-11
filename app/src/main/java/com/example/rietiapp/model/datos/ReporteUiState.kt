package com.example.rietiapp.model.datos

import android.net.Uri

data class ReporteUiState(
    // Tipo de reporte
    val modalidad: String = "ANONIMO",
    val correo: String = "",

    // Catálogos
    val idMunicipio: Int = 1,
    val idCatalogoActividad: Int = 0,

    // 🟢 CAMPOS UI: Todos vacíos para mostrar placeholders en la pantalla
    val cantidadSeleccionada: String = "",     // Muestra "Selecciona cantidad"
    val edadSeleccionada: String = "",         // Muestra "Selecciona edad"
    val generoSeleccionado: String = "",       // Muestra "Selecciona género"
    val actividadSeleccionada: String = "",    // OBLIGATORIO: Vacío para forzar la selección

    val hora: String = "",
    val descripcion: String = "",
    val riesgo: String = "",                   // Sin opción marcada por defecto

    // Ubicación
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val direccion: String = "",
    val calle: String = "",
    val colonia: String = "",
    val cp: String = "",
    val referencias: String = "",

    // Aviso de privacidad
    val aceptoAviso: Boolean = false,          // Debe iniciar desmarcado

    // Respuesta backend y estado UI
    val folio: String = "",
    val estatus: String = "",
    val fotosUris: List<Uri> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)