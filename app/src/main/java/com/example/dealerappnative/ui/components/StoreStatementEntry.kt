package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.StoreUiModel
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign

/** Placed immediately after saldo, so both roles find the history beside the balance. */
@Composable
internal fun StoreStatementEntry(store: StoreUiModel) {
    val actions = LocalStoreActions.current
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White)
        .softCardPress { actions.openStatement(store) }.heightIn(min = 52.dp).padding(horizontal = 16.dp, vertical = 12.dp)
        .semantics { contentDescription = "История взаиморасчётов: ${store.name}" }, verticalAlignment = Alignment.CenterVertically) {
        DealerIcon(R.drawable.ic_agent_history, DealerDesign.secondary, 22.dp)
        Spacer(Modifier.width(12.dp))
        Text("История взаиморасчётов", style = AgentVisitDesign.name, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(8.dp))
        DealerIcon(R.drawable.ic_chevron, DealerDesign.tertiary, 18.dp)
    }
}
