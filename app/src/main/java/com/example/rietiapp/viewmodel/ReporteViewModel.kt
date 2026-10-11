package com.example.rietiapp.viewmodel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rietiapp.model.datos.Municipio
import com.example.rietiapp.model.datos.ReporteRequest
import com.example.rietiapp.model.datos.ReporteUiState
import com.example.rietiapp.model.repository.ReporteRepository
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.Normalizer

class ReporteViewModel(
    private val repository: ReporteRepository = ReporteRepository()
) : ViewModel() {

    var uiState by mutableStateOf(ReporteUiState())
        private set

    private var catalogoMunicipios: List<Municipio> = emptyList()

    init {
        cargarCatalogoMunicipios()
    }

    private fun cargarCatalogoMunicipios() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.obtenerMunicipios()
                if (response.isSuccessful) {
                    catalogoMunicipios = response.body() ?: emptyList()
                    Log.d("REPORTE_DEBUG", "Catálogo cargado: ${catalogoMunicipios.size} municipios.")

                    if (uiState.latitud != 0.0 && uiState.longitud != 0.0 && uiState.direccion.isNotBlank()) {
                        val municipioLimpio = normalizarTexto(uiState.direccion)
                        val idEncontrado = asociarMunicipioPorNombre(municipioLimpio)
                        updateState { it.copy(idMunicipio = idEncontrado) }
                    }
                } else {
                    Log.e("REPORTE_DEBUG", "Error cargando municipios: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("REPORTE_DEBUG", "Excepción cargando municipios: ${e.message}")
            }
        }
    }

    fun updateState(transform: (ReporteUiState) -> ReporteUiState) {
        uiState = transform(uiState)
    }

    fun resolverYActualizarUbicacion(
        latitud: Double,
        longitud: Double,
        direccionCompleta: String,
        nombreMunicipioDetectado: String
    ) {
        Log.d("REPORTE_DEBUG", "Ubicación detectada por Geocoder -> Municipio: '$nombreMunicipioDetectado' | Dirección: '$direccionCompleta'")

        val idDetectado = asociarMunicipioPorNombre(nombreMunicipioDetectado)

        updateState {
            it.copy(
                latitud = latitud,
                longitud = longitud,
                direccion = direccionCompleta,
                idMunicipio = idDetectado
            )
        }
    }

    private fun asociarMunicipioPorNombre(nombreDetectado: String): Int {
        if (catalogoMunicipios.isEmpty()) {
            Log.w("REPORTE_DEBUG", "El catálogo de municipios aún no se ha cargado de la API.")
            return 1
        }

        val nombreLimpio = normalizarTexto(nombreDetectado)

        val municipioEncontrado = catalogoMunicipios.find { municipio ->
            val nombreCat = normalizarTexto(municipio.nombre)
            nombreCat.isNotBlank() && (
                    nombreCat == nombreLimpio ||
                            nombreCat.contains(nombreLimpio) ||
                            nombreLimpio.contains(nombreCat)
                    )
        }

        return if (municipioEncontrado != null) {
            Log.d("REPORTE_DEBUG", "¡Coincidencia encontrada! Map: '$nombreDetectado' -> DB: '${municipioEncontrado.nombre}' (ID: ${municipioEncontrado.id})")
            municipioEncontrado.id
        } else {
            Log.e("REPORTE_DEBUG", "No hubo coincidencia para '$nombreDetectado'. Asignando ID 1 por defecto.")
            1
        }
    }

    private fun normalizarTexto(texto: String): String {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .lowercase()
            .trim()
    }

    private fun obtenerIdActividad(actividadNombre: String): Int {
        val actividades = listOf(
            "Venta de productos en via publica",
            "Mendicidad",
            "Trabajo en comercio local",
            "Construccion",
            "Limpieza de parabrisas",
            "Actividades agricolas",
            "Otra"
        )
        val index = actividades.indexOf(actividadNombre)
        return if (index >= 0) index + 1 else 1
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun enviarReporteConEvidencias(context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                uiState = uiState.copy(isLoading = true, error = null)
            }

            try {
                val request = construirRequest()
                val jsonEnviado = Gson().toJson(request)
                Log.d("REPORTE_DEBUG", "Payload enviado: $jsonEnviado")

                val respuestaReporte = repository.crearReporte(request)

                if (respuestaReporte.isSuccessful && respuestaReporte.body() != null) {
                    val body = respuestaReporte.body()!!
                    val folioGenerado = body.folio
                    Log.d("REPORTE_DEBUG", "¡Éxito! Folio generado: $folioGenerado")

                    withContext(Dispatchers.Main) {
                        uiState = uiState.copy(
                            folio = folioGenerado,
                            estatus = body.estatus
                        )
                    }

                    if (uiState.fotosUris.isNotEmpty()) {
                        for (fotoUri in uiState.fotosUris) {
                            repository.subirFotoEvidencia(context, folioGenerado, fotoUri)
                        }
                    }

                    withContext(Dispatchers.Main) {
                        uiState = uiState.copy(isLoading = false)
                        onSuccess()
                    }

                } else {
                    val errorBody = respuestaReporte.errorBody()?.string() ?: ""
                    Log.e("REPORTE_DEBUG", "Código HTTP: ${respuestaReporte.code()}")
                    Log.e("REPORTE_DEBUG", "Respuesta de error Backend: $errorBody")

                    withContext(Dispatchers.Main) {
                        uiState = uiState.copy(
                            isLoading = false,
                            error = "Error ${respuestaReporte.code()}: $errorBody"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("REPORTE_DEBUG", "Excepción de red: ${e.localizedMessage}", e)
                withContext(Dispatchers.Main) {
                    uiState = uiState.copy(
                        isLoading = false,
                        error = e.localizedMessage ?: "Error de conexión"
                    )
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun construirRequest(): ReporteRequest {
        var horaFormateada = uiState.hora.ifBlank {
            java.time.LocalTime.now().withNano(0).toString()
        }
        if (horaFormateada.length == 5) {
            horaFormateada += ":00"
        }

        return ReporteRequest(
            idMunicipio = if (uiState.idMunicipio > 0) uiState.idMunicipio else 1,
            idCatalogoActividad = obtenerIdActividad(uiState.actividadSeleccionada),
            modalidad = if (uiState.modalidad.isNotBlank()) uiState.modalidad else "ANONIMO",
            correo_contacto = if (uiState.modalidad == "ANONIMO") null else uiState.correo.ifBlank { null },

            // 🟢 FALLBACKS SEGUROS PARA EL BACKEND
            num_menores = uiState.cantidadSeleccionada.toIntOrNull() ?: 1,
            rango_edad = uiState.edadSeleccionada.ifBlank { "No especificado" },
            genero_observado = uiState.generoSeleccionado.ifBlank { "No especificado" },
            hora_observada = horaFormateada,

            descripcion = uiState.descripcion.trim(),
            situacion_riesgo = uiState.riesgo == "Sí",

            latitud = uiState.latitud,
            longitud = uiState.longitud,

            calle = uiState.calle.ifBlank { "No especificada" },
            colonia = uiState.colonia.ifBlank { "No especificada" },
            cp = uiState.cp.ifBlank { null },
            referencias = uiState.referencias.ifBlank { null },

            acepto_aviso = true
        )
    }
}