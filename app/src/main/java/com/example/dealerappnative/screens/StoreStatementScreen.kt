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
import com.example.dealerappnative.ui.theme.StatementDesign as Design
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
        if (entries == null) Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text("Загружаем историю…", style = Design.body, color = Design.secondary)
        } else LazyColumn(Modifier.weight(1f), state = list, contentPadding = PaddingValues(vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item(key = "context") { StoreStatementHeader(store, from, to, onDates = { dates = true }) }
            item(key = "count") {
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Операции", style = Design.name, color = Design.text, modifier = Modifier.weight(1f))
                        Text(visible.size.toString(), style = Design.label, color = Design.secondary)
                    }
                    if (type != StatementType.All || currency != null) Text(
                        listOfNotNull(type.takeIf { it != StatementType.All }?.label, currency).joinToString(" · "),
                        style = Design.caption, color = Design.secondary)
                }
            }
            items(visible, key = { it.id }) { entry -> StoreStatementCard(entry, store.name, expanded == entry.id) {
                expanded = if (expanded == entry.id) null else entry.id
            } }
            if (visible.isEmpty()) item {
                Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("За этот период операций нет", style = Design.name, color = Design.text)
                    SoftTextButton({ applyFilter(StatementFilter()) }) { Text("Сбросить фильтры", style = Design.body, color = DealerDesign.accent) }
                }
            }
        }
    }
    if (filters) StoreStatementFilters(filter, currencies, onDismiss = { filters = false }, onApply = { applyFilter(it); filters = false })
    if (dates) AgentHistoryDatePicker(from, to, onDismiss = { dates = false }, onApply = { a, b ->
        applyFilter(filter.copy(from = a, to = b)); dates = false
    })
}
