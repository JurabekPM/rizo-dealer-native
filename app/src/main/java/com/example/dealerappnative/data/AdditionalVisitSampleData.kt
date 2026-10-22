package com.example.dealerappnative.data

import com.example.dealerappnative.model.DebtDirection
import com.example.dealerappnative.model.VisitDetails
import com.example.dealerappnative.model.VisitResult
import com.example.dealerappnative.model.VisitStatus
import com.example.dealerappnative.model.VisitStore

private data class VisitSeed(val name: String, val address: String, val debt: String,
    val results: List<VisitResult>, val status: VisitStatus = VisitStatus.Completed)

/** Extra visit-only samples; Menu/Stores and Statistics data are not affected. */
internal val additionalActiveVisits: List<VisitStore> = listOf(
    VisitSeed("Epa Olmazor", "Ташкент, улица Сагбан, 86", "1 750 000 UZS", VisitResult.entries.toList()),
    VisitSeed("Baraka Market", "Ташкент, улица Мукими, 28", "680 000 UZS", listOf(VisitResult.Delivery, VisitResult.Request)),
    VisitSeed("Epa Sergeli", "Ташкент, улица Янги Сергели, 8", "3 200 000 UZS", VisitResult.entries.toList()),
    VisitSeed("Fayz Savdo", "Ташкент, улица Фархад, 42", "420 000 UZS", listOf(VisitResult.Request, VisitResult.Revision, VisitResult.Collection), VisitStatus.Completed),
    VisitSeed("Nurli Market", "Ташкент, улица Бунёдкор, 31", "980 000 UZS", emptyList(), VisitStatus.Active),
    VisitSeed("Epa Yunusobod", "Ташкент, Юнусабад, квартал 6", "2 450 000 UZS", listOf(VisitResult.Delivery, VisitResult.Request, VisitResult.Revision, VisitResult.Collection)),
    VisitSeed("Ishonch Savdo", "Ташкент, улица Шота Руставели, 71Б", "150 000 UZS", listOf(VisitResult.Collection), VisitStatus.Completed),
    VisitSeed("Ziyo Market", "Ташкент, улица Ойбек, 12", "735 000 UZS", VisitResult.entries.toList()),
    VisitSeed("Saxovat Market", "Ташкент, улица Чопон-ота, 24", "1 180 000 UZS", listOf(VisitResult.Request, VisitResult.Revision), VisitStatus.Completed),
    VisitSeed("Oila Market", "Ташкент, улица Катартал, 63", "260 000 UZS", listOf(VisitResult.Delivery, VisitResult.Collection, VisitResult.Return)),
    VisitSeed("Epa Yangiobod", "Ташкент, улица Янгиобод, 14", "4 350 000 UZS", VisitResult.entries.toList()),
    VisitSeed("Hamkor Market", "Ташкент, улица Паркент, 47", "560 000 UZS", emptyList(), VisitStatus.NotVisited)
).mapIndexed { index, seed ->
    VisitStore(id = (761 + index).toString(), name = seed.name, debt = seed.debt,
        status = seed.status, distance = "${index + 2},4 км", address = seed.address,
        debtDirection = if (index % 4 == 1) DebtDirection.DealerOwesStore else DebtDirection.StoreOwesDealer,
        results = seed.results,
        details = VisitDetails("20.10.2026", "Valiev Muhiddin",
            if (VisitResult.Delivery in seed.results) "Доставка завершена" else "Нет доставок",
            if (VisitResult.Request in seed.results) "Заявка на 3 товара" else "Нет заявок",
            if (VisitResult.Revision in seed.results) "Ревизия завершена" else "Нет ревизии",
            if (VisitResult.Collection in seed.results) "Получено 250 000 UZS" else "Нет инкассации",
            if (VisitResult.Return in seed.results) "2 товара возвращены" else "Нет товаров для возврата"))
}
