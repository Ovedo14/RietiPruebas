package com.example.rietiapp.model.datos

data class SeguimientoUiState(
    val folioInput: String = "",
    val isLoading: Boolean = false,
    val reporteEncontrado: ConsultaReporteResponse? = null,
    val nivelProgreso: Int = 1,
    val descripcionEstatus: String = "",
    val error: String? = null
)