package com.example.dealerappnative.model

import java.time.LocalDate
import java.time.ZoneId

/** Only self-editable personal data; role/access flags do not belong in this draft. */
internal data class AdminPersonalProfile(
    val name: String = "Хусанов Акбар",
    val phone: String = "+998901165115",
    val birthDate: String = "",
    val gender: String = "Не указан",
    val country: String = "Узбекистан",
    val region: String = "Коканд",
    val district: String = "Коканд",
    val photoUri: String = ""
)

internal fun normalizeAdminPhone(value: String): String = value.filterNot { it.isWhitespace() || it in "()-" }

internal fun displayAdminPhone(value: String): String {
    val phone = normalizeAdminPhone(value)
    if (!phone.startsWith("+998") || phone.length != 13) return phone
    return "+998 ${phone.substring(4, 6)} ${phone.substring(6, 9)} ${phone.substring(9, 11)} ${phone.substring(11)}"
}

internal fun AdminPersonalProfile.validationError(today: LocalDate = LocalDate.now(ZoneId.of("Asia/Tashkent"))): String? = when {
    name.trim().length !in 2..120 -> "Укажите ФИО"
    !normalizeAdminPhone(phone).matches(Regex("\\+[1-9][0-9]{7,14}")) -> "Укажите телефон с кодом страны, например +998 90 123 45 67"
    country.isBlank() || region.isBlank() || district.isBlank() -> "Укажите страну, регион и район"
    gender !in listOf("Не указан", "Мужской", "Женский") -> "Выберите пол"
    birthDate.isNotBlank() && runCatching { LocalDate.parse(birthDate).let { it.isAfter(today) || it.year < 1900 } }.getOrDefault(true) -> "Проверьте дату рождения"
    else -> null
}
