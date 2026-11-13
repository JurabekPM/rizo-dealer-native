package com.example.dealerappnative.data

import com.example.dealerappnative.model.DebtDirection
import com.example.dealerappnative.model.VisitResult
import com.example.dealerappnative.model.VisitDetails
import com.example.dealerappnative.model.VisitStatus
import com.example.dealerappnative.model.VisitStore

// Sample values for the UI prototype; no location or backend access.
internal val activeVisits = listOf(
    VisitStore("751", "Epa Yaypan", "5 250 000 UZS", VisitStatus.NotVisited, "2,4 км",
        "Узбекистан, Ферганская область, Багдадский район",
        details = VisitDetails("14.10.2026", "Valiev Muhiddin", "2 запланированные доставки", "Заявка на 3 товара", "Нет ревизии", "Запланировано 500 000 UZS", "Нет товаров для возврата")),
    VisitStore("750", "Epa Rishton", "1 840 000 UZS", VisitStatus.Completed, "8,7 км",
        "Узбекистан, Ферганская область, Коканд, махалля Навбахор",
        details = VisitDetails("14.10.2026", "Khamidov Alisher", "1 запланированная доставка", "Заявка на 5 товаров", "Ревизия от 12.10.2026", "Нет инкассации", "1 товар для возврата"),
        debtDirection = DebtDirection.DealerOwesStore,
        results = listOf(VisitResult.Request, VisitResult.Revision)),
    VisitStore("756", "Epa Qo‘qon", "750 000 UZS", VisitStatus.Completed, "12,3 км",
        "Узбекистан, Ферганская область, Коканд, улица Истиклол",
        details = VisitDetails("13.10.2026", "Karimov Azizbek", "Доставка завершена", "Заявка на 2 товара", "Ревизия завершена", "Получено 250 000 UZS", "Нет товаров для возврата"),
        results = listOf(VisitResult.Delivery, VisitResult.Request, VisitResult.Revision, VisitResult.Collection)),
    VisitStore("84001", "Epa Chorsu", "1 450 000 UZS", VisitStatus.NotVisited, "2,1 км",
        "Ташкент, площадь Чорсу",
        details = VisitDetails("13.11.2026", "Valiev Muhiddin", "1 запланированная доставка",
            "Нет заявок", "Нет ревизии", "Нет инкассации", "Нет товаров для возврата")),
    VisitStore("84002", "Navbahor Savdo", "780 000 UZS", VisitStatus.NotVisited, "3,4 км",
        "Ташкент, улица Фароби, 16",
        details = VisitDetails("13.11.2026", "Khamidov Alisher", "Нет доставок",
            "Нет заявок", "Запланирована ревизия", "Нет инкассации", "Нет товаров для возврата")),
    VisitStore("84003", "Epa Uchtepa", "2 360 000 UZS", VisitStatus.NotVisited, "5,8 км",
        "Ташкент, Учтепинский район, квартал 12",
        details = VisitDetails("13.11.2026", "Karimov Azizbek", "Нет доставок",
            "Нет заявок", "Нет ревизии", "Запланировано 400 000 UZS", "Нет товаров для возврата")),
    VisitStore("84004", "Asil Market", "420 000 UZS", VisitStatus.NotVisited, "7,2 км",
        "Ташкент, улица Бунёдкор, 24",
        details = VisitDetails("13.11.2026", "Rasulov Dilshod", "2 запланированные доставки",
            "Нет заявок", "Нет ревизии", "Нет инкассации", "Нет товаров для возврата"),
        debtDirection = DebtDirection.DealerOwesStore),
    VisitStore("84005", "Epa Qibray", "1 180 000 UZS", VisitStatus.NotVisited, "12,6 км",
        "Ташкентская область, Кибрай, улица Мустакиллик, 8",
        details = VisitDetails("13.11.2026", "Umarov Alisher", "Нет доставок",
            "Нет заявок", "Нет ревизии", "Запланировано 200 000 UZS", "Нет товаров для возврата"))
) + additionalActiveVisits
internal val pastVisits = listOf(
    VisitStore("752", "Epa Marg‘ilon", "320 000 UZS", VisitStatus.Completed, "5,1 км",
        "Узбекистан, Ферганская область, Маргилан",
        details = VisitDetails("12.10.2026", "Rasulov Dilshod", "2 доставки завершены", "Заявка на 4 товара", "Нет ревизии", "Получено 100 000 UZS", "2 товара возвращены"),
        debtDirection = DebtDirection.DealerOwesStore,
        results = listOf(VisitResult.Delivery, VisitResult.Request, VisitResult.Collection, VisitResult.Return)),
    VisitStore("753", "Epa Farg‘ona", "980 000 UZS", VisitStatus.Completed, "14,6 км",
        "Узбекистан, Ферганская область, Фергана",
        details = VisitDetails("11.10.2026", "Umarov Alisher", "Доставка завершена", "Заявка на 6 товаров", "Ревизия от 11.10.2026", "Получено 400 000 UZS", "1 товар возвращён"),
        results = listOf(VisitResult.Delivery, VisitResult.Request, VisitResult.Revision, VisitResult.Collection, VisitResult.Return))
)

internal fun findVisitStore(id: String?): VisitStore? =
    (activeVisits + pastVisits).firstOrNull { it.id == id }
