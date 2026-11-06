package com.example.dealerappnative.model

internal data class StatisticsPlan(
    val period: StatisticsPeriod,
    val dates: StatisticsDateRange,
    val targetUsd: Int
) {
    init { require(targetUsd > 0) }
}

internal data class StatisticsPlanProgress(val plan: StatisticsPlan, val confirmedUsd: Int) {
    val fraction get() = confirmedUsd.toFloat() / plan.targetUsd
    val percent get() = confirmedUsd * 100L / plan.targetUsd
}

internal fun statisticsPlanProgress(
    plans: List<StatisticsPlan>,
    selected: StatisticsDateRange,
    today: Long,
    confirmedUsd: (StatisticsDateRange) -> Int
): List<StatisticsPlanProgress> = plans
    .filter { it.dates.start <= selected.end && selected.start <= it.dates.end }
    .map { plan ->
        val through = minOf(today, selected.end, plan.dates.end)
        val completed = if (through >= plan.dates.start) {
            confirmedUsd(StatisticsDateRange(plan.dates.start, through))
        } else 0
        StatisticsPlanProgress(plan, completed)
    }
