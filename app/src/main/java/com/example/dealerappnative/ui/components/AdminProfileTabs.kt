package com.example.dealerappnative.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.ProfileDesign
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/** Same live pager indicator as the app's other tabs; no ripple/selected hover. */
@Composable
internal fun AdminProfileTabs(pagerState: PagerState, enabled: Boolean) {
    val scope = rememberCoroutineScope()
    BoxWithConstraints(Modifier.fillMaxWidth().height(42.dp).clip(RoundedCornerShape(12.dp))
        .background(DealerDesign.background).padding(4.dp)) {
        val width = maxWidth / 2
        Box(Modifier.offset {
            IntOffset((width.toPx() * (pagerState.currentPage + pagerState.currentPageOffsetFraction).coerceIn(0f, 1f)).roundToInt(), 0)
        }.width(width).fillMaxHeight().clip(RoundedCornerShape(9.dp)).background(Color.White))
        Row(Modifier.fillMaxSize().selectableGroup()) {
            listOf("Физические лица", "Юридические лица").forEachIndexed { index, title ->
                Box(Modifier.weight(1f).fillMaxHeight().selectable(selected = pagerState.currentPage == index,
                    enabled = enabled, role = Role.Tab, interactionSource = remember { MutableInteractionSource() }, indication = null,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index, animationSpec = tween(280, easing = FastOutSlowInEasing)) } }),
                    contentAlignment = Alignment.Center) {
                    Text(title, style = ProfileDesign.body, maxLines = 1, overflow = TextOverflow.Ellipsis,
                        color = if (pagerState.currentPage == index) DealerDesign.text else DealerDesign.secondary)
                }
            }
        }
    }
}
