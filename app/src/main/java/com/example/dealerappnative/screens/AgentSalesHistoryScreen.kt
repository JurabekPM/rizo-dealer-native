package com.example.dealerappnative.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

/** Real app flow: month -> monthly totals/stores -> store sales/returns -> document products. */
@Composable
internal fun AgentSalesHistoryScreen(entries: List<AgentHistoryEntry>, onBack: () -> Unit) {
    var monthKey by rememberSaveable { mutableStateOf<String?>(null) }
    var storeId by rememberSaveable { mutableStateOf<String?>(null) }
    var visitId by rememberSaveable { mutableStateOf<String?>(null) }
    var result by rememberSaveable { mutableStateOf<VisitResult?>(null) }
    val states = rememberSaveableStateHolder()
    val months = (historyPlanMonths(entries) + entries.flatMap { entry ->
        listOfNotNull(entry.returnRecord?.createdAt, entry.requestRecord?.createdAt).map(HistoryMonth::at)
    })
        .distinct().sortedByDescending { it.key }
    val month = months.find { it.key == monthKey }
    val stores = month?.let { historySalesStores(it, entries) }.orEmpty()
    val store = stores.find { it.visit.id == storeId }
    val document = store?.documents?.find { it.first.visit.id == visitId && it.second.result == result }
    val back: () -> Unit = {
        when {
            visitId != null -> { visitId = null; result = null }
            storeId != null -> storeId = null
            monthKey != null -> monthKey = null
            else -> onBack()
        }
    }
    BackHandler(onBack = back)
    states.SaveableStateProvider("sales/$monthKey/$storeId/$visitId/$result") {
        if (document != null) AgentHistoryOperationScreen(document.first, document.second, back)
        else Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
            HistoryHeader(month?.label ?: "История продаж", back)
            LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                when {
                    store != null -> {
                        item { HistoryStoreSummary(store.visit) }
                        item { HistorySurface { SalesValues(store.received, store.returned, "Факт", store.net) } }
                        items(store.documents, key = { "${it.first.visit.id}/${it.second.result}" }) { (entry, report) ->
                            HistorySurface(onClick = { visitId = entry.visit.id; result = report.result }) {
                                HistoryValue(if (report.result == VisitResult.Return) "Номер возврата" else "Номер заявки", report.documentId)
                                HistoryValue(if (report.result == VisitResult.Return) "Возвращено" else "Общая сумма",
                                    (if (report.result == VisitResult.Return) "−" else "") + historyMoney(report.amount),
                                    if (report.result == VisitResult.Return) DebtDirection.StoreOwesDealer.color else DealerDesign.text)
                                HistoryValue(if (report.result == VisitResult.Return) "Дата возврата" else "Дата подтверждения",
                                    historyDate(when (report.result) {
                                        VisitResult.Request -> entry.requestRecord?.createdAt
                                        VisitResult.Return -> entry.returnRecord?.createdAt
                                        else -> null
                                    } ?: requireNotNull(entry.session.finishedAt)))
                                HistoryValue("Товары", "Посмотреть (${report.products.size})", DealerDesign.accent)
                            }
                        }
                        if (store.documents.isEmpty()) item { HistoryEmpty("Ничего не найдено") }
                    }
                    month != null -> {
                        item { SalesMonthSummary(month, stores) }
                        val recorded = stores.filter { it.documents.isNotEmpty() }
                        items(recorded, key = { it.visit.id }) { item ->
                            HistorySurface(onClick = { storeId = item.visit.id }) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    DealerIcon(R.drawable.ic_store); Spacer(Modifier.width(12.dp))
                                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(item.visit.name, style = AgentVisitDesign.name, color = DealerDesign.text,
                                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        Text(historyMoney(item.net), style = AgentVisitDesign.detail, color = DealerDesign.secondary)
                                    }
                                    DealerIcon(R.drawable.ic_chevron, size = 16.dp)
                                }
                            }
                        }
                        if (recorded.isEmpty()) item { HistoryEmpty("За этот месяц продаж нет") }
                    }
                    else -> items(months, key = { it.key }) { item ->
                        val target = historySalesStores(item, entries).sumOf { it.target }
                        HistorySurface(onClick = { monthKey = item.key }) {
                            Row(Modifier.heightIn(min = 42.dp), verticalAlignment = Alignment.CenterVertically) {
                                DealerIcon(R.drawable.ic_visit); Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(item.label, style = AgentVisitDesign.detail, color = DealerDesign.secondary)
                                    Text(historyMoney(target), style = AgentVisitDesign.emphasis, color = DealerDesign.text)
                                }
                                DealerIcon(R.drawable.ic_chevron, size = 16.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SalesMonthSummary(month: HistoryMonth, stores: List<HistorySalesStore>) {
    val target = stores.sumOf { it.target }
    val received = stores.sumOf { it.received }
    val returned = stores.sumOf { it.returned }
    val net = received - returned
    val progress = if (target > 0) (net.toFloat() / target).coerceIn(0f, 1f) else 0f
    HistorySurface {
        Text(month.label, style = AgentVisitDesign.detail, color = DealerDesign.secondary)
        Text(historyMoney(target), style = AgentVisitDesign.emphasis, color = DealerDesign.text)
        Spacer(Modifier.height(8.dp))
        Text(if (net >= target && target > 0) "Завершено" else if (received > 0 || returned > 0) "Частично завершено" else "Не начато",
            style = AgentVisitDesign.status, color = if (net >= target && target > 0) Color(0xFF22A447) else Color(0xFFE69B20))
        Spacer(Modifier.height(8.dp))
        SalesValues(received, returned, "Остаток", (target - net).coerceAtLeast(0))
        Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            LinearProgressIndicator(progress = { progress }, color = Color(0xFFBCEBCB),
                trackColor = Color(0xFFE8F4EC), modifier = Modifier.weight(1f).height(6.dp))
            Spacer(Modifier.width(8.dp))
            Text("${(progress * 100).toInt()}%", style = AgentVisitDesign.detail, color = DealerDesign.text)
        }
        HorizontalDivider(color = DealerDesign.divider, modifier = Modifier.padding(vertical = 12.dp))
        HistoryValue("Дата создания", historyDate(month.start))
    }
}

@Composable
private fun SalesValues(received: Long, returned: Long, lastLabel: String, lastAmount: Long) {
    HistoryValue("Получено", historyMoney(received), Color(0xFF22A447))
    HistoryValue("Возвращено", (if (returned > 0) "−" else "") + historyMoney(returned), DebtDirection.StoreOwesDealer.color)
    HistoryValue(lastLabel, historyMoney(lastAmount), if (lastLabel == "Остаток" && lastAmount > 0) DebtDirection.StoreOwesDealer.color else DealerDesign.text)
}
