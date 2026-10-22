package com.example.dealerappnative.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.VisitResult
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun AdminReturnCreateScreen(onBack: () -> Unit) {
    val returns = LocalVisitReturns.current
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    var query by rememberSaveable { mutableStateOf("") }
    val stores = (activeVisits + pastVisits).distinctBy { it.id }
    val selected = stores.find { it.id == selectedId }
    val back: () -> Unit = { if (selectedId != null) selectedId = null else onBack() }
    BackHandler(onBack = back)
    if (selected != null) {
        VisitReturnScreen(selected, allowCreate = true,
            onBack = { if (selected.id in returns.records) onBack() else selectedId = null },
            canManageReasons = true)
    } else Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
        HistoryHeader("Выберите магазин", back)
        Box(Modifier.padding(16.dp)) { SearchField(query, { query = it }) }
        val visible = stores.filter { it.id !in returns.records && VisitResult.Return !in it.results &&
            (it.name.contains(query.trim(), true) || it.id.contains(query.trim())) }
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(visible, key = { it.id }) { store ->
                HistorySurface(onClick = { selectedId = store.id }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DealerIcon(R.drawable.ic_store); Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(store.name, style = AgentVisitDesign.emphasis, color = DealerDesign.text)
                            Text(store.address, style = AgentVisitDesign.detail, color = DealerDesign.secondary,
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        DealerIcon(R.drawable.ic_chevron, size = 16.dp)
                    }
                }
            }
            if (visible.isEmpty()) item { HistoryEmpty("Магазины не найдены") }
        }
    }
}
