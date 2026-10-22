package com.example.dealerappnative

import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.*
import org.junit.Assert.*
import org.junit.Test

class AgentHistoryReportsTest {
    private val time = StatisticsDates.day(2026, 10, 21) + 6 * 3_600_000L
    private fun entry(index: Int, results: List<VisitResult>) = AgentHistoryEntry(agentVisitCards[index],
        AgentVisitSession(time + index * 60_000L, time + (index + 12) * 60_000L,
            comment = "Комментарий $index", photoUris = listOf("content://photo/$index"), results = results))

    @Test fun onlyRecordedWorkCreatesReportsAndReopeningKeepsSameData() {
        val source = entry(0, listOf(VisitResult.Request, VisitResult.Collection))
        val reports = historyOperations(source)
        assertEquals(source.session.results, reports.map { it.result })
        assertEquals(reports, historyOperations(source))
        assertTrue(historyOperations(entry(0, emptyList())).isEmpty())
        assertTrue(historyOperations(source.copy(session = source.session.copy(finishedAt = null))).isEmpty())
        assertEquals("Комментарий 0", source.session.comment)
        assertEquals(listOf("content://photo/0"), source.session.photoUris)
    }

    @Test fun eachVisitHasItsOwnDocuments() {
        val first = historyOperations(entry(0, listOf(VisitResult.Request))).single()
        val second = historyOperations(entry(1, listOf(VisitResult.Request))).single()
        assertNotEquals(first.documentId, second.documentId)
        assertEquals(first.amount, first.products.sumOf { it.total })
        val fiveProducts = entry(0, listOf(VisitResult.Request)).let {
            it.copy(visit = it.visit.copy(details = it.visit.details.copy(request = "Заявка на 5 товаров")))
        }
        assertEquals(5, historyOperations(fiveProducts).single().products.size)
    }

    @Test fun planTotalsMatchSameMonthSameStoreSameOperation() {
        val entries = listOf(entry(0, listOf(VisitResult.Request, VisitResult.Collection)),
            entry(1, listOf(VisitResult.Request)))
        val plans = historyStorePlans(HistorySection.RequestPlan, HistoryMonth(2026, 10), entries)
        val expected = entries.flatMap(::historyOperations).filter { it.result == VisitResult.Request }.sumOf { it.amount }
        assertEquals(expected, plans.sumOf { it.received })
        assertEquals(1, plans.first { it.visit.id == entries[0].visit.id }.operations.size)
        assertEquals(0L, historyStorePlans(HistorySection.RequestPlan, HistoryMonth(2026, 9), entries).sumOf { it.received })
        val collection = historyStorePlans(HistorySection.CollectionPlan, HistoryMonth(2026, 10), entries)
        assertEquals(0L, collection.first { it.visit.id == entries[1].visit.id }.received)
    }

    @Test fun dateGroupingUsesTashkentAndRetainsOlderRecordedMonths() {
        val nearMidnight = StatisticsDates.day(2026, 10, 1) - 4 * 3_600_000L
        assertEquals(HistoryMonth(2026, 10), HistoryMonth.at(nearMidnight))
        val old = entry(0, emptyList()).let { it.copy(session = it.session.copy(startedAt = StatisticsDates.day(2025, 12, 3))) }
        val months = historyPlanMonths(listOf(old), time)
        assertEquals(HistoryMonth(2026, 10), months.first())
        assertTrue(HistoryMonth(2025, 12) in months)
        assertEquals(months.distinct(), months)
    }
}
