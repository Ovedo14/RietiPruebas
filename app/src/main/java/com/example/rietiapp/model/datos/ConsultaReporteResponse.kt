package com.example.rietiapp.model.datos

import com.google.gson.annotations.SerializedName

data class ConsultaReporteWrapper(
    @SerializedName("reporte") val reporte: ConsultaReporteResponse
)

data class ConsultaReporteResponse(
    @SerializedName("folio") val folio: String = "",
    @SerializedName("estatus") val estatus: String = "",
    @SerializedName("fecha_registro") val fechaRegistro: String? = null,
    @SerializedName("fecha_ultima_atencion") val fechaUltimaAtencion: String? = null,
    @SerializedName("municipio") val municipio: String = ""
) {
    val estado: String
        get() = estatus

    val fechaFormateada: String
        get() {
            if (fechaRegistro.isNullOrBlank() || fechaRegistro.length < 10) return "Sin fecha"

            val partes = fechaRegistro.take(10).split("-") // Separa AAAA-MM-DD
            return if (partes.size == 3) "${partes[2]}/${partes[1]}/${partes[0]}" else fechaRegistro.take(10)
        }
}