package com.example.dealerappnative.data

import android.content.Context
import com.example.dealerappnative.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.math.BigDecimal

/** Read-only snapshots. Do not instantiate AgentVisitSessions: its constructor seeds samples. */
internal suspend fun loadStoreStatement(context: Context, store: StoreUiModel,
    now: Long = System.currentTimeMillis()): List<StoreStatementEntry> = withContext(Dispatchers.IO) {
    fun records(name: String): Map<String, String> = context.applicationContext.getSharedPreferences(name, Context.MODE_PRIVATE)
        .all.mapNotNull { (key, value) -> (value as? String)?.let { key to it } }.toMap()
    val links = records("agent_unplanned_visits").mapNotNull { (visitId, raw) -> runCatching {
        JSONObject(raw).optString("storeId").takeIf { it.isNotBlank() }?.let { visitId to it }
    }.getOrNull() }.toMap()
    fun belongs(id: String) = (links[id] ?: id) == store.id
    val entries = mutableListOf<StoreStatementEntry>()
    fun documents(preference: String, kind: StatementKind) {
        records(preference).filterKeys(::belongs).forEach { (visitId, raw) -> runCatching {
            val json = JSONObject(raw)
            val items = json.getJSONArray("items")
            val lines = List(items.length()) { i -> items.getJSONObject(i).let {
                StatementLine(it.getString("name"), it.getInt("quantity"), BigDecimal.valueOf(it.getLong("price")))
            } }
            require(lines.isNotEmpty())
            val amount = lines.fold(BigDecimal.ZERO) { sum, line -> sum + line.price * BigDecimal.valueOf(line.quantity.toLong()) }
            entries += StoreStatementEntry("local-${kind.name}-$visitId", visitId, kind, json.getString("documentId"),
                json.getLong("createdAt"), amount, "UZS", if (kind == StatementKind.Request) "Ожидает выполнения" else "Возврат товара",
                if (kind == StatementKind.Request) "Создана" else "Оформлен", lines,
                listOf(json.optString("reasonLabel"), json.optString("comment")).filter { it.isNotBlank() }.joinToString(" · "))
        } }
    }
    documents("agent_visit_requests", StatementKind.Request)
    documents("visit_returns", StatementKind.Return)
    records("agent_visit_sessions").filterKeys(::belongs).forEach { (visitId, raw) -> runCatching {
        val json = JSONObject(raw)
        val amount = json.optLong("collectionAmount", 0)
        val results = json.optJSONArray("results")
        val collected = results != null && (0 until results.length()).any { results.optString(it) == VisitResult.Collection.name }
        if (amount > 0 && collected) entries += StoreStatementEntry("local-payment-$visitId", visitId,
            StatementKind.Payment, "", json.getLong("startedAt"), BigDecimal.valueOf(amount), "UZS", "Инкассация", "Принята",
            dateLabel = "Дата визита") // Older sessions do not store a separate payment timestamp.
    } }
    statementEntriesForStore(store.id, storeStatementSamples(store, now) + entries, links)
}
