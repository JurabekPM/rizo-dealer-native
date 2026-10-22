package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.AgentHistoryEntry
import com.example.dealerappnative.model.HistoryStatus
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/** Visit -> History, 375-point full-width cards and SF Pro 15/20, 13/18 metrics. */
@Composable
internal fun AgentHistoryCard(entry: AgentHistoryEntry, showAddress: Boolean = false, onClick: () -> Unit) {
    val date = SimpleDateFormat("dd.MM.yyyy | HH:mm", Locale.forLanguageTag("ru")).apply {
        timeZone = TimeZone.getTimeZone("Asia/Tashkent")
    }.format(entry.session.startedAt)
    Column(Modifier.fillMaxWidth().softCardPress(onClick = onClick).clip(RoundedCornerShape(12.dp)).background(Color.White)
        .padding(horizontal = 16.dp)) {
        Row(Modifier.fillMaxWidth().heightIn(min = 72.dp).padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            DealerIcon(R.drawable.ic_store)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(entry.visit.name, style = AgentVisitDesign.name, color = DealerDesign.text,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(entry.visit.id, style = AgentVisitDesign.detail, color = DealerDesign.secondary,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.width(12.dp))
            Text(entry.status.label, style = AgentVisitDesign.status,
                color = when (entry.status) {
                    HistoryStatus.Completed -> Color(0xFF22A447)
                    HistoryStatus.Partial -> Color(0xFFE69B20)
                    HistoryStatus.Cancelled -> Color(0xFFE96666)
                }, maxLines = 2, modifier = Modifier.widthIn(max = 132.dp))
        }
        HorizontalDivider(color = DealerDesign.divider)
        Text(if (showAddress) entry.visit.address else date, maxLines = 1, overflow = TextOverflow.Ellipsis,
            style = AgentVisitDesign.detail, color = DealerDesign.secondary,
            modifier = Modifier.padding(vertical = 12.dp))
    }
}
