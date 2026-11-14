package com.example.dealerappnative.navigation

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
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
    val art = ImageVector.vectorResource(iconRes)
    val glyph = remember(art) { NavigationIconArtwork(iconRes, art) }
    val transition = updateTransition(selected, label = "Navigation selection")
    val movement = transition.animateFloat(
        transitionSpec = { tween(if (targetState) 320 else 245, easing = LinearEasing) },
        label = "Icon shape"
    ) { if (it) 1f else 0f }
    UnreadNavigationIcon(unreadCount) {
        Canvas(Modifier.size(DealerDesign.iconSize)) {
            val progress = movement.value.coerceIn(0f, 1f)
            val tintProgress = FastOutSlowInEasing.transform((progress * 2f).coerceAtMost(1f))
            val tint = lerp(inactiveTint, accent, tintProgress)
            with(glyph) { drawIcon(progress, tint, surface, inactiveTint) }
        }
    }
}
