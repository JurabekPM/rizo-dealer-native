package com.example.dealerappnative.model

import java.util.Locale

/** Elapsed time, not wall-clock time: hours keep increasing after midnight/24 hours. */
internal fun formatVisitElapsed(millis: Long): String {
    val seconds = millis.coerceAtLeast(0) / 1000
    return String.format(Locale.ROOT, "%02d:%02d:%02d", seconds / 3600, seconds / 60 % 60, seconds % 60)
}
