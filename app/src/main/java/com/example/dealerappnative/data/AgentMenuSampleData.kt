package com.example.dealerappnative.data

/** Profile and menu values shown in the supplied reference; no account backend. */
internal object AgentMenuSampleData {
    const val name = "Valiev Muhiddin"
    const val phone = "+99800807799"
    const val initials = "VM"
    const val lastSync = "Последняя синхронизация: 19.10.2026 | 11:34"
}

// Sample assignments in the agent's store directory.
internal val newAgentStoreIds = setOf("758", "760")

internal val agentNotifications = listOf(
    DealerNotification("visit-901", "Назначен визит", "Atlas Market", "13.11.2026 | 09:20", isNew = true),
    DealerNotification("visit-902", "Назначен визит", "Sifat Savdo", "13.11.2026 | 09:15", isNew = true),
    DealerNotification("store-758", "Магазин добавлен", "Epa Naymancha", "13.11.2026 | 08:50", isNew = true),
    DealerNotification("store-760", "Магазин добавлен", "Epa Begobod", "13.11.2026 | 08:40", isNew = true),
    DealerNotification("schedule-13", "Расписание обновлено", "Проверьте список визитов", "13.11.2026 | 08:00", isNew = true),
    DealerNotification("sync-12", "Синхронизация завершена", "Данные обновлены", "12.11.2026 | 18:00")
)
