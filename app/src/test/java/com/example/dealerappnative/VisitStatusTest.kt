package com.example.dealerappnative

import com.example.dealerappnative.model.*
import org.junit.Assert.*
import org.junit.Test

class VisitStatusTest {
    @Test fun statusFollowsThisVisitsActualLifecycle() {
        assertEquals(VisitStatus.NotVisited, agentVisitStatus(null, emptyList()))
        val active = AgentVisitSession(1_000L)
        assertEquals(VisitStatus.Active, agentVisitStatus(active, emptyList()))
        val finished = active.finish(2_000L, VisitFinishReason.NoOrderNeeded)
        assertEquals(VisitStatus.Completed, agentVisitStatus(finished, emptyList()))
        assertEquals(VisitStatus.Active, agentVisitStatus(active, listOf(VisitResult.Request)))
    }
    @Test fun completedFixtureAndClosedStoreAttemptKeepTheirStatuses() {
        assertEquals(VisitStatus.Completed, agentVisitStatus(null, listOf(VisitResult.Request)))
        val attempted = AgentVisitSession(1_000L).finish(2_000L, VisitFinishReason.StoreClosed)
        assertEquals(VisitStatus.Attempted, agentVisitStatus(attempted, emptyList()))
    }
    @Test fun unstartedAndRunningLabelsUseTheRequestedNames() {
        assertEquals("Новый", VisitStatus.NotVisited.label)
        assertEquals("Активный", VisitStatus.Active.label)
        assertFalse(VisitStatus.entries.any { it.label == "Не посещено" })
    }
}
