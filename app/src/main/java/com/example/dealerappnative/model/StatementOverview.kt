package com.example.dealerappnative.model

import java.math.BigDecimal
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

internal data class StatementMonth(val key: String, val title: String, val entries: List<StoreStatementEntry>)
internal data class StatementCurrencyTotals(val currency: String, val orders: BigDecimal,
    val payments: BigDecimal, val returns: BigDecimal)

/** Snapshot activity totals only, never a reconstructed debt/balance. Requests are unposted. */
internal fun statementTotals(entries: List<StoreStatementEntry>): List<StatementCurrencyTotals> =
    entries.groupBy { it.currency }.toSortedMap().map { (currency, rows) ->
        fun total(kind: StatementKind) = rows.filter { it.kind == kind }.fold(BigDecimal.ZERO) { sum, row -> sum + row.amount }
        StatementCurrencyTotals(currency, total(StatementKind.Order), total(StatementKind.Payment), total(StatementKind.Return))
    }

internal fun statementMonths(entries: List<StoreStatementEntry>): List<StatementMonth> {
    val zone = ZoneId.of("Asia/Tashkent")
    val ru = Locale.forLanguageTag("ru")
    val format = DateTimeFormatter.ofPattern("LLLL yyyy", ru)
    return entries.sortedWith(compareByDescending<StoreStatementEntry> { it.occurredAt }.thenBy { it.id })
        .groupBy { YearMonth.from(Instant.ofEpochMilli(it.occurredAt).atZone(zone)) }
        .map { (month, rows) -> StatementMonth(month.toString(), month.format(format).replaceFirstChar { it.titlecase(ru) }, rows) }
}
