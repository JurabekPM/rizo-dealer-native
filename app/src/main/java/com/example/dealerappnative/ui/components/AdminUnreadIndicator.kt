package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.material3.Badge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import com.example.dealerappnative.ui.theme.DealerDesign

private val unreadCardColor = Color(0xFFF5EAFF)

internal fun Modifier.adminCardBackground(unread: Boolean): Modifier =
    if (unread) background(unreadCardColor).semantics { stateDescription = "Новое" }
    else background(Color.White)

@Composable
internal fun AdminUnreadBadge(count: Int, modifier: Modifier = Modifier) {
    Badge(
        modifier = modifier.clearAndSetSemantics {},
        containerColor = DealerDesign.accent,
        contentColor = Color.White
    ) {
        Text(if (count > 99) "99+" else count.toString(), style = DealerDesign.type(10, 12, tracking = 0f))
    }
}
