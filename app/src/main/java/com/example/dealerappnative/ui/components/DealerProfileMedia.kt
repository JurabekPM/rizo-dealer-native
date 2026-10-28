package com.example.dealerappnative.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.dealerProfileThumbnail
import com.example.dealerappnative.data.retainDealerProfileImage
import com.example.dealerappnative.model.DealerProfile
import com.example.dealerappnative.ui.theme.ProfileDesign as Design
import com.example.dealerappnative.ui.theme.DealerDesign
import kotlinx.coroutines.launch

@Composable
internal fun DealerProfileMedia(profile: DealerProfile, onChange: (DealerProfile) -> Unit, onBusyChange: (Boolean) -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val current by rememberUpdatedState(profile)
    val currentChange by rememberUpdatedState(onChange)
    val busyChange by rememberUpdatedState(onBusyChange)
    var slot by rememberSaveable { mutableIntStateOf(0) }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val target = slot
            busy = true
            busyChange(true)
            scope.launch {
                try {
                    if (retainDealerProfileImage(context, uri)) {
                        currentChange(when (target) {
                            0 -> current.copy(logoUri = uri.toString())
                            1 -> current.copy(photoUri = uri.toString())
                            else -> current.copy(qrUri = uri.toString())
                        })
                    } else error = "Выберите изображение размером до 5 МБ"
                } finally {
                    busy = false
                    busyChange(false)
                }
            }
        }
    }
    fun select(index: Int) {
        if (busy) return
        slot = index
        error = null
        runCatching { picker.launch(arrayOf("image/*")) }.onFailure { error = "Не удалось открыть изображения" }
    }
    DealerProfileSection("Оформление") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            DealerProfileImage("Логотип", profile.logoUri, "48 × 48", Modifier.weight(1f), !busy) { select(0) }
            DealerProfileImage("Фото", profile.photoUri, "343 × 148", Modifier.weight(2f), !busy) { select(1) }
        }
        Text("Изображения до 5 МБ", style = Design.label, color = DealerDesign.secondary)
        androidx.compose.material3.HorizontalDivider(color = DealerDesign.divider)
        DealerProfileImage("UzQR магазина", profile.qrUri, "Статический QR-код банка магазина", Modifier.fillMaxWidth(), !busy) { select(2) }
        error?.let { Text(it, style = Design.label, color = androidx.compose.ui.graphics.Color(0xFFE45555)) }
        if (busy) Text("Проверяем изображение…", style = Design.label, color = DealerDesign.secondary)
    }
}

@Composable
private fun DealerProfileImage(label: String, uri: String, hint: String, modifier: Modifier, enabled: Boolean, onClick: () -> Unit) {
    val context = LocalContext.current
    val bitmap by produceState<android.graphics.Bitmap?>(null, uri) { value = dealerProfileThumbnail(context, uri) }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = Design.label, color = DealerDesign.secondary)
        Box(Modifier.fillMaxWidth().height(88.dp).clip(RoundedCornerShape(12.dp)).background(DealerDesign.background)
            .softCardPress(enabled = enabled, onClick = onClick), contentAlignment = Alignment.Center) {
            if (bitmap != null) Image(bitmap!!.asImageBitmap(), label, Modifier.fillMaxSize().padding(4.dp), contentScale = ContentScale.Fit)
            else Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                DealerIcon(R.drawable.ic_request_add, DealerDesign.accent, 24.dp)
                Text(if (uri.isBlank()) "Загрузить" else "Выбрать заново", style = Design.label, color = DealerDesign.accent)
            }
        }
        Text(hint, style = Design.label, color = DealerDesign.secondary)
    }
}
