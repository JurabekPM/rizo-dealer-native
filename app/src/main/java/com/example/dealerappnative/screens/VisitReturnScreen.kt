package com.example.dealerappnative.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

/** Figma Visits -> Return, adapted with the requested standard-reason sheet. */
@Composable
internal fun VisitReturnScreen(visit: VisitStore, session: AgentVisitSession? = null, allowCreate: Boolean,
    onBack: () -> Unit, onSave: () -> Boolean = { true }, onStart: (() -> Boolean)? = null,
    canManageReasons: Boolean = false) {
    val returns = LocalVisitReturns.current
    val context = LocalContext.current
    val record = returns.records[visit.id]
    BackHandler(onBack = onBack)
    if (record != null || VisitResult.Return in visit.results) {
        val timestamp = record?.createdAt ?: session?.finishedAt ?: runCatching {
            java.text.SimpleDateFormat("dd.MM.yyyy", java.util.Locale.forLanguageTag("ru")).apply {
                timeZone = java.util.TimeZone.getTimeZone("Asia/Tashkent")
            }.parse(visit.details.date)?.time?.plus(12 * 3_600_000L)
        }.getOrNull() ?: System.currentTimeMillis()
        val snapshot = (session ?: AgentVisitSession(timestamp)).copy(finishedAt = timestamp,
            results = (visit.results + VisitResult.Return).distinct())
        val entry = AgentHistoryEntry(visit, snapshot, returnRecord = record)
        AgentHistoryOperationScreen(entry, historyOperations(entry).first { it.result == VisitResult.Return }, onBack)
        return
    }
    var quantities by rememberSaveable(visit.id) { mutableStateOf(List(returnCatalog.size) { 0 }) }
    var comment by rememberSaveable(visit.id) { mutableStateOf("") }
    var selectedReasonId by rememberSaveable(visit.id) { mutableStateOf<String?>(null) }
    var pickerVisible by rememberSaveable(visit.id) { mutableStateOf(false) }
    var reasonVisible by rememberSaveable(visit.id) { mutableStateOf(false) }
    var settingsVisible by rememberSaveable(visit.id) { mutableStateOf(false) }
    var infoIndex by rememberSaveable(visit.id) { mutableStateOf<Int?>(null) }
    var quantityIndex by rememberSaveable(visit.id) { mutableStateOf<Int?>(null) }
    val listState = rememberLazyListState()
    val currentQuantities = normalizeReturnQuantities(quantities)
    val reasons = returns.reasonSettings.reasons
    if (settingsVisible && canManageReasons) {
        ReturnReasonSettingsScreen(onBack = { settingsVisible = false; reasonVisible = true })
        return
    }
    if (pickerVisible) {
        ReturnProductPickerScreen(currentQuantities, visit.details.date, onBack = { pickerVisible = false }) {
            quantities = it; pickerVisible = false
        }
        return
    }
    val selected = returnCatalog.indices.filter { currentQuantities[it] > 0 }
    val items = selected.map { index -> VisitReturnItem(returnCatalog[index].name, returnCatalog[index].price, currentQuantities[index]) }
    val total = items.sumOf { it.total }
    Column(Modifier.fillMaxSize().background(DealerDesign.background).imePadding()) {
        HistoryHeader(visit.name, onBack)
        LazyColumn(Modifier.weight(1f), state = listState, contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item(key = "store") { HistoryStoreSummary(visit, showId = true) }
            if (allowCreate) item(key = "total") { HistorySurface {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Общая сумма", style = AgentVisitDesign.emphasis, color = DealerDesign.secondary, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(12.dp))
                    Text(historyMoney(total), style = AgentVisitDesign.emphasis, color = DealerDesign.text)
                }
            } }
            if (!allowCreate) item { HistorySurface {
                Text(if (session?.isActive == false) "Визит завершён. Возвраты не оформлялись" else "Начните визит, чтобы оформить возврат",
                    style = AgentVisitDesign.name, color = DealerDesign.secondary)
            } }
            if (allowCreate) {
                if (selected.isEmpty()) item(key = "empty") {
                    Column(Modifier.fillMaxWidth().padding(vertical = 28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(Modifier.size(96.dp).background(DealerDesign.accent.copy(alpha = .06f), RoundedCornerShape(24.dp)),
                            contentAlignment = Alignment.Center) { DealerIcon(R.drawable.ic_box, DealerDesign.accent, 48.dp) }
                        Spacer(Modifier.height(16.dp))
                        Text("Нет товаров в списке", style = AgentVisitDesign.detail, color = DealerDesign.secondary)
                    }
                }
                items(selected, key = { "product-$it" }) { index ->
                    ReturnProductCard(returnCatalog[index], currentQuantities[index],
                        onQuantity = { quantity -> quantities = currentQuantities.toMutableList().apply { this[index] = quantity.coerceIn(0, returnCatalog[index].available) } },
                        onEditQuantity = { quantityIndex = index },
                        onRemove = { quantities = currentQuantities.toMutableList().apply { this[index] = 0 } },
                        onInfo = { infoIndex = index })
                }
                item(key = "add") { Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                    ReturnPrimaryButton("Добавить") { pickerVisible = true }
                } }

            }
        }
        if (allowCreate) Column(Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
            ReturnPrimaryButton("Возвратить", items.isNotEmpty()) { reasonVisible = true }
        } else if (onStart != null && session == null) AgentVisitAction("Начать визит", onClick = {
            if (!onStart()) Toast.makeText(context, "Для этого магазина визит уже начат или завершён", Toast.LENGTH_SHORT).show()
        })
    }
    quantityIndex?.let { index -> ReturnQuantitySheet(returnCatalog[index], currentQuantities[index], onDismiss = { quantityIndex = null }) {
        quantities = currentQuantities.toMutableList().apply { this[index] = it }; quantityIndex = null
    } }
    infoIndex?.let { index -> ReturnProductInfoSheet(returnCatalog[index], visit.details.date,
        onDismiss = { infoIndex = null }) }
    if (reasonVisible) ReturnReasonSheet(reasons, selectedReasonId, comment, onDismiss = { reasonVisible = false },
        onConfirm = { id, text ->
            selectedReasonId = id; comment = text
            if (items.isNotEmpty() && canConfirmReturnReason(id, text, reasons) && onSave()) {
                val now = System.currentTimeMillis()
                val label = if (id == otherReturnReasonId) "Другое" else reasons.first { it.id == id && it.enabled }.label
                returns.save(visit.id, VisitReturnRecord("RN-$now", now, items, text.trim(), id, label))
                reasonVisible = false
                onBack()
            }
        }, confirmLabel = "Возвратить",
        onManage = if (canManageReasons) ({ reasonVisible = false; settingsVisible = true }) else null)
}
