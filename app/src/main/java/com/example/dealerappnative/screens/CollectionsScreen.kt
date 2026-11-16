package com.example.dealerappnative.screens

import com.example.dealerappnative.ui.components.BottomActionLayout
import com.example.dealerappnative.ui.theme.displayColor
import com.example.dealerappnative.ui.theme.dealerColor
import com.example.dealerappnative.ui.components.softCardPress
import com.example.dealerappnative.ui.components.cardShadow
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.DebtDirection
import com.example.dealerappnative.navigation.LocalDealerWindowLayout
import com.example.dealerappnative.ui.components.DealerDocumentDetails
import com.example.dealerappnative.ui.components.DealerListDetail
import com.example.dealerappnative.ui.components.dealerSelectedCard
import com.example.dealerappnative.ui.components.ActiveHistoryTabs
import com.example.dealerappnative.ui.components.DealerIcon
import com.example.dealerappnative.ui.components.PrototypeCreateButton
import com.example.dealerappnative.ui.components.ScreenHeader
import com.example.dealerappnative.ui.components.SearchField
import com.example.dealerappnative.ui.components.ShortcutCard
import com.example.dealerappnative.ui.components.UnreadCardTitle
import com.example.dealerappnative.ui.components.unreadCardBackground
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun CollectionsScreen(unreadIds: Set<String>, onRead: (String) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val activeListState = rememberLazyListState()
    val historyListState = rememberLazyListState()
    var query by rememberSaveable { mutableStateOf("") }
    val historySelected = pagerState.currentPage == 1

    val layout = LocalDealerWindowLayout.current
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    val opened = activeCollections.find { it.collectionId == selectedId }
        ?: collectionHistory.find { it.collectionId == selectedId }
    DealerListDetail(
        hasSelection = opened != null,
        emptyIcon = R.drawable.ic_money,
        emptyText = "Выберите инкассацию",
        list = {
            Column(Modifier.fillMaxSize().background(DealerDesign.background)) {
                ScreenHeader(
                    title = "Инкассации",
                    showSortIcon = true,
                    actionIconRes = if (historySelected) R.drawable.ic_collection_filter else R.drawable.ic_sort
                )
                Column(
                    Modifier.background(DealerDesign.surface).padding(horizontal = DealerDesign.pageInset)
                        .padding(bottom = 16.dp)
                ) {
                    ActiveHistoryTabs(
                        pagerState = pagerState,
                        activeCount = activeCollections.size
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
                    detailColor = dealerColor(Color(0xFF4094F7), Color(0xFF91B8FF))
                )
                Spacer(Modifier.height(8.dp))
                ShortcutCard(title = "План инкассации", iconRes = R.drawable.ic_money)
                Spacer(Modifier.height(8.dp))

                BottomActionLayout(Modifier.weight(1f), actions = {
                    PrototypeCreateButton()
                }) { actionHeight ->
                    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                        val entries = if (page == 0) activeCollections else collectionHistory
                        val search = query.trim()
                        val visibleEntries = entries.filter {
                            it.storeName.contains(search, ignoreCase = true) || it.storeId.contains(search)
                        }
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = if (page == 0) activeListState else historyListState,
                            contentPadding = PaddingValues(top = 8.dp, bottom = actionHeight + 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(visibleEntries, key = { it.collectionId }) { entry ->
                                val unread = entry.collectionId in unreadIds
                                val openEntry: (() -> Unit)? = if (unread || layout.hasRail) ({
                                    if (unread) onRead(entry.collectionId)
                                    if (layout.hasRail) selectedId = entry.collectionId
                                }) else null
                                CollectionCard(entry, unread, layout.hasPanes && selectedId == entry.collectionId, openEntry)
                            }
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
                }
            }
        },
        detail = {
            opened?.let { entry ->
                DealerDocumentDetails(
                    documentId = entry.collectionId,
                    title = "Инкассация",
                    fields = listOf(
                        "Магазин" to entry.storeName,
                        "ID магазина" to entry.storeId,
                        "Адрес" to entry.address,
                        "Статус" to entry.status.label,
                        "Тип" to entry.kind,
                        "Сальдо" to entry.debtDirection.formatAmount(entry.debtUsd),
                        "Сумма инкассации" to entry.amount
                    ),
                    onBack = { selectedId = null }
                )
            }
        }
    )
}

@Composable
private fun CollectionCard(entry: CollectionEntry, unread: Boolean, selected: Boolean, onClick: (() -> Unit)?) {
    Column(
        Modifier.fillMaxWidth().height(DealerDesign.cardHeight).softCardPress(onClick = onClick)
            .cardShadow().unreadCardBackground(unread).dealerSelectedCard(selected)
            .padding(horizontal = DealerDesign.pageInset)
    ) {
        Row(Modifier.fillMaxWidth().height(72.dp), verticalAlignment = Alignment.CenterVertically) {
            DealerIcon(R.drawable.ic_store)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                UnreadCardTitle(entry.storeName, unread)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = entry.debtDirection.formatAmount(entry.debtUsd),
                    style = DealerDesign.detail,
                    color = entry.debtDirection.displayColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(entry.status.label, style = DealerDesign.cardTitle, color = dealerColor(entry.status.color, Color(0xFF83D7A3)),
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
    val kind: String = "Внеплановые",
    val isNew: Boolean = false
)

// Screen structure follows the real app; balances are local samples for the prototype.
private val activeCollections = listOf(
    CollectionEntry("collection-84001", "84001", "Epa Chorsu", "310 USD", CollectionStatus.Confirmed,
        "Ташкент, площадь Чорсу", "120 USD", isNew = true),
    CollectionEntry("collection-84002", "84002", "Navbahor Savdo", "185 USD", CollectionStatus.Confirmed,
        "Ташкент, улица Фароби, 16", "75 USD", isNew = true),
    CollectionEntry("collection-84003", "84003", "Epa Uchtepa", "640 USD", CollectionStatus.Confirmed,
        "Ташкент, Учтепинский район, квартал 12", "250 USD", isNew = true),
    CollectionEntry("collection-84004", "84004", "Asil Market", "270 USD", CollectionStatus.Confirmed,
        "Ташкент, улица Бунёдкор, 24", "95 USD", isNew = true),
    CollectionEntry("collection-84005", "84005", "Epa Qibray", "430 USD", CollectionStatus.Confirmed,
        "Ташкентская область, Кибрай, улица Мустакиллик, 8", "180 USD", isNew = true),
    CollectionEntry("collection-754", "754", "Epa Qo‘qon", "250 USD", CollectionStatus.Confirmed,
        "Узбекистан, Ферганская область, Коканд, улица Истиклол", "100 USD", isNew = true),
    CollectionEntry("collection-760", "760", "Epa Rishton", "300 USD", CollectionStatus.Confirmed,
        "Узбекистан, Ферганская область, Риштан, махалля Навбахор", "50 USD", isNew = true),
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

internal val newCollectionIds = activeCollections.filter { it.isNew }.map { it.collectionId }.toSet()

private val collectionHistory = listOf(
    CollectionEntry("collection-759-200", "759", "Epa Sulton", "50 USD", CollectionStatus.Completed,
        "Узбекистан, Ташкент, Яккасарайский район", "200 USD", DebtDirection.DealerOwesStore),
    CollectionEntry("collection-759-500", "759", "Epa Sulton", "180 USD", CollectionStatus.Completed,
        "Узбекистан, Ташкент, Яккасарайский район", "500 USD"),
    CollectionEntry("collection-757", "757", "Epa Begobod", "1 000 USD", CollectionStatus.Completed,
        "Узбекистан, Ферганская область, Учкуприкский район", "1 000 USD")
)
