package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.theme.StatementDesign as Design
import java.math.BigDecimal

/** Activity tiles appear only at the history's top; the store identity stays in the AppBar. */
@Composable
internal fun StoreStatementOverview(totals: List<StatementCurrencyTotals>, count: Int, filtered: Boolean) {
    Column(Modifier.fillMaxWidth().background(Color.White).padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (totals.isNotEmpty()) Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatementTotalTile("Заказы", totals.map { it.currency to it.orders }, Design.order)
            StatementTotalTile("Оплачено", totals.map { it.currency to it.payments }, Design.payment)
            StatementTotalTile("Возвраты", totals.map { it.currency to it.returns }, Design.returned)
        }
        Text((if (filtered) "Найдено операций: " else "Всего операций: ") + count,
            style = Design.caption, color = Design.secondary, modifier = Modifier.padding(horizontal = 16.dp))
    }
}

@Composable
private fun StatementTotalTile(label: String, values: List<Pair<String, BigDecimal>>, color: Color) {
    Column(Modifier.widthIn(min = 120.dp).background(Design.surface, RoundedCornerShape(12.dp)).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = Design.label, color = color)
        values.forEach { (currency, amount) -> Text(statementMoney(amount, currency), style = Design.amount, color = color, maxLines = 1) }
    }
}
