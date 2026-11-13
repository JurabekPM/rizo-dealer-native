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
import androidx.compose.runtime.remember
import com.example.dealerappnative.data.UnreadContent
import com.example.dealerappnative.data.agentNotifications
import com.example.dealerappnative.data.agentVisitCards
import com.example.dealerappnative.data.newAgentStoreIds
import com.example.dealerappnative.data.sampleStores
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
                AgentDestination.Stores to sampleStores.filter { it.id in newAgentStoreIds }.map { it.id }.toSet(),
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
    Column(Modifier.fillMaxSize().background(DealerDesign.surface).safeDrawingPadding()) {
        if (notificationsVisible) {
            NotificationsScreen(
                notifications = agentNotifications,
                unreadIds = unreadContent[AgentDestination.More],
                onRead = { unreadContent.markRead(AgentDestination.More, it) },
                onBack = { notificationsVisible = false }
            )
        } else if (storeDebtsVisible) {
            AgentStoreDebtsScreen(onBack = { storeDebtsVisible = false })
        } else if (storesVisible) {
            AgentStoresScreen(
                onBack = { storesVisible = false },
                unreadIds = unreadStores,
                onRead = onStoreRead
            )
        } else {
            Box(Modifier.weight(1f)) {
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
            if (!tabDetailVisible) {
                AgentBottomBar(
                    selected = destination,
                    unreadCounts = AgentDestination.entries.associateWith { unreadContent[it].size },
                    onSelect = { destination = it }
                )
            }
        }
    }
}
