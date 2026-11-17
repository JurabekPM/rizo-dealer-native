package com.example.dealerappnative.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun ScreenHeader(
    title: String,
    showSortIcon: Boolean = false,
    @DrawableRes actionIconRes: Int = R.drawable.ic_sort,
    tabletStyle: Boolean = false
) {
    Box(
        Modifier.fillMaxWidth().height(56.dp)
            .then(if (tabletStyle) Modifier else Modifier.background(DealerDesign.surface)),
        contentAlignment = Alignment.Center
    ) {
        Text(title, style = DealerDesign.title,
            modifier = if (tabletStyle) Modifier.align(Alignment.CenterStart).padding(end = 48.dp) else Modifier)
        if (showSortIcon) {
            // Sort is displayed for the mockup; business actions are not implemented.
            Box(
                Modifier.align(Alignment.CenterEnd)
                    .then(if (tabletStyle) Modifier.size(40.dp).background(TabletColors.card, RoundedCornerShape(12.dp))
                        else Modifier.padding(end = 4.dp).size(48.dp)),
                contentAlignment = Alignment.Center
            ) {
                DealerIcon(actionIconRes, DealerDesign.text)
            }
        }
    }
}
