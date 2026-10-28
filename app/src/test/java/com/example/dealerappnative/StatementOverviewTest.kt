package com.example.dealerappnative

import com.example.dealerappnative.model.*
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant

class StatementOverviewTest {
    private fun entry(id: String, kind: StatementKind, amount: String, currency: String = "USD",
        time: String = "2026-10-28T07:00:00Z") = StoreStatementEntry(id, "a", kind, id,
        Instant.parse(time).toEpochMilli(), BigDecimal(amount), currency, "Наличные", "Завершено")

    @Test fun activityTotalsKeepCurrenciesExactAndRequestsUnposted() {
        val totals = statementTotals(listOf(entry("o1", StatementKind.Order, "10.1"), entry("o2", StatementKind.Order, "0.2"),
            entry("p", StatementKind.Payment, "5.05"), entry("r", StatementKind.Return, "1.25"),
            entry("request", StatementKind.Request, "999"), entry("uzs", StatementKind.Order, "1250000", "UZS")))
        val usd = totals.single { it.currency == "USD" }
        assertEquals(BigDecimal("10.3"), usd.orders)
        assertEquals(BigDecimal("5.05"), usd.payments)
        assertEquals(BigDecimal("1.25"), usd.returns)
        assertEquals(BigDecimal("1250000"), totals.single { it.currency == "UZS" }.orders)
    }

    @Test fun monthlySectionsUseLocalMonthAndNewestFirstIncludingYearBoundary() {
        val january = entry("new-year", StatementKind.Payment, "1", time = "2026-12-31T19:00:00Z")
        val december = entry("year-end", StatementKind.Order, "2", time = "2026-12-31T18:59:59Z")
        val october = entry("oct", StatementKind.Return, "3", time = "2026-09-30T19:00:00Z")
        val september = entry("sep", StatementKind.Order, "4", time = "2026-09-30T18:59:59Z")
        val months = statementMonths(listOf(september, december, january, october))
        assertEquals(listOf("2027-01", "2026-12", "2026-10", "2026-09"), months.map { it.key })
        assertEquals("Январь 2027", months.first().title)
        assertEquals(listOf(january, december, october, september), months.flatMap { it.entries })
    }

    @Test fun emptyAndFilteredSnapshotsDoNotInventActivity() {
        assertTrue(statementTotals(emptyList()).isEmpty())
        assertTrue(statementMonths(emptyList()).isEmpty())
        val payments = listOf(entry("o", StatementKind.Order, "100"), entry("p", StatementKind.Payment, "20"))
            .filter(StatementFilter(StatementType.Payments)::matches)
        assertEquals(BigDecimal.ZERO, statementTotals(payments).single().orders)
        assertEquals(BigDecimal("20"), statementTotals(payments).single().payments)
    }
}
