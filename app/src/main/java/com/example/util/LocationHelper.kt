package com.example.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Locale

object LocationHelper {
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocationAndCity(context: Context): Pair<Location?, String?> {
        return withContext(Dispatchers.IO) {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                val cancellationTokenSource = CancellationTokenSource()
                
                val location = fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    cancellationTokenSource.token
                ).await()

                if (location != null) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    val city = addresses?.firstOrNull()?.locality ?: addresses?.firstOrNull()?.subAdminArea
                    Pair(location, city)
                } else {
                    Pair(null, null)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Pair(null, null)
            }
        }
    }
}
