package com.example.dealerappnative.data

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.dealerappnative.model.*
import org.json.JSONArray
import org.json.JSONObject

internal class VisitReturnStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences("visit_returns", Context.MODE_PRIVATE)
    val reasonSettings = ReturnReasonStore(context)
    val records = mutableStateMapOf<String, VisitReturnRecord>()
    init {
        preferences.all.forEach { (id, raw) -> runCatching {
            val json = JSONObject(raw as String)
            val items = json.getJSONArray("items")
            records[id] = VisitReturnRecord(json.getString("documentId"), json.getLong("createdAt"),
                (0 until items.length()).map { index -> items.getJSONObject(index).let {
                    VisitReturnItem(it.getString("name"), it.getLong("price"), it.getInt("quantity"))
                } }, json.optString("comment"), json.optString("reasonId").takeIf { it.isNotBlank() }, json.optString("reasonLabel"))
        } }
    }
    fun save(storeId: String, record: VisitReturnRecord) {
        val items = JSONArray()
        record.items.forEach { items.put(JSONObject().put("name", it.name).put("price", it.price).put("quantity", it.quantity)) }
        preferences.edit().putString(storeId, JSONObject().put("documentId", record.documentId)
            .put("createdAt", record.createdAt).put("items", items).put("comment", record.comment)
            .put("reasonId", record.reasonId.orEmpty()).put("reasonLabel", record.reasonLabel).toString()).apply()
        records[storeId] = record
    }
    fun enrich(visit: VisitStore): VisitStore = records[visit.id]?.let { record ->
        visit.copy(results = (visit.results + VisitResult.Return).distinct(),
            status = if (visit.status == VisitStatus.NotVisited) VisitStatus.Active else visit.status,
            details = visit.details.copy(returns = "${record.items.sumOf { it.quantity }} шт · ${historyMoney(record.total)}"))
    } ?: visit
}

internal val LocalVisitReturns = staticCompositionLocalOf<VisitReturnStore> { error("VisitReturnsProvider is required") }

@Composable
internal fun VisitReturnsProvider(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val returns = remember(context) { VisitReturnStore(context) }
    CompositionLocalProvider(LocalVisitReturns provides returns, content = content)
}
