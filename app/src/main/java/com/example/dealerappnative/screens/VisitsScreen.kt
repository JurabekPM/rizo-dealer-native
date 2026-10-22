package com.example.dealerappnative.screens

import com.example.dealerappnative.ui.components.softCardPress
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.LocalVisitReturns
import com.example.dealerappnative.data.activeVisits
import com.example.dealerappnative.data.pastVisits
import com.example.dealerappnative.ui.components.VisitStoreCard
import com.example.dealerappnative.ui.components.ActiveHistoryTabs
import com.example.dealerappnative.ui.components.DealerIcon
import com.example.dealerappnative.ui.components.ScreenHeader
import com.example.dealerappnative.ui.components.SearchField
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun VisitsScreen(
    onRoutesClick: () -> Unit, onStoreClick: (String) -> Unit, pagerState: PagerState,
    activeListState: LazyListState, historyListState: LazyListState,
    query: String, onQueryChange: (String) -> Unit
) {

    val returns = LocalVisitReturns.current
    Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
        ScreenHeader("Визиты")
        Column(Modifier.background(Color.White).padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
            ActiveHistoryTabs(
                pagerState = pagerState,
                activeCount = activeVisits.size,
                badgeBackground = Color(0xFFD5DADD),
                badgeTextColor = DealerDesign.text,
                inactiveTextColor = DealerDesign.text
            )
            Spacer(Modifier.height(8.dp))
            SearchField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = "Поиск по имени",
                height = 40.dp
            )
        }
        Spacer(Modifier.height(8.dp))
        // Routes are common to both tabs and stay outside the pager.
        Row(
            Modifier.fillMaxWidth().height(64.dp).softCardPress(onClick = onRoutesClick).clip(RoundedCornerShape(DealerDesign.radius))
                .background(Color.White)
                .padding(horizontal = DealerDesign.pageInset),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DealerIcon(R.drawable.ic_route)
            Spacer(Modifier.width(12.dp))
            Text("Маршруты", style = DealerDesign.body, modifier = Modifier.weight(1f))
            DealerIcon(R.drawable.ic_chevron, DealerDesign.tertiary)
        }
        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            val stores = (if (page == 0) activeVisits else pastVisits).map(returns::enrich)
            val search = query.trim()
            val visibleStores = stores.filter {
                it.name.contains(search, ignoreCase = true) ||
                    it.address.contains(search, ignoreCase = true)
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = if (page == 0) activeListState else historyListState,
                contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(visibleStores, key = { it.id }) { store -> VisitStoreCard(store, onClick = { onStoreClick(store.id) }) }
                if (visibleStores.isEmpty()) {
                    item {
                        Text(
                            text = "Магазины не найдены",
                            style = DealerDesign.body,
                            color = DealerDesign.secondary,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
