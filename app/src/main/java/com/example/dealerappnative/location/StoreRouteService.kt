package com.example.dealerappnative.location

import com.example.dealerappnative.data.DealerMapDefaults
import com.example.dealerappnative.model.StoreLocation
import com.example.dealerappnative.model.StoreRoute
import com.example.dealerappnative.model.StoreRouteSummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/** Only saved store coordinates are routed; a GPS editing draft is never sent here. */
internal suspend fun loadStoreRoute(location: StoreLocation): StoreRoute? = withContext(Dispatchers.IO) {
    if (!DealerMapDefaults.routesEnabled) return@withContext null
    val start = "${DealerMapDefaults.longitude},${DealerMapDefaults.latitude}"
    val end = "${location.longitude},${location.latitude}"
    val connection = URL("https://router.project-osrm.org/route/v1/driving/$start;$end?overview=full&geometries=geojson&steps=false")
        .openConnection() as HttpURLConnection
    try {
        connection.connectTimeout = 12_000
        connection.readTimeout = 12_000
        connection.setRequestProperty("User-Agent", "DealerUiPrototype/1.0")
        val result = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
        if (result.optString("code") != "Ok") return@withContext null
        val route = result.getJSONArray("routes").getJSONObject(0)
        val distance = route.getDouble("distance")
        val duration = route.getDouble("duration")
        if (!distance.isFinite() || !duration.isFinite() || distance < 0 || duration < 0) return@withContext null
        StoreRoute(StoreRouteSummary(distance, duration), route.getJSONObject("geometry").toString())
    } catch (_: Exception) { null }
    finally { connection.disconnect() }
}
