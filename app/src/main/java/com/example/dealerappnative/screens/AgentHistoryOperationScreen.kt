package com.example.dealerappnative.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.AgentHistoryEntry
import com.example.dealerappnative.model.VisitResult
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

/** One recorded document, opened from a visit, revision list or monthly plan ledger. */
@Composable
internal fun AgentHistoryOperationScreen(entry: AgentHistoryEntry, operation: HistoryOperation, onBack: () -> Unit) {
    if (operation.result == VisitResult.Request && entry.requestRecord != null) {
        VisitRequestDetailScreen(entry.visit, entry.requestRecord, onBack)
        return
    }
    Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
        HistoryHeader(entry.visit.name, onBack)
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item(key = "store") { HistoryStoreSummary(entry.visit) }
            item(key = "date") { HistorySurface {
                Row(verticalAlignment = Alignment.Top) {
                    Box(Modifier.padding(top = 5.dp).size(8.dp).background(Color(0xFF22C348), CircleShape))
                    Spacer(Modifier.width(12.dp))
                    HistoryDateLine("Дата завершения", if (operation.result == VisitResult.Return) entry.returnRecord?.createdAt ?: requireNotNull(entry.session.finishedAt) else requireNotNull(entry.session.finishedAt))
                }
            } }
            item(key = "summary") { HistorySurface {
                HistoryValue(operation.title, "#${operation.documentId}")
                if (operation.result == VisitResult.Revision) {
                    val expected = operation.products.sumOf { it.price * it.expected }
                    val shortage = operation.products.sumOf { it.price * (it.expected - it.quantity).coerceAtLeast(0) }
                    val excess = operation.products.sumOf { it.price * (it.quantity - it.expected).coerceAtLeast(0) }
                    HistoryValue("Текущая сумма товаров", historyMoney(expected))
                    HistoryValue("Ревизия факт", historyMoney(operation.amount))
                    HistoryValue("Недостача", (if (shortage > 0) "−" else "") + historyMoney(shortage), Color(0xFFE96666))
                    HistoryValue("Излишки", (if (excess > 0) "+" else "") + historyMoney(excess), Color(0xFF22A447))
                    Spacer(Modifier.height(8.dp))
                    HistoryValue("Сальдо магазина", entry.visit.debtDirection.formatAmount(entry.visit.debt), entry.visit.debtDirection.color)
                } else {
                    HistoryValue(if (operation.result == VisitResult.Collection) "Получено" else "Общая сумма",
                        historyMoney(operation.amount), Color(0xFF22A447))
                    if (operation.result == VisitResult.Collection) {
                        HistoryValue("Способ оплаты", "Наличные")
                        HistoryValue("Принял", entry.visit.details.employee)
                    } else HistoryValue("Количество товаров", "${operation.products.size}")
                }
            } }
            if (operation.result == VisitResult.Return && !entry.returnRecord?.reasonLabel.isNullOrBlank()) item(key = "return-reason") {
                HistorySurface {
                    Text("Причина возврата", style = AgentVisitDesign.detail, color = DealerDesign.secondary)
                    Spacer(Modifier.height(8.dp))
                    Text(entry.returnRecord!!.reasonLabel, style = AgentVisitDesign.name, color = DealerDesign.text)
                }
            }
            if (operation.result == VisitResult.Return && !entry.returnRecord?.comment.isNullOrBlank()) item(key = "return-comment") {
                HistorySurface {
                    Text("Комментарий", style = AgentVisitDesign.emphasis, color = DealerDesign.text)
                    Spacer(Modifier.height(8.dp))
                    Text(entry.returnRecord!!.comment, style = AgentVisitDesign.name, color = DealerDesign.text)
                }
            }
            items(operation.products, key = { it.name }, contentType = { "product" }) { product ->
                HistorySurface {
                    Row(verticalAlignment = Alignment.Top) {
                        DealerIcon(R.drawable.ic_box)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(product.name, style = AgentVisitDesign.name, color = DealerDesign.text)
                            Text(historyMoney(product.price) + "/шт", style = AgentVisitDesign.detail, color = DealerDesign.text)
                            if (operation.result == VisitResult.Revision) {
                                Text("${product.expected} шт → В магазине: ${product.quantity} шт",
                                    style = AgentVisitDesign.detail, color = DealerDesign.secondary)
                                val difference = product.quantity - product.expected
                                Text(when {
                                    difference < 0 -> "Недостача ${-difference} шт"
                                    difference > 0 -> "Излишки $difference шт"
                                    else -> "Количество совпадает"
                                }, style = AgentVisitDesign.detail,
                                    color = if (difference < 0) Color(0xFFE69B20) else Color(0xFF22A447))
                            } else Text("${product.quantity} шт · ${historyMoney(product.total)}",
                                style = AgentVisitDesign.detail, color = DealerDesign.secondary)
                        }
                    }
                }
            }
        }
    }
}
