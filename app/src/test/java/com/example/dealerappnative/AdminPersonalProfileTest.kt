package com.example.dealerappnative

import com.example.dealerappnative.model.*
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate

class AdminPersonalProfileTest {
    @Test fun formattedPhoneNormalizesAndInvalidNumbersAreRejected() {
        val p = AdminPersonalProfile(phone = "+998 (90) 116-51-15")
        assertNull(p.validationError())
        assertEquals("+998901165115", normalizeAdminPhone(p.phone))
        assertEquals("+998 90 116 51 15", displayAdminPhone(p.phone))
        assertNull(p.copy(phone = "+79991234567").validationError())
        assertNotNull(p.copy(phone = "901165115").validationError())
        assertNotNull(p.copy(phone = "+99890abc1111").validationError())
        assertNotNull(p.copy(phone = "+0123456789").validationError())
    }

    @Test fun personalFieldsAreValidatedSeparatelyFromCompany() {
        val p = AdminPersonalProfile()
        assertNull(p.validationError())
        assertNotNull(p.copy(name = " ").validationError())
        assertNotNull(p.copy(region = "").validationError())
        assertNotNull(p.copy(gender = "unknown").validationError())
        val dealer = DealerProfile()
        p.copy(name = "Changed person", region = "Ташкент")
        assertEquals("Diller pro", dealer.name)
    }

    @Test fun optionalBirthDateUsesRealCalendarAndRejectsFutureOrInvalidDate() {
        val p = AdminPersonalProfile()
        val today = LocalDate.of(2026, 10, 28)
        assertNull(p.validationError(today))
        assertNull(p.copy(birthDate = "2000-02-29").validationError(today))
        assertNotNull(p.copy(birthDate = "2001-02-29").validationError(today))
        assertNotNull(p.copy(birthDate = "2026-10-29").validationError(today))
        assertNotNull(p.copy(birthDate = "1899-12-31").validationError(today))
    }
}
