package com.example.dealerappnative.data

import com.example.dealerappnative.model.AgentHomeMetric
import com.example.dealerappnative.model.StatisticsDateRange
import com.example.dealerappnative.model.StatisticsDates
import com.example.dealerappnative.model.StatisticsPeriod
import com.example.dealerappnative.model.StatisticsPlan
import com.example.dealerappnative.model.statisticsPlanProgress
import java.text.NumberFormat
import java.util.Locale

internal data class AgentStatisticsSnapshot(val completedVisits: Int, val plannedVisits: Int,
    val unplannedVisits: Int, val metrics: List<AgentHomeMetric>, val hasData: Boolean)

/** Dated local fixtures for the UI prototype; no backend or live financial data. */
internal object AgentStatisticsSampleData {
    private val baseline = StatisticsDates.day(2026, 10, 21)
    private val november = StatisticsDates.day(2026, 11, 1)
    private val novemberMonth = StatisticsDateRange(november, november + 29 * StatisticsDates.dayMillis)
    private val requestPlans = listOf(
        StatisticsPlan(StatisticsPeriod.Day, StatisticsDateRange(baseline, baseline), 500),
        StatisticsPlan(
            StatisticsPeriod.Range,
            StatisticsDateRange(StatisticsDates.day(2026, 10, 22), StatisticsDates.day(2026, 10, 28)),
            5_000
        )
    ) + listOf(2, 9, 16, 23).map { day ->
        val monday = StatisticsDates.day(2026, 11, day)
        StatisticsPlan(StatisticsPeriod.Week, StatisticsDates.range(StatisticsPeriod.Week, monday, novemberMonth), 5_000)
    }
    private val collectionPlans = listOf(StatisticsPlan(StatisticsPeriod.Month, novemberMonth, 20_000))

    private data class Day(val date: Long, val variation: Int) {
        val plannedVisits get() = 20 + variation
        val completedVisits get() = 14 + variation
        val unplannedVisits get() = if (variation == 0) 2 else variation % 3
        val requests get() = 9 + variation % 4
        val requestUsd get() = 1240 + variation * 125
        val deliveries get() = 8 + variation % 3
        val plannedDeliveries get() = 12 + variation % 3
        val deliveryUsd get() = 3860 + variation * 180
        val collectedUsd get() = 2150 + variation * 140
        val cashUsd get() = 300 + variation * 20
        val confirmedRequestUsd = 600
        val confirmedCollectionUsd get() = if (variation == 2) 1_500 else 1_300
        val returns get() = 2 + variation % 2
        val returnUsd get() = 120 + variation * 15
        val revisions get() = 3 + variation % 3
        val activeStores get() = (0 until completedVisits + unplannedVisits).map { index ->
            ((date / StatisticsDates.dayMillis).toInt() * 3 + index).mod(70)
        }
    }

    private fun daysFor(range: StatisticsDateRange, today: Long): Sequence<Day> {
        // Bound calendar work; actuals stop today, while week/month plans include their remaining days.
        val start = maxOf(range.start, today - 364 * StatisticsDates.dayMillis)
        val end = minOf(range.end, today + 31 * StatisticsDates.dayMillis)
        return generateSequence(start) { it + StatisticsDates.dayMillis }
            .takeWhile { it <= end }
            .map { date -> Day(date, ((date - baseline) / StatisticsDates.dayMillis).mod(7).toInt()) }
    }

    fun forRange(range: StatisticsDateRange, today: Long): AgentStatisticsSnapshot {
        val days = daysFor(range, today).toList()
        val actual = days.filter { it.date <= today }
        val format = NumberFormat.getIntegerInstance(Locale.forLanguageTag("ru"))
        fun number(value: Int) = format.format(value)
        fun money(value: Int) = "${number(value)} $"
        fun plural(value: Int, one: String, few: String, many: String): String = when {
            value % 100 in 11..14 -> many
            value % 10 == 1 -> one
            value % 10 in 2..4 -> few
            else -> many
        }
        val requests = actual.sumOf { it.requests }
        val deliveries = actual.sumOf { it.deliveries }
        val plannedDeliveries = days.sumOf { it.plannedDeliveries }
        val collected = actual.sumOf { it.collectedUsd }
        val cash = actual.sumOf { it.cashUsd }
        val returns = actual.sumOf { it.returns }
        val activeStores = actual.flatMap { it.activeStores }.toSet().size
        val values = AgentHomeSampleData.metrics.mapIndexed { index, metric ->
            when (index) {
                0 -> metric.copy(value = money(actual.sumOf { it.requestUsd }),
                    detail = "${number(requests)} ${plural(requests, "заявка", "заявки", "заявок")}",
                    planProgress = statisticsPlanProgress(requestPlans, range, today) { dates ->
                        daysFor(dates, today).sumOf { it.confirmedRequestUsd }
                    })
                1 -> metric.copy(value = money(actual.sumOf { it.deliveryUsd }),
                    detail = "${number(deliveries)} из ${number(plannedDeliveries)} доставок")
                2 -> metric.copy(value = money(collected), detail = "у меня ${money(cash)} · сдано ${money(collected - cash)}",
                    planProgress = statisticsPlanProgress(collectionPlans, range, today) { dates ->
                        daysFor(dates, today).sumOf { it.confirmedCollectionUsd }
                    })
                3 -> metric.copy(value = money(actual.sumOf { it.returnUsd }),
                    detail = "${number(returns)} ${plural(returns, "возврат", "возврата", "возвратов")}")
                4 -> metric.copy(value = number(actual.sumOf { it.revisions }))
                else -> metric.copy(value = "$activeStores / 70", detail = "За выбранный период", progress = activeStores / 70f)
            }
        }
        return AgentStatisticsSnapshot(actual.sumOf { it.completedVisits }, days.sumOf { it.plannedVisits },
            actual.sumOf { it.unplannedVisits }, values, actual.isNotEmpty())
    }
}
