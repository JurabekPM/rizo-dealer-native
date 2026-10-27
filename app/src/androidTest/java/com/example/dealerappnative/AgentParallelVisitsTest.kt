package com.example.dealerappnative

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

/** Real session persistence, isolated preferences only; never touches user visits or camera. */
@RunWith(AndroidJUnit4::class)
class AgentParallelVisitsTest {
    private val context = IsolatedPreferences(InstrumentationRegistry.getInstrumentation().targetContext)
    @After fun cleanup() { context.clearOwnPreferences() }

    @Test fun differentStoresStartFinishAndRestoreIndependently() {
        val sessions = AgentVisitSessions(context)
        assertTrue(sessions.start("parallel-a", emptyList()))
        assertTrue(sessions.start("parallel-b", emptyList()))
        val extra = UnplannedVisit("unplanned-test-c", sampleStores.first().copy(id = "parallel-c"),
            UnplannedVisitKind.Visit, System.currentTimeMillis())
        assertTrue(sessions.startUnplanned(extra))
        val startedA = sessions.sessions.getValue("parallel-a").startedAt
        sessions.comment("parallel-a", "Comment A")
        sessions.addPhotos("parallel-a", listOf("content://test/photo-a"))
        sessions.comment("parallel-b", "Comment B")
        assertTrue(sessions.saveCollection("parallel-a", 50_000L))
        assertTrue(sessions.finish("parallel-b", VisitFinishReason.NoOrderNeeded))
        assertTrue(sessions.sessions.getValue("parallel-a").isActive)
        assertTrue(sessions.sessions.getValue(extra.id).isActive)
        assertFalse(sessions.sessions.getValue("parallel-b").isActive)
        assertFalse(sessions.start("parallel-b", emptyList()))
        val restored = AgentVisitSessions(context)
        assertTrue(restored.activeVisitIds.containsAll(setOf("parallel-a", extra.id)))
        assertFalse("parallel-b" in restored.activeVisitIds)
        assertEquals(startedA, restored.sessions.getValue("parallel-a").startedAt)
        assertEquals("Comment A", restored.sessions.getValue("parallel-a").comment)
        assertEquals(listOf("content://test/photo-a"), restored.sessions.getValue("parallel-a").photoUris)
        assertEquals(50_000L, restored.sessions.getValue("parallel-a").collectionAmount)
        assertEquals("Comment B", restored.sessions.getValue("parallel-b").comment)
    }

    @Test fun activePhysicalStoreIsNotDuplicatedWhileOtherStoresRemainAvailable() {
        val sessions = AgentVisitSessions(context)
        val source = sampleStores.first().copy(id = "parallel-source")
        val first = UnplannedVisit("unplanned-first", source, UnplannedVisitKind.Visit, System.currentTimeMillis())
        assertTrue(sessions.startUnplanned(first))
        assertTrue(sessions.start("parallel-other", emptyList()))
        assertFalse(sessions.start(source.id, emptyList()))
        assertFalse(sessions.startUnplanned(first.copy(id = "unplanned-second")))
        assertTrue(sessions.finish(first.id, VisitFinishReason.NoOrderNeeded))
        assertTrue(sessions.startUnplanned(first.copy(id = "unplanned-second")))
        assertTrue(sessions.sessions.getValue("parallel-other").isActive)
        assertTrue(source.id in sessions.activeStoreIds)
        assertFalse(sessions.startUnplanned(first))
    }

    private class IsolatedPreferences(base: Context) : ContextWrapper(base) {
        private val prefix = "parallel_visit_test_${UUID.randomUUID()}_"
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
