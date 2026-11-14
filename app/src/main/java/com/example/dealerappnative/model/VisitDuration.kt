package com.example.dealerappnative.model

import java.util.Locale

private const val SECONDS_PER_HOUR = 3_600
private const val SECONDS_PER_DAY = 24 * SECONDS_PER_HOUR

internal fun formatVisitElapsed(millis: Long): String {
    val seconds = millis.coerceAtLeast(0) / 1_000
    val days = seconds / SECONDS_PER_DAY
    val hours = seconds / SECONDS_PER_HOUR % 24
    val clock = String.format(Locale.ROOT, "%02d:%02d:%02d", hours, seconds / 60 % 60, seconds % 60)
    return if (days > 0) "$days дн. $clock" else clock
}

internal fun formatFinishedVisitDuration(millis: Long): String {
    val seconds = millis.coerceAtLeast(0) / 1_000
    return when {
        seconds >= SECONDS_PER_DAY -> formatVisitElapsed(millis)
        seconds >= SECONDS_PER_HOUR -> "${seconds / SECONDS_PER_HOUR} ч ${seconds / 60 % 60} мин"
        seconds >= 60 -> "${seconds / 60} мин"
        else -> "$seconds сек"
    }
}
