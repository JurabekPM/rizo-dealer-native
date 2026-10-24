package com.example.dealerappnative.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.AgentVisitSessions
import com.example.dealerappnative.data.agentVisitCards
import com.example.dealerappnative.data.LocalVisitReturns
import com.example.dealerappnative.data.HistorySection
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

/** The same local sessions as Visits; finishing a visit immediately makes it available here. */
@Composable
internal fun AgentHistoryScreen(sessions: AgentVisitSessions, onDetailVisible: (Boolean) -> Unit) {
    var filter by rememberSaveable(stateSaver = agentHistoryFilterSaver) { mutableStateOf(AgentHistoryFilter()) }
    var showFilter by rememberSaveable { mutableStateOf(false) }
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    var section by rememberSaveable { mutableStateOf<HistorySection?>(null) }
    val returns = LocalVisitReturns.current
    val listState = rememberLazyListState()
    // Explicit demo scheduling metadata, independent of status/results shown on cards.
    val unplannedIds = remember { agentVisitCards.filter { it.results.isNotEmpty() }
        .filterIndexed { index, _ -> index % 4 == 1 }.map { it.id }.toSet() }
    val entries = agentVisitCards.mapNotNull { visit ->
        sessions.sessions[visit.id]?.takeUnless { it.isActive }?.let {
            AgentHistoryEntry(sessions.requests.enrich(returns.enrich(visit.copy(results = it.results))), it,
                visit.id in unplannedIds, returns.records[visit.id], sessions.requests.records[visit.id])
        }
    }.sortedByDescending { it.session.startedAt }
    val visible = entries.filter(filter::matches)
    val selected = entries.find { it.visit.id == selectedId }
    val close: () -> Unit = { selectedId = null; onDetailVisible(false) }
    LaunchedEffect(selectedId, section) { onDetailVisible(selectedId != null || section != null) }
    DisposableEffect(Unit) { onDispose { onDetailVisible(false) } }
    BackHandler(enabled = selectedId != null, onBack = close)
    if (section != null) {
        AgentHistorySectionScreen(section!!, entries, onBack = { section = null; onDetailVisible(false) })
    } else if (selected != null) {
        AgentHistoryVisitDetailScreen(selected, onBack = close)
    } else Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
        Box(Modifier.fillMaxWidth().height(56.dp).background(Color.White), contentAlignment = Alignment.Center) {
            Text("История", style = AgentVisitDesign.screenTitle, color = DealerDesign.text)
            SoftIconButton(onClick = { showFilter = true }, modifier = Modifier.align(Alignment.CenterEnd)
                .padding(end = 4.dp).size(48.dp).semantics { contentDescription = "Фильтр истории" }) {
                Box {
                    DealerIcon(R.drawable.ic_collection_filter, DealerDesign.text)
                    if (filter.active) Box(Modifier.align(Alignment.TopEnd).size(6.dp)
                        .background(DealerDesign.accent, CircleShape))
                }
            }
        }
        LazyColumn(Modifier.fillMaxSize(), state = listState,
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item(key = "plans", contentType = "plans") { HistoryPlans { section = it; onDetailVisible(true) } }
            items(visible, key = { it.visit.id }, contentType = { "history" }) { entry ->
                AgentHistoryCard(entry) { onDetailVisible(true); selectedId = entry.visit.id }
            }
            if (visible.isEmpty()) item(key = "empty") {
                Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DealerIcon(R.drawable.ic_agent_history, size = 32.dp)
                    Text(if (filter.active) "Визиты не найдены" else "Пока нет завершённых визитов",
                        style = AgentVisitDesign.emphasis, color = DealerDesign.text)
                    Text(if (filter.active) "Измените параметры фильтра" else "Завершённые визиты появятся здесь",
                        style = AgentVisitDesign.detail, color = DealerDesign.secondary)
                    if (filter.active) SoftTextButton(onClick = { filter = AgentHistoryFilter() }) {
                        Text("Сбросить фильтр", style = AgentVisitDesign.button, color = DealerDesign.accent)
                    }
                }
            }
        }
    }
    if (showFilter) AgentHistoryFilterSheet(filter, agentVisitCards, onDismiss = { showFilter = false }, onApply = {
        filter = it; showFilter = false; listState.requestScrollToItem(0)
    })
}

@Composable
private fun HistoryPlans(onSelect: (HistorySection) -> Unit) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White)
        .padding(horizontal = 16.dp)) {
        val sections = listOf(HistorySection.Revisions, HistorySection.Visits, HistorySection.CollectionPlan,
            HistorySection.Returns, HistorySection.Sales)
        sections.forEachIndexed { index, section ->
            val icon = when (section) {
                HistorySection.CollectionPlan -> R.drawable.img_visit_collection_result
                HistorySection.Revisions -> R.drawable.ic_edit
                HistorySection.RequestPlan, HistorySection.Sales -> R.drawable.ic_visit
                HistorySection.Visits -> R.drawable.ic_agent_history
                HistorySection.Returns -> R.drawable.ic_return
            }
            Row(Modifier.fillMaxWidth().height(56.dp).softCardPress { onSelect(section) },
                verticalAlignment = Alignment.CenterVertically) {
                DealerIcon(icon, if (section == HistorySection.CollectionPlan) Color.Unspecified else DealerDesign.secondary)
                Spacer(Modifier.width(12.dp))
                Text(section.title, style = AgentVisitDesign.name, color = DealerDesign.text, modifier = Modifier.weight(1f))
                DealerIcon(R.drawable.ic_chevron, size = 16.dp)
            }
            if (index < sections.lastIndex) HorizontalDivider(color = DealerDesign.divider,
                modifier = Modifier.padding(start = 36.dp))
        }
    }
}
