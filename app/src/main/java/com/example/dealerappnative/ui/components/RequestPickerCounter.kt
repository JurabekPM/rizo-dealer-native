package com.example.dealerappnative.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.RequestCatalogProduct
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.RequestPickerDesign as Design

/** Fixed bounds keep the plus anchored; only the number translates inside its clipped slot. */
@Composable
internal fun RequestPickerCounter(product: RequestCatalogProduct, quantity: Int,
    onStep: (Int) -> Unit, onEditQuantity: () -> Unit) {
    Row(Modifier.size(108.dp, 32.dp).clip(RoundedCornerShape(10.dp))
        .background(if (quantity > 0) Design.addBackground else Color.Transparent),
        verticalAlignment = Alignment.CenterVertically) {
        if (quantity > 0) {
            Box(Modifier.size(32.dp).softCardPress { onStep(-1) }
                .semantics { contentDescription = "Уменьшить количество: ${product.name}" },
                contentAlignment = Alignment.Center) {
                DealerIcon(R.drawable.ic_request_remove, DealerDesign.accent, size = 18.dp)
            }
            Box(Modifier.size(44.dp, 32.dp).softCardPress(onClick = onEditQuantity)
                .padding(horizontal = 2.dp)
                .semantics { contentDescription = "Ввести количество: ${product.name}, $quantity" },
                contentAlignment = Alignment.Center) {
                AnimatedContent(targetState = quantity, modifier = Modifier.fillMaxSize().clipToBounds()
                    .clearAndSetSemantics { }, contentAlignment = Alignment.Center, label = "picker-quantity",
                    transitionSpec = {
                        val direction = if (targetState > initialState) 1 else -1
                        ((slideInVertically(tween(200, easing = FastOutSlowInEasing)) { it * direction } + fadeIn(tween(120)))
                            togetherWith (slideOutVertically(tween(200, easing = FastOutSlowInEasing)) { -it * direction } + fadeOut(tween(120))))
                            .using(SizeTransform(clip = true))
                    }) { count ->
                    Text("$count", style = Design.quantity, color = DealerDesign.accent, maxLines = 1)
                }
            }
        } else Spacer(Modifier.width(76.dp))
        Box(Modifier.size(32.dp).clip(RoundedCornerShape(10.dp))
            .background(if (quantity > 0) Color.Transparent else Design.addBackground)
            .softCardPress(enabled = quantity < product.stock) { onStep(1) }
            .semantics {
                contentDescription = if (quantity == 0) "Добавить: ${product.name}" else "Увеличить количество: ${product.name}"
                if (quantity >= product.stock) disabled()
            }, contentAlignment = Alignment.Center) {
            DealerIcon(R.drawable.ic_request_add,
                if (quantity < product.stock) DealerDesign.accent else DealerDesign.tertiary, size = 18.dp)
        }
    }
}
