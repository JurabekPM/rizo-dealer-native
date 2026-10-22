package com.example.dealerappnative.screens

import com.example.dealerappnative.ui.components.softCardPress
import com.example.dealerappnative.ui.components.SoftIconButton
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.tween
import com.example.dealerappnative.location.findCurrentLocation
import com.example.dealerappnative.location.hasLocationPermission
import com.example.dealerappnative.model.StoreRoute
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Surface
import com.example.dealerappnative.ui.components.StoreMapInfoSheet
import com.example.dealerappnative.ui.components.DraggableMapSheet
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.location.findAddress
import com.example.dealerappnative.location.compactStoreAddress
import kotlinx.coroutines.delay
import com.example.dealerappnative.model.StoreLocation
import com.example.dealerappnative.model.StoreUiModel
import com.example.dealerappnative.ui.components.DealerIcon
import com.example.dealerappnative.ui.components.StoreMapView
import com.example.dealerappnative.ui.components.StoreMapMarker
import com.example.dealerappnative.ui.theme.DealerDesign

@Composable
internal fun StoreMapScreen(
    store: StoreUiModel,
    location: StoreLocation,
    route: StoreRoute?,
    routeLoading: Boolean,
    onBack: () -> Unit,
    onSave: (StoreLocation) -> Unit,
    allowEditing: Boolean = true
) {
    var editRequested by rememberSaveable(store.id) { mutableStateOf(false) }
    val editing = allowEditing && editRequested
    var latitude by rememberSaveable(store.id) { mutableDoubleStateOf(location.latitude) }
    var longitude by rememberSaveable(store.id) { mutableDoubleStateOf(location.longitude) }
    var address by rememberSaveable(store.id) { mutableStateOf(compactStoreAddress(location.address)) }
    var recenter by rememberSaveable(store.id) { mutableIntStateOf(0) }
    var tilesAvailable by rememberSaveable { mutableStateOf<Boolean?>(null) }
    var saved by rememberSaveable(store.id) { mutableStateOf(false) }

    val context = LocalContext.current
    var gpsRequest by remember { mutableIntStateOf(0) }
    var cameraRequest by remember { mutableIntStateOf(0) }
    var cameraTarget by remember { mutableStateOf<StoreLocation?>(null) }
    var locating by remember { mutableStateOf(false) }
    var gpsMessage by remember { mutableStateOf<String?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (hasLocationPermission(context)) gpsRequest++ else gpsMessage = "Разрешите доступ к геолокации"
    }
    LaunchedEffect(gpsRequest, editing) {
        if (gpsRequest == 0 || !editing) return@LaunchedEffect
        locating = true
        gpsMessage = null
        try {
            val point = findCurrentLocation(context)
            if (point != null) {
                cameraTarget = StoreLocation(point.latitude, point.longitude, "")
                cameraRequest++
            } else gpsMessage = "Не удалось найти местоположение. Попробуйте ещё раз"
        } finally { locating = false }
    }
    var addressLoading by remember { mutableStateOf(false) }
    var addressMessage by remember { mutableStateOf<String?>(null) }
    var manualAddress by remember { mutableStateOf(false) }
    var mapMoving by remember { mutableStateOf(false) }

    LaunchedEffect(editing, latitude, longitude, manualAddress) {
        if (!editing || manualAddress) return@LaunchedEffect
        address = ""
        addressMessage = null
        addressLoading = true
        delay(650)
        val result = findAddress(context, latitude, longitude)
        address = result.orEmpty()
        addressLoading = false
        addressMessage = if (result == null) "Адрес не найден. Введите вручную" else null
    }

    fun cancelEdit() {
        gpsRequest = 0
        editRequested = false
        gpsMessage = null
        cameraTarget = null
        manualAddress = false
        addressLoading = false
        addressMessage = null
        latitude = location.latitude
        longitude = location.longitude
        address = compactStoreAddress(location.address)
        recenter++
    }
    fun startEdit() {
        gpsRequest = 0
        cameraTarget = null
        gpsMessage = null
        manualAddress = false
        addressMessage = null
        latitude = location.latitude
        longitude = location.longitude
        address = compactStoreAddress(location.address)
        saved = false
        editRequested = true
        recenter++
    }
    BackHandler(enabled = editing) { cancelEdit() }

    DraggableMapSheet(editing = editing, sheetContent = {
        if (!editing) {
            StoreMapInfoSheet(store, location, route?.summary ?: location.route, routeLoading,
                onEdit = if (allowEditing) ({ startEdit() }) else null, saved = saved, showHandle = false)
        } else Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(Color.White).padding(horizontal = 16.dp).padding(top = 8.dp, bottom = 16.dp)) {
            Text(store.name, style = DealerDesign.cardTitle, modifier = Modifier.fillMaxWidth(),
                maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                DealerIcon(R.drawable.ic_pin)
                Spacer(Modifier.width(8.dp))
                BasicTextField(address, {
                    manualAddress = true
                    addressLoading = false
                    addressMessage = null
                    address = it
                }, textStyle = DealerDesign.body,
                    singleLine = true, cursorBrush = SolidColor(DealerDesign.accent),
                    modifier = Modifier.weight(1f).height(44.dp)
                        .semantics { contentDescription = "Адрес магазина" },
                    decorationBox = { inner ->
                        Box(Modifier.fillMaxSize().clip(RoundedCornerShape(8.dp))
                            .background(DealerDesign.background).padding(horizontal = 8.dp),
                            contentAlignment = Alignment.CenterStart) {
                            if (address.isEmpty()) Text(
                                if (mapMoving) "Выберите точку…" else if (addressLoading) "Определяем адрес…" else "Введите адрес",
                                style = DealerDesign.detail, color = DealerDesign.secondary
                            )
                            inner()
                        }
                    })
            }
            if (addressMessage != null) {
                Spacer(Modifier.height(4.dp))
                Text(addressMessage!!, style = DealerDesign.detail, color = DealerDesign.secondary)
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                val summary = route?.summary ?: location.route
                if (summary != null && !routeLoading) {
                    Text(summary.distanceText, style = DealerDesign.detail, color = DealerDesign.secondary)
                    Spacer(Modifier.width(20.dp))
                    Text(summary.durationText, style = DealerDesign.detail, color = DealerDesign.secondary)
                } else Text(if (routeLoading) "Прокладываем маршрут…" else "Маршрут недоступен",
                    style = DealerDesign.detail, color = DealerDesign.secondary)
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.height(48.dp).softCardPress { cancelEdit() }.clip(RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp), contentAlignment = Alignment.Center) {
                    Text("Отмена", style = DealerDesign.button, color = DealerDesign.secondary)
                }
                Spacer(Modifier.width(8.dp))
                val enabled = address.isNotBlank() && !addressLoading && !mapMoving && !locating
                Box(Modifier.weight(1f).height(DealerDesign.buttonHeight)
                    .softCardPress(enabled = enabled) {
                        onSave(StoreLocation(latitude, longitude, compactStoreAddress(address)))
                        editRequested = false
                        addressLoading = false
                        addressMessage = null
                        saved = true
                    }.clip(RoundedCornerShape(DealerDesign.radius))
                    .background(if (enabled) DealerDesign.accent else DealerDesign.tertiary), contentAlignment = Alignment.Center) {
                    Text("Сохранить локацию", style = DealerDesign.button, color = Color.White)
                }
            }
        }
    }) { controlsOffset, mapBottomInset ->
        Box(Modifier.fillMaxSize()) {
            StoreMapView(
                location = location, editing = editing, recenterRequest = recenter,
                route = route, cameraTarget = cameraTarget, cameraRequest = cameraRequest, bottomContentInset = mapBottomInset,
                onPositionChange = { lat, lng ->
                    if (kotlin.math.abs(latitude - lat) > .0000001 || kotlin.math.abs(longitude - lng) > .0000001) {
                        manualAddress = false
                        address = ""
                        addressLoading = true
                        latitude = lat
                        longitude = lng
                    }
                },
                onMapMovingChange = { mapMoving = it },
                onTileStatus = { tilesAvailable = it }, modifier = Modifier.fillMaxSize()
            )
            if (editing) {
                // The ground dot, rather than the animated badge, marks the exact map center.
                StoreMapMarker(Modifier.align(Alignment.Center).offset(y = (-28).dp)
                    .semantics { contentDescription = "Новая локация магазина" })

            }
            if (tilesAvailable == false) {
                Text("Карта недоступна. Проверьте интернет", style = DealerDesign.detail,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp,
                        start = 16.dp, end = 56.dp).clip(RoundedCornerShape(12.dp))
                        .background(Color.White).padding(12.dp))
            }
            MapIconButton("Назад", R.drawable.ic_arrow_back,
                onClick = { if (editing) cancelEdit() else onBack() },
                modifier = Modifier.align(Alignment.BottomStart).then(controlsOffset).padding(start = 12.dp, bottom = 24.dp))
            androidx.compose.animation.AnimatedVisibility(visible = editing,
                enter = fadeIn(tween(220)) + scaleIn(tween(220), initialScale = .7f),
                exit = fadeOut(tween(160)) + scaleOut(tween(160), targetScale = .7f),
                modifier = Modifier.align(Alignment.BottomEnd).then(controlsOffset).padding(end = 12.dp, bottom = 24.dp)) {
                MapIconButton(if (locating) "Поиск местоположения" else "Моё местоположение", R.drawable.ic_pin,
                    onClick = {
                        if (!locating) {
                            if (hasLocationPermission(context)) gpsRequest++
                            else permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION))
                        }
                    }, enabled = !locating, modifier = Modifier)
            }
            if (editing && (locating || gpsMessage != null)) {
                Text(if (locating) "Определяем местоположение…" else gpsMessage.orEmpty(),
                    style = DealerDesign.detail,
                    modifier = Modifier.align(Alignment.TopCenter).padding(16.dp)
                        .clip(RoundedCornerShape(12.dp)).background(Color.White).padding(12.dp))
            }
        }

    }
}

@Composable
private fun MapIconButton(label: String, icon: Int, onClick: () -> Unit, modifier: Modifier, enabled: Boolean = true) {
    SoftIconButton(onClick, enabled = enabled, modifier = modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(Color.White)
        .semantics { contentDescription = label }) {
        DealerIcon(icon, DealerDesign.text)
    }
}
