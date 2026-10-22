package com.example.dealerappnative.model

internal data class StoreLocation(
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val route: StoreRouteSummary? = null
)
