package com.example.dealerappnative

import com.example.dealerappnative.data.*
import org.junit.Assert.*
import org.junit.Test

class RequestCatalogCompatibilityTest {
    @Test fun quickStepsAddOneThenAccumulateAndStopAtStockWithoutChangingOtherSelections() {
        var draft = List(requestCatalog.size) { if (it == 4) 3 else 0 }
        draft = stepRequestPickerQuantity(draft, 0, 1)
        assertEquals(1, draft[0])
        repeat(9) { draft = stepRequestPickerQuantity(draft, 0, 1) }
        assertEquals(10, draft[0])
        assertEquals(3, draft[4])
        assertEquals(requestCatalog[0].price * 10L + requestCatalog[4].price * 3L, requestPickerTotal(draft))
        repeat(requestCatalog[0].stock) { draft = stepRequestPickerQuantity(draft, 0, 1) }
        assertEquals(requestCatalog[0].stock, draft[0])
        assertEquals(3, draft[4])
        draft = stepRequestPickerQuantity(draft, 3, 1)
        assertEquals(0, draft[3]) // An unavailable item cannot enter the draft.
    }

    @Test fun lastMinusRemovesSelectionAndTotalButDoesNotMutateTheIncomingOrder() {
        val incoming = MutableList(requestCatalog.size) { 0 }.apply { this[0] = 1 }
        val draft = stepRequestPickerQuantity(incoming, 0, -1)
        assertEquals(1, incoming[0])
        assertEquals(0, draft[0])
        assertTrue(requestPickerIndexes(draft, selectedOnly = true).isEmpty())
        assertEquals(0L, requestPickerTotal(draft))
        assertEquals(draft, stepRequestPickerQuantity(draft, 0, -1))
    }

    @Test fun unavailableProductsFollowAllAvailableProductsWithoutChangingCatalogOrder() {
        val ids = requestCatalog.map { it.id }
        val shown = requestPickerIndexes(emptyList())
        val available = shown.takeWhile { requestCatalog[it].stock > 0 }
        assertEquals(requestCatalog.count { it.stock > 0 }, available.size)
        assertTrue(shown.drop(available.size).all { requestCatalog[it].stock == 0 })
        assertEquals(available.sortedBy { requestCatalog[it].displayOrder }, available)
        assertEquals(requestCatalog.indices.toSet(), shown.toSet())
        assertEquals(ids, requestCatalog.map { it.id })
    }

    @Test fun selectedViewCombinesSearchWithSelectionAndRemovingLastItemLeavesNoGhostRow() {
        val draft = MutableList(requestCatalog.size) { 0 }.apply { this[0] = 2; this[4] = 3; this[3] = 99 }
        assertEquals(listOf(4, 0), requestPickerIndexes(draft, selectedOnly = true))
        assertEquals(listOf(4), requestPickerIndexes(draft, "  mAkItA  ", selectedOnly = true))
        assertEquals(listOf(0), requestPickerIndexes(draft, requestCatalog[0].id, selectedOnly = true))
        assertTrue(requestPickerIndexes(draft, "Ryobi", selectedOnly = true).isEmpty())
        assertTrue(requestPickerIndexes(draft, "Ryobi").isNotEmpty())
        draft[0] = 0; draft[4] = 0
        assertTrue(requestPickerIndexes(draft, selectedOnly = true).isEmpty())
    }

    @Test fun summaryIncludesAllQuantitiesAtCurrentPricesEvenWhenSearchHidesThem() {
        val draft = MutableList(requestCatalog.size) { 0 }.apply { this[0] = 2; this[4] = 3 }
        val expected = requestCatalog[0].price * 2L + 450_000L * 3L
        assertEquals(listOf(4), requestPickerIndexes(draft, requestCatalog[4].id))
        assertEquals(expected, requestPickerTotal(draft))
        assertEquals(0L, requestPickerTotal(emptyList()))
        val maximum = requestCatalog.map { it.stock }
        val maximumTotal = requestCatalog.sumOf { it.price * it.stock.toLong() }
        assertEquals(maximumTotal, requestPickerTotal(maximum))
        assertEquals(maximumTotal, requestPickerTotal(requestCatalog.map { Int.MAX_VALUE }))
    }

    @Test fun referenceDisplayOrderCannotReassignAnExistingDraftToDifferentProducts() {
        val oldIds = returnCatalog.map { it.barcode }
        assertEquals(oldIds, requestCatalog.take(oldIds.size).map { it.id })
        val oldDraft = oldIds.indices.map { if (it == 3) 0 else 2 }
        val restored = normalizeRequestQuantities(oldDraft)
        assertEquals(oldDraft, restored.take(oldDraft.size))
        assertTrue(restored.drop(oldDraft.size).all { it == 0 })
        val shown = requestPickerIndexes(restored)
        assertEquals(oldIds[4], requestCatalog[shown.first()].id)
        shown.forEach { index -> assertEquals(restored[index], oldIds.indexOf(requestCatalog[index].id)
            .takeIf { it >= 0 }?.let { oldDraft[it] } ?: 0) }
    }
}
