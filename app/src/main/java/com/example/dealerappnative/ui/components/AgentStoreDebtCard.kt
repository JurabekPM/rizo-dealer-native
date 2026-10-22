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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.AgentStoreDebt
import com.example.dealerappnative.ui.theme.AgentDesign

@Composable
internal fun AgentStoreDebtCard(item: AgentStoreDebt, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth().softCardPress(onClick = onClick).clip(RoundedCornerShape(AgentDesign.radius)).background(Color.White)
        .padding(12.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.agentStoreSharedPart(item.store.id, AgentStorePart.Logo)) {
                DealerIcon(R.drawable.ic_agent_store, AgentDesign.secondary, AgentDesign.iconSize)
            }
            Spacer(Modifier.width(10.dp))
            Box(Modifier.weight(1f)) {
                Text(item.store.name, style = AgentDesign.menuTitle, color = AgentDesign.text,
                    modifier = Modifier.agentStoreSharedPart(item.store.id, AgentStorePart.Name),
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(Modifier.width(8.dp))
            Text("ID ${item.store.id}", style = AgentDesign.detail, color = AgentDesign.secondary,
                modifier = Modifier.agentStoreSharedPart(item.store.id, AgentStorePart.Id))
            Spacer(Modifier.width(4.dp))
            DealerIcon(R.drawable.ic_agent_chevron, AgentDesign.secondary, 18.dp)
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(item.store.address, style = AgentDesign.detail, color = AgentDesign.secondary,
                modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.width(12.dp))
            Text(item.amountText, style = AgentDesign.debt,
                modifier = Modifier.agentStoreSharedPart(item.store.id, AgentStorePart.Debt),
                color = if (item.hasDebt) AgentDesign.negative else AgentDesign.secondary,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
