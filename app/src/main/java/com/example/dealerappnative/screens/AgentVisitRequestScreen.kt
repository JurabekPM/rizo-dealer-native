package com.example.dealerappnative.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

/** Figma Applicant / Visit -> Заявка. Saving the request does not finish its visit. */
@Composable
internal fun AgentVisitRequestScreen(visit: VisitStore, sessions: AgentVisitSessions, onBack: () -> Unit,
    onStart: (() -> Boolean)? = null) {
    val session = sessions.sessions[visit.id]
    val record = sessions.requests.records[visit.id]
    val context = LocalContext.current
    var editing by rememberSaveable(visit.id) { mutableStateOf(record == null) }
    var quantities by rememberSaveable(visit.id) { mutableStateOf(requestQuantities(record)) }
    var pickerVisible by rememberSaveable(visit.id) { mutableStateOf(false) }
    var confirmVisible by rememberSaveable(visit.id) { mutableStateOf(false) }
    var quantityIndex by rememberSaveable(visit.id) { mutableStateOf<Int?>(null) }
    val listState = rememberLazyListState()
    val back: () -> Unit = {
        if (record != null && editing) { quantities = requestQuantities(record); editing = false }
        else onBack()
    }
    BackHandler(onBack = back)

    // Historical fixtures remain read-only and must never become a new draft.
    if (record == null && VisitResult.Request in (session?.results ?: visit.results)) {
        val time = session?.finishedAt ?: System.currentTimeMillis()
        val snapshot = (session ?: AgentVisitSession(time)).copy(finishedAt = time,
            results = (session?.results.orEmpty() + visit.results + VisitResult.Request).distinct())
        val entry = AgentHistoryEntry(visit, snapshot)
        AgentHistoryOperationScreen(entry, historyOperations(entry).first { it.result == VisitResult.Request }, onBack)
        return
    }
    if (record != null && (!editing || session?.isActive != true)) {
        VisitRequestDetailScreen(visit, record, onBack,
            onEdit = if (session?.isActive == true) ({ quantities = requestQuantities(record); editing = true }) else null)
        return
    }
    if (pickerVisible && session?.isActive == true) {
        RequestProductPickerScreen(quantities, onBack = { pickerVisible = false }) {
            quantities = it; pickerVisible = false
        }
        return
    }
    val current = remember(quantities) { normalizeRequestQuantities(quantities) }
    val selected = remember(current) { requestCatalog.indices.filter { current[it] > 0 } }
    val lines = remember(selected, current) {
        selected.map { index -> requestCatalog[index].let {
            VisitRequestItem(it.id, it.name, it.price, current[index])
        } }
    }
    Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
        HistoryHeader("Заявка", back)
        if (session?.isActive != true) {
            BottomActionLayout(Modifier.weight(1f), actions = {
                if (session == null) {
                    AgentVisitAction("Начать визит") {
                        if (!(onStart?.invoke() ?: sessions.start(visit.id, visit.results)))
                            Toast.makeText(context, "Для этого магазина визит уже начат или завершён", Toast.LENGTH_SHORT).show()
                    }
                }
            }) { actionHeight ->
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(top = 8.dp, bottom = actionHeight),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { HistoryStoreSummary(visit) }
                    item { HistorySurface {
                        Text(if (session == null) "Начните визит, чтобы оформить заявку" else "Визит завершён. Заявки не оформлялись",
                            style = AgentVisitDesign.name, color = DealerDesign.secondary)
                    } }
                }
            }
        } else {
            BottomActionLayout(Modifier.weight(1f), actions = {
                CartFooter("Завершить", lines.isNotEmpty()) { confirmVisible = true }
            }) { actionHeight ->
                LazyColumn(Modifier.fillMaxSize(), state = listState, contentPadding = PaddingValues(top = 8.dp, bottom = actionHeight + 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (selected.isEmpty()) item(key = "empty") {
                        HistorySurface(Modifier.animateItem(), verticalPadding = 24.dp) {
                            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(painterResource(R.drawable.request_empty), null, modifier = Modifier.size(220.dp))
                                Spacer(Modifier.height(4.dp))
                                Text("Нет товаров в списке", style = AgentVisitDesign.detail, color = DealerDesign.secondary)
                                Spacer(Modifier.height(24.dp))
                                Box(Modifier.width(240.dp)) { ReturnPrimaryButton("Добавить") { pickerVisible = true } }
                            }
                        }
                    } else {
                        item(key = "total") { CartTotal(lines.sumOf { it.total }) }
                        items(selected, key = { requestCatalog[it].id }, contentType = { "cart-product" }) { index ->
                            CartProductCard(requestCatalog[index], current[index],
                                onStep = { step -> quantities = stepRequestPickerQuantity(quantities, index, step) },
                                modifier = Modifier.animateItem(),
                                onEditQuantity = { quantityIndex = index },
                                onRemove = { quantities = normalizeRequestQuantities(quantities).toMutableList().apply { this[index] = 0 } })
                        }
                        item(key = "add") { CartAddAction(Modifier.animateItem()) { pickerVisible = true } }
                    }
                }
            }
        }
    }
    quantityIndex?.let { index -> RequestQuantitySheet(requestCatalog[index], current[index],
        onDismiss = { quantityIndex = null }, onConfirm = {
            quantities = current.toMutableList().apply { this[index] = it }; quantityIndex = null
        }) }
    if (confirmVisible) AlertDialog(onDismissRequest = { confirmVisible = false }, containerColor = DealerDesign.surface,
        title = { Text("Внимание", style = AgentVisitDesign.screenTitle) },
        text = { Text("Вы хотите завершить заявку?", style = AgentVisitDesign.name) },
        dismissButton = { SoftTextButton(onClick = { confirmVisible = false }) {
            Text("Нет", style = AgentVisitDesign.button, color = DealerDesign.secondary)
        } }, confirmButton = { SoftTextButton(onClick = {
            if (sessions.saveRequest(visit.id, lines)) { confirmVisible = false; editing = false; onBack() }
            else {
                confirmVisible = false
                Toast.makeText(context, "Заявку можно сохранить только во время активного визита", Toast.LENGTH_SHORT).show()
            }
        }) { Text("Да", style = AgentVisitDesign.button, color = DealerDesign.accent) } })
}

private fun requestQuantities(record: VisitRequestRecord?): List<Int> = requestCatalog.map { product ->
    record?.items?.find { it.productId == product.id }?.quantity ?: 0
}
