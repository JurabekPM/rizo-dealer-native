package com.example.dealerappnative

import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.*
import java.math.BigDecimal
import org.junit.Assert.*
import org.junit.Test

class RevisionMonthBalancesTest {
    private val month = HistoryMonth(2026, 10)
    private fun entry(id: String, amount: String, direction: DebtDirection = DebtDirection.StoreOwesDealer,
        day: Int = 2, results: List<VisitResult> = listOf(VisitResult.Revision)): AgentHistoryEntry {
        val time = StatisticsDates.day(2026, 10, day)
        return AgentHistoryEntry(agentVisitCards.first().copy(id = id, debt = amount, debtDirection = direction),
            AgentVisitSession(time, time + 60_000L, results = results))
    }

    @Test fun onlyLatestRevisionBalancePerStoreIsCounted() {
        val balances = historyRevisionBalances(month, listOf(entry("a", "1 000 UZS"), entry("a", "600 UZS", day = 4),
            entry("b", "100 UZS", DebtDirection.DealerOwesStore)))
        assertEquals(0, balances.single().amount.compareTo(BigDecimal("-500")))
    }

    @Test fun currenciesAndFractionalBalancesStaySeparate() {
        val balances = historyRevisionBalances(month, listOf(entry("a", "1 000 UZS"),
            entry("b", "9 316,5 USD"), entry("c", "320 USD", DebtDirection.DealerOwesStore)))
        assertEquals(listOf("UZS", "USD"), balances.map { it.currency })
        assertEquals(0, balances[1].amount.compareTo(BigDecimal("-8996.5")))
        assertTrue(balances[1].label.startsWith("− "))
    }

    @Test fun otherMonthsUnfinishedAndNonRevisionVisitsDoNotAffectBalance() {
        val visit = entry("a", "1 000 UZS")
        val active = visit.copy(session = visit.session.copy(finishedAt = null))
        val balances = historyRevisionBalances(month, listOf(active,
            entry("b", "200 UZS", results = listOf(VisitResult.Request))))
        assertEquals(0, balances.single().amount.signum())
        assertEquals(0, historyRevisionBalances(HistoryMonth(2026, 9), listOf(visit)).single().amount.signum())
    }
}
