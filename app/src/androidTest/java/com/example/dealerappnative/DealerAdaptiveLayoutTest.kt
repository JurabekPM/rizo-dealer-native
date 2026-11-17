package com.example.dealerappnative

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.os.Build
import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.ViewRootForTest
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getAllSemanticsNodes
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.dealerappnative.data.VisitReturnsProvider
import com.example.dealerappnative.data.agentVisitCards
import com.example.dealerappnative.screens.AgentScreen
import com.example.dealerappnative.ui.components.StoreActionsHost
import com.example.dealerappnative.ui.theme.DealerPrototypeTheme
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DealerAdaptiveLayoutTest {
    @Test
    fun adminRequestsKeepTheirSelectionAcrossWindowSizes() = withRole({ DealerApp() }) { window ->
        window.onNodes {
            val rail = tabs()
            assertTrue(rail.maxOf { it.positionInRoot.x } - rail.minOf { it.positionInRoot.x } < 6f)
            assertTrue(hasText("Выберите визит"))
            tab("Заявки").click()
        }
        window.onNodes { action("Epa Chorsu").click() }
        window.onNodes {
            val row = action("Epa Chorsu")
            val detail = text("Заявка № 18012105")
            assertTrue(row.boundsInRoot.right < detail.boundsInRoot.left)
            assertTrue(hasText("Общая сумма"))
            tab("Доставки").click()
        }
        window.onNodes { tab("Заявки").click() }
        window.onNodes {
            assertTrue(hasText("Заявка № 18012105"))
            assertTrue(action("Epa Chorsu").boundsInRoot.right < text("Заявка № 18012105").boundsInRoot.left)
        }
        window.resize(720, 980)
        window.onNodes {
            assertEquals(5, tabs().size)
            assertTrue(hasText("Заявка № 18012105"))
            assertFalse(hasText("Поиск по имени и ид"))
        }
        window.back()
        window.onNodes {
            assertFalse(hasText("Заявка № 18012105"))
            assertTrue(action("Epa Chorsu").boundsInRoot.width <= window.dpToPx(600) + 1)
        }
    }

    @Test
    fun agentVisitsKeepTheirListOnLandscapeAndUseBottomTabsOnPhones() = withRole({ AgentScreen(onBack = {}) }) { window ->
        val first = agentVisitCards[0].name
        val second = agentVisitCards[1].name
        window.onNodes { action(first).click() }
        window.onNodes {
            assertTrue(action(first).boundsInRoot.right < text("Конечное сальдо").boundsInRoot.left)
            action(second).click()
        }
        window.onNodes {
            val row = action(second)
            assertTrue(row.config.getOrNull(SemanticsProperties.Selected) == true)
            assertTrue(row.boundsInRoot.right < text("Конечное сальдо").boundsInRoot.left)
        }
        window.resize(390, 820)
        window.onNodes {
            assertTrue(hasText("Конечное сальдо"))
            assertTrue(tabs().isEmpty())
        }
        window.back()
        window.onNodes {
            val bottom = tabs()
            assertEquals(5, bottom.size)
            assertTrue(bottom.maxOf { it.positionInRoot.x } - bottom.minOf { it.positionInRoot.x } > window.dpToPx(220))
            assertTrue(action(second).boundsInRoot.bottom < bottom.first().boundsInRoot.top)
            assertFalse(hasText("Конечное сальдо"))
        }
        window.resize(480, 820)
        window.onNodes {
            assertTrue(tabs().maxOf { it.positionInRoot.x } - tabs().minOf { it.positionInRoot.x } > window.dpToPx(280))
            assertFalse(hasText("Выберите визит"))
        }
    }

    @Test
    fun agentSplitDragPersistsWithFullWidthUnplannedAction() = withRole({ AgentScreen(onBack = {}) }) { window ->
        window.resize(1024, 790)
        val pending = agentVisitCards[1].name
        var initialHandle = Offset.Zero
        window.onNodes {
            val listPane = divider().boundsInWindow
            initialHandle = listPane.center
            val listAction = action("Внеплановые").boundsInWindow
            val bottomGap = listPane.bottom - listAction.bottom
            assertTrue("List action $listAction should sit at the bottom of $listPane", bottomGap in 0f..window.dpToPx(24))
            val row = action(pending).boundsInWindow
            val map = description("Карта визитов").boundsInWindow
            assertEquals(row.width, listAction.width, 2f)
            assertEquals(listAction.right, map.right, 2f)
            assertEquals(map.width, map.height, 2f)
            assertTrue(map.bottom < listAction.top)
            action(pending).click()
        }
        window.onNodes {
            val start = action("Начать визит").boundsInWindow
            val operations = text("Возврат", rightOf = initialHandle.x).boundsInWindow
            assertTrue(start.top >= operations.bottom)
            assertTrue(start.left > initialHandle.x)
            assertTrue(start.center.x > operations.center.x)
            assertTrue(start.width < window.dpToPx(200))
        }
        window.drag(initialHandle, window.dpToPx(158))
        var adjustedX = 0f
        window.onNodes {
            adjustedX = divider().boundsInWindow.center.x
            assertTrue("Divider must move right from ${initialHandle.x}, got $adjustedX", adjustedX > initialHandle.x + window.dpToPx(94))
            assertTrue(action(pending).boundsInWindow.right < divider().boundsInWindow.left)
            assertTrue(action("Начать визит").boundsInWindow.left > adjustedX)
        }
        window.resize(840, 700)
        window.onNodes { assertTrue(divider().boundsInWindow.center.x < adjustedX) }
        window.resize(1024, 790)
        window.onNodes { assertEquals(adjustedX, divider().boundsInWindow.center.x, 2f) }
        window.reopenRole()
        window.onNodes { assertEquals(adjustedX, divider().boundsInWindow.center.x, 2f) }
    }

    @Test
    fun tabletActiveVisitPlacesCameraAboveFinishAndStatisticsFiltersShareARow() =
        withRole({ AgentScreen(onBack = {}) }) { window ->
            window.resize(1024, 820)
            window.darkTheme()
            window.onNodes { action("Ravnaq Market").click() }
            window.onNodes {
                val operations = text("Операции визита").boundsInWindow
                val camera = action("Сделать фото").boundsInWindow
                val finish = action("Завершить визит").boundsInWindow
                assertTrue(camera.top > operations.bottom)
                assertTrue("Camera bounds: $camera, finish bounds: $finish",
                    camera.bottom <= finish.top && camera.right < finish.right)
                assertTrue(camera.width > camera.height)
                tab("Статистика").click()
            }
            window.onNodes {
                val period = text("День").boundsInWindow
                val date = description("Выбрать даты:", prefix = true).boundsInWindow
                assertTrue(date.left > period.right && date.top < period.bottom && period.top < date.bottom)
                assertTrue(hasText("АКБ / ОКБ"))
            }
        }

    @Test
    fun tabletPortraitDeliveriesPlaceShortcutsSideBySideAndCreateAtTheRightEdge() =
        withRole({ DealerApp() }) { window ->
            window.resize(800, 1040)
            window.darkTheme()
            window.onNodes { tab("Доставки").click() }
            window.onNodes {
                val cooperation = text("Запросы на сотрудничество").boundsInWindow
                val returns = text("Возвраты").boundsInWindow
                val order = action("Asil Market").boundsInWindow
                val create = text("Создать").boundsInWindow
                assertTrue(cooperation.top < returns.bottom && returns.top < cooperation.bottom)
                assertTrue(cooperation.right < returns.left)
                assertTrue(order.center.x < create.left && create.top > order.bottom)
                action("Asil Market").click()
            }
            window.onNodes { assertTrue(hasText("Доставка № 33446105")) }
        }

    private fun withRole(content: @Composable () -> Unit, verify: (TestWindow) -> Unit) {
        ActivityScenario.launch(ComponentActivity::class.java).use { scenario ->
            lateinit var context: TestPreferences
            val window = TestWindow(scenario)
            scenario.onActivity { activity ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    activity.setShowWhenLocked(true)
                    activity.setTurnScreenOn(true)
                }
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                context = TestPreferences(activity)
                window.density = (activity.resources.displayMetrics.widthPixels / 1100f).coerceAtMost(1f)
                activity.setContent {
                    CompositionLocalProvider(LocalContext provides context, LocalDensity provides Density(window.density, 1f)) {
                        Box(Modifier.size(window.size.value)) {
                            DealerPrototypeTheme(dark = window.dark.value) {
                                key(window.roleGeneration.intValue) {
                                    VisitReturnsProvider { StoreActionsHost { content() } }
                                }
                            }
                        }
                    }
                }
            }
            try {
                verify(window)
            } finally {
                scenario.onActivity { context.cleanUp() }
            }
        }
    }

    private class TestWindow(private val scenario: ActivityScenario<ComponentActivity>) {
        val size = mutableStateOf(DpSize(1024.dp, 700.dp))
        val dark = mutableStateOf(false)
        val roleGeneration = mutableIntStateOf(0)
        var density = 1f
        fun dpToPx(dp: Int) = dp * density

        fun onNodes(block: ScreenNodes.() -> Unit) {
            // The previous action may have invalidated composition for the next frame.
            val frame = CountDownLatch(1)
            scenario.onActivity { activity ->
                val view = activity.window.decorView
                view.postOnAnimation { view.postOnAnimation { frame.countDown() } }
            }
            assertTrue("The test window did not draw", frame.await(5, TimeUnit.SECONDS))
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            scenario.onActivity { activity ->
                val root = checkNotNull(activity.window.decorView.composeRoot())
                ScreenNodes(root.semanticsOwner.getAllSemanticsNodes(mergingEnabled = true)).block()
            }
        }

        fun resize(width: Int, height: Int) {
            scenario.onActivity { size.value = DpSize(width.dp, height.dp) }
        }

        fun darkTheme() {
            scenario.onActivity { dark.value = true }
        }

        fun reopenRole() {
            scenario.onActivity { roleGeneration.intValue++ }
        }

        fun drag(start: Offset, distance: Float) {
            val downTime = SystemClock.uptimeMillis()
            fun send(action: Int, x: Float) {
                scenario.onActivity { activity ->
                    val event = MotionEvent.obtain(downTime, SystemClock.uptimeMillis(), action, x, start.y, 0)
                    try {
                        activity.dispatchTouchEvent(event)
                    } finally {
                        event.recycle()
                    }
                }
            }
            send(MotionEvent.ACTION_DOWN, start.x)
            for (step in 1..8) {
                SystemClock.sleep(20)
                send(MotionEvent.ACTION_MOVE, start.x + distance * step / 8)
            }
            send(MotionEvent.ACTION_UP, start.x + distance)
        }

        fun back() {
            scenario.onActivity { it.onBackPressedDispatcher.onBackPressed() }
            SystemClock.sleep(650)
        }
    }

    private class ScreenNodes(private val nodes: List<SemanticsNode>) {
        fun tabs() = nodes.filter { it.config.getOrNull(SemanticsProperties.Role) == Role.Tab }
            .filter { it.config.getOrNull(SemanticsProperties.Text)?.any { text -> text.text in navigationNames } == true }
        fun tab(text: String) = tabs().first { it.hasText(text) }
        fun text(text: String, rightOf: Float = Float.NEGATIVE_INFINITY) = checkNotNull(
            nodes.firstOrNull { it.hasText(text) && it.boundsInWindow.left > rightOf }
        ) {
            "Missing $text in ${nodes.mapNotNull { it.config.getOrNull(SemanticsProperties.Text) }.flatten()}"
        }
        fun hasText(text: String) = nodes.any { it.hasText(text) }
        fun action(text: String) = checkNotNull(nodes.firstOrNull {
            it.hasText(text) && it.config.getOrNull(SemanticsActions.OnClick) != null
        }) { "Missing action $text in ${nodes.mapNotNull { it.config.getOrNull(SemanticsProperties.Text) }.flatten()}" }

        fun divider() = description("Ширина списка")

        fun description(value: String, prefix: Boolean = false) = nodes.first {
            it.config.getOrNull(SemanticsProperties.ContentDescription)?.any { description ->
                if (prefix) description.startsWith(value) else description == value
            } == true
        }

        private fun SemanticsNode.hasText(value: String) =
            config.getOrNull(SemanticsProperties.Text)?.any { it.text == value } == true

        fun SemanticsNode.click() {
            assertTrue(checkNotNull(config[SemanticsActions.OnClick].action).invoke())
        }

        companion object {
            private val navigationNames = setOf("Визиты", "Заявки", "Доставки", "Инкассация", "Магазины", "Статистика", "История", "Меню")
        }
    }

    private class TestPreferences(base: Context) : ContextWrapper(base) {
        private val prefix = "adaptive-test-${UUID.randomUUID()}-"
        private val names = mutableSetOf<String>()

        override fun getApplicationContext(): Context = this

        override fun getSharedPreferences(name: String, mode: Int): SharedPreferences {
            val isolatedName = prefix + name
            names += isolatedName
            return super.getSharedPreferences(isolatedName, mode)
        }

        fun cleanUp() = names.forEach { baseContext.deleteSharedPreferences(it) }
    }
}

private fun View.composeRoot(): ViewRootForTest? {
    if (this is ViewRootForTest) return this
    if (this is ViewGroup) {
        for (index in 0 until childCount) getChildAt(index).composeRoot()?.let { return it }
    }
    return null
}
