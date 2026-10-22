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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.*
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

/** Menu -> Returns owns its monthly archive, creation and reason settings. */
@Composable
internal fun AdminReturnsScreen(onBack: () -> Unit) {
    val returns = LocalVisitReturns.current
    val documents = adminReturnDocuments(returns.records)
    var monthKey by rememberSaveable { mutableStateOf<String?>(null) }
    var documentKey by rememberSaveable { mutableStateOf<String?>(null) }
    var reasonsVisible by rememberSaveable { mutableStateOf(false) }
    var createVisible by rememberSaveable { mutableStateOf(false) }
    val monthsState = rememberLazyListState()
    val documentsState = rememberLazyListState()
    val months = (historyPlanMonths(emptyList()) + documents.map { it.month }).distinct().sortedByDescending { it.key }
    val month = months.find { it.key == monthKey }
    val selected = documents.find { it.key == documentKey }
    val back: () -> Unit = {
        when {
            reasonsVisible -> reasonsVisible = false
            createVisible -> createVisible = false
            documentKey != null -> documentKey = null
            monthKey != null -> monthKey = null
            else -> onBack()
        }
    }
    BackHandler(onBack = back)
    when {
        reasonsVisible -> ReturnReasonSettingsScreen(back)
        createVisible -> AdminReturnCreateScreen(onBack = back)
        selected != null -> AdminReturnDetailScreen(selected, back)
        else -> Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
            HistoryHeader(month?.label ?: "Возвраты", back)
            LazyColumn(Modifier.weight(1f), state = if (month == null) monthsState else documentsState,
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (month == null) {
                    item(key = "reasons") {
                        HistorySurface(onClick = { reasonsVisible = true }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                DealerIcon(R.drawable.ic_settings); Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Причины возврата", style = AgentVisitDesign.emphasis, color = DealerDesign.text)
                                    Text("Настроить причины для агентов", style = AgentVisitDesign.detail, color = DealerDesign.secondary)
                                }
                                DealerIcon(R.drawable.ic_chevron, size = 16.dp)
                            }
                        }
                    }
                    items(months, key = { it.key }) { item ->
                        val entries = adminReturnsInMonth(documents, item)
                        HistoryMonthCard(item, R.drawable.ic_return, "Сумма возвратов",
                            listOf(historyMoney(entries.sumOf { it.record.total }) to DealerDesign.text)) {
                            documentsState.requestScrollToItem(0); monthKey = item.key
                        }
                    }
                } else {
                    val entries = adminReturnsInMonth(documents, month)
                    items(entries, key = { it.key }) { document ->
                        AdminReturnCard(document) { documentKey = document.key }
                    }
                    if (entries.isEmpty()) item { HistoryEmpty("За этот месяц возвратов нет") }
                }
            }
            Box(Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
                ReturnPrimaryButton("Создать") { createVisible = true }
            }
        }
    }
}

@Composable
private fun AdminReturnCard(document: AdminReturnDocument, onClick: () -> Unit) {
    HistorySurface(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            DealerIcon(R.drawable.ic_store); Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(document.visit.name, style = AgentVisitDesign.emphasis, color = DealerDesign.text,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(document.record.documentId.removePrefix("RN-"), style = AgentVisitDesign.detail, color = DealerDesign.secondary)
            }
        }
        HorizontalDivider(color = DealerDesign.divider, modifier = Modifier.padding(vertical = 12.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(historyDate(document.record.createdAt), style = AgentVisitDesign.detail, color = DealerDesign.secondary,
                modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.width(8.dp))
            Text("${document.record.items.sumOf { it.quantity }} шт", style = AgentVisitDesign.detail, color = DealerDesign.secondary)
            Spacer(Modifier.width(8.dp)); DealerIcon(R.drawable.ic_chevron, size = 16.dp)
        }
    }
}
