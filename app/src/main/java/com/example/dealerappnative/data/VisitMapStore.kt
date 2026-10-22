package com.example.dealerappnative.data

import com.example.dealerappnative.model.StoreLocation
import com.example.dealerappnative.model.StoreUiModel
import com.example.dealerappnative.model.VisitStore

/** Approximate fixture points for visit-only demo stores, not device GPS coordinates. */
internal fun sampleVisitLocation(id: String, address: String): StoreLocation {
    val city = when {
        address.contains("Багдад", ignoreCase = true) -> 40.463 to 71.213
        address.contains("Маргилан", ignoreCase = true) -> 40.472 to 71.724
        address.contains("Фергана", ignoreCase = true) -> 40.386 to 71.787
        address.contains("Ферганск", ignoreCase = true) || address.contains("Коканд", ignoreCase = true) -> 40.529 to 70.943
        else -> 41.31115 to 69.27970
    }
    val seed = id.hashCode().toLong().let { if (it < 0) -it else it }
    return StoreLocation(city.first + ((seed % 61) - 30) * .0003,
        city.second + (((seed / 61) % 61) - 30) * .0003, address)
}

/** Resolve by stable ID, never by a possibly repeated store title. */
internal fun storeForVisit(visit: VisitStore): StoreUiModel {
    val known = agentVisitStores.find { it.id == visit.id }
        ?: sampleStores.find { it.id == visit.id }
    val point = sampleVisitLocation(visit.id, visit.address)
    return known?.copy(name = visit.name, address = visit.address, debt = visit.debt,
        debtDirection = visit.debtDirection) ?: StoreUiModel(
        id = visit.id, name = visit.name, address = visit.address, debt = visit.debt,
        debtDirection = visit.debtDirection, distance = visit.distance,
        assignedRole = visit.details.employee, latitude = point.latitude, longitude = point.longitude,
        currency = "UZS")
}
