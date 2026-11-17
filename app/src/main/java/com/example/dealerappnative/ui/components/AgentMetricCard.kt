package com.example.dealerappnative.ui.components

import com.example.dealerappnative.navigation.LocalDealerWindowLayout
import androidx.compose.ui.graphics.Color
import com.example.dealerappnative.ui.theme.dealerColor
import com.example.dealerappnative.ui.theme.DealerDesign
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.AgentHomeMetric
import com.example.dealerappnative.model.StatisticsDates
import com.example.dealerappnative.model.StatisticsPlanProgress
import com.example.dealerappnative.ui.theme.AgentDesign

@Composable
internal fun AgentMetricCard(metric: AgentHomeMetric, modifier: Modifier = Modifier) {
    val tablet = LocalDealerWindowLayout.current.hasRail
    Column(modifier.then(if (tablet) Modifier.tabletCard() else Modifier.cardShadow().background(DealerDesign.surface)).padding(12.dp)) {
        if (tablet) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                DealerIcon(metric.icon, AgentDesign.secondary, 17.dp)
                Text(metric.title, style = AgentDesign.menuTitle, color = AgentDesign.text,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        } else {
            Row(Modifier.fillMaxWidth().height(22.dp), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top) {
                DealerIcon(metric.icon, AgentDesign.secondary, AgentDesign.iconSize)
                if (metric.monthlyPlan == null) DealerIcon(R.drawable.ic_agent_chevron, AgentDesign.secondary, 18.dp)
            }
            Spacer(Modifier.height(4.dp))
            Text(metric.title, style = AgentDesign.label, color = AgentDesign.secondary,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(Modifier.height(2.dp))
        Text(metric.value, style = AgentDesign.value,
            color = if (metric.negative) AgentDesign.negative else AgentDesign.text,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
        metric.progress?.let { progress ->
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AgentProgress(progress, Modifier.weight(1f), height = 4.dp)
                SlidingNumber((progress * 100).toLong(), AgentDesign.detail, AgentDesign.text, suffix = "%")
            }
        }
        metric.detail?.let { detail ->
            Spacer(Modifier.height(4.dp))
            Text(detail, style = AgentDesign.detail, color = AgentDesign.secondary,
                maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        metric.monthlyPlan?.let { MonthlyPlanSummary(it, tablet) }
    }
}

@Composable
private fun MonthlyPlanSummary(progress: StatisticsPlanProgress, tablet: Boolean) {
    Spacer(Modifier.height(8.dp))
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text("План · ${StatisticsDates.monthName(progress.monthStart)}",
            style = AgentDesign.detail, color = AgentDesign.secondary,
            modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis)
        val color = if (tablet && progress.percent >= 100) dealerColor(Color(0xFF18754C), Color(0xFF91DBB2)) else AgentDesign.accent
        SlidingNumber(progress.percent.toLong(), AgentDesign.planPercent, color, suffix = "%")
    }
    Spacer(Modifier.height(4.dp))
    AgentProgress(progress.fraction, height = 4.dp)
}
