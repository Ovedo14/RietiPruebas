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

/**
 * Interfaz de la API REST para el envío de reportes y gestión de evidencias en la plataforma RIETI.
 * Define endpoints HTTP administrados mediante Retrofit.
 */
interface ReporteApi {

    /**
     * Envía la solicitud para registrar un nuevo reporte de situación en el backend.
     *
     * @param reporte Objeto de transferencia con los detalles del reporte (ubicación, datos observados, etc.).
     * @return [Response] conteniendo un [ReporteResponse] con el folio asignado y estatus inicial.
     */
    @POST("api/v1/reportes")
    suspend fun crearReporte(
        @Body reporte: ReporteRequest
    ): Response<ReporteResponse>

    /**
     * Solicita una URL firmada de S3 para la carga directa de fotografías de evidencia.
     *
     * @param request Datos del reporte y del archivo a subir.
     * @return [Response] conteniendo [PresignedUrlResponse] con la URL presignada y la clave asignada en S3.
     */
    @POST("api/v1/evidencias/presigned-url")
    suspend fun obtenerPresignedUrl(
        @Body request: PresignedUrlRequest
    ): Response<PresignedUrlResponse>

    /**
     * Realiza una carga binaria directa hacia Amazon S3 utilizando la URL presignada.
     *
     * @param uploadUrl URL presignada proporcionada por el backend.
     * @param contentType Tipo MIME del archivo (por defecto `image/jpeg`).
     * @param archivoBinario Contenido binario del archivo a subir.
     * @return [Response] con código HTTP indicando el resultado del envío en S3.
     */
    @PUT
    suspend fun subirArchivoS3(
        @Url uploadUrl: String,
        @Header("Content-Type") contentType: String = "image/jpeg",
        @Body archivoBinario: RequestBody
    ): Response<Unit>

    /**
     * Notifica al backend que un archivo ha sido subido exitosamente a S3 para asociarlo al folio.
     *
     * @param request Solicitud con el folio del reporte y la clave asignada al archivo.
     * @return [Response] con la respuesta de confirmación.
     */
    @POST("api/v1/evidencias/confirmar")
    suspend fun confirmarEvidencia(
        @Body request: ConfirmarEvidenciaRequest
    ): Response<ConfirmarEvidenciaResponse>

    /**
     * Obtiene el catálogo completo de municipios registrados en el sistema.
     *
     * @return [Response] con la lista de objetos [Municipio].
     */
    @GET("api/v1/municipios")
    suspend fun obtenerMunicipios(): Response<List<Municipio>>

    /**
     * Consulta el estado e información pública de un reporte a través de su folio único.
     *
     * @param folio Folio asignado al reporte durante su creación.
     * @return [Response] conteniendo un [ConsultaReporteWrapper] con los detalles del reporte.
     */
    @GET("api/v1/reportes/{folio}")
    suspend fun consultarReporte(
        @Path("folio") folio: String
    ): Response<ConsultaReporteWrapper>
}
