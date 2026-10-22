package com.example.dealerappnative

import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.*
import org.junit.Assert.*
import org.junit.Test

class AgentHistorySalesTest {
    private val time = StatisticsDates.day(2026, 10, 21) + 3_600_000L
    private fun entry(index: Int, results: List<VisitResult>) = AgentHistoryEntry(agentVisitCards[index],
        AgentVisitSession(startedAt = time, finishedAt = time + 600_000L, results = results))

    @Test fun salesSubtractReturnsUsingTheSameVisitDocuments() {
        val source = entry(0, listOf(VisitResult.Request, VisitResult.Return, VisitResult.Collection))
        val store = historySalesStores(HistoryMonth(2026, 10), listOf(source)).first { it.visit.id == source.visit.id }
        val reports = historyOperations(source)
        assertEquals(reports.first { it.result == VisitResult.Request }.amount, store.received)
        assertEquals(reports.first { it.result == VisitResult.Return }.amount, store.returned)
        assertEquals(store.received - store.returned, store.net)
        assertEquals(setOf(VisitResult.Request, VisitResult.Return), store.documents.map { it.second.result }.toSet())
    }

    @Test fun salesNeverIncludeAnActiveVisitOrAnotherMonth() {
        val source = entry(0, listOf(VisitResult.Request))
        val active = source.copy(session = source.session.copy(finishedAt = null))
        assertTrue(historySalesStores(HistoryMonth(2026, 10), listOf(active)).all { it.documents.isEmpty() })
        assertTrue(historySalesStores(HistoryMonth(2026, 9), listOf(source)).all { it.documents.isEmpty() })
    }

    @Test fun totalsAndDrilldownStayWithTheirOwnStore() {
        val first = entry(0, listOf(VisitResult.Request, VisitResult.Return))
        val second = entry(1, listOf(VisitResult.Return))
        val stores = historySalesStores(HistoryMonth(2026, 10), listOf(first, second))
        assertEquals(3, stores.sumOf { it.documents.size })
        stores.forEach { store -> assertTrue(store.documents.all { it.first.visit.id == store.visit.id }) }
        val returnOnly = stores.first { it.visit.id == second.visit.id }
        assertEquals(0L, returnOnly.received)
        assertEquals(-returnOnly.returned, returnOnly.net)
        assertEquals(stores.sumOf { it.received } - stores.sumOf { it.returned }, stores.sumOf { it.net })
    }
}
