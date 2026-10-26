package com.example.dealerappnative.data

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import com.example.dealerappnative.model.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** Only started unplanned visits are persisted. Cancelling store selection writes nothing. */
internal class AgentUnplannedVisitStore(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences("agent_unplanned_visits", Context.MODE_PRIVATE)
    val records = mutableStateMapOf<String, UnplannedVisit>()
    init {
        preferences.all.forEach { (id, raw) -> runCatching {
            val json = JSONObject(raw as String)
            val store = StoreUiModel(json.getString("storeId"), json.getString("name"), json.getString("debt"),
                DebtDirection.valueOf(json.getString("debtDirection")), json.getString("distance"),
                json.getString("address"), json.getString("assignedRole"), json.getDouble("latitude"), json.getDouble("longitude"),
                json.optString("currency", "UZS"), phoneNumber = json.optString("phone", referenceContactPhone))
            records[id] = UnplannedVisit(id, store, UnplannedVisitKind.valueOf(json.getString("kind")), json.getLong("createdAt"))
        } }
    }
    fun save(visit: UnplannedVisit) {
        val store = visit.store
        val json = JSONObject().put("storeId", store.id).put("name", store.name).put("debt", store.debt)
            .put("debtDirection", store.debtDirection.name).put("distance", store.distance).put("address", store.address)
            .put("assignedRole", store.assignedRole).put("latitude", store.latitude).put("longitude", store.longitude)
            .put("currency", store.currency).put("phone", store.phoneNumber)
            .put("kind", visit.kind.name).put("createdAt", visit.createdAt)
        preferences.edit().putString(visit.id, json.toString()).apply()
        records[visit.id] = visit
    }
}

internal fun unplannedVisitCard(visit: UnplannedVisit): VisitStore {
    val store = visit.store
    val date = SimpleDateFormat("dd.MM.yyyy", Locale.forLanguageTag("ru")).apply {
        timeZone = TimeZone.getTimeZone("Asia/Tashkent")
    }.format(Date(visit.createdAt))
    return VisitStore(visit.id, store.name, store.debt, VisitStatus.NotVisited, store.distance, store.address,
        VisitDetails(date, AgentMenuSampleData.name, "Нет доставок", "Нет заявки", "Нет ревизии", "Нет инкассации", "Нет возвращённых товаров"),
        debtDirection = store.debtDirection, sourceStore = store)
}
