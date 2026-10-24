package com.example.dealerappnative.model

/** Snapshot prices and names: catalog changes must not rewrite a saved order. */
internal data class VisitRequestItem(val productId: String, val name: String, val price: Long, val quantity: Int) {
    init { require(productId.isNotBlank() && name.isNotBlank() && price >= 0 && quantity in 1..9999) }
    val total: Long get() = Math.multiplyExact(price, quantity.toLong())
}

internal data class VisitRequestRecord(val documentId: String, val createdAt: Long,
    val items: List<VisitRequestItem>) {
    init {
        require(documentId.isNotBlank() && createdAt > 0 && items.isNotEmpty())
        require(items.map { it.productId }.distinct().size == items.size)
    }
    val total: Long get() = items.fold(0L) { sum, item -> Math.addExact(sum, item.total) }
}

/** A saved request may be edited during its visit, never after finishing it. */
internal fun canSaveVisitRequest(session: AgentVisitSession?, items: List<VisitRequestItem>): Boolean =
    session?.isActive == true && items.isNotEmpty() && items.map { it.productId }.distinct().size == items.size
