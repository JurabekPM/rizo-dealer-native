package com.example.dealerappnative.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.theme.StatementDesign as Design
import com.example.dealerappnative.ui.theme.DealerDesign
import androidx.compose.ui.graphics.Color

internal fun statementPeriodLabel(from: Long?, to: Long?): String = if (from == null || to == null) "За весь период"
    else StatisticsDates.label(StatisticsDateRange(from, to), StatisticsPeriod.Range)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StoreStatementFilters(initial: StatementFilter, currencies: List<String>, onDismiss: () -> Unit, onApply: (StatementFilter) -> Unit) {
    var typeNames by rememberSaveable { mutableStateOf(initial.types.map { it.name }) }
    var currency by rememberSaveable { mutableStateOf(initial.currency) }
    var from by rememberSaveable { mutableStateOf(initial.from) }
    var to by rememberSaveable { mutableStateOf(initial.to) }
    var dates by rememberSaveable { mutableStateOf(false) }
    val types = StatementType.entries.filter { it != StatementType.All && it.name in typeNames }.toSet()
    val draft = StatementFilter(types, currency, from, to)
    val today = StatisticsDates.today()
    val monthStart = StatisticsDates.range(StatisticsPeriod.Month, today, StatisticsDateRange(today, today)).start
    val thisMonth = from == monthStart && to == today
    val maxHeight = LocalConfiguration.current.screenHeightDp.dp * .88f
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Color.White,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        dragHandle = { Box(Modifier.padding(top = 12.dp, bottom = 8.dp).size(32.dp, 4.dp)
            .background(Design.border, RoundedCornerShape(2.dp))) }) {
        Column(Modifier.fillMaxWidth().heightIn(max = maxHeight)) {
            Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Фильтры", style = Design.title, color = Design.text, modifier = Modifier.weight(1f))
                SoftTextButton(enabled = draft.active, onClick = { typeNames = emptyList(); currency = null; from = null; to = null }) {
                    Text("Сбросить", style = Design.choice, color = if (draft.active) DealerDesign.accent else Design.secondary)
                }
            }
            Column(Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Тип операции", style = Design.name, color = Design.text)
                    StatementType.entries.toList().chunked(2).forEach { pair ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            pair.forEach { item -> StatementChoice(item.label, if (item == StatementType.All) types.isEmpty() else item in types, Modifier.weight(1f)) { typeNames = draft.toggleType(item).types.map { it.name } } }
                        }
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Валюта", style = Design.name, color = Design.text)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatementChoice("Все", currency == null, Modifier.weight(1f)) { currency = null }
                        currencies.forEach { item -> StatementChoice(item, currency == item, Modifier.weight(1f)) { currency = if (currency == item) null else item } }
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Период", style = Design.name, color = Design.text)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatementChoice("Весь период", from == null, Modifier.weight(1f)) { from = null; to = null }
                        StatementChoice("Этот месяц", thisMonth, Modifier.weight(1f)) { from = if (thisMonth) null else monthStart; to = if (thisMonth) null else today }
                    }
                    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Design.surface)
                        .softCardPress { dates = true }.heightIn(min = 48.dp).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        DealerIcon(R.drawable.ic_agent_schedule, Design.secondary, 20.dp)
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text("Указать даты", style = Design.choice, color = if (from != null && !thisMonth) DealerDesign.accent else Design.text)
                            if (from != null) Text(statementPeriodLabel(from, to), style = Design.caption, color = Design.secondary)
                        }
                        DealerIcon(R.drawable.ic_chevron, Design.secondary, 16.dp)
                    }
                }
            }
            HorizontalDivider(color = Design.border)
            Box(Modifier.padding(16.dp).fillMaxWidth().height(48.dp).clip(RoundedCornerShape(12.dp))
                .background(DealerDesign.accent).softCardPress { onApply(draft) }, contentAlignment = Alignment.Center) {
                Text("Применить", style = Design.button, color = Color.White)
            }
        }
    }
    if (dates) AgentHistoryDatePicker(from, to, onDismiss = { dates = false }, onApply = { a, b -> from = a; to = b; dates = false })
}

@Composable
private fun StatementChoice(label: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val background by animateColorAsState(if (selected) Design.selected else Design.surface, tween(200), label = "statement-choice")
    val shape = RoundedCornerShape(12.dp)
    Box(modifier.heightIn(min = 44.dp).clip(shape).background(background)
        .border(1.dp, if (selected) DealerDesign.accent.copy(alpha = .22f) else Color.Transparent, shape)
        .softCardPress(onClick = onClick).semantics { this.selected = selected }
        .padding(horizontal = 10.dp, vertical = 12.dp), contentAlignment = Alignment.Center) {
        Text(label, style = Design.choice, color = if (selected) DealerDesign.accent else Design.text,
            maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}
