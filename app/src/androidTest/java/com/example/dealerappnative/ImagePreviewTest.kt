package com.example.dealerappnative

import android.os.SystemClock
import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inspector.WindowInspector
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.ViewRootForTest
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getAllSemanticsNodes
import androidx.compose.ui.semantics.getOrNull
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import com.example.dealerappnative.data.findVisitStore
import com.example.dealerappnative.data.requestCatalog
import com.example.dealerappnative.model.AgentVisitSession
import com.example.dealerappnative.model.VisitRequestItem
import com.example.dealerappnative.model.VisitRequestRecord
import com.example.dealerappnative.screens.VisitRequestDetailScreen
import com.example.dealerappnative.ui.components.AgentVisitNotes
import com.example.dealerappnative.ui.theme.DealerPrototypeTheme
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/** Render the real entry points in a test activity with packaged photos and synthetic documents. */
@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = 29)
class ImagePreviewTest {
    @Test fun savedRequestThumbnailOpensItsProductPhoto() {
        val product = requestCatalog.first()
        val item = VisitRequestItem(product.id, product.name, product.price, 1)
        val record = VisitRequestRecord("RQ-test", 1_795_000_000_000L, listOf(item))
        verifyPreview(product.name) {
            VisitRequestDetailScreen(checkNotNull(findVisitStore("751")), record, onBack = {})
        }
    }

    @Test fun completedVisitThumbnailOpensItsEvidencePhoto() {
        val session = AgentVisitSession(1L, finishedAt = 2L,
            photoUris = listOf("asset:///visit_samples/store-interior.png"))
        verifyPreview("Фото визита") {
            AgentVisitNotes(session, onComment = {}, onAddPhotos = {}, onRemovePhoto = {})
        }
    }

    @Test fun visitGallerySwipesAndPinchesWithoutClosing() {
        val session = AgentVisitSession(1L, finishedAt = 2L, photoUris = listOf(
            "asset:///visit_samples/store-interior.png",
            "asset:///visit_samples/visit-product.jpg"
        ))
        ActivityScenario.launch(ComponentActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.setContent {
                    DealerPrototypeTheme {
                        AgentVisitNotes(session, onComment = {}, onAddPhotos = {}, onRemovePhoto = {})
                    }
                }
            }
            val label = "Фото визита"
            await(scenario) { all ->
                all.count { it.config.getOrNull(SemanticsProperties.ContentDescription)?.contains("Открыть фото: $label") == true } == 2
            }
            scenario.onActivity { nodes().node("Открыть фото: $label")!!.click() }
            await(scenario) { all -> all.hasText("1 / 2") && all.isZoomed(false) }
            gesture(scenario) { view, bounds ->
                val center = bounds.center
                val radius = bounds.width * .09f
                val x = Offset(radius, 0f)
                val start = SystemClock.uptimeMillis() - 208L
                val initial = listOf(center - x, center + x)
                view.sendTouch(start, 0, MotionEvent.ACTION_DOWN, listOf(initial.first()))
                view.sendTouch(start, 16, MotionEvent.ACTION_POINTER_DOWN or
                    (1 shl MotionEvent.ACTION_POINTER_INDEX_SHIFT), initial)
                for (step in 1..10) {
                    val distance = x * (1f + step * .12f)
                    view.sendTouch(start, 16 + step * 16L, MotionEvent.ACTION_MOVE, listOf(center - distance, center + distance))
                }
                view.sendTouch(start, 192, MotionEvent.ACTION_POINTER_UP or
                    (1 shl MotionEvent.ACTION_POINTER_INDEX_SHIFT), listOf(center - x * 2.2f, center + x * 2.2f))
                view.sendTouch(start, 208, MotionEvent.ACTION_UP, listOf(center - x * 2.2f))
            }
            await(scenario) { all -> all.hasText("1 / 2") && all.isZoomed(true) }
            swipe(scenario)
            await(scenario) { all -> all.hasText("1 / 2") && all.isZoomed(true) }
            scenario.onActivity { nodes().node("Закрыть фото")!!.click() }
            await(scenario) { it.node("Закрыть фото") == null }
            scenario.onActivity { nodes().node("Открыть фото: $label")!!.click() }
            await(scenario) { all -> all.hasText("1 / 2") && all.isZoomed(false) }
            swipe(scenario)
            await(scenario) { all -> all.hasText("2 / 2") && all.isZoomed(false) }
            swipe(scenario, towardNext = false)
            await(scenario) { all -> all.hasText("1 / 2") && all.isZoomed(false) }

            doubleTap(scenario)
            await(scenario) { all -> all.hasText("1 / 2") && all.hasZoom("250%") }
            doubleTap(scenario)
            await(scenario) { all -> all.hasText("1 / 2") && all.hasZoom("100%") }

            dragVertically(scenario, fraction = .06f)
            await(scenario) { all -> all.hasText("1 / 2") && all.hasZoom("100%") }
            dragVertically(scenario, fraction = .32f)
            await(scenario) { all -> all.node("Закрыть фото") == null && all.node("Открыть фото: $label") != null }

            scenario.onActivity { nodes().node("Открыть фото: $label")!!.click() }
            await(scenario) { all -> all.hasText("1 / 2") && all.hasZoom("100%") }
            dragVertically(scenario, fraction = -.06f)
            await(scenario) { all -> all.hasText("1 / 2") && all.hasZoom("100%") }
            dragVertically(scenario, fraction = -.32f)
            await(scenario) { all -> all.node("Закрыть фото") == null && all.node("Открыть фото: $label") != null }
        }
    }

    private fun swipe(scenario: ActivityScenario<ComponentActivity>, towardNext: Boolean = true) {
        gesture(scenario) { view, bounds ->
            val direction = if (towardNext) 1f else -1f
            val half = Offset(bounds.width * .33f * direction, 0f)
            val start = SystemClock.uptimeMillis() - 192L
            view.sendTouch(start, 0, MotionEvent.ACTION_DOWN, listOf(bounds.center + half))
            for (step in 1..11) {
                view.sendTouch(start, step * 16L, MotionEvent.ACTION_MOVE, listOf(bounds.center + half * (1f - step * 2f / 11)))
            }
            view.sendTouch(start, 192, MotionEvent.ACTION_UP, listOf(bounds.center - half))
        }
    }

    private fun doubleTap(scenario: ActivityScenario<ComponentActivity>) {
        gesture(scenario) { view, bounds ->
            val start = SystemClock.uptimeMillis() - 140L
            val center = listOf(bounds.center)
            view.sendTouch(start, 0, MotionEvent.ACTION_DOWN, center)
            view.sendTouch(start, 30, MotionEvent.ACTION_UP, center)
            view.sendTouch(start + 110, 0, MotionEvent.ACTION_DOWN, center)
            view.sendTouch(start + 110, 30, MotionEvent.ACTION_UP, center)
        }
    }

    private fun dragVertically(scenario: ActivityScenario<ComponentActivity>, fraction: Float) {
        gesture(scenario) { view, bounds ->
            val start = SystemClock.uptimeMillis() - 192L
            val from = bounds.center
            val distance = Offset(0f, bounds.height * fraction)
            view.sendTouch(start, 0, MotionEvent.ACTION_DOWN, listOf(from))
            for (step in 1..11) {
                view.sendTouch(start, step * 16L, MotionEvent.ACTION_MOVE, listOf(from + distance * (step / 11f)))
            }
            view.sendTouch(start, 192, MotionEvent.ACTION_UP, listOf(from + distance))
        }
    }

    private fun List<SemanticsNode>.hasZoom(value: String): Boolean = any {
        it.config.getOrNull(SemanticsProperties.StateDescription) == value
    }

    private fun gesture(scenario: ActivityScenario<ComponentActivity>, send: (View, Rect) -> Unit) {
        scenario.onActivity {
            val root = WindowInspector.getGlobalWindowViews().mapNotNull { it.composeRoot() }.last {
                it.semanticsOwner.getAllSemanticsNodes(mergingEnabled = false).node("Закрыть фото") != null
            }
            val viewport = root.semanticsOwner.getAllSemanticsNodes(mergingEnabled = false).single {
                it.config.getOrNull(SemanticsProperties.StateDescription)?.endsWith("%") == true
            }
            send(root.view, viewport.boundsInRoot)
        }
    }

    private fun View.sendTouch(start: Long, elapsed: Long, action: Int, positions: List<Offset>) {
        val pointers = Array(positions.size) { index ->
            MotionEvent.PointerProperties().apply { id = index; toolType = MotionEvent.TOOL_TYPE_FINGER }
        }
        val coordinates = Array(positions.size) { index ->
            MotionEvent.PointerCoords().apply { x = positions[index].x; y = positions[index].y; pressure = 1f; size = 1f }
        }
        val event = MotionEvent.obtain(start, start + elapsed, action, positions.size, pointers, coordinates,
            0, 0, 1f, 1f, 0, 0, InputDevice.SOURCE_TOUCHSCREEN, 0)
        try { dispatchTouchEvent(event) } finally { event.recycle() }
    }

    private fun List<SemanticsNode>.hasText(text: String): Boolean = any {
        it.config.getOrNull(SemanticsProperties.Text)?.any { value -> value.text == text } == true
    }

    private fun List<SemanticsNode>.isZoomed(zoomed: Boolean): Boolean = any {
        val scale = it.config.getOrNull(SemanticsProperties.StateDescription)?.removeSuffix("%")?.toIntOrNull()
        scale != null && if (zoomed) scale > 100 else scale == 100
    }

    private fun SemanticsNode.click() = assertTrue(checkNotNull(config.getOrNull(SemanticsActions.OnClick)?.action)())

    private fun verifyPreview(label: String, content: @Composable () -> Unit) {
        ActivityScenario.launch(ComponentActivity::class.java).use { scenario ->
            scenario.onActivity { it.setContent { DealerPrototypeTheme(content = content) } }
            val trigger = "Открыть фото: $label"
            await(scenario) { it.node(trigger) != null }
            scenario.onActivity { assertTrue(checkNotNull(nodes().node(trigger)?.config
                ?.getOrNull(SemanticsActions.OnClick)?.action)()) }
            await(scenario) { all -> all.node(label) != null && all.node("Закрыть фото") != null }
            scenario.onActivity { assertTrue(checkNotNull(nodes().node("Закрыть фото")?.config
                ?.getOrNull(SemanticsActions.OnClick)?.action)()) }
            await(scenario) { it.node("Закрыть фото") == null && it.node(trigger) != null }
        }
    }

    private fun await(scenario: ActivityScenario<ComponentActivity>, matches: (List<SemanticsNode>) -> Boolean) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        repeat(60) {
            instrumentation.waitForIdleSync()
            var success = false
            scenario.onActivity { success = matches(nodes()) }
            if (success) return
            android.os.SystemClock.sleep(50)
        }
        throw AssertionError("Expected image preview state did not appear")
    }

    private fun List<SemanticsNode>.node(description: String): SemanticsNode? = firstOrNull {
        it.config.getOrNull(SemanticsProperties.ContentDescription)?.contains(description) == true
    }

    private fun nodes() = WindowInspector.getGlobalWindowViews().mapNotNull { it.composeRoot() }
        .flatMap { it.semanticsOwner.getAllSemanticsNodes(mergingEnabled = false) }

    private fun View.composeRoot(): ViewRootForTest? {
        if (this is ViewRootForTest) return this
        if (this is ViewGroup) {
            for (index in 0 until childCount) getChildAt(index).composeRoot()?.let { return it }
        }
        return null
    }
}
