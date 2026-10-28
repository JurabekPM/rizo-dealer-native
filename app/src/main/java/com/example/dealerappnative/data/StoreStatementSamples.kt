package com.example.dealerappnative.data

import com.example.dealerappnative.model.*
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId

/** Per-store visual fixtures, never persisted and never used to recalculate a store balance. */
internal fun storeStatementSamples(store: StoreUiModel, now: Long): List<StoreStatementEntry> {
    val seed = store.id.hashCode().toLong().let { if (it < 0) -it else it }
    val currency = store.currency.takeIf { it in listOf("USD", "UZS") } ?: "UZS"
    val today = Instant.ofEpochMilli(now).atZone(ZoneId.of("Asia/Tashkent")).toLocalDate()
    val kinds = listOf(StatementKind.Payment, StatementKind.Order, StatementKind.Return, StatementKind.Payment,
        StatementKind.Order, StatementKind.Payment, StatementKind.Order, StatementKind.Return, StatementKind.Order, StatementKind.Payment)
    val days = listOf(1L, 3L, 7L, 12L, 20L, 28L, 37L, 45L, 60L, 75L)
    return kinds.mapIndexed { index, kind ->
        val value = if (currency == "UZS") BigDecimal.valueOf((seed % 12 + 8) * 100_000 + index * 65_000L)
            else BigDecimal.valueOf(50 + seed % 160 + index * 17).add(if (index % 3 == 1) BigDecimal("0.5") else BigDecimal.ZERO)
        val time = today.minusDays(days[index]).atTime(10 + index % 6, ((seed + index * 7) % 60).toInt())
            .atZone(ZoneId.of("Asia/Tashkent")).toInstant().toEpochMilli()
        StoreStatementEntry("sample-${store.id}-$index", store.id, kind, (5000 + seed % 3000 + index * 23).toString(),
            time, value, currency, if (index % 3 == 0) "Наличные" else "Перечисление",
            when (kind) { StatementKind.Order -> "Выполнен"; StatementKind.Payment -> "Принята"; else -> "Оформлен" },
            lines = if (kind == StatementKind.Payment) emptyList() else listOf(StatementLine(
                if (kind == StatementKind.Return) "Возвращённый товар EPA" else "Электроинструмент EPA", 1, value)))
    }
}
