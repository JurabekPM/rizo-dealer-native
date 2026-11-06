package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.model.AgentHomeMetric
import com.example.dealerappnative.model.StatisticsDateRange
import com.example.dealerappnative.model.StatisticsDates
import com.example.dealerappnative.model.StatisticsPeriod
import com.example.dealerappnative.ui.theme.AgentDesign
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AgentStatisticsPlanSheet(metric: AgentHomeMetric, through: Long, onDismiss: () -> Unit) {
    val plans = metric.planProgress.orEmpty()
    val date = StatisticsDates.label(StatisticsDateRange(through, through), StatisticsPeriod.Day)
    val number = remember { NumberFormat.getIntegerInstance(Locale.forLanguageTag("ru")) }
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = {
            Box(Modifier.padding(top = 10.dp, bottom = 16.dp).size(36.dp, 4.dp)
                .background(AgentDesign.divider, RoundedCornerShape(2.dp)))
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = AgentDesign.inset, end = AgentDesign.inset, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(metric.title, style = AgentDesign.reportTitle, color = AgentDesign.text)
                Text("Выполнение плана на $date", style = AgentDesign.detail, color = AgentDesign.secondary,
                    modifier = Modifier.padding(top = 4.dp))
            }
            items(plans) { progress ->
                Column(Modifier.fillMaxWidth().background(AgentDesign.background, RoundedCornerShape(AgentDesign.radius))
                    .padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(StatisticsDates.label(progress.plan.dates, progress.plan.period),
                            style = AgentDesign.menuTitle, color = AgentDesign.text, modifier = Modifier.weight(1f))
                        Text("${progress.percent}%", style = AgentDesign.percent, color = AgentDesign.accent)
                    }
                    AgentProgress(progress.fraction, height = 4.dp)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        PlanAmount("Выполнено", "${number.format(progress.confirmedUsd)} $", Modifier.weight(1f))
                        PlanAmount("План", "${number.format(progress.plan.targetUsd)} $", Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun PlanAmount(label: String, amount: String, modifier: Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, style = AgentDesign.detail, color = AgentDesign.secondary)
        Text(amount, style = AgentDesign.menuTitle, color = AgentDesign.text)
    }
}
