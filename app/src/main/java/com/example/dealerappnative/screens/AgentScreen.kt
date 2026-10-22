package com.example.dealerappnative.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import com.example.dealerappnative.data.AgentVisitSessions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.dealerappnative.navigation.AgentDestination
import com.example.dealerappnative.navigation.AgentBottomBar

/** The agent owns its navigation; administrator destinations are not reused. */
@Composable
internal fun AgentScreen(onBack: () -> Unit) {
    var destination by rememberSaveable { mutableStateOf(AgentDestination.Visits) }
    val context = LocalContext.current.applicationContext
    val visitSessions = remember(context) { AgentVisitSessions(context) }
    val screenState = rememberSaveableStateHolder()
    var tabDetailVisible by rememberSaveable { mutableStateOf(false) }
    var storesVisible by rememberSaveable { mutableStateOf(false) }
    var storeDebtsVisible by rememberSaveable { mutableStateOf(false) }
    BackHandler {
        when {
            storeDebtsVisible -> storeDebtsVisible = false
            storesVisible -> storesVisible = false
            destination != AgentDestination.Visits -> destination = AgentDestination.Visits
            else -> onBack()
        }
    }
    Column(Modifier.fillMaxSize().background(Color.White).safeDrawingPadding()) {
        if (storeDebtsVisible) {
            AgentStoreDebtsScreen(onBack = { storeDebtsVisible = false })
        } else if (storesVisible) {
            AgentStoresScreen(onBack = { storesVisible = false })
        } else {
            Box(Modifier.weight(1f)) {
                screenState.SaveableStateProvider(destination.name) {
                    when (destination) {
                        AgentDestination.Visits -> AgentVisitsScreen(visitSessions, onDetailVisible = { tabDetailVisible = it })
                        AgentDestination.Stores -> AgentStoresScreen(onBack = { destination = AgentDestination.Visits },
                            onDetailVisible = { tabDetailVisible = it })
                        AgentDestination.Statistics -> AgentStatisticsScreen(onStoreDebtsClick = { storeDebtsVisible = true })
                        AgentDestination.History -> AgentHistoryScreen(visitSessions, onDetailVisible = { tabDetailVisible = it })
                        AgentDestination.More -> AgentMenuScreen(onExit = onBack, onStoresClick = { storesVisible = true })
                    }
                }
            }
            if (!tabDetailVisible) AgentBottomBar(selected = destination, onSelect = { destination = it })
        }
    }
}
