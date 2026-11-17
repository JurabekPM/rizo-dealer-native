package com.example.dealerappnative.ui.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.selected
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.OrderUiModel
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun OrderCard(order: OrderUiModel, unread: Boolean, selected: Boolean, onClick: (() -> Unit)?,
    tabletStyle: Boolean = false) {
    val secondaryDetail = DealerDesign.detail.copy(fontWeight = FontWeight.Normal)
    val appearance = if (tabletStyle) {
        Modifier.tabletCard(if (selected || unread) TabletColors.selected else TabletColors.card)
            .then(if (selected) Modifier.border(1.dp, DealerDesign.accent.copy(alpha = .36f), RoundedCornerShape(16.dp))
                .semantics { this.selected = true } else Modifier)
    } else Modifier.cardShadow().unreadCardBackground(unread).dealerSelectedCard(selected)
    Column(Modifier.fillMaxWidth().then(if (tabletStyle) Modifier else Modifier.height(DealerDesign.cardHeight))
        .softCardPress(onClick = onClick).then(appearance)
        .padding(horizontal = if (tabletStyle) 12.dp else DealerDesign.pageInset)) {
        Row(Modifier.fillMaxWidth().then(if (tabletStyle) Modifier.heightIn(min = 53.dp).padding(vertical = 5.dp)
            else Modifier.height(72.dp)), verticalAlignment = Alignment.CenterVertically) {
            DealerIcon(R.drawable.ic_store, DealerDesign.secondary, 24.dp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                UnreadCardTitle(order.storeName, unread = unread)
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
        HorizontalDivider(color = if (tabletStyle) TabletColors.edge else DealerDesign.divider)
        Row(Modifier.fillMaxWidth().then(if (tabletStyle) Modifier.heightIn(min = 30.dp).padding(vertical = 4.dp)
            else Modifier.weight(1f)), verticalAlignment = Alignment.CenterVertically) {
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
