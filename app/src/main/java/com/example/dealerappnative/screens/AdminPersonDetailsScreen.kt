package com.example.dealerappnative.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.dealerappnative.R
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.components.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
internal fun AdminPersonDetailsScreen(person: DealerPerson, dealerName: String, listState: LazyListState) {
    val profile = person.profile
    LazyColumn(Modifier.fillMaxSize(), state = listState, contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { ProfileIdentity(profile.name, person.role, R.drawable.ic_person_outline,
            imageUri = profile.photoUri, initials = profileInitials(profile.name), self = person.isCurrentAdministrator) }
        item { DealerProfileSection("Личные данные") {
            ProfileInfo("Телефон", displayAdminPhone(profile.phone))
            ProfileInfo("Дата рождения", runCatching { LocalDate.parse(profile.birthDate).format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) }.getOrDefault("Не указана"))
            ProfileInfo("Пол", profile.gender)
        } }
        item { DealerProfileSection("Место проживания") {
            ProfileInfo("Страна", profile.country)
            ProfileInfo("Регион", profile.region)
            ProfileInfo("Район", profile.district)
        } }
        item { DealerProfileSection("Учётная запись") { ProfileInfo("Юридическое лицо", dealerName) } }
    }
}
