package com.example.dealerappnative.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.dealerappnative.model.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/** Local prototype settings shared by administrator and agent; existing documents keep their label snapshot. */
internal class ReturnReasonStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences("return_reason_settings", Context.MODE_PRIVATE)
    var reasons by mutableStateOf(load())
        private set

    private fun load(): List<ReturnReason> = runCatching {
        val array = JSONArray(preferences.getString("reasons", "[]"))
        val saved = (0 until array.length()).map { array.getJSONObject(it).let { item ->
            ReturnReason(item.getString("id"), item.getString("label"), item.optBoolean("system"), item.optBoolean("enabled", true))
        } }
        systemReturnReasons.map { preset -> saved.find { it.id == preset.id } ?: preset } + saved.filterNot { it.system }
    }.getOrDefault(systemReturnReasons)

    fun add(label: String): String? {
        returnReasonError(label, reasons)?.let { return it }
        update(reasons + ReturnReason(UUID.randomUUID().toString(), label.trim(), false))
        return null
    }
    fun rename(id: String, label: String): String? {
        returnReasonError(label, reasons, id)?.let { return it }
        update(reasons.map { if (it.id == id && !it.system) it.copy(label = label.trim()) else it })
        return null
    }
    fun setEnabled(id: String, enabled: Boolean) = update(reasons.map { if (it.id == id) it.copy(enabled = enabled) else it })

    private fun update(values: List<ReturnReason>) {
        val array = JSONArray()
        values.forEach { array.put(JSONObject().put("id", it.id).put("label", it.label).put("system", it.system).put("enabled", it.enabled)) }
        preferences.edit().putString("reasons", array.toString()).apply()
        reasons = values
    }
}
