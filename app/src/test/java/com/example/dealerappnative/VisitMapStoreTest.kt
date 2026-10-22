package com.example.dealerappnative

import com.example.dealerappnative.data.*
import org.junit.Assert.*
import org.junit.Test

class VisitMapStoreTest {
    @Test fun agentAndHistoryMapsKeepTheSelectedVisitIdentity() {
        agentVisitCards.forEach { visit ->
            val store = storeForVisit(visit)
            assertEquals(visit.id, store.id)
            assertEquals(visit.name, store.name)
            assertEquals(visit.address, store.address)
            assertEquals(visit.debt, store.debt)
        }
    }

    @Test fun administratorVisitMapsKeepTheirOwnStore() {
        (activeVisits + pastVisits).forEach { visit ->
            val store = storeForVisit(visit)
            assertEquals(visit.id, store.id)
            assertEquals(visit.name, store.name)
            assertEquals(visit.address, store.address)
            assertTrue(store.latitude.isFinite() && store.latitude in -90.0..90.0)
            assertTrue(store.longitude.isFinite() && store.longitude in -180.0..180.0)
        }
    }

    @Test fun visitOnlySamplesDoNotAllInheritThePosLitePoint() {
        val points = agentVisitStores.map { it.latitude to it.longitude }.toSet()
        assertTrue(points.size > agentVisitStores.size / 2)
        val baghdad = sampleVisitLocation("751", "Ферганская область, Багдадский район")
        val tashkent = sampleVisitLocation("761", "Ташкент, улица Сагбан")
        assertTrue(baghdad.longitude > 71.0)
        assertTrue(tashkent.longitude in 69.0..70.0)
        assertEquals(tashkent, sampleVisitLocation("761", "Ташкент, улица Сагбан"))
    }
}
