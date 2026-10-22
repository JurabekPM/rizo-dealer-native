package com.example.dealerappnative

import com.example.dealerappnative.data.completedAgentVisitSample
import com.example.dealerappnative.model.AgentVisitSession
import com.example.dealerappnative.model.VisitFinishReason
import com.example.dealerappnative.model.VisitResult
import org.junit.Assert.*
import org.junit.Test

class AgentVisitSessionTest {
    @Test fun durationStartsAtActualTapAndContinuesAcrossMidnight() {
        val start = 86_395_000L
        val session = AgentVisitSession(startedAt = start)
        assertEquals(0L, session.elapsedMillis(start))
        assertEquals(12_000L, session.elapsedMillis(start + 12_000))
        assertTrue(session.isActive)
    }

    @Test fun notesAndPhotosDoNotCountAsBusinessWork() {
        val session = AgentVisitSession(1000, comment = "Посетил магазин", photoUris = listOf("content://photo/1"))
        assertTrue(session.needsReason)
        assertFalse(session.canFinish(null))
        assertTrue(session.canFinish(VisitFinishReason.NoOrderNeeded))
    }

    @Test fun otherRequiresNonBlankComment() {
        assertFalse(AgentVisitSession(1000, comment = "  ").canFinish(VisitFinishReason.Other))
        assertTrue(AgentVisitSession(1000, comment = "Обсудили ассортимент").canFinish(VisitFinishReason.Other))
    }

    @Test(expected = IllegalArgumentException::class)
    fun cannotFinishWithoutReasonWhenNoWorkExists() {
        AgentVisitSession(1000).finish(5000)
    }

    @Test fun completedWorkCanFinishWithoutReasonAndTimerStops() {
        val finished = AgentVisitSession(1000, results = listOf(VisitResult.Request)).finish(5000)
        assertFalse(finished.isActive)
        assertEquals(4000L, finished.elapsedMillis(90_000))
        assertFalse(finished.canFinish(null))
    }

    @Test fun closingRetainsEvidenceAndDoesNotInventResults() {
        val photos = listOf("content://photo/1")
        val finished = AgentVisitSession(1000, comment = "Закрыто", photoUris = photos)
            .finish(5000, VisitFinishReason.StoreClosed)
        assertTrue(finished.isAttempt)
        assertEquals("Закрыто", finished.comment)
        assertEquals(photos, finished.photoUris)
        assertTrue(finished.results.isEmpty())
    }

    @Test fun clockGoingBackDoesNotProduceNegativeDuration() {
        assertEquals(0L, AgentVisitSession(5000).elapsedMillis(1000))
        assertEquals(5000L, AgentVisitSession(5000).finish(1000, VisitFinishReason.NoOrderNeeded).finishedAt)
    }
    @Test fun resultCardIsCompletedBeforeOpeningAndCannotFinishAgain() {
        val now = 1_792_500_000_000L
        val sample = completedAgentVisitSample(2, listOf(VisitResult.Request), now)
        assertFalse(sample.isActive)
        assertFalse(sample.canFinish(null))
        assertTrue(sample.finishedAt!! <= now)
        assertTrue(sample.elapsedMillis(now) > 0)
        assertTrue(sample.comment.isNotBlank())
        assertTrue(sample.photoUris.isNotEmpty())
    }

    @Test fun completedSamplePreservesExistingTimesCommentAndPhotos() {
        val previous = AgentVisitSession(1000, finishedAt = 5000, comment = "Мой комментарий",
            photoUris = listOf("content://my-photo"), results = listOf(VisitResult.Collection))
        val sample = completedAgentVisitSample(2, previous.results, 10_000, previous)
        assertEquals(previous, sample)
    }

    @Test(expected = IllegalArgumentException::class)
    fun emptyResultCardMustNotBecomeACompletedSample() {
        completedAgentVisitSample(0, emptyList(), 10_000)
    }
}
