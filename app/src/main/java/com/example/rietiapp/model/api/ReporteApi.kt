package com.example.rietiapp.model.api

import com.example.rietiapp.model.datos.ConfirmarEvidenciaRequest
import com.example.rietiapp.model.datos.ConfirmarEvidenciaResponse
import com.example.rietiapp.model.datos.ConsultaReporteResponse
import com.example.rietiapp.model.datos.ConsultaReporteWrapper
import com.example.rietiapp.model.datos.Municipio
import com.example.rietiapp.model.datos.PresignedUrlRequest
import com.example.rietiapp.model.datos.PresignedUrlResponse
import com.example.rietiapp.model.datos.ReporteRequest
import com.example.rietiapp.model.datos.ReporteResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface ReporteApi {

    @POST("api/v1/reportes")
    suspend fun crearReporte(
        @Body reporte: ReporteRequest
    ): Response<ReporteResponse>

    // Paso 2.1: Obtener presigned URL
    @POST("api/v1/evidencias/presigned-url")
    suspend fun obtenerPresignedUrl(@Body request: PresignedUrlRequest): Response<PresignedUrlResponse>

    // Paso 2.2: Subida directa a S3 (Usa @Url porque la URL proviene dinámicamente de S3)
    @PUT
    suspend fun subirArchivoS3(
        @Url uploadUrl: String,
        @Header("Content-Type") contentType: String = "image/jpeg",
        @Body archivoBinario: RequestBody
    ): Response<Unit>

    // Paso 2.3: Confirmar evidencia en MySQL
    @POST("api/v1/evidencias/confirmar")
    suspend fun confirmarEvidencia(@Body request: ConfirmarEvidenciaRequest): Response<ConfirmarEvidenciaResponse>

    @GET("api/v1/municipios")
    suspend fun obtenerMunicipios(): Response<List<Municipio>>

    @GET("api/v1/reportes/{folio}")
    suspend fun consultarReporte(
        @Path("folio") folio: String
    ): Response<ConsultaReporteWrapper>
}