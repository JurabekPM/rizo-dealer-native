package com.example.dealerappnative.model

internal data class ReturnReason(val id: String, val label: String, val system: Boolean, val enabled: Boolean = true)

internal val systemReturnReasons = listOf(
    ReturnReason("system-defect", "Брак или повреждение товара", true),
    ReturnReason("system-order", "Товар не соответствует заказу", true),
    ReturnReason("system-mistake", "Ошибка при заказе", true),
    ReturnReason("system-excess", "Излишки товара", true),
    ReturnReason("system-package", "Повреждена упаковка", true)
)

internal const val otherReturnReasonId = "other"

internal fun returnReasonError(value: String, reasons: List<ReturnReason>, editingId: String? = null): String? = when {
    value.trim().length < 2 -> "Введите не менее 2 символов"
    value.trim().length > 100 -> "Не более 100 символов"
    reasons.any { it.id != editingId && it.label.trim().equals(value.trim(), ignoreCase = true) } -> "Такая причина уже есть"
    value.trim().equals("Другое", ignoreCase = true) -> "Для своего комментария уже есть «Другое»"
    else -> null
}

/** Disabled reasons cannot be used for a new return. The comment remains optional for a standard reason. */
internal fun canConfirmReturnReason(id: String?, comment: String, reasons: List<ReturnReason>): Boolean =
    if (id == otherReturnReasonId) comment.isNotBlank() else reasons.any { it.id == id && it.enabled }
