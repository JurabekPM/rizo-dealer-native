package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.RequestCatalogProduct
import com.example.dealerappnative.data.historyMoney
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.RequestPickerDesign as Design

/** Clear stays inside the input, so typing never changes its width. */
@Composable
internal fun RequestPickerSearch(query: String, onQuery: (String) -> Unit, focus: FocusRequester) {
    BasicTextField(query, onQuery, singleLine = true, textStyle = Design.body,
        cursorBrush = SolidColor(DealerDesign.accent),
        keyboardOptions = KeyboardOptions(autoCorrectEnabled = false),
        modifier = Modifier.fillMaxWidth().height(40.dp).focusRequester(focus)
            .semantics { contentDescription = "Поиск товаров" },
        decorationBox = { input ->
            Row(Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)).background(DealerDesign.background)
                .padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                DealerIcon(R.drawable.ic_search, size = 24.dp)
                Spacer(Modifier.width(8.dp))
                Box(Modifier.weight(1f)) {
                    if (query.isEmpty()) Text("Поиск товаров", style = Design.body, color = DealerDesign.tertiary)
                    input()
                }
                Spacer(Modifier.width(8.dp))
                if (query.isNotEmpty()) Box(Modifier.size(24.dp).softCardPress { onQuery("") }
                    .semantics { contentDescription = "Очистить поиск" }, contentAlignment = Alignment.Center) {
                    DealerIcon(R.drawable.ic_request_clear, Design.clear)
                } else Spacer(Modifier.width(24.dp))
            }
        })
}

/** Quick add is inline; only the numeric counter opens the bulk quantity sheet. */
@Composable
internal fun RequestPickerProduct(product: RequestCatalogProduct, quantity: Int,
    onStep: (Int) -> Unit, onEditQuantity: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RequestProductThumbnail(product, selected = quantity > 0)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(product.name, style = Design.productTitle, color = DealerDesign.text,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(buildAnnotatedString {
                    append(historyMoney(product.price).removeSuffix(" UZS"))
                    withStyle(Design.unit.toSpanStyle().copy(color = DealerDesign.secondary)) {
                        append(" UZS/шт")
                    }
                }, style = Design.price, color = DealerDesign.text, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (product.stock <= 0) Text("Нет в наличии", style = Design.detail,
                    color = DealerDesign.secondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                product.previousPrice?.takeIf { it > product.price }?.let { previous ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        val percent = kotlin.math.round((previous - product.price).toDouble() * 100 / previous).toInt()
                        Box(Modifier.clip(RoundedCornerShape(6.dp)).background(Color.Red)
                            .padding(horizontal = 4.dp, vertical = 1.dp)) {
                            Text("$percent%", style = Design.detail, color = Color.White)
                        }
                        Box {
                            Text(historyMoney(previous) + "/шт", style = Design.detail, color = DealerDesign.text)
                            Box(Modifier.matchParentSize(), contentAlignment = Alignment.Center) {
                                Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFF76659)))
                            }
                        }
                    }
                }
            }
        }
        if (product.stock > 0) Box(Modifier.fillMaxWidth().padding(top = 8.dp),
            contentAlignment = Alignment.CenterEnd) {
            RequestPickerCounter(product, quantity, onStep, onEditQuantity)
        }
    }
}

@Composable
private fun RequestPickerCounter(product: RequestCatalogProduct, quantity: Int,
    onStep: (Int) -> Unit, onEditQuantity: () -> Unit) {
    // Fixed size in both states: reveal controls to the left of the same plus button.
    Row(Modifier.size(128.dp, 40.dp).clip(RoundedCornerShape(12.dp))
        .background(if (quantity > 0) Design.addBackground else Color.Transparent),
        verticalAlignment = Alignment.CenterVertically) {
        if (quantity > 0) {
            SoftTextButton(onClick = { onStep(-1) }, modifier = Modifier.size(40.dp)
                .semantics { contentDescription = "Уменьшить количество: ${product.name}" }) {
                Text("−", style = Design.sheetTitle, color = DealerDesign.accent)
            }
            Box(Modifier.size(48.dp, 40.dp).softCardPress(onClick = onEditQuantity)
                .padding(horizontal = 4.dp)
                .semantics { contentDescription = "Ввести количество: ${product.name}, $quantity" },
                contentAlignment = Alignment.Center) {
                Text("$quantity", style = Design.price, color = DealerDesign.accent, maxLines = 1)
            }
        } else Spacer(Modifier.width(88.dp))
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Design.addBackground)
            .softCardPress(enabled = quantity < product.stock) { onStep(1) }
            .semantics { contentDescription = if (quantity == 0) "Добавить: ${product.name}"
                else "Увеличить количество: ${product.name}" }, contentAlignment = Alignment.Center) {
            DealerIcon(R.drawable.ic_request_add,
                if (quantity < product.stock) DealerDesign.accent else DealerDesign.tertiary)
        }
    }
}

@Composable
internal fun RequestPickerButton(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(12.dp))
        .background(if (enabled) DealerDesign.accent else DealerDesign.background)
        .softCardPress(enabled = enabled, onClick = onClick), contentAlignment = Alignment.Center) {
        Text(label, style = Design.button, color = if (enabled) Color.White else DealerDesign.secondary)
    }
}
