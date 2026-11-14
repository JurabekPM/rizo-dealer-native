package com.example.dealerappnative

import com.example.dealerappnative.model.AgentVisitSession
import com.example.dealerappnative.model.VisitFinishReason
import com.example.dealerappnative.model.formatFinishedVisitDuration
import com.example.dealerappnative.model.formatVisitElapsed
import org.junit.Assert.assertEquals
import org.junit.Test

class VisitDurationTest {
    @Test fun alwaysShowsHoursMinutesAndSecondsIncludingBoundaries() {
        mapOf(0L to "00:00:00", 999L to "00:00:00", 1_000L to "00:00:01", 59_000L to "00:00:59",
            60_000L to "00:01:00", 3_599_000L to "00:59:59", 3_600_000L to "01:00:00", 3_661_000L to "01:01:01")
            .forEach { (millis, expected) -> assertEquals(expected, formatVisitElapsed(millis)) }
    }
    @Test fun elapsedShowsWholeDaysFromTwentyFourHours() {
        assertEquals("23:59:59", formatVisitElapsed(86_399_999L))
        assertEquals("1 дн. 00:00:00", formatVisitElapsed(86_400_000L))
        assertEquals("1 дн. 01:01:01", formatVisitElapsed(90_061_000L))
        assertEquals("15 дн. 19:20:09", formatVisitElapsed(1_365_609_000L))
        assertEquals("00:00:00", formatVisitElapsed(-1_000L))
    }
    @Test fun finishedVisitDurationKeepsShortLabelsAndShowsHoursOrDaysForLongVisits() {
        assertEquals("59 сек", formatFinishedVisitDuration(59_000L))
        assertEquals("1 мин", formatFinishedVisitDuration(60_000L))
        assertEquals("59 мин", formatFinishedVisitDuration(3_599_000L))
        assertEquals("1 ч 1 мин", formatFinishedVisitDuration(3_661_000L))
        assertEquals("1 дн. 00:00:00", formatFinishedVisitDuration(86_400_000L))
    }
    @Test fun actualVisitStartAndFinishedStopRemainTheTimeSource() {
        val active = AgentVisitSession(86_395_000L)
        assertEquals("00:00:12", formatVisitElapsed(active.elapsedMillis(86_407_000L)))
        val finished = active.finish(86_407_000L, VisitFinishReason.NoOrderNeeded)
        assertEquals("00:00:12", formatVisitElapsed(finished.elapsedMillis(90_000_000L)))
    }
}
