package com.example.dealerappnative.data

import org.junit.Assert.*
import org.junit.Test

class ReturnCatalogTest {
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
