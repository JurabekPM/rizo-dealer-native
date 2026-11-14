package com.example.dealerappnative.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.historyMoney
import com.example.dealerappnative.data.storeForVisit
import com.example.dealerappnative.model.PickerProduct
import com.example.dealerappnative.model.VisitStore
import com.example.dealerappnative.ui.theme.AgentDesign
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.dealerColor

private val cartShape = RoundedCornerShape(12.dp)
private val controlShape = RoundedCornerShape(8.dp)
private val cartText = DealerDesign.type(16, 22, FontWeight.Normal, -.2f)
private val cartTotalLabel = DealerDesign.type(16, 22, FontWeight.Medium, -.2f)
private val cartTotalAmount = DealerDesign.type(18, 24, FontWeight.SemiBold, -.2f)
private val cartProductName = DealerDesign.type(17, 23, FontWeight.Medium, -.2f)
private val cartDetail = DealerDesign.type(14, 19, FontWeight.Normal, -.08f)
private val cartButton = DealerDesign.type(16, 22, FontWeight.Medium)

@Composable
internal fun CartTotal(total: Long) {
    Row(
        Modifier.fillMaxWidth().background(DealerDesign.surface, cartShape)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Общая сумма", style = cartTotalLabel, color = DealerDesign.secondary, modifier = Modifier.weight(1f))
        SlidingNumber(total, cartTotalAmount, DealerDesign.text, groupThousands = true, suffix = " UZS")
    }
}

@Composable
internal fun CartStoreCard(visit: VisitStore) {
    val store = storeForVisit(visit)
    val actions = LocalStoreActions.current
    Column(Modifier.fillMaxWidth().background(DealerDesign.surface, cartShape).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DealerIcon(R.drawable.ic_store)
            Column(Modifier.weight(1f)) {
                Text(visit.name, style = cartButton, color = DealerDesign.text, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(visit.address.ifBlank { "ID ${store.id}" }, style = cartDetail, color = DealerDesign.secondary,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CartStoreAction("Открыть на карте", R.drawable.ic_route, Modifier.weight(1.35f),
                onClick = { actions.openMap(store) })
            CartStoreAction("Позвонить", R.drawable.ic_phone_call, Modifier.weight(1f),
                onClick = { actions.dial(store.phoneNumber) })
        }
    }
}

@Composable
private fun CartStoreAction(label: String, @DrawableRes icon: Int, modifier: Modifier, onClick: () -> Unit) {
    Row(modifier.heightIn(min = 40.dp).softCardPress(onClick = onClick)
        .background(DealerDesign.background, cartShape).padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)) {
        DealerIcon(icon, size = 20.dp)
        Text(label, style = cartButton, color = DealerDesign.text, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
internal fun CartProductCard(
    product: PickerProduct,
    quantity: Int,
    onStep: (Int) -> Unit,
    onEditQuantity: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    onInfo: (() -> Unit)? = null
) {
    Row(modifier.fillMaxWidth().background(DealerDesign.surface, cartShape).padding(16.dp),
        verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        RequestProductThumbnail(product, previewEnabled = true)
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(product.name, style = cartProductName, color = DealerDesign.text)
                    Text(historyMoney(product.price) + "/шт", style = cartText, color = DealerDesign.text)
                }
                CartIconControl(R.drawable.ic_agent_clear, "Убрать: ${product.name}", Modifier.size(24.dp),
                    tint = DealerDesign.secondary, onClick = onRemove)
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Row(Modifier.width(112.dp).heightIn(min = 32.dp)
                    .background(DealerDesign.background, controlShape).padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    CartIconControl(R.drawable.ic_request_remove, "Уменьшить количество: ${product.name}",
                        Modifier.size(20.dp, 32.dp), onClick = { onStep(-1) })
                    Box(Modifier.weight(1f).heightIn(min = 32.dp).softCardPress(onClick = onEditQuantity),
                        contentAlignment = Alignment.Center) {
                        SlidingNumber(quantity.toLong(), cartText, DealerDesign.text,
                            accessibleLabel = "Количество: ${product.name}, $quantity")
                    }
                    CartIconControl(R.drawable.ic_request_add, "Увеличить количество: ${product.name}",
                        Modifier.size(20.dp, 32.dp), enabled = quantity < product.stock, onClick = { onStep(1) })
                }
                Spacer(Modifier.weight(1f))
                if (onInfo != null) {
                    CartIconControl(R.drawable.ic_info, "Информация: ${product.name}",
                        Modifier.size(28.dp).background(DealerDesign.background, controlShape),
                        tint = DealerDesign.secondary, onClick = onInfo)
                }
            }
        }
    }
}

@Composable
private fun CartIconControl(
    @DrawableRes icon: Int,
    label: String,
    modifier: Modifier,
    enabled: Boolean = true,
    tint: Color = DealerDesign.text,
    onClick: () -> Unit
) {
    Box(modifier.softCardPress(enabled = enabled, onClick = onClick)
        .semantics { contentDescription = label }, contentAlignment = Alignment.Center) {
        DealerIcon(icon, if (enabled) tint else DealerDesign.tertiary, 20.dp)
    }
}

@Composable
internal fun CartAddAction(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(modifier.fillMaxWidth().background(DealerDesign.surface, cartShape).padding(16.dp)) {
        CartButton("Добавить", AgentDesign.tint, DealerDesign.accent, true, onClick)
    }
}

@Composable
internal fun CartFooter(label: String, enabled: Boolean, onClick: () -> Unit) {
    val shadow = dealerColor(Color.Black.copy(alpha = .11f), Color.Black.copy(alpha = .38f))
    Box(Modifier.fillMaxWidth().drawBehind {
        val depth = 10.dp.toPx()
        drawRect(
            brush = Brush.verticalGradient(listOf(Color.Transparent, shadow), startY = -depth, endY = 0f),
            topLeft = Offset(0f, -depth),
            size = Size(size.width, depth)
        )
    }.background(DealerDesign.surface, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
        .padding(16.dp)) {
        CartButton(label, if (enabled) DealerDesign.accent else DealerDesign.background,
            if (enabled) DealerDesign.onAccent else DealerDesign.secondary, enabled, onClick)
    }
}

@Composable
private fun CartButton(label: String, background: Color, textColor: Color, enabled: Boolean, onClick: () -> Unit) {
    Box(Modifier.fillMaxWidth().heightIn(min = 48.dp).softCardPress(enabled = enabled, onClick = onClick)
        .background(background, cartShape).padding(horizontal = 12.dp), contentAlignment = Alignment.Center) {
        Text(label, style = cartButton, color = textColor)
    }
}
