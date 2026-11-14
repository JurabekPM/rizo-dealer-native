package com.example.dealerappnative.navigation

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.ui.components.UnreadNavigationIcon
import com.example.dealerappnative.ui.theme.DealerDesign
import kotlinx.coroutines.launch

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
    val turn = if (iconRes == R.drawable.ic_money) Modifier.collectionTurn(selected) else Modifier
    UnreadNavigationIcon(unreadCount) {
        Canvas(Modifier.size(DealerDesign.iconSize).then(turn)) {
            val progress = movement.value.coerceIn(0f, 1f)
            val tintProgress = FastOutSlowInEasing.transform((progress * 2f).coerceAtMost(1f))
            val tint = lerp(inactiveTint, accent, tintProgress)
            with(glyph) { drawIcon(progress, tint, surface, inactiveTint) }
        }
    }
}

@Composable
private fun Modifier.collectionTurn(selected: Boolean): Modifier {
    val angle = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var wasSelected by remember { mutableStateOf(selected) }
    LaunchedEffect(selected) {
        val newlySelected = selected && !wasSelected
        wasSelected = selected
        // An in-flight turn finishes naturally if the user switches tabs again.
        if (newlySelected && !angle.isRunning) {
            scope.launch {
                angle.snapTo(0f)
                angle.animateTo(360f, tween(440, easing = CubicBezierEasing(.4f, 0f, .6f, 1f)))
            }
        }
    }
    return graphicsLayer {
        rotationY = angle.value
        cameraDistance = 56.dp.toPx()
    }
}
