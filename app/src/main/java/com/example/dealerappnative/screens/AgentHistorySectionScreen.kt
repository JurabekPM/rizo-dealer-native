package com.example.dealerappnative.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

/** Recorded documents, grouped by month; request-plan aliases keep restored navigation valid. */
@Composable
internal fun AgentHistorySectionScreen(section: HistorySection, entries: List<AgentHistoryEntry>, onBack: () -> Unit) {
    when (section) {
        HistorySection.Visits, HistorySection.Returns -> AgentHistoryArchiveScreen(section, entries, onBack)
        HistorySection.Sales, HistorySection.RequestPlan -> AgentSalesHistoryScreen(entries, onBack)
        HistorySection.Revisions, HistorySection.CollectionPlan -> MonthlyOperationsScreen(section, entries, onBack)
    }
}

@Composable
internal fun HistoryDocumentCard(entry: AgentHistoryEntry, amount: String?, onClick: () -> Unit) {
    HistorySurface(onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            DealerIcon(R.drawable.ic_store); Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(entry.visit.name, style = AgentVisitDesign.name, color = DealerDesign.text,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(entry.visit.id, style = AgentVisitDesign.detail, color = DealerDesign.secondary)
            }
            if (amount != null) Text(amount, style = AgentVisitDesign.detail, color = DealerDesign.text)
            DealerIcon(R.drawable.ic_chevron, size = 16.dp)
        }
        HorizontalDivider(color = DealerDesign.divider, modifier = Modifier.padding(top = 12.dp, bottom = 12.dp))
        Text(historyDate(requireNotNull(entry.session.finishedAt)), style = AgentVisitDesign.detail, color = DealerDesign.secondary)
    }
}

@Composable
internal fun HistoryEmpty(message: String) {
    Text(message, style = AgentVisitDesign.name, color = DealerDesign.secondary,
        modifier = Modifier.fillMaxWidth().padding(24.dp))
}
