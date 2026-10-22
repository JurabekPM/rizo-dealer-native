package com.example.dealerappnative.ui.theme

import android.content.res.Configuration
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.createFontFamilyResolver
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Shared colors, dimensions and typography for every prototype screen. Keep line height explicit. */
internal object DealerDesign {
    val accent = Color(0xFF9103E4)
    val secondary = Color(0xFF84919A)
    val tertiary = Color(0xFF9AA6AC)
    val background = Color(0xFFF4F4F4)
    val divider = Color(0xFFEBEBEB)
    val text = Color.Black
    val pageInset = 16.dp
    val sectionGap = 8.dp
    val iconSize = 24.dp
    // Figma Nav Bar -> Admin: 87 including a 34-point iOS safe area.
    // Android supplies its actual safe area separately.
    val navigationHeight = 53.dp
    val cardHeight = 113.dp
    val buttonHeight = 48.dp
    val radius = 12.dp

    // Figma uses SF Pro; Android uses its native sans-serif with the same metrics.
    fun type(size: Int, line: Int, weight: FontWeight = FontWeight.SemiBold, tracking: Float = -0.5f) = TextStyle(
        fontFamily = FontFamily.SansSerif, fontSize = size.sp, lineHeight = line.sp,
        fontWeight = weight, letterSpacing = tracking.sp,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    )
    val title = type(17, 22, FontWeight.SemiBold)
    val body = type(15, 20)
    val cardTitle = type(15, 20, tracking = -0.24f)
    val search = type(15, 20, tracking = -0.24f)
    val detail = type(13, 18, tracking = -0.08f)
    val tab = type(13, 18, tracking = -0.08f)
    val navigation = type(11, 13, tracking = 0.06f)
    val button = type(15, 20)
}

/** This visual test app renders the specified weights even with Samsung bold text.
 * Only this composition is affected; the device settings remain untouched.
 * Reconsider this preview behavior before using these screens in a production app.
 */
@Composable
internal fun PrototypeFontWeights(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val resolver = remember(context) {
        val config = Configuration(context.resources.configuration)
        if (Build.VERSION.SDK_INT >= 31) config.fontWeightAdjustment = 0
        createFontFamilyResolver(context.createConfigurationContext(config))
    }
    CompositionLocalProvider(LocalFontFamilyResolver provides resolver, content = content)
}
