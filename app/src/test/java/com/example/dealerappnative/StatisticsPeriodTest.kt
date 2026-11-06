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

    @Test fun dayWeekAndMonthShowTheSameMonthlyPlanThroughToday() {
        val today = date(2026, 11, 6)
        val day = StatisticsDateRange(today, today)
        val week = StatisticsDates.range(StatisticsPeriod.Week, today, day)
        val month = StatisticsDates.range(StatisticsPeriod.Month, today, day)
        val dayMetrics = AgentStatisticsSampleData.forRange(day, today).metrics
        val weekMetrics = AgentStatisticsSampleData.forRange(week, today).metrics
        val monthMetrics = AgentStatisticsSampleData.forRange(month, today).metrics

        val requests = dayMetrics[0].monthlyPlan!!
        assertEquals(month.start, requests.monthStart)
        assertEquals(3_600, requests.confirmedUsd)
        assertEquals(20L, requests.percent)
        assertEquals(requests, weekMetrics[0].monthlyPlan)
        assertEquals(requests, monthMetrics[0].monthlyPlan)
        assertNotEquals(dayMetrics[0].value, monthMetrics[0].value)

        val collections = dayMetrics[2].monthlyPlan!!
        assertEquals(8_000, collections.confirmedUsd)
        assertEquals(40L, collections.percent)
        assertEquals(collections, weekMetrics[2].monthlyPlan)
        assertEquals(collections, monthMetrics[2].monthlyPlan)
        assertNull(dayMetrics[0].detail)
        assertNull(dayMetrics[2].detail)
        assertNull(dayMetrics[1].monthlyPlan)
    }

    @Test fun pastDayAndCrossMonthSelectionUseTheMonthOfTheCutoff() {
        val today = date(2026, 11, 6)
        val end = date(2026, 11, 2)
        val day = StatisticsDateRange(end, end)
        val crossMonth = StatisticsDateRange(date(2026, 10, 25), end)
        val daily = AgentStatisticsSampleData.forRange(day, today)
        val combined = AgentStatisticsSampleData.forRange(crossMonth, today)

        val requests = daily.metrics[0].monthlyPlan!!
        assertEquals(date(2026, 11, 1), requests.monthStart)
        assertEquals(1_200, requests.confirmedUsd)
        assertEquals(requests, combined.metrics[0].monthlyPlan)
        assertEquals(daily.metrics[2].monthlyPlan, combined.metrics[2].monthlyPlan)
        assertNotEquals(daily.metrics[0].value, combined.metrics[0].value)
    }

    @Test fun monthlyProgressCanExceedTheTarget() {
        val today = date(2026, 12, 1)
        val november = StatisticsDateRange(date(2026, 11, 1), date(2026, 11, 30))
        val progress = AgentStatisticsSampleData.forRange(november, today).metrics[2].monthlyPlan!!
        assertTrue(progress.percent > 100L)
    }

    @Test(expected = IllegalArgumentException::class)
    fun invertedRangeIsRejected() { StatisticsDateRange(anchor, anchor - StatisticsDates.dayMillis) }
}
