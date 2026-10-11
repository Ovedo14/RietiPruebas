package com.example.rietiapp.model.repository

import android.content.Context
import android.net.Uri
import com.example.rietiapp.model.api.RetrofitInstance
import com.example.rietiapp.model.datos.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

/**
 * Repositorio central que abstrae el acceso a fuentes de datos remotas para el envío de reportes,
 * sincronización de municipios y gestión de evidencias fotográficas en almacenamiento de S3.
 */
class ReporteRepository {

    private val api = RetrofitInstance.api

    /**
     * Envía la solicitud para registrar un reporte en el backend.
     *
     * @param request Datos del reporte estructurados para la API.
     * @return [Response] con la respuesta enviada por el servidor backend.
     */
    suspend fun crearReporte(request: ReporteRequest) = api.crearReporte(request)

    /**
     * Ejecuta el flujo de tres pasos para subir una imagen de evidencia adjunta:
     * 1. Solicita la URL presignada para S3.
     * 2. Realiza la carga de datos binarios directamente al bucket de S3.
     * 3. Confirma el registro de la evidencia en la base de datos MySQL del servidor.
     *
     * @param context Contexto de la aplicación para resolver el [Uri] del contenido.
     * @param folio Folio asignado al reporte.
     * @param uri [Uri] del archivo local a subir.
     * @return `true` si todos los pasos se completan con éxito, `false` en caso contrario.
     */
    suspend fun subirFotoEvidencia(context: Context, folio: String, uri: Uri): Boolean {
        try {
            // Convertir la Uri a ByteArray para enviarla a S3
            val inputStream = context.contentResolver.openInputStream(uri) ?: return false
            val bytes = inputStream.readBytes()
            inputStream.close()

            val requestBody = bytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
            val nombreArchivo = "foto_${System.currentTimeMillis()}.jpg"

            // 1. Obtener URL firmada
            val presignedRes = api.obtenerPresignedUrl(
                PresignedUrlRequest(folio = folio, nombreArchivo = nombreArchivo)
            )
            if (!presignedRes.isSuccessful || presignedRes.body() == null) return false
            val presignedData = presignedRes.body()!!

            // 2. PUT directo a S3
            val s3Res = api.subirArchivoS3(
                uploadUrl = presignedData.uploadUrl,
                archivoBinario = requestBody
            )
            if (!s3Res.isSuccessful) return false

            // 3. Confirmar evidencia registrada
            val confirmRes = api.confirmarEvidencia(
                ConfirmarEvidenciaRequest(folio = folio, key = presignedData.key)
            )
            return confirmRes.isSuccessful

        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * Consulta el catálogo global de municipios.
     *
     * @return [Response] con la lista de municipios disponibles.
     */
    suspend fun obtenerMunicipios() = api.obtenerMunicipios()

    /**
     * Obtiene el reporte asociado a un folio específico para dar seguimiento.
     *
     * @param folio Folio único del reporte.
     * @return [Response] envuelto en [ConsultaReporteWrapper].
     */
    suspend fun consultarReportePorFolio(folio: String): Response<ConsultaReporteWrapper> {
        return api.consultarReporte(folio)
    }
}
