package com.example.dealerappnative.data

import com.example.dealerappnative.model.DebtDirection
import com.example.dealerappnative.model.VisitDetails
import com.example.dealerappnative.model.VisitResult
import com.example.dealerappnative.model.VisitStatus
import com.example.dealerappnative.model.VisitStore

internal const val demoActiveAgentVisitId = "agent-mixed-1001"

/** A mixture of untouched, in-progress and finished visit examples with 0–5 results. */
internal val mixedAgentVisitCards = listOf(
    "Ravnaq Market" to "Ташкент, улица Фидокор, 12",
    "Epa Yashnobod" to "Ташкент, улица Махтумкули, 45",
    "Istiqlol Savdo" to "Ташкент, улица Истиклол, 23",
    "Epa Mirobod" to "Ташкент, улица Мирабад, 16",
    "Yuksalish Market" to "Ташкент, улица Афросиёб, 34",
    "Epa Uchtepa" to "Ташкент, Учтепа, квартал 14",
    "Saodat Savdo" to "Ташкент, улица Зарафшан, 7",
    "Epa Bektemir" to "Ташкент, улица Хусейна Байкары, 28",
    "Dilshod Market" to "Ташкент, улица Олтинтепа, 63",
    "Madad Savdo" to "Ташкент, улица Тараккиёт, 51",
    "Epa Yangi Hayot" to "Ташкент, Янги Хаёт, квартал 2",
    "Zamin Market" to "Ташкент, улица Кушбеги, 18"
).mapIndexed { index, (name, address) ->
    val results = when (index) {
        0, 4, 8, 11 -> emptyList()
        1 -> listOf(VisitResult.Request)
        2 -> listOf(VisitResult.Request, VisitResult.Revision)
        3 -> listOf(VisitResult.Delivery, VisitResult.Request, VisitResult.Collection)
        5 -> VisitResult.entries.toList()
        6 -> listOf(VisitResult.Collection)
        7 -> listOf(VisitResult.Delivery, VisitResult.Request, VisitResult.Revision, VisitResult.Return)
        else -> listOf(VisitResult.Request, VisitResult.Collection, VisitResult.Return)
    }
    VisitStore("agent-mixed-${1001 + index}", name, "${index + 1} 250 000 UZS",
        if (results.isEmpty()) VisitStatus.NotVisited else VisitStatus.Completed,
        "${index + 2},3 км", address,
        details = VisitDetails("20.10.2026", "Valiev Muhiddin",
            if (VisitResult.Delivery in results) "Доставка завершена" else "Нет доставок",
            if (VisitResult.Request in results) "Заявка на ${index % 4 + 2} товара" else "Нет заявки",
            if (VisitResult.Revision in results) "Остатки проверены" else "Нет ревизии",
            if (VisitResult.Collection in results) "Получено 350 000 UZS" else "Нет инкассации",
            if (VisitResult.Return in results) "Возвращён 1 товар" else "Нет возвращённых товаров"),
        debtDirection = if (index % 4 == 2) DebtDirection.DealerOwesStore else DebtDirection.StoreOwesDealer,
        results = results)
}
