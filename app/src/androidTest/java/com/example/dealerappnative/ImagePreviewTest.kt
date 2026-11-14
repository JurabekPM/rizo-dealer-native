package com.example.dealerappnative

import android.view.View
import android.view.ViewGroup
import android.view.inspector.WindowInspector
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
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
