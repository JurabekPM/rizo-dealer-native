package com.example.dealerappnative

import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.ViewRootForTest
import androidx.compose.ui.semantics.ScrollAxisRange
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getAllSemanticsNodes
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.dealerappnative.model.StatementCurrencyTotals
import com.example.dealerappnative.ui.components.StoreStatementOverview
import com.example.dealerappnative.ui.theme.DealerPrototypeTheme
import java.math.BigDecimal
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StoreStatementOverviewLayoutTest {
    @Test fun normalTotalsKeepAllThreeCardsInOneRow() = withOverview(
        listOf(
            StatementCurrencyTotals("USD", BigDecimal("1152"), BigDecimal("1117"), BigDecimal("567.5")),
            StatementCurrencyTotals("UZS", BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)
        )
    ) { nodes, scroll ->
        assertHorizontalRow(nodes)
        assertTrue("The last card must be reachable by scrolling", scroll.maxValue() > 0)
        listOf("1 152 USD", "1 117 USD", "567,5 USD", "0 UZS").forEach {
            assertSingleLine(nodes.getValue(it), it)
        }
    }

    @Test fun longTotalsRemainSingleLineInTheScrollableRowAtLargeFontScale() = withOverview(
        listOf(
            StatementCurrencyTotals("USD", BigDecimal("12345678.901234"), BigDecimal.ONE, BigDecimal.ZERO),
            StatementCurrencyTotals("UZS", BigDecimal("9876543210123456789"), BigDecimal("5105000"), BigDecimal("2585000"))
        ),
        fontScale = 1.8f
    ) { nodes, scroll ->
        assertHorizontalRow(nodes)
        assertTrue("Wide cards must remain reachable by scrolling", scroll.maxValue() > 0)
        listOf("12 345 678,901234 USD", "9 876 543 210 123 456 789 UZS", "5 105 000 UZS", "2 585 000 UZS").forEach {
            assertSingleLine(nodes.getValue(it), it)
        }
    }

    private fun assertHorizontalRow(nodes: Map<String, SemanticsNode>) {
        val labels = listOf("Заказы", "Оплачено", "Возвраты")
        val positions = labels.map { label ->
            nodes.getValue(label).also { assertSingleLine(it, label) }.positionInRoot
        }
        assertTrue("Expected one horizontal row, got $positions", positions.all { it.y == positions.first().y })
        assertTrue(positions.zipWithNext().all { (left, right) -> left.x < right.x })
    }

    // Use the platform activity and semantics because this device's Espresso cannot initialize input.
    private fun withOverview(
        totals: List<StatementCurrencyTotals>,
        fontScale: Float = 1f,
        verify: (Map<String, SemanticsNode>, ScrollAxisRange) -> Unit
    ) {
        ActivityScenario.launch(ComponentActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.setContent {
                    val density = LocalDensity.current
                    CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
                        DealerPrototypeTheme(dark = fontScale > 1f) {
                            Box(Modifier.width(320.dp)) {
                                StoreStatementOverview(totals, count = 11, filtered = false)
                            }
                        }
                    }
                }
            }
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            scenario.onActivity { activity ->
                val root = checkNotNull(activity.window.decorView.composeRoot())
                val nodes = root.semanticsOwner.getAllSemanticsNodes(mergingEnabled = false)

                val textNodes = nodes.mapNotNull { node ->
                    node.config.getOrNull(SemanticsProperties.Text)?.singleOrNull()?.text?.let { it to node }
                }.toMap()
                val scroll = nodes.mapNotNull { it.config.getOrNull(SemanticsProperties.HorizontalScrollAxisRange) }.single()
                verify(textNodes, scroll)
            }
        }
    }

    private fun View.composeRoot(): ViewRootForTest? {
        if (this is ViewRootForTest) return this
        if (this is ViewGroup) {
            for (index in 0 until childCount) getChildAt(index).composeRoot()?.let { return it }
        }
        return null
    }

    private fun assertSingleLine(node: SemanticsNode, text: String) {
        val results = mutableListOf<TextLayoutResult>()
        val action = checkNotNull(node.config[SemanticsActions.GetTextLayoutResult].action)
        assertTrue(action(results))
        val layout = results.single()
        assertFalse("$text exceeds the available height", layout.didOverflowHeight)
        assertEquals("$text must stay on one line", 1, layout.lineCount)
        assertTrue("$text exceeds the available width", layout.getLineRight(0) <= layout.size.width + 1f)
    }
}
