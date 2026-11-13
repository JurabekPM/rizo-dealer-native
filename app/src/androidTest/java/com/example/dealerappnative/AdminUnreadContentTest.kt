package com.example.dealerappnative

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.dealerappnative.data.UnreadContent
import com.example.dealerappnative.navigation.AgentDestination
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
        val agentPreferences = context.getSharedPreferences("${name}_agent", Context.MODE_PRIVATE)
        val visits = DealerDestination.Visits
        val requests = DealerDestination.Requests
        val fixtures = mapOf(visits to setOf("1", "2"), requests to setOf("1"))

        try {
            val content = UnreadContent(preferences, fixtures)
            assertEquals(setOf("1", "2"), content[visits])
            content.markRead(visits, "1")
            content.markRead(visits, "1")
            content.markRead(visits, "unknown")
            assertEquals(setOf("2"), content[visits])
            assertEquals(setOf("1"), content[requests])

            val restored = UnreadContent(preferences, fixtures + (visits to setOf("1", "2", "3")))
            assertEquals(setOf("2", "3"), restored[visits])
            assertEquals(setOf("1"), restored[requests])
            assertEquals(emptySet<String>(), restored[DealerDestination.Menu])

            val agentFixtures = mapOf(AgentDestination.Visits to setOf("1", "2"))
            val agent = UnreadContent(agentPreferences, agentFixtures)
            assertEquals(setOf("1", "2"), agent[AgentDestination.Visits])
            agent.markRead(AgentDestination.Visits, "2")
            assertEquals(setOf("1"), UnreadContent(agentPreferences, agentFixtures)[AgentDestination.Visits])
            assertEquals(setOf("2"), UnreadContent(preferences, fixtures)[visits])
        } finally {
            context.deleteSharedPreferences(name)
            context.deleteSharedPreferences("${name}_agent")
        }
    }
}
