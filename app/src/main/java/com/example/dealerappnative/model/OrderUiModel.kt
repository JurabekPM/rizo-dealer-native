package com.example.dealerappnative.model

/** Display values shared by request and delivery cards. No backend model is needed. */
internal data class OrderUiModel(
    val id: String,
    val storeName: String,
    val customerName: String,
    val amount: String,
    val productCount: Int,
    val date: String,
    val source: String
)
