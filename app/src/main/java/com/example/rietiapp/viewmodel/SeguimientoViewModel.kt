package com.example.rietiapp.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rietiapp.model.datos.SeguimientoUiState
import com.example.rietiapp.model.repository.ReporteRepository
import com.example.rietiapp.view.screens.seguimientoReportes.obtenerDescripcionEstado
import kotlinx.coroutines.launch

private const val TAG = "SEGUIMIENTO_DEBUG"

class SeguimientoViewModel(
    private val repository: ReporteRepository = ReporteRepository()
) : ViewModel() {

    var uiState by mutableStateOf(SeguimientoUiState())
        private set

    fun onFolioInputChange(nuevoFolio: String) {
        uiState = uiState.copy(folioInput = nuevoFolio, error = null)
    }

    fun buscarReporte(onExito: () -> Unit) {
        val folioLimpio = uiState.folioInput.trim()

        if (folioLimpio.isBlank()) {
            uiState = uiState.copy(error = "Ingresa un número de folio válido")
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, error = null)

            Log.d(TAG, "🔍 Intentando consultar folio: '$folioLimpio'")

            try {
                val response = repository.consultarReportePorFolio(folioLimpio)

                Log.d(TAG, "📡 Código de respuesta HTTP: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    // 🟢 FIX: Extraer el body() de Retrofit y luego acceder a .reporte del wrapper
                    val wrapper = response.body()!!
                    val datos = wrapper.reporte

                    Log.d(TAG, "✅ Datos extraídos: $datos")

                    uiState = uiState.copy(
                        isLoading = false,
                        reporteEncontrado = datos,
                        nivelProgreso = calcularNivelProgreso(datos.estatus),
                        descripcionEstatus = obtenerDescripcionEstado(datos.estatus)
                    )
                    onExito()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "❌ Error en el servidor (${response.code()}): $errorBody")

                    uiState = uiState.copy(
                        isLoading = false,
                        error = "No se encontró ningún reporte con el folio ingresado."
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "💥 Excepción de red o parseo: ${e.message}", e)

                uiState = uiState.copy(
                    isLoading = false,
                    error = "Error de conexión. Verifica tu acceso a internet."
                )
            }
        }
    }

    private fun calcularNivelProgreso(estado: String): Int {
        return when (estado.lowercase().trim()) {
            "registrado", "recibido" -> 1
            "en revisión", "en revision" -> 2
            "en seguimiento", "reincidente" -> 3
            "canalizado" -> 4
            "concluido", "archivado" -> 5
            else -> 1
        }
    }
}