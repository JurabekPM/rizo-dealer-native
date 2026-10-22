package com.example.dealerappnative.screens

import com.example.dealerappnative.ui.components.SoftIconButton
import com.example.dealerappnative.ui.components.softCardPress
import com.example.dealerappnative.ui.components.LocalStoreActions
import com.example.dealerappnative.data.storeForVisit
import androidx.annotation.DrawableRes
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
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
import com.example.dealerappnative.model.VisitStore
import com.example.dealerappnative.model.VisitDetails
import com.example.dealerappnative.ui.components.VisitResultBadge
import com.example.dealerappnative.ui.components.VisitStorePart
import com.example.dealerappnative.ui.components.sharedVisitStorePart
import com.example.dealerappnative.model.VisitResult
import com.example.dealerappnative.ui.components.DealerIcon
import com.example.dealerappnative.ui.theme.DealerDesign

private val visitActionColor = Color(0xFFFF4D55)

@Composable
internal fun VisitDetailScreen(store: VisitStore, onBack: () -> Unit) {
    val detailListState = rememberLazyListState()
    var returnVisible by rememberSaveable(store.id) { mutableStateOf(false) }
    if (returnVisible) {
        VisitReturnScreen(store, allowCreate = true, onBack = { returnVisible = false }, canManageReasons = true)
        return
    }
    Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
        Box(Modifier.fillMaxWidth().height(68.dp).background(Color.White), contentAlignment = Alignment.Center) {
            Text(store.name, style = DealerDesign.title, maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 56.dp))
            SoftIconButton(onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp)
                    .size(48.dp).semantics { contentDescription = "Назад" }) {
                DealerIcon(R.drawable.ic_arrow_back, DealerDesign.text)
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f), state = detailListState,
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { StoreSummary(store) }
            item { VisitValueRow("Конечное сальдо", store.debtDirection.formatAmount(store.debt), store.debtDirection.color,
                showChevron = true, valueModifier = Modifier.sharedVisitStorePart(store.id, VisitStorePart.Debt)) }
            item { VisitValueRow("Дата визита", store.details.date, DealerDesign.text) }
            item { EmployeeCard(store.details.employee, store.details.employeePhone) }
            item {
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(DealerDesign.radius))
                    .background(Color.White).padding(horizontal = DealerDesign.pageInset)) {
                    val operations = visitOperations(store.details)
                    operations.forEachIndexed { index, operation ->
                        OperationRow(operation, store.id, operation.result in store.results,
                            onClick = if (operation.result == VisitResult.Return) ({ returnVisible = true }) else null)
                        if (index != operations.lastIndex) HorizontalDivider(color = DealerDesign.divider)
                    }
                }
            }
        }
        // The action is visual-only until visit completion/cancellation is designed.
        Box(Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
            Box(Modifier.fillMaxWidth().height(DealerDesign.buttonHeight)
                .clip(RoundedCornerShape(DealerDesign.radius)).background(visitActionColor),
                contentAlignment = Alignment.Center) {
                Text("Отменить визит", style = DealerDesign.button, color = Color.White)
            }
        }
    }
}

@Composable
private fun StoreSummary(store: VisitStore) {
    val actions = LocalStoreActions.current
    val mapStore = storeForVisit(store)
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(DealerDesign.radius))
        .background(Color.White).padding(horizontal = DealerDesign.pageInset, vertical = 12.dp)) {
        Row(Modifier.fillMaxWidth().height(44.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.sharedVisitStorePart(store.id, VisitStorePart.Logo)) {
                DealerIcon(R.drawable.ic_store)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(store.name, style = DealerDesign.cardTitle, maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.sharedVisitStorePart(store.id, VisitStorePart.Name))
                Spacer(Modifier.height(4.dp))
                Text(store.id, style = DealerDesign.detail, color = DealerDesign.secondary)
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryAction("Открыть карту", R.drawable.ic_route, Modifier.weight(1f)) { actions.openMap(mapStore) }
            SummaryAction("Позвонить", R.drawable.ic_phone_call, Modifier.weight(1f)) { actions.dial(mapStore.phoneNumber) }
        }
    }
}

@Composable
private fun SummaryAction(label: String, @DrawableRes iconRes: Int, modifier: Modifier, onClick: () -> Unit) {
    Row(modifier.softCardPress(onClick = onClick).height(48.dp).clip(RoundedCornerShape(DealerDesign.radius))
        .background(DealerDesign.background).padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        DealerIcon(iconRes)
        Spacer(Modifier.width(8.dp))
        Text(label, style = DealerDesign.body, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun VisitValueRow(label: String, value: String, valueColor: Color, showChevron: Boolean = false, valueModifier: Modifier = Modifier) {
    Row(Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(DealerDesign.radius))
        .background(Color.White).padding(horizontal = DealerDesign.pageInset),
        verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = DealerDesign.body, color = DealerDesign.secondary, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(8.dp))
        Text(value, style = DealerDesign.body, color = valueColor, modifier = valueModifier)
        if (showChevron) {
            Spacer(Modifier.width(4.dp))
            Box(Modifier.rotate(90f)) { DealerIcon(R.drawable.ic_chevron) }
        }
    }
}

@Composable
private fun EmployeeCard(employeeName: String, phoneNumber: String) {
    val actions = LocalStoreActions.current
    Row(Modifier.fillMaxWidth().height(72.dp).clip(RoundedCornerShape(DealerDesign.radius))
        .background(Color.White).padding(horizontal = DealerDesign.pageInset),
        verticalAlignment = Alignment.CenterVertically) {
        DealerIcon(R.drawable.ic_person_outline)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text("Сотрудник", style = DealerDesign.detail, color = DealerDesign.secondary)
            Spacer(Modifier.height(4.dp))
            Text(employeeName, style = DealerDesign.body)
        }
        SoftIconButton(onClick = { actions.dial(phoneNumber) }, modifier = Modifier.size(40.dp)
            .semantics { contentDescription = "Позвонить сотруднику" }) {
            DealerIcon(R.drawable.ic_phone_call)
        }
    }
}

@Composable
private fun OperationRow(operation: VisitOperation, storeId: String, completed: Boolean, onClick: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().height(68.dp).softCardPress(onClick = onClick), verticalAlignment = Alignment.CenterVertically) {
        if (completed) VisitResultBadge(operation.result, storeId, inDetails = true)
        else DealerIcon(operation.iconRes, operation.iconColor)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(operation.title, style = DealerDesign.body)
            Spacer(Modifier.height(4.dp))
            Text(operation.description, style = DealerDesign.detail, color = DealerDesign.secondary,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        if (onClick != null) DealerIcon(R.drawable.ic_chevron, size = 16.dp)
    }
}

private data class VisitOperation(
    val title: String,
    val description: String,
    @param:DrawableRes @get:DrawableRes val iconRes: Int,
    val iconColor: Color = DealerDesign.secondary,
    val result: VisitResult
)

private fun visitOperations(details: VisitDetails) = listOf(
    VisitOperation("Доставки", details.deliveries, R.drawable.ic_box, result = VisitResult.Delivery),
    VisitOperation("Заявка", details.request, R.drawable.ic_visit, result = VisitResult.Request),
    VisitOperation("Ревизии", details.revisions, R.drawable.ic_edit, result = VisitResult.Revision),
    VisitOperation("Инкассация", details.collection, R.drawable.img_visit_collection_result, Color.Unspecified, result = VisitResult.Collection),
    VisitOperation("Возврат", details.returns, R.drawable.ic_return, result = VisitResult.Return)
)
