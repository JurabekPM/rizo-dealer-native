package com.example.dealerappnative.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.data.dealerCountries
import com.example.dealerappnative.data.dealerRegions
import com.example.dealerappnative.data.dealerKokandDistricts
import com.example.dealerappnative.model.AdminPersonalProfile
import com.example.dealerappnative.ui.components.*
import com.example.dealerappnative.ui.theme.AgentVisitDesign
import com.example.dealerappnative.ui.theme.DealerDesign
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
internal fun AdminPersonalProfileScreen(profile: AdminPersonalProfile, dealerName: String,
    listState: LazyListState, onChange: (AdminPersonalProfile) -> Unit,
    role: String = "Администратор", onRoleChange: ((String) -> Unit)? = null) {
    val context = LocalContext.current
    val today = LocalDate.now(ZoneId.of("Asia/Tashkent"))
    LazyColumn(Modifier.fillMaxSize(), state = listState, contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item(key = "personal") { DealerProfileSection("Личные данные") {
            DealerProfileField("ФИО", profile.name, { onChange(profile.copy(name = it.take(120))) })
            DealerProfileField("Номер телефона", profile.phone, {
                onChange(profile.copy(phone = it.filter { c -> c in '0'..'9' || c in "+ ()-" }.take(32)))
            }, phone = true)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Дата рождения", style = AgentVisitDesign.detail, color = DealerDesign.secondary)
                Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).clip(RoundedCornerShape(12.dp)).background(DealerDesign.background)
                    .softCardPress {
                        val date = runCatching { LocalDate.parse(profile.birthDate) }.getOrDefault(today.minusYears(25))
                        DatePickerDialog(context, { _, y, m, d -> onChange(profile.copy(birthDate = LocalDate.of(y, m + 1, d).toString())) },
                            date.year, date.monthValue - 1, date.dayOfMonth).apply {
                            datePicker.maxDate = today.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() - 1
                            datePicker.minDate = LocalDate.of(1900, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                        }.show()
                    }.padding(horizontal = 12.dp).semantics { contentDescription = "Выбрать дату рождения" },
                    verticalAlignment = Alignment.CenterVertically) {
                    Text(runCatching { LocalDate.parse(profile.birthDate).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) }.getOrDefault("Не указана"),
                        style = AgentVisitDesign.name, modifier = Modifier.weight(1f))
                    DealerIcon(R.drawable.ic_agent_schedule, size = 22.dp)
                }
            }
            DealerProfileChoice("Пол", profile.gender, listOf("Не указан", "Мужской", "Женский")) { onChange(profile.copy(gender = it)) }
        } }
        item(key = "personal-region") { DealerProfileSection("Место проживания") {
            DealerProfileChoice("Страна", profile.country, dealerCountries) {
                if (it != profile.country) onChange(profile.copy(country = it, region = "", district = ""))
            }
            if (profile.country == "Узбекистан") DealerProfileChoice("Регион", profile.region, dealerRegions) {
                if (it != profile.region) onChange(profile.copy(region = it, district = ""))
            } else DealerProfileField("Регион", profile.region, { onChange(profile.copy(region = it)) })
            if (profile.region == "Коканд") DealerProfileChoice("Район", profile.district, dealerKokandDistricts) { onChange(profile.copy(district = it)) }
            else DealerProfileField("Район", profile.district, { onChange(profile.copy(district = it)) })
        } }
        item(key = "account") { DealerProfileSection("Учётная запись") {
            if (onRoleChange != null) DealerProfileChoice("Роль", role, com.example.dealerappnative.model.dealerPersonRoles, onRoleChange)
            else AdminAccountInfo("Роль", role)
            AdminAccountInfo("Юридическое лицо", dealerName)
        } }
    }
}

@Composable
private fun AdminAccountInfo(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, style = AgentVisitDesign.detail, color = DealerDesign.secondary)
        Text(value, style = AgentVisitDesign.name, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}
