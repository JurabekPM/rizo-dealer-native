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
import com.example.dealerappnative.data.agentVisitStores
import com.example.dealerappnative.data.sampleStores
import com.example.dealerappnative.model.StoreUiModel
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

internal val unplannedStoreChoices = (sampleStores + agentVisitStores).distinctBy { it.id }

/** Figma 579:32035: store selection, with the already-active store disabled. */
@Composable
internal fun AgentUnplannedVisitsScreen(activeStoreId: String?, onBack: () -> Unit, onSelect: (StoreUiModel) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()
    val stores = remember(query) { unplannedStoreChoices.filter {
        it.name.contains(query.trim(), true) || it.id.contains(query.trim(), true)
    } }
    BackHandler(onBack = onBack)
    Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
        HistoryHeader("Внеплановый", onBack)
        Box(Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
            SearchField(query, { query = it }, placeholder = "Поиск по имени и ид",
                height = 40.dp, textStyle = AgentVisitDesign.name)
        }
        LazyColumn(Modifier.weight(1f), state = listState, contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(stores, key = { it.id }, contentType = { "unplanned-store" }) { store ->
                val active = store.id == activeStoreId
                HistorySurface(onClick = if (active) null else ({ onSelect(store) })) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DealerIcon(R.drawable.ic_store)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(store.name, style = AgentVisitDesign.name,
                                color = if (active) DealerDesign.secondary else DealerDesign.text,
                                maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Text(store.id, style = AgentVisitDesign.detail, color = DealerDesign.secondary)
                        }
                    }
                    HorizontalDivider(Modifier.padding(vertical = 8.dp), color = DealerDesign.divider)
                    Text(store.address, style = AgentVisitDesign.detail, color = DealerDesign.secondary,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (active) Text("У этого магазина уже есть активный визит. Вы не можете добавить новый.",
                        style = AgentVisitDesign.detail, color = DealerDesign.secondary,
                        modifier = Modifier.padding(top = 8.dp))
                }
            }
            if (stores.isEmpty()) item { HistoryEmpty("Магазины не найдены") }
        }
    }
}
