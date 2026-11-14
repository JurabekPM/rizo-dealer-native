package com.example.dealerappnative.ui.components

import kotlin.math.abs
import kotlin.math.roundToInt

internal class PhotoDismissMotion(releaseVelocity: Float, density: Float) {
    private val velocity = releaseVelocity.coerceIn(-3_200f * density, 3_200f * density)
    val durationMillis = (310f - 105f * (abs(velocity) / (2_200f * density)).coerceAtMost(1f)).roundToInt()

    fun progress(time: Float): Float = time * time * (3f - 2f * time)

    fun verticalOffset(time: Float): Float {
        // Preserve the finger's vertical speed at release. The offset and its speed both vanish at landing.
        val remaining = 1f - time
        return velocity * (durationMillis / 1_000f) * time * remaining * remaining * remaining
    }
}
