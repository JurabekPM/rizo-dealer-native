package com.example.dealerappnative

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.example.dealerappnative.location.loadStoreRoute
import com.example.dealerappnative.model.StoreRoute
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.dealerappnative.data.findVisitStore
import com.example.dealerappnative.data.findStore
import com.example.dealerappnative.data.sampleStores
import com.example.dealerappnative.data.StoreLocationPreferences
import com.example.dealerappnative.model.StoreLocation
import com.example.dealerappnative.navigation.DealerBottomNavigation
import com.example.dealerappnative.navigation.DealerDestination
import com.example.dealerappnative.screens.CollectionsScreen
import com.example.dealerappnative.screens.DeliveriesScreen
import com.example.dealerappnative.screens.AdminRevisionsScreen
import com.example.dealerappnative.screens.AdminReturnsScreen
import com.example.dealerappnative.screens.AdminProfileScreen
import com.example.dealerappnative.data.DealerProfilePreferences
import com.example.dealerappnative.screens.MenuScreen
import com.example.dealerappnative.screens.RequestsScreen
import com.example.dealerappnative.screens.AdminVisitsFlowScreen
import com.example.dealerappnative.screens.RoutesScreen
import com.example.dealerappnative.screens.StoresScreen
import com.example.dealerappnative.screens.StoreDetailScreen
import com.example.dealerappnative.screens.StoreMapScreen
import com.example.dealerappnative.ui.theme.DealerDesign
import com.example.dealerappnative.ui.theme.DealerPrototypeTheme

@Composable
internal fun DealerApp(initialTab: Int = 0) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(initialTab) }
    val destination = DealerDestination.entries[selectedIndex]
    var dealerProfileVisible by rememberSaveable { mutableStateOf(false) }
    var revisionsVisible by rememberSaveable { mutableStateOf(false) }
    var returnsVisible by rememberSaveable { mutableStateOf(false) }
    var routesVisible by rememberSaveable { mutableStateOf(false) }
    var selectedStoreId by rememberSaveable { mutableStateOf<String?>(null) }
    val openedStore = findVisitStore(selectedStoreId)
    var storesVisible by rememberSaveable { mutableStateOf(false) }
    var selectedMagazineId by rememberSaveable { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val administratorPreferences = remember(context) { DealerProfilePreferences(context) }
    var administrator by remember { mutableStateOf(administratorPreferences.readAdministrator()) }
    val locationPreferences = remember(context) { StoreLocationPreferences(context) }
    val storeLocations = remember {
        mutableStateMapOf<String, StoreLocation>().apply {
            sampleStores.forEach { put(it.id, locationPreferences.read(it)) }
        }
    }
    val routeScope = rememberCoroutineScope()
    val routes = remember { mutableStateMapOf<String, StoreRoute>() }
    val routeLoading = remember { mutableStateMapOf<String, Boolean>() }
    val routeJobs = remember { mutableMapOf<String, Job>() }
    fun refreshRoute(storeId: String) {
        routeJobs.remove(storeId)?.cancel()
        val point = storeLocations.getValue(storeId)
        routes.remove(storeId)
        routeLoading[storeId] = true
        routeJobs[storeId] = routeScope.launch {
            val result = loadStoreRoute(point)
            val current = storeLocations.getValue(storeId)
            if (current.latitude == point.latitude && current.longitude == point.longitude) {
                result?.let {
                    routes[storeId] = it
                    val updated = current.copy(route = it.summary)
                    storeLocations[storeId] = updated
                    locationPreferences.save(storeId, updated)
                }
                routeLoading[storeId] = false
            }
        }
    }
    // Recover a pending distance after process recreation or an earlier location edit.
    LaunchedEffect(Unit) {
        sampleStores.forEach { store ->
            val point = storeLocations.getValue(store.id)
            if (point.route == null && (point.latitude != store.latitude || point.longitude != store.longitude)) {
                refreshRoute(store.id)
            }
        }
    }
    var mapVisible by rememberSaveable { mutableStateOf(false) }
    val openedMagazine = findStore(selectedMagazineId)?.let {
        it.copy(address = storeLocations.getValue(it.id).address)
    }
    LaunchedEffect(mapVisible, selectedMagazineId) {
        val id = selectedMagazineId
        if (mapVisible && id != null && routes[id] == null && routeLoading[id] != true) refreshRoute(id)
    }
    val childScreenVisible = dealerProfileVisible || revisionsVisible || returnsVisible || routesVisible || openedStore != null || storesVisible || openedMagazine != null
    val storesScreenState = rememberSaveableStateHolder()
    val mainScreenState = rememberSaveableStateHolder()
    val closeChildScreen: () -> Unit = {
        when {
            dealerProfileVisible -> dealerProfileVisible = false
            returnsVisible -> returnsVisible = false
            revisionsVisible -> revisionsVisible = false
            mapVisible -> mapVisible = false
            selectedMagazineId != null -> selectedMagazineId = null
            storesVisible -> storesVisible = false
            else -> {
                routesVisible = false
                selectedStoreId = null
            }
        }
    }

    BackHandler(enabled = childScreenVisible, onBack = closeChildScreen)

    DealerPrototypeTheme {
        Scaffold(
            containerColor = if (childScreenVisible) DealerDesign.background else Color.White,
            topBar = {
                if (childScreenVisible) {
                    Spacer(Modifier.fillMaxWidth().windowInsetsTopHeight(WindowInsets.statusBars)
                        .background(Color.White))
                }
            },
            bottomBar = {
                if (!childScreenVisible) {
                    DealerBottomNavigation(
                        selected = destination,
                        onSelect = { selectedIndex = it.ordinal }
                    )
                }
            }
        ) { contentPadding ->
            Box(Modifier.fillMaxSize().padding(contentPadding)) {
                if (dealerProfileVisible) {
                    AdminProfileScreen(onBack = closeChildScreen, onSaved = { administrator = it; closeChildScreen() })
                } else if (revisionsVisible) {
                    AdminRevisionsScreen(onBack = closeChildScreen)
                } else if (returnsVisible) {
                    AdminReturnsScreen(onBack = closeChildScreen)
                } else if (mapVisible && openedMagazine != null) {
                    StoreMapScreen(
                        store = openedMagazine,
                        location = storeLocations.getValue(openedMagazine.id),
                        route = routes[openedMagazine.id],
                        routeLoading = routeLoading[openedMagazine.id] == true,
                        onBack = closeChildScreen,
                        onSave = { location ->
                            locationPreferences.save(openedMagazine.id, location)
                            storeLocations[openedMagazine.id] = location
                            refreshRoute(openedMagazine.id)
                        }
                    )
                } else if (openedMagazine != null) {
                    storesScreenState.SaveableStateProvider("detail-${openedMagazine.id}") {
                        StoreDetailScreen(store = openedMagazine, onBack = closeChildScreen,
                            onOpenMap = { mapVisible = true })
                    }
                } else if (storesVisible) {
                    storesScreenState.SaveableStateProvider("stores") {
                        StoresScreen(locations = storeLocations, onBack = closeChildScreen, onStoreClick = { selectedMagazineId = it })
                    }
                } else if (routesVisible) {
                    RoutesScreen(onBack = closeChildScreen)
                } else if (destination == DealerDestination.Visits) {
                    mainScreenState.SaveableStateProvider("visits-flow") {
                        AdminVisitsFlowScreen(selectedStoreId,
                            onRoutesClick = { routesVisible = true },
                            onStoreClick = { selectedStoreId = it }, onBack = closeChildScreen)
                    }
                } else {
                    // Restore the visit tab, query and scroll position after closing a child screen.
                    mainScreenState.SaveableStateProvider("main") {
                        when (destination) {
                            DealerDestination.Visits -> Unit // Handled by the shared visit flow above.
                            DealerDestination.Requests -> RequestsScreen()
                            DealerDestination.Deliveries -> DeliveriesScreen()
                            DealerDestination.Collections -> CollectionsScreen()
                            DealerDestination.Menu -> MenuScreen(onStoresClick = { storesVisible = true }, onReturnsClick = { returnsVisible = true }, onRevisionsClick = { revisionsVisible = true }, onEditDealerClick = { dealerProfileVisible = true }, administrator = administrator)
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Визиты", showBackground = true, widthDp = 393, heightDp = 852)
@Composable private fun VisitsPreview() = DealerApp(DealerDestination.Visits.ordinal)

@Preview(name = "Заявки", showBackground = true, widthDp = 393, heightDp = 852)
@Composable private fun RequestsPreview() = DealerApp(DealerDestination.Requests.ordinal)

@Preview(name = "Доставки", showBackground = true, widthDp = 393, heightDp = 852)
@Composable private fun DeliveriesPreview() = DealerApp(DealerDestination.Deliveries.ordinal)

@Preview(name = "Инкассация", showBackground = true, widthDp = 393, heightDp = 852)
@Composable private fun CollectionsPreview() = DealerApp(DealerDestination.Collections.ordinal)

@Preview(name = "Меню", showBackground = true, widthDp = 393, heightDp = 852)
@Composable private fun MenuPreview() = DealerApp(DealerDestination.Menu.ordinal)
