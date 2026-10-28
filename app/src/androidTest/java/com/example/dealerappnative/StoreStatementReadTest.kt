package com.example.dealerappnative

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.*
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal
import java.util.UUID

/** Real Android JSON/preferences reader, isolated UUID namespaces only; no UI or user data. */
@RunWith(AndroidJUnit4::class)
class StoreStatementReadTest {
    private val context = IsolatedPreferences(InstrumentationRegistry.getInstrumentation().targetContext)
    private val store = StoreUiModel("statement-a", "Same name", "500 USD", DebtDirection.StoreOwesDealer,
        "1 км", "Address A", "", 41.0, 69.0)
    @After fun cleanup() { context.clearOwnPreferences() }

    @Test fun openingEmptyHistoryDoesNotSeedOrWriteAnyPreferences() = runBlocking {
        val entries = loadStoreStatement(context, store)
        assertEquals(10, entries.size)
        assertTrue(context.snapshot().values.all { it.isEmpty() })
    }

    @Test fun localDocumentsResolvePhysicalIdsKeepCurrenciesAndNeverModifyStorage() = runBlocking {
        val now = System.currentTimeMillis()
        val unplanned = "unplanned-statement-test"
        AgentUnplannedVisitStore(context).save(UnplannedVisit(unplanned, store, UnplannedVisitKind.Visit, now))
        VisitRequestStore(context).save(store.id, VisitRequestRecord("RQ-test", now,
            listOf(VisitRequestItem("p1", "Product request", 125_000L, 2))))
        val returns = VisitReturnStore(context)
        returns.save(unplanned, VisitReturnRecord("RN-test", now, listOf(VisitReturnItem("Product return", 55_000L, 3)),
            "Comment", "reason-test", "Reason snapshot"))
        returns.save("statement-b", VisitReturnRecord("RN-other", now, listOf(VisitReturnItem("Other store", 1L, 1))))
        context.getSharedPreferences("agent_visit_sessions", 0).edit().putString(unplanned,
            JSONObject().put("startedAt", now - 60_000).put("collectionAmount", 300_000L)
                .put("results", JSONArray().put(VisitResult.Collection.name)).toString()).commit()
        context.getSharedPreferences("agent_unplanned_visits", 0).edit().putString("broken-test",
            JSONObject().put("storeId", store.id).toString()).commit()
        context.getSharedPreferences("visit_returns", 0).edit().putString("broken-test", "corrupt-json").commit()
        val before = context.snapshot()
        val entries = loadStoreStatement(context, store)
        val local = entries.filter { it.id.startsWith("local-") }
        assertEquals(3, local.size)
        assertTrue(local.all { it.storeId == store.id && it.currency == "UZS" })
        assertEquals(BigDecimal("250000"), local.single { it.kind == StatementKind.Request }.amount)
        val returned = local.single { it.kind == StatementKind.Return }
        assertEquals(BigDecimal("165000"), returned.amount)
        assertEquals("Reason snapshot · Comment", returned.comment)
        val payment = local.single { it.kind == StatementKind.Payment }
        assertEquals("Оплата", payment.title)
        assertEquals("Дата визита", payment.dateLabel)
        assertEquals(now - 60_000, payment.occurredAt)
        assertEquals(setOf("USD", "UZS"), entries.map { it.currency }.toSet())
        val other = loadStoreStatement(context, store.copy(id = "statement-b"))
        assertEquals(listOf("RN-other"), other.filter { it.id.startsWith("local-") }.map { it.document })
        assertEquals(before, context.snapshot())
    }

    private class IsolatedPreferences(base: Context) : ContextWrapper(base) {
        private val prefix = "statement_test_${UUID.randomUUID()}_"
        private val owned = mutableSetOf<String>()
        override fun getApplicationContext(): Context = this
        override fun getSharedPreferences(name: String, mode: Int): SharedPreferences {
            val key = prefix + name
            owned += key
            return super.getSharedPreferences(key, mode)
        }
        fun snapshot(): Map<String, Map<String, *>> = owned.associateWith { super.getSharedPreferences(it, 0).all.toMap() }
        fun clearOwnPreferences() { owned.forEach { baseContext.deleteSharedPreferences(it) } }
    }
}
