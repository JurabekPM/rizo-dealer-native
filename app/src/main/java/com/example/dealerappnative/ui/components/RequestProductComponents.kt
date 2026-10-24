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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.RequestCatalogProduct
import com.example.dealerappnative.data.historyMoney
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun RequestProductThumbnail(product: RequestCatalogProduct, size: Dp = 48.dp) {
    Box(Modifier.size(size).clip(RoundedCornerShape(8.dp)).background(DealerDesign.background),
        contentAlignment = Alignment.Center) {
        if (product.photo != null) Image(painterResource(product.photo), null,
            contentScale = ContentScale.Fit, modifier = Modifier.fillMaxSize().padding(2.dp))
        else DealerIcon(R.drawable.ic_box)
    }
}

/** Figma order card: name/price and a compact counter, with no return-source controls. */
@Composable
internal fun RequestProductCard(product: RequestCatalogProduct, quantity: Int,
    onQuantity: (Int) -> Unit, onEditQuantity: () -> Unit, onRemove: () -> Unit) {
    HistorySurface {
        Row(verticalAlignment = Alignment.Top) {
            RequestProductThumbnail(product)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.Top) {
                    Text(product.name, style = AgentVisitDesign.name, color = DealerDesign.text,
                        maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    SoftIconButton(onClick = onRemove, modifier = Modifier.size(28.dp)
                        .semantics { contentDescription = "Убрать: ${product.name}" }) {
                        DealerIcon(R.drawable.ic_agent_clear, size = 20.dp)
                    }
                }
                Text(historyMoney(product.price) + "/шт", style = AgentVisitDesign.detail,
                    color = DealerDesign.text, modifier = Modifier.padding(top = 4.dp))
                Row(Modifier.padding(top = 8.dp).height(32.dp).clip(RoundedCornerShape(8.dp))
                    .background(DealerDesign.background), verticalAlignment = Alignment.CenterVertically) {
                    SoftTextButton(onClick = { onQuantity(quantity - 1) }, modifier = Modifier.size(36.dp, 32.dp)
                        .semantics { contentDescription = "Уменьшить количество: ${product.name}" }) {
                        Text("−", style = AgentVisitDesign.screenTitle, color = DealerDesign.text)
                    }
                    Box(Modifier.widthIn(min = 40.dp).height(32.dp).softCardPress(onClick = onEditQuantity)
                        .padding(horizontal = 4.dp).semantics { contentDescription = "Количество: ${product.name}, $quantity" },
                        contentAlignment = Alignment.Center) {
                        Text("$quantity", style = AgentVisitDesign.name, color = DealerDesign.text)
                    }
                    SoftTextButton(onClick = { onQuantity(quantity + 1) }, enabled = quantity < product.stock,
                        modifier = Modifier.size(36.dp, 32.dp).semantics { contentDescription = "Увеличить количество: ${product.name}" }) {
                        Text("+", style = AgentVisitDesign.screenTitle,
                            color = if (quantity < product.stock) DealerDesign.text else DealerDesign.tertiary)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RequestQuantitySheet(product: RequestCatalogProduct, quantity: Int,
    onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    val initial = quantity.takeIf { it > 0 }?.toString().orEmpty()
    var field by rememberSaveable(product.id, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(initial, TextRange(0, initial.length)))
    }
    val value = field.text.toIntOrNull()
    val valid = value != null && value in 0..product.stock
    val focus = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
        Column(Modifier.fillMaxWidth().imePadding().padding(horizontal = 16.dp)) {
            Text("Количество", style = AgentVisitDesign.sheetTitle, color = DealerDesign.text)
            Text(product.name, style = AgentVisitDesign.detail, color = DealerDesign.secondary,
                maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 8.dp, bottom = 12.dp))
            OutlinedTextField(field, { if (it.text.length <= 4 && it.text.all(Char::isDigit)) field = it },
                modifier = Modifier.fillMaxWidth().focusRequester(focus), singleLine = true,
                textStyle = AgentVisitDesign.name, shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("Введите", style = AgentVisitDesign.name) },
                suffix = { Text("Штука", style = AgentVisitDesign.detail) },
                isError = field.text.isNotEmpty() && !valid)
            Text("В наличии: ${product.stock} шт", style = AgentVisitDesign.detail, color = DealerDesign.secondary,
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp))
            ReturnPrimaryButton("Подтвердить", valid) { keyboard?.hide(); onConfirm(value!!) }
            Spacer(Modifier.height(16.dp))
        }
        LaunchedEffect(Unit) { focus.requestFocus() }
    }
}
