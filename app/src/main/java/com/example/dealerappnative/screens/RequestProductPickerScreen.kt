package com.example.dealerappnative.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.data.*
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.DealerDesign

/** Figma SearchSelection; only Continue applies this picker draft to the order. */
@Composable
internal fun RequestProductPickerScreen(initial: List<Int>, onBack: () -> Unit, onConfirm: (List<Int>) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    var draft by rememberSaveable { mutableStateOf(normalizeRequestQuantities(initial)) }
    var quantityIndex by rememberSaveable { mutableStateOf<Int?>(null) }
    val quantities = remember(draft) { normalizeRequestQuantities(draft) }
    val selectedCount = quantities.count { it > 0 }
    val total = remember(quantities) { requestPickerTotal(quantities) }
    val listState = rememberLazyListState()
    val focus = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val indexes = remember(query, quantities) { requestPickerIndexes(quantities, query) }
    val back: () -> Unit = { keyboard?.hide(); onBack() }
    BackHandler(onBack = back)
    Column(Modifier.fillMaxSize().background(Color.White).imePadding()) {
        ProductPickerHeader(query, { query = it }, focus, back)
        LazyColumn(Modifier.weight(1f), state = listState) {
            items(indexes, key = { requestCatalog[it].id }, contentType = { "request-product" }) { index ->
                RequestPickerProduct(requestCatalog[index], quantities[index], onStep = { step ->
                    draft = stepRequestPickerQuantity(draft, index, step)
                }, onEditQuantity = { quantityIndex = index })
                HorizontalDivider(color = DealerDesign.divider, modifier = Modifier.padding(horizontal = 16.dp))
            }
            if (indexes.isEmpty()) item(key = "empty") { HistoryEmpty("Товары не найдены") }
        }
        ProductPickerFooter(selectedCount, total, "Продолжить", enabled = selectedCount > 0) {
            keyboard?.hide(); onConfirm(quantities)
        }
    }
    LaunchedEffect(Unit) { focus.requestFocus(); keyboard?.show() }
    quantityIndex?.let { index -> RequestQuantitySheet(requestCatalog[index], quantities[index],
        onDismiss = { quantityIndex = null; focusManager.clearFocus(); keyboard?.hide() }, onConfirm = {
            draft = normalizeRequestQuantities(draft).toMutableList().apply { this[index] = it }
            quantityIndex = null
            focusManager.clearFocus()
        }) }
}
