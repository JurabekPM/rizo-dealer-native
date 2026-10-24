package com.example.dealerappnative.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.*
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.RequestPickerDesign

/** Figma SearchSelection; only Continue applies this picker draft to the order. */
@Composable
internal fun RequestProductPickerScreen(initial: List<Int>, onBack: () -> Unit, onConfirm: (List<Int>) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var draft by rememberSaveable { mutableStateOf(normalizeRequestQuantities(initial)) }
    var quantityIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    val quantities = normalizeRequestQuantities(draft)
    val listState = rememberLazyListState()
    val focus = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    val indexes = remember(query) { requestCatalog.indices.filter {
        requestCatalog[it].name.contains(query.trim(), true) || requestCatalog[it].id.contains(query.trim())
    }.sortedBy { requestCatalog[it].displayOrder } }
    val back: () -> Unit = { keyboard?.hide(); onBack() }
    BackHandler(onBack = back)
    Column(Modifier.fillMaxSize().background(Color.White).imePadding()) {
        Row(Modifier.fillMaxWidth().shadow(4.dp, ambientColor = Color.Black.copy(alpha = .06f),
            spotColor = Color.Black.copy(alpha = .06f)).background(Color.White)
            .height(72.dp).padding(horizontal = 16.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(24.dp).fillMaxHeight().softCardPress(onClick = back)
                .semantics { contentDescription = "Назад" }, contentAlignment = Alignment.Center) {
                DealerIcon(R.drawable.ic_request_back, DealerDesign.text)
            }
            Spacer(Modifier.width(12.dp))
            Box(Modifier.weight(1f)) { RequestPickerSearch(query, { query = it }, focus) }
        }
        Row(Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 12.dp)
            .height(24.dp), verticalAlignment = Alignment.CenterVertically) {
            DealerIcon(R.drawable.ic_request_done_all)
            Spacer(Modifier.width(12.dp))
            Text("Выбрано: ${quantities.count { it > 0 }} ${requestProductWord(quantities.count { it > 0 })}",
                style = RequestPickerDesign.body, color = DealerDesign.text)
        }
        HorizontalDivider(color = DealerDesign.divider, modifier = Modifier.padding(horizontal = 16.dp))
        LazyColumn(Modifier.weight(1f), state = listState) {
            items(indexes, key = { requestCatalog[it].id }, contentType = { "request-product" }) { index ->
                RequestPickerProduct(requestCatalog[index], quantities[index], onSelect = { quantityIndex = index })
                HorizontalDivider(color = DealerDesign.divider, modifier = Modifier.padding(horizontal = 16.dp))
            }
            if (indexes.isEmpty()) item { HistoryEmpty("Товары не найдены") }
        }
        Box(Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
            RequestPickerButton("Продолжить", quantities.any { it > 0 }) { keyboard?.hide(); onConfirm(quantities) }
        }
    }
    LaunchedEffect(quantityIndex) {
        if (quantityIndex == null) { focus.requestFocus(); keyboard?.show() }
    }
    quantityIndex?.let { index -> RequestQuantitySheet(requestCatalog[index], quantities[index],
        onDismiss = { quantityIndex = null }, keepKeyboardOpen = true, onConfirm = {
            draft = quantities.toMutableList().apply { this[index] = it }; quantityIndex = null
        }) }
}

private fun requestProductWord(count: Int): String = when {
    count % 100 in 11..14 -> "товаров"
    count % 10 == 1 -> "товар"
    count % 10 in 2..4 -> "товара"
    else -> "товаров"
}
