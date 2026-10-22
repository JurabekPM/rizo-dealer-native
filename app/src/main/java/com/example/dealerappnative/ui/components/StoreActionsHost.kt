package com.example.dealerappnative.ui.components

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.dealerappnative.data.StoreLocationPreferences
import com.example.dealerappnative.location.loadStoreRoute
import com.example.dealerappnative.model.DebtDirection
import com.example.dealerappnative.model.StoreRoute
import com.example.dealerappnative.model.StoreUiModel
import com.example.dealerappnative.screens.StoreMapScreen
import com.example.dealerappnative.ui.theme.DealerPrototypeTheme

internal class StoreActions(
    val openMap: (StoreUiModel) -> Unit,
    val dial: (String) -> Unit
)

internal val LocalStoreActions = staticCompositionLocalOf<StoreActions> {
    error("StoreActionsHost is required")
}

// Only the map identity is saved; the underlying details/list stays composed and keeps its scroll.
private val MapStoreSaver = listSaver<StoreUiModel?, Any>(
    save = { store -> store?.let { listOf(it.id, it.name, it.address, it.latitude, it.longitude) } ?: emptyList() },
    restore = { values -> if (values.isEmpty()) null else StoreUiModel(
        id = values[0] as String, name = values[1] as String, address = values[2] as String,
        latitude = values[3] as Double, longitude = values[4] as Double,
        debt = "", debtDirection = DebtDirection.StoreOwesDealer, distance = "", assignedRole = "") }
)

/** Shared read-only map/dial actions for Agent, history reports and Admin visit details.
 * Admin Menu/Stores keeps its existing editable map and location-save flow.
 */
@Composable
internal fun StoreActionsHost(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val preferences = remember(context) { StoreLocationPreferences(context) }
    var mapStore by rememberSaveable(stateSaver = MapStoreSaver) { mutableStateOf<StoreUiModel?>(null) }
    val actions = remember(context) {
        StoreActions(openMap = { mapStore = it }, dial = { number ->
            try {
                context.startActivity(Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", number, null)))
            } catch (_: ActivityNotFoundException) {
                Toast.makeText(context, "Приложение для звонков не найдено", Toast.LENGTH_SHORT).show()
            }
        })
    }
    CompositionLocalProvider(LocalStoreActions provides actions) { content() }

    mapStore?.let { store ->
        // Re-read persisted edits when a store is opened, including edits made by the administrator.
        val location = remember(store) { preferences.read(store) }
        var route by remember(store, location) { mutableStateOf<StoreRoute?>(null) }
        var loading by remember(store, location) { mutableStateOf(true) }
        LaunchedEffect(store.id, location) {
            try {
                route = loadStoreRoute(location)
                route?.let { preferences.save(store.id, location.copy(route = it.summary)) }
            } finally { loading = false }
        }
        Dialog(onDismissRequest = { mapStore = null }, properties = DialogProperties(
            usePlatformDefaultWidth = false, decorFitsSystemWindows = false
        )) {
            DealerPrototypeTheme {
                Box(Modifier.fillMaxSize().background(Color.White).safeDrawingPadding()) {
                    StoreMapScreen(store, location, route, loading,
                        onBack = { mapStore = null }, onSave = {}, allowEditing = false)
                }
            }
        }
    }
}
