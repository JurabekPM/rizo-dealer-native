package com.example.dealerappnative.screens

import com.example.dealerappnative.ui.components.softCardPress
import com.example.dealerappnative.ui.components.LocalStoreActions
import com.example.dealerappnative.ui.components.StoreStatementEntry
import androidx.annotation.DrawableRes
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.StoreUiModel
import com.example.dealerappnative.ui.components.ChildScreenHeader
import com.example.dealerappnative.ui.components.DealerIcon
import com.example.dealerappnative.ui.components.MenuRow
import com.example.dealerappnative.ui.components.PrototypeCreateButton
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun StoreDetailScreen(store: StoreUiModel, onBack: () -> Unit, onOpenMap: () -> Unit) {
    Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
        ChildScreenHeader(store.name, onBack, height = 68.dp)
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item { StoreSummary(store, onOpenMap) }
            item { StoreSettings(store) }
            item {
                Row(Modifier.fillMaxWidth().height(56.dp).clip(RoundedCornerShape(DealerDesign.radius))
                    .background(Color.White).padding(horizontal = DealerDesign.pageInset),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("Конечное сальдо", style = DealerDesign.body, color = DealerDesign.secondary,
                        modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    Text(store.debtDirection.formatAmount(store.debt), style = DealerDesign.body,
                        color = store.debtDirection.color)
                    Spacer(Modifier.width(4.dp))
                    Box(Modifier.rotate(90f)) { DealerIcon(R.drawable.ic_chevron) }
                }
            }
            item { StoreStatementEntry(store) }
            item {
                Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(DealerDesign.radius)).background(Color.White)) {
                    Text("Отчёты", style = DealerDesign.type(22, 28),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp))
                    storeReports.forEachIndexed { index, (title, icon) ->
                        MenuRow(title, icon, divider = index != storeReports.lastIndex, rowHeight = 56.dp)
                    }
                }
            }
        }
        PrototypeCreateButton(label = "Обновить")
    }
}

@Composable
private fun StoreSummary(store: StoreUiModel, onOpenMap: () -> Unit) {
    val actions = LocalStoreActions.current
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(DealerDesign.radius))
        .background(Color.White).padding(horizontal = DealerDesign.pageInset, vertical = 12.dp)) {
        Row(Modifier.fillMaxWidth().height(44.dp), verticalAlignment = Alignment.CenterVertically) {
            DealerIcon(R.drawable.ic_store)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(store.name, style = DealerDesign.cardTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(store.id, style = DealerDesign.detail, color = DealerDesign.secondary)
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StoreAction("Открыть карту", R.drawable.ic_route, Modifier.weight(1f), onClick = onOpenMap)
            StoreAction("Позвонить", R.drawable.ic_phone_call, Modifier.weight(1f), onClick = { actions.dial(store.phoneNumber) })
        }
    }
}

@Composable
private fun StoreAction(label: String, @DrawableRes icon: Int, modifier: Modifier, onClick: (() -> Unit)? = null) {
    Row(modifier.height(48.dp).then(if (onClick != null) Modifier.softCardPress(onClick = onClick) else Modifier).clip(RoundedCornerShape(DealerDesign.radius))
        .background(DealerDesign.background)
        .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        DealerIcon(icon)
        Spacer(Modifier.width(8.dp))
        Text(label, style = DealerDesign.body, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun StoreSettings(store: StoreUiModel) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(DealerDesign.radius))
        .background(Color.White).padding(horizontal = DealerDesign.pageInset, vertical = 4.dp)) {
        Row(Modifier.fillMaxWidth().height(48.dp), verticalAlignment = Alignment.CenterVertically) {
            DealerIcon(R.drawable.ic_info)
            Spacer(Modifier.width(12.dp))
            Text("Отображение цен заказчику", style = DealerDesign.body, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            // Display the store's sample setting; editing is outside this screen prototype.
            Box(Modifier.width(36.dp).height(22.dp).clip(CircleShape)
                .background(if (store.showPrices) DealerDesign.accent else Color(0xFFD5DADD)).padding(3.dp)) {
                Box(Modifier.size(16.dp).align(if (store.showPrices) Alignment.CenterEnd else Alignment.CenterStart)
                    .clip(CircleShape).background(Color.White))
            }
        }
        Row(Modifier.fillMaxWidth().height(48.dp), verticalAlignment = Alignment.CenterVertically) {
            DealerIcon(R.drawable.ic_money)
            Spacer(Modifier.width(12.dp))
            Text("Валюта", style = DealerDesign.body, modifier = Modifier.weight(1f))
            Text(store.currency, style = DealerDesign.body)
            Box(Modifier.rotate(90f)) { DealerIcon(R.drawable.ic_chevron) }
        }
        Row(Modifier.fillMaxWidth().height(68.dp), verticalAlignment = Alignment.CenterVertically) {
            DealerIcon(R.drawable.ic_people)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text("Роли", style = DealerDesign.body)
                Spacer(Modifier.height(4.dp))
                Text(store.assignedRole, style = DealerDesign.detail, color = DealerDesign.secondary)
            }
            DealerIcon(R.drawable.ic_chevron, DealerDesign.tertiary)
        }
    }
}

private val storeReports = listOf(
    "Заявки" to R.drawable.ic_box,
    "Визиты" to R.drawable.ic_visit,
    "Ревизии" to R.drawable.ic_edit,
    "Инкассации" to R.drawable.ic_money,
    "Возвраты" to R.drawable.ic_return
)
