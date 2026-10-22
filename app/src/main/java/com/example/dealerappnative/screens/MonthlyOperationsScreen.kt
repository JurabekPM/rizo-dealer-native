package com.example.dealerappnative.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

/** One source for the monthly count and the documents opened from that month. */
@Composable
internal fun MonthlyOperationsScreen(section: HistorySection, entries: List<AgentHistoryEntry>, onBack: () -> Unit, showRevisionBalanceLabel: Boolean = false) {
    val result = requireNotNull(section.result)
    val revision = result == VisitResult.Revision
    var monthKey by rememberSaveable(section) { mutableStateOf<String?>(null) }
    var documentKey by rememberSaveable(section) { mutableStateOf<String?>(null) }
    var showFilter by rememberSaveable(section) { mutableStateOf(false) }
    var filter by rememberSaveable(section, stateSaver = agentHistoryFilterSaver) { mutableStateOf(AgentHistoryFilter()) }
    val months = historyPlanMonths(entries)
    val month = months.find { it.key == monthKey }
    val monthList = rememberLazyListState()
    val state = rememberSaveableStateHolder()
    val selected = entries.find { monthlyDocumentKey(it) == documentKey }
    val back: () -> Unit = {
        when {
            documentKey != null -> documentKey = null
            monthKey != null -> monthKey = null
            else -> onBack()
        }
    }
    BackHandler(onBack = back)
    if (selected != null) {
        historyOperations(selected).firstOrNull { it.result == result }?.let {
            AgentHistoryOperationScreen(selected, it, back)
        }
    } else if (month == null) Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
        HistoryHeader(section.title, back)
        LazyColumn(Modifier.weight(1f), state = monthList, contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(months, key = { it.key }) { item ->
                val documents = historyMonthlyDocuments(result, item, entries)
                val value = if (revision) "${documents.size}" else historyMoney(documents.sumOf { entry ->
                    historyOperations(entry).first { it.result == result }.amount
                })
                HistoryMonthCard(item, if (revision) R.drawable.ic_edit else R.drawable.ic_money,
                    if (revision) "Количество ревизий" else "Получено",
                    listOf(value to DealerDesign.text)) { monthKey = item.key; filter = AgentHistoryFilter() }
            }
        }
    } else state.SaveableStateProvider("${section.name}/${month.key}") {
        val documents = historyMonthlyDocuments(result, month, entries, filter)
        Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
            HistoryHeader(month.label, back) {
                SoftIconButton(onClick = { showFilter = true }, modifier = Modifier.size(48.dp)
                    .semantics { contentDescription = "Фильтр: ${section.title}" }) {
                    DealerIcon(R.drawable.ic_collection_filter, if (filter.active) DealerDesign.accent else DealerDesign.text)
                }
            }
            LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(documents, key = ::monthlyDocumentKey) { entry ->
                    MonthlyOperationCard(entry, revision, showRevisionBalanceLabel) { documentKey = monthlyDocumentKey(entry) }
                }
                if (documents.isEmpty()) item { HistoryEmpty(if (revision) "Ревизии не найдены" else "Инкассации не найдены") }
            }
        }
    }
    if (showFilter) AgentHistoryFilterSheet(filter, entries.map { it.visit }.distinctBy { it.id },
        onDismiss = { showFilter = false }, onApply = { filter = it; showFilter = false })
}

private fun monthlyDocumentKey(entry: AgentHistoryEntry): String = "${entry.visit.id}/${entry.session.startedAt}"

@Composable
private fun MonthlyOperationCard(entry: AgentHistoryEntry, revision: Boolean, showRevisionBalanceLabel: Boolean, onClick: () -> Unit) {
    HistorySurface(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            DealerIcon(R.drawable.ic_store); Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(entry.visit.name, style = AgentVisitDesign.emphasis, color = DealerDesign.text,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(entry.visit.address, style = AgentVisitDesign.detail, color = DealerDesign.secondary,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.width(12.dp)); DealerIcon(R.drawable.ic_chevron, size = 16.dp)
        }
        HorizontalDivider(color = DealerDesign.divider, modifier = Modifier.padding(vertical = 12.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (!revision || showRevisionBalanceLabel) Text(if (revision) "Сальдо магазина" else "Получено",
                style = AgentVisitDesign.detail, color = DealerDesign.secondary, modifier = Modifier.weight(1f))
            else Spacer(Modifier.weight(1f))
            Spacer(Modifier.width(12.dp))
            Text(if (revision) entry.visit.debtDirection.formatAmount(entry.visit.debt) else historyMoney(
                historyOperations(entry).first { it.result == VisitResult.Collection }.amount),
                style = AgentVisitDesign.emphasis, color = if (revision) entry.visit.debtDirection.color else DealerDesign.text,
                maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.widthIn(max = 200.dp))
        }
        Text(historyDate(requireNotNull(entry.session.finishedAt)), style = AgentVisitDesign.detail,
            color = DealerDesign.secondary, modifier = Modifier.padding(top = 8.dp))
    }
}
