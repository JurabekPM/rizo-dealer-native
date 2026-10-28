package com.example.dealerappnative.model

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

internal enum class StatementKind(val label: String, val direction: Int) {
    Order("Заказ", -1), Payment("Оплата", 1), Return("Возврат", 1), Request("Заявка", 0)
}

internal data class StatementLine(val name: String, val quantity: Int, val price: BigDecimal) {
    init { require(name.isNotBlank() && quantity > 0 && price.signum() >= 0) }
}

/** Display history only. Requests are not posted debt; no running/account balance is inferred. */
internal data class StoreStatementEntry(val id: String, val storeId: String, val kind: StatementKind,
    val document: String, val occurredAt: Long, val amount: BigDecimal, val currency: String,
    val method: String, val status: String, val lines: List<StatementLine> = emptyList(),
    val comment: String = "", val dateLabel: String = "Дата операции") {
    init { require(id.isNotBlank() && storeId.isNotBlank() && occurredAt > 0 && amount.signum() >= 0 && currency in listOf("USD", "UZS")) }
    val title: String get() = kind.label + if (document.isBlank()) "" else " № $document"
}

internal enum class StatementType(val label: String) {
    All("Все операции"), Orders("Заказы и заявки"), Payments("Оплаты"), Returns("Возвраты");
    fun matches(kind: StatementKind): Boolean = when (this) {
        All -> true; Orders -> kind == StatementKind.Order || kind == StatementKind.Request
        Payments -> kind == StatementKind.Payment; Returns -> kind == StatementKind.Return
    }
}

internal data class StatementFilter(val types: Set<StatementType> = emptySet(), val currency: String? = null,
    val from: Long? = null, val to: Long? = null) {
    // Retain the internal single-type construction used by existing tests/callers.
    constructor(type: StatementType, currency: String? = null, from: Long? = null, to: Long? = null) :
        this(if (type == StatementType.All) emptySet() else setOf(type), currency, from, to)
    init {
        require(StatementType.All !in types)
        require((from == null && to == null) || (from != null && to != null && to >= from))
    }
    val active: Boolean get() = types.isNotEmpty() || currency != null || from != null
    fun toggleType(type: StatementType): StatementFilter = copy(types = when {
        type == StatementType.All -> emptySet()
        type in types -> types - type
        else -> types + type
    })
    fun matches(entry: StoreStatementEntry): Boolean {
        val day = StatisticsDates.today(entry.occurredAt)
        return (types.isEmpty() || types.any { it.matches(entry.kind) }) && (currency == null || currency == entry.currency) &&
            (from == null || day >= from) && (to == null || day <= to)
    }
}

internal fun statementMoney(amount: BigDecimal, currency: String): String =
    DecimalFormat("#,##0.######", DecimalFormatSymbols(Locale.forLanguageTag("ru"))).format(amount).replace('\u00a0', ' ') + " $currency"

internal fun statementAmount(entry: StoreStatementEntry): String {
    val prefix = if (entry.kind.direction < 0 && entry.amount.signum() != 0) "− " else ""
    return prefix + statementMoney(entry.amount, entry.currency)
}

/** Repeated names never link records. Only explicit unplanned -> physical store IDs do. */
internal fun statementEntriesForStore(storeId: String, entries: List<StoreStatementEntry>, physicalLinks: Map<String, String>): List<StoreStatementEntry> =
    entries.mapNotNull { entry ->
        val physicalId = physicalLinks[entry.storeId] ?: entry.storeId
        if (physicalId == storeId) entry.copy(storeId = physicalId) else null
    }.distinctBy { it.id }.sortedWith(compareByDescending<StoreStatementEntry> { it.occurredAt }.thenBy { it.id })
