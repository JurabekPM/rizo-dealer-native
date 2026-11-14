package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun MapBackButton(onBack: () -> Unit, modifier: Modifier = Modifier, onMap: Boolean = false) {
    val shape = RoundedCornerShape(14.dp)
    Box(modifier.size(44.dp).softCardPress(onClick = onBack)
        .then(if (onMap) Modifier.shadow(4.dp, shape) else Modifier)
        .background(if (onMap) DealerDesign.surface else DealerDesign.background, shape)
        .semantics { contentDescription = "Назад" }, contentAlignment = Alignment.Center) {
        DealerIcon(R.drawable.ic_arrow_back, DealerDesign.text)
    }
}
