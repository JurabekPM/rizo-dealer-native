package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.StoreUiModel
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.StatementDesign as Design

/** One compact context card, scrolled with operations to leave more room for the history. */
@Composable
internal fun StoreStatementHeader(store: StoreUiModel, from: Long?, to: Long?, onDates: () -> Unit) {
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(36.dp).background(Design.selected, RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
                DealerIcon(R.drawable.ic_store, DealerDesign.accent, 22.dp)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(store.name, style = Design.name, color = Design.text, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(store.address, style = Design.caption, color = Design.secondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(Design.surface)
            .softCardPress(onClick = onDates).heightIn(min = 44.dp).padding(horizontal = 12.dp, vertical = 10.dp)
            .semantics { contentDescription = "Выбрать период истории" }, verticalAlignment = Alignment.CenterVertically) {
            DealerIcon(R.drawable.ic_agent_schedule, Design.secondary, 20.dp)
            Spacer(Modifier.width(10.dp))
            Text(statementPeriodLabel(from, to), style = Design.choice, color = Design.text,
                maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(8.dp))
            Box(Modifier.rotate(90f)) { DealerIcon(R.drawable.ic_chevron, Design.secondary, 16.dp) }
        }
    }
}
