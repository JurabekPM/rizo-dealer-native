package com.example.dealerappnative.ui.components

import com.example.dealerappnative.ui.theme.dealerColor
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun Modifier.unreadCardBackground(unread: Boolean, soft: Boolean = false): Modifier {
    val color = when {
        !unread -> DealerDesign.surface
        soft -> DealerDesign.softAccent
        else -> dealerColor(Color(0xFFF5EAFF), Color(0xFF372845))
    }
    val surface = background(color)
    return if (unread) surface.semantics { stateDescription = "Не просмотрено" } else surface
}

@Composable
internal fun UnreadBadge(count: Int, modifier: Modifier = Modifier, compact: Boolean = false) {
    val size = if (compact) Modifier.sizeIn(minWidth = 14.dp, minHeight = 14.dp) else Modifier
    Badge(
        modifier = modifier.then(size).clearAndSetSemantics {},
        containerColor = DealerDesign.accent,
        contentColor = DealerDesign.onAccent
    ) {
        Text(
            text = if (count > 99) "99+" else count.toString(),
            style = if (compact) DealerDesign.type(9, 11, tracking = 0f)
                else DealerDesign.type(10, 12, tracking = 0f),
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
internal fun UnreadNavigationIcon(@DrawableRes iconRes: Int, tint: Color, unreadCount: Int) {
    BadgedBox(badge = {
        if (unreadCount > 0) {
            UnreadBadge(
                count = unreadCount,
                modifier = Modifier.offset(x = 6.dp, y = (-6).dp),
                compact = true
            )
        }
    }) {
        DealerIcon(iconRes, tint)
    }
}
