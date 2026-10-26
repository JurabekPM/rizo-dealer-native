package com.example.dealerappnative

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.dealerappnative.model.*
import com.example.dealerappnative.ui.components.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicReference

/** Synthetic UI only: no preferences, actual stores or financial actions.
 * Use public platform synchronization: the project's old Espresso cannot inject input
 * on this phone's Android version. Raw list deltas exercise the same scroll consumer.
 */
@RunWith(AndroidJUnit4::class)
class VisitScrollRegressionTest {
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    private lateinit var scenario: ActivityScenario<ComponentActivity>
    private lateinit var listState: LazyListState
    private lateinit var openVisit: (String) -> Unit
    private var selected by mutableStateOf<String?>(null)
    private var showVisits by mutableStateOf(true)
    private var work by mutableStateOf(false)
    private var savedIndex = 0
    private var savedOffset = 0
    private val stores = List(80) { index -> VisitStore("test-$index", "Test store $index", "10 UZS",
        VisitStatus.Completed, "1 км", "Test address $index",
        VisitDetails("26.10.2026", "Test agent", "", "", "", "", ""),
        results = listOf(VisitResult.Delivery, VisitResult.Request)) }

    @Before fun launch() {
        scenario = ActivityScenario.launch(ComponentActivity::class.java)
        scenario.onActivity { activity -> activity.setContent {
            MaterialTheme {
                listState = rememberLazyListState()
                if (showVisits) VisitResultsTransition(selected, onSelect = {
                    savedIndex = listState.firstVisibleItemIndex
                    savedOffset = listState.firstVisibleItemScrollOffset
                    selected = it
                }) { id, open ->
                    if (id == null) {
                        openVisit = open
                        VisitListViewport(active = selected == null) {
                            LazyColumn(Modifier.fillMaxSize(), state = listState) {
                                items(stores, key = { it.id }) { original ->
                                    val store = if (work) original.copy(results = original.results + VisitResult.Collection) else original
                                    VisitStoreCard(store) { open(store.id) }
                                }
                            }
                        }
                    } else {
                        val store = stores.first { it.id == id }
                        Column(Modifier.fillMaxSize().background(Color.White).padding(16.dp)) {
                            Box(Modifier.sharedVisitStorePart(id, VisitStorePart.Logo)) { DealerIcon(R.drawable.ic_store) }
                            Text(store.name, Modifier.sharedVisitStorePart(id, VisitStorePart.Name))
                            Text(store.debtDirection.formatAmount(store.debt), Modifier.sharedVisitStorePart(id, VisitStorePart.Debt))
                            store.results.forEach { VisitResultBadge(it, id, inDetails = true) }
                            Text(if (work) "Test operation saved" else "No test work")
                        }
                    }
                } else Text("Other screen")
            }
        } }
        settle()
    }

    @After fun close() { scenario.close() }

    private fun settle() {
        // Includes both 440ms bounds and the 600ms cleanup that caused the original bug.
        android.os.SystemClock.sleep(900)
        instrumentation.waitForIdleSync()
    }

    private fun <T> onMain(block: () -> T): T {
        val value = AtomicReference<T>()
        instrumentation.runOnMainSync { value.set(block()) }
        return value.get()
    }

    private fun position(): Pair<Int, Int> = onMain {
        listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset
    }

    private fun scroll(delta: Float) {
        onMain { listState.dispatchRawDelta(listState.layoutInfo.viewportSize.height * delta) }
        instrumentation.waitForIdleSync()
    }

    private fun assertScrollableBothWays() {
        val start = position()
        scroll(.8f)
        val down = position()
        assertTrue("Forward scroll stalled: $start -> $down", down.first >= start.first + 2)
        scroll(-.8f)
        val back = position()
        assertTrue("Backward scroll stalled: $down -> $back", back.first <= down.first - 2)
    }

    @Test fun scrollingSurvivesRepeatedDetailWorkAndBack() {
        scroll(1.6f)
        repeat(3) {
            val before = position()
            onMain { openVisit("test-${before.first + 1}") }
            settle()
            assertNotNull(onMain { selected })
            onMain { work = !work }
            instrumentation.waitForIdleSync()
            onMain {
                listState.requestScrollToItem(savedIndex, savedOffset)
                selected = null
            }
            settle()
            assertEquals("Return lost the viewport", before, position())
            assertScrollableBothWays()
        }
    }

    @Test fun hoistedStateScrollsAfterLeavingAndReturningToVisitsTab() {
        scroll(1.6f)
        val before = position()
        onMain { showVisits = false }
        settle()
        onMain { showVisits = true }
        settle()
        assertEquals(before, position())
        assertScrollableBothWays()
    }
}
