package com.example.dealerappnative.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.StatementType
import com.example.dealerappnative.model.StoreUiModel
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.StatementDesign as Design

/** Fixed store identity; no duplicated store card or collapsing toolbar. */
@Composable
internal fun StoreStatementAppBar(store: StoreUiModel, filtered: Boolean, onBack: () -> Unit, onFilters: () -> Unit) {
    Row(Modifier.fillMaxWidth().heightIn(min = 64.dp).background(Color.White).padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically) {
        SoftIconButton(onBack, Modifier.size(44.dp).semantics { contentDescription = "Назад" }) {
            DealerIcon(R.drawable.ic_arrow_back, Design.text, 22.dp)
        }
        Box(Modifier.size(34.dp).background(Design.surface, RoundedCornerShape(11.dp)), contentAlignment = Alignment.Center) {
            DealerIcon(R.drawable.ic_store, Design.secondary, 22.dp)
        }
        Column(Modifier.weight(1f).padding(start = 10.dp, end = 8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(store.name, style = Design.name, color = Design.text, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(store.address, style = Design.caption, color = Design.secondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        SoftIconButton(onFilters, Modifier.size(44.dp).semantics { contentDescription = "Фильтры истории взаиморасчётов" }) {
            Box(Modifier.size(26.dp), contentAlignment = Alignment.Center) {
                DealerIcon(R.drawable.ic_collection_filter, Design.text, 22.dp)
                if (filtered) Box(Modifier.align(Alignment.TopEnd).size(6.dp).background(DealerDesign.accent, CircleShape))
            }
        }
    }
}

/** Market Category: 32dp visual height, 12dp sides, 13/18 Semibold. Touch area stays >=44dp. */
@Composable
internal fun StoreStatementQuickFilters(types: Set<StatementType>, dated: Boolean,
    onType: (StatementType) -> Unit, onDates: () -> Unit) {
    Row(Modifier.fillMaxWidth().background(Color.White).horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        StatementQuickChoice("Оплаты", StatementType.Payments in types) { onType(StatementType.Payments) }
        StatementQuickChoice("Заказы", StatementType.Orders in types) { onType(StatementType.Orders) }
        StatementQuickChoice("Возвраты", StatementType.Returns in types) { onType(StatementType.Returns) }
        StatementQuickChoice("Дата", dated, onDates)
    }
}

@Composable
private fun StatementQuickChoice(label: String, active: Boolean, onClick: () -> Unit) {
    val background by animateColorAsState(if (active) DealerDesign.accent else Design.surface, tween(200), label = "statement-quick-choice")
    val foreground by animateColorAsState(if (active) Color.White else Design.text, tween(200), label = "statement-quick-label")
    Box(Modifier.heightIn(min = 44.dp).clip(RoundedCornerShape(50.dp)).softCardPress(onClick = onClick)
        .semantics { selected = active }.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
        Box(Modifier.heightIn(min = 32.dp).background(background, RoundedCornerShape(50.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp), contentAlignment = Alignment.Center) {
            Text(label, style = Design.choice, color = foreground, maxLines = 1)
        }
    }
}
