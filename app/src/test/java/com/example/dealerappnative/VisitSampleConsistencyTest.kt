package com.example.dealerappnative

import com.example.dealerappnative.data.*
import com.example.dealerappnative.model.VisitStatus
import org.junit.Assert.*
import org.junit.Test

class VisitSampleConsistencyTest {
    @Test fun adminStatusesMatchResultBadges() {
        val cards = activeVisits + pastVisits
        assertTrue(cards.any { it.status == VisitStatus.Active && it.results.isEmpty() })
        assertTrue(cards.any { it.status == VisitStatus.NotVisited && it.results.isEmpty() })
        cards.filter { it.results.isNotEmpty() }.forEach { assertEquals(VisitStatus.Completed, it.status) }
    }

    @Test fun agentHasMoreCardsMixedAtTheTopAndAllResultCounts() {
        assertEquals(34, agentVisitCards.size)
        assertEquals(agentVisitCards.size, agentVisitCards.map { it.id }.distinct().size)
        assertTrue(agentVisitCards.take(5).any { it.results.isNotEmpty() })
        assertTrue(agentVisitCards.take(5).any { it.results.isEmpty() })
        (0..5).forEach { count -> assertTrue(agentVisitCards.any { it.results.size == count }) }
        assertEquals(demoActiveAgentVisitId, agentVisitCards[2].id)
    }

    @Test fun everyAgentCardResolvesToItsOwnDetails() {
        agentVisitCards.forEach { card ->
            val store = agentVisitStores.single { it.id == card.id }
            assertEquals(card.name, store.name)
            assertEquals(card.debt, store.debt)
        }
    }
}
