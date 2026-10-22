package com.example.dealerappnative.data

import com.example.dealerappnative.R
import com.example.dealerappnative.model.AgentHomeMetric

/** Local preview values from the light Super-courier design. */
internal object AgentHomeSampleData {
    const val completedVisits = 14
    const val plannedVisits = 20
    const val unplannedVisits = 2
    val metrics = listOf(
        AgentHomeMetric("Заявки", "1 240 $", R.drawable.ic_agent_orders, "9 заявок"),
        AgentHomeMetric("Доставки", "3 860 $", R.drawable.ic_agent_delivery, "8 из 12 доставок"),
        AgentHomeMetric("Инкассации", "2 150 $", R.drawable.ic_agent_wallet, "у меня 300 $ · сдано 1 850 $"),
        AgentHomeMetric("Возвраты", "120 $", R.drawable.ic_agent_return, "2 возврата", negative = true),
        AgentHomeMetric("Ревизии", "3", R.drawable.ic_agent_revision),
        AgentHomeMetric("АКБ / ОКБ", "42 / 70", R.drawable.ic_agent_store, "За сентябрь", progress = .6f)
    )
}
