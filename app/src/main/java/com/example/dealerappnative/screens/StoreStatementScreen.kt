package com.example.dealerappnative.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.loadStoreStatement
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign as Design
import com.example.dealerappnative.ui.theme.DealerDesign
import kotlinx.coroutines.launch

/** One read-only history for both roles; no reconciliation request or money-changing action. */
@Composable
internal fun StoreStatementScreen(store: StoreUiModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val entries by produceState<List<StoreStatementEntry>?>(null, store.id) { value = loadStoreStatement(context, store) }
    var type by rememberSaveable(store.id) { mutableStateOf(StatementType.All) }
    var currency by rememberSaveable(store.id) { mutableStateOf<String?>(null) }
    var from by rememberSaveable(store.id) { mutableStateOf<Long?>(null) }
    var to by rememberSaveable(store.id) { mutableStateOf<Long?>(null) }
    var filters by rememberSaveable(store.id) { mutableStateOf(false) }
    var dates by rememberSaveable(store.id) { mutableStateOf(false) }
    var expanded by rememberSaveable(store.id) { mutableStateOf<String?>(null) }
    val filter = StatementFilter(type, currency, from, to)
    val visible = remember(entries, filter) { entries.orEmpty().filter(filter::matches) }
    val currencies = remember(entries) { entries.orEmpty().map { it.currency }.distinct().sorted() }
    val list = rememberLazyListState()
    val scope = rememberCoroutineScope()
    fun applyFilter(value: StatementFilter) {
        type = value.type; currency = value.currency; from = value.from; to = value.to
        scope.launch { list.scrollToItem(0) }
    }
    Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
        HistoryHeader("Взаиморасчёты", onBack, action = {
            SoftIconButton({ filters = true }, Modifier.size(48.dp).semantics { contentDescription = "Фильтры истории взаиморасчётов" }) {
                DealerIcon(R.drawable.ic_collection_filter, if (filter.active) DealerDesign.accent else DealerDesign.text)
            }
        })
        Column(Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp).padding(top = 8.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(store.name, style = Design.name, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(store.address, style = Design.detail, color = DealerDesign.secondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Row(Modifier.fillMaxWidth().background(Color.White).softCardPress { dates = true }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Период", style = Design.detail, color = DealerDesign.secondary)
            Spacer(Modifier.width(12.dp))
            Text(statementPeriodLabel(from, to), style = Design.detail, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            Box(Modifier.rotate(90f)) { DealerIcon(R.drawable.ic_chevron, size = 18.dp) }
        }
        if (entries == null) Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text("Загружаем историю…", style = Design.detail, color = DealerDesign.secondary)
        } else LazyColumn(Modifier.weight(1f), state = list, contentPadding = PaddingValues(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item(key = "count") {
                Text(if (entries == null) "Загружаем историю…" else "Операции · ${visible.size}" +
                    (if (type == StatementType.All) "" else " · ${type.label}") + (currency?.let { " · $it" } ?: ""),
                    style = Design.detail, color = DealerDesign.secondary, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
            }
            items(visible, key = { it.id }) { entry -> StoreStatementCard(entry, store.name, expanded == entry.id) {
                expanded = if (expanded == entry.id) null else entry.id
            } }
            if (entries != null && visible.isEmpty()) item {
                Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("За этот период операций нет", style = Design.name)
                    SoftTextButton({ applyFilter(StatementFilter()) }) { Text("Сбросить фильтры", style = Design.detail, color = DealerDesign.accent) }
                }
            }
        }
    }
    if (filters) StoreStatementFilters(filter, currencies, onDismiss = { filters = false }, onApply = { applyFilter(it); filters = false })
    if (dates) AgentHistoryDatePicker(from, to, onDismiss = { dates = false }, onApply = { a, b ->
        applyFilter(filter.copy(from = a, to = b)); dates = false
    })
}
