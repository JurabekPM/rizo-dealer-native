package com.example.dealerappnative.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.location.findAddress
import com.example.dealerappnative.model.DealerProfile
import com.example.dealerappnative.model.StoreLocation
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign
import kotlinx.coroutines.delay
import kotlin.math.abs

/** Local profile draft only; no OSRM request, store/session edits or GPS permission. */
@Composable
internal fun DealerProfileLocationScreen(profile: DealerProfile, onBack: () -> Unit, onSelect: (StoreLocation) -> Unit) {
    var lat by rememberSaveable { mutableDoubleStateOf(profile.latitude) }
    var lon by rememberSaveable { mutableDoubleStateOf(profile.longitude) }
    var address by rememberSaveable { mutableStateOf(profile.address) }
    var changed by rememberSaveable { mutableStateOf(false) }
    var manual by rememberSaveable { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val initialLocation = remember { StoreLocation(lat, lon, address) }
    LaunchedEffect(lat, lon, manual) {
        if (!changed || manual) return@LaunchedEffect
        loading = true
        delay(650)
        val result = findAddress(context, lat, lon)
        address = result.orEmpty()
        loading = false
    }
    BackHandler(onBack = onBack)
    Column(Modifier.fillMaxSize().background(Color.White).imePadding()) {
        ChildScreenHeader("Адрес на карте", onBack)
        Box(Modifier.weight(1f).fillMaxWidth()) {
            StoreMapView(initialLocation, editing = true,
                recenterRequest = 0, route = null, cameraTarget = null, cameraRequest = 0,
                onPositionChange = { a, b ->
                    if (abs(a - lat) > .000001 || abs(b - lon) > .000001) {
                        lat = a; lon = b; changed = true; manual = false
                    }
                },
                onTileStatus = {}, onMapMovingChange = {}, modifier = Modifier.fillMaxSize())
            StoreMapMarker(Modifier.align(Alignment.Center).offset(y = (-28).dp))
        }
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Передвиньте карту к нужной точке", style = AgentVisitDesign.detail, color = DealerDesign.secondary)
            DealerProfileField(if (loading) "Определяем адрес…" else "Адрес", address, { address = it; manual = true; loading = false })
            DealerProfileButton("Выбрать адрес", enabled = address.isNotBlank() && !loading) {
                onSelect(StoreLocation(lat, lon, address.trim()))
            }
        }
    }
}
