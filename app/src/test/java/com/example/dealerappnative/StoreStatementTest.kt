package com.example.dealerappnative

import com.example.dealerappnative.data.storeStatementSamples
import com.example.dealerappnative.model.*
import org.junit.Assert.*
import org.junit.Test
import java.math.BigDecimal
import java.time.Instant

class StoreStatementTest {
    private fun entry(id: String, store: String = "a", kind: StatementKind = StatementKind.Payment,
        time: Long = Instant.parse("2026-10-27T12:00:00Z").toEpochMilli(), currency: String = "USD") =
        StoreStatementEntry(id, store, kind, "5046", time, BigDecimal("233.05"), currency, "Наличные", "Принята")

    @Test fun moneyDirectionAndUnpostedRequestsStayDistinct() {
        assertEquals("− 233,05 USD", statementAmount(entry("1", kind = StatementKind.Order)))
        assertEquals("+ 233,05 USD", statementAmount(entry("2")))
        assertEquals("+ 233,05 USD", statementAmount(entry("3", kind = StatementKind.Return)))
        assertEquals("233,05 USD", statementAmount(entry("4", kind = StatementKind.Request)))
        assertEquals("1 250 000 UZS", statementMoney(BigDecimal("1250000"), "UZS"))
        assertEquals("Оплата № 5046", entry("5").title)
        assertFalse(entry("6").title.contains("Rustam"))
    }

    @Test fun dateTypeAndCurrencyFilterUsesTashkentInclusiveDays() {
        val day = StatisticsDates.day(2026, 10, 28)
        val filter = StatementFilter(StatementType.Payments, "USD", day, day)
        assertTrue(filter.matches(entry("first", time = Instant.parse("2026-10-27T19:00:00Z").toEpochMilli())))
        assertTrue(filter.matches(entry("last", time = Instant.parse("2026-10-28T18:59:59Z").toEpochMilli())))
        assertFalse(filter.matches(entry("before", time = Instant.parse("2026-10-27T18:59:59Z").toEpochMilli())))
        assertFalse(filter.matches(entry("after", time = Instant.parse("2026-10-28T19:00:00Z").toEpochMilli())))
        assertFalse(filter.matches(entry("other-money", currency = "UZS")))
        assertFalse(filter.matches(entry("return", kind = StatementKind.Return)))
        assertTrue(StatementFilter(StatementType.Orders).matches(entry("request", kind = StatementKind.Request)))
    }

    @Test fun explicitStoreLinksIncludeRepeatedVisitsAndExcludeOtherStores() {
        val old = entry("old", time = 1000)
        val first = entry("first", "unplanned-1", time = 2000)
        val second = entry("second", "unplanned-2", time = 3000)
        val unrelated = entry("unrelated", "b", time = 4000)
        val actual = statementEntriesForStore("a", listOf(old, first, second, unrelated, first),
            mapOf("unplanned-1" to "a", "unplanned-2" to "a"))
        assertEquals(listOf("second", "first", "old"), actual.map { it.id })
        assertTrue(actual.all { it.storeId == "a" })
        assertTrue(statementEntriesForStore("unknown", listOf(old), emptyMap()).isEmpty())
    }

    @Test fun samplesBelongToPhysicalStoreAndDoNotDeriveFromCurrentDebt() {
        val store = StoreUiModel("a", "Одинаковое название", "500 USD", DebtDirection.StoreOwesDealer,
            "1 км", "Манзил", "", 41.0, 69.0)
        val now = Instant.parse("2026-10-28T07:00:00Z").toEpochMilli()
        val entries = storeStatementSamples(store, now)
        assertEquals(10, entries.size)
        assertEquals(entries, storeStatementSamples(store.copy(debt = "9000 USD"), now))
        assertTrue(entries.all { it.storeId == store.id && it.currency == "USD" && it.occurredAt < now })
        assertEquals(setOf(StatementKind.Order, StatementKind.Return, StatementKind.Payment), entries.map { it.kind }.toSet())
        assertTrue(entries.map { it.id }.intersect(storeStatementSamples(store.copy(id = "b"), now).map { it.id }.toSet()).isEmpty())
    }
}
