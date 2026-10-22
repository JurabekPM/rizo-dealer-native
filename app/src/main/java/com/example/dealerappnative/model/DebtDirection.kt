package com.example.dealerappnative.model

import androidx.compose.ui.graphics.Color

/** Shared display rule for store/dealer balances in Visits and Collections. */
internal enum class DebtDirection(val color: Color) {
    DealerOwesStore(Color(0xFF15803D)),
    StoreOwesDealer(Color(0xFFDC2626));

    fun formatAmount(amount: String): String =
        if (this == StoreOwesDealer) "− $amount" else amount
}
