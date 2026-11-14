package com.example.dealerappnative.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun Modifier.unreadCardBackground(unread: Boolean): Modifier {
    val surface = background(if (unread) DealerDesign.softAccent else DealerDesign.surface)
    return if (unread) surface.semantics { stateDescription = "Не просмотрено" } else surface
}

@Composable
internal fun UnreadCardTitle(
    title: String,
    unread: Boolean,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    style: TextStyle = DealerDesign.cardTitle,
    color: Color = DealerDesign.text
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        if (unread) Box(Modifier.size(6.dp).background(DealerDesign.accent, CircleShape))
        Text(title, modifier = textModifier.weight(1f, fill = false), style = style, color = color,
            maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
internal fun UnreadBadge(count: Int, modifier: Modifier = Modifier, compact: Boolean = false) {
    val size = if (compact) Modifier.sizeIn(minWidth = 14.dp, minHeight = 14.dp) else Modifier
    Badge(
        modifier = modifier.then(size).clearAndSetSemantics {},
        containerColor = DealerDesign.accent,
        contentColor = DealerDesign.onAccent
    ) {
        SlidingNumber(count.coerceAtMost(99).toLong(),
            style = if (compact) DealerDesign.type(9, 11, tracking = 0f) else DealerDesign.type(10, 12, tracking = 0f),
            color = DealerDesign.onAccent, suffix = if (count > 99) "+" else "")
    }
}

@Composable
internal fun UnreadNavigationIcon(
    @DrawableRes iconRes: Int,
    tint: Color,
    unreadCount: Int,
    iconModifier: Modifier = Modifier
) {
    BadgedBox(badge = {
        if (unreadCount > 0) {
            UnreadBadge(
                count = unreadCount,
                modifier = Modifier.offset(x = 6.dp, y = (-6).dp),
                compact = true
            )
        }
    }) {
        DealerIcon(iconRes, tint, modifier = iconModifier)
    }
}
