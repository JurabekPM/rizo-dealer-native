package com.example.dealerappnative

import com.example.dealerappnative.ui.components.PhotoDismissMotion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PhotoDismissMotionTest {
    @Test fun fasterReleasesFinishSoonerInBothDirections() {
        val slow = PhotoDismissMotion(300f, density = 2f)
        val downward = PhotoDismissMotion(3_000f, density = 2f)
        val upward = PhotoDismissMotion(-3_000f, density = 2f)

        assertTrue(downward.durationMillis < slow.durationMillis)
        assertEquals(downward.durationMillis, upward.durationMillis)
        assertEquals(downward.verticalOffset(.15f), -upward.verticalOffset(.15f), .001f)
    }

    @Test fun motionStartsAtFingerSpeedAndSettlesAtTheExactThumbnail() {
        val velocity = -3_000f
        val motion = PhotoDismissMotion(velocity, density = 2f)
        val duration = motion.durationMillis / 1_000f
        val step = .001f

        assertEquals(velocity, motion.verticalOffset(step) / (step * duration), 10f)
        assertEquals(0f, motion.verticalOffset(1f), 0f)
        assertEquals(0f, motion.verticalOffset(1f - step) / (step * duration), .01f)
        assertEquals(1f, motion.progress(1f), 0f)
        assertEquals(0f, (1f - motion.progress(1f - step)) / step, .004f)
    }
}
