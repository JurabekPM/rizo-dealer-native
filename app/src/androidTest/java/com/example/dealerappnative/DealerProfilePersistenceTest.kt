package com.example.dealerappnative

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

/** JSON and durable storage only; never opens the prototype, user images or real preferences. */
@RunWith(AndroidJUnit4::class)
class DealerProfilePersistenceTest {
    private val context = ProfileTestContext(InstrumentationRegistry.getInstrumentation().targetContext)
    @After fun cleanup() { context.clearOwnPreferences() }

    @Test fun profileDraftAndSaveKeepAllFieldsAndLeaveVisitsAlone() = runBlocking {
        val sentinel = context.getSharedPreferences("agent_visit_sessions", Context.MODE_PRIVATE)
        sentinel.edit().putString("session", "keep-original").commit()
        val store = DealerProfilePreferences(context)
        val before = store.read()
        val draft = before.copy(taxId = "12345678901234", name = "Test dealer", interaction = "Подконтрольный",
            activity = "Электроника", currency = "USD", tariff = "Premium", region = "Ташкент", district = "Юнусабад",
            address = "Test address", latitude = 41.32, longitude = 69.28, logoUri = "content://test/logo",
            photoUri = "content://test/photo", qrUri = "content://test/qr", pricesVisible = true,
            days = List(7) { i -> DealerWorkingDay(i < 5, 600 + i, 1200 + i) })
        assertEquals(before, store.read()) // Editing/cancelling has not persisted a draft.
        assertEquals(draft, decodeDealerProfile(encodeDealerProfile(draft)))
        assertTrue(store.save(draft))
        assertEquals(draft, DealerProfilePreferences(context).read())
        assertEquals("keep-original", sentinel.getString("session", null))
        assertFalse(store.save(draft.copy(name = "")))
        assertEquals(draft, store.read())
    }

    @Test fun damagedOrOlderJsonHasSafeDefaultsAndAFullWeek() {
        assertEquals(DealerProfile(), decodeDealerProfile("invalid-json"))
        val older = decodeDealerProfile("{\"name\":\"Old dealer\",\"days\":[{\"enabled\":false,\"opensAt\":99999}]}")
        assertEquals("Old dealer", older.name)
        assertEquals(7, older.days.size)
        assertFalse(older.days.first().enabled)
        assertEquals(1439, older.days.first().opensAt)
        assertEquals("UZS", older.currency)
        assertEquals("", older.photoUri)
    }

    private class ProfileTestContext(base: Context) : ContextWrapper(base) {
        private val prefix = "dealer_profile_test_${UUID.randomUUID()}_"
        private val owned = mutableSetOf<String>()
        override fun getApplicationContext(): Context = this
        override fun getSharedPreferences(name: String, mode: Int): SharedPreferences {
            val key = prefix + name
            owned += key
            return super.getSharedPreferences(key, mode)
        }
        fun clearOwnPreferences() { owned.forEach { baseContext.deleteSharedPreferences(it) } }
    }
}
