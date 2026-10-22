package com.example.dealerappnative.data

import com.example.dealerappnative.model.AgentHistoryEntry
import com.example.dealerappnative.model.VisitResult
import com.example.dealerappnative.model.VisitStore

internal data class HistorySalesStore(val visit: VisitStore, val target: Long,
    val documents: List<Pair<AgentHistoryEntry, HistoryOperation>>) {
    val received: Long get() = documents.filter { it.second.result == VisitResult.Request }.sumOf { it.second.amount }
    val returned: Long get() = documents.filter { it.second.result == VisitResult.Return }.sumOf { it.second.amount }
    val net: Long get() = received - returned
}

/** Sales and returns use the very same documents shown in visit details. No extra completed work is invented. */
internal fun historySalesStores(month: HistoryMonth, entries: List<AgentHistoryEntry>): List<HistorySalesStore> {
    val documents = entries.filter { !it.session.isActive }
        .flatMap { entry -> historyOperations(entry).filter {
            (it.result == VisitResult.Request || it.result == VisitResult.Return) &&
                HistoryMonth.at(if (it.result == VisitResult.Return) entry.returnRecord?.createdAt ?: entry.session.startedAt else entry.session.startedAt) == month
        }.map { entry to it } }.groupBy { it.first.visit.id }
    return historyStorePlans(HistorySection.RequestPlan, month, entries).map { plan ->
        HistorySalesStore(plan.visit, plan.target, documents[plan.visit.id].orEmpty()
            .sortedByDescending { it.first.session.finishedAt })
    }
}
