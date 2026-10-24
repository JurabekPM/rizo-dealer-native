package com.example.dealerappnative.model

/** History contains finished sessions only; missing operations do not mean partial completion. */
internal enum class HistoryStatus(val label: String) {
    Completed("Завершено"), Partial("Частично завершено"), Cancelled("Отменено")
}

internal enum class HistoryVisitType(val label: String) {
    Goods("Товары"), Request("Заявка"), Collection("Инкассация");

    fun matches(results: List<VisitResult>): Boolean = when (this) {
        Goods -> results.any { it == VisitResult.Delivery || it == VisitResult.Revision || it == VisitResult.Return }
        Request -> VisitResult.Request in results
        Collection -> VisitResult.Collection in results
    }
}

internal data class AgentHistoryEntry(val visit: VisitStore, val session: AgentVisitSession, val unplanned: Boolean = false, val returnRecord: VisitReturnRecord? = null, val requestRecord: VisitRequestRecord? = null) {
    val status: HistoryStatus get() = if (session.isAttempt) HistoryStatus.Cancelled else HistoryStatus.Completed
}

internal data class AgentHistoryFilter(
    val storeId: String? = null,
    val type: HistoryVisitType? = null,
    val status: HistoryStatus? = null,
    val from: Long? = null,
    val to: Long? = null,
    val unplannedOnly: Boolean = false
) {
    init { require((from == null && to == null) || (from != null && to != null && to >= from)) }
    val active: Boolean get() = this != AgentHistoryFilter()

    fun matches(entry: AgentHistoryEntry): Boolean {
        if (entry.session.isActive) return false
        val day = StatisticsDates.today(entry.session.startedAt)
        return (storeId == null || storeId == entry.visit.id) &&
            (type == null || type.matches(entry.session.results)) &&
            (status == null || status == entry.status) &&
            (from == null || day >= from) && (to == null || day <= to) &&
            (!unplannedOnly || entry.unplanned)
    }
}
