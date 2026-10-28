package com.example.dealerappnative.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.DealerProfile
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.ProfileDesign as Design
import com.example.dealerappnative.ui.theme.DealerDesign

/** Legal-entity form inside AdminProfileScreen; state/save/navigation belong to the host. */
@Composable
internal fun DealerProfileScreen(profile: DealerProfile, listState: LazyListState, onChange: (DealerProfile) -> Unit,
    onOpenMap: () -> Unit, onImageBusyChange: (Boolean) -> Unit) {
    fun update(value: DealerProfile) = onChange(value)
    LazyColumn(Modifier.fillMaxSize(), state = listState, contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item(key = "company") { DealerProfileSection("Юридическое лицо") {
            DealerProfileField("ИНН / ПИНФЛ", profile.taxId, { update(profile.copy(taxId = it.filter { c -> c in '0'..'9' }.take(14))) }, numeric = true)
            DealerProfileField("Наименование юридического лица", profile.name, { update(profile.copy(name = it.take(120))) })
            DealerProfileChoice("Тип взаимодействия", profile.interaction, listOf("Свободный", "Подконтрольный")) { update(profile.copy(interaction = it)) }
            DealerProfileChoice("Вид деятельности", profile.activity, dealerActivities) { update(profile.copy(activity = it)) }
        } }
        item(key = "preferences") { DealerProfileSection("Параметры") {
            DealerProfileChoice("Валюта по умолчанию", profile.currency, listOf("UZS", "USD")) { update(profile.copy(currency = it)) }
            DealerProfileChoice("Тип тарифа", profile.tariff, listOf("Start", "Business", "Premium")) { update(profile.copy(tariff = it)) }
        } }
        item(key = "address") { DealerProfileSection("Адрес") {
            DealerProfileChoice("Страна", profile.country, dealerCountries) {
                if (it != profile.country) update(profile.copy(country = it, region = "", district = ""))
            }
            if (profile.country == "Узбекистан") DealerProfileChoice("Регион", profile.region, dealerRegions) {
                if (it != profile.region) update(profile.copy(region = it, district = ""))
            } else DealerProfileField("Регион", profile.region, { update(profile.copy(region = it)) })
            if (profile.region == "Коканд") DealerProfileChoice("Район", profile.district, dealerKokandDistricts) { update(profile.copy(district = it)) }
            else DealerProfileField("Район", profile.district, { update(profile.copy(district = it)) })
            DealerProfileField("Адрес", profile.address, { update(profile.copy(address = it)) })
            Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).softCardPress { onOpenMap() }, verticalAlignment = Alignment.CenterVertically) {
                DealerIcon(R.drawable.ic_pin, DealerDesign.accent)
                Spacer(Modifier.width(8.dp))
                Text("Выбрать на карте", style = Design.value, color = DealerDesign.accent, modifier = Modifier.weight(1f))
                DealerIcon(R.drawable.ic_chevron, DealerDesign.secondary, 20.dp)
            }
        } }
        item(key = "media") { DealerProfileMedia(profile, ::update, onBusyChange = onImageBusyChange) }
        item(key = "hours") { DealerProfileWorkingHours(profile, ::update) }
        item(key = "price-visibility") { DealerProfileSection("Отображение цены") {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Цена доступна", style = Design.value, modifier = Modifier.weight(1f))
                Switch(profile.pricesVisible, { update(profile.copy(pricesVisible = it)) },
                    colors = SwitchDefaults.colors(checkedTrackColor = DealerDesign.accent))
            }
        } }
    }
}
