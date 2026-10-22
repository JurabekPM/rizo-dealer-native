package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.ui.theme.AgentDesign

@Composable
internal fun AgentProgress(progress: Float, modifier: Modifier = Modifier, height: Dp = 6.dp) {
    val value = progress.coerceIn(0f, 1f)
    Box(modifier.fillMaxWidth().height(height).clip(RoundedCornerShape(height / 2))
        .background(AgentDesign.divider)
        .semantics { progressBarRangeInfo = ProgressBarRangeInfo(value, 0f..1f) }) {
        if (value > 0f) Box(Modifier.fillMaxWidth(value).fillMaxSize()
            .clip(RoundedCornerShape(height / 2))
            .background(if (value >= 1f) Color(0xFF55A980) else AgentDesign.accent))
    }
}
