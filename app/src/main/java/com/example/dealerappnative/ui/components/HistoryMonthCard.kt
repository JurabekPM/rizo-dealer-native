package com.example.dealerappnative.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.HistoryMonth
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

/** Shared month/year hierarchy for Returns and Revisions; signed/currency values stay separate. */
@Composable
internal fun HistoryMonthCard(month: HistoryMonth, @DrawableRes icon: Int, amountLabel: String,
    amounts: List<Pair<String, Color>>, onClick: () -> Unit) {
    HistorySurface(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp).background(DealerDesign.accent.copy(alpha = .07f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center) { DealerIcon(icon, DealerDesign.accent) }
            Spacer(Modifier.width(12.dp))
            Text(month.label, style = AgentVisitDesign.screenTitle, color = DealerDesign.text,
                maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(12.dp))
            DealerIcon(R.drawable.ic_chevron, size = 16.dp)
        }
        HorizontalDivider(color = DealerDesign.divider, modifier = Modifier.padding(top = 12.dp, bottom = 12.dp))
        Row(verticalAlignment = Alignment.Top) {
            Text(amountLabel, style = AgentVisitDesign.detail, color = DealerDesign.secondary,
                modifier = Modifier.weight(1f))
            Spacer(Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                amounts.forEach { (text, color) -> Text(text, style = AgentVisitDesign.emphasis, color = color) }
            }
        }
    }
}
