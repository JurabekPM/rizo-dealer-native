package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun ChildScreenHeader(
    title: String,
    onBack: () -> Unit,
    height: Dp = 56.dp,
    showFilter: Boolean = false
) {
    Box(Modifier.fillMaxWidth().height(height).background(Color.White), contentAlignment = Alignment.Center) {
        Text(title, style = DealerDesign.title, maxLines = 1, overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 56.dp))
        SoftIconButton(onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp).size(48.dp)
                .semantics { contentDescription = "Назад" }) {
            DealerIcon(R.drawable.ic_arrow_back, DealerDesign.text)
        }
        if (showFilter) {
            // Filter is visual-only until its options are designed.
            Box(Modifier.align(Alignment.CenterEnd).padding(end = 4.dp).size(48.dp),
                contentAlignment = Alignment.Center) {
                DealerIcon(R.drawable.ic_collection_filter, DealerDesign.text)
            }
        }
    }
}
