package com.example.dealerappnative.data

import org.junit.Assert.*
import com.example.dealerappnative.model.PickerProduct
import com.example.dealerappnative.model.VisitReturnItem
import com.example.dealerappnative.model.VisitReturnRecord
import org.junit.Test

class ReturnCatalogTest {
    @Test fun sharedPickerUsesReturnStockRatherThanLargerRequestAvailability() {
        val product: PickerProduct = returnCatalog.first()
        assertEquals(returnCatalog.first().barcode, product.id)
        assertEquals(returnCatalog.first().available, product.stock)
        assertTrue(requestCatalog.first().stock > product.stock)
        val incoming = MutableList(returnCatalog.size) { 0 }.apply { this[1] = 2 }
        var draft: List<Int> = incoming
        repeat(requestCatalog.first().stock + 1) { draft = stepReturnPickerQuantity(draft, 0, 1) }
        assertEquals(product.stock, draft[0])
        assertEquals(2, draft[1])
        assertEquals(0, incoming[0]) // Cancel can keep the caller's original draft unchanged.
    }

    @Test fun pickerTotalMatchesSavedReturnAndRemovingEverythingCanConfirmAnEmptyDraft() {
        val incoming = listOf(2, 1)
        var draft = normalizeReturnQuantities(incoming)
        val items = returnCatalog.indices.filter { draft[it] > 0 }.map {
            VisitReturnItem(returnCatalog[it].name, returnCatalog[it].price, draft[it])
        }
        val record = VisitReturnRecord("RN-picker", 1L, items)
        assertEquals(1_125_000L, returnPickerTotal(draft))
        assertEquals(record.total, returnPickerTotal(draft))
        repeat(3) { draft = stepReturnPickerQuantity(draft, 0, -1) }
        draft = stepReturnPickerQuantity(draft, 1, -1)
        assertTrue(draft.all { it == 0 })
        assertEquals(0L, returnPickerTotal(draft))
        assertEquals(listOf(2, 1), incoming)
    }

    @Test fun restoredOldDraftKeepsSelectionAndAddsUnselectedProducts() {
        val normalized = normalizeReturnQuantities(listOf(2, 1, 0, 4))
        assertEquals(returnCatalog.size, normalized.size)
        assertEquals(listOf(2, 1, 0, 4), normalized.take(4))
        assertTrue(normalized.drop(4).all { it == 0 })
    }

    @Test fun draftCannotExceedStockOrContainNegativeQuantities() {
        val normalized = normalizeReturnQuantities(listOf(-1, 999))
        assertEquals(0, normalized[0])
        assertEquals(returnCatalog[1].available, normalized[1])
        assertTrue(returnCatalog.all { it.available in 1..99 && it.originalQuantity >= it.available })
    }

    @Test fun sourceDocumentsAndProductIdentitiesRemainDistinct() {
        assertEquals(returnCatalog.size, returnCatalog.map { it.barcode }.distinct().size)
        assertEquals(returnCatalog.size, returnCatalog.map { it.orderNumber }.distinct().size)
        assertEquals(350_000L, returnCatalog.first().price)
        assertTrue(returnCatalog.size >= 20)
    }
}
