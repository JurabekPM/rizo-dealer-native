package com.example.dealerappnative.data

import com.example.dealerappnative.model.DebtDirection
import com.example.dealerappnative.model.StoreUiModel

private data class DebtStoreSample(val name: String, val address: String, val debtUsd: String)

/** More local examples for scrolling, filtering and zero-balance states in Agent Home. */
internal val agentDebtSampleStores: List<StoreUiModel> = listOf(
    DebtStoreSample("Epa Yangiobod", "улица Янгиобод, 14", "2850"),
    DebtStoreSample("Baraka Market", "улица Мукими, 28", "0"),
    DebtStoreSample("Epa Rishton", "Риштан, улица Навои, 7", "1840"),
    DebtStoreSample("Fayz Savdo", "улица Фархад, 42", "475.5"),
    DebtStoreSample("Epa Yaypan", "Яйпан, улица Истиклол, 16", "3250"),
    DebtStoreSample("Nurli Market", "улица Бунёдкор, 31", "0"),
    DebtStoreSample("Epa Dang'ara", "Дангара, улица Мустакиллик, 9", "960"),
    DebtStoreSample("Ishonch Savdo", "улица Шота Руставели, 71Б", "128.75"),
    DebtStoreSample("Epa Qo'qon", "Коканд, улица Алишера Навои, 22", "4200"),
    DebtStoreSample("Chilonzor Market", "Чиланзар, квартал 12, дом 18", "0"),
    DebtStoreSample("Epa Buvayda", "Бувайда, улица Богистон, 5", "685"),
    DebtStoreSample("Ziyo Market", "улица Ойбек, 12", "199.5"),
    DebtStoreSample("Epa Olmazor", "Алмазар, улица Сагбан, 86", "1350"),
    DebtStoreSample("Saxovat Savdo", "улица Чопон-ота, 24", "0"),
    DebtStoreSample("Epa Sergeli", "Сергели, улица Янги Сергели, 8", "7280"),
    DebtStoreSample("Oila Market", "улица Катартал, 63", "88"),
    DebtStoreSample("Epa Yunusobod", "Юнусабад, квартал 6, дом 4", "1125"),
    DebtStoreSample("Besh Chinor", "улица Бешчинор, 11", "0"),
    DebtStoreSample("Epa Marg'ilon", "Маргилан, улица Бурханиддин Маргинони, 30", "2340"),
    DebtStoreSample("Hamkor Market", "улица Паркент, 47", "350.25"),
    DebtStoreSample("Epa Farg'ona", "Фергана, улица Мустакиллик, 54", "5670"),
    DebtStoreSample("Yuksalish Savdo", "улица Янги Узбекистон, 15", "0"),
    DebtStoreSample("Epa Quva", "Кува, улица Фергана, 19", "790"),
    DebtStoreSample("Do'stlik Market", "улица Дустлик, 26", "42.5")
).mapIndexed { index, sample ->
    StoreUiModel(
        id = "AG${1001 + index}", name = sample.name,
        debt = "${sample.debtUsd} USD", debtDirection = DebtDirection.StoreOwesDealer,
        distance = "${index + 2},4 км", address = sample.address,
        assignedRole = "Valiev Muhiddin", latitude = 41.31115, longitude = 69.27970
    )
}
