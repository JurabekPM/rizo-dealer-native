package com.example.dealerappnative.model

/** Read-only display contract. Each catalog supplies its OWN quantity limit; no saving rules live here. */
internal interface PickerProduct {
    val id: String
    val name: String
    val price: Long
    val stock: Int
    val photo: Int?
    val previousPrice: Long? get() = null
}
