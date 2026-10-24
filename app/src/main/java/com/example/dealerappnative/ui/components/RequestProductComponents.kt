package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.RequestCatalogProduct
import com.example.dealerappnative.data.historyMoney
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

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
