package com.example.dealerappnative

import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.*
import org.junit.Assert.*
import org.junit.Test

class MonthlyHistoryDocumentsTest {
    private val month = HistoryMonth(2026, 10)
    private fun entry(index: Int, results: List<VisitResult>, at: Long = StatisticsDates.day(2026, 10, 21)) =
        AgentHistoryEntry(agentVisitCards[index], AgentVisitSession(at, at + 60_000L, results = results))

    @Test fun monthlyRevisionCountEqualsItsActualDocuments() {
        val first = entry(0, listOf(VisitResult.Revision))
        val second = entry(1, listOf(VisitResult.Request, VisitResult.Revision, VisitResult.Revision))
        val unrelated = entry(2, listOf(VisitResult.Collection))
        val old = entry(3, listOf(VisitResult.Revision), StatisticsDates.day(2026, 9, 20))
        val active = first.copy(session = first.session.copy(finishedAt = null))
        val documents = historyMonthlyDocuments(VisitResult.Revision, month, listOf(first, second, unrelated, old, active))
        assertEquals(2, documents.size)
        assertEquals(2, documents.flatMap(::historyOperations).count { it.result == VisitResult.Revision })
    }

    @Test fun storeFilterAndCollectionHistoryUseRecordedWorkOnly() {
        val first = entry(0, listOf(VisitResult.Collection))
        val second = entry(1, listOf(VisitResult.Request))
        val values = listOf(first, second)
        assertEquals(listOf(first), historyMonthlyDocuments(VisitResult.Collection, month, values))
        assertTrue(historyMonthlyDocuments(VisitResult.Collection, month, values,
            AgentHistoryFilter(storeId = second.visit.id)).isEmpty())
    }

    @Test fun administratorRevisionArchiveUsesAdministratorStoresAndDates() {
        val source = (activeVisits + pastVisits).filter { VisitResult.Revision in it.results }
        val entries = adminRevisionHistoryEntries()
        assertEquals(source.map { it.id }, entries.map { it.visit.id })
        assertTrue(entries.all { !it.session.isActive && it.visit.address.isNotBlank() })
        entries.groupBy { HistoryMonth.at(it.session.startedAt) }.forEach { (item, values) ->
            assertEquals(values.size, historyMonthlyDocuments(VisitResult.Revision, item, entries).size)
        }
    }
}
