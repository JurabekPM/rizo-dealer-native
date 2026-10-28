package com.example.dealerappnative.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.DealerProfilePreferences
import com.example.dealerappnative.data.decodeDealerProfile
import com.example.dealerappnative.data.encodeDealerProfile
import com.example.dealerappnative.model.DealerProfile
import com.example.dealerappnative.model.validationError
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign
import kotlinx.coroutines.launch

private val profileSaver = Saver<DealerProfile, String>(save = { encodeDealerProfile(it) }, restore = { decodeDealerProfile(it) })

@Composable
internal fun DealerProfileScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val preferences = remember(context) { DealerProfilePreferences(context) }
    var profile by rememberSaveable(stateSaver = profileSaver) { mutableStateOf(preferences.read()) }
    var mapOpen by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var saving by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var imageBusy by remember { mutableStateOf(false) }
    fun update(value: DealerProfile) { profile = value; error = null }
    if (mapOpen) {
        DealerProfileLocationScreen(profile, onBack = { mapOpen = false }, onSelect = { point ->
            update(profile.copy(latitude = point.latitude, longitude = point.longitude, address = point.address)); mapOpen = false
        })
        return
    }
    BackHandler(onBack = { if (!saving) onBack() })
    Column(Modifier.fillMaxSize().background(DealerDesign.background).imePadding()) {
        ChildScreenHeader("Изменить данные дилера", { if (!saving) onBack() })
        LazyColumn(Modifier.weight(1f), state = listState, contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                DealerProfileChoice("Страна", profile.country, listOf("Узбекистан", "Россия", "Казахстан", "Беларусь")) {
                    if (it != profile.country) update(profile.copy(country = it, region = "", district = ""))
                }
                if (profile.country == "Узбекистан") DealerProfileChoice("Регион", profile.region, dealerRegions) {
                    if (it != profile.region) update(profile.copy(region = it, district = ""))
                } else DealerProfileField("Регион", profile.region, { update(profile.copy(region = it)) })
                if (profile.region == "Коканд") DealerProfileChoice("Район", profile.district, dealerKokandDistricts) { update(profile.copy(district = it)) }
                else DealerProfileField("Район", profile.district, { update(profile.copy(district = it)) })
                DealerProfileField("Адрес", profile.address, { update(profile.copy(address = it)) })
                Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).softCardPress { mapOpen = true }, verticalAlignment = Alignment.CenterVertically) {
                    DealerIcon(R.drawable.ic_pin, DealerDesign.accent)
                    Spacer(Modifier.width(8.dp))
                    Text("Выбрать на карте", style = AgentVisitDesign.name, color = DealerDesign.accent, modifier = Modifier.weight(1f))
                    DealerIcon(R.drawable.ic_chevron, DealerDesign.secondary, 20.dp)
                }
            } }
            item(key = "media") { DealerProfileMedia(profile, ::update, onBusyChange = { imageBusy = it }) }
            item(key = "hours") { DealerProfileWorkingHours(profile, ::update) }
            item(key = "price-visibility") { DealerProfileSection("Отображение цены") {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Цена доступна", style = AgentVisitDesign.name, modifier = Modifier.weight(1f))
                    Switch(profile.pricesVisible, { update(profile.copy(pricesVisible = it)) },
                        colors = SwitchDefaults.colors(checkedTrackColor = DealerDesign.accent))
                }
            } }
        }
        Column(Modifier.fillMaxWidth().background(Color.White).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            error?.let { Text(it, style = AgentVisitDesign.detail, color = Color(0xFFE45555)) }
            DealerProfileButton(if (saving) "Сохраняем…" else "Сохранить", !saving && !imageBusy) {
                error = profile.validationError()
                if (error == null) {
                    val snapshot = profile
                    saving = true
                    scope.launch {
                        val saved = preferences.save(snapshot)
                        saving = false
                        if (saved) { Toast.makeText(context, "Данные сохранены", Toast.LENGTH_SHORT).show(); onBack() }
                        else error = "Не удалось сохранить данные. Попробуйте ещё раз"
                    }
                }
            }
        }
    }
}

private val dealerActivities = listOf("Строительство и ремонт", "Электр-инструменты", "Продукты питания", "Электроника", "Бытовая техника", "Красота и здоровье", "Одежда", "Обувь", "Дом и сад", "Детские товары", "Спорт и отдых", "Товары для животных", "Книги", "Туризм, рыбалка, охота", "Автотовары", "Мебель", "Хобби и творчество", "Ювелирные украшения", "Аксессуары", "Игры и консоли", "Канцелярские товары", "Антиквариат и коллекционирование", "Цифровые товары", "Бытовая химия и гигиена", "Музыка и видео", "Аптека")
private val dealerRegions = listOf("Коканд", "Ташкент", "Ташкентская область", "Ферганская область", "Андижанская область", "Наманганская область", "Самаркандская область", "Бухарская область", "Навоийская область", "Джизакская область", "Хорезмская область", "Сурхандарьинская область", "Кашкадарьинская область", "Сырдарьинская область", "Республика Каракалпакстан")
private val dealerKokandDistricts = listOf("Коканд", "Дангара", "Бувайда", "Учкуприк", "Яйпан", "Бешарык", "Багдад", "Кува", "Олтыарык", "Куштепа", "Риштан")
