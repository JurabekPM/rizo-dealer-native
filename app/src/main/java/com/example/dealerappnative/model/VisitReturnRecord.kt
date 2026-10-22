package com.example.dealerappnative.model

internal data class VisitReturnItem(val name: String, val price: Long, val quantity: Int) {
    init { require(name.isNotBlank() && price >= 0 && quantity in 1..99) }
    val total: Long get() = price * quantity
}

/** A local return document belongs to one selected visit; it does not finish that visit. */
internal data class VisitReturnRecord(val documentId: String, val createdAt: Long,
    val items: List<VisitReturnItem>, val comment: String = "",
    val reasonId: String? = null, val reasonLabel: String = "") {
    init { require(items.isNotEmpty()) }
    val total: Long get() = items.sumOf { it.total }
}
