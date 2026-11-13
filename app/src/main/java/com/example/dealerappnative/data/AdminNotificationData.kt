package com.example.dealerappnative.data

internal data class AdminNotification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val isNew: Boolean = false
)

internal val adminNotifications = listOf(
    AdminNotification("request-18012076", "Новая заявка", "Eng zur magazin · №18012076", "08.10.2026 | 15:41", isNew = true),
    AdminNotification("collection-754", "Инкассация подтверждена", "Epa Qo‘qon · 100 USD", "08.10.2026 | 12:20", isNew = true),
    AdminNotification("delivery-76493974", "Доставка обновлена", "Epa Sulton · №76493974", "06.10.2026 | 13:48")
)
