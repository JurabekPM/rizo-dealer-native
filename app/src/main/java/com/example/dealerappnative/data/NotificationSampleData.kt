package com.example.dealerappnative.data

internal data class DealerNotification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val isNew: Boolean = false
)

internal val adminNotifications = listOf(
    DealerNotification("delivery-33446105", "Новая доставка", "Asil Market · №33446105", "13.11.2026 | 11:10", isNew = true),
    DealerNotification("collection-84003", "Инкассация подтверждена", "Epa Uchtepa · 250 USD", "13.11.2026 | 10:40", isNew = true),
    DealerNotification("request-18012105", "Новая заявка", "Epa Chorsu · №18012105", "13.11.2026 | 10:20", isNew = true),
    DealerNotification("request-18012104", "Новая заявка", "Navbahor Savdo · №18012104", "13.11.2026 | 10:05", isNew = true),
    DealerNotification("visit-84005", "Запланирован визит", "Epa Qibray · Umarov Alisher", "13.11.2026 | 09:00", isNew = true),
    DealerNotification("request-18012076", "Новая заявка", "Eng zur magazin · №18012076", "08.10.2026 | 15:41", isNew = true),
    DealerNotification("collection-754", "Инкассация подтверждена", "Epa Qo‘qon · 100 USD", "08.10.2026 | 12:20", isNew = true),
    DealerNotification("delivery-76493974", "Доставка обновлена", "Epa Sulton · №76493974", "06.10.2026 | 13:48")
)
