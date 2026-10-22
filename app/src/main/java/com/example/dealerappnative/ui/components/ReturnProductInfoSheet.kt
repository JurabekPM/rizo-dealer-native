package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.ReturnCatalogProduct
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

/** Read-only source order and stock, separate from the editable return quantity. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ReturnProductInfoSheet(product: ReturnCatalogProduct, orderDate: String, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.White, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 24.dp)) {
            Text(product.name, style = AgentVisitDesign.sheetTitle, color = DealerDesign.text,
                maxLines = 3, overflow = TextOverflow.Ellipsis)
            Row(Modifier.fillMaxWidth().padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Текущий остаток", style = AgentVisitDesign.emphasis, color = DealerDesign.secondary, modifier = Modifier.weight(1f))
                Text("${product.available} шт", style = AgentVisitDesign.emphasis, color = DealerDesign.text)
            }
            Column(Modifier.fillMaxWidth().background(DealerDesign.background, RoundedCornerShape(12.dp)).padding(16.dp)) {
                ReturnSourceRow(R.drawable.ic_agent_schedule, "№${product.orderNumber}", "Заявка $orderDate")
                HorizontalDivider(Modifier.padding(vertical = 12.dp), color = DealerDesign.divider)
                ReturnSourceRow(R.drawable.ic_store, "Исходный заказ", "${product.originalQuantity} шт")
            }
        }
    }
}

@Composable
private fun ReturnSourceRow(icon: Int, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        DealerIcon(icon); Spacer(Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = AgentVisitDesign.emphasis, color = DealerDesign.text)
            Text(subtitle, style = AgentVisitDesign.detail, color = DealerDesign.secondary)
        }
    }
}
