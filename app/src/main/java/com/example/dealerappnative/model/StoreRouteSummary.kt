package com.example.dealerappnative.model

import java.util.Locale
import kotlin.math.ceil
import kotlin.math.roundToInt

internal data class StoreRouteSummary(val distanceMeters: Double, val durationSeconds: Double) {
    val distanceText: String get() = if (distanceMeters < 1000) "${distanceMeters.roundToInt()} м"
        else String.format(Locale("ru"), "%.1f км", distanceMeters / 1000)
    val durationText: String get() {
        val minutes = ceil(durationSeconds / 60).toInt().coerceAtLeast(1)
        return if (minutes < 60) "≈ $minutes мин" else "≈ ${minutes / 60} ч ${minutes % 60} мин"
    }
}
