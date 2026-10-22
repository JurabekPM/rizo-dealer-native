package com.example.dealerappnative.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.ui.theme.DealerDesign
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
internal fun ActiveHistoryTabs(
    pagerState: PagerState,
    activeCount: Int,
    badgeBackground: Color = Color(0xFFF5E5FF),
    badgeTextColor: Color = DealerDesign.accent,
    inactiveTextColor: Color = Color(0xFF4D4D4D)
) {
    val coroutineScope = rememberCoroutineScope()

    BoxWithConstraints(
        Modifier.fillMaxWidth().height(44.dp).clip(RoundedCornerShape(DealerDesign.radius))
            .background(DealerDesign.background).padding(4.dp)
    ) {
        val tabWidth = maxWidth / 2

        // Follow the live pager position during both finger drags and tab-click animations.
        Box(
            Modifier.offset {
                val pagePosition = (pagerState.currentPage + pagerState.currentPageOffsetFraction)
                    .coerceIn(0f, 1f)
                IntOffset((tabWidth.toPx() * pagePosition).roundToInt(), 0)
            }.width(tabWidth).fillMaxHeight()
                .clip(RoundedCornerShape(DealerDesign.radius)).background(Color.White)
        )
        Row(Modifier.fillMaxSize()) {
            listOf("Активные", "История").forEachIndexed { index, label ->
                val selected = pagerState.currentPage == index
                Row(
                    modifier = Modifier.weight(1f).fillMaxHeight()
                        .clip(RoundedCornerShape(DealerDesign.radius))
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(
                                    page = index,
                                    animationSpec = tween(280, easing = FastOutSlowInEasing)
                                )
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(label, style = DealerDesign.tab,
                        color = if (selected) DealerDesign.text else inactiveTextColor)
                    if (index == 0) {
                        Spacer(Modifier.width(4.dp))
                        Box(
                            Modifier.size(24.dp).clip(CircleShape).background(badgeBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(activeCount.toString(), style = DealerDesign.detail, color = badgeTextColor)
                        }
                    }
                }
            }
        }
    }
}
