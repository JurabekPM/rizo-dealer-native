package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.OrderUiModel
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun OrderCard(order: OrderUiModel) {
    val secondaryDetail = DealerDesign.detail.copy(fontWeight = FontWeight.Normal)
    Column(Modifier.fillMaxWidth().height(DealerDesign.cardHeight)
        .clip(RoundedCornerShape(DealerDesign.radius)).background(Color.White)
        .padding(horizontal = DealerDesign.pageInset)) {
        Row(Modifier.fillMaxWidth().height(72.dp), verticalAlignment = Alignment.CenterVertically) {
            DealerIcon(R.drawable.ic_store, DealerDesign.secondary, 24.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(order.storeName, style = DealerDesign.cardTitle, color = DealerDesign.text,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(order.customerName, style = secondaryDetail, color = DealerDesign.secondary,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(order.amount, style = DealerDesign.cardTitle, color = DealerDesign.text,
                    maxLines = 1, softWrap = false)
                Spacer(Modifier.height(4.dp))
                Text("${order.productCount} xil tovar", style = secondaryDetail, color = DealerDesign.secondary)
            }
        }
        HorizontalDivider(color = DealerDesign.divider)
        Row(Modifier.fillMaxWidth().weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Text(order.date, style = DealerDesign.detail, color = DealerDesign.secondary,
                modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.width(8.dp))
            Text(order.source, style = DealerDesign.detail, color = DealerDesign.secondary,
                maxLines = 1, softWrap = false)
            Spacer(Modifier.width(8.dp))
            DealerIcon(R.drawable.ic_chevron, DealerDesign.secondary, 18.dp)
        }
    }
}
