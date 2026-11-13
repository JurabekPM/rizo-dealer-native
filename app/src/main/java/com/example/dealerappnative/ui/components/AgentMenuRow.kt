package com.example.dealerappnative.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.ui.theme.AgentDesign

@Composable
internal fun AgentMenuRow(
    title: String,
    @DrawableRes icon: Int,
    subtitle: String? = null,
    badge: Int? = null,
    divider: Boolean = true,
    onClick: (() -> Unit)? = null,
    leadingContent: (@Composable () -> Unit)? = null
) {
    Column {
        Row(Modifier.fillMaxWidth()
            .softCardPress(onClick = onClick)
            .semantics { if (badge != null) stateDescription = "Новых: $badge" }
            .heightIn(min = if (subtitle == null) 56.dp else 72.dp)
            .padding(start = 20.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            if (leadingContent != null) leadingContent() else DealerIcon(icon, AgentDesign.secondary, 24.dp)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = AgentDesign.menuTitle, color = AgentDesign.text,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                subtitle?.let {
                    Spacer(Modifier.height(2.dp))
                    Text(it, style = AgentDesign.menuDetail, color = AgentDesign.secondary,
                        maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
            badge?.let { count ->
                Spacer(Modifier.width(12.dp))
                UnreadBadge(count)
            }
            Spacer(Modifier.width(8.dp))
            DealerIcon(R.drawable.ic_chevron, AgentDesign.secondary, 24.dp)
        }
        if (divider) HorizontalDivider(color = AgentDesign.divider,
            modifier = Modifier.padding(start = 60.dp, end = 20.dp))
    }
}
