package com.example.dealerappnative

import com.example.dealerappnative.data.AgentStatisticsSampleData
import com.example.dealerappnative.model.StatisticsDateRange
import com.example.dealerappnative.model.StatisticsDates
import com.example.dealerappnative.model.StatisticsPeriod
import org.junit.Assert.*
import org.junit.Test

class StatisticsPeriodTest {
    private fun date(year: Int, month: Int, day: Int) = StatisticsDates.day(year, month, day)
    private val anchor = date(2026, 10, 21)
    private fun range(period: StatisticsPeriod) = StatisticsDates.range(period, anchor, StatisticsDateRange(anchor, anchor))

    @Test fun weekStartsOnMondayAndEndsOnSunday() {
        assertEquals(StatisticsDateRange(date(2026, 10, 19), date(2026, 10, 25)), range(StatisticsPeriod.Week))
        val sunday = date(2026, 10, 25)
        assertEquals(range(StatisticsPeriod.Week), StatisticsDates.range(StatisticsPeriod.Week, sunday, StatisticsDateRange(sunday, sunday)))
    }

    @Test fun monthHandlesLeapYear() {
        val feb = date(2024, 2, 15)
        assertEquals(StatisticsDateRange(date(2024, 2, 1), date(2024, 2, 29)),
            StatisticsDates.range(StatisticsPeriod.Month, feb, StatisticsDateRange(feb, feb)))
    }

    @Test fun tashkentDayDoesNotShiftToPreviousUtcDay() {
        val lateUtc = date(2026, 10, 20) + 20 * 60 * 60_000L
        assertEquals(anchor, StatisticsDates.today(lateUtc))
    }

    @Test fun customIntervalIncludesBothBoundaryDays() {
        val yesterday = anchor - StatisticsDates.dayMillis
        val first = AgentStatisticsSampleData.forRange(StatisticsDateRange(yesterday, yesterday), anchor)
        val second = AgentStatisticsSampleData.forRange(StatisticsDateRange(anchor, anchor), anchor)
        val combined = AgentStatisticsSampleData.forRange(StatisticsDateRange(yesterday, anchor), anchor)
        assertEquals(first.completedVisits + second.completedVisits, combined.completedVisits)
        assertEquals(first.plannedVisits + second.plannedVisits, combined.plannedVisits)
        assertNotEquals(first.metrics[0].value, second.metrics[0].value)
    }

    @Test fun futureDaysOnlyAddPlansToTheCurrentWeek() {
        val partial = AgentStatisticsSampleData.forRange(StatisticsDateRange(date(2026, 10, 19), anchor), anchor)
        val week = AgentStatisticsSampleData.forRange(range(StatisticsPeriod.Week), anchor)
        assertEquals(partial.completedVisits, week.completedVisits)
        assertEquals(partial.metrics[0].value, week.metrics[0].value)
        assertTrue(week.plannedVisits > partial.plannedVisits)
    }

    @Test fun oldRangeHasZeroActualsAndNoInvalidProgress() {
        val old = date(2020, 1, 1)
        val summary = AgentStatisticsSampleData.forRange(StatisticsDateRange(old, old), anchor)
        assertFalse(summary.hasData)
        assertEquals(0, summary.plannedVisits)
        assertEquals(0, summary.completedVisits)
        assertEquals(0f, summary.metrics.last().progress!!, 0f)
    }

    @Test fun planProgressUsesConfirmedTotalsFromPlanStartEvenOnDayTab() {
        val today = date(2026, 11, 6)
        val day = StatisticsDateRange(today, today)
        val dayMetrics = AgentStatisticsSampleData.forRange(day, today).metrics
        val weekMetrics = AgentStatisticsSampleData.forRange(StatisticsDates.range(StatisticsPeriod.Week, today, day), today).metrics

        val requests = dayMetrics[0].planProgress!!.single()
        assertEquals(StatisticsPeriod.Week, requests.plan.period)
        assertEquals(3_000, requests.confirmedUsd)
        assertEquals(60L, requests.percent)
        assertEquals(requests, weekMetrics[0].planProgress!!.single())
        assertNotEquals(dayMetrics[0].value, weekMetrics[0].value)

        val collections = dayMetrics[2].planProgress!!.single()
        assertEquals(StatisticsPeriod.Month, collections.plan.period)
        assertEquals(8_000, collections.confirmedUsd)
        assertEquals(40L, collections.percent)
        assertEquals(collections, weekMetrics[2].planProgress!!.single())
        assertNull(dayMetrics[1].planProgress)
    }

    @Test fun historicalDayAndMonthShowTheirOwnPlanProgress() {
        val today = date(2026, 11, 6)
        val monday = date(2026, 11, 2)
        val historical = AgentStatisticsSampleData.forRange(StatisticsDateRange(monday, monday), today)
        assertEquals(12L, historical.metrics[0].planProgress!!.single().percent)

        val month = StatisticsDates.range(StatisticsPeriod.Month, today, StatisticsDateRange(today, today))
        val monthly = AgentStatisticsSampleData.forRange(month, today)
        assertEquals(listOf(60L, 0L, 0L, 0L), monthly.metrics[0].planProgress!!.map { it.percent })
        assertEquals(40L, monthly.metrics[2].planProgress!!.single().percent)
    }

    @Test fun samplesIncludeDailyCustomOverTargetAndUnassignedPlans() {
        val today = date(2026, 11, 6)
        fun plansOn(day: Long) =
            AgentStatisticsSampleData.forRange(StatisticsDateRange(day, day), today).metrics[0].planProgress!!

        val daily = plansOn(anchor).single()
        assertEquals(StatisticsPeriod.Day, daily.plan.period)
        assertEquals(120L, daily.percent)
        val custom = plansOn(date(2026, 10, 28)).single()
        assertEquals(StatisticsPeriod.Range, custom.plan.period)
        assertEquals(84L, custom.percent)
        assertTrue(plansOn(date(2026, 10, 29)).isEmpty())
    }

    @Test(expected = IllegalArgumentException::class)
    fun invertedRangeIsRejected() { StatisticsDateRange(anchor, anchor - StatisticsDates.dayMillis) }
}
