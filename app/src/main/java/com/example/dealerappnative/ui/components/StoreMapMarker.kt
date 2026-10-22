package com.example.dealerappnative.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.ui.theme.DealerDesign

/** The ground dot stays on the selected coordinate while only the store badge floats. */
@Composable
internal fun StoreMapMarker(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "Store marker")
    val lift = transition.animateFloat(
        initialValue = 0f, targetValue = 6f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "Gentle lift"
    )
    Box(modifier.size(width = 56.dp, height = 68.dp)) {
        // A soft ellipse has enough drawing space, with no clipped animated layer or hard edge.
        Canvas(Modifier.align(Alignment.BottomCenter).width(52.dp).height(26.dp)) {
            val center = Offset(size.width / 2f, size.height - 8.dp.toPx())
            val progress = lift.value / 6f
            val spread = .98f - progress * .34f
            withTransform({ scale(spread, .34f - progress * .12f, center) }) {
                drawCircle(Brush.radialGradient(
                    listOf(Color.Black.copy(alpha = .42f - progress * .22f),
                        Color.Black.copy(alpha = .20f - progress * .12f), Color.Transparent),
                    center = center, radius = size.width / 2f
                ), radius = size.width / 2f, center = center)
            }
        }
        Box(Modifier.align(Alignment.BottomCenter).size(12.dp).clip(CircleShape).background(Color.White),
            contentAlignment = Alignment.Center) {
            Box(Modifier.size(6.dp).clip(CircleShape).background(DealerDesign.accent))
        }
        Box(Modifier.align(Alignment.TopCenter).graphicsLayer { translationY = -lift.value * density }) {
            DealerIcon(R.drawable.ic_store_map_marker, Color.Unspecified, 56.dp)
        }
    }
}
