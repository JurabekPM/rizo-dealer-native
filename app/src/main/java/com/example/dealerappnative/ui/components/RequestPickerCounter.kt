package com.example.dealerappnative.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.RequestCatalogProduct
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.RequestPickerDesign as Design

/** Fixed hit slots; reveal is draw-only, so the plus and product layout never move. */
@Composable
internal fun RequestPickerCounter(product: RequestCatalogProduct, quantity: Int,
    onStep: (Int) -> Unit, onEditQuantity: () -> Unit) {
    val expanded = quantity > 0
    val reveal = animateFloatAsState(if (expanded) 1f else 0f,
        animationSpec = tween(300, easing = FastOutSlowInEasing), label = "picker-counter-reveal")
    val showingPrefix by remember { derivedStateOf { reveal.value > 0f } }
    Box(Modifier.size(120.dp, 40.dp).clip(RoundedCornerShape(12.dp)).drawBehind {
        val visibleWidth = 40.dp.toPx() + 80.dp.toPx() * reveal.value
        drawRoundRect(Design.addBackground, topLeft = Offset(size.width - visibleWidth, 0f),
            size = Size(visibleWidth, size.height), cornerRadius = CornerRadius(12.dp.toPx()))
    }) {
        if (expanded || showingPrefix) {
            Row(Modifier.align(Alignment.CenterStart).size(80.dp, 40.dp).clipToBounds()
                .graphicsLayer {
                    alpha = reveal.value
                    translationX = (1f - reveal.value) * 8.dp.toPx()
                }.then(if (expanded) Modifier else Modifier.clearAndSetSemantics { }),
                verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(40.dp).softCardPress(enabled = expanded) { onStep(-1) }
                    .semantics { contentDescription = "Уменьшить количество: ${product.name}" },
                    contentAlignment = Alignment.Center) {
                    DealerIcon(R.drawable.ic_request_remove, DealerDesign.accent, size = 22.dp)
                }
                Box(Modifier.size(40.dp).clipToBounds().softCardPress(enabled = expanded, onClick = onEditQuantity)
                    .semantics { contentDescription = "Ввести количество: ${product.name}, $quantity" },
                    contentAlignment = Alignment.Center) {
                    // No size transform: a wrapped stack stays truly centered in its fixed slot.
                    AnimatedContent(targetState = quantity, modifier = Modifier.clearAndSetSemantics { },
                        contentAlignment = Alignment.Center, label = "picker-quantity",
                        transitionSpec = {
                            val direction = if (targetState > initialState) 1 else -1
                            ((slideInVertically(tween(320, easing = FastOutSlowInEasing)) { it * direction } + fadeIn(tween(220)))
                                togetherWith (slideOutVertically(tween(320, easing = FastOutSlowInEasing)) { -it * direction } + fadeOut(tween(220))))
                                .using(null)
                        }) { count ->
                        Text("$count", style = Design.quantity, color = DealerDesign.accent, maxLines = 1)
                    }
                }
            }
        }
        Box(Modifier.align(Alignment.CenterEnd).size(40.dp).clip(RoundedCornerShape(12.dp))
            .softCardPress(enabled = quantity < product.stock) { onStep(1) }
            .semantics {
                contentDescription = if (quantity == 0) "Добавить: ${product.name}" else "Увеличить количество: ${product.name}"
                if (quantity >= product.stock) disabled()
            }, contentAlignment = Alignment.Center) {
            DealerIcon(R.drawable.ic_request_add,
                if (quantity < product.stock) DealerDesign.accent else DealerDesign.tertiary, size = 22.dp)
        }
    }
}
