package com.example.dealerappnative.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

/** Profile-local refinement; other Admin/Agent screens keep their approved typography. */
internal object ProfileDesign {
    val text: Color @Composable get() = dealerColor(Color(0xFF20262C), DealerDesign.text)
    val secondary: Color @Composable get() = dealerColor(Color(0xFF6C7780), DealerDesign.secondary)
    val field: Color @Composable get() = dealerColor(Color(0xFFF6F7F9), Color(0xFF292733))
    val tint: Color @Composable get() = dealerColor(Color(0xFFF2EDF8), Color(0xFF352A45))
    val mutedAccent: Color @Composable get() = dealerColor(Color(0xFF76618B), Color(0xFFD5B1F5))
    val title = DealerDesign.type(20, 26, FontWeight.SemiBold, -.3f)
    val name = DealerDesign.type(16, 21, FontWeight.SemiBold, -.15f)
    val value = DealerDesign.type(15, 21, FontWeight.Medium, -.1f)
    val body = DealerDesign.type(14, 19, FontWeight.Medium, -.05f)
    val label = DealerDesign.type(13, 17, FontWeight.Medium, 0f)
    val badge = DealerDesign.type(12, 15, FontWeight.Medium, 0f)
    val section = DealerDesign.type(16, 21, FontWeight.SemiBold, -.1f)
    val button = DealerDesign.type(16, 21, FontWeight.Medium, -.15f)
}
