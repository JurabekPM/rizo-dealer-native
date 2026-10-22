package com.example.dealerappnative.model

import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

/** Receivable from this store, separate from any money the dealer owes to it. */
internal data class AgentStoreDebt(val store: StoreUiModel, val amount: BigDecimal) {
    val hasDebt: Boolean get() = amount.signum() > 0
    val amountText: String get() = if (hasDebt) "− ${formatStoreDebt(amount)} USD" else "0 USD"
}

internal fun formatStoreDebt(amount: BigDecimal): String = NumberFormat.getNumberInstance(Locale.forLanguageTag("ru"))
    .apply { minimumFractionDigits = 0; maximumFractionDigits = 2 }.format(amount)
