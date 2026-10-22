package com.example.dealerappnative.data

import com.example.dealerappnative.model.*
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/** Shared by month totals and their lists, so no planned or active work enters history. */
internal fun historyMonthlyDocuments(result: VisitResult, month: HistoryMonth, entries: List<AgentHistoryEntry>,
    filter: AgentHistoryFilter = AgentHistoryFilter()): List<AgentHistoryEntry> = entries.filter {
    result in it.session.results && HistoryMonth.at(it.session.startedAt) == month && filter.matches(it)
}.sortedByDescending { it.session.finishedAt }

/** Administrator demo revisions use administrator stores and their existing dates. */
internal fun adminRevisionHistoryEntries(): List<AgentHistoryEntry> {
    val date = SimpleDateFormat("dd.MM.yyyy", Locale.forLanguageTag("ru")).apply {
        timeZone = TimeZone.getTimeZone("Asia/Tashkent")
    }
    return (activeVisits + pastVisits).filter { VisitResult.Revision in it.results }.mapIndexedNotNull { index, visit ->
        val day = runCatching { date.parse(visit.details.date)?.time }.getOrNull() ?: return@mapIndexedNotNull null
        val startedAt = day + 9 * 3_600_000L + index * 20 * 60_000L
        AgentHistoryEntry(visit, AgentVisitSession(startedAt, startedAt + 15 * 60_000L, results = visit.results))
    }
}
