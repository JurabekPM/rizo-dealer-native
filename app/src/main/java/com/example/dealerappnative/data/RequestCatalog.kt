package com.example.dealerappnative.data

internal data class RequestCatalogProduct(val id: String, val name: String, val price: Long,
    val stock: Int, val photo: Int? = null, val previousPrice: Long? = null)

/** Separate order stock from the store's returnable stock. No inventory is modified. */
internal val requestCatalog = returnCatalog.mapIndexed { index, product ->
    RequestCatalogProduct(product.barcode, product.name, product.price,
        if (index == 3) 0 else product.available * 3, product.photo,
        if (index == 4) 550_000L else null)
}

internal fun normalizeRequestQuantities(values: List<Int>): List<Int> = requestCatalog.mapIndexed { index, product ->
    values.getOrElse(index) { 0 }.coerceIn(0, product.stock)
}
