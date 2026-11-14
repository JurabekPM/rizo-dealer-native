package com.example.dealerappnative.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import kotlinx.coroutines.delay

private const val DigitDuration = 320
private class PreviousValue(var value: Long)

/** Keys are decimal positions counted from the right, including across carries and borrows. */
@Composable
internal fun SlidingNumber(
    value: Long,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    groupThousands: Boolean = false,
    suffix: String = "",
    accessibleLabel: String? = null
) {
    val number = value.toString()
    val label = if (groupThousands) number.reversed().chunked(3).joinToString(" ").reversed() else number
    val previous = remember { PreviousValue(value) }
    val previousNumber = previous.value.toString()
    val direction = if (value >= previous.value) 1 else -1
    var retainedPlaces by remember { mutableIntStateOf(number.length) }
    val places = maxOf(retainedPlaces, number.length)
    SideEffect { previous.value = value }
    LaunchedEffect(number.length) {
        if (number.length < retainedPlaces) delay(DigitDuration.toLong())
        retainedPlaces = number.length
    }

    val digitStyle = style.copy(fontFeatureSettings = "tnum")
    val measurer = rememberTextMeasurer(cacheSize = 2)
    val density = LocalDensity.current
    val digitWidth = with(density) { measurer.measure("0", digitStyle).size.width.toDp() }
    val gapWidth = if (groupThousands) with(density) {
        measurer.measure("0\u00A0", digitStyle).size.width.toDp() - digitWidth
    } else null
    Row(modifier.clearAndSetSemantics { contentDescription = accessibleLabel ?: (label + suffix) }
        .animateContentSize(tween(220)), verticalAlignment = Alignment.CenterVertically) {
        for (place in places - 1 downTo 0) {
            key(place) {
                val slotWidth = if (gapWidth != null && place > 0 && place % 3 == 0) digitWidth + gapWidth else digitWidth
                val digit = number.getOrNull(number.lastIndex - place)
                val state = remember { MutableTransitionState(previousNumber.getOrNull(previousNumber.lastIndex - place)) }
                SideEffect { state.targetState = digit }
                val transition = rememberTransition(state, label = "decimal-place")
                transition.AnimatedContent(
                    modifier = Modifier.width(slotWidth).clipToBounds(),
                    contentAlignment = Alignment.CenterStart,
                    transitionSpec = {
                        (slideInVertically(tween(DigitDuration, easing = FastOutSlowInEasing)) { it * direction } + fadeIn(tween(220)))
                            .togetherWith(slideOutVertically(tween(DigitDuration, easing = FastOutSlowInEasing)) { -it * direction } + fadeOut(tween(220)))
                            .using(null)
                    }
                ) { digit ->
                    if (digit != null) Text(digit.toString(), style = digitStyle, color = color, maxLines = 1, softWrap = false)
                }
            }
        }
        if (suffix.isNotEmpty()) Text(suffix, style = style, color = color, maxLines = 1, softWrap = false)
    }
}
