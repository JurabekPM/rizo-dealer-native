package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dealerappnative.model.StatementCurrencyTotals
import com.example.dealerappnative.model.StatementKind
import com.example.dealerappnative.model.statementMoney
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.StatementDesign as Design
import java.math.BigDecimal

private val tileGap = 8.dp
private val tileInset = 8.dp
private val compactAmountStyle = Design.amount.copy(fontSize = 15.sp, lineHeight = 20.sp)

/** Activity tiles appear only at the history's top; the store identity stays in the AppBar. */
@Composable
internal fun StoreStatementOverview(totals: List<StatementCurrencyTotals>, count: Int, filtered: Boolean) {
    Column(
        Modifier.fillMaxWidth().background(DealerDesign.surface).padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (totals.isNotEmpty()) StatementTotals(totals)
        Text(
            (if (filtered) "Найдено операций: " else "Всего операций: ") + count,
            style = Design.caption,
            color = Design.secondary
        )
    }
}

private data class TotalTile(val kind: StatementKind, val label: String, val amounts: List<String>)

@Composable
private fun StatementTotals(totals: List<StatementCurrencyTotals>) {
    val tiles = remember(totals) {
        listOf(
            TotalTile(StatementKind.Order, "Заказы", totals.displayAmounts { it.orders }),
            TotalTile(StatementKind.Payment, "Оплачено", totals.displayAmounts { it.payments }),
            TotalTile(StatementKind.Return, "Возвраты", totals.displayAmounts { it.returns })
        )
    }
    val measurer = rememberTextMeasurer(cacheSize = 0)
    val widths = remember(tiles, measurer) {
        tiles.map { tile ->
            val label = measurer.measure(tile.label, Design.label, softWrap = false).size.width
            val amount = tile.amounts.maxOf {
                measurer.measure(it, Design.amount, softWrap = false).size.width
            }
            val compactAmount = tile.amounts.maxOf {
                measurer.measure(it, compactAmountStyle, softWrap = false).size.width
            }
            Triple(label, amount, compactAmount)
        }
    }
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val density = LocalDensity.current
        val (fullTextWidth, gridTextWidth, gap) = with(density) {
            val inset = tileInset.roundToPx() * 2
            val gap = tileGap.roundToPx()
            val width = maxWidth.roundToPx()
            Triple(width - inset, (width - gap * (tiles.size - 1)) / tiles.size - inset, gap)
        }
        if (widths.all { (label, _, compactAmount) -> maxOf(label, compactAmount) <= gridTextWidth }) {
            Row(
                Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(tileGap)
            ) {
                tiles.forEach { tile ->
                    StatementTotalTile(tile, Modifier.weight(1f).fillMaxHeight(), inline = false, amountStyle = compactAmountStyle)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(tileGap)) {
                tiles.forEachIndexed { index, tile ->
                    val (label, amount) = widths[index]
                    StatementTotalTile(tile, Modifier.fillMaxWidth(), inline = label + gap + amount <= fullTextWidth)
                }
            }
        }
    }
}

@Composable
private fun StatementTotalTile(
    tile: TotalTile,
    modifier: Modifier,
    inline: Boolean,
    amountStyle: TextStyle = Design.amount
) {
    val color = Design.operationColor(tile.kind)
    val surface = modifier.cardShadow().background(Design.surface).padding(tileInset)
    if (inline) {
        Row(surface, horizontalArrangement = Arrangement.spacedBy(tileGap), verticalAlignment = Alignment.CenterVertically) {
            Text(tile.label, style = Design.label, color = color, modifier = Modifier.weight(1f))
            StatementTotalAmounts(tile.amounts, amountStyle, color, Alignment.End)
        }
    } else {
        Column(surface, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(tile.label, style = Design.label, color = color)
            StatementTotalAmounts(tile.amounts, amountStyle, color, Alignment.Start)
        }
    }
}

@Composable
private fun StatementTotalAmounts(amounts: List<String>, style: TextStyle, color: Color, alignment: Alignment.Horizontal) {
    Column(horizontalAlignment = alignment, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        amounts.forEach { amount ->
            Text(amount, style = style, color = color)
        }
    }
}

private fun List<StatementCurrencyTotals>.displayAmounts(amountFor: (StatementCurrencyTotals) -> BigDecimal): List<String> {
    val amounts = map { it.currency to amountFor(it) }
    val nonZero = amounts.filter { (_, amount) -> amount.signum() != 0 }
    return nonZero.ifEmpty { amounts }.map { (currency, amount) -> statementMoney(amount, currency) }
}
