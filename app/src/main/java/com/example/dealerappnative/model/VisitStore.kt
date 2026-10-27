package com.example.dealerappnative.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

internal enum class VisitStatus(val label: String, val color: Color) {
    Active("Активный", Color(0xFF2563EB)),
    Attempted("Попытка визита", Color(0xFFB7791F)),
    Completed("Завершено", Color(0xFF5EA783)),
    NotVisited("Новый", Color(0xFF84919A))
}

@Immutable
internal data class VisitStore(
    val id: String,
    val name: String,
    val debt: String,
    val status: VisitStatus,
    val distance: String,
    val address: String,
    val details: VisitDetails,
    val debtDirection: DebtDirection = DebtDirection.StoreOwesDealer,
    val results: List<VisitResult> = emptyList(),
    val sourceStore: StoreUiModel? = null
)

@Immutable
internal data class VisitDetails(
    val date: String,
    val employee: String,
    val deliveries: String,
    val request: String,
    val revisions: String,
    val collection: String,
    val returns: String,
    val employeePhone: String = referenceContactPhone
)
