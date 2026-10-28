package com.example.dealerappnative

import com.example.dealerappnative.model.*
import org.junit.Assert.*
import org.junit.Test

class DealerProfileTest {
    @Test fun optionalTaxIdAndValidInnPinflAreAccepted() {
        val profile = DealerProfile()
        assertNull(profile.validationError())
        assertNull(profile.copy(taxId = "123456789").validationError())
        assertNull(profile.copy(taxId = "12345678901234").validationError())
        assertNotNull(profile.copy(taxId = "12345678").validationError())
        assertNotNull(profile.copy(taxId = "12345678A").validationError())
    }

    @Test fun missingAddressNameAndInvalidCoordinatesCannotBeSaved() {
        val p = DealerProfile()
        assertNotNull(p.copy(name = " ").validationError())
        assertNotNull(p.copy(address = " ").validationError())
        assertNotNull(p.copy(region = "").validationError())
        assertNotNull(p.copy(latitude = Double.NaN).validationError())
        assertNotNull(p.copy(longitude = 181.0).validationError())
    }

    @Test fun workingHoursCopyPreservesWeekendAndDoesNotMutateOriginal() {
        val days = List(7) { i -> DealerWorkingDay(i < 5, if (i == 0) 600 else 540, if (i == 0) 1260 else 1200) }
        val profile = DealerProfile(days = days)
        val copied = profile.applyWorkingHoursToAll()
        assertTrue(copied.days.all { it.opensAt == 600 && it.closesAt == 1260 })
        assertFalse(copied.days[5].enabled)
        assertEquals(540, profile.days[1].opensAt)
        assertEquals(days, profile.days)
    }

    @Test fun overnightScheduleWorksAndInvalidOrEqualTimesAreRejected() {
        val p = DealerProfile()
        assertNull(p.copy(days = List(7) { DealerWorkingDay(true, 1320, 360) }).validationError())
        assertNotNull(p.copy(days = List(7) { DealerWorkingDay(true, 540, 540) }).validationError())
        assertNotNull(p.copy(days = List(7) { DealerWorkingDay(true, 0, 1440) }).validationError())
        assertNotNull(p.copy(days = emptyList()).validationError())
        assertEquals("09:05", dealerClock(545))
        val closed = p.copy(days = List(7) { DealerWorkingDay(false) })
        assertEquals(closed, closed.applyWorkingHoursToAll())
    }
}
