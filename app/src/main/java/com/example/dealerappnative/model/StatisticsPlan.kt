package com.example.dealerappnative.model

internal data class StatisticsPlanProgress(
    val monthStart: Long,
    val confirmedUsd: Int,
    val targetUsd: Int
) {
    init { require(targetUsd > 0) }

    val fraction get() = confirmedUsd.toFloat() / targetUsd
    val percent get() = confirmedUsd * 100L / targetUsd
}
