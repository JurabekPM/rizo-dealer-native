package com.example.dealerappnative.screens

import com.example.dealerappnative.ui.components.softCardPress
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.AgentVisitSessions
import com.example.dealerappnative.model.VisitStatus
import com.example.dealerappnative.model.VisitStore
import com.example.dealerappnative.data.LocalVisitReturns
import com.example.dealerappnative.data.agentVisitCards
import com.example.dealerappnative.data.agentVisitStores
import com.example.dealerappnative.ui.components.VisitListViewport
import com.example.dealerappnative.ui.components.VisitResultsTransition
import com.example.dealerappnative.ui.components.VisitStoreCard
import com.example.dealerappnative.ui.components.DealerIcon
import com.example.dealerappnative.ui.components.SearchField
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

/** Agent Visits: Figma frame 1370:61589, without its simulated iOS system bars. */
@Composable
internal fun AgentVisitsScreen(visitSessions: AgentVisitSessions, onDetailVisible: (Boolean) -> Unit) {
    // Position the already-active visit near the top once, without moving cards
    // during start/finish or shared transitions.
    val orderedCards = remember(visitSessions) {
        val activeId = visitSessions.activeStoreId
        val active = agentVisitCards.firstOrNull { it.id == activeId }
        if (active == null) agentVisitCards else agentVisitCards.filter { it.id != activeId }
            .toMutableList().apply { add(2.coerceAtMost(size), active) }
    }
    val returns = LocalVisitReturns.current
    val cards = orderedCards.map(returns::enrich).map(visitSessions.requests::enrich).map { card ->
        visitSessions.sessions[card.id]?.let { session ->
            card.copy(status = when {
                session.isActive -> VisitStatus.Active
                session.isAttempt -> VisitStatus.Attempted
                else -> VisitStatus.Completed
            }, results = session.results)
        } ?: card.copy(status = if (card.results.isNotEmpty()) VisitStatus.Completed else VisitStatus.NotVisited)
    }
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    val state = rememberSaveableStateHolder()
    val listState = rememberLazyListState()
    var query by rememberSaveable { mutableStateOf("") }
    var savedIndex by rememberSaveable { mutableIntStateOf(0) }
    var savedOffset by rememberSaveable { mutableIntStateOf(0) }
    val closeVisit: () -> Unit = {
        // A new card tap opens visit details, not its previously open return form.
        // Map/call round-trips do not close this visit and retain their saved state.
        selectedId?.let { state.removeState("visit-detail-$it") }
        listState.requestScrollToItem(savedIndex, savedOffset)
        onDetailVisible(false)
        selectedId = null
    }
    val selected = agentVisitStores.firstOrNull { it.id == selectedId }
    LaunchedEffect(selected != null) { onDetailVisible(selected != null) }
    DisposableEffect(Unit) { onDispose { onDetailVisible(false) } }
    BackHandler(enabled = selected != null, onBack = closeVisit)
    VisitResultsTransition(selectedId, onSelect = {
        savedIndex = listState.firstVisibleItemIndex
        savedOffset = listState.firstVisibleItemScrollOffset
        onDetailVisible(true)
        selectedId = it
    }) { targetId, openVisit ->
        val targetStore = agentVisitStores.firstOrNull { it.id == targetId }
        if (targetStore != null) state.SaveableStateProvider("visit-detail-${targetStore.id}") {
            AgentVisitDetailScreen(targetStore, cards.first { it.id == targetStore.id }, visitSessions, onBack = closeVisit)
        } else state.SaveableStateProvider("visits") {
            VisitListViewport(active = selectedId == null) {
                AgentVisitsList(cards, onStoreClick = openVisit, query = query, onQueryChange = { query = it }, listState = listState)
            }
        }
    }
}

@Composable
private fun AgentVisitsList(cards: List<VisitStore>, onStoreClick: (String) -> Unit, query: String,
    onQueryChange: (String) -> Unit, listState: LazyListState) {
    val search = query.trim()
    val stores = remember(search, cards) {
        cards.filter { it.name.contains(search, true) || it.id.contains(search) || it.address.contains(search, true) }
    }
    Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
        Column(Modifier.fillMaxWidth().background(Color.White)) {
            Box(Modifier.fillMaxWidth().height(56.dp), contentAlignment = Alignment.Center) {
                Text("Визиты", style = AgentVisitDesign.title, color = Color.Black)
                Row(Modifier.align(Alignment.CenterEnd).padding(end = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Сегодня", style = AgentVisitDesign.detail, color = Color.Black)
                    Box(Modifier.rotate(90f)) { DealerIcon(R.drawable.ic_chevron, DealerDesign.secondary, 16.dp) }
                }
            }
            Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
                SearchField(query, onQueryChange, placeholder = "Поиск по имени и ид",
                    height = 40.dp, textStyle = AgentVisitDesign.name)
            }
        }
        Box(Modifier.fillMaxWidth().weight(1f)) {
            LazyColumn(Modifier.fillMaxSize(), state = listState, contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item(key = "plans", contentType = "plans") { AgentVisitPlans() }
                items(stores, key = { it.id }, contentType = { "visit" }) { store -> VisitStoreCard(store, onClick = { onStoreClick(store.id) }) }
                if (stores.isEmpty()) item {
                    Text("Магазины не найдены", style = AgentVisitDesign.name, color = DealerDesign.secondary,
                        modifier = Modifier.fillMaxWidth().padding(24.dp))
                }
            }
            IconButton(onClick = {}, modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 16.dp)
                .shadow(4.dp, CircleShape).clip(CircleShape).background(Color.White).size(48.dp)
                .semantics { contentDescription = "Карта визитов" }) {
                DealerIcon(R.drawable.ic_route, Color.Black)
            }
        }
    }
}

@Composable
private fun AgentVisitPlans() {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth().height(62.dp).clip(RoundedCornerShape(12.dp)).background(DealerDesign.background)
            .padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            DealerIcon(R.drawable.img_visit_collection_result, Color.Unspecified)
            Spacer(Modifier.width(12.dp))
            Column {
                Text("План инкассации", style = AgentVisitDesign.name, color = Color.Black)
                Text("50 000 000 UZS", style = AgentVisitDesign.detail, color = DealerDesign.secondary)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AgentPlanTile("План - Август", "80 000 000 UZS", R.drawable.ic_visit, Modifier.weight(1f))
            AgentPlanTile("Возврат", "Нет возврата", R.drawable.ic_return, Modifier.weight(1f))
        }
    }
}

@Composable
private fun AgentPlanTile(title: String, value: String, icon: Int, modifier: Modifier) {
    Column(modifier.height(98.dp).clip(RoundedCornerShape(12.dp)).background(DealerDesign.background).padding(12.dp)) {
        DealerIcon(icon, DealerDesign.secondary)
        Spacer(Modifier.height(12.dp))
        Text(title, style = AgentVisitDesign.name, color = Color.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(value, style = AgentVisitDesign.detail, color = DealerDesign.secondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}
