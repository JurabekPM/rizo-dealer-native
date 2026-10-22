package com.example.dealerappnative

import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.*
import org.junit.Assert.*
import org.junit.Test

class AdminReturnDocumentsTest {
    @Test fun archiveMonthsContainEachDocumentExactlyOnce() {
        val documents = adminReturnDocuments(emptyMap())
        assertTrue(documents.isNotEmpty())
        val grouped = documents.map { it.month }.distinct().flatMap { adminReturnsInMonth(documents, it) }
        assertEquals(documents.size, grouped.size)
        assertEquals(documents.map { it.key }.toSet(), grouped.map { it.key }.toSet())
        assertTrue(documents.all { it.record.total == it.record.items.sumOf { line -> line.price * line.quantity } })
    }

    @Test fun savedDocumentReplacesDemoAndUsesItsOwnDateReasonAndProducts() {
        val previous = adminReturnDocuments(emptyMap()).first()
        val record = VisitReturnRecord("RN-test", StatisticsDates.day(2026, 9, 30),
            listOf(VisitReturnItem("Возвращённый товар", 200_000L, 2)), "Упаковка", "custom", "Повреждение")
        val documents = adminReturnDocuments(mapOf(previous.visit.id to record))
        val replacement = documents.single { it.visit.id == previous.visit.id }
        assertEquals(record, replacement.record)
        assertEquals(HistoryMonth(2026, 9), replacement.month)
        assertEquals(400_000L, adminReturnsInMonth(documents, replacement.month).sumOf { it.record.total })
        assertNull(replacement.acceptedAt)
        assertNull(replacement.completedAt)
    }

    @Test fun administratorArchiveIncludesSavedAgentReturns() {
        val agent = agentVisitCards.first { candidate -> (activeVisits + pastVisits).none { it.id == candidate.id } }
        val record = VisitReturnRecord("RN-agent", StatisticsDates.day(2026, 10, 22), listOf(VisitReturnItem("Товар", 50_000L, 1)))
        assertEquals(record, adminReturnDocuments(mapOf(agent.id to record)).single { it.visit.id == agent.id }.record)
    }
}
