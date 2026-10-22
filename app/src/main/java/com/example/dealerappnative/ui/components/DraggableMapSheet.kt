package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/** Native anchored dragging/fling and nested scrolling. Map pixels do not resize on each drag frame. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DraggableMapSheet(editing: Boolean, sheetContent: @Composable ColumnScope.() -> Unit,
    content: @Composable (Modifier, androidx.compose.ui.unit.Dp) -> Unit) {
    val state = rememberBottomSheetScaffoldState(bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Expanded, skipHiddenState = true))
    var sheetHeight by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    LaunchedEffect(editing) { if (editing) state.bottomSheetState.expand() }
    BottomSheetScaffold(
        modifier = Modifier.fillMaxSize().imePadding(), scaffoldState = state,
        sheetPeekHeight = 112.dp, sheetMaxWidth = androidx.compose.ui.unit.Dp.Unspecified,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetContainerColor = Color.White, sheetShadowElevation = 8.dp,
        sheetSwipeEnabled = !editing,
        sheetDragHandle = {
            Box(Modifier.fillMaxWidth().height(24.dp), contentAlignment = Alignment.TopCenter) {
                Box(Modifier.padding(top = 8.dp).size(32.dp, 4.dp).clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFEBEBEB)))
            }
        },
        sheetContent = { Column(Modifier.onSizeChanged { sheetHeight = it.height }, content = sheetContent) }
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val fullHeight = constraints.maxHeight
            // Read the sheet offset during placement, without recomposing or resizing the WebView.
            val controlsOffset = Modifier.offset {
                val offset = runCatching { state.bottomSheetState.requireOffset() }.getOrDefault(fullHeight.toFloat())
                IntOffset(0, if (editing) 0 else -(fullHeight - offset).coerceIn(0f, fullHeight.toFloat()).roundToInt())
            }
            val editInset = with(density) { (sheetHeight + 24.dp.roundToPx()).toDp() }
            Box(Modifier.fillMaxSize().padding(bottom = if (editing) editInset else 0.dp)) {
                content(controlsOffset, if (editing) 0.dp else editInset)
            }
        }
    }
}
