package com.example.dealerappnative.data

import com.example.dealerappnative.R

internal data class RequestCatalogProduct(val id: String, val name: String, val price: Long,
    val stock: Int, val photo: Int? = null, val previousPrice: Long? = null,
    val displayOrder: Int = Int.MAX_VALUE)

/** Existing positions/IDs stay stable for restored drafts; display sorting is UI-only. */
internal val requestCatalog = returnCatalog.mapIndexed { index, product ->
    RequestCatalogProduct(product.barcode,
        if (index == 4) "Аккумуляторная дрель Makita" else product.name,
        if (index == 4) 450_000L else product.price,
        if (index == 3) 0 else product.available * 3,
        when (index) {
            0 -> R.drawable.request_bosch
            1 -> R.drawable.return_dewalt
            4 -> R.drawable.return_makita
            else -> product.photo
        }, if (index == 4) 500_000L else null,
        when (index) { 4 -> 0; 0 -> 1; 1 -> 2; else -> 100 + index })
} + listOf(
    RequestCatalogProduct("request-xgt-40v", "Аккумуляторная дисковая пила XGT® 40 В макс", 500_000L,
        35, R.drawable.request_xgt, displayOrder = 3),
    RequestCatalogProduct("request-ryobi-r18dd3", "Аккумуляторная дрель-шуруповерт Ryobi R18DD3-0", 500_000L,
        150, R.drawable.request_ryobi, displayOrder = 4)
)

internal fun normalizeRequestQuantities(values: List<Int>): List<Int> = requestCatalog.mapIndexed { index, product ->
    values.getOrElse(index) { 0 }.coerceIn(0, product.stock)
}
