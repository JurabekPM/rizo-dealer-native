package com.example.dealerappnative.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.example.dealerappnative.ui.theme.DealerDesign

/** Uses the original vector resources, without string-based icon lookup. */
@Composable
internal fun DealerIcon(
    @DrawableRes iconRes: Int,
    tint: Color = DealerDesign.secondary,
    size: Dp = DealerDesign.iconSize
) {
    Icon(
        painter = painterResource(iconRes),
        contentDescription = null,
        tint = tint,
        modifier = Modifier.size(size)
    )
}
