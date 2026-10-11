package com.example.rietiapp.model.repository

import android.content.Context
import android.net.Uri
import com.example.rietiapp.model.api.RetrofitInstance
import com.example.rietiapp.model.datos.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class ReporteRepository {

    private val api = RetrofitInstance.api

    suspend fun crearReporte(request: ReporteRequest) = api.crearReporte(request)

    /**
     * Sigue los 3 pasos de subida de evidencias para un archivo Uri dado
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

    suspend fun obtenerMunicipios() = api.obtenerMunicipios()

    suspend fun consultarReportePorFolio(folio: String): Response<ConsultaReporteWrapper> {
        return api.consultarReporte(folio)
    }
}