package com.example.dealerappnative

import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.*
import org.junit.Assert.*
import org.junit.Test

class VisitRequestsTest {
    private val lines = listOf(VisitRequestItem("one", "Дрель", 350_000L, 3),
        VisitRequestItem("two", "Молоток", 55_000L, 2))
    private val started = StatisticsDates.day(2026, 9, 30) + 18 * 3_600_000L
    private val sent = StatisticsDates.day(2026, 10, 1) + 2 * 3_600_000L
    private val record = VisitRequestRecord("RQ-123", sent, lines)

    @Test fun lineTotalsUseExactIntegerPricesAndSavedSnapshots() {
        assertEquals(1_160_000L, record.total)
        assertEquals(1_050_000L, lines.first().total)
        assertEquals("Дрель", record.items.first().name)
    }

    @Test fun requestCreationRequiresActiveVisitAndNonemptyUniqueProducts() {
        val active = AgentVisitSession(startedAt = started)
        assertTrue(canSaveVisitRequest(active, lines))
        assertFalse(canSaveVisitRequest(null, lines))
        assertFalse(canSaveVisitRequest(active.copy(finishedAt = sent), lines))
        assertFalse(canSaveVisitRequest(active, emptyList()))
        assertFalse(canSaveVisitRequest(active, lines + lines.first()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun zeroQuantityCannotBecomeSavedWork() { VisitRequestItem("one", "Дрель", 10, 0) }

    @Test(expected = IllegalArgumentException::class)
    fun duplicateLinesCannotCreateARecord() { record.copy(items = lines + lines.first()) }

    @Test fun historyAndSalesUseThisRequestInsteadOfSampleProducts() {
        val visit = agentVisitCards.first()
        val session = AgentVisitSession(startedAt = started, finishedAt = sent + 60_000L, results = listOf(VisitResult.Request))
        val entry = AgentHistoryEntry(visit, session, requestRecord = record)
        val operation = historyOperations(entry).single()
        assertEquals(record.documentId, operation.documentId)
        assertEquals(record.total, operation.amount)
        assertEquals(lines.map { it.name }, operation.products.map { it.name })
        assertEquals(lines.map { it.quantity }, operation.products.map { it.quantity })
        val october = historySalesStores(HistoryMonth(2026, 10), listOf(entry)).single { it.visit.id == visit.id }
        assertEquals(record.total, october.received)
        assertTrue(historySalesStores(HistoryMonth(2026, 9), listOf(entry)).all { it.documents.isEmpty() })
        assertTrue(historyOperations(entry.copy(session = session.copy(finishedAt = null))).isEmpty())
    }
}
