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

/**
 * ViewModel encargado de la lógica de negocio y gestión de estado UI durante la creación de reportes.
 * Administra el catálogo de municipios, geocodificación inversa, estados de los pasos e integración
 * con el repositorio [ReporteRepository] para el envío de formularios y evidencias.
 *
 * @property repository Instancia de [ReporteRepository] para la comunicación con la API.
 */
class ReporteViewModel(
    private val repository: ReporteRepository = ReporteRepository()
) : ViewModel() {

    /**
     * Estado reactivo de la UI para la pantalla del formulario de reporte.
     */
    var uiState by mutableStateOf(ReporteUiState())
        private set

    private var catalogoMunicipios: List<Municipio> = emptyList()

    init {
        cargarCatalogoMunicipios()
    }

    /**
     * Carga el catálogo de municipios desde el backend de forma asíncrona.
     */
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

    /**
     * Aplica una transformación inmutable sobre el estado de la UI [ReporteUiState].
     *
     * @param transform Función que toma el estado actual y retorna un nuevo estado.
     */
    fun updateState(transform: (ReporteUiState) -> ReporteUiState) {
        uiState = transform(uiState)
    }

    /**
     * Actualiza la ubicación seleccionada por el usuario y determina el municipio correspondiente.
     *
     * @param latitud Coordenada de latitud.
     * @param longitud Coordenada de longitud.
     * @param direccionCompleta Dirección formateada provista por el servicio Geocoder.
     * @param nombreMunicipioDetectado Nombre del municipio identificado por la ubicación.
     */
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

    /**
     * Busca la id del municipio en el catálogo comparando cadenas de texto normalizadas.
     *
     * @param nombreDetectado Nombre del municipio a buscar.
     * @return Identificador [Int] del municipio coincidente, o `1` por defecto si no se encuentra.
     */
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

    /**
     * Remueve acentos, diacríticos y convierte a minúsculas una cadena de texto para facilitar comparaciones.
     *
     * @param texto Cadena original.
     * @return Cadena de texto normalizada.
     */
    private fun normalizarTexto(texto: String): String {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .lowercase()
            .trim()
    }

    /**
     * Mapea la cadena de la actividad observada a su correspondiente ID numérico del catálogo.
     *
     * @param actividadNombre Nombre descriptivo de la actividad.
     * @return ID numérico asignado a la actividad en el catálogo.
     */
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

    /**
     * Procesa y envía el reporte completo junto con sus archivos de evidencia fotográfica si existen.
     *
     * @param context Contexto necesario para acceder a los recursos de archivos.
     * @param onSuccess Callback ejecutado al completar exitosamente la transacción.
     */
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

    /**
     * Construye el objeto [ReporteRequest] a partir de los valores capturados en [uiState].
     *
     * @return Instancia configurada de [ReporteRequest].
     */
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
