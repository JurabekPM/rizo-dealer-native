package com.example.dealerappnative.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dealerappnative.R

/** Agent Figma: Onest and its own text color; administrator typography stays unchanged. */
internal object AgentDesign {
    val text = Color(0xFF211F26)
    val secondary = DealerDesign.secondary
    val accent = DealerDesign.accent
    val background = DealerDesign.background
    val divider = DealerDesign.divider
    val tint = Color(0xFFF4E6FC)
    val negative = Color(0xFFF76659)
    val radius = DealerDesign.radius
    val inset = DealerDesign.pageInset
    val gap = DealerDesign.sectionGap
    val iconSize = 22.dp
    // Static instances keep each weight independent on Android and support API 24+.
    private val onest = FontFamily(
        Font(R.font.agent_onest_regular, FontWeight.Normal),
        Font(R.font.agent_onest_medium, FontWeight.Medium),
        Font(R.font.agent_onest_semibold, FontWeight.SemiBold),
        Font(R.font.agent_onest_bold, FontWeight.Bold)
    )
    private fun type(size: Float, line: Float, weight: FontWeight = FontWeight.Normal, tracking: Float = 0f) = TextStyle(
        fontFamily = onest, fontSize = size.sp, lineHeight = line.sp, fontWeight = weight,
        letterSpacing = tracking.sp, platformStyle = PlatformTextStyle(includeFontPadding = false)
    )
    val date = type(16f, 22f, FontWeight.SemiBold)
    val updated = type(12f, 16f, FontWeight.Medium)
    val heroLabel = type(15f, 20f, FontWeight.Medium)
    val heroValue = type(36f, 43.2f, FontWeight.Bold, -.72f)
    val percent = type(16f, 22f, FontWeight.SemiBold)
    val label = type(13f, 20f, FontWeight.Medium)
    val value = type(20f, 28f, FontWeight.Bold)
    val detail = type(12f, 16f, FontWeight.Medium)
    val badge = type(12f, 16f, FontWeight.SemiBold)
    val debt = type(18f, 25f, FontWeight.Bold)
    val screenTitle = type(17f, 22f, FontWeight.SemiBold)
    val reportTitle = type(22f, 28f, FontWeight.SemiBold)
    val menuTitle = type(15f, 20f, FontWeight.SemiBold)
    val menuDetail = type(13f, 18f, FontWeight.Medium)
    val navigation = type(11f, 16f, FontWeight.Medium)
}
