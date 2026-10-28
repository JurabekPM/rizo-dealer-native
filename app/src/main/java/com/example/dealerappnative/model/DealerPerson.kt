package com.example.dealerappnative.model

internal const val currentAdministratorId = "current-administrator"
internal val dealerPersonRoles = listOf("Администратор", "Бухгалтер", "Мерчендайзер", "Главный кассир", "Кассир",
    "Консультант", "Супер-курьер", "Курьер (товары)", "Курьер-инкассатор", "Торговый агент")

internal data class DealerPerson(val id: String, val profile: AdminPersonalProfile, val role: String) {
    val isCurrentAdministrator: Boolean get() = id == currentAdministratorId
}

internal fun dealerPeopleError(people: List<DealerPerson>): String? {
    if (people.count { it.isCurrentAdministrator } != 1 || people.any { it.id.isBlank() } || people.map { it.id }.distinct().size != people.size)
        return "Проверьте список сотрудников"
    people.forEach { person ->
        person.profile.validationError()?.let { return it }
        if (person.role !in dealerPersonRoles || (person.isCurrentAdministrator && person.role != "Администратор")) return "Выберите роль сотрудника"
    }
    if (people.map { normalizeAdminPhone(it.profile.phone) }.distinct().size != people.size) return "Сотрудник с таким номером уже существует"
    return null
}

/** Stable IDs isolate records even when names or phone numbers change. */
internal fun upsertDealerPerson(people: List<DealerPerson>, person: DealerPerson): List<DealerPerson> =
    if (people.any { it.id == person.id }) people.map { if (it.id == person.id) person else it } else people + person
