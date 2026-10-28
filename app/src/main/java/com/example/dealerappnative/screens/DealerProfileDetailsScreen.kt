package com.example.dealerappnative.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.ProfileDesign as Design

/** Complete legal profile without any edit controls, upload launchers or switches. */
@Composable
internal fun DealerProfileDetailsScreen(profile: DealerProfile, listState: LazyListState) {
    LazyColumn(Modifier.fillMaxSize(), state = listState, contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { ProfileIdentity(profile.name, profile.activity, R.drawable.ic_store, imageUri = profile.logoUri) }
        item { DealerProfileSection("Основные сведения") {
            ProfileInfo("ИНН / ПИНФЛ", profile.taxId)
            ProfileInfo("Тип взаимодействия", profile.interaction)
            ProfileInfo("Валюта по умолчанию", profile.currency)
            ProfileInfo("Тип тарифа", profile.tariff)
            ProfileInfo("Цена доступна", if (profile.pricesVisible) "Да" else "Нет")
        } }
        item { DealerProfileSection("Адрес") {
            ProfileInfo("Страна", profile.country)
            ProfileInfo("Регион", profile.region)
            ProfileInfo("Район", profile.district)
            ProfileInfo("Адрес", profile.address)
        } }
        item { DealerProfileSection("Рабочие часы") {
            profile.days.forEachIndexed { index, day ->
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(dealerWeekdays[index], style = Design.body, color = Design.secondary, modifier = Modifier.weight(1f))
                    Text(if (day.enabled) "${dealerClock(day.opensAt)} — ${dealerClock(day.closesAt)}" else "Выходной",
                        style = Design.value, color = if (day.enabled) Design.text else Design.secondary)
                }
            }
        } }
        item { DealerProfileSection("Изображения") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ProfileImagePreview("Логотип", profile.logoUri, Modifier.weight(1f))
                ProfileImagePreview("Фото", profile.photoUri, Modifier.weight(2f))
            }
            ProfileImagePreview("UzQR магазина", profile.qrUri, Modifier.fillMaxWidth())
        } }
    }
}
