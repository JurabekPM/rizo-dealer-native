package com.example.dealerappnative

import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.*
import org.junit.Assert.*
import org.junit.Test

class VisitReturnsTest {
    private val started = StatisticsDates.day(2026, 10, 31) + 18 * 3_600_000L // 23:00 in Tashkent
    private val record = VisitReturnRecord("RN-test", StatisticsDates.day(2026, 11, 1),
        listOf(VisitReturnItem("Дрель", 350_000L, 2), VisitReturnItem("Аккумулятор", 275_000L, 1)), "Повреждена упаковка")
    private val entry = AgentHistoryEntry(agentVisitCards.first(), AgentVisitSession(started,
        started + 3_600_000L, results = listOf(VisitResult.Return)), returnRecord = record)

    @Test fun returnDocumentUsesChosenProductsAndExactTotal() {
        assertEquals(975_000L, record.total)
        val report = historyOperations(entry).single()
        assertEquals(record.documentId, report.documentId)
        assertEquals(record.total, report.amount)
        assertEquals(listOf(2, 1), report.products.map { it.quantity })
        assertEquals(listOf("Дрель", "Аккумулятор"), report.products.map { it.name })
    }

    @Test fun monthlyReturnTotalUsesDocumentMonthAndExcludesActiveVisits() {
        assertEquals(0L, historyReturnTotal(HistoryMonth(2026, 10), listOf(entry)))
        assertEquals(record.total, historyReturnTotal(HistoryMonth(2026, 11), listOf(entry)))
        val active = entry.copy(session = entry.session.copy(finishedAt = null))
        assertEquals(0L, historyReturnTotal(HistoryMonth(2026, 11), listOf(active)))
    }

    @Test fun monthlySalesSubtractTheSameCustomReturnAndDoNotMoveOtherWork() {
        val both = entry.copy(session = entry.session.copy(results = listOf(VisitResult.Request, VisitResult.Return)))
        val october = historySalesStores(HistoryMonth(2026, 10), listOf(both)).first { it.visit.id == entry.visit.id }
        val november = historySalesStores(HistoryMonth(2026, 11), listOf(both)).first { it.visit.id == entry.visit.id }
        assertTrue(october.received > 0)
        assertEquals(0L, october.returned)
        assertEquals(0L, november.received)
        assertEquals(record.total, november.returned)
    }

    @Test(expected = IllegalArgumentException::class) fun cannotSaveAnEmptyReturn() {
        VisitReturnRecord("empty", started, emptyList())
    }

    @Test(expected = IllegalArgumentException::class) fun cannotReturnZeroProducts() {
        VisitReturnItem("Дрель", 350_000L, 0)
    }
}
