package com.example.dealerappnative.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import com.example.dealerappnative.R
import com.example.dealerappnative.model.StatisticsPeriod
import com.example.dealerappnative.ui.theme.AgentDesign

/** Pinned below the Statistics app bar, using the existing Agent typography and segmented control. */
@Composable
internal fun StatisticsPeriodFilter(period: StatisticsPeriod, dateLabel: String,
    onPeriodChange: (StatisticsPeriod) -> Unit, onChooseDates: () -> Unit) {
    // Keep animation state outside the constraints subcomposition, like the other tabs.
    val position by animateFloatAsState(period.ordinal.toFloat(),
        tween(280, easing = FastOutSlowInEasing), label = "Statistics tab indicator")
    Column(Modifier.fillMaxWidth().background(Color.White)) {
        Column(Modifier.padding(horizontal = 16.dp).padding(top = 8.dp, bottom = 4.dp)) {
            BoxWithConstraints(Modifier.fillMaxWidth().height(44.dp).clip(RoundedCornerShape(12.dp))
                .background(AgentDesign.background).padding(4.dp)) {
                val width = maxWidth / StatisticsPeriod.entries.size
                Box(Modifier.offset {
                    IntOffset((width.toPx() * position).roundToInt(), 0)
                }.width(width).fillMaxHeight().clip(RoundedCornerShape(9.dp))
                    .background(Color.White))
                Row(Modifier.fillMaxSize().selectableGroup()) {
                    StatisticsPeriod.entries.forEach { item ->
                        Box(Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(9.dp))
                            .selectable(item == period, role = Role.Tab, interactionSource = remember { MutableInteractionSource() }, indication = null) { onPeriodChange(item) }, contentAlignment = Alignment.Center) {
                            Text(item.label, style = if (item == period) AgentDesign.menuDetail else AgentDesign.label,
                                color = if (item == period) AgentDesign.accent else AgentDesign.secondary)
                        }
                    }
                }
            }
            Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).clip(RoundedCornerShape(12.dp))
                .softCardPress(onClick = onChooseDates)
                .semantics { contentDescription = "Выбрать даты: $dateLabel" }.padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically) {
                DealerIcon(R.drawable.ic_agent_schedule, AgentDesign.accent, 20.dp)
                Spacer(Modifier.width(10.dp))
                Text(dateLabel, style = AgentDesign.menuTitle, color = AgentDesign.text,
                    maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                Box(Modifier.rotate(90f)) { DealerIcon(R.drawable.ic_agent_chevron, AgentDesign.secondary, 18.dp) }
            }
        }
        HorizontalDivider(color = AgentDesign.divider)
    }
}
