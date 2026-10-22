package com.example.dealerappnative.data

import com.example.dealerappnative.model.AgentStoreDebt
import com.example.dealerappnative.model.DebtDirection
import com.example.dealerappnative.model.formatStoreDebt
import java.math.BigDecimal

/** Home totals and the full list always use the same USD store samples. */
internal object AgentStoreDebtData {
    val stores = (sampleStores + agentDebtSampleStores).map { store ->
        val amount = if (store.debtDirection == DebtDirection.StoreOwesDealer) {
            store.debt.filter { it.isDigit() || it == ',' || it == '.' }.replace(',', '.')
                .toBigDecimalOrNull()?.max(BigDecimal.ZERO) ?: BigDecimal.ZERO
        } else BigDecimal.ZERO
        AgentStoreDebt(store, amount)
    }.sortedWith(compareByDescending<AgentStoreDebt> { it.amount }.thenBy { it.store.name })
    val total: BigDecimal = stores.fold(BigDecimal.ZERO) { amount, store -> amount + store.amount }
    val withDebtCount: Int = stores.count { it.hasDebt }
    val withoutDebtCount: Int = stores.size - withDebtCount
    val totalText: String get() = "${formatStoreDebt(total)} USD"
    val homeTotalText: String get() = "${formatStoreDebt(total)} $"
}
