package com.example.dealerappnative.data

import com.example.dealerappnative.model.*
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

internal data class AdminReturnDocument(val visit: VisitStore, val record: VisitReturnRecord,
    val acceptedAt: Long? = null, val completedAt: Long? = null) {
    val key: String get() = "${visit.id}/${record.documentId}"
    val month: HistoryMonth get() = HistoryMonth.at(record.createdAt)
}

/** Existing demo returns plus locally saved returns. A saved document replaces its demo. */
internal fun adminReturnDocuments(records: Map<String, VisitReturnRecord>): List<AdminReturnDocument> {
    val stores = (activeVisits + pastVisits + agentVisitCards).distinctBy { it.id }
    val date = SimpleDateFormat("dd.MM.yyyy", Locale.forLanguageTag("ru")).apply {
        timeZone = TimeZone.getTimeZone("Asia/Tashkent")
    }
    val demos = (activeVisits + pastVisits).filter { VisitResult.Return in it.results && it.id !in records }.mapIndexedNotNull { index, visit ->
        val day = runCatching { date.parse(visit.details.date)?.time }.getOrNull() ?: return@mapIndexedNotNull null
        val created = day + 10 * 3_600_000L + index * 15 * 60_000L
        val entry = AgentHistoryEntry(visit, AgentVisitSession(created, created + 60_000L, results = visit.results))
        val operation = historyOperations(entry).first { it.result == VisitResult.Return }
        val reason = systemReturnReasons[index % systemReturnReasons.size]
        AdminReturnDocument(visit, VisitReturnRecord("RN-${operation.documentId}", created,
            operation.products.map { VisitReturnItem(it.name, it.price, it.quantity) },
            reasonId = reason.id, reasonLabel = reason.label), created + 60_000L, created + 60_000L)
    }
    val saved = records.mapNotNull { (id, record) -> stores.find { it.id == id }?.let { AdminReturnDocument(it, record) } }
    return (demos + saved).sortedByDescending { it.record.createdAt }
}

internal fun adminReturnsInMonth(documents: List<AdminReturnDocument>, month: HistoryMonth): List<AdminReturnDocument> =
    documents.filter { it.month == month }.sortedByDescending { it.record.createdAt }
