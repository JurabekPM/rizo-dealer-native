package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.RequestPickerDesign

/** Shared visuals only: the caller owns the draft and decides whether empty confirmation is allowed. */
@Composable
internal fun ProductPickerHeader(query: String, onQuery: (String) -> Unit, onBack: () -> Unit) {
    Row(Modifier.fillMaxWidth().shadow(4.dp, ambientColor = Color.Black.copy(alpha = .06f),
        spotColor = Color.Black.copy(alpha = .06f)).background(DealerDesign.surface)
        .height(72.dp).padding(horizontal = 16.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(24.dp).fillMaxHeight().softCardPress(onClick = onBack)
            .semantics { contentDescription = "Назад" }, contentAlignment = Alignment.Center) {
            DealerIcon(R.drawable.ic_request_back, DealerDesign.text)
        }
        Spacer(Modifier.width(12.dp))
        Box(Modifier.weight(1f)) { RequestPickerSearch(query, onQuery) }
    }
}

@Composable
internal fun ProductPickerFooter(count: Int, total: Long, label: String, enabled: Boolean, onConfirm: () -> Unit) {
    BottomActionPanel(spacing = 12.dp) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            SlidingNumber(count.toLong(), RequestPickerDesign.unit, DealerDesign.secondary,
                suffix = " ${productWord(count)}")
            Spacer(Modifier.weight(1f))
            SlidingNumber(total, RequestPickerDesign.price, DealerDesign.text, groupThousands = true, suffix = " UZS")
        }
        RequestPickerButton(label, enabled, onConfirm)
    }
}

private fun productWord(count: Int): String = when {
    count % 100 in 11..14 -> "товаров"
    count % 10 == 1 -> "товар"
    count % 10 in 2..4 -> "товара"
    else -> "товаров"
}
