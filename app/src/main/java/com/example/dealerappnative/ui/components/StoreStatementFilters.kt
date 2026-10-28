package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign as Design
import com.example.dealerappnative.ui.theme.DealerDesign

internal fun statementPeriodLabel(from: Long?, to: Long?): String = if (from == null || to == null) "За весь период"
    else StatisticsDates.label(StatisticsDateRange(from, to), StatisticsPeriod.Range)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StoreStatementFilters(initial: StatementFilter, currencies: List<String>, onDismiss: () -> Unit, onApply: (StatementFilter) -> Unit) {
    var type by rememberSaveable { mutableStateOf(initial.type) }
    var currency by rememberSaveable { mutableStateOf(initial.currency) }
    var from by rememberSaveable { mutableStateOf(initial.from) }
    var to by rememberSaveable { mutableStateOf(initial.to) }
    var dates by rememberSaveable { mutableStateOf(false) }
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White) {
        Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Фильтры", style = Design.sheetTitle)
            Text("Операции", style = Design.name)
            StatementType.entries.toList().chunked(2).forEach { pair ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    pair.forEach { item -> StatementChoice(item.label, type == item, Modifier.weight(1f)) { type = item } }
                }
            }
            Text("Валюта", style = Design.name)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatementChoice("Все", currency == null, Modifier.weight(1f)) { currency = null }
                currencies.forEach { item -> StatementChoice(item, currency == item, Modifier.weight(1f)) { currency = item } }
            }
            Text("Период", style = Design.name)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatementChoice("Весь период", from == null, Modifier.weight(1f)) { from = null; to = null }
                val today = StatisticsDates.today()
                val monthStart = StatisticsDates.range(StatisticsPeriod.Month, today, StatisticsDateRange(today, today)).start
                StatementChoice("Этот месяц", from == monthStart && to == today, Modifier.weight(1f)) {
                    val today = StatisticsDates.today()
                    from = StatisticsDates.range(StatisticsPeriod.Month, today, StatisticsDateRange(today, today)).start
                    to = today
                }
            }
            StatementChoice(statementPeriodLabel(from, to), from != null, Modifier.fillMaxWidth()) { dates = true }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.weight(1f).height(48.dp).clip(RoundedCornerShape(12.dp)).background(DealerDesign.background)
                    .softCardPress { type = StatementType.All; currency = null; from = null; to = null }, contentAlignment = Alignment.Center) {
                    Text("Сбросить", style = Design.button)
                }
                Box(Modifier.weight(1f).height(48.dp).clip(RoundedCornerShape(12.dp)).background(DealerDesign.accent)
                    .softCardPress { onApply(StatementFilter(type, currency, from, to)) }, contentAlignment = Alignment.Center) {
                    Text("Применить", style = Design.button, color = Color.White)
                }
            }
        }
    }
    if (dates) AgentHistoryDatePicker(from, to, onDismiss = { dates = false }, onApply = { a, b -> from = a; to = b; dates = false })
}

@Composable
private fun StatementChoice(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Box(modifier.heightIn(min = 42.dp).clip(RoundedCornerShape(10.dp))
        .background(if (selected) Color(0xFFF3E8FC) else DealerDesign.background).softCardPress(onClick = onClick)
        .padding(horizontal = 10.dp, vertical = 10.dp), contentAlignment = Alignment.Center) {
        Text(label, style = Design.detail, color = if (selected) DealerDesign.accent else DealerDesign.text,
            maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}
