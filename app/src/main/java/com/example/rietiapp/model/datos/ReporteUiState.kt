package com.example.rietiapp.model.datos

import android.net.Uri

/**
 * Modelo inmutable de estado UI que contiene la información completa del formulario de reporte de incidencias.
 *
 * @property modalidad Tipo de reporte ("ANONIMO" o "SEGUIMIENTO").
 * @property correo Correo de contacto opcional para seguimiento.
 * @property idMunicipio Identificador numérico del municipio asignado.
 * @property idCatalogoActividad Identificador numérico de la actividad seleccionada.
 * @property cantidadSeleccionada Cantidad observada de niñas, niños o adolescentes.
 * @property edadSeleccionada Rango de edad observado.
 * @property generoSeleccionado Género observado.
 * @property actividadSeleccionada Descripción de la actividad realizada.
 * @property hora Hora aproximada de los hechos observados.
 * @property descripcion Descripción detallada introducida por el usuario.
 * @property riesgo Indicación de situación de riesgo ("Sí", "No", "No se").
 * @property latitud Coordenada de latitud seleccionada en el mapa.
 * @property longitud Coordenada de longitud seleccionada en el mapa.
 * @property direccion Dirección textual correspondiente a las coordenadas GPS.
 * @property calle Calle principal de la ubicación.
 * @property colonia Colonia o vecindario.
 * @property cp Código postal.
 * @property referencias Referencias adicionales del lugar.
 * @property aceptoAviso Indicador de aceptación del aviso de privacidad.
 * @property folio Folio asignado al reporte por la API.
 * @property estatus Estado del reporte devuelto por el servidor.
 * @property fotosUris Lista de [Uri]s locales de fotos adjuntas como evidencia.
 * @property isLoading Muestra el estado de carga al enviar datos a la API.
 * @property error Mensaje de error para mostrar en la interfaz si ocurre un fallo.
 * @property isSending Indicador de estado de envío en proceso.
 * @property folioGenerado Folio devuelto tras la creación exitosa en el backend.
 */
data class ReporteUiState(
    val modalidad: String = "ANONIMO",
    val correo: String = "",

    val idMunicipio: Int = 1,
    val idCatalogoActividad: Int = 0,

    val cantidadSeleccionada: String = "",
    val edadSeleccionada: String = "",
    val generoSeleccionado: String = "",
    val actividadSeleccionada: String = "",

    val hora: String = "",
    val descripcion: String = "",
    val riesgo: String = "",

    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val direccion: String = "",
    val calle: String = "",
    val colonia: String = "",
    val cp: String = "",
    val referencias: String = "",

    val aceptoAviso: Boolean = false,

    val folio: String = "",
    val estatus: String = "",
    val fotosUris: List<Uri> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,

    val isSending: Boolean = false,
    val folioGenerado: String = ""
)
