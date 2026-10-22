package com.example.dealerappnative.model

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

internal enum class StatisticsPeriod(val label: String) {
    Day("День"), Week("Неделя"), Month("Месяц"), Range("Период")
}

/** Date-only values use UTC midnight, the same convention as Android's calendar picker. */
internal data class StatisticsDateRange(val start: Long, val end: Long) {
    init { require(end >= start) }
}

internal object StatisticsDates {
    const val dayMillis = 86_400_000L
    private val utc = TimeZone.getTimeZone("UTC")
    private val tashkent = TimeZone.getTimeZone("Asia/Tashkent")
    private val ru = Locale.forLanguageTag("ru")

    fun day(year: Int, month: Int, date: Int): Long = Calendar.getInstance(utc).apply {
        clear(); set(year, month - 1, date)
    }.timeInMillis

    fun today(now: Long = System.currentTimeMillis()): Long {
        val local = Calendar.getInstance(tashkent).apply { timeInMillis = now }
        return day(local.get(Calendar.YEAR), local.get(Calendar.MONTH) + 1, local.get(Calendar.DAY_OF_MONTH))
    }

    fun range(period: StatisticsPeriod, anchor: Long, custom: StatisticsDateRange): StatisticsDateRange {
        val date = Calendar.getInstance(utc).apply { timeInMillis = anchor }
        return when (period) {
            StatisticsPeriod.Day -> StatisticsDateRange(anchor, anchor)
            StatisticsPeriod.Week -> {
                val backToMonday = (date.get(Calendar.DAY_OF_WEEK) + 5) % 7
                val start = anchor - backToMonday * dayMillis
                StatisticsDateRange(start, start + 6 * dayMillis)
            }
            StatisticsPeriod.Month -> {
                val start = day(date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, 1)
                StatisticsDateRange(start, start + (date.getActualMaximum(Calendar.DAY_OF_MONTH) - 1) * dayMillis)
            }
            StatisticsPeriod.Range -> custom
        }
    }

    fun label(range: StatisticsDateRange, period: StatisticsPeriod): String {
        fun format(pattern: String, value: Long) = SimpleDateFormat(pattern, ru).apply { timeZone = utc }.format(value)
        if (period == StatisticsPeriod.Day || range.start == range.end) return format("d MMMM yyyy", range.start)
        if (period == StatisticsPeriod.Month) return format("LLLL yyyy", range.start).replaceFirstChar { it.titlecase(ru) }
        val first = Calendar.getInstance(utc).apply { timeInMillis = range.start }
        val last = Calendar.getInstance(utc).apply { timeInMillis = range.end }
        return when {
            first.get(Calendar.YEAR) != last.get(Calendar.YEAR) ->
                "${format("d MMM yyyy", range.start)} — ${format("d MMM yyyy", range.end)}"
            first.get(Calendar.MONTH) == last.get(Calendar.MONTH) ->
                "${format("d", range.start)}–${format("d MMMM yyyy", range.end)}"
            else -> "${format("d MMM", range.start)} — ${format("d MMM yyyy", range.end)}"
        }
    }
}
