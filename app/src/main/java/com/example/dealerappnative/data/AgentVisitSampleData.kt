package com.example.dealerappnative.data

/** Original reference identities are retained; only the visit list ordering is mixed. */
private val originalAgentVisitStores = listOf(
    sampleStores[0].copy(id = "456123", name = "Хусанов Акбар", address = "Чорсу 21Б, Ташкент"),
    sampleStores[1].copy(id = "789456", name = "Тохиров Алишер", address = "Мирзо-Улугбек 3А, Ташкент"),
    sampleStores[2].copy(id = "123789", name = "Усмонов Хусан", address = "Шайхонтохур 45, Ташкент"),
    sampleStores[3].copy(id = "321654", name = "Умаров Хасан", address = "улица Фароби, Ташкент")
)

private val allAgentVisitCards = originalAgentVisitStores.mapIndexed { index, store ->
    activeVisits[index % 3].copy(id = store.id, name = store.name, debt = store.debt,
        debtDirection = store.debtDirection, distance = store.distance, address = store.address)
} + additionalActiveVisits.map { it.copy(id = "agent-${it.id}") } + pendingAgentVisitCards + mixedAgentVisitCards

// Alternate finished and untouched cards, so the first screen already shows both.
internal val agentVisitCards = buildList {
    val finished = allAgentVisitCards.filter { it.results.isNotEmpty() }
    val untouched = allAgentVisitCards.filter { it.results.isEmpty() && it.id != demoActiveAgentVisitId }
    for (index in 0 until maxOf(finished.size, untouched.size)) {
        finished.getOrNull(index)?.let { add(it) }
        untouched.getOrNull(index)?.let { add(it) }
    }
    add(2, allAgentVisitCards.first { it.id == demoActiveAgentVisitId })
}

// Derive the details from those same identities and amounts to prevent mismatches.
internal val agentVisitStores = agentVisitCards.map { visit ->
    val original = originalAgentVisitStores.find { it.id == visit.id }
    val point = sampleVisitLocation(visit.id, visit.address)
    original ?: sampleStores[0].copy(id = visit.id, name = visit.name, debt = visit.debt,
        debtDirection = visit.debtDirection, distance = visit.distance, address = visit.address,
        assignedRole = visit.details.employee, currency = "UZS",
        latitude = point.latitude, longitude = point.longitude)
}
