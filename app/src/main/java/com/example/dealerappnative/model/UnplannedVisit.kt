package com.example.dealerappnative.model

/** A separate visit identity lets the same store be visited again without replacing its old session. */
internal enum class UnplannedVisitKind(val label: String) {
    Visit("Визит"), Request("Заявка"), Collection("Инкассация")
}

internal data class UnplannedVisit(val id: String, val store: StoreUiModel,
    val kind: UnplannedVisitKind, val createdAt: Long) {
    init { require(id.startsWith("unplanned-") && id != store.id && createdAt > 0) }
}
