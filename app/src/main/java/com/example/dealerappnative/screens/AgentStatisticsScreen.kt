package com.example.dealerappnative.screens

import com.example.dealerappnative.ui.components.SoftIconButton
import com.example.dealerappnative.ui.components.softCardPress
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import com.example.dealerappnative.data.AgentStoreDebtData
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.AgentStatisticsSampleData
import com.example.dealerappnative.data.AgentStatisticsSnapshot
import com.example.dealerappnative.model.StatisticsPeriod
import com.example.dealerappnative.model.StatisticsDates
import com.example.dealerappnative.model.StatisticsDateRange
import com.example.dealerappnative.ui.components.StatisticsPeriodFilter
import com.example.dealerappnative.ui.components.StatisticsDatePicker
import androidx.compose.runtime.mutableStateOf
import com.example.dealerappnative.ui.components.AgentMetricCard
import com.example.dealerappnative.ui.components.AgentProgress
import com.example.dealerappnative.ui.components.DealerIcon
import com.example.dealerappnative.ui.theme.AgentDesign
import com.example.dealerappnative.ui.theme.DealerPrototypeTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun AgentStatisticsScreen(onStoreDebtsClick: () -> Unit) {
    var updatedAt by rememberSaveable { mutableLongStateOf(System.currentTimeMillis()) }
    val today = StatisticsDates.today(updatedAt)
    var period by rememberSaveable { mutableStateOf(StatisticsPeriod.Day) }
    var anchor by rememberSaveable { mutableLongStateOf(today) }
    var customStart by rememberSaveable { mutableLongStateOf(today) }
    var customEnd by rememberSaveable { mutableLongStateOf(today) }
    var picking by rememberSaveable { mutableStateOf<StatisticsPeriod?>(null) }
    val range = remember(period, anchor, customStart, customEnd) {
        StatisticsDates.range(period, anchor, StatisticsDateRange(customStart, customEnd))
    }
    val data = remember(range, today) { AgentStatisticsSampleData.forRange(range, today) }
    Column(Modifier.fillMaxSize().background(Color.White)) {
        AgentStatisticsHeader(updatedAt, onRefresh = { updatedAt = System.currentTimeMillis() })
        StatisticsPeriodFilter(picking ?: period, StatisticsDates.label(range, period), onPeriodChange = {
            if (it == StatisticsPeriod.Range) picking = it else period = it
        }, onChooseDates = { picking = period })
        LazyColumn(Modifier.fillMaxWidth().weight(1f).background(AgentDesign.background),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = AgentDesign.inset, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(AgentDesign.gap)) {
            if (!data.hasData) item(key = "empty-period") {
                Text("Нет данных за выбранный период", style = AgentDesign.menuDetail, color = AgentDesign.secondary,
                    modifier = Modifier.padding(vertical = 8.dp))
            }
            item(key = "visits") { AgentVisitsSummary(data) }
            data.metrics.chunked(2).forEachIndexed { index, pair ->
                item(key = "metrics-$index") {
                    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(AgentDesign.gap)) {
                        pair.forEach { metric ->
                            AgentMetricCard(metric, Modifier.weight(1f).fillMaxHeight())
                        }
                    }
                }
            }
            item(key = "store-debt") { AgentStoreDebt(onClick = onStoreDebtsClick) }
        }
    }
    picking?.let { chosen ->
        StatisticsDatePicker(chosen, anchor,
            if (chosen == StatisticsPeriod.Range && period == StatisticsPeriod.Range) StatisticsDateRange(customStart, customEnd) else range,
            today, onDismiss = { picking = null }, onApply = { start, end ->
                if (chosen == StatisticsPeriod.Range) { customStart = start; customEnd = end; anchor = end } else anchor = start
                period = chosen
                picking = null
            })
    }
}

@Composable
private fun AgentStatisticsHeader(updatedAt: Long, onRefresh: () -> Unit) {
    val locale = remember { Locale.forLanguageTag("ru") }
    var refreshCount by rememberSaveable { mutableIntStateOf(0) }
    val rotation by animateFloatAsState(refreshCount * 360f, tween(500), label = "Refresh agent summary")
    val time = remember(updatedAt) { SimpleDateFormat("HH:mm", locale).format(Date(updatedAt)) }
    Box(Modifier.fillMaxWidth().height(56.dp).background(Color.White), contentAlignment = Alignment.Center) {
        Column(Modifier.align(Alignment.CenterStart).padding(start = 16.dp, end = 56.dp)) {
            Text("Статистика", style = AgentDesign.screenTitle, color = AgentDesign.text,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("Обновлено $time", style = AgentDesign.updated, color = AgentDesign.secondary)
        }
        SoftIconButton(onClick = { onRefresh(); refreshCount++ },
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 4.dp).size(40.dp)
                .semantics { contentDescription = "Обновить сводку" }) {
            Box(Modifier.rotate(rotation)) {
                DealerIcon(R.drawable.ic_agent_refresh, AgentDesign.secondary, AgentDesign.iconSize)
            }
        }
    }
}

@Composable
private fun AgentVisitsSummary(data: AgentStatisticsSnapshot) {
    val progress = if (data.plannedVisits > 0) data.completedVisits.toFloat() / data.plannedVisits else 0f
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(AgentDesign.radius))
        .background(Color.White).padding(16.dp)) {
        Row(Modifier.fillMaxWidth().height(40.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).clip(CircleShape).background(AgentDesign.tint), contentAlignment = Alignment.Center) {
                DealerIcon(R.drawable.ic_agent_visit, AgentDesign.accent, AgentDesign.iconSize)
            }
            Spacer(Modifier.width(12.dp))
            Text("Визиты", style = AgentDesign.heroLabel, color = AgentDesign.secondary, modifier = Modifier.weight(1f))
            DealerIcon(R.drawable.ic_agent_chevron, AgentDesign.secondary, 20.dp)
        }
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
            Text("${data.completedVisits} / ${data.plannedVisits}", style = AgentDesign.heroValue,
                color = AgentDesign.text, modifier = Modifier.weight(1f))
            Text("${(progress * 100).toInt()}%", style = AgentDesign.percent, color = AgentDesign.text)
        }
        Spacer(Modifier.height(10.4.dp))
        AgentProgress(progress)
        Spacer(Modifier.height(10.dp))
        Box(Modifier.align(Alignment.End).clip(RoundedCornerShape(8.dp)).background(AgentDesign.tint)
            .padding(horizontal = 8.dp, vertical = 4.dp)) {
            Text("+${data.unplannedVisits} вне плана", style = AgentDesign.badge, color = AgentDesign.accent)
        }
    }
}

@Composable
private fun AgentStoreDebt(onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().softCardPress(onClick = onClick).clip(RoundedCornerShape(AgentDesign.radius)).background(Color.White).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
        DealerIcon(R.drawable.ic_agent_debt, AgentDesign.secondary, AgentDesign.iconSize)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text("Задолженность магазинов", style = AgentDesign.label, color = AgentDesign.secondary)
            Spacer(Modifier.height(4.dp))
            Text("На сегодня · ${AgentStoreDebtData.stores.size} магазинов", style = AgentDesign.detail, color = AgentDesign.secondary)
        }
        Spacer(Modifier.width(8.dp))
        Text(AgentStoreDebtData.homeTotalText, style = AgentDesign.debt, color = AgentDesign.negative,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.width(6.dp))
        DealerIcon(R.drawable.ic_agent_chevron, AgentDesign.secondary, 18.dp)
    }
}

@Preview(name = "Agent home 360", showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun AgentHomePreview() = DealerPrototypeTheme { AgentScreen(onBack = {}) }
