package com.example.dealerappnative

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import androidx.activity.ComponentActivity
import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.sp
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.dealerappnative.ui.components.SlidingNumber
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@RunWith(AndroidJUnit4::class)
class SlidingNumberTest {
    @Test fun unchangedPrefixStaysStillDuringIncreaseAndDecrease() {
        val quantity = mutableLongStateOf(124)
        lateinit var view: ComposeView
        var bounds = IntRect.Zero
        ActivityScenario.launch(ComponentActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                view = ComposeView(activity).apply {
                    setContent {
                        Box {
                            SlidingNumber(quantity.longValue,
                                TextStyle(fontSize = 32.sp, fontFamily = FontFamily.Monospace), Color.Black,
                                Modifier.background(Color.White).onGloballyPositioned {
                                    val rect = it.boundsInRoot()
                                    bounds = IntRect(rect.left.roundToInt(), rect.top.roundToInt(),
                                        rect.right.roundToInt(), rect.bottom.roundToInt())
                                })
                        }
                    }
                }
                activity.setContentView(view)
            }
            val instrumentation = InstrumentationRegistry.getInstrumentation()
            instrumentation.waitForIdleSync()
            SystemClock.sleep(350)
            for (target in listOf(125L, 124L)) {
                var sawIntermediateDigit = false
                scenario.onActivity {
                    assertTrue("Number bounds must be smaller than the root",
                        bounds.width in 1 until view.width && bounds.height in 1 until view.height)
                    quantity.longValue = target
                }
                // Compare animated frames against the settled target and the unchanged prefix.
                val frames = List(12) {
                    SystemClock.sleep(23)
                    capture(scenario, view, bounds)
                }
                SystemClock.sleep(400)
                val settled = capture(scenario, view, bounds)
                val split = settled.width * 3 / 5
                for (frame in frames) {
                    assertEquals(settled.width, frame.width)
                    assertEquals(settled.height, frame.height)
                    for (y in 0 until settled.height) {
                        for (x in 0 until settled.width) {
                            if (settled.getPixel(x, y) == frame.getPixel(x, y)) continue
                            assertTrue("Prefix changed at x=$x, y=$y (limit $split)", x >= split)
                            sawIntermediateDigit = true
                        }
                    }
                    frame.recycle()
                }
                assertTrue("Changed digit produced animation frames", sawIntermediateDigit)
                settled.recycle()
            }
        }
    }

    private fun capture(scenario: ActivityScenario<ComponentActivity>, view: ComposeView, bounds: IntRect): Bitmap {
        val done = CountDownLatch(1)
        val image = Bitmap.createBitmap(bounds.width, bounds.height, Bitmap.Config.ARGB_8888)
        var result = PixelCopy.ERROR_UNKNOWN
        scenario.onActivity { activity ->
            val position = IntArray(2).also(view::getLocationInWindow)
            val source = Rect(bounds.left + position[0], bounds.top + position[1],
                bounds.right + position[0], bounds.bottom + position[1])
            PixelCopy.request(activity.window, source, image, {
                result = it
                done.countDown()
            }, Handler(Looper.getMainLooper()))
        }
        assertTrue("PixelCopy did not finish", done.await(3, TimeUnit.SECONDS))
        assertEquals(PixelCopy.SUCCESS, result)
        return image
    }
}
