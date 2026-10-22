package com.example.dealerappnative.navigation

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.ui.components.DealerIcon
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun DealerBottomNavigation(
    selected: DealerDestination,
    onSelect: (DealerDestination) -> Unit
) {
    Column(Modifier.background(Color.White).navigationBarsPadding()) {
        HorizontalDivider(color = DealerDesign.divider)
        Row(
            modifier = Modifier.fillMaxWidth().height(DealerDesign.navigationHeight)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DealerDestination.entries.forEach { destination ->
                val isSelected = destination == selected
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight()
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onSelect(destination) }.padding(top = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DealerIcon(
                        iconRes = destination.iconRes,
                        tint = if (isSelected) DealerDesign.accent else DealerDesign.secondary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = destination.title,
                        color = if (isSelected) DealerDesign.accent else DealerDesign.tertiary,
                        style = DealerDesign.navigation,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}
