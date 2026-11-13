package com.example.dealerappnative.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.adminNotifications
import com.example.dealerappnative.ui.components.ChildScreenHeader
import com.example.dealerappnative.ui.components.DealerIcon
import com.example.dealerappnative.ui.components.adminCardBackground
import com.example.dealerappnative.ui.components.softCardPress
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun AdminNotificationsScreen(
    unreadIds: Set<String>,
    onRead: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
        ChildScreenHeader("Уведомления", onBack = onBack)
        LazyColumn(
            contentPadding = PaddingValues(vertical = DealerDesign.sectionGap),
            verticalArrangement = Arrangement.spacedBy(DealerDesign.sectionGap)
        ) {
            items(adminNotifications, key = { it.id }) { notification ->
                val unread = notification.id in unreadIds
                val markRead: (() -> Unit)? = if (unread) ({ onRead(notification.id) }) else null
                Row(
                    Modifier.fillMaxWidth().softCardPress(onClick = markRead)
                        .clip(RoundedCornerShape(DealerDesign.radius)).adminCardBackground(unread)
                        .padding(DealerDesign.pageInset),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DealerIcon(R.drawable.ic_bell, if (unread) DealerDesign.accent else DealerDesign.secondary)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(notification.title, style = DealerDesign.cardTitle)
                        Text(notification.message, style = DealerDesign.detail)
                        Text(notification.time, style = DealerDesign.detail, color = DealerDesign.secondary)
                    }
                }
            }
        }
    }
}
