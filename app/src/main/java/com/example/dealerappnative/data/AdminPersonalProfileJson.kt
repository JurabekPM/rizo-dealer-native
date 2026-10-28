package com.example.dealerappnative.data

import com.example.dealerappnative.model.AdminPersonalProfile
import org.json.JSONObject

internal fun encodeAdminPersonalProfile(profile: AdminPersonalProfile): String = with(profile) {
    JSONObject().put("name", name).put("phone", phone).put("birthDate", birthDate).put("gender", gender)
        .put("country", country).put("region", region).put("district", district).toString()
}

internal fun decodeAdminPersonalProfile(raw: String?): AdminPersonalProfile {
    val defaults = AdminPersonalProfile()
    return runCatching {
        val json = JSONObject(raw ?: return defaults)
        defaults.copy(name = json.optString("name", defaults.name), phone = json.optString("phone", defaults.phone),
            birthDate = json.optString("birthDate"), gender = json.optString("gender", defaults.gender),
            country = json.optString("country", defaults.country), region = json.optString("region", defaults.region),
            district = json.optString("district", defaults.district))
    }.getOrDefault(defaults)
}
