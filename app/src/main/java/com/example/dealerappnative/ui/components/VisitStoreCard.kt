package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.example.dealerappnative.model.VisitStore
import com.example.dealerappnative.ui.theme.DealerDesign

/** A single card implementation keeps Agent and Administrator visit styling identical. */

@Composable
internal fun VisitStoreCard(store: VisitStore, onClick: () -> Unit) {
    val debtLabel = store.debtDirection.formatAmount(store.debt)
    Column(Modifier.fillMaxWidth().height(DealerDesign.cardHeight).softCardPress(onClick = onClick).clip(RoundedCornerShape(12.dp))
        .background(Color.White).padding(horizontal = 16.dp)) {
        Row(Modifier.fillMaxWidth().height(72.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.sharedVisitStorePart(store.id, VisitStorePart.Logo)) {
                DealerIcon(R.drawable.ic_store, DealerDesign.secondary, 24.dp)
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                Text(store.name, style = DealerDesign.cardTitle, maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.sharedVisitStorePart(store.id, VisitStorePart.Name))
                Spacer(Modifier.height(4.dp))
                Text(debtLabel, style = DealerDesign.detail, color = store.debtDirection.color,
                    modifier = Modifier.sharedVisitStorePart(store.id, VisitStorePart.Debt),
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.align(Alignment.Top).padding(top = 16.dp),
                horizontalAlignment = Alignment.End) {
                Text(store.status.label, style = DealerDesign.detail, color = store.status.color)
                if (store.results.isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    VisitResultsRow(store.results, store.id)
                }
            }
        }
        HorizontalDivider(color = DealerDesign.divider)
        Row(Modifier.fillMaxWidth().weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Text(store.address, style = DealerDesign.detail, color = DealerDesign.secondary,
                modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.width(12.dp))
            Text(store.distance, style = DealerDesign.detail, color = DealerDesign.secondary,
                maxLines = 1, softWrap = false)
        }
    }
}
