package com.example.dealerappnative.screens

import com.example.dealerappnative.ui.components.SoftIconButton
import com.example.dealerappnative.ui.components.softCardPress
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
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
import com.example.dealerappnative.ui.components.DealerIcon
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun RoutesScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
        Box(
            Modifier.fillMaxWidth().height(68.dp).background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text("Маршруты", style = DealerDesign.title)
            SoftIconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp)
                    .size(48.dp).semantics { contentDescription = "Назад" }
            ) {
                DealerIcon(R.drawable.ic_arrow_back, DealerDesign.text)
            }
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn(Modifier.fillMaxSize()) {
            item {
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(DealerDesign.radius))
                        .background(Color.White).padding(horizontal = DealerDesign.pageInset)
                ) {
                    Text(
                        text = "Агенты",
                        style = DealerDesign.type(22, 28),
                        modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                    )
                    routeAgents.forEachIndexed { index, agent ->
                        AgentRow(agent)
                        if (index != routeAgents.lastIndex) {
                            HorizontalDivider(color = DealerDesign.divider)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AgentRow(agent: RouteAgent) {
    Row(Modifier.fillMaxWidth().height(68.dp), verticalAlignment = Alignment.CenterVertically) {
        DealerIcon(R.drawable.ic_person_outline)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(agent.name, style = DealerDesign.cardTitle,
                maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            Text(agent.storeCountLabel, style = DealerDesign.detail, color = DealerDesign.secondary)
        }
        Spacer(Modifier.width(8.dp))
        DealerIcon(R.drawable.ic_chevron, DealerDesign.secondary)
    }
}

private data class RouteAgent(val name: String, val storeCountLabel: String)

// The first routes screen follows the real app; agents' detail flows will be added separately.
private val routeAgents = listOf(
    RouteAgent("Valiev Muhiddin", "6 магазина"),
    RouteAgent("Khamidov Alisher", "5 магазина")
)
