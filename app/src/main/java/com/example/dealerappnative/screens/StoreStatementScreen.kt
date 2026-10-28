package com.example.dealerappnative.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.data.loadStoreStatement
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.StatementDesign as Design
import com.example.dealerappnative.ui.theme.DealerDesign
import kotlinx.coroutines.launch

/** Shared, read-only physical-store history for both roles. */
@Composable
internal fun StoreStatementScreen(store: StoreUiModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val entries by produceState<List<StoreStatementEntry>?>(null, store.id) { value = loadStoreStatement(context, store) }
    var typeNames by rememberSaveable(store.id) { mutableStateOf(emptyList<String>()) }
    var currency by rememberSaveable(store.id) { mutableStateOf<String?>(null) }
    var from by rememberSaveable(store.id) { mutableStateOf<Long?>(null) }
    var to by rememberSaveable(store.id) { mutableStateOf<Long?>(null) }
    var filters by rememberSaveable(store.id) { mutableStateOf(false) }
    var dates by rememberSaveable(store.id) { mutableStateOf(false) }
    var expanded by rememberSaveable(store.id) { mutableStateOf<String?>(null) }
    val types = remember(typeNames) { StatementType.entries.filter { it != StatementType.All && it.name in typeNames }.toSet() }
    val filter = StatementFilter(types, currency, from, to)
    val visible = remember(entries, filter) { entries.orEmpty().filter(filter::matches) }
    val months = remember(visible) { statementMonths(visible) }
    val totals = remember(visible) { statementTotals(visible) }
    val currencies = remember(entries) { entries.orEmpty().map { it.currency }.distinct().sorted() }
    val list = rememberLazyListState()
    val scope = rememberCoroutineScope()
    fun applyFilter(value: StatementFilter) {
        typeNames = value.types.map { it.name }; currency = value.currency; from = value.from; to = value.to
        scope.launch { list.scrollToItem(0) }
    }
    Column(Modifier.fillMaxSize().background(DealerDesign.background).semantics { paneTitle = "Взаиморасчёты" }) {
        StoreStatementAppBar(store, filter.active, onBack, onFilters = { filters = true })
        StoreStatementQuickFilters(types, from != null, onType = { applyFilter(filter.toggleType(it)) }, onDates = {
            if (from != null) applyFilter(filter.copy(from = null, to = null)) else dates = true
        })
        if (entries == null) Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text("Загружаем историю…", style = Design.body, color = Design.secondary)
        } else LazyColumn(Modifier.weight(1f), state = list, contentPadding = PaddingValues(bottom = 16.dp)) {
            item(key = "context") { StoreStatementOverview(totals) }
            months.forEachIndexed { monthIndex, month ->
                item(key = "month-${month.key}") {
                    Row(Modifier.fillMaxWidth().padding(top = 8.dp)
                        .background(Color.White, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(month.title, style = Design.name, color = Design.text, modifier = Modifier.weight(1f))
                        if (monthIndex == 0) Text("Всего ${visible.size}", style = Design.caption, color = Design.secondary)
                    }
                }
                itemsIndexed(month.entries, key = { _, entry -> entry.id }) { index, entry ->
                    StoreStatementCard(entry, store.name, expanded == entry.id,
                        grouped = true, lastInGroup = index == month.entries.lastIndex) {
                        expanded = if (expanded == entry.id) null else entry.id
                    }
                }
            }
            if (visible.isEmpty()) item(key = "empty") {
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
