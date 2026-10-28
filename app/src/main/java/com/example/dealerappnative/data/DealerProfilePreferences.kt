package com.example.dealerappnative.data

import android.content.Context
import com.example.dealerappnative.model.AdminPersonalProfile
import com.example.dealerappnative.model.normalizeAdminPhone
import com.example.dealerappnative.model.DealerProfile
import com.example.dealerappnative.model.DealerWorkingDay
import com.example.dealerappnative.model.validationError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/** Separate namespace: never writes store locations, Agent profile or visit preferences. */
internal class DealerProfilePreferences(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences("administrator_dealer_profile", Context.MODE_PRIVATE)
    fun readAdministrator(): AdminPersonalProfile = decodeAdminPersonalProfile(preferences.getString("administrator", null))
    suspend fun saveProfiles(dealer: DealerProfile, person: AdminPersonalProfile): Boolean = withContext(Dispatchers.IO) {
        if (dealer.validationError() != null || person.validationError() != null) return@withContext false
        preferences.edit().putString("profile", encodeDealerProfile(dealer.copy(name = dealer.name.trim(), address = dealer.address.trim(),
            region = dealer.region.trim(), district = dealer.district.trim())))
            .putString("administrator", encodeAdminPersonalProfile(person.copy(name = person.name.trim(), phone = normalizeAdminPhone(person.phone),
                region = person.region.trim(), district = person.district.trim()))).commit()
    }
    fun read(): DealerProfile = decodeDealerProfile(preferences.getString("profile", null))
    suspend fun save(profile: DealerProfile): Boolean = withContext(Dispatchers.IO) {
        if (profile.validationError() != null) return@withContext false
        preferences.edit().putString("profile", encodeDealerProfile(profile.copy(
            name = profile.name.trim(), address = profile.address.trim(), region = profile.region.trim(), district = profile.district.trim()
        ))).commit()
    }
}

internal fun encodeDealerProfile(profile: DealerProfile): String = with(profile) {
    JSONObject().put("taxId", taxId).put("name", name).put("interaction", interaction).put("activity", activity)
        .put("currency", currency).put("tariff", tariff).put("country", country).put("region", region)
        .put("district", district).put("address", address).put("latitude", latitude).put("longitude", longitude)
        .put("logoUri", logoUri).put("photoUri", photoUri).put("qrUri", qrUri).put("pricesVisible", pricesVisible)
        .put("days", JSONArray().apply { days.forEach { day -> put(JSONObject().put("enabled", day.enabled)
            .put("opensAt", day.opensAt).put("closesAt", day.closesAt)) } }).toString()
}

internal fun decodeDealerProfile(raw: String?): DealerProfile {
    val defaults = DealerProfile()
    return runCatching {
        val json = JSONObject(raw ?: return defaults)
        defaults.copy(taxId = json.optString("taxId"), name = json.optString("name", defaults.name),
            interaction = json.optString("interaction", defaults.interaction), activity = json.optString("activity", defaults.activity),
            currency = json.optString("currency", defaults.currency), tariff = json.optString("tariff", defaults.tariff),
            country = json.optString("country", defaults.country), region = json.optString("region", defaults.region),
            district = json.optString("district", defaults.district), address = json.optString("address", defaults.address),
            latitude = json.optDouble("latitude", defaults.latitude), longitude = json.optDouble("longitude", defaults.longitude),
            logoUri = json.optString("logoUri"), photoUri = json.optString("photoUri"), qrUri = json.optString("qrUri"),
            pricesVisible = json.optBoolean("pricesVisible"), days = List(7) { index ->
                val day = json.optJSONArray("days")?.optJSONObject(index)
                DealerWorkingDay(day?.optBoolean("enabled", true) ?: true,
                    (day?.optInt("opensAt", 540) ?: 540).coerceIn(0, 1439),
                    (day?.optInt("closesAt", 1200) ?: 1200).coerceIn(0, 1439))
            })
    }.getOrDefault(defaults)
}
