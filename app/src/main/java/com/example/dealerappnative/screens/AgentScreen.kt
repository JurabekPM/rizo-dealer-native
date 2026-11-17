package com.example.dealerappnative.screens

import com.example.dealerappnative.ui.theme.DealerDesign
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.navigation.DealerAdaptiveShell
import com.example.dealerappnative.navigation.DealerNavigationRail
import com.example.dealerappnative.navigation.DealerTabletContent
import com.example.dealerappnative.navigation.LocalDealerWindowLayout
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import androidx.compose.runtime.remember
import com.example.dealerappnative.data.UnreadContent
import com.example.dealerappnative.data.agentNotifications
import com.example.dealerappnative.data.agentVisitCards
import com.example.dealerappnative.data.newStoreIds
import com.example.dealerappnative.data.AgentVisitSessions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.dealerappnative.model.VisitStatus
import com.example.dealerappnative.navigation.AgentDestination
import com.example.dealerappnative.navigation.AgentBottomBar

/** The agent owns its navigation; administrator destinations are not reused. */
@Composable
internal fun AgentScreen(onBack: () -> Unit, onSettingsClick: () -> Unit = {}) {
    var destination by rememberSaveable { mutableStateOf(AgentDestination.Visits) }
    val context = LocalContext.current.applicationContext
    val visitSessions = remember(context) { AgentVisitSessions(context) }
    val unreadContent = remember(context, visitSessions) {
        UnreadContent(
            context.getSharedPreferences("agent_unread_content", Context.MODE_PRIVATE),
            mapOf(
                AgentDestination.Visits to agentVisitCards.filter {
                    it.status == VisitStatus.NotVisited && it.id !in visitSessions.sessions
                }.map { it.id }.toSet(),
                AgentDestination.Stores to newStoreIds,
                AgentDestination.More to agentNotifications.filter { it.isNew }.map { it.id }.toSet()
            )
        )
    }
    val unreadStores = unreadContent[AgentDestination.Stores]
    val onStoreRead: (String) -> Unit = { unreadContent.markRead(AgentDestination.Stores, it) }
    val screenState = rememberSaveableStateHolder()
    val visitsListState = rememberLazyListState()
    var tabDetailVisible by rememberSaveable { mutableStateOf(false) }
    var notificationsVisible by rememberSaveable { mutableStateOf(false) }
    var storesVisible by rememberSaveable { mutableStateOf(false) }
    var storeDebtsVisible by rememberSaveable { mutableStateOf(false) }
    BackHandler {
        when {
            notificationsVisible -> notificationsVisible = false
            storeDebtsVisible -> storeDebtsVisible = false
            storesVisible -> storesVisible = false
            destination != AgentDestination.Visits -> destination = AgentDestination.Visits
            else -> onBack()
        }
    }
    val unreadCounts = AgentDestination.entries.associateWith { unreadContent[it].size }
    val selectTab: (AgentDestination) -> Unit = {
        if (destination != it) {
            notificationsVisible = false
            storesVisible = false
            storeDebtsVisible = false
            tabDetailVisible = false
            destination = it
        }
    }
    DealerAdaptiveShell(rail = {
        DealerNavigationRail(
            AgentDestination.entries, destination, unreadCounts,
            title = { it.title }, icon = { it.iconRes }, onSelect = selectTab,
            iconColor = AgentVisitDesign.tertiary, textStyle = AgentVisitDesign.navigation, enabled = { it.available }
        )
    }) {
        val layout = LocalDealerWindowLayout.current
        Column(Modifier.fillMaxSize().background(DealerDesign.surface).safeDrawingPadding()) {
            val bodyWidth = when {
                notificationsVisible || storeDebtsVisible -> 600.dp
                !layout.hasPanes -> 600.dp
                storesVisible -> 1200.dp
                destination == AgentDestination.Statistics -> 1200.dp
                destination == AgentDestination.More -> 600.dp
                else -> 1200.dp
            }
            if (notificationsVisible || storeDebtsVisible || storesVisible) {
                DealerTabletContent(maxWidth = bodyWidth) {
                    when {
                        notificationsVisible -> NotificationsScreen(
                            notifications = agentNotifications,
                            unreadIds = unreadContent[AgentDestination.More],
                            onRead = { unreadContent.markRead(AgentDestination.More, it) },
                            onBack = { notificationsVisible = false }
                        )
                        storeDebtsVisible -> AgentStoreDebtsScreen(
                            onBack = { storeDebtsVisible = false }, unreadIds = unreadStores, onRead = onStoreRead
                        )
                        else -> AgentStoresScreen(
                            onBack = { storesVisible = false }, unreadIds = unreadStores, onRead = onStoreRead
                        )
                    }
                }
            } else {
                Box(Modifier.weight(1f)) {
                    DealerTabletContent(maxWidth = bodyWidth) {
                        screenState.SaveableStateProvider(destination.name) {
                            when (destination) {
                                AgentDestination.Visits -> AgentVisitsScreen(
                                    visitSessions, visitsListState,
                                    unreadIds = unreadContent[AgentDestination.Visits],
                                    onRead = { unreadContent.markRead(AgentDestination.Visits, it) },
                                    onDetailVisible = { tabDetailVisible = it }
                                )
                                AgentDestination.Stores -> AgentStoresScreen(
                                    onBack = { destination = AgentDestination.Visits },
                                    unreadIds = unreadStores,
                                    onRead = onStoreRead,
                                    onDetailVisible = { tabDetailVisible = it }
                                )
                                AgentDestination.Statistics -> AgentStatisticsScreen(onStoreDebtsClick = { storeDebtsVisible = true })
                                AgentDestination.History -> AgentHistoryScreen(visitSessions, onDetailVisible = { tabDetailVisible = it })
                                AgentDestination.More -> AgentMenuScreen(
                                    onExit = onBack,
                                    onStoresClick = { storesVisible = true },
                                    unreadNotifications = unreadContent[AgentDestination.More].size,
                                    onNotificationsClick = { notificationsVisible = true },
                                    onSettingsClick = onSettingsClick
                                )
                            }
                        }
                    }
                }
                if (!layout.hasRail && !tabDetailVisible) {
                    AgentBottomBar(
                        selected = destination,
                        unreadCounts = unreadCounts,
                        onSelect = selectTab
                    )
                }
            }
        }
    }
}
