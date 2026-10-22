package com.example.dealerappnative.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity

/** Hiding navigation must not grow/reflow the outgoing list while shared elements are moving. */
@Composable
internal fun VisitListViewport(active: Boolean, content: @Composable () -> Unit) {
    var heightPx by rememberSaveable { mutableIntStateOf(0) }
    val savedHeight = with(LocalDensity.current) { heightPx.toDp() }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
        Box(Modifier.fillMaxWidth()
            .then(if (!active && heightPx > 0) Modifier.requiredHeight(savedHeight) else Modifier.fillMaxHeight())
            .onSizeChanged { if (active) heightPx = it.height }, contentAlignment = Alignment.TopStart) {
            content()
        }
    }
}
