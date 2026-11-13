package com.example.dealerappnative

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.dealerappnative.data.AdminUnreadContent
import com.example.dealerappnative.navigation.DealerDestination
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
class AdminUnreadContentTest {
    @Test
    fun readItemsStayReadAndCountsAreIndependentAcrossSections() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "admin_unread_test_${UUID.randomUUID()}"
        val preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        val visits = DealerDestination.Visits
        val requests = DealerDestination.Requests
        val fixtures = mapOf(visits to setOf("1", "2"), requests to setOf("1"))

        try {
            val content = AdminUnreadContent(preferences, fixtures)
            assertEquals(setOf("1", "2"), content[visits])
            content.markRead(visits, "1")
            content.markRead(visits, "1")
            content.markRead(visits, "unknown")
            assertEquals(setOf("2"), content[visits])
            assertEquals(setOf("1"), content[requests])

            val restored = AdminUnreadContent(preferences, fixtures + (visits to setOf("1", "2", "3")))
            assertEquals(setOf("2", "3"), restored[visits])
            assertEquals(setOf("1"), restored[requests])
            assertEquals(emptySet<String>(), restored[DealerDestination.Menu])
        } finally {
            context.deleteSharedPreferences(name)
        }
    }
}
