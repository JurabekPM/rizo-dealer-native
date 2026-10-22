package com.example.dealerappnative.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.model.OrderUiModel
import com.example.dealerappnative.ui.theme.DealerDesign

/** Shortcut cards are shared; only the order lists belong to the pager pages. */
@Composable
internal fun OrderListScreen(
    title: String,
    activeOrders: List<OrderUiModel>,
    historyOrders: List<OrderUiModel>,
    emptyMessage: String,
    headerContent: @Composable () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    var query by rememberSaveable(title) { mutableStateOf("") }
    val search = query.trim()

    Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
        ScreenHeader(title, showSortIcon = true)
        Column(
            Modifier.background(Color.White).padding(horizontal = DealerDesign.pageInset)
                .padding(bottom = 16.dp)
        ) {
            ActiveHistoryTabs(pagerState = pagerState, activeCount = activeOrders.size)
            Spacer(Modifier.height(16.dp))
            SearchField(value = query, onValueChange = { query = it })
        }
        Spacer(Modifier.height(8.dp))
        headerContent()
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            val orders = if (page == 0) activeOrders else historyOrders
            val visibleOrders = orders.filter { order ->
                order.storeName.contains(search, ignoreCase = true) ||
                    order.customerName.contains(search, ignoreCase = true) ||
                    order.id.contains(search)
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(visibleOrders, key = { it.id }) { order -> OrderCard(order) }
                if (visibleOrders.isEmpty()) {
                    item {
                        Text(
                            text = emptyMessage,
                            style = DealerDesign.body,
                            color = DealerDesign.secondary,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        PrototypeCreateButton()
    }
}
