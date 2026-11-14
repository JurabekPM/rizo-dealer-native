package com.example.dealerappnative.navigation

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.ui.components.UnreadNavigationIcon
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun AnimatedNavigationIcon(
    @DrawableRes iconRes: Int,
    selected: Boolean,
    inactiveTint: Color,
    unreadCount: Int
) {
    val accent = DealerDesign.accent
    val surface = DealerDesign.surface
    val indicator = lerp(surface, accent, .085f)
    val art = ImageVector.vectorResource(iconRes)
    val glyph = remember(art) { NavigationIconArtwork(iconRes, art) }
    val transition = updateTransition(selected, label = "Navigation selection")
    val movement = transition.animateFloat(
        transitionSpec = { tween(if (targetState) 320 else 245, easing = LinearEasing) },
        label = "Icon shape"
    ) { if (it) 1f else 0f }
    val background = transition.animateFloat(
        transitionSpec = { tween(170, easing = FastOutSlowInEasing) },
        label = "Icon background"
    ) { if (it) 1f else 0f }

    Box(
        modifier = Modifier.fillMaxWidth().height(DealerDesign.iconSize).drawBehind {
            val progress = background.value.coerceIn(0f, 1f)
            if (progress > 0f) {
                val width = minOf(52.dp.toPx(), size.width) * (.76f + .24f * progress)
                val height = 30.dp.toPx()
                drawRoundRect(
                    color = indicator.copy(alpha = progress),
                    topLeft = Offset((size.width - width) / 2f, (size.height - height) / 2f),
                    size = Size(width, height),
                    cornerRadius = CornerRadius(height / 2f)
                )
            }
        },
        contentAlignment = Alignment.Center
    ) {
        UnreadNavigationIcon(unreadCount) {
            Canvas(Modifier.size(DealerDesign.iconSize)) {
                val progress = movement.value.coerceIn(0f, 1f)
                val tint = lerp(inactiveTint, accent, FastOutSlowInEasing.transform((progress * 2).coerceAtMost(1f)))
                val cutout = lerp(surface, indicator, background.value.coerceIn(0f, 1f))
                with(glyph) { drawIcon(progress, tint, cutout) }
            }
        }
    }
}
