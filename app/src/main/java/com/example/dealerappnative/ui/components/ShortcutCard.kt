package com.example.dealerappnative.ui.components

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun ShortcutCard(
    title: String,
    @DrawableRes iconRes: Int,
    detail: String? = null,
    detailColor: Color = DealerDesign.secondary,
    modifier: Modifier = Modifier,
    tabletStyle: Boolean = false
) {
    Row(
        modifier.fillMaxWidth()
            .then(if (tabletStyle) Modifier.heightIn(min = 64.dp).tabletCard()
                else Modifier.height(if (detail == null) 48.dp else 68.dp).cardShadow().background(DealerDesign.surface))
            .padding(horizontal = if (tabletStyle) 10.dp else DealerDesign.pageInset, vertical = if (tabletStyle) 7.dp else 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (tabletStyle) {
            Box(Modifier.size(32.dp).background(TabletColors.raised, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center) {
                DealerIcon(iconRes, DealerDesign.secondary, 18.dp)
            }
        } else DealerIcon(iconRes)
        Spacer(Modifier.width(if (tabletStyle) 8.dp else 12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = DealerDesign.body, color = DealerDesign.text,
                maxLines = if (tabletStyle) 2 else 1, overflow = TextOverflow.Ellipsis)
            if (detail != null) {
                Spacer(Modifier.height(4.dp))
                Text(detail, style = DealerDesign.detail, color = detailColor)
            }
        }
        Spacer(Modifier.width(8.dp))
        if (tabletStyle) DealerIcon(R.drawable.ic_chevron, DealerDesign.tertiary, 16.dp)
        else DealerIcon(R.drawable.ic_chevron, DealerDesign.tertiary)
    }
}
