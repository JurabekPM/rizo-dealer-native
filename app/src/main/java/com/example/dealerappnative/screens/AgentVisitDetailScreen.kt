package com.example.dealerappnative.screens

import androidx.compose.ui.unit.Dp
import com.example.dealerappnative.model.AgentVisitSession
import com.example.dealerappnative.model.VisitStatus
import com.example.dealerappnative.ui.theme.displayColor
import com.example.dealerappnative.ui.theme.dealerColor
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
import com.example.dealerappnative.model.StoreUiModel
import com.example.dealerappnative.model.VisitResult
import com.example.dealerappnative.model.VisitStore
import com.example.dealerappnative.navigation.LocalDealerWindowLayout
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

/** Visit -> History, Figma 14776:218419; store details keep their own screen. */
@Composable
internal fun AgentVisitDetailScreen(store: StoreUiModel, visit: VisitStore,
    sessions: AgentVisitSessions, onBack: () -> Unit, initialOperation: VisitResult? = null, onStart: (() -> Boolean)? = null) {
    val session = sessions.sessions[store.id]
    val detailListState = rememberLazyListState()
    var requestVisible by rememberSaveable(store.id) { mutableStateOf(initialOperation == VisitResult.Request) }
    if (requestVisible) {
        AgentVisitRequestScreen(visit, sessions, onBack = { requestVisible = false }, onStart = onStart)
        return
    }
    var collectionVisible by rememberSaveable(store.id) { mutableStateOf(initialOperation == VisitResult.Collection) }
    if (collectionVisible) {
        AgentVisitCollectionScreen(visit, sessions, onBack = { collectionVisible = false }, onStart = onStart)
        return
    }
    var returnVisible by rememberSaveable(store.id) { mutableStateOf(false) }
    if (returnVisible) {
        VisitReturnScreen(visit, session, allowCreate = session?.isActive == true,
            onBack = { returnVisible = false }, onSave = { sessions.addReturn(store.id) },
            onStart = { onStart?.invoke() ?: sessions.start(store.id, visit.results) })
        return
    }
    var finishSheet by rememberSaveable(store.id) { mutableStateOf(false) }
    var warning by rememberSaveable(store.id) { mutableStateOf(false) }
    val keyboard = LocalSoftwareKeyboardController.current
    val tablet = LocalDealerWindowLayout.current.hasRail
    val actionLabel = when {
        session == null -> "Начать визит"
        session.isActive -> "Завершить визит"
        else -> null
    }
    val visitAction: () -> Unit = {
        keyboard?.hide()
        when {
            session == null -> if (!(onStart?.invoke() ?: sessions.start(store.id, visit.results))) warning = true
            session.needsReason -> finishSheet = true
            else -> sessions.finish(store.id)
        }
    }
    Column(Modifier.fillMaxSize().then(if (tablet) Modifier.tabletPanel() else Modifier.background(DealerDesign.background))
        .imePadding()) {
        if (!tablet) Box(Modifier.fillMaxWidth().height(56.dp).background(DealerDesign.surface), contentAlignment = Alignment.Center) {
            Text(store.name, style = AgentVisitDesign.screenTitle, color = DealerDesign.text,
                maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = 56.dp))
            SoftIconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp)
                .size(48.dp).semantics { contentDescription = "Назад" }) {
                DealerIcon(R.drawable.ic_arrow_back, DealerDesign.text)
            }
        }
        val visitContent: @Composable (Modifier, Dp) -> Unit = { modifier, actionHeight ->
            val inset = if (tablet) 12.dp else 0.dp
            LazyColumn(
                modifier.fillMaxSize(), state = detailListState,
                contentPadding = PaddingValues(start = inset, top = if (tablet) 12.dp else 8.dp, end = inset,
                    bottom = actionHeight + if (tablet) 8.dp else 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item(key = "store") {
                    VisitStoreSummary(store, visit.sourceStore ?: store, visit.status, session, onBack)
                }
                if (!tablet && session != null) item(key = "time") { AgentVisitTiming(session) }
                if (!tablet) item(key = "balance") {
                    Row(Modifier.fillMaxWidth().cardShadow().background(DealerDesign.surface)
                        .heightIn(min = 56.dp).padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically) {
                        Text("Конечное сальдо", style = AgentVisitDesign.name, color = DealerDesign.secondary,
                            modifier = Modifier.weight(1f))
                        Spacer(Modifier.width(12.dp))
                        Text(store.debtDirection.formatAmount(store.debt), style = AgentVisitDesign.emphasis,
                            color = store.debtDirection.displayColor, maxLines = 1,
                            modifier = Modifier.sharedVisitStorePart(store.id, VisitStorePart.Debt))
                    }
                }
                item(key = "statement") { StoreStatementEntry(visit.sourceStore ?: store, tabletStyle = tablet) }
                item(key = "operations") {
                    Column(Modifier.fillMaxWidth()
                        .then(if (tablet) Modifier.tabletCard() else Modifier.cardShadow().background(DealerDesign.surface))
                        .padding(horizontal = if (tablet) 12.dp else 16.dp)) {
                        if (tablet) Text("Операции визита", style = AgentVisitDesign.name, color = DealerDesign.text,
                            modifier = Modifier.padding(top = 10.dp, bottom = 4.dp))
                        VisitResult.entries.forEachIndexed { index, result ->
                            VisitOperationRow(visit, result, result in (session?.results ?: visit.results),
                                onClick = when (result) {
                                    VisitResult.Return -> ({ returnVisible = true })
                                    VisitResult.Request -> ({ requestVisible = true })
                                    VisitResult.Collection -> ({ collectionVisible = true })
                                    else -> null
                                })
                            if (index < VisitResult.entries.lastIndex) HorizontalDivider(color = if (tablet) TabletColors.edge else DealerDesign.divider,
                                modifier = Modifier.padding(start = if (tablet) 0.dp else 36.dp))
                        }
                    }
                }
                if (session != null) item(key = "notes") {
                    AgentVisitNotes(session, onComment = { sessions.comment(store.id, it) },
                        onAddPhotos = { sessions.addPhotos(store.id, it) }, onRemovePhoto = { sessions.removePhoto(store.id, it) },
                        compact = tablet)
                }
            }
        }
        if (tablet) {
            visitContent(Modifier.weight(1f), 0.dp)
            if (actionLabel != null) {
                Box(Modifier.fillMaxWidth().padding(12.dp), contentAlignment = Alignment.CenterEnd) {
                    AgentVisitButton(actionLabel, onClick = visitAction)
                }
            }
        } else {
            BottomActionLayout(Modifier.weight(1f), actions = {
                if (actionLabel != null) AgentVisitAction(actionLabel, onClick = visitAction)
            }) { actionHeight -> visitContent(Modifier, actionHeight) }
        }
    }
    if (finishSheet && session?.isActive == true) VisitFinishSheet(session,
        onComment = { sessions.comment(store.id, it) }, onDismiss = { finishSheet = false }, onFinish = {
            if (sessions.finish(store.id, it)) { keyboard?.hide(); finishSheet = false }
        })
    if (warning) AlertDialog(onDismissRequest = { warning = false }, containerColor = DealerDesign.surface,
        title = { Text("Не удалось начать визит", style = AgentVisitDesign.screenTitle) },
        text = { Text("Для этого магазина визит уже начат или завершён.", style = AgentVisitDesign.name) },
        confirmButton = { SoftTextButton(onClick = { warning = false }) {
            Text("Понятно", style = AgentVisitDesign.emphasis, color = DealerDesign.accent)
        } })
}

@Composable
private fun VisitStoreSummary(store: StoreUiModel, actionStore: StoreUiModel, status: VisitStatus,
    session: AgentVisitSession?, onBack: () -> Unit) {
    val actions = LocalStoreActions.current
    val tablet = LocalDealerWindowLayout.current.hasRail
    Column(Modifier.fillMaxWidth()
        .then(if (tablet) Modifier.tabletCard() else Modifier.cardShadow().background(DealerDesign.surface))
        .padding(if (tablet) 10.dp else 16.dp)) {
        Row(Modifier.fillMaxWidth().heightIn(min = 44.dp), verticalAlignment = Alignment.CenterVertically) {
            if (tablet) {
                SoftIconButton(onClick = onBack, modifier = Modifier.size(44.dp)
                    .semantics { contentDescription = "Назад" }) {
                    DealerIcon(R.drawable.ic_arrow_back, DealerDesign.text, 20.dp)
                }
            } else Box(Modifier.sharedVisitStorePart(store.id, VisitStorePart.Logo)) { DealerIcon(R.drawable.ic_store) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(store.name, style = AgentVisitDesign.emphasis, color = DealerDesign.text,
                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.sharedVisitStorePart(store.id, VisitStorePart.Name))
                Text(store.address, style = AgentVisitDesign.detail, color = DealerDesign.secondary,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            if (tablet) Text(status.label, style = AgentVisitDesign.detail, color = status.displayColor,
                modifier = Modifier.padding(start = 6.dp).background(TabletColors.inset, RoundedCornerShape(20.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp))
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            VisitSummaryAction("Открыть карту", R.drawable.ic_route, Modifier.weight(1f, fill = !tablet)) {
                actions.openMap(actionStore)
            }
            VisitSummaryAction("Позвонить", R.drawable.ic_phone_call, Modifier.weight(1f, fill = !tablet)) {
                actions.dial(actionStore.phoneNumber)
            }
        }
        if (tablet) {
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth().tabletCard(TabletColors.inset).padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (session != null) AgentVisitTiming(session, Modifier.weight(1f), compact = true)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text("Конечное сальдо", style = AgentVisitDesign.detail, color = DealerDesign.secondary)
                    Text(store.debtDirection.formatAmount(store.debt), style = AgentVisitDesign.emphasis,
                        color = store.debtDirection.displayColor)
                }
            }
        }
    }
}

@Composable
private fun VisitSummaryAction(label: String, icon: Int, modifier: Modifier, onClick: () -> Unit) {
    Row(modifier.softCardPress(onClick = onClick).heightIn(min = 40.dp).clip(RoundedCornerShape(12.dp))
        .background(if (LocalDealerWindowLayout.current.hasRail) TabletColors.raised else DealerDesign.background)
        .padding(horizontal = 8.dp), horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
        DealerIcon(icon, size = 20.dp)
        Spacer(Modifier.width(6.dp))
        Text(label, style = AgentVisitDesign.emphasis, color = DealerDesign.text,
            maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun VisitOperationRow(visit: VisitStore, result: VisitResult, completed: Boolean, onClick: (() -> Unit)? = null) {
    val (title, _, description) = when (result) {
        VisitResult.Delivery -> Triple("Доставка", R.drawable.ic_box, if (completed) visit.details.deliveries else "Нет доставок")
        VisitResult.Request -> Triple("Заявка", R.drawable.ic_visit, if (completed) visit.details.request else "Нет заявки")
        VisitResult.Revision -> Triple("Ревизия", R.drawable.ic_edit, if (completed) visit.details.revisions else "Нет ревизии")
        VisitResult.Collection -> Triple("Инкассация", R.drawable.img_visit_collection_result, if (completed) visit.details.collection else "Нет инкассации")
        VisitResult.Return -> Triple("Возврат", R.drawable.ic_return, if (completed) visit.details.returns else "Нет возвращённых товаров")
    }
    val tablet = LocalDealerWindowLayout.current.hasRail
    Row(Modifier.fillMaxWidth().heightIn(min = if (tablet) 48.dp else 64.dp)
        .softCardPress(onClick = onClick).padding(vertical = if (tablet) 4.dp else 12.dp),
        verticalAlignment = Alignment.CenterVertically) {
        VisitResultBadge(result, if (completed) visit.id else null, inDetails = true, completed = completed)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = AgentVisitDesign.emphasis, color = DealerDesign.text)
            Text(description, style = AgentVisitDesign.detail, color = DealerDesign.secondary,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        if (completed) {
            Spacer(Modifier.width(8.dp))
            Text("Завершено", style = AgentVisitDesign.status, color = dealerColor(Color(0xFF22A447), Color(0xFF83D7A3)))
        }
    }
}
