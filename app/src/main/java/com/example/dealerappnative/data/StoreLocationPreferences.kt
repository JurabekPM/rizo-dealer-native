package com.example.dealerappnative.data

import android.content.Context
import com.example.dealerappnative.model.StoreLocation
import com.example.dealerappnative.model.StoreUiModel
import org.json.JSONObject
import com.example.dealerappnative.model.StoreRouteSummary

/** Per-store edits stay on this device; no dealer backend is contacted. */
internal class StoreLocationPreferences(context: Context) {
    private val preferences = context.getSharedPreferences("prototype_store_locations", Context.MODE_PRIVATE)

    fun read(store: StoreUiModel): StoreLocation {
        val original = StoreLocation(store.latitude, store.longitude, store.address)
        return runCatching {
            val json = JSONObject(preferences.getString(store.id, null) ?: return original)
            StoreLocation(json.getDouble("latitude"), json.getDouble("longitude"), json.getString("address"),
                if (json.has("distanceMeters") && json.has("durationSeconds"))
                    StoreRouteSummary(json.getDouble("distanceMeters"), json.getDouble("durationSeconds")) else null)
        }.getOrDefault(original)
    }

    fun save(storeId: String, location: StoreLocation) {
        val json = JSONObject().put("latitude", location.latitude).put("longitude", location.longitude)
            .put("address", location.address)
        location.route?.let { json.put("distanceMeters", it.distanceMeters).put("durationSeconds", it.durationSeconds) }
        preferences.edit().putString(storeId, json.toString()).apply()
    }
}
