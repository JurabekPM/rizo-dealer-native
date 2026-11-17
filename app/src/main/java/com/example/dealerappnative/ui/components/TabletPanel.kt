package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.ui.theme.dealerColor

internal object TabletColors {
    val canvas: Color @Composable get() = dealerColor(Color(0xFFF4F2F8), Color(0xFF17161F))
    val rail: Color @Composable get() = dealerColor(Color(0xFFFCFAFF), Color(0xFF211F2A))
    val panel: Color @Composable get() = dealerColor(Color.White, Color(0xFF231F2C))
    val card: Color @Composable get() = dealerColor(Color(0xFFF7F5FA), Color(0xFF2C2736))
    val raised: Color @Composable get() = dealerColor(Color(0xFFEEEBF4), Color(0xFF352F41))
    val inset: Color @Composable get() = dealerColor(Color(0xFFEFEDF4), Color(0xFF1C1924))
    val edge: Color @Composable get() = dealerColor(Color(0xFFE4DFED), Color(0xFF373140))
    val selected: Color @Composable get() = dealerColor(Color(0xFFEEE6F7), Color(0xFF38304A))
}

@Composable
internal fun Modifier.tabletPanel(color: Color = TabletColors.panel): Modifier {
    val shape = RoundedCornerShape(22.dp)
    return border(1.dp, TabletColors.edge, shape).clip(shape).background(color)
}

@Composable
internal fun Modifier.tabletCard(color: Color = TabletColors.card): Modifier {
    val shape = RoundedCornerShape(16.dp)
    return clip(shape).background(color)
}
