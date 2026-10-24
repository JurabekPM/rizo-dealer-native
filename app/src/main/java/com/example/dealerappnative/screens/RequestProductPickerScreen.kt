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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
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
    var selectedOnly by rememberSaveable { mutableStateOf(false) }
    val quantities = remember(draft) { normalizeRequestQuantities(draft) }
    val selectedCount = quantities.count { it > 0 }
    val total = remember(quantities) { requestPickerTotal(quantities) }
    val allListState = rememberLazyListState()
    val selectedListState = rememberLazyListState()
    val focus = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current
    val indexes = remember(query, quantities, selectedOnly) { requestPickerIndexes(quantities, query, selectedOnly) }
    val toggleSelection: () -> Unit = {
        selectedOnly = !selectedOnly
        focusManager.clearFocus()
        keyboard?.hide()
    }
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
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp)
            .clip(RoundedCornerShape(12.dp)).softCardPress(onClick = toggleSelection)
            .semantics { stateDescription = if (selectedOnly) "Только выбранные товары" else "Все товары" },
            verticalAlignment = Alignment.CenterVertically) {
            DealerIcon(R.drawable.ic_request_done_all,
                if (selectedOnly) DealerDesign.accent else DealerDesign.secondary)
            Spacer(Modifier.width(12.dp))
            Text("Выбрано: $selectedCount ${requestProductWord(selectedCount)}",
                style = RequestPickerDesign.body, color = if (selectedOnly) DealerDesign.accent else DealerDesign.text,
                modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.width(8.dp))
            Text(if (selectedOnly) "Все товары" else "Показать", style = RequestPickerDesign.unit,
                color = DealerDesign.accent)
            Spacer(Modifier.width(4.dp))
            DealerIcon(R.drawable.ic_chevron, DealerDesign.accent, size = 16.dp)
        }
        HorizontalDivider(color = DealerDesign.divider, modifier = Modifier.padding(horizontal = 16.dp))
        LazyColumn(Modifier.weight(1f), state = if (selectedOnly) selectedListState else allListState) {
            items(indexes, key = { requestCatalog[it].id }, contentType = { "request-product" }) { index ->
                RequestPickerProduct(requestCatalog[index], quantities[index], onStep = { step ->
                    draft = stepRequestPickerQuantity(draft, index, step)
                    focusManager.clearFocus()
                    keyboard?.hide()
                }, onEditQuantity = { quantityIndex = index })
                HorizontalDivider(color = DealerDesign.divider, modifier = Modifier.padding(horizontal = 16.dp))
            }
            if (indexes.isEmpty()) item(key = "empty") {
                HistoryEmpty(if (selectedOnly && selectedCount == 0) "Пока ничего не выбрано" else "Товары не найдены")
                if (selectedOnly) Box(Modifier.padding(horizontal = 16.dp)) {
                    SoftTextButton(onClick = toggleSelection, modifier = Modifier.fillMaxWidth().height(40.dp)) {
                        Text("Показать все товары", style = RequestPickerDesign.unit, color = DealerDesign.accent)
                    }
                }
            }
        }
        Column(Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
            if (selectedCount > 0) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("$selectedCount ${requestProductWord(selectedCount)}", style = RequestPickerDesign.unit,
                        color = DealerDesign.secondary, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(12.dp))
                    Text(historyMoney(total), style = RequestPickerDesign.price, color = DealerDesign.text,
                        maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Spacer(Modifier.height(12.dp))
            }
            RequestPickerButton("Продолжить", selectedCount > 0) { keyboard?.hide(); onConfirm(quantities) }
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

private fun requestProductWord(count: Int): String = when {
    count % 100 in 11..14 -> "товаров"
    count % 10 == 1 -> "товар"
    count % 10 in 2..4 -> "товара"
    else -> "товаров"
}
