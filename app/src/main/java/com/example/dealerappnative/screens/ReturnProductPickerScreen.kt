package com.example.dealerappnative.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.returnCatalog
import com.example.dealerappnative.data.historyMoney
import com.example.dealerappnative.data.normalizeReturnQuantities
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

/** Product selection is a draft; backing out leaves the return unchanged. */
@Composable
internal fun ReturnProductPickerScreen(initial: List<Int>, orderDate: String, onBack: () -> Unit, onConfirm: (List<Int>) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var draft by rememberSaveable { mutableStateOf(normalizeReturnQuantities(initial)) }
    val quantities = normalizeReturnQuantities(draft)
    var selectedIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    BackHandler(onBack = onBack)
    val indexes = remember(query) {
        returnCatalog.indices.filter { returnCatalog[it].name.contains(query.trim(), true) || returnCatalog[it].barcode.contains(query.trim()) }
    }
    Column(Modifier.fillMaxSize().background(Color.White).imePadding()) {
        Row(Modifier.fillMaxWidth().height(56.dp).padding(end = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            SoftIconButton(onClick = onBack, modifier = Modifier.size(48.dp).semantics { contentDescription = "Назад" }) {
                DealerIcon(R.drawable.ic_arrow_back, DealerDesign.text)
            }
            Box(Modifier.weight(1f)) { SearchField(query, { query = it }, height = 36.dp, textStyle = AgentVisitDesign.name) }
        }
        Text("Выбрано: ${quantities.count { it > 0 }}", style = AgentVisitDesign.detail, color = DealerDesign.secondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
        LazyColumn(Modifier.weight(1f)) {
            items(indexes, key = { returnCatalog[it].barcode }) { index ->
                val product = returnCatalog[index]
                val quantity = quantities[index]
                Row(Modifier.fillMaxWidth().softCardPress { selectedIndex = index }.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    ReturnProductThumbnail(product, 40.dp); Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(product.name, style = AgentVisitDesign.emphasis, color = DealerDesign.text, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text(historyMoney(product.price) + "/шт · ${product.available} шт", style = AgentVisitDesign.detail, color = DealerDesign.text)
                        Text("Заявка: $orderDate · ${product.originalQuantity} шт", style = AgentVisitDesign.detail, color = DealerDesign.secondary,
                            maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    SoftTextButton(onClick = { selectedIndex = index }, modifier = Modifier.padding(start = 12.dp).size(36.dp)
                        .clip(RoundedCornerShape(8.dp)).background(DealerDesign.accent.copy(alpha = .07f))
                        .semantics { contentDescription = if (quantity > 0) "Изменить количество: ${product.name}, $quantity" else "Добавить: ${product.name}" }) {
                        Text(if (quantity > 0) "$quantity" else "+", style = if (quantity > 0) AgentVisitDesign.emphasis else AgentVisitDesign.sheetTitle,
                            color = DealerDesign.accent)
                    }
                }
                HorizontalDivider(color = DealerDesign.divider, modifier = Modifier.padding(start = 68.dp, end = 16.dp))
            }
            if (indexes.isEmpty()) item { HistoryEmpty("Товары не найдены") }
        }
        Box(Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
            ReturnPrimaryButton("Подтвердить") { onConfirm(quantities) }
        }
    }
    selectedIndex?.let { index -> ReturnQuantitySheet(returnCatalog[index], quantities[index], onDismiss = { selectedIndex = null }) {
        draft = quantities.toMutableList().apply { this[index] = it }; selectedIndex = null
    } }
}
