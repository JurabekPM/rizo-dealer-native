package com.example.dealerappnative.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.DealerDesign

/** Separate visit archive and return documents, backed by the recorded sessions. */
@Composable
internal fun AgentHistoryArchiveScreen(section: HistorySection, entries: List<AgentHistoryEntry>, onBack: () -> Unit) {
    var monthKey by rememberSaveable(section) { mutableStateOf<String?>(null) }
    val months = (historyPlanMonths(entries) + entries.mapNotNull { it.returnRecord?.let { record -> HistoryMonth.at(record.createdAt) } })
        .distinct().sortedByDescending { it.key }
    val month = months.find { it.key == monthKey }
    val monthsState = rememberLazyListState()
    var selectedId by rememberSaveable(section) { mutableStateOf<String?>(null) }
    var showFilter by rememberSaveable(section) { mutableStateOf(false) }
    var filter by rememberSaveable(section, stateSaver = agentHistoryFilterSaver) { mutableStateOf(AgentHistoryFilter()) }
    val listState = rememberLazyListState()
    val visible = entries.filter { filter.matches(it) &&
        (section == HistorySection.Visits || (VisitResult.Return in it.session.results && HistoryMonth.at(it.returnRecord?.createdAt ?: it.session.startedAt) == month)) }
        .sortedByDescending { it.session.startedAt }
    val selected = entries.find { it.visit.id == selectedId }
    val back: () -> Unit = {
        when {
            selectedId != null -> selectedId = null
            monthKey != null -> monthKey = null
            else -> onBack()
        }
    }
    BackHandler(onBack = back)
    if (selected != null) {
        if (section == HistorySection.Visits) AgentHistoryVisitDetailScreen(selected, back)
        else historyOperations(selected).find { it.result == VisitResult.Return }?.let {
            AgentHistoryOperationScreen(selected, it, back)
        }
    } else if (section == HistorySection.Returns && month == null) Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
        HistoryHeader(section.title, back)
        LazyColumn(Modifier.weight(1f), state = monthsState,
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(months, key = { it.key }) { item ->
                HistoryMonthCard(item, R.drawable.ic_return, "Сумма возвратов",
                    listOf(historyMoney(historyReturnTotal(item, entries)) to DealerDesign.text)) { monthKey = item.key }
            }
        }
    } else Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
        HistoryHeader(month?.label ?: section.title, back) {
            SoftIconButton(onClick = { showFilter = true }, modifier = Modifier.size(48.dp)
                .semantics { contentDescription = "Фильтр: ${section.title}" }) {
                DealerIcon(R.drawable.ic_collection_filter, if (filter.active) DealerDesign.accent else DealerDesign.text)
            }
        }
        LazyColumn(Modifier.weight(1f), state = listState,
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(visible, key = { it.visit.id }) { entry ->
                if (section == HistorySection.Visits) AgentHistoryCard(entry, showAddress = true) { selectedId = entry.visit.id }
                else HistoryDocumentCard(entry, historyMoney(historyOperations(entry).first { it.result == VisitResult.Return }.amount)) {
                    selectedId = entry.visit.id
                }
            }
            if (visible.isEmpty()) item { HistoryEmpty("Ничего не найдено") }
        }
    }
    if (showFilter) AgentHistoryFilterSheet(filter, agentVisitCards, onDismiss = { showFilter = false },
        onApply = { filter = it; showFilter = false; listState.requestScrollToItem(0) })
}
