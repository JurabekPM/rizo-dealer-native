package com.example.dealerappnative.model

import java.util.Locale

internal data class DealerWorkingDay(
    val enabled: Boolean = true,
    val opensAt: Int = 9 * 60,
    val closesAt: Int = 20 * 60
)

/** Administrator business profile only. Currency/tariff do not change visit accounting. */
internal data class DealerProfile(
    val taxId: String = "",
    val name: String = "Diller pro",
    val interaction: String = "Свободный",
    val activity: String = "Строительство и ремонт",
    val currency: String = "UZS",
    val tariff: String = "Start",
    val country: String = "Узбекистан",
    val region: String = "Коканд",
    val district: String = "Коканд",
    val address: String = "Коканд",
    val latitude: Double = 40.5286,
    val longitude: Double = 70.9425,
    val logoUri: String = "",
    val photoUri: String = "",
    val qrUri: String = "",
    val days: List<DealerWorkingDay> = List(7) { DealerWorkingDay() },
    val pricesVisible: Boolean = false
)

internal val dealerWeekdays = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье")

internal fun dealerClock(minutes: Int): String = String.format(Locale.ROOT, "%02d:%02d", minutes / 60, minutes % 60)

internal fun DealerProfile.validationError(): String? = when {
    name.trim().length !in 2..120 -> "Укажите название юридического лица"
    taxId.isNotBlank() && (taxId.length !in listOf(9, 14) || taxId.any { it !in '0'..'9' }) -> "ИНН должен содержать 9 цифр, ПИНФЛ — 14"
    country.isBlank() || region.isBlank() || district.isBlank() -> "Укажите страну, регион и район"
    address.isBlank() -> "Укажите адрес"
    currency !in listOf("UZS", "USD") -> "Выберите валюту"
    !latitude.isFinite() || latitude !in -85.0..85.0 || !longitude.isFinite() || longitude !in -180.0..180.0 -> "Выберите точку на карте"
    days.size != 7 || days.any { it.opensAt !in 0..1439 || it.closesAt !in 0..1439 } -> "Проверьте рабочие часы"
    days.any { it.enabled && it.opensAt == it.closesAt } -> "Время открытия и закрытия не должно совпадать"
    else -> null
}

/** Copy times, not open/closed flags: weekends stay closed. */
internal fun DealerProfile.applyWorkingHoursToAll(): DealerProfile {
    val source = days.firstOrNull { it.enabled } ?: return this
    return copy(days = days.map { it.copy(opensAt = source.opensAt, closesAt = source.closesAt) })
}
