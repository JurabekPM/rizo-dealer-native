package com.example.dealerappnative.screens

import com.example.dealerappnative.ui.components.LocalStoreActions
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.AgentVisitSessions
import com.example.dealerappnative.data.agentVisitStores
import com.example.dealerappnative.model.StoreUiModel
import com.example.dealerappnative.model.VisitResult
import com.example.dealerappnative.model.VisitStore
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

/** Visit -> History, Figma 14776:218419; store details keep their own screen. */
@Composable
internal fun AgentVisitDetailScreen(store: StoreUiModel, visit: VisitStore,
    sessions: AgentVisitSessions, onBack: () -> Unit) {
    val session = sessions.sessions[store.id]
    val detailListState = rememberLazyListState()
    var requestVisible by rememberSaveable(store.id) { mutableStateOf(false) }
    if (requestVisible) {
        AgentVisitRequestScreen(visit, sessions, onBack = { requestVisible = false })
        return
    }
    var returnVisible by rememberSaveable(store.id) { mutableStateOf(false) }
    if (returnVisible) {
        VisitReturnScreen(visit, session, allowCreate = session?.isActive == true,
            onBack = { returnVisible = false }, onSave = { sessions.addReturn(store.id) },
            onStart = { sessions.start(store.id, visit.results) })
        return
    }
    var finishSheet by rememberSaveable(store.id) { mutableStateOf(false) }
    var warning by rememberSaveable(store.id) { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current
    Column(Modifier.fillMaxSize().background(DealerDesign.background).imePadding()) {
        Box(Modifier.fillMaxWidth().height(56.dp).background(Color.White), contentAlignment = Alignment.Center) {
            Text(store.name, style = AgentVisitDesign.screenTitle, color = DealerDesign.text,
                maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = 56.dp))
            SoftIconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp)
                .size(48.dp).semantics { contentDescription = "Назад" }) {
                DealerIcon(R.drawable.ic_arrow_back, DealerDesign.text)
            }
        }
        LazyColumn(Modifier.weight(1f), state = detailListState, contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item(key = "store") { VisitStoreSummary(store) }
            if (session != null) item(key = "time") { AgentVisitTiming(session) }
            item(key = "balance") {
                Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White)
                    .heightIn(min = 56.dp).padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("Конечное сальдо", style = AgentVisitDesign.name, color = DealerDesign.secondary,
                        modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(12.dp))
                    Text(store.debtDirection.formatAmount(store.debt), style = AgentVisitDesign.emphasis,
                        color = store.debtDirection.color, maxLines = 1,
                        modifier = Modifier.sharedVisitStorePart(store.id, VisitStorePart.Debt))
                }
            }
            item(key = "operations") {
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White)
                    .padding(horizontal = 16.dp)) {
                    VisitResult.entries.forEachIndexed { index, result ->
                        VisitOperationRow(visit, result, result in (session?.results ?: visit.results),
                            onClick = when (result) {
                                VisitResult.Return -> ({ returnVisible = true })
                                VisitResult.Request -> ({ requestVisible = true })
                                else -> null
                            })
                        if (index < VisitResult.entries.lastIndex) HorizontalDivider(color = DealerDesign.divider,
                            modifier = Modifier.padding(start = 36.dp))
                    }
                }
            }
            if (session != null) item(key = "notes") {
                AgentVisitNotes(session, onComment = { sessions.comment(store.id, it) },
                    onAddPhotos = { sessions.addPhotos(store.id, it) }, onRemovePhoto = { sessions.removePhoto(store.id, it) })
            }
        }
        if (session == null || session.isActive) AgentVisitAction(
            if (session == null) "Начать визит" else "Завершить визит") {
            keyboard?.hide()
            when {
                session == null -> if (!sessions.start(store.id, visit.results)) warning = true
                session.needsReason -> finishSheet = true
                else -> sessions.finish(store.id)
            }
        }
    }
    if (finishSheet && session?.isActive == true) VisitFinishSheet(session,
        onComment = { sessions.comment(store.id, it) }, onDismiss = { finishSheet = false }, onFinish = {
            if (sessions.finish(store.id, it)) { keyboard?.hide(); finishSheet = false }
        })
    if (warning) AlertDialog(onDismissRequest = { warning = false }, containerColor = Color.White,
        title = { Text("Уже идёт визит", style = AgentVisitDesign.screenTitle) },
        text = { Text("Сначала завершите визит: " +
            (agentVisitStores.find { it.id == sessions.activeStoreId }?.name ?: "другой магазин"),
            style = AgentVisitDesign.name) },
        confirmButton = { SoftTextButton(onClick = { warning = false }) {
            Text("Понятно", style = AgentVisitDesign.emphasis, color = DealerDesign.accent)
        } })
}

@Composable
private fun VisitStoreSummary(store: StoreUiModel) {
    val actions = LocalStoreActions.current
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White).padding(16.dp)) {
        Row(Modifier.fillMaxWidth().heightIn(min = 44.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.sharedVisitStorePart(store.id, VisitStorePart.Logo)) { DealerIcon(R.drawable.ic_store) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(store.name, style = AgentVisitDesign.emphasis, color = DealerDesign.text,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.sharedVisitStorePart(store.id, VisitStorePart.Name))
                Text(store.address, style = AgentVisitDesign.detail, color = DealerDesign.secondary,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            VisitSummaryAction("Открыть карту", R.drawable.ic_route, Modifier.weight(1f)) { actions.openMap(store) }
            VisitSummaryAction("Позвонить", R.drawable.ic_phone_call, Modifier.weight(1f)) { actions.dial(store.phoneNumber) }
        }
    }
}

@Composable
private fun VisitSummaryAction(label: String, icon: Int, modifier: Modifier, onClick: () -> Unit) {
    Row(modifier.softCardPress(onClick = onClick).height(40.dp).clip(RoundedCornerShape(12.dp)).background(DealerDesign.background)
        .padding(horizontal = 8.dp), horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
        DealerIcon(icon)
        Spacer(Modifier.width(8.dp))
        Text(label, style = AgentVisitDesign.emphasis, color = DealerDesign.text,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun VisitOperationRow(visit: VisitStore, result: VisitResult, completed: Boolean, onClick: (() -> Unit)? = null) {
    val (title, icon, description) = when (result) {
        VisitResult.Delivery -> Triple("Доставка", R.drawable.ic_box, if (completed) visit.details.deliveries else "Нет доставок")
        VisitResult.Request -> Triple("Заявка", R.drawable.ic_visit, if (completed) visit.details.request else "Нет заявки")
        VisitResult.Revision -> Triple("Ревизия", R.drawable.ic_edit, if (completed) visit.details.revisions else "Нет ревизии")
        VisitResult.Collection -> Triple("Инкассация", R.drawable.img_visit_collection_result, if (completed) visit.details.collection else "Нет инкассации")
        VisitResult.Return -> Triple("Возврат", R.drawable.ic_return, if (completed) visit.details.returns else "Нет возвращённых товаров")
    }
    Row(Modifier.fillMaxWidth().heightIn(min = 64.dp).softCardPress(onClick = onClick).padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        if (completed) VisitResultBadge(result, visit.id, inDetails = true)
        else DealerIcon(icon, if (result == VisitResult.Collection) Color.Unspecified else DealerDesign.secondary)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = AgentVisitDesign.emphasis, color = DealerDesign.text)
            Text(description, style = AgentVisitDesign.detail, color = DealerDesign.secondary,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        if (completed) {
            Spacer(Modifier.width(8.dp))
            Text("Завершено", style = AgentVisitDesign.status, color = Color(0xFF22A447))
        }
        if (onClick != null) { Spacer(Modifier.width(8.dp)); DealerIcon(R.drawable.ic_chevron, size = 16.dp) }
    }
}
