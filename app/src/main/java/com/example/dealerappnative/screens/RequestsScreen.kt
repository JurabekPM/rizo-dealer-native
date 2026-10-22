package com.example.dealerappnative.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.OrderUiModel
import com.example.dealerappnative.ui.components.MenuRow
import com.example.dealerappnative.ui.components.OrderListScreen
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun RequestsScreen() {
    OrderListScreen(
        title = "Заявки",
        activeOrders = activeRequests,
        historyOrders = requestHistory,
        emptyMessage = "Заявки не найдены"
    ) {
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(DealerDesign.radius)).background(Color.White)) {
            MenuRow("Ревизия", R.drawable.ic_edit, rowHeight = 48.dp)
            MenuRow("Ежемесячный план", R.drawable.ic_visit, divider = false, rowHeight = 48.dp)
        }
    }
}

// Local sample data for visual testing.
private val activeRequests = listOf(
    OrderUiModel("18012076", "Eng zur magazin", "Azizbek Karimov", "1 250 000 UZS",
        3, "08.10.2026 | 15:41", "Магазин"),
    OrderUiModel("18011587", "SUPPER DUKON FILYAL 2", "Dilshod Rasulov", "840 000 UZS",
        1, "07.10.2026 | 10:35", "Заявщик")
)
private val requestHistory = listOf(
    OrderUiModel("18011109", "Epa Qo‘qon", "Умаров Хасан", "640 000 UZS",
        4, "06.10.2026 | 14:20", "Магазин"),
    OrderUiModel("18010942", "Epa Rishton", "Azizbek Karimov", "320 000 UZS",
        2, "05.10.2026 | 09:15", "Заявщик")
)
