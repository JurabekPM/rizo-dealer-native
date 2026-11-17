package com.example.dealerappnative.screens

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import com.example.dealerappnative.navigation.LocalDealerWindowLayout
import com.example.dealerappnative.ui.theme.dealerColor
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.OrderUiModel
import com.example.dealerappnative.ui.components.OrderListScreen
import com.example.dealerappnative.ui.components.ShortcutCard

@Composable
internal fun DeliveriesScreen(unreadIds: Set<String>, onRead: (String) -> Unit) {
    OrderListScreen(
        title = "Доставки",
        activeOrders = activeDeliveries,
        unreadIds = unreadIds,
        onRead = onRead,
        historyOrders = deliveryHistory,
        emptyMessage = "Доставки не найдены",
        documentTitle = "Доставка",
        roundedTablet = true
    ) {
        val tablet = LocalDealerWindowLayout.current.hasRail
        val cooperation: @Composable (Modifier) -> Unit = { modifier ->
            ShortcutCard("Запросы на сотрудничество", R.drawable.ic_visit, "1 магазин",
                dealerColor(Color(0xFF529FDB), Color(0xFF91B8FF)), modifier, tablet)
        }
        val returns: @Composable (Modifier) -> Unit = { modifier ->
            ShortcutCard("Возвраты", if (tablet) R.drawable.ic_return else R.drawable.ic_store, "1 доставщик",
                dealerColor(Color(0xFFD66F6F), Color(0xFFFF939D)), modifier, tablet)
        }
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            if (tablet && maxWidth >= 500.dp) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    cooperation(Modifier.weight(1f))
                    returns(Modifier.weight(1f))
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    cooperation(Modifier)
                    returns(Modifier)
                }
            }
        }
    }
}

// Local sample data for visual testing.
private val activeDeliveries = listOf(
    OrderUiModel("33446105", "Asil Market", "Valiev Muhiddin", "940 000 UZS",
        3, "13.11.2026 | 11:10", "Магазин", isNew = true),
    OrderUiModel("33446104", "Epa Uchtepa", "Умаров Хасан", "1 650 000 UZS",
        4, "13.11.2026 | 10:35", "Магазин", isNew = true),
    OrderUiModel("33446103", "Epa Chorsu", "Azizbek Karimov", "720 000 UZS",
        2, "13.11.2026 | 10:10", "Заявщик", isNew = true),
    OrderUiModel("33446102", "Epa Qibray", "Khamidov Alisher", "2 380 000 UZS",
        5, "13.11.2026 | 09:40", "Магазин", isNew = true),
    OrderUiModel("33446101", "Navbahor Savdo", "Dilshod Rasulov", "290 000 UZS",
        1, "13.11.2026 | 09:15", "Заявщик", isNew = true),
    OrderUiModel("33446087", "Epa Qo‘qon", "Azizbek Karimov", "1 250 000 UZS",
        3, "10.10.2026 | 16:57", "Заявщик", isNew = true),
    OrderUiModel("33042772", "Epa Qo‘qon", "Dilshod Rasulov", "840 000 UZS",
        1, "10.10.2026 | 16:50", "Магазин", isNew = true),
    OrderUiModel("76493974", "Epa Sulton", "Умаров Хасан", "640 000 UZS",
        4, "06.10.2026 | 13:48", "Заявщик")
)
internal val newDeliveryIds = activeDeliveries.filter { it.isNew }.map { it.id }.toSet()

private val deliveryHistory = listOf(
    OrderUiModel("33446080", "Epa Rishton", "Azizbek Karimov", "320 000 UZS",
        2, "05.10.2026 | 09:15", "Магазин"),
    OrderUiModel("33042770", "Epa Marg‘ilon", "Dilshod Rasulov", "750 000 UZS",
        3, "04.10.2026 | 11:30", "Заявщик")
)
