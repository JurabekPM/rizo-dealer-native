package com.example.dealerappnative.ui.components

import androidx.annotation.DrawableRes
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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun MenuRow(
    label: String,
    @DrawableRes iconRes: Int,
    divider: Boolean = true,
    rowHeight: Dp = 64.dp,
    modifier: Modifier = Modifier,
    unreadCount: Int = 0,
    onClick: (() -> Unit)? = null
) {
    Column(modifier.softCardPress(onClick = onClick)
        .semantics { if (unreadCount > 0) stateDescription = "Новых: $unreadCount" }
        .padding(horizontal = 16.dp)) {
        Row(Modifier.fillMaxWidth().height(rowHeight), verticalAlignment = Alignment.CenterVertically) {
            DealerIcon(iconRes)
            Spacer(Modifier.width(12.dp))
            Text(label, Modifier.weight(1f), style = DealerDesign.body)
            if (unreadCount > 0) {
                UnreadBadge(unreadCount)
                Spacer(Modifier.width(8.dp))
            }
            DealerIcon(R.drawable.ic_chevron, DealerDesign.tertiary, 24.dp)
        }
        if (divider) {
            HorizontalDivider(color = DealerDesign.divider, modifier = Modifier.padding(start = 36.dp))
        }
    }
}
