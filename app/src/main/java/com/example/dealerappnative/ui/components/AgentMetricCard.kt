package com.example.dealerappnative.ui.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.AgentHomeMetric
import com.example.dealerappnative.model.StatisticsDates
import com.example.dealerappnative.model.StatisticsPlanProgress
import com.example.dealerappnative.ui.theme.AgentDesign

@Composable
internal fun AgentMetricCard(metric: AgentHomeMetric, modifier: Modifier = Modifier) {
    Column(modifier.cardShadow().background(Color.White).padding(12.dp)) {
        Row(Modifier.fillMaxWidth().height(22.dp), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top) {
            DealerIcon(metric.icon, AgentDesign.secondary, AgentDesign.iconSize)
            if (metric.monthlyPlan == null) {
                DealerIcon(R.drawable.ic_agent_chevron, AgentDesign.secondary, 18.dp)
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(metric.title, style = AgentDesign.label, color = AgentDesign.secondary,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(2.dp))
        Text(metric.value, style = AgentDesign.value,
            color = if (metric.negative) AgentDesign.negative else AgentDesign.text,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
        metric.progress?.let { progress ->
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AgentProgress(progress, Modifier.weight(1f), height = 4.dp)
                Text("${(progress * 100).toInt()}%", style = AgentDesign.detail, color = AgentDesign.text)
            }
        }
        metric.detail?.let { detail ->
            Spacer(Modifier.height(4.dp))
            Text(detail, style = AgentDesign.detail, color = AgentDesign.secondary,
                maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
        metric.monthlyPlan?.let { MonthlyPlanSummary(it) }
    }
}

@Composable
private fun MonthlyPlanSummary(progress: StatisticsPlanProgress) {
    Spacer(Modifier.height(8.dp))
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text("План · ${StatisticsDates.monthName(progress.monthStart)}",
            style = AgentDesign.detail, color = AgentDesign.secondary,
            modifier = Modifier.weight(1f), maxLines = 2, overflow = TextOverflow.Ellipsis)
        Text("${progress.percent}%", style = AgentDesign.planPercent, color = AgentDesign.accent)
    }
    Spacer(Modifier.height(4.dp))
    AgentProgress(progress.fraction, height = 4.dp)
}
