package com.example.dealerappnative.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import com.example.dealerappnative.data.LocalVisitReturns
import com.example.dealerappnative.data.findVisitStore
import com.example.dealerappnative.ui.components.VisitListViewport
import com.example.dealerappnative.ui.components.VisitResultsTransition

@Composable
internal fun AdminVisitsFlowScreen(
    selectedStoreId: String?, onRoutesClick: () -> Unit, onStoreClick: (String) -> Unit, onBack: () -> Unit
) {
    val returns = LocalVisitReturns.current
    val state = rememberSaveableStateHolder()
    // All list state lives outside the animated screen, including each pager's scroll position.
    val pager = rememberPagerState(pageCount = { 2 })
    val activeList = rememberLazyListState()
    val historyList = rememberLazyListState()
    var query by rememberSaveable { mutableStateOf("") }
    var activeIndex by rememberSaveable { mutableIntStateOf(0) }
    var activeOffset by rememberSaveable { mutableIntStateOf(0) }
    var historyIndex by rememberSaveable { mutableIntStateOf(0) }
    var historyOffset by rememberSaveable { mutableIntStateOf(0) }
    val closeVisit: () -> Unit = {
        // Clear only the closed detail subtree; list/pager positions remain hoisted.
        selectedStoreId?.let { state.removeState("detail-$it") }
        activeList.requestScrollToItem(activeIndex, activeOffset)
        historyList.requestScrollToItem(historyIndex, historyOffset)
        onBack()
    }
    BackHandler(enabled = selectedStoreId != null, onBack = closeVisit)
    VisitResultsTransition(selectedStoreId, onSelect = { id ->
        activeIndex = activeList.firstVisibleItemIndex
        activeOffset = activeList.firstVisibleItemScrollOffset
        historyIndex = historyList.firstVisibleItemIndex
        historyOffset = historyList.firstVisibleItemScrollOffset
        onStoreClick(id)
    }) { targetId, openVisit ->
        val store = findVisitStore(targetId)?.let(returns::enrich)
        if (store != null) state.SaveableStateProvider("detail-${store.id}") {
            VisitDetailScreen(store, closeVisit)
        } else state.SaveableStateProvider("list") {
            VisitListViewport(active = selectedStoreId == null) {
                VisitsScreen(onRoutesClick, openVisit, pager, activeList, historyList, query, { query = it })
            }
        }
    }
}
