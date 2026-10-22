package com.example.dealerappnative.data

import com.example.dealerappnative.model.AgentHistoryEntry
import com.example.dealerappnative.model.DebtDirection
import com.example.dealerappnative.model.VisitResult
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

internal data class RevisionMonthBalance(val currency: String, val amount: BigDecimal) {
    val label: String get() {
        val number = NumberFormat.getNumberInstance(Locale.forLanguageTag("ru")).apply {
            maximumFractionDigits = 2
        }.format(amount.abs())
        return (if (amount.signum() < 0) "− " else "") + number + " " + currency
    }
}

/** Month-end revision balances: latest recorded snapshot per store, without mixing currencies. */
internal fun historyRevisionBalances(month: HistoryMonth, entries: List<AgentHistoryEntry>): List<RevisionMonthBalance> {
    val snapshots = entries.filter {
        !it.session.isActive && VisitResult.Revision in it.session.results && HistoryMonth.at(it.session.startedAt) == month
    }.groupBy { it.visit.id }.values.map { visits -> visits.maxBy { requireNotNull(it.session.finishedAt) } }
    val totals = mutableMapOf<String, BigDecimal>()
    snapshots.forEach { entry ->
        val raw = entry.visit.debt
        val currency = Regex("[A-Z]{3}").find(raw)?.value ?: "UZS"
        val value = raw.filter { it.isDigit() || it == ',' || it == '.' }.replace(',', '.').toBigDecimalOrNull()
            ?: BigDecimal.ZERO
        val signed = if (entry.visit.debtDirection == DebtDirection.StoreOwesDealer) value.negate() else value
        totals[currency] = (totals[currency] ?: BigDecimal.ZERO) + signed
    }
    if (totals.isEmpty()) return listOf(RevisionMonthBalance("UZS", BigDecimal.ZERO))
    return totals.entries.sortedWith(compareBy({ it.key != "UZS" }, { it.key }))
        .map { RevisionMonthBalance(it.key, it.value) }
}
