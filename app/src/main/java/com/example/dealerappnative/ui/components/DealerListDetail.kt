package com.example.dealerappnative.ui.components

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.navigation.LocalDealerSplitWidth
import com.example.dealerappnative.navigation.LocalDealerWindowLayout
import com.example.dealerappnative.ui.theme.DealerDesign

/** A selected document uses the same detail content when the window changes size. */
@Composable
internal fun DealerListDetail(
    hasSelection: Boolean,
    @DrawableRes emptyIcon: Int,
    emptyText: String,
    list: @Composable () -> Unit,
    detail: @Composable () -> Unit
) {
    val split = LocalDealerWindowLayout.current.hasPanes
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val paneWidth = maxWidth
        val maxListWidth = (paneWidth - SplitHandleWidth - 344.dp).coerceAtLeast(0.dp)
        val minListWidth = minOf(280.dp, maxListWidth)
        val splitWidth = if (split) LocalDealerSplitWidth.current else null
        val listWidth = splitWidth?.let { (paneWidth * it.fraction).coerceIn(minListWidth, maxListWidth) }
        Row(Modifier.fillMaxSize()) {
            if (split || !hasSelection) {
                Box(Modifier.then(if (listWidth != null) Modifier.width(listWidth).clipToBounds() else Modifier.weight(1f))
                    .fillMaxHeight()) {
                    list()
                }
            }
            if (listWidth != null && paneWidth > 0.dp) {
                val minFraction = minListWidth / paneWidth
                val maxFraction = maxListWidth / paneWidth
                DealerSplitHandle(
                    fraction = listWidth / paneWidth,
                    range = minFraction..maxFraction,
                    paneWidth = paneWidth,
                    onResize = { delta ->
                        val current = splitWidth.fraction.coerceIn(minFraction, maxFraction)
                        splitWidth.resize((current + delta).coerceIn(minFraction, maxFraction))
                    },
                    onSet = {
                        splitWidth.resize(it)
                        splitWidth.save()
                    },
                    onDragFinished = splitWidth::save
                )
            }
            if (split || hasSelection) {
                Box(
                    Modifier.weight(1f).fillMaxHeight().background(DealerDesign.background)
                        .then(if (split) Modifier.clipToBounds() else Modifier),
                    contentAlignment = Alignment.TopCenter
                ) {
                    if (hasSelection) {
                        Box(Modifier.widthIn(max = 720.dp).fillMaxSize()) { detail() }
                    } else {
                        Column(
                            Modifier.align(Alignment.Center).padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DealerIcon(emptyIcon, DealerDesign.secondary, 32.dp)
                            Text(emptyText, style = DealerDesign.detail, color = DealerDesign.secondary)
                        }
                    }
                }
            }
        }
    }
}

private val SplitHandleWidth = 28.dp

@Composable
private fun DealerSplitHandle(
    fraction: Float,
    range: ClosedFloatingPointRange<Float>,
    paneWidth: Dp,
    onResize: (Float) -> Unit,
    onSet: (Float) -> Unit,
    onDragFinished: () -> Unit
) {
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val widthPx = with(LocalDensity.current) { paneWidth.toPx() }
    val dragState = rememberDraggableState { onResize(it / widthPx) }
    val interactions = remember { MutableInteractionSource() }
    val dragging by interactions.collectIsDraggedAsState()
    val focused by interactions.collectIsFocusedAsState()
    val active = dragging || focused
    val adjustable = range.start < range.endInclusive
    Box(
        Modifier.width(SplitHandleWidth).fillMaxHeight().background(DealerDesign.background)
            .semantics {
                contentDescription = "Ширина списка"
                progressBarRangeInfo = ProgressBarRangeInfo(fraction, range)
                if (adjustable) {
                    setProgress { target ->
                        val value = target.coerceIn(range)
                        if (value == fraction) return@setProgress false
                        onSet(value)
                        true
                    }
                }
            }
            .onKeyEvent { event ->
                if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
                val direction = when (event.key) {
                    Key.DirectionLeft -> if (rtl) 1 else -1
                    Key.DirectionRight -> if (rtl) -1 else 1
                    else -> return@onKeyEvent false
                }
                onSet((fraction + direction * (24.dp / paneWidth)).coerceIn(range))
                true
            }
            .focusable(enabled = adjustable, interactionSource = interactions)
            .draggable(
                state = dragState,
                orientation = Orientation.Horizontal,
                enabled = adjustable,
                interactionSource = interactions,
                reverseDirection = rtl,
                onDragStopped = { onDragFinished() }
            ),
        contentAlignment = Alignment.Center
    ) {
        VerticalDivider(
            color = if (active) DealerDesign.accent.copy(alpha = .45f) else DealerDesign.divider
        )
        Column(
            Modifier.width(16.dp).height(40.dp).background(DealerDesign.surface, CircleShape),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp, Alignment.CenterVertically)
        ) {
            repeat(3) {
                Box(Modifier.size(3.dp).background(
                    if (active) DealerDesign.accent else DealerDesign.secondary, CircleShape
                ))
            }
        }
    }
}

@Composable
internal fun Modifier.dealerSelectedCard(isSelected: Boolean): Modifier {
    if (!isSelected) return this
    return then(
        Modifier.border(1.dp, DealerDesign.accent, RoundedCornerShape(DealerDesign.cardRadius))
            .semantics { selected = true }
    )
}

/** Read-only details for Admin documents backed by the existing summary data. */
@Composable
internal fun DealerDocumentDetails(
    documentId: String,
    title: String,
    fields: List<Pair<String, String>>,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val scroll = key(documentId) { rememberScrollState() }
    Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
        ChildScreenHeader(title, onBack)
        Column(
            Modifier.fillMaxWidth().verticalScroll(scroll).padding(DealerDesign.pageInset),
            verticalArrangement = Arrangement.spacedBy(DealerDesign.sectionGap)
        ) {
            fields.forEach { (label, value) ->
                Column(
                    Modifier.fillMaxWidth().cardShadow().background(DealerDesign.surface)
                        .padding(DealerDesign.pageInset),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(label, style = DealerDesign.detail, color = DealerDesign.secondary)
                    Text(value, style = DealerDesign.body, color = DealerDesign.text)
                }
            }
        }
    }
}
