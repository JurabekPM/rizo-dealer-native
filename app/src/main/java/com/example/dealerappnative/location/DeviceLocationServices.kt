package com.example.dealerappnative.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.core.os.CancellationSignal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume

internal fun hasLocationPermission(context: Context): Boolean =
    listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).any {
        ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }

@SuppressLint("MissingPermission")
@Suppress("DEPRECATION")
internal suspend fun findCurrentLocation(context: Context): Location? {
    if (!hasLocationPermission(context)) return null
    val manager = context.getSystemService(LocationManager::class.java)
    val providers = listOf("fused", LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER)
        .filter { runCatching { manager.isProviderEnabled(it) }.getOrDefault(false) }
    if (providers.isEmpty()) return null
    return withTimeoutOrNull(15_000) {
        suspendCancellableCoroutine { continuation ->
            val signals = providers.map { CancellationSignal() }
            var remaining = providers.size
            continuation.invokeOnCancellation { signals.forEach { it.cancel() } }
            providers.forEachIndexed { index, provider ->
                try {
                    LocationManagerCompat.getCurrentLocation(manager, provider, signals[index],
                        ContextCompat.getMainExecutor(context)) { location ->
                        if (continuation.isActive) {
                            remaining--
                            if (location != null || remaining == 0) {
                                continuation.resume(location)
                                signals.forEach { it.cancel() }
                            }
                        }
                    }
                } catch (_: Exception) {
                    remaining--
                    if (remaining == 0 && continuation.isActive) continuation.resume(null)
                }
            }
        }
    }
}

/** Uses Android's geocoding service; cancellation prevents stale responses replacing a newer point. */
@Suppress("DEPRECATION")
internal suspend fun findAddress(context: Context, latitude: Double, longitude: Double): String? {
    if (!Geocoder.isPresent()) return null
    val geocoder = Geocoder(context, Locale("ru"))
    val addresses = withTimeoutOrNull(8_000) {
        if (Build.VERSION.SDK_INT >= 33) {
            suspendCancellableCoroutine { continuation ->
                try {
                    geocoder.getFromLocation(latitude, longitude, 1, object : Geocoder.GeocodeListener {
                        override fun onGeocode(addresses: MutableList<Address>) {
                            if (continuation.isActive) continuation.resume(addresses)
                        }
                        override fun onError(errorMessage: String?) {
                            if (continuation.isActive) continuation.resume(emptyList())
                        }
                    })
                } catch (_: Exception) {
                    if (continuation.isActive) continuation.resume(emptyList())
                }
            }
        } else {
            withContext(Dispatchers.IO) {
                runCatching { geocoder.getFromLocation(latitude, longitude, 1).orEmpty() }.getOrDefault(emptyList())
            }
        }
    }
    val address = addresses?.firstOrNull() ?: return null
    val street = listOfNotNull(address.thoroughfare, address.subThoroughfare).joinToString(", ")
    val displayAddress = if (street.isNotBlank()) listOfNotNull(street, address.locality).distinct().joinToString(", ")
        else address.getAddressLine(0).orEmpty()
    return compactStoreAddress(displayAddress).takeIf { it.isNotBlank() }
}
