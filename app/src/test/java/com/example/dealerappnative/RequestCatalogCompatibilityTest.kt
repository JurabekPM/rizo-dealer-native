package com.example.dealerappnative

import com.example.dealerappnative.data.*
import org.junit.Assert.*
import org.junit.Test

class RequestCatalogCompatibilityTest {
    @Test fun referenceDisplayOrderCannotReassignAnExistingDraftToDifferentProducts() {
        val oldIds = returnCatalog.map { it.barcode }
        assertEquals(oldIds, requestCatalog.take(oldIds.size).map { it.id })
        val oldDraft = oldIds.indices.map { if (it == 3) 0 else 2 }
        val restored = normalizeRequestQuantities(oldDraft)
        assertEquals(oldDraft, restored.take(oldDraft.size))
        assertTrue(restored.drop(oldDraft.size).all { it == 0 })
        val shown = requestCatalog.indices.sortedBy { requestCatalog[it].displayOrder }
        assertEquals(oldIds[4], requestCatalog[shown.first()].id)
        shown.forEach { index -> assertEquals(restored[index], oldIds.indexOf(requestCatalog[index].id)
            .takeIf { it >= 0 }?.let { oldDraft[it] } ?: 0) }
    }
}
