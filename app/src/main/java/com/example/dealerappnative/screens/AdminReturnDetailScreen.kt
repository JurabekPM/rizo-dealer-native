package com.example.dealerappnative.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.*
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

/** Administrator return document follows the supplied timeline/summary/products/reason reference. */
@Composable
internal fun AdminReturnDetailScreen(document: AdminReturnDocument, onBack: () -> Unit) {
    val record = document.record
    Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
        HistoryHeader(document.visit.name, onBack)
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item(key = "store") { HistoryStoreSummary(document.visit, showId = true) }
            item(key = "timeline") {
                val dates = buildList {
                    add(record.createdAt to if (document.acceptedAt != null) "Магазин подтвердил" else "Дата оформления")
                    document.acceptedAt?.let { add(it to "Возврат товаров админу") }
                    document.completedAt?.let { add(it to "Дата завершения") }
                }
                HistorySurface {
                    dates.forEachIndexed { index, (time, label) ->
                        ReturnTimelineRow(time, label, index == dates.lastIndex)
                    }
                }
            }
            item(key = "summary") { HistorySurface {
                HistoryValue("Номер возврата", "#${record.documentId}")
                HistoryValue("Товары", "${record.items.sumOf { it.quantity }}")
                HistoryValue("Доставщик", document.visit.details.employee)
                HistoryValue("Общая сумма", historyMoney(record.total))
            } }
            itemsIndexed(record.items, key = { index, _ -> index }) { _, product ->
                HistorySurface {
                    Row {
                        val catalogProduct = returnCatalog.find { it.name == product.name }
                        if (catalogProduct != null) ReturnProductThumbnail(catalogProduct)
                        else Box(Modifier.size(48.dp)) { DealerIcon(R.drawable.ic_box) }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(product.name, style = AgentVisitDesign.emphasis, color = DealerDesign.text)
                            Text(historyMoney(product.price) + "/шт", style = AgentVisitDesign.emphasis, color = DealerDesign.text)
                            Text("${product.quantity} шт", style = AgentVisitDesign.emphasis, color = DealerDesign.text)
                        }
                    }
                }
            }
            item(key = "reason") { HistorySurface {
                Text("Причина возврата", style = AgentVisitDesign.screenTitle, color = DealerDesign.text)
                Spacer(Modifier.height(8.dp))
                Text(record.reasonLabel.ifBlank { record.comment.ifBlank { "Не указана" } },
                    style = AgentVisitDesign.name, color = DealerDesign.text)
                if (record.reasonLabel.isNotBlank() && record.comment.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(record.comment, style = AgentVisitDesign.name, color = DealerDesign.text)
                }
            } }
        }
    }
}

@Composable
private fun ReturnTimelineRow(time: Long, label: String, last: Boolean) {
    Row(Modifier.height(IntrinsicSize.Min)) {
        Box(Modifier.width(8.dp).fillMaxHeight().drawBehind {
            if (!last) drawLine(DealerDesign.accent, Offset(size.width / 2, 8.dp.toPx()),
                Offset(size.width / 2, size.height + 4.dp.toPx()), strokeWidth = 2.dp.toPx())
        }) {
            Box(Modifier.padding(top = 4.dp).size(8.dp).background(DealerDesign.accent, CircleShape))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.padding(bottom = if (last) 0.dp else 12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(historyDate(time), style = AgentVisitDesign.emphasis, color = DealerDesign.text)
            Text(label, style = AgentVisitDesign.detail, color = DealerDesign.secondary)
        }
    }
}
