package com.example.dealerappnative.screens

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.dealerappnative.data.StoreLocationPreferences
import com.example.dealerappnative.data.findStore
import com.example.dealerappnative.data.sampleStores

/** Agent reads saved store locations; map access and location editing are not available. */
@Composable
internal fun AgentStoresScreen(onBack: () -> Unit, onDetailVisible: (Boolean) -> Unit = {}) {
    var selectedStoreId by rememberSaveable { mutableStateOf<String?>(null) }
    val screenState = rememberSaveableStateHolder()
    val context = LocalContext.current
    val preferences = remember(context) { StoreLocationPreferences(context) }
    val locations = remember(preferences) {
        sampleStores.associate { it.id to preferences.read(it) }
    }
    val store = findStore(selectedStoreId)?.let { it.copy(address = locations.getValue(it.id).address) }
    LaunchedEffect(store != null) { onDetailVisible(store != null) }
    DisposableEffect(Unit) { onDispose { onDetailVisible(false) } }
    val closeDetail: () -> Unit = { selectedStoreId = null }
    BackHandler(enabled = store != null, onBack = closeDetail)
    if (store != null) {
        screenState.SaveableStateProvider("detail-${store.id}") {
            AgentStoreDetailScreen(store, onBack = closeDetail)
        }
    } else {
        screenState.SaveableStateProvider("stores") {
            StoresScreen(locations, onBack = onBack, onStoreClick = { selectedStoreId = it })
        }
    }
}
