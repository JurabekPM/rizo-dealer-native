package com.example.dealerappnative.data

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import com.example.dealerappnative.model.AgentVisitSession
import com.example.dealerappnative.model.VisitFinishReason
import com.example.dealerappnative.model.VisitResult
import com.example.dealerappnative.model.VisitRequestItem
import com.example.dealerappnative.model.VisitRequestRecord
import com.example.dealerappnative.model.canSaveVisitRequest
import org.json.JSONArray
import org.json.JSONObject

/** Small local prototype store. No midnight reset and no server calls. */
internal class AgentVisitSessions(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences("agent_visit_sessions", Context.MODE_PRIVATE)
    val sessions = mutableStateMapOf<String, AgentVisitSession>()
    val requests = VisitRequestStore(context)

    init {
        preferences.all.forEach { (id, value) ->
            runCatching {
                val json = JSONObject(value as String)
                sessions[id] = AgentVisitSession(
                    startedAt = json.getLong("startedAt"),
                    finishedAt = if (json.has("finishedAt")) json.getLong("finishedAt") else null,
                    comment = json.optString("comment"),
                    photoUris = json.optJSONArray("photos").strings(),
                    results = json.optJSONArray("results").strings().mapNotNull { name ->
                        VisitResult.entries.find { it.name == name }
                    },
                    reason = VisitFinishReason.entries.find { it.name == json.optString("reason") }
                )
            }
        }
        val now = System.currentTimeMillis()
        agentVisitCards.forEachIndexed { index, card ->
            if (card.results.isNotEmpty()) {
                val sample = completedAgentVisitSample(index, card.results, now, sessions[card.id])
                if (sample != sessions[card.id]) save(card.id, sample)
            }
        }
        // One explicit live demo, seeded only once. Real user-started visits win.
        if (!preferences.getBoolean("_live_demo_seeded_v1", false)) {
            if (activeStoreId == null && !sessions.containsKey(demoActiveAgentVisitId)) {
                save(demoActiveAgentVisitId, AgentVisitSession(startedAt = now - 7 * 60_000L,
                    comment = "Проверяю остатки и обсуждаю ассортимент."))
            }
            preferences.edit().putBoolean("_live_demo_seeded_v1", true).apply()
        }
    }

    val activeStoreId: String? get() = sessions.entries.firstOrNull { it.value.isActive }?.key

    fun start(storeId: String, results: List<VisitResult>): Boolean {
        if (results.isNotEmpty() || sessions.containsKey(storeId) || activeStoreId != null) return false
        save(storeId, AgentVisitSession(startedAt = System.currentTimeMillis(), results = results.toList()))
        return true
    }

    /** Save exactly this visit's order; preserve its start, notes, photos and other work. */
    fun saveRequest(storeId: String, items: List<VisitRequestItem>): Boolean {
        val session = sessions[storeId] ?: return false
        if (!canSaveVisitRequest(session, items)) return false
        val existing = requests.records[storeId]
        val now = System.currentTimeMillis()
        val record = VisitRequestRecord(existing?.documentId ?: "RQ-$now", existing?.createdAt ?: now, items.toList())
        requests.save(storeId, record)
        save(storeId, session.copy(results = (session.results + VisitResult.Request).distinct()))
        return true
    }

    fun addReturn(storeId: String): Boolean {
        val session = sessions[storeId] ?: return false
        if (!session.isActive) return false
        save(storeId, session.copy(results = (session.results + VisitResult.Return).distinct()))
        return true
    }

    fun comment(storeId: String, text: String) = edit(storeId) { copy(comment = text) }
    fun addPhotos(storeId: String, uris: List<String>) = edit(storeId) {
        copy(photoUris = (photoUris + uris).distinct().take(6))
    }
    fun removePhoto(storeId: String, uri: String) = edit(storeId) { copy(photoUris = photoUris - uri) }

    fun finish(storeId: String, reason: VisitFinishReason? = null): Boolean {
        val session = sessions[storeId] ?: return false
        if (!session.canFinish(reason)) return false
        save(storeId, session.finish(System.currentTimeMillis(), reason))
        return true
    }

    private fun edit(storeId: String, change: AgentVisitSession.() -> AgentVisitSession) {
        val session = sessions[storeId] ?: return
        if (session.isActive) save(storeId, session.change())
    }

    private fun save(storeId: String, session: AgentVisitSession) {
        sessions[storeId] = session
        val json = JSONObject().put("startedAt", session.startedAt).put("comment", session.comment)
            .put("photos", JSONArray(session.photoUris)).put("results", JSONArray(session.results.map { it.name }))
        session.finishedAt?.let { json.put("finishedAt", it) }
        session.reason?.let { json.put("reason", it.name) }
        preferences.edit().putString(storeId, json.toString()).apply()
    }
}

private fun JSONArray?.strings(): List<String> = if (this == null) emptyList()
    else (0 until length()).map { getString(it) }
