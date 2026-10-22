package com.example.dealerappnative.data

import com.example.dealerappnative.model.DebtDirection
import com.example.dealerappnative.model.VisitDetails
import com.example.dealerappnative.model.VisitStatus
import com.example.dealerappnative.model.VisitStore

/** Fresh visit IDs: no business results and no pre-created start/end timestamps. */
internal val pendingAgentVisitCards = listOf(
    Triple("Atlas Market", "Ташкент, улица Нукус, 18", "1 250 000 UZS"),
    Triple("Sifat Savdo", "Ташкент, улица Шахрисабз, 24", "480 000 UZS"),
    Triple("Epa Chilonzor", "Ташкент, Чиланзар, квартал 12", "2 700 000 UZS"),
    Triple("Omad Market", "Ташкент, улица Беруни, 56", "350 000 UZS"),
    Triple("Diyor Savdo", "Ташкент, улица Авиасозлар, 9", "920 000 UZS"),
    Triple("Epa Qorasuv", "Ташкент, Карасу, квартал 4", "1 630 000 UZS")
).mapIndexed { index, (name, address, debt) ->
    VisitStore(id = "agent-pending-${901 + index}", name = name, debt = debt,
        status = VisitStatus.NotVisited, distance = "${index + 1},${index + 2} км", address = address,
        debtDirection = if (index % 3 == 1) DebtDirection.DealerOwesStore else DebtDirection.StoreOwesDealer,
        details = VisitDetails("20.10.2026", "Valiev Muhiddin", "Нет доставок", "Нет заявки",
            "Нет ревизии", "Нет инкассации", "Нет возвращённых товаров"))
}

internal val pendingAgentVisitStores = pendingAgentVisitCards.map { visit ->
    sampleStores[0].copy(id = visit.id, name = visit.name, debt = visit.debt,
        debtDirection = visit.debtDirection, distance = visit.distance, address = visit.address,
        assignedRole = visit.details.employee, currency = "UZS")
}
