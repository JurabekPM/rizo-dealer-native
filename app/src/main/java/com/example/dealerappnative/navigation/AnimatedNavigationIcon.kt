package com.example.dealerappnative.navigation

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.ui.components.UnreadNavigationIcon
import com.example.dealerappnative.ui.theme.DealerDesign
import kotlin.math.PI
import kotlin.math.sin

private val IndicatorWidth = 54.dp
private val IndicatorHeight = 30.dp

// Each motion uses the original glyph and returns it to its original bounds.
internal enum class NavigationIconMotion(
    val scaleXAtPeak: Float,
    val scaleYAtPeak: Float,
    val angleAtPeak: Float = 0f,
    val liftAtPeak: Float = 0f,
    val travelAtPeak: Float = 0f
) {
    Route(.88f, 1.04f, angleAtPeak = 7f, travelAtPeak = 2.5f),
    Lift(1.06f, .78f, liftAtPeak = 2f),
    Turn(.73f, 1.03f, angleAtPeak = -13f),
    Squeeze(.67f, 1.02f)
}

@Composable
internal fun AnimatedNavigationIcon(
    @DrawableRes iconRes: Int,
    selected: Boolean,
    inactiveTint: Color,
    unreadCount: Int,
    motion: NavigationIconMotion
) {
    val accent = DealerDesign.accent
    val pillColor = lerp(DealerDesign.surface, accent, .14f)
    val transition = updateTransition(selected, label = "Navigation selection")
    val selection = transition.animateFloat(
        transitionSpec = { tween(if (targetState) 240 else 170, easing = FastOutSlowInEasing) },
        label = "Navigation indicator"
    ) { if (it) 1f else 0f }

    Box(
        modifier = Modifier.fillMaxWidth().height(DealerDesign.iconSize).drawBehind {
            val progress = selection.value.coerceIn(0f, 1f)
            if (progress > 0f) {
                val height = IndicatorHeight.toPx()
                val maxWidth = minOf(IndicatorWidth.toPx(), size.width)
                val width = height + (maxWidth - height) * progress
                drawRoundRect(
                    color = pillColor.copy(alpha = progress),
                    topLeft = Offset((size.width - width) / 2f, (size.height - height) / 2f),
                    size = Size(width, height),
                    cornerRadius = CornerRadius(height / 2f)
                )
            }
        },
        contentAlignment = Alignment.Center
    ) {
        UnreadNavigationIcon(
            iconRes = iconRes,
            tint = lerp(inactiveTint, accent, selection.value.coerceIn(0f, 1f)),
            unreadCount = unreadCount,
            iconModifier = Modifier.graphicsLayer {
                // Both settled states use the same bounds, including their badge anchor.
                val progress = selection.value.coerceIn(0f, 1f)
                val pulse = sin(PI * progress).toFloat()
                scaleX = 1f + (motion.scaleXAtPeak - 1f) * pulse
                scaleY = 1f + (motion.scaleYAtPeak - 1f) * pulse
                rotationZ = motion.angleAtPeak * pulse
                translationX = motion.travelAtPeak.dp.toPx() * pulse
                translationY = -motion.liftAtPeak.dp.toPx() * pulse
            }
        )
    }
}
