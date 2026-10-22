package com.example.dealerappnative.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.ReturnCatalogProduct
import com.example.dealerappnative.data.historyMoney
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun ReturnProductThumbnail(product: ReturnCatalogProduct, size: Dp = 48.dp) {
    Box(Modifier.size(size).clip(RoundedCornerShape(8.dp)).background(DealerDesign.background),
        contentAlignment = Alignment.Center) {
        if (product.photo != null) Image(painterResource(product.photo), contentDescription = null,
            contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize().padding(2.dp))
        else DealerIcon(R.drawable.ic_box)
    }
}

/** Full-width reference card: product, remaining stock, one counter and source-document action. */
@Composable
internal fun ReturnProductCard(product: ReturnCatalogProduct, quantity: Int, onQuantity: (Int) -> Unit,
    onEditQuantity: () -> Unit, onRemove: () -> Unit, onInfo: () -> Unit) {
    HistorySurface {
        Row(verticalAlignment = Alignment.Top) {
            ReturnProductThumbnail(product)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(product.name, style = AgentVisitDesign.emphasis, color = DealerDesign.text,
                        maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    SoftIconButton(onClick = onRemove, modifier = Modifier.padding(start = 4.dp).size(28.dp)
                        .semantics { contentDescription = "Убрать: ${product.name}" }) {
                        DealerIcon(R.drawable.ic_agent_clear, size = 20.dp)
                    }
                }
                Text(historyMoney(product.price) + "/шт", style = AgentVisitDesign.emphasis,
                    color = DealerDesign.text, modifier = Modifier.padding(top = 6.dp))
                Text("${product.available} шт", style = AgentVisitDesign.name, color = DealerDesign.text,
                    modifier = Modifier.padding(top = 4.dp).semantics { contentDescription = "Текущий остаток: ${product.available} шт" })
                Row(Modifier.fillMaxWidth().padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Row(Modifier.height(36.dp).clip(RoundedCornerShape(8.dp)).background(DealerDesign.background),
                        verticalAlignment = Alignment.CenterVertically) {
                        SoftTextButton(onClick = { onQuantity(quantity - 1) }, modifier = Modifier.size(40.dp, 36.dp)
                            .semantics { contentDescription = "Уменьшить количество: ${product.name}" }) {
                            Text("−", style = AgentVisitDesign.sheetTitle, color = DealerDesign.text)
                        }
                        Box(Modifier.size(40.dp, 36.dp).softCardPress(onClick = onEditQuantity)
                            .semantics { contentDescription = "Количество: ${product.name}, $quantity" }, contentAlignment = Alignment.Center) {
                            Text("$quantity", style = AgentVisitDesign.emphasis, color = DealerDesign.text)
                        }
                        SoftTextButton(onClick = { onQuantity(quantity + 1) }, enabled = quantity < product.available,
                            modifier = Modifier.size(40.dp, 36.dp).semantics { contentDescription = "Увеличить количество: ${product.name}" }) {
                            Text("+", style = AgentVisitDesign.sheetTitle, color = if (quantity < product.available) DealerDesign.text else DealerDesign.tertiary)
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    SoftIconButton(onClick = onInfo, modifier = Modifier.padding(start = 8.dp).size(36.dp)
                        .clip(RoundedCornerShape(8.dp)).background(DealerDesign.background)
                        .semantics { contentDescription = "Информация: ${product.name}" }) { DealerIcon(R.drawable.ic_info) }
                }
            }
        }
    }
}

/** Same number-entry pattern as the real app; changes apply only on confirmation. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReturnQuantitySheet(product: ReturnCatalogProduct, quantity: Int, onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    val initial = if (quantity == 0) "" else quantity.toString()
    var field by rememberSaveable(product.name, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(initial, TextRange(0, initial.length)))
    }
    val text = field.text
    val value = text.toIntOrNull()
    val valid = value != null && value in 0..product.available
    val focus = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
        Column(Modifier.fillMaxWidth().imePadding().padding(horizontal = 16.dp)) {
            Text("Количество", style = AgentVisitDesign.sheetTitle, color = DealerDesign.text)
            Text(product.name, style = AgentVisitDesign.detail, color = DealerDesign.secondary,
                maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 8.dp, bottom = 12.dp))
            OutlinedTextField(field, { input -> if (input.text.length <= 2 && input.text.all(Char::isDigit)) field = input },
                modifier = Modifier.fillMaxWidth().focusRequester(focus).semantics { contentDescription = "Количество товара" },
                singleLine = true, textStyle = AgentVisitDesign.name, shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("Введите", style = AgentVisitDesign.name) }, isError = text.isNotEmpty() && !valid)
            Text("Можно вернуть: ${product.available} шт", style = AgentVisitDesign.detail, color = DealerDesign.secondary,
                modifier = Modifier.padding(top = 8.dp))
            Spacer(Modifier.height(12.dp))
            ReturnPrimaryButton("Подтвердить", valid) { keyboard?.hide(); onConfirm(value!!) }
            Spacer(Modifier.height(16.dp))
        }
        LaunchedEffect(Unit) { focus.requestFocus() }
    }
}

@Composable
internal fun ReturnPrimaryButton(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(48.dp).softCardPress(enabled = enabled, onClick = onClick)
        .clip(RoundedCornerShape(12.dp)).background(if (enabled) DealerDesign.accent else DealerDesign.background),
        contentAlignment = Alignment.Center) {
        Text(label, style = AgentVisitDesign.button, color = if (enabled) Color.White else DealerDesign.secondary)
    }
}
