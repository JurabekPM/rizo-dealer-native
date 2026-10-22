package com.example.dealerappnative.model

import androidx.compose.runtime.Immutable

internal enum class VisitFinishReason(val label: String) {
    NoOrderNeeded("Заказ не требуется"),
    EnoughStock("Достаточно товара"),
    ClientDeclined("Клиент отказался"),
    ResponsibleAbsent("Нет ответственного"),
    StoreClosed("Магазин закрыт"),
    Other("Другое")
}

/** Actual timestamps belong to this visit, never to the scheduled day's midnight. */
@Immutable
internal data class AgentVisitSession(
    val startedAt: Long,
    val finishedAt: Long? = null,
    val comment: String = "",
    val photoUris: List<String> = emptyList(),
    val results: List<VisitResult> = emptyList(),
    val reason: VisitFinishReason? = null
) {
    val isActive: Boolean get() = finishedAt == null
    val needsReason: Boolean get() = results.isEmpty()
    val isAttempt: Boolean get() = reason == VisitFinishReason.StoreClosed

    fun canFinish(reason: VisitFinishReason?): Boolean = isActive &&
        (!needsReason || reason != null) &&
        (reason != VisitFinishReason.Other || comment.isNotBlank())

    fun finish(now: Long, reason: VisitFinishReason? = null): AgentVisitSession {
        require(canFinish(reason)) { "A visit without work needs a reason; Other needs a comment." }
        return copy(finishedAt = now.coerceAtLeast(startedAt), reason = if (needsReason) reason else null)
    }

    fun elapsedMillis(now: Long): Long = ((finishedAt ?: now) - startedAt).coerceAtLeast(0)
}
