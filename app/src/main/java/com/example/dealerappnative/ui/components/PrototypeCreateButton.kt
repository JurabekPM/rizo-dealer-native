package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.ui.theme.DealerDesign

/** Visual-only button until the create flow is added to the prototype. */
@Composable
internal fun PrototypeCreateButton(label: String = "Создать") {
    BottomActionPanel {
        PrototypeActionLabel(label, Modifier.fillMaxWidth().height(DealerDesign.buttonHeight))
    }
}

@Composable
internal fun PrototypeActionLabel(label: String, modifier: Modifier = Modifier) {
    Box(
        modifier.heightIn(min = DealerDesign.buttonHeight)
            .clip(RoundedCornerShape(DealerDesign.radius)).background(DealerDesign.accent)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, style = DealerDesign.button, color = DealerDesign.onAccent)
    }
}
