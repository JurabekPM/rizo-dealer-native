package com.example.dealerappnative.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

/** Saved order uses its own timestamp and lines, with no invented warehouse/delivery events. */
@Composable
internal fun VisitRequestDetailScreen(visit: VisitStore, record: VisitRequestRecord,
    onBack: () -> Unit, onEdit: (() -> Unit)? = null) {
    BackHandler(onBack = onBack)
    Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
        HistoryHeader("Заявка", onBack)
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item(key = "created") { HistorySurface {
                HistoryDateLine("Дата отправления", record.createdAt)
                HistoryValue("Номер заявки", record.documentId)
            } }
            item(key = "total") { HistorySurface { HistoryValue("Общая сумма", historyMoney(record.total)) } }
            items(record.items, key = { it.productId }, contentType = { "saved-request-line" }) { line ->
                HistorySurface {
                    Row(verticalAlignment = Alignment.Top) {
                        RequestProductThumbnail(requestCatalog.find { it.id == line.productId }
                            ?: RequestCatalogProduct(line.productId, line.name, line.price, 0))
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(line.name, style = AgentVisitDesign.name, color = DealerDesign.text)
                            Text(historyMoney(line.price) + "/шт", style = AgentVisitDesign.detail, color = DealerDesign.text)
                            Text("${line.quantity} шт", style = AgentVisitDesign.emphasis, color = DealerDesign.text)
                        }
                    }
                }
            }
        }
        if (onEdit != null) Box(Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
            ReturnPrimaryButton("Редактировать", onClick = onEdit)
        }
    }
}
