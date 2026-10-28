package com.example.dealerappnative.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.StatementType
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.StatementDesign as Design

@Composable
internal fun StoreStatementAppBar(storeName: String, filtered: Boolean, onBack: () -> Unit, onFilters: () -> Unit) {
    Row(Modifier.fillMaxWidth().height(60.dp).background(Color.White).padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        SoftIconButton(onBack, Modifier.size(48.dp).semantics { contentDescription = "Назад" }) {
            DealerIcon(R.drawable.ic_arrow_back, Design.text)
        }
        Column(Modifier.weight(1f).padding(horizontal = 8.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("Взаиморасчёты", style = Design.name, color = Design.text, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(storeName, style = Design.caption, color = Design.secondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        SoftIconButton(onFilters, Modifier.size(48.dp).semantics { contentDescription = "Фильтры истории взаиморасчётов" }) {
            Box(Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                DealerIcon(R.drawable.ic_collection_filter, Design.text, 22.dp)
                if (filtered) Box(Modifier.align(Alignment.TopEnd).size(6.dp).background(DealerDesign.accent, CircleShape))
            }
        }
    }
}

/** Native enter-always behavior consumes one header offset, without per-row scroll observers. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StoreStatementQuickFilters(type: StatementType, dated: Boolean, state: TopAppBarState,
    onType: (StatementType) -> Unit, onDates: () -> Unit) {
    val density = LocalDensity.current
    val height = 44.dp * density.fontScale.coerceAtLeast(1f) + 12.dp
    SideEffect { state.heightOffsetLimit = -with(density) { height.toPx() } }
    val removed = with(density) { (-state.heightOffset).toDp() }.coerceIn(0.dp, height)
    val progress = removed / height
    Box(Modifier.fillMaxWidth().height(height - removed).clipToBounds().background(Color.White)) {
        Row(Modifier.fillMaxWidth().wrapContentHeight(Alignment.Top, unbounded = true).height(height)
            .graphicsLayer { alpha = 1f - progress; translationY = -6.dp.toPx() * progress }
            .horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            StatementQuickChoice("Дата", dated, onDates, calendar = true)
            StatementQuickChoice("Все", type == StatementType.All, { onType(StatementType.All) })
            StatementQuickChoice("Оплаты", type == StatementType.Payments, { onType(StatementType.Payments) })
            StatementQuickChoice("Заказы", type == StatementType.Orders, { onType(StatementType.Orders) })
            StatementQuickChoice("Возвраты", type == StatementType.Returns, { onType(StatementType.Returns) })
        }
    }
}

@Composable
private fun StatementQuickChoice(label: String, active: Boolean, onClick: () -> Unit, calendar: Boolean = false) {
    val background by animateColorAsState(if (active) DealerDesign.accent else Design.surface, tween(220), label = "statement-quick-choice")
    val foreground = if (active) Color.White else Design.text
    Row(Modifier.fillMaxHeight().clip(RoundedCornerShape(24.dp)).background(background)
        .softCardPress(onClick = onClick).semantics { selected = active }.padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        if (calendar) DealerIcon(R.drawable.ic_agent_schedule, foreground, 18.dp)
        Text(label, style = Design.choice, color = foreground, maxLines = 1)
    }
}
