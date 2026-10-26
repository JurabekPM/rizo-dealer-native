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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.data.*
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.RequestPickerDesign

/** Shared picker appearance; returns keep their own stock, source info and transactional confirmation. */
@Composable
internal fun ReturnProductPickerScreen(initial: List<Int>, orderDate: String, onBack: () -> Unit, onConfirm: (List<Int>) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var draft by rememberSaveable { mutableStateOf(normalizeReturnQuantities(initial)) }
    var quantityIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    val quantities = remember(draft) { normalizeReturnQuantities(draft) }
    val selectedCount = quantities.count { it > 0 }
    val total = remember(quantities) { returnPickerTotal(quantities) }
    val listState = rememberLazyListState()
    val focus = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val indexes = remember(query) {
        returnCatalog.indices.filter {
            returnCatalog[it].name.contains(query.trim(), true) || returnCatalog[it].barcode.contains(query.trim())
        }.sortedBy { returnCatalog[it].available <= 0 }
    }
    val back: () -> Unit = { keyboard?.hide(); onBack() }
    BackHandler(onBack = back)
    Column(Modifier.fillMaxSize().background(Color.White).imePadding()) {
        ProductPickerHeader(query, { query = it }, focus, back)
        LazyColumn(Modifier.weight(1f), state = listState) {
            items(indexes, key = { returnCatalog[it].barcode }, contentType = { "return-product" }) { index ->
                val product = returnCatalog[index]
                RequestPickerProduct(product, quantities[index], onStep = { step ->
                    draft = stepReturnPickerQuantity(draft, index, step)
                }, onEditQuantity = { quantityIndex = index }, unavailableText = "Нет товаров для возврата",
                    supportingContent = {
                        Text("Можно вернуть: ${product.available} шт", style = RequestPickerDesign.detail,
                            color = DealerDesign.secondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("Заявка: $orderDate · ${product.originalQuantity} шт", style = RequestPickerDesign.detail,
                            color = DealerDesign.secondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    })
                HorizontalDivider(color = DealerDesign.divider, modifier = Modifier.padding(horizontal = 16.dp))
            }
            if (indexes.isEmpty()) item(key = "empty") { HistoryEmpty("Товары не найдены") }
        }
        // Unlike a new request, confirming an empty return draft can clear previously selected items.
        ProductPickerFooter(selectedCount, total, "Подтвердить", enabled = true) { keyboard?.hide(); onConfirm(quantities) }
    }
    LaunchedEffect(Unit) { focus.requestFocus(); keyboard?.show() }
    quantityIndex?.let { index -> RequestQuantitySheet(returnCatalog[index], quantities[index],
        onDismiss = { quantityIndex = null; focusManager.clearFocus(); keyboard?.hide() }, maxDigits = 2,
        stockLabel = "Можно вернуть", onConfirm = {
            draft = normalizeReturnQuantities(draft).toMutableList().apply { this[index] = it }
            quantityIndex = null
            focusManager.clearFocus()
        }) }
}
