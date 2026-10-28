package com.example.dealerappnative

import com.example.dealerappnative.data.sampleDealerPeople
import com.example.dealerappnative.model.*
import org.junit.Assert.*
import org.junit.Test

class DealerPeopleTest {
    @Test fun samplesCoverAllSourceRolesWithUniquePeopleAndOneCurrentAdministrator() {
        val people = sampleDealerPeople(AdminPersonalProfile())
        assertEquals(10, people.size)
        assertEquals(dealerPersonRoles.toSet(), people.map { it.role }.toSet())
        assertEquals(1, people.count { it.isCurrentAdministrator })
        assertNull(dealerPeopleError(people))
    }

    @Test fun editingAndAddingUseIdsAndPreserveEveryOtherPerson() {
        val people = sampleDealerPeople(AdminPersonalProfile())
        val edited = people[2].copy(profile = people[2].profile.copy(name = "Changed staff"), role = "Бухгалтер")
        val updated = upsertDealerPerson(people, edited)
        assertEquals(people.filter { it.id != edited.id }, updated.filter { it.id != edited.id })
        assertEquals(edited, updated[2])
        val added = DealerPerson("new-id", AdminPersonalProfile(name = "New staff", phone = "+998991234567"), "Торговый агент")
        assertEquals(11, upsertDealerPerson(updated, added).size)
        assertEquals(people, sampleDealerPeople(AdminPersonalProfile()))
    }

    @Test fun duplicateIdsPhonesAndMissingCurrentAdministratorAreRejected() {
        val people = sampleDealerPeople(AdminPersonalProfile())
        assertNotNull(dealerPeopleError(people + people.last()))
        assertNotNull(dealerPeopleError(people + people.last().copy(id = "new")))
        assertNotNull(dealerPeopleError(people.drop(1)))
        assertNotNull(dealerPeopleError(people.map { if (it.isCurrentAdministrator) it.copy(role = "Кассир") else it }))
        assertNotNull(dealerPeopleError(people.mapIndexed { i, p -> if (i == 1) p.copy(role = "unknown") else p }))
    }
}
