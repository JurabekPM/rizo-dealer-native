package com.example.dealerappnative.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.*
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

/** Search selection is transactional. Back never changes the order being edited. */
@Composable
internal fun RequestProductPickerScreen(initial: List<Int>, onBack: () -> Unit, onConfirm: (List<Int>) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var draft by rememberSaveable { mutableStateOf(normalizeRequestQuantities(initial)) }
    var quantityIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    val quantities = normalizeRequestQuantities(draft)
    val listState = rememberLazyListState()
    val indexes = remember(query) { requestCatalog.indices.filter {
        requestCatalog[it].name.contains(query.trim(), true) || requestCatalog[it].id.contains(query.trim())
    } }
    BackHandler(onBack = onBack)
    Column(Modifier.fillMaxSize().background(Color.White).imePadding()) {
        Row(Modifier.fillMaxWidth().height(56.dp).padding(end = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            SoftIconButton(onClick = onBack, modifier = Modifier.size(48.dp).semantics { contentDescription = "Назад" }) {
                DealerIcon(R.drawable.ic_arrow_back, DealerDesign.text)
            }
            Box(Modifier.weight(1f)) {
                SearchField(query, { query = it }, placeholder = "Поиск товаров", height = 36.dp, textStyle = AgentVisitDesign.name)
            }
            if (query.isNotEmpty()) SoftIconButton(onClick = { query = "" }, modifier = Modifier.size(36.dp)
                .semantics { contentDescription = "Очистить поиск" }) { DealerIcon(R.drawable.ic_agent_clear, size = 20.dp) }
        }
        Text("Выбрано: ${quantities.count { it > 0 }} товаров", style = AgentVisitDesign.detail, color = DealerDesign.secondary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
        LazyColumn(Modifier.weight(1f), state = listState) {
            items(indexes, key = { requestCatalog[it].id }, contentType = { "request-product" }) { index ->
                val product = requestCatalog[index]
                val quantity = quantities[index]
                Row(Modifier.fillMaxWidth().softCardPress(enabled = product.stock > 0, onClick = { quantityIndex = index })
                    .padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    RequestProductThumbnail(product, 40.dp)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(product.name, style = AgentVisitDesign.name, color = DealerDesign.text,
                            maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text(historyMoney(product.price) + "/шт", style = AgentVisitDesign.detail, color = DealerDesign.text)
                        Text(if (product.stock > 0) "В наличии" else "Нет в наличии", style = AgentVisitDesign.detail,
                            color = if (product.stock > 0) Color(0xFF22A447) else DealerDesign.secondary)
                        product.previousPrice?.let { previous ->
                            Text(historyMoney(previous) + "/шт", style = AgentVisitDesign.detail.copy(textDecoration = TextDecoration.LineThrough),
                                color = DealerDesign.tertiary)
                        }
                    }
                    if (product.stock > 0) Box(Modifier.padding(start = 12.dp)) {
                        SoftTextButton(onClick = { quantityIndex = index }, modifier = Modifier.size(36.dp)
                            .clip(RoundedCornerShape(8.dp)).background(DealerDesign.accent.copy(alpha = .07f))
                            .semantics { contentDescription = "Выбрать количество: ${product.name}, $quantity" }) {
                            Text(if (quantity > 0) "$quantity" else "+", style = AgentVisitDesign.screenTitle, color = DealerDesign.accent)
                        }
                    }
                }
                HorizontalDivider(color = DealerDesign.divider, modifier = Modifier.padding(start = 68.dp, end = 16.dp))
            }
            if (indexes.isEmpty()) item { HistoryEmpty("Товары не найдены") }
        }
        Box(Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
            ReturnPrimaryButton("Продолжить", quantities.any { it > 0 }) { onConfirm(quantities) }
        }
    }
    quantityIndex?.let { index -> RequestQuantitySheet(requestCatalog[index], quantities[index],
        onDismiss = { quantityIndex = null }, onConfirm = {
            draft = quantities.toMutableList().apply { this[index] = it }; quantityIndex = null
        }) }
}
