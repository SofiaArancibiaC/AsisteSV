package com.js.smartvolt.asiste.model.data.local

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

data class LecturaUbicacion(
    val latitud: Double,
    val longitud: Double,
    val precisionMetros: Float,
    val simulada: Boolean
)

sealed interface ResultadoUbicacion {
    data class Ok(val lectura: LecturaUbicacion) : ResultadoUbicacion
    data object SinPermiso : ResultadoUbicacion
    data object GpsApagado : ResultadoUbicacion
    data class Error(val mensaje: String) : ResultadoUbicacion
}

class UbicacionProvider(context: Context) {

    private val appContext = context.applicationContext
    private val client = LocationServices.getFusedLocationProviderClient(appContext)

    // Solo cuenta el permiso PRECISO
    private fun tienePermiso(): Boolean =
        ContextCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED

    private fun gpsActivo(): Boolean {
        val lm = appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(lm)
    }

    @SuppressLint("MissingPermission")
    suspend fun obtener(): ResultadoUbicacion {
        if (!tienePermiso()) return ResultadoUbicacion.SinPermiso
        if (!gpsActivo()) return ResultadoUbicacion.GpsApagado

        return try {
            val actual = withTimeoutOrNull(12_000) {
                client.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    CancellationTokenSource().token
                ).await()
            }
            // Si no llegó una lectura nueva en 12 s, usa la última conocida
            val loc = actual ?: client.lastLocation.await()

            if (loc == null) {
                ResultadoUbicacion.Error("No se pudo obtener la ubicación. Sal al aire libre e intenta de nuevo.")
            } else {
                val simulada = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    loc.isMock
                } else {
                    @Suppress("DEPRECATION")
                    loc.isFromMockProvider
                }
                ResultadoUbicacion.Ok(
                    LecturaUbicacion(
                        latitud = loc.latitude,
                        longitud = loc.longitude,
                        precisionMetros = loc.accuracy,
                        simulada = simulada
                    )
                )
            }
        } catch (e: Exception) {
            ResultadoUbicacion.Error("Error al leer la ubicación: ${e.message}")
        }
    }
}
