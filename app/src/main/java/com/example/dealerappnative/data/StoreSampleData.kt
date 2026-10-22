package com.example.dealerappnative.data

import com.example.dealerappnative.model.DebtDirection
import com.example.dealerappnative.model.StoreUiModel

// Independent sample stores for the Menu → Магазин prototype flow.
internal val sampleStores = listOf(
    StoreUiModel(
        id = "664", latitude = 41.3272, longitude = 69.2741, name = "Pos Lite", debt = "1 250 USD",
        debtDirection = DebtDirection.StoreOwesDealer, distance = "1,2 км",
        address = "улица Зульфияханум, 12", assignedRole = "Khamidov Alisher"
    ),
    StoreUiModel(
        id = "711", latitude = 41.2931, longitude = 69.2264, name = "Freedommmmmmmmmmmmmmmmmmmmmmmm", debt = "420 USD",
        debtDirection = DebtDirection.DealerOwesStore, distance = "3,6 км",
        address = "Novza mulkimiy", assignedRole = "Rasulov Dilshod"
    ),
    StoreUiModel(
        id = "613", latitude = 41.3101, longitude = 69.2797, name = "SUPPER DUKON", debt = "840 USD",
        debtDirection = DebtDirection.StoreOwesDealer, distance = "5,1 км",
        address = "MJK EPA OFES YONI", assignedRole = "Karimov Azizbek"
    ),
    StoreUiModel(
        id = "759", latitude = 41.2861, longitude = 69.2673, name = "Epa Sulton", debt = "9 316,5 USD",
        debtDirection = DebtDirection.StoreOwesDealer, distance = "8,7 км",
        address = "Узбекистан, Ташкент, Яккасарайский район",
        assignedRole = "Valiev Muhiddin"
    ),
    StoreUiModel(
        id = "758", latitude = 41.3456, longitude = 69.2102, name = "Epa Naymancha", debt = "320 USD",
        debtDirection = DebtDirection.DealerOwesStore, distance = "12,3 км",
        address = "Узбекистан, Ташкент, улица Гульхани",
        assignedRole = "Umarov Alisher"
    ),
    StoreUiModel(
        id = "760", latitude = 40.2211, longitude = 69.2697, name = "Epa Begobod", debt = "2 180 USD",
        debtDirection = DebtDirection.StoreOwesDealer, distance = "24,6 км",
        address = "Узбекистан, Ташкентская область, Бекабад",
        assignedRole = "Valiev Muhiddin"
    )
)

internal fun findStore(id: String?): StoreUiModel? = sampleStores.firstOrNull { it.id == id }
