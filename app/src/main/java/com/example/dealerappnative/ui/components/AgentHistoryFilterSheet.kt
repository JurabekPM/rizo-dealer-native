package com.example.dealerappnative.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

internal val agentHistoryFilterSaver = listSaver<AgentHistoryFilter, Any>(
    save = { listOf(it.storeId.orEmpty(), it.type?.name.orEmpty(), it.status?.name.orEmpty(),
        it.from ?: -1L, it.to ?: -1L, it.unplannedOnly) },
    restore = { AgentHistoryFilter((it[0] as String).ifEmpty { null },
        (it[1] as String).takeIf(String::isNotEmpty)?.let(HistoryVisitType::valueOf),
        (it[2] as String).takeIf(String::isNotEmpty)?.let(HistoryStatus::valueOf),
        (it[3] as Long).takeIf { value -> value >= 0 },
        (it[4] as Long).takeIf { value -> value >= 0 }, it[5] as Boolean) }
)

private enum class HistoryFilterPage(val title: String) {
    Main("Фильтр"), Stores("Магазин"), Type("Тип визита"), Status("Статус")
}

/** Draft selections remain local until Apply; dismissing the sheet leaves the list unchanged. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AgentHistoryFilterSheet(applied: AgentHistoryFilter, stores: List<VisitStore>,
    onDismiss: () -> Unit, onApply: (AgentHistoryFilter) -> Unit) {
    var draft by rememberSaveable(stateSaver = agentHistoryFilterSaver) { mutableStateOf(applied) }
    var page by rememberSaveable { mutableStateOf(HistoryFilterPage.Main) }
    var query by rememberSaveable { mutableStateOf("") }
    var dates by rememberSaveable { mutableStateOf(false) }
    ModalBottomSheet(onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White, shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        dragHandle = { Box(Modifier.padding(top = 8.dp, bottom = 12.dp).size(36.dp, 4.dp)
            .background(DealerDesign.divider, RoundedCornerShape(2.dp))) }) {
        BackHandler(enabled = page != HistoryFilterPage.Main) { page = HistoryFilterPage.Main }
        Column(Modifier.fillMaxWidth().heightIn(max = 640.dp).imePadding()) {
            Row(Modifier.fillMaxWidth().heightIn(min = 44.dp).padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically) {
                if (page != HistoryFilterPage.Main) SoftIconButton(onClick = { page = HistoryFilterPage.Main },
                    modifier = Modifier.size(40.dp).semantics { contentDescription = "Назад к фильтру" }) {
                    DealerIcon(R.drawable.ic_arrow_back, DealerDesign.text)
                }
                Text(page.title, style = AgentVisitDesign.sheetTitle, color = DealerDesign.text,
                    modifier = Modifier.weight(1f))
                if (page == HistoryFilterPage.Main) SoftTextButton(onClick = { draft = AgentHistoryFilter() }) {
                    Text("Сбросить", style = AgentVisitDesign.name, color = DealerDesign.accent)
                }
            }
            if (page == HistoryFilterPage.Main) {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    Row(Modifier.fillMaxWidth().heightIn(min = 56.dp)
                        .softCardPress { draft = draft.copy(unplannedOnly = !draft.unplannedOnly) },
                        verticalAlignment = Alignment.CenterVertically) {
                        Text("Только внеплановые визиты", style = AgentVisitDesign.name,
                            color = DealerDesign.text, modifier = Modifier.weight(1f))
                        Switch(draft.unplannedOnly, onCheckedChange = { draft = draft.copy(unplannedOnly = it) },
                            colors = SwitchDefaults.colors(checkedTrackColor = DealerDesign.accent))
                    }
                    HistoryFilterRow("Магазин", stores.find { it.id == draft.storeId }?.name) {
                        query = ""; page = HistoryFilterPage.Stores
                    }
                    HistoryFilterRow("Тип визита", draft.type?.label) { page = HistoryFilterPage.Type }
                    HistoryFilterRow("Статус", draft.status?.label) { page = HistoryFilterPage.Status }
                    Text("Дата", style = AgentVisitDesign.name, color = DealerDesign.text,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                    Row(Modifier.fillMaxWidth().height(44.dp).clip(RoundedCornerShape(12.dp))
                        .softCardPress { dates = true }.background(DealerDesign.background).padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Text(if (draft.from == null) "Выберите" else StatisticsDates.label(
                            StatisticsDateRange(draft.from!!, draft.to!!), StatisticsPeriod.Range),
                            style = AgentVisitDesign.name,
                            color = if (draft.from == null) DealerDesign.tertiary else DealerDesign.text,
                            maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        DealerIcon(R.drawable.ic_agent_schedule)
                    }
                    Spacer(Modifier.height(16.dp))
                }
                AgentVisitAction("Применить") { onApply(draft) }
            } else {
                if (page == HistoryFilterPage.Stores) Box(Modifier.padding(16.dp)) {
                    SearchField(query, { query = it }, height = 40.dp, textStyle = AgentVisitDesign.name)
                }
                LazyColumn(Modifier.fillMaxWidth().weight(1f, fill = false), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                    item { HistoryChoice("Все", when (page) {
                        HistoryFilterPage.Stores -> draft.storeId == null
                        HistoryFilterPage.Type -> draft.type == null
                        HistoryFilterPage.Status -> draft.status == null
                        else -> false
                    }) {
                        draft = when (page) {
                            HistoryFilterPage.Stores -> draft.copy(storeId = null)
                            HistoryFilterPage.Type -> draft.copy(type = null)
                            HistoryFilterPage.Status -> draft.copy(status = null)
                            else -> draft
                        }; page = HistoryFilterPage.Main
                    } }
                    when (page) {
                        HistoryFilterPage.Stores -> items(stores.filter {
                            it.name.contains(query.trim(), true) || it.id.contains(query.trim(), true)
                        }, key = { it.id }) { store ->
                            HistoryChoice(store.name, draft.storeId == store.id, store.id) {
                                draft = draft.copy(storeId = store.id); page = HistoryFilterPage.Main
                            }
                        }
                        HistoryFilterPage.Type -> items(HistoryVisitType.entries) { type ->
                            HistoryChoice(type.label, draft.type == type) {
                                draft = draft.copy(type = type); page = HistoryFilterPage.Main
                            }
                        }
                        HistoryFilterPage.Status -> items(HistoryStatus.entries) { status ->
                            HistoryChoice(status.label, draft.status == status) {
                                draft = draft.copy(status = status); page = HistoryFilterPage.Main
                            }
                        }
                        else -> Unit
                    }
                }
            }
        }
    }
    if (dates) AgentHistoryDatePicker(draft.from, draft.to, onDismiss = { dates = false }, onApply = { from, to ->
        draft = draft.copy(from = from, to = to); dates = false
    })
}

@Composable
private fun HistoryFilterRow(title: String, value: String?, onClick: () -> Unit) {
    Column {
        Row(Modifier.fillMaxWidth().heightIn(min = 56.dp).softCardPress(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically) {
            Text(title, style = AgentVisitDesign.name, color = DealerDesign.text)
            Spacer(Modifier.width(16.dp))
            Text(value ?: "Все", style = AgentVisitDesign.detail, color = DealerDesign.secondary,
                maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            DealerIcon(R.drawable.ic_chevron, size = 16.dp)
        }
        HorizontalDivider(color = DealerDesign.divider)
    }
}

@Composable
private fun HistoryChoice(title: String, selected: Boolean, subtitle: String? = null, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min = 56.dp).selectable(selected, role = Role.RadioButton, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f).padding(vertical = 10.dp)) {
            Text(title, style = AgentVisitDesign.name, color = DealerDesign.text,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (subtitle != null) Text(subtitle, style = AgentVisitDesign.detail, color = DealerDesign.secondary)
        }
        RadioButton(selected, onClick = null, colors = RadioButtonDefaults.colors(selectedColor = DealerDesign.accent))
    }
    HorizontalDivider(color = DealerDesign.divider)
}
