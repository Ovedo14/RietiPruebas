package com.example.rietiapp.model.datos

import org.osmdroid.util.GeoPoint

data class UbicacionData(
    val geoPoint: GeoPoint? = null,
    val direccion: String = "",
    val municipio: String = "",
    val estaCargandoDireccion: Boolean = false
) {
    val latitud: Double
        get() = geoPoint?.latitude ?: 0.0

    val longitud: Double
        get() = geoPoint?.longitude ?: 0.0
}