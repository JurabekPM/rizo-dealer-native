package com.example.dealerappnative

import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.*
import org.junit.Assert.*
import org.junit.Test

class UnplannedVisitTest {
    private val source = sampleStores.first().copy(latitude = 41.32, longitude = 69.25,
        address = "Ташкент, улица Фароби, 12")
    private val started = StatisticsDates.day(2026, 10, 26) + 5 * 3_600_000L
    private fun metadata(id: String) = UnplannedVisit(id, source, UnplannedVisitKind.Visit, started)

    @Test fun repeatingStoreKeepsSeparateVisitAndOriginalStoreIdentities() {
        val first = unplannedVisitCard(metadata("unplanned-first"))
        val second = unplannedVisitCard(metadata("unplanned-second"))
        assertNotEquals(first.id, second.id)
        assertNotEquals(source.id, first.id)
        assertEquals(source.id, storeForVisit(first).id)
        assertEquals(source.latitude, storeForVisit(first).latitude, 0.0)
        assertEquals(source.longitude, storeForVisit(first).longitude, 0.0)
        assertEquals(source.phoneNumber, storeForVisit(first).phoneNumber)
        assertEquals(source.address, first.address)
        assertEquals(source.debt, first.debt)
        assertEquals(VisitStatus.NotVisited, first.status)
        assertTrue(first.results.isEmpty())
        assertEquals("26.10.2026", first.details.date)
    }

    @Test fun parallelVisitsAreAllowedButExistingVisitIdsCannotRestart() {
        val active = AgentVisitSession(started)
        val completed = active.copy(finishedAt = started + 60_000L)
        assertTrue(canStartAgentVisit("unplanned-new", emptyList(), mapOf(source.id to active)))
        assertFalse(canStartAgentVisit("unplanned-old", emptyList(), mapOf("unplanned-old" to completed)))
        assertFalse(canStartAgentVisit("unplanned-new", listOf(VisitResult.Request), emptyMap()))
        assertTrue(canStartAgentVisit("unplanned-new", emptyList(), mapOf(source.id to completed)))
    }

    @Test fun savingCollectionKeepsVisitActiveAndPreservesOtherWorkAndNotes() {
        val original = AgentVisitSession(started, comment = "Мой комментарий", photoUris = listOf("asset:/photo.jpg"),
            results = listOf(VisitResult.Request))
        val saved = original.recordCollection(125_000L).recordCollection(150_000L)
        assertTrue(saved.isActive)
        assertEquals(original.startedAt, saved.startedAt)
        assertEquals(original.comment, saved.comment)
        assertEquals(original.photoUris, saved.photoUris)
        assertEquals(listOf(VisitResult.Request, VisitResult.Collection), saved.results)
        assertEquals(150_000L, saved.collectionAmount)
        assertTrue(original.collectionAmount == null)
        assertEquals(listOf(VisitResult.Request), original.results)
    }

    @Test fun collectionRequiresActiveVisitAndPositiveAmount() {
        val active = AgentVisitSession(started)
        assertFalse(canSaveVisitCollection(null, 10L))
        assertFalse(canSaveVisitCollection(active, 0L))
        assertFalse(canSaveVisitCollection(active, -1L))
        assertFalse(canSaveVisitCollection(active.copy(finishedAt = started), 10L))
        assertTrue(canSaveVisitCollection(active, 10L))
    }

    @Test fun finishedUnplannedCollectionUsesOwnAmountAndMatchesPhysicalStoreFilter() {
        val visit = unplannedVisitCard(metadata("unplanned-history"))
        val active = AgentVisitSession(started).recordCollection(789_000L)
        val finished = active.finish(started + 900_000L)
        val entry = AgentHistoryEntry(visit, finished, unplanned = true)
        val operation = historyOperations(entry).single()
        assertEquals(VisitResult.Collection, operation.result)
        assertEquals(789_000L, operation.amount)
        assertTrue(operation.products.isEmpty())
        assertTrue(AgentHistoryFilter(storeId = source.id, unplannedOnly = true).matches(entry))
        assertFalse(AgentHistoryFilter(storeId = "different").matches(entry))
        assertFalse(AgentHistoryFilter(unplannedOnly = true).matches(entry.copy(session = active)))
        assertEquals(900_000L, finished.elapsedMillis(started + 5_000_000L))
    }

    @Test fun repeatedUnplannedOrdersAreIncludedInTheSameStoresMonthlySales() {
        val month = HistoryMonth(2026, 10)
        fun entry(id: String, amount: Long): AgentHistoryEntry {
            val record = VisitRequestRecord("RQ-$id", started, listOf(VisitRequestItem("one", "Дрель", amount, 1)))
            return AgentHistoryEntry(unplannedVisitCard(metadata(id)),
                AgentVisitSession(started, finishedAt = started + 60_000L, results = listOf(VisitResult.Request)),
                unplanned = true, requestRecord = record)
        }
        val entries = listOf(entry("unplanned-a", 200_000L), entry("unplanned-b", 300_000L))
        val store = historySalesStores(month, entries).single { it.visit.id == source.id }
        assertEquals(500_000L, store.received)
        assertEquals(2, store.documents.size)
        assertEquals(setOf("RQ-unplanned-a", "RQ-unplanned-b"), store.documents.map { it.second.documentId }.toSet())
        assertTrue(historySalesStores(HistoryMonth(2026, 9), entries).all { it.documents.isEmpty() })
    }

}
