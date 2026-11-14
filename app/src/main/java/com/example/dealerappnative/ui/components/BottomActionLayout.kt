package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.dealerColor

private val actionPanelShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)

@Composable
internal fun BottomActionPanel(
    spacing: Dp = 0.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val shadow = dealerColor(Color.Black.copy(alpha = .10f), Color.Black.copy(alpha = .28f))
    Column(
        Modifier.fillMaxWidth()
            .dropShadow(actionPanelShape, Shadow(radius = 12.dp, color = shadow, offset = DpOffset(0.dp, (-2).dp)))
            .background(DealerDesign.surface, actionPanelShape)
            .padding(DealerDesign.pageInset),
        verticalArrangement = Arrangement.spacedBy(spacing),
        content = content
    )
}

/** Measure the action first so scrollable content can clear its actual height, including larger text. */
@Composable
internal fun BottomActionLayout(
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit,
    content: @Composable (actionHeight: Dp) -> Unit
) {
    SubcomposeLayout(modifier) { constraints ->
        val panel = subcompose("actions") { Box(Modifier.fillMaxWidth()) { actions() } }
            .single().measure(constraints.copy(minHeight = 0))
        val actionHeight = panel.height.toDp()
        val body = subcompose("content") {
            Box(propagateMinConstraints = true) { content(actionHeight) }
        }.single().measure(constraints)
        layout(body.width, body.height) {
            body.placeRelative(0, 0)
            panel.placeRelative(0, body.height - panel.height)
        }
    }
}
