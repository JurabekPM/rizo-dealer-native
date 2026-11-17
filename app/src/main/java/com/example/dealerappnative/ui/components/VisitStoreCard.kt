package com.example.dealerappnative.ui.components

import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.VisitStore
import com.example.dealerappnative.navigation.LocalDealerWindowLayout
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.displayColor

/** A single card implementation keeps Agent and Administrator visit styling identical. */

@Composable
internal fun VisitStoreCard(
    store: VisitStore,
    unread: Boolean = false,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    val debtLabel = store.debtDirection.formatAmount(store.debt)
    val tablet = LocalDealerWindowLayout.current.hasRail
    val cardHeight = if (tablet) Modifier.heightIn(min = 84.dp) else Modifier.height(DealerDesign.cardHeight)
    val titleHeight = if (tablet) Modifier.heightIn(min = 52.dp).padding(vertical = 4.dp) else Modifier.height(72.dp)
    Column(Modifier.fillMaxWidth().then(cardHeight).softCardPress(onClick = onClick).cardShadow()
        .unreadCardBackground(unread).dealerSelectedCard(selected).padding(horizontal = 16.dp)) {
        Row(Modifier.fillMaxWidth().then(titleHeight), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.sharedVisitStorePart(store.id, VisitStorePart.Logo)) {
                DealerIcon(R.drawable.ic_store, DealerDesign.secondary, 24.dp)
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.weight(1f)) {
                UnreadCardTitle(store.name, unread,
                    textModifier = Modifier.sharedVisitStorePart(store.id, VisitStorePart.Name))
                Spacer(Modifier.height(4.dp))
                Text(debtLabel, style = DealerDesign.detail, color = store.debtDirection.displayColor,
                    modifier = Modifier.sharedVisitStorePart(store.id, VisitStorePart.Debt),
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.width(8.dp))
            Column(Modifier.align(Alignment.Top).padding(top = if (tablet) 4.dp else 16.dp),
                horizontalAlignment = Alignment.End) {
                Text(store.status.label, style = DealerDesign.detail, color = store.status.displayColor)
                if (store.results.isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    VisitResultsRow(store.results, store.id)
                }
            }
        }
        HorizontalDivider(color = DealerDesign.divider)
        val addressHeight = if (tablet) Modifier.heightIn(min = 31.dp).padding(vertical = 4.dp)
            else Modifier.weight(1f)
        Row(Modifier.fillMaxWidth().then(addressHeight), verticalAlignment = Alignment.CenterVertically) {
            Text(store.address, style = DealerDesign.detail, color = DealerDesign.secondary,
                modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.width(12.dp))
            Text(store.distance, style = DealerDesign.detail, color = DealerDesign.secondary,
                maxLines = 1, softWrap = false)
        }
    }
}
