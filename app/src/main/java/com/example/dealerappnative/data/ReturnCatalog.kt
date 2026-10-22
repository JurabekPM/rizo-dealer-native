package com.example.dealerappnative.data

import com.example.dealerappnative.R

internal data class ReturnCatalogProduct(
    val name: String, val price: Long, val available: Int, val barcode: String,
    val originalQuantity: Int = available, val photo: Int? = null
) {
    // Stable sample source document, independent of the quantity being returned.
    val orderNumber: String get() = (40_873_230L + barcode.takeLast(4).toLong()).toString()
}

/** Local fixtures. The first four entries retain their previous positions and prices. */
internal val returnCatalog = listOf(
    ReturnCatalogProduct("Дрель-шуруповёрт Bosch GSR 120-LI", 350_000L, 25, "2300000000001", 30),
    ReturnCatalogProduct("Перфоратор DeWalt D25134K", 425_000L, 12, "2300000000002", 15, R.drawable.return_dewalt),
    ReturnCatalogProduct("Аккумуляторная пила Makita CXT", 500_000L, 20, "2300000000003", 24),
    ReturnCatalogProduct("Аккумулятор Makita 12V", 275_000L, 18, "2300000000004", 20),
    ReturnCatalogProduct("Аккумуляторная дрель-шуруповёрт Makita CXT", 500_000L, 20, "2300000000005", 24, R.drawable.return_makita),
    ReturnCatalogProduct("Насос водяной автоматический EPA EVN-A200-2, 200 Вт", 412_500L, 6, "2300000000006", 8),
    ReturnCatalogProduct("Молоток EPA EMM-800, 800 г", 110_000L, 14, "2300000000007", 20),
    ReturnCatalogProduct("Молоток EPA EMM-200, 200 г", 55_000L, 9, "2300000000008", 12),
    ReturnCatalogProduct("Угловая шлифмашина Bosch GWS 750", 680_000L, 7, "2300000000009", 10),
    ReturnCatalogProduct("Электролобзик Makita 4329", 720_000L, 5, "2300000000010", 6),
    ReturnCatalogProduct("Рулетка EPA 5 м", 38_000L, 32, "2300000000011", 40),
    ReturnCatalogProduct("Набор отвёрток EPA, 6 предметов", 125_000L, 16, "2300000000012", 20),
    ReturnCatalogProduct("Плоскогубцы EPA 180 мм", 65_000L, 22, "2300000000013", 30),
    ReturnCatalogProduct("Ключ разводной EPA 250 мм", 98_000L, 11, "2300000000014", 15),
    ReturnCatalogProduct("Диск отрезной по металлу 125 мм", 15_000L, 48, "2300000000015", 60),
    ReturnCatalogProduct("Набор свёрл по металлу, 13 шт", 145_000L, 13, "2300000000016", 18),
    ReturnCatalogProduct("Уровень строительный EPA 60 см", 88_000L, 8, "2300000000017", 10),
    ReturnCatalogProduct("Удлинитель электрический 10 м", 175_000L, 10, "2300000000018", 12),
    ReturnCatalogProduct("Перчатки рабочие усиленные", 22_000L, 45, "2300000000019", 50),
    ReturnCatalogProduct("Набор шестигранных ключей, 9 шт", 79_000L, 17, "2300000000020", 20)
)

/** Restored drafts can predate catalogue additions; missing positions start unselected. */
internal fun normalizeReturnQuantities(values: List<Int>): List<Int> = returnCatalog.mapIndexed { index, product ->
    values.getOrElse(index) { 0 }.coerceIn(0, product.available)
}
