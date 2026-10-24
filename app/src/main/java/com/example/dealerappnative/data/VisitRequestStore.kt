package com.example.dealerappnative.data

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import com.example.dealerappnative.model.*
import org.json.JSONArray
import org.json.JSONObject

/** One locally saved request per prototype visit, independent of return storage. */
internal class VisitRequestStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences("agent_visit_requests", Context.MODE_PRIVATE)
    val records = mutableStateMapOf<String, VisitRequestRecord>()

    init {
        preferences.all.forEach { (id, raw) -> runCatching {
            val json = JSONObject(raw as String)
            val lines = json.getJSONArray("items")
            records[id] = VisitRequestRecord(json.getString("documentId"), json.getLong("createdAt"),
                (0 until lines.length()).map { index -> lines.getJSONObject(index).let {
                    VisitRequestItem(it.getString("productId"), it.getString("name"), it.getLong("price"), it.getInt("quantity"))
                } })
        } }
    }

    fun save(visitId: String, record: VisitRequestRecord) {
        val lines = JSONArray()
        record.items.forEach { lines.put(JSONObject().put("productId", it.productId).put("name", it.name)
            .put("price", it.price).put("quantity", it.quantity)) }
        preferences.edit().putString(visitId, JSONObject().put("documentId", record.documentId)
            .put("createdAt", record.createdAt).put("items", lines).toString()).apply()
        records[visitId] = record
    }

    fun enrich(visit: VisitStore): VisitStore = records[visit.id]?.let { record ->
        visit.copy(details = visit.details.copy(request = "${record.items.size} товаров · ${historyMoney(record.total)}"))
    } ?: visit
}
