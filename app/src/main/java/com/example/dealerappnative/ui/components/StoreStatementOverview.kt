package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.StatementDesign as Design
import java.math.BigDecimal

@Composable
internal fun StoreStatementOverview(store: StoreUiModel, period: String, totals: List<StatementCurrencyTotals>) {
    Column(Modifier.fillMaxWidth().background(Color.White).padding(vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(36.dp).background(Design.surface, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                DealerIcon(R.drawable.ic_store, Design.secondary, 22.dp)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(store.name, style = Design.name, color = Design.text, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(store.address, style = Design.caption, color = Design.secondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Text(period, style = Design.caption, color = Design.secondary, modifier = Modifier.padding(horizontal = 16.dp))
        if (totals.isNotEmpty()) Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatementTotalTile("Заказы", totals.map { it.currency to it.orders }, Design.text)
            StatementTotalTile("Оплачено", totals.map { it.currency to it.payments }, Color(0xFF15803D))
            StatementTotalTile("Возвраты", totals.map { it.currency to it.returns }, Color(0xFF15803D))
        }
    }
}

@Composable
private fun StatementTotalTile(label: String, values: List<Pair<String, BigDecimal>>, color: Color) {
    Column(Modifier.widthIn(min = 136.dp).background(Design.surface, RoundedCornerShape(14.dp)).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(label, style = Design.body, color = Design.secondary)
        values.forEach { (currency, amount) -> Text(statementMoney(amount, currency), style = Design.name, color = color, maxLines = 1) }
    }
}
