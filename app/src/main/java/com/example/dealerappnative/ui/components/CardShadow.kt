package com.example.dealerappnative.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.ui.theme.DealerDesign

private val cardShape = RoundedCornerShape(DealerDesign.cardRadius)

internal fun Modifier.cardShadow(shape: Shape = cardShape): Modifier = shadow(
    elevation = 2.dp,
    shape = shape,
    clip = true,
    ambientColor = Color.Black.copy(alpha = .30f),
    spotColor = Color.Black.copy(alpha = .42f)
)
