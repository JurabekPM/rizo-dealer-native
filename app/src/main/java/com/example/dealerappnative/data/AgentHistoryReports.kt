package com.example.dealerappnative.data

import com.example.dealerappnative.model.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

internal enum class HistorySection(val title: String, val result: VisitResult?) {
    CollectionPlan("История инкассации", VisitResult.Collection),
    Revisions("Ревизия", VisitResult.Revision),
    RequestPlan("План заявки", VisitResult.Request),
    Visits("История визитов", null),
    Returns("Возвраты", VisitResult.Return),
    Sales("История продаж", VisitResult.Request)
}

internal data class HistoryProduct(val name: String, val price: Long, val quantity: Int, val expected: Int = quantity) {
    val total: Long get() = price * quantity
}

internal data class HistoryOperation(val result: VisitResult, val documentId: String,
    val products: List<HistoryProduct>, val amount: Long) {
    val title: String get() = when (result) {
        VisitResult.Delivery -> "Доставка"
        VisitResult.Request -> "Заявка"
        VisitResult.Revision -> "Ревизия"
        VisitResult.Collection -> "Инкассация"
        VisitResult.Return -> "Возврат"
    }
}

/** Fictional document lines are stable per visit ID; only recorded session results create documents. */
internal fun historyOperations(entry: AgentHistoryEntry): List<HistoryOperation> =
    if (entry.session.isActive) emptyList() else entry.session.results.distinct().map { result ->
    if (result == VisitResult.Collection && entry.session.collectionAmount != null) {
        return@map HistoryOperation(result, "IC-${entry.session.startedAt}", emptyList(), entry.session.collectionAmount)
    }
    if (result == VisitResult.Request && entry.requestRecord != null) {
        val record = entry.requestRecord
        return@map HistoryOperation(result, record.documentId,
            record.items.map { HistoryProduct(it.name, it.price, it.quantity) }, record.total)
    }
    if (result == VisitResult.Return && entry.returnRecord != null) {
        val record = entry.returnRecord
        return@map HistoryOperation(result, record.documentId,
            record.items.map { HistoryProduct(it.name, it.price, it.quantity) }, record.total)
    }
    val seed = (entry.visit.id.hashCode().toLong() and 0x7fffffff) + result.ordinal * 31
    val names = listOf("Дрель-шуруповёрт Bosch GSR 120-LI", "Перфоратор DeWalt D25134K", "Аккумуляторная пила Makita CXT",
        "Угловая шлифмашина Bosch GWS 750", "Аккумулятор Makita 12V", "Набор свёрл Bosch X-Line")
    val sourceDescription = when (result) {
        VisitResult.Request -> entry.visit.details.request
        VisitResult.Return -> entry.visit.details.returns
        else -> ""
    }
    val count = Regex("\\d+").find(sourceDescription)?.value?.toIntOrNull()?.coerceIn(1, names.size)
        ?: if (result == VisitResult.Return) 1 else 3
    val products = if (result == VisitResult.Collection) emptyList() else (0 until count).map { index ->
        val expected = 4 + ((seed + index) % 7).toInt()
        HistoryProduct(names[index], 350_000L + (seed + index) % 5 * 75_000L,
            if (result == VisitResult.Revision) (expected + listOf(-2, 1, 0)[index]).coerceAtLeast(0)
            else 1 + ((seed + index) % 4).toInt(), expected)
    }
    val amount = if (result == VisitResult.Collection) {
        Regex("\\d[\\d\\s]*").find(entry.visit.details.collection)?.value?.filter(Char::isDigit)?.toLongOrNull()
            ?: (200_000L + seed % 9 * 50_000L)
    } else products.sumOf { it.total }
    HistoryOperation(result, (10_000_000L + seed % 80_000_000L).toString(), products, amount)
}

internal data class HistoryMonth(val year: Int, val month: Int) {
    val key: String get() = "%04d-%02d".format(year, month)
    val start: Long get() = StatisticsDates.day(year, month, 1)
    val label: String get() = SimpleDateFormat("LLLL yyyy", Locale.forLanguageTag("ru")).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }.format(start).replaceFirstChar { it.titlecase(Locale.forLanguageTag("ru")) }

    companion object {
        fun at(timestamp: Long): HistoryMonth = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tashkent")).run {
            timeInMillis = timestamp; HistoryMonth(get(Calendar.YEAR), get(Calendar.MONTH) + 1)
        }
    }
}

internal fun historyPlanMonths(entries: List<AgentHistoryEntry>, now: Long = System.currentTimeMillis()): List<HistoryMonth> {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tashkent")).apply { timeInMillis = now }
    val recent = (0..2).map { index ->
        if (index > 0) calendar.add(Calendar.MONTH, -1)
        HistoryMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)
    }
    return (recent + entries.map { HistoryMonth.at(it.session.startedAt) }).distinct().sortedByDescending { it.key }
}

internal data class HistoryStorePlan(val visit: VisitStore, val target: Long, val operations: List<Pair<AgentHistoryEntry, HistoryOperation>>) {
    val received: Long get() = operations.sumOf { it.second.amount }
}

/** Monthly targets are demo plans; actual totals always come from the same finished visit documents. */
internal fun historyStorePlans(section: HistorySection, month: HistoryMonth, entries: List<AgentHistoryEntry>): List<HistoryStorePlan> =
    (agentVisitCards + entries.map { entry ->
        entry.visit.sourceStore?.let { entry.visit.copy(id = it.id) } ?: entry.visit
    }).distinctBy { it.id }.map { visit ->
        val target = (if (section == HistorySection.CollectionPlan) 800_000L else 2_000_000L) +
            (visit.id.hashCode().toLong() and 0x7fffffff) % 7 * 250_000L
        val operations = entries.filter { (it.visit.sourceStore?.id ?: it.visit.id) == visit.id && HistoryMonth.at(it.session.startedAt) == month }
            .flatMap { entry -> historyOperations(entry).filter { it.result == section.result }.map { entry to it } }
        HistoryStorePlan(visit, target, operations)
    }

internal fun historyMoney(value: Long): String = NumberFormat.getIntegerInstance(Locale.forLanguageTag("ru")).format(value) + " UZS"
internal fun historyDate(value: Long): String = SimpleDateFormat("dd.MM.yyyy | HH:mm", Locale.forLanguageTag("ru")).apply {
    timeZone = TimeZone.getTimeZone("Asia/Tashkent")
}.format(value)
