package com.example.dealerappnative.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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

/** Read-only Visit -> History, frame 14776:218419. Active Visits keep their own flow. */
@Composable
internal fun AgentHistoryVisitDetailScreen(entry: AgentHistoryEntry, onBack: () -> Unit) {
    var openedResult by rememberSaveable(entry.visit.id) { mutableStateOf<VisitResult?>(null) }
    val operations = remember(entry) { historyOperations(entry) }
    val listState = rememberLazyListState()
    val opened = operations.find { it.result == openedResult }
    if (opened != null) {
        BackHandler { openedResult = null }
        AgentHistoryOperationScreen(entry, opened, onBack = { openedResult = null })
        return
    }
    Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
        HistoryHeader(entry.visit.name, onBack)
        LazyColumn(Modifier.weight(1f), state = listState, contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item(key = "store") { HistoryStoreSummary(entry.visit) }
            item(key = "dates") { HistoryVisitTimeline(entry.session) }
            item(key = "operations") {
                HistorySurface(verticalPadding = 0.dp) {
                    VisitResult.entries.forEachIndexed { index, result ->
                        val operation = operations.find { it.result == result }
                        Row(Modifier.fillMaxWidth().heightIn(min = 64.dp)
                            .softCardPress(enabled = operation != null) { openedResult = result }.padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            if (operation != null) VisitResultBadge(result, entry.visit.id, inDetails = true)
                            else DealerIcon(when (result) {
                                VisitResult.Delivery -> R.drawable.ic_box
                                VisitResult.Request -> R.drawable.ic_visit
                                VisitResult.Revision -> R.drawable.ic_edit
                                VisitResult.Collection -> R.drawable.img_visit_collection_result
                                VisitResult.Return -> R.drawable.ic_return
                            }, if (result == VisitResult.Collection) Color.Unspecified else DealerDesign.secondary)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(if (operation == null) historyOperationTitle(result) else operation.title + " #" + operation.documentId,
                                    style = AgentVisitDesign.name, color = DealerDesign.text, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(operation?.let { historyMoney(it.amount) } ?: historyEmptyOperation(result),
                                    style = AgentVisitDesign.detail, color = DealerDesign.secondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            if (operation != null) {
                                Spacer(Modifier.width(8.dp))
                                Text("Завершено", style = AgentVisitDesign.detail, color = Color(0xFF22A447))
                                DealerIcon(R.drawable.ic_chevron, size = 16.dp)
                            }
                        }
                        if (index < VisitResult.entries.lastIndex) HorizontalDivider(color = DealerDesign.divider,
                            modifier = Modifier.padding(start = 44.dp))
                    }
                }
            }
            item(key = "notes") {
                AgentVisitNotes(entry.session, onComment = {}, onAddPhotos = {}, onRemovePhoto = {})
            }
        }
    }
}

@Composable
private fun HistoryVisitTimeline(session: AgentVisitSession) {
    HistorySurface {
        Row(Modifier.heightIn(min = 92.dp)) {
            Box(Modifier.width(8.dp).height(92.dp)) {
                Box(Modifier.align(Alignment.TopCenter).padding(top = 4.dp).size(8.dp).background(DealerDesign.accent, CircleShape))
                Box(Modifier.align(Alignment.TopCenter).padding(top = 14.dp).width(2.dp).height(40.dp).background(DealerDesign.accent))
                Box(Modifier.align(Alignment.TopCenter).padding(top = 56.dp).size(8.dp).background(Color(0xFF22C348), CircleShape))
            }
            Spacer(Modifier.width(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                HistoryDateLine("Дата визита", session.startedAt)
                HistoryDateLine(if (session.isAttempt) "Дата отмены" else "Дата завершения", requireNotNull(session.finishedAt))
            }
        }
        session.reason?.let {
            Text(it.label, style = AgentVisitDesign.detail, color = DealerDesign.secondary, modifier = Modifier.padding(start = 20.dp))
        }
    }
}

@Composable
internal fun HistoryDateLine(title: String, time: Long) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(title, style = AgentVisitDesign.name, color = DealerDesign.text)
        Text(historyDate(time), style = AgentVisitDesign.detail, color = DealerDesign.secondary)
    }
}

internal fun historyOperationTitle(result: VisitResult): String = when (result) {
    VisitResult.Delivery -> "Доставка"; VisitResult.Request -> "Заявка"; VisitResult.Revision -> "Ревизия"
    VisitResult.Collection -> "Инкассация"; VisitResult.Return -> "Возврат"
}

private fun historyEmptyOperation(result: VisitResult): String = when (result) {
    VisitResult.Delivery -> "Нет доставок"; VisitResult.Request -> "Нет заявки"; VisitResult.Revision -> "Нет ревизии"
    VisitResult.Collection -> "Нет инкассации"; VisitResult.Return -> "Нет возвращённых товаров"
}
