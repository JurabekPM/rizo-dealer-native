package com.example.dealerappnative.navigation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.ui.components.DealerIcon
import com.example.dealerappnative.ui.theme.AgentDesign
import com.example.dealerappnative.ui.theme.AgentVisitDesign

/** Native safe-area padding is supplied by the Agent root, outside this bar. */
@Composable
internal fun AgentBottomBar(selected: AgentDestination, onSelect: (AgentDestination) -> Unit) {
    Column(Modifier.fillMaxWidth().background(Color.White)) {
        HorizontalDivider(thickness = 1.dp, color = AgentDesign.divider)
        Row(Modifier.fillMaxWidth().height(53.dp)) {
            AgentDestination.entries.forEach { destination ->
                val color = if (destination == selected) AgentDesign.accent else AgentVisitDesign.tertiary
                Column(Modifier.weight(1f).fillMaxHeight()
                    .selectable(selected = selected == destination, enabled = destination.available,
                        role = Role.Tab, interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = { onSelect(destination) }).padding(top = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    DealerIcon(destination.iconRes, color, 24.dp)
                    Spacer(Modifier.height(4.dp))
                    Text(destination.title, style = AgentVisitDesign.navigation, color = color, maxLines = 1,
                        overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}
