package com.example.dealerappnative.model

internal const val referenceContactPhone = "+998114305353"

internal data class StoreUiModel(
    val id: String,
    val name: String,
    val debt: String,
    val debtDirection: DebtDirection,
    val distance: String,
    val address: String,
    val assignedRole: String,
    val latitude: Double,
    val longitude: Double,
    val currency: String = "USD",
    val showPrices: Boolean = false,
    // Reference contact number for this UI prototype; ACTION_DIAL never places a call automatically.
    val phoneNumber: String = referenceContactPhone
)
