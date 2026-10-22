package com.example.dealerappnative.data

import com.example.dealerappnative.model.AgentVisitSession
import com.example.dealerappnative.model.VisitResult
import java.util.Calendar

/** Explicit fictional completed visits. Empty-result cards remain available to start. */
internal fun completedAgentVisitSample(index: Int, results: List<VisitResult>, now: Long,
    previous: AgentVisitSession? = null): AgentVisitSession {
    require(results.isNotEmpty())
    val end = Calendar.getInstance().apply {
        timeInMillis = now
        set(Calendar.HOUR_OF_DAY, 9)
        set(Calendar.MINUTE, 20)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.MINUTE, index * 17)
        if (timeInMillis > now) add(Calendar.DAY_OF_YEAR, -1)
    }.timeInMillis
    val start = previous?.startedAt ?: (end - (12 + index % 5 * 3) * 60_000L)
    val comments = listOf(
        "Обсудили ассортимент. Клиент подтвердил заявку и следующую поставку.",
        "Проверили остатки, согласовали пополнение ходовых товаров.",
        "Товар передан, документы проверены. Замечаний нет.",
        "Провели сверку, согласовали оплату и следующий визит."
    )
    val photos = if (index % 3 == 0) listOf("asset:///visit_samples/store-interior.png", "asset:///visit_samples/visit-product.jpg")
        else listOf("asset:///visit_samples/store-interior.png")
    return AgentVisitSession(startedAt = start,
        finishedAt = previous?.finishedAt ?: if (previous != null) now.coerceAtLeast(start) else end,
        comment = previous?.comment?.takeIf { it.isNotBlank() } ?: comments[index % comments.size],
        photoUris = previous?.photoUris?.takeIf { it.isNotEmpty() } ?: photos,
        results = results)
}
