package com.example.dealerappnative.data

import com.example.dealerappnative.model.AgentHistoryEntry
import com.example.dealerappnative.model.VisitResult

/** Returns are actual recorded documents in this month, never monthly targets or store debt. */
internal fun historyReturnTotal(month: HistoryMonth, entries: List<AgentHistoryEntry>): Long =
    entries.filter { !it.session.isActive && HistoryMonth.at(it.returnRecord?.createdAt ?: it.session.startedAt) == month }
        .sumOf { entry -> historyOperations(entry).filter { it.result == VisitResult.Return }.sumOf { it.amount } }
