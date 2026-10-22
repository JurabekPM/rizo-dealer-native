package com.example.dealerappnative

import com.example.dealerappnative.data.agentVisitCards
import com.example.dealerappnative.model.*
import org.junit.Assert.*
import org.junit.Test

class AgentHistoryFilterTest {
    private val visit = agentVisitCards.first()
    // Tashkent 2026-10-21 00:05, still October 20 in UTC.
    private val start = StatisticsDates.day(2026, 10, 21) - 5 * 3_600_000L + 5 * 60_000L
    private val entry = AgentHistoryEntry(visit,
        AgentVisitSession(startedAt = start, finishedAt = start + 10 * 60_000L,
            results = listOf(VisitResult.Request)), unplanned = true)

    @Test fun activeSessionsNeverAppearEvenWithoutFilters() {
        assertFalse(AgentHistoryFilter().matches(entry.copy(session = entry.session.copy(finishedAt = null))))
        assertTrue(AgentHistoryFilter().matches(entry))
    }

    @Test fun filtersAreCombinedAndUseSessionResults() {
        assertTrue(AgentHistoryFilter(storeId = visit.id, type = HistoryVisitType.Request,
            status = HistoryStatus.Completed, unplannedOnly = true).matches(entry))
        assertFalse(AgentHistoryFilter(type = HistoryVisitType.Collection).matches(entry))
        assertFalse(AgentHistoryFilter(storeId = "other").matches(entry))
        assertFalse(AgentHistoryFilter(unplannedOnly = true).matches(entry.copy(unplanned = false)))
    }

    @Test fun inclusiveDatesUseTashkentCalendarDay() {
        val day = StatisticsDates.day(2026, 10, 21)
        assertTrue(AgentHistoryFilter(from = day, to = day).matches(entry))
        assertTrue(AgentHistoryFilter(from = day - StatisticsDates.dayMillis, to = day).matches(entry))
        assertFalse(AgentHistoryFilter(from = day - StatisticsDates.dayMillis, to = day - StatisticsDates.dayMillis).matches(entry))
    }

    @Test fun fewerOperationsDoNotMakeCompletedVisitPartial() {
        assertEquals(HistoryStatus.Completed, entry.status)
        assertFalse(AgentHistoryFilter(status = HistoryStatus.Partial).matches(entry))
        val closed = entry.copy(session = entry.session.copy(results = emptyList(), reason = VisitFinishReason.StoreClosed))
        assertTrue(AgentHistoryFilter(status = HistoryStatus.Cancelled).matches(closed))
        assertTrue(AgentHistoryFilter().matches(entry.copy(session = entry.session.copy(results = emptyList(),
            reason = VisitFinishReason.NoOrderNeeded))))
    }

    @Test(expected = IllegalArgumentException::class) fun invertedRangeIsRejected() {
        AgentHistoryFilter(from = 2L, to = 1L)
    }
}
