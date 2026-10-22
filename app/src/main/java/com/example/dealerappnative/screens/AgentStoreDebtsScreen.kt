package com.example.dealerappnative.screens

import com.example.dealerappnative.ui.components.softCardPress
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.CompositionLocalProvider
import com.example.dealerappnative.model.AgentStoreDebt
import com.example.dealerappnative.ui.components.AgentStoreSharedTransition
import com.example.dealerappnative.ui.components.LocalAgentStoreTransition
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.AgentStoreDebtData
import com.example.dealerappnative.data.StoreLocationPreferences
import com.example.dealerappnative.ui.components.AgentStoreDebtCard
import com.example.dealerappnative.ui.components.AgentStoreDebtHeader
import com.example.dealerappnative.ui.components.DealerIcon
import com.example.dealerappnative.ui.theme.AgentDesign

private enum class DebtFilter(val label: String) { All("Все"), WithDebt("С долгом"), WithoutDebt("Без долга") }

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun AgentStoreDebtsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val stores = remember(context) {
        val preferences = StoreLocationPreferences(context)
        AgentStoreDebtData.stores.map { item ->
            item.copy(store = item.store.copy(address = preferences.read(item.store).address))
        }
    }
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    val state = rememberSaveableStateHolder()
    BackHandler(enabled = selectedId != null) { selectedId = null }
    SharedTransitionLayout(Modifier.fillMaxSize()) {
        val sharedScope = this
        AnimatedContent(targetState = selectedId, modifier = Modifier.fillMaxSize(),
            label = "Agent store details",
            transitionSpec = {
                (fadeIn(tween(240, delayMillis = 60)) togetherWith fadeOut(tween(180)))
                    .using(SizeTransform(clip = false))
            }) { targetId ->
            val transition = remember(sharedScope, this) { AgentStoreSharedTransition(sharedScope, this) }
            CompositionLocalProvider(LocalAgentStoreTransition provides transition) {
                val selected = stores.firstOrNull { it.store.id == targetId }?.store
                if (selected != null) state.SaveableStateProvider("debt-detail-${selected.id}") {
                    AgentStoreDetailScreen(selected, onBack = { selectedId = null })
                } else state.SaveableStateProvider("store-debts") {
                    AgentStoreDebtList(stores, onBack, isCurrentScreen = selectedId == null,
                        onStoreClick = { selectedId = it })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AgentStoreDebtList(
    stores: List<AgentStoreDebt>,
    onBack: () -> Unit,
    isCurrentScreen: Boolean,
    onStoreClick: (String) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf(DebtFilter.All) }
    val listState = rememberLazyListState()
    val headerState = rememberTopAppBarState()
    var toolbarSearchActive by rememberSaveable { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        headerState, canScroll = { !toolbarSearchActive && isCurrentScreen })
    val searchFocus = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val closeSearch: () -> Unit = {
        toolbarSearchActive = false
        focusManager.clearFocus()
        keyboard?.hide()
    }
    BackHandler(enabled = toolbarSearchActive && isCurrentScreen) { closeSearch() }
    val search = query.trim()
    val visible = stores.filter {
        (filter == DebtFilter.All || (filter == DebtFilter.WithDebt && it.hasDebt) ||
            (filter == DebtFilter.WithoutDebt && !it.hasDebt)) &&
            (it.store.name.contains(search, true) || it.store.id.contains(search) ||
                it.store.address.contains(search, true))
    }
    Column(Modifier.fillMaxSize().background(AgentDesign.background).imePadding()
        .nestedScroll(scrollBehavior.nestedScrollConnection)) {
        AgentStoreDebtHeader(query = query, onQueryChange = { query = it },
            state = headerState, searchFocus = searchFocus, onBack = onBack,
            toolbarSearchActive = toolbarSearchActive, onSearchClose = closeSearch,
            onSearchClick = { toolbarSearchActive = true })
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 8.dp, bottom = 8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DebtFilter.entries.forEach { option ->
                    val count = when (option) {
                        DebtFilter.All -> stores.size
                        DebtFilter.WithDebt -> AgentStoreDebtData.withDebtCount
                        DebtFilter.WithoutDebt -> AgentStoreDebtData.withoutDebtCount
                    }
                    val active = option == filter
                    Box(Modifier.weight(1f).height(36.dp).clip(RoundedCornerShape(AgentDesign.radius))
                        .background(if (active) AgentDesign.accent else Color.White)
                        .selectable(selected = active, role = Role.RadioButton, interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = { filter = option }),
                        contentAlignment = Alignment.Center) {
                        Text("${option.label} $count", style = AgentDesign.menuDetail,
                            color = if (active) Color.White else AgentDesign.secondary,
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text("Магазины · ${visible.size}", style = AgentDesign.menuTitle, color = AgentDesign.text,
                modifier = Modifier.weight(1f))
            Text("По сумме долга", style = AgentDesign.detail, color = AgentDesign.secondary)
        }
        LazyColumn(Modifier.fillMaxWidth().weight(1f), state = listState, contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(visible, key = { it.store.id }) { item ->
                AgentStoreDebtCard(item, onClick = {
                    focusManager.clearFocus()
                    keyboard?.hide()
                    onStoreClick(item.store.id)
                })
            }
            if (visible.isEmpty()) item {
                Column(Modifier.fillMaxWidth().padding(vertical = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    DealerIcon(R.drawable.ic_search, AgentDesign.secondary)
                    Spacer(Modifier.height(12.dp))
                    Text("Магазины не найдены", style = AgentDesign.menuTitle, color = AgentDesign.text)
                    Spacer(Modifier.height(4.dp))
                    Text("Измените поиск или фильтр", style = AgentDesign.detail, color = AgentDesign.secondary)
                    Text("Сбросить", style = AgentDesign.menuTitle, color = AgentDesign.accent,
                        modifier = Modifier.clip(RoundedCornerShape(8.dp)).softCardPress { query = ""; filter = DebtFilter.All }
                            .padding(12.dp))
                }
            }
        }
    }
}
