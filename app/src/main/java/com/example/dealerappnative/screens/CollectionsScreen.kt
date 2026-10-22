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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.DebtDirection
import com.example.dealerappnative.ui.components.ActiveHistoryTabs
import com.example.dealerappnative.ui.components.DealerIcon
import com.example.dealerappnative.ui.components.PrototypeCreateButton
import com.example.dealerappnative.ui.components.ScreenHeader
import com.example.dealerappnative.ui.components.SearchField
import com.example.dealerappnative.ui.components.ShortcutCard
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun CollectionsScreen() {
    val pagerState = rememberPagerState(pageCount = { 2 })
    var query by rememberSaveable { mutableStateOf("") }
    val historySelected = pagerState.currentPage == 1

    Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
        ScreenHeader(
            title = "Инкассации",
            showSortIcon = true,
            actionIconRes = if (historySelected) R.drawable.ic_collection_filter else R.drawable.ic_sort
        )
        Column(
            Modifier.background(Color.White).padding(horizontal = DealerDesign.pageInset)
                .padding(bottom = 16.dp)
        ) {
            ActiveHistoryTabs(
                pagerState = pagerState,
                activeCount = activeCollections.size,
                badgeBackground = if (historySelected) Color(0xFFD5DADD) else Color(0xFFF5E5FF),
                badgeTextColor = if (historySelected) DealerDesign.text else DealerDesign.accent
            )
            Spacer(Modifier.height(16.dp))
            SearchField(value = query, onValueChange = { query = it })
        }

        // These two common cards stay in place when the pager changes pages.
        Spacer(Modifier.height(8.dp))
        ShortcutCard(
            title = "Запросы на сотрудничество",
            iconRes = R.drawable.ic_pending_actions,
            detail = "1 магазин",
            detailColor = Color(0xFF4094F7)
        )
        Spacer(Modifier.height(8.dp))
        ShortcutCard(title = "План инкассации", iconRes = R.drawable.ic_money)
        Spacer(Modifier.height(8.dp))

        HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
            val entries = if (page == 0) activeCollections else collectionHistory
            val search = query.trim()
            val visibleEntries = entries.filter {
                it.storeName.contains(search, ignoreCase = true) || it.storeId.contains(search)
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(visibleEntries, key = { it.collectionId }) { entry -> CollectionCard(entry) }
                if (visibleEntries.isEmpty()) {
                    item {
                        Text(
                            text = "Инкассации не найдены",
                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                            style = DealerDesign.body,
                            color = DealerDesign.secondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        PrototypeCreateButton()
    }
}

@Composable
private fun CollectionCard(entry: CollectionEntry) {
    Column(
        Modifier.fillMaxWidth().height(DealerDesign.cardHeight)
            .clip(RoundedCornerShape(DealerDesign.radius)).background(Color.White)
            .padding(horizontal = DealerDesign.pageInset)
    ) {
        Row(Modifier.fillMaxWidth().height(72.dp), verticalAlignment = Alignment.CenterVertically) {
            DealerIcon(R.drawable.ic_store)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(entry.storeName, style = DealerDesign.cardTitle,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = entry.debtDirection.formatAmount(entry.debtUsd),
                    style = DealerDesign.detail,
                    color = entry.debtDirection.color,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(entry.status.label, style = DealerDesign.cardTitle, color = entry.status.color,
                    maxLines = 1, softWrap = false)
                Spacer(Modifier.height(4.dp))
                Text(entry.kind, style = DealerDesign.detail, color = DealerDesign.secondary)
            }
        }
        HorizontalDivider(color = DealerDesign.divider)
        Row(Modifier.fillMaxWidth().weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = entry.address,
                modifier = Modifier.weight(1f),
                color = DealerDesign.secondary,
                style = DealerDesign.detail,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.width(8.dp))
            Text(entry.amount, color = DealerDesign.secondary, style = DealerDesign.detail,
                maxLines = 1, softWrap = false)
        }
    }
}

private enum class CollectionStatus(val label: String, val color: Color) {
    Confirmed("Подтверждено", Color(0xFF22C348)),
    Completed("Завершено", Color(0xFF22C348))
}

private data class CollectionEntry(
    val collectionId: String,
    val storeId: String,
    val storeName: String,
    val debtUsd: String,
    val status: CollectionStatus,
    val address: String,
    val amount: String,
    val debtDirection: DebtDirection = DebtDirection.StoreOwesDealer,
    val kind: String = "Внеплановые"
)

// Screen structure follows the real app; balances are local samples for the prototype.
private val activeCollections = listOf(
    CollectionEntry("collection-754", "754", "Epa Qo‘qon", "250 USD", CollectionStatus.Confirmed,
        "Узбекистан, Ферганская область, Коканд, улица Истиклол", "100 USD"),
    CollectionEntry("collection-760", "760", "Epa Rishton", "300 USD", CollectionStatus.Confirmed,
        "Узбекистан, Ферганская область, Риштан, махалля Навбахор", "50 USD"),
    CollectionEntry("collection-761", "761", "Epa Marg‘ilon", "120 USD", CollectionStatus.Confirmed,
        "Узбекистан, Ферганская область, Маргилан, улица Мустакиллик", "120 USD",
        DebtDirection.DealerOwesStore),
    CollectionEntry("collection-762", "762", "Epa Yaypan", "500 USD", CollectionStatus.Confirmed,
        "Узбекистан, Ферганская область, Узбекистанский район, Яйпан", "200 USD"),
    CollectionEntry("collection-763", "763", "Epa Farg‘ona", "80 USD", CollectionStatus.Confirmed,
        "Узбекистан, Ферганская область, Фергана, улица Алишера Навои", "80 USD",
        DebtDirection.DealerOwesStore),
    CollectionEntry("collection-764", "764", "Epa Dang‘ara", "550 USD", CollectionStatus.Confirmed,
        "Узбекистан, Ферганская область, Дангаринский район", "150 USD")
)

private val collectionHistory = listOf(
    CollectionEntry("collection-759-200", "759", "Epa Sulton", "50 USD", CollectionStatus.Completed,
        "Узбекистан, Ташкент, Яккасарайский район", "200 USD", DebtDirection.DealerOwesStore),
    CollectionEntry("collection-759-500", "759", "Epa Sulton", "180 USD", CollectionStatus.Completed,
        "Узбекистан, Ташкент, Яккасарайский район", "500 USD"),
    CollectionEntry("collection-757", "757", "Epa Begobod", "1 000 USD", CollectionStatus.Completed,
        "Узбекистан, Ферганская область, Учкуприкский район", "1 000 USD")
)
