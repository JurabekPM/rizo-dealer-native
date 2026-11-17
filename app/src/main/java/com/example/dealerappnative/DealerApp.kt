package com.example.dealerappnative

import android.content.Context
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.ui.components.DealerListDetail
import com.example.dealerappnative.data.UnreadContent
import com.example.dealerappnative.data.newStoreIds
import com.example.dealerappnative.data.activeVisits
import com.example.dealerappnative.data.adminNotifications
import com.example.dealerappnative.data.findVisitStore
import com.example.dealerappnative.model.VisitStatus
import com.example.dealerappnative.screens.NotificationsScreen
import com.example.dealerappnative.screens.newCollectionIds
import com.example.dealerappnative.screens.newDeliveryIds
import com.example.dealerappnative.screens.newRequestIds
import com.example.dealerappnative.data.findStore
import com.example.dealerappnative.data.sampleStores
import com.example.dealerappnative.data.StoreLocationPreferences
import com.example.dealerappnative.model.StoreLocation
import com.example.dealerappnative.navigation.DealerAdaptiveShell
import com.example.dealerappnative.navigation.DealerBottomNavigation
import com.example.dealerappnative.navigation.DealerNavigationRail
import com.example.dealerappnative.navigation.DealerTabletContent
import com.example.dealerappnative.navigation.LocalDealerWindowLayout
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

private enum class AdminMenuUnread { Stores }

@Composable
internal fun DealerApp(initialTab: Int = 0, onSettingsClick: () -> Unit = {}) {
    var selectedIndex by rememberSaveable { mutableIntStateOf(initialTab) }
    val destination = DealerDestination.entries[selectedIndex]
    var dealerProfileVisible by rememberSaveable { mutableStateOf(false) }
    var revisionsVisible by rememberSaveable { mutableStateOf(false) }
    var returnsVisible by rememberSaveable { mutableStateOf(false) }
    var routesVisible by rememberSaveable { mutableStateOf(false) }
    var notificationsVisible by rememberSaveable { mutableStateOf(false) }
    var selectedStoreId by rememberSaveable { mutableStateOf<String?>(null) }
    val openedStore = findVisitStore(selectedStoreId)
    var storesVisible by rememberSaveable { mutableStateOf(false) }
    var selectedMagazineId by rememberSaveable { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val unreadPreferences = remember(context) {
        context.applicationContext.getSharedPreferences("administrator_unread_content", Context.MODE_PRIVATE)
    }
    val unreadContent = remember(unreadPreferences) {
        UnreadContent(
            unreadPreferences,
            mapOf(
                DealerDestination.Visits to activeVisits.filter { it.status == VisitStatus.NotVisited }.map { it.id }.toSet(),
                DealerDestination.Requests to newRequestIds,
                DealerDestination.Deliveries to newDeliveryIds,
                DealerDestination.Collections to newCollectionIds,
                DealerDestination.Menu to adminNotifications.filter { it.isNew }.map { it.id }.toSet()
            )
        )
    }
    val storeUnreadContent = remember(unreadPreferences) {
        UnreadContent(unreadPreferences, mapOf(AdminMenuUnread.Stores to newStoreIds))
    }
    val unreadStores = storeUnreadContent[AdminMenuUnread.Stores]
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
    val menuTab = destination == DealerDestination.Menu
    val visitsTab = destination == DealerDestination.Visits
    val childScreenVisible = if (menuTab) {
        dealerProfileVisible || notificationsVisible || revisionsVisible || returnsVisible || storesVisible
    } else visitsTab && (routesVisible || openedStore != null)
    val storesScreenState = rememberSaveableStateHolder()
    val mainScreenState = rememberSaveableStateHolder()
    val closeChildScreen: () -> Unit = {
        if (menuTab) {
            when {
                dealerProfileVisible -> dealerProfileVisible = false
                notificationsVisible -> notificationsVisible = false
                returnsVisible -> returnsVisible = false
                revisionsVisible -> revisionsVisible = false
                mapVisible -> mapVisible = false
                selectedMagazineId != null -> selectedMagazineId = null
                else -> storesVisible = false
            }
        } else if (routesVisible) {
            routesVisible = false
        } else {
            selectedStoreId = null
        }
    }

    BackHandler(enabled = childScreenVisible, onBack = closeChildScreen)

    DealerPrototypeTheme {
        val unreadCounts = DealerDestination.entries.associateWith {
            unreadContent[it].size + if (it == DealerDestination.Menu) unreadStores.size else 0
        }
        val selectTab: (DealerDestination) -> Unit = { selectedIndex = it.ordinal }
        DealerAdaptiveShell(rail = {
            DealerNavigationRail(
                DealerDestination.entries, destination, unreadCounts,
                title = { it.title }, icon = { it.iconRes }, onSelect = selectTab
            )
        }) {
            val layout = LocalDealerWindowLayout.current
            Scaffold(
                containerColor = if (childScreenVisible) DealerDesign.background else DealerDesign.surface,
                topBar = {
                    if (childScreenVisible) {
                        Spacer(Modifier.fillMaxWidth().windowInsetsTopHeight(WindowInsets.statusBars)
                            .background(DealerDesign.surface))
                    }
                },
                bottomBar = {
                    if (!layout.hasRail && !childScreenVisible) {
                        DealerBottomNavigation(
                            selected = destination,
                            unreadCounts = unreadCounts,
                            onSelect = selectTab
                        )
                    }
                }
            ) { contentPadding ->
                Box(Modifier.fillMaxSize().padding(contentPadding)) {
                    val usePanes = when (destination) {
                        DealerDestination.Visits -> !routesVisible
                        DealerDestination.Menu -> storesVisible && !mapVisible
                        else -> true
                    }
                    val bodyWidth = when {
                        layout.hasPanes && usePanes -> 1200.dp
                        destination == DealerDestination.Deliveries -> 860.dp
                        else -> 600.dp
                    }
                    DealerTabletContent(maxWidth = bodyWidth) {
                        if (menuTab && dealerProfileVisible) {
                            AdminProfileScreen(onBack = closeChildScreen, onAdministratorChanged = { administrator = it })
                        } else if (menuTab && notificationsVisible) {
                            NotificationsScreen(
                                notifications = adminNotifications,
                                unreadIds = unreadContent[DealerDestination.Menu],
                                onRead = { unreadContent.markRead(DealerDestination.Menu, it) },
                                onBack = closeChildScreen
                            )
                        } else if (menuTab && revisionsVisible) {
                            AdminRevisionsScreen(onBack = closeChildScreen)
                        } else if (menuTab && returnsVisible) {
                            AdminReturnsScreen(onBack = closeChildScreen)
                        } else if (menuTab && mapVisible && openedMagazine != null) {
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
                        } else if (menuTab && storesVisible) {
                            DealerListDetail(
                                hasSelection = openedMagazine != null,
                                emptyIcon = R.drawable.ic_store,
                                emptyText = "Выберите магазин",
                                list = {
                                    storesScreenState.SaveableStateProvider("stores") {
                                        StoresScreen(
                                            locations = storeLocations,
                                            onBack = {
                                                selectedMagazineId = null
                                                storesVisible = false
                                            },
                                            unreadIds = unreadStores,
                                            selectedId = openedMagazine?.id,
                                            onStoreClick = { id ->
                                                storeUnreadContent.markRead(AdminMenuUnread.Stores, id)
                                                selectedMagazineId = id
                                            }
                                        )
                                    }
                                },
                                detail = {
                                    openedMagazine?.let { store ->
                                        storesScreenState.SaveableStateProvider("detail-${store.id}") {
                                            StoreDetailScreen(store, onBack = closeChildScreen,
                                                onOpenMap = { mapVisible = true })
                                        }
                                    }
                                }
                            )
                        } else if (visitsTab && routesVisible) {
                            RoutesScreen(onBack = closeChildScreen)
                        } else if (visitsTab) {
                            mainScreenState.SaveableStateProvider("visits-flow") {
                                AdminVisitsFlowScreen(selectedStoreId, unreadContent[DealerDestination.Visits],
                                    onRoutesClick = { routesVisible = true },
                                    onStoreClick = { id ->
                                        unreadContent.markRead(DealerDestination.Visits, id)
                                        selectedStoreId = id
                                    }, onBack = closeChildScreen)
                            }
                        } else {
                            // Keep each primary tab’s state when the tablet rail switches tabs.
                            mainScreenState.SaveableStateProvider(destination.name) {
                                when (destination) {
                                    DealerDestination.Visits -> Unit // Handled by the shared visit flow above.
                                    DealerDestination.Requests -> RequestsScreen(unreadContent[destination]) {
                                        unreadContent.markRead(destination, it)
                                    }
                                    DealerDestination.Deliveries -> DeliveriesScreen(unreadContent[destination]) {
                                        unreadContent.markRead(destination, it)
                                    }
                                    DealerDestination.Collections -> CollectionsScreen(unreadContent[destination]) {
                                        unreadContent.markRead(destination, it)
                                    }
                                    DealerDestination.Menu -> MenuScreen(
                                        onStoresClick = { storesVisible = true },
                                        onReturnsClick = { returnsVisible = true },
                                        onRevisionsClick = { revisionsVisible = true },
                                        onEditDealerClick = { dealerProfileVisible = true },
                                        onNotificationsClick = { notificationsVisible = true },
                                        onSettingsClick = onSettingsClick,
                                        unreadNotificationCount = unreadContent[destination].size,
                                        unreadStoreCount = unreadStores.size,
                                        administrator = administrator
                                    )
                                }
                            }
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
