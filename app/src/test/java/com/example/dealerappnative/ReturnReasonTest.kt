package com.example.dealerappnative

import com.example.dealerappnative.model.*
import org.junit.Assert.*
import org.junit.Test

class ReturnReasonTest {
    @Test fun systemPresetsHaveStableUniqueIdsAndDoNotDuplicateOther() {
        assertEquals(systemReturnReasons.size, systemReturnReasons.map { it.id }.toSet().size)
        assertTrue(systemReturnReasons.all { it.system && it.enabled && it.label.isNotBlank() })
        assertTrue(systemReturnReasons.none { it.id == otherReturnReasonId })
    }

    @Test fun disablingAReasonPreventsItsUseEvenWithAComment() {
        val preset = systemReturnReasons.first()
        val disabled = listOf(preset.copy(enabled = false))
        assertFalse(canConfirmReturnReason(preset.id, "Комментарий", disabled))
        assertFalse(canConfirmReturnReason("missing", "Комментарий", disabled))
        assertFalse(canConfirmReturnReason(null, "", disabled))
        assertTrue(canConfirmReturnReason(otherReturnReasonId, "Своя причина", disabled))
    }

    @Test fun standardReasonAllowsAnAdditionalNoteAndOtherRequiresOne() {
        val id = systemReturnReasons.first().id
        assertTrue(canConfirmReturnReason(id, "", systemReturnReasons))
        assertTrue(canConfirmReturnReason(id, "Подробности возврата", systemReturnReasons))
        assertFalse(canConfirmReturnReason(otherReturnReasonId, "   ", systemReturnReasons))
        assertTrue(canConfirmReturnReason(otherReturnReasonId, "Не подошёл размер", systemReturnReasons))
    }

    @Test fun administratorNamesAreValidatedWithoutLosingDisabledReasons() {
        val custom = ReturnReason("admin-1", "Не подошёл размер", false, false)
        val reasons = systemReturnReasons + custom
        assertNotNull(returnReasonError("  НЕ ПОДОШЁЛ РАЗМЕР  ", reasons))
        assertNotNull(returnReasonError("Другое", reasons))
        assertNotNull(returnReasonError(" ", reasons))
        assertNotNull(returnReasonError("x".repeat(101), reasons))
        assertNull(returnReasonError("  Не подошёл размер  ", reasons, "admin-1"))
        assertNull(returnReasonError("Не подошла комплектация", reasons))
    }
}
