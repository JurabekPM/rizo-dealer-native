package com.example.dealerappnative.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    detailColor: Color = DealerDesign.secondary
) {
    Row(
        Modifier.fillMaxWidth().height(if (detail == null) 48.dp else 68.dp)
            .clip(RoundedCornerShape(DealerDesign.radius)).background(Color.White)
            .padding(horizontal = DealerDesign.pageInset),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DealerIcon(iconRes)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = DealerDesign.body, color = DealerDesign.text,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (detail != null) {
                Spacer(Modifier.height(4.dp))
                Text(detail, style = DealerDesign.detail, color = detailColor)
            }
        }
        Spacer(Modifier.width(8.dp))
        DealerIcon(R.drawable.ic_chevron, DealerDesign.tertiary)
    }
}
