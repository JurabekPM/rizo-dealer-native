package com.example.dealerappnative.screens

import com.example.dealerappnative.ui.components.SoftIconButton
import com.example.dealerappnative.ui.components.softCardPress
import com.example.dealerappnative.ui.components.LocalStoreActions
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.VisitResult
import com.example.dealerappnative.ui.components.VisitResultBadge
import com.example.dealerappnative.ui.components.VisitStorePart
import com.example.dealerappnative.ui.components.sharedVisitStorePart
import com.example.dealerappnative.model.StoreUiModel
import com.example.dealerappnative.ui.components.AgentStorePart
import com.example.dealerappnative.ui.components.LocalAgentStoreTransition
import com.example.dealerappnative.ui.components.agentStoreSharedPart
import com.example.dealerappnative.ui.components.AgentMenuRow
import com.example.dealerappnative.ui.components.DealerIcon
import com.example.dealerappnative.ui.components.PrototypeCreateButton
import com.example.dealerappnative.ui.theme.AgentDesign

/** Agent store details use the supplied reference; starting a visit is visual at this stage. */
@Composable
internal fun AgentStoreDetailScreen(store: StoreUiModel, onBack: () -> Unit, visitResults: List<VisitResult>? = null) {
    Column(Modifier.fillMaxSize().background(AgentDesign.background)) {
        Box(Modifier.fillMaxWidth().height(68.dp).background(Color.White), contentAlignment = Alignment.Center) {
            Text(store.name, style = AgentDesign.screenTitle, color = AgentDesign.text,
                maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(horizontal = 56.dp))
            SoftIconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp)
                .size(48.dp).semantics { contentDescription = "Назад" }) {
                DealerIcon(R.drawable.ic_arrow_back, AgentDesign.text)
            }
        }
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item(key = "store") { AgentStoreSummary(store) }
            item(key = "balance") {
                Row(Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(AgentDesign.radius))
                    .background(Color.White).padding(horizontal = AgentDesign.inset),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("Конечное сальдо", style = AgentDesign.menuTitle, color = AgentDesign.secondary,
                        modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    Text(store.debtDirection.formatAmount(store.debt), style = AgentDesign.menuTitle,
                        color = store.debtDirection.color, maxLines = 1,
                        modifier = Modifier.agentStoreSharedPart(store.id, AgentStorePart.Debt)
                            .sharedVisitStorePart(store.id, VisitStorePart.Debt))
                    Spacer(Modifier.width(4.dp))
                    Box(Modifier.rotate(90f)) { DealerIcon(R.drawable.ic_chevron, AgentDesign.secondary) }
                }
            }
            item(key = "reports") {
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(AgentDesign.radius)).background(Color.White)) {
                    Text("Отчёты", style = AgentDesign.reportTitle, color = AgentDesign.text,
                        modifier = Modifier.padding(16.dp))
                    if (visitResults != null) {
                        AgentVisitOperationRow("Доставки", R.drawable.ic_box, VisitResult.Delivery, store.id, visitResults)
                        AgentVisitOperationRow("Заявки", R.drawable.ic_visit, VisitResult.Request, store.id, visitResults)
                        AgentMenuRow("Визиты", R.drawable.ic_visit)
                        AgentVisitOperationRow("Ревизии", R.drawable.ic_edit, VisitResult.Revision, store.id, visitResults)
                        AgentVisitOperationRow("Инкассация", R.drawable.ic_money, VisitResult.Collection, store.id, visitResults)
                        AgentVisitOperationRow("Возвраты", R.drawable.ic_return, VisitResult.Return, store.id, visitResults, divider = false)
                    } else {
                        AgentMenuRow("Заявки", R.drawable.ic_box)
                        AgentMenuRow("Визиты", R.drawable.ic_visit)
                        AgentMenuRow("Ревизии", R.drawable.ic_edit)
                        AgentMenuRow("Инкассация", R.drawable.ic_money, divider = false)
                    }
                }
            }
        }
        PrototypeCreateButton(label = "Начать визит")
    }
}

@Composable
private fun AgentStoreSummary(store: StoreUiModel) {
    val actions = LocalStoreActions.current
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(AgentDesign.radius)).background(Color.White)
        .padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(Modifier.fillMaxWidth().height(44.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.agentStoreSharedPart(store.id, AgentStorePart.Logo)
                .sharedVisitStorePart(store.id, VisitStorePart.Logo)) {
                if (LocalAgentStoreTransition.current != null) {
                    DealerIcon(R.drawable.ic_agent_store, AgentDesign.secondary, AgentDesign.iconSize)
                } else DealerIcon(R.drawable.ic_store, AgentDesign.secondary)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(store.name, style = AgentDesign.menuTitle, color = AgentDesign.text,
                    modifier = Modifier.agentStoreSharedPart(store.id, AgentStorePart.Name)
                        .sharedVisitStorePart(store.id, VisitStorePart.Name),
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(store.id, style = AgentDesign.menuDetail, color = AgentDesign.secondary,
                    modifier = Modifier.agentStoreSharedPart(store.id, AgentStorePart.Id))
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AgentStoreAction("Открыть карту", R.drawable.ic_route, Modifier.weight(1f)) { actions.openMap(store) }
            AgentStoreAction("Позвонить", R.drawable.ic_phone_call, Modifier.weight(1f)) { actions.dial(store.phoneNumber) }
        }
    }
}

@Composable
private fun AgentStoreAction(label: String, icon: Int, modifier: Modifier, onClick: () -> Unit) {
    Row(modifier.softCardPress(onClick = onClick).height(48.dp).clip(RoundedCornerShape(AgentDesign.radius)).background(AgentDesign.background)
        .padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
        DealerIcon(icon, AgentDesign.secondary)
        Spacer(Modifier.width(8.dp))
        Text(label, style = AgentDesign.menuTitle, color = AgentDesign.text,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun AgentVisitOperationRow(title: String, icon: Int, result: VisitResult,
    storeId: String, completed: List<VisitResult>, divider: Boolean = true) {
    AgentMenuRow(title, icon, divider = divider, leadingContent = {
        VisitResultBadge(result, if (result in completed) storeId else null, inDetails = true, completed = result in completed)
    })
}
