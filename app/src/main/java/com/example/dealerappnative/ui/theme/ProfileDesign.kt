package com.example.dealerappnative.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight

/** Profile-local refinement; other Admin/Agent screens keep their approved typography. */
internal object ProfileDesign {
    val text = Color(0xFF20262C)
    val secondary = Color(0xFF6C7780)
    val field = Color(0xFFF6F7F9)
    val tint = Color(0xFFF2EDF8)
    val mutedAccent = Color(0xFF76618B)
    val title = DealerDesign.type(20, 26, FontWeight.SemiBold, -.3f)
    val name = DealerDesign.type(15, 20, FontWeight.SemiBold, -.15f)
    val value = DealerDesign.type(14, 20, FontWeight.Medium, -.1f)
    val body = DealerDesign.type(13, 18, FontWeight.Medium, -.05f)
    val label = DealerDesign.type(12, 16, FontWeight.Medium, 0f)
    val badge = DealerDesign.type(11, 14, FontWeight.Medium, 0f)
    val section = DealerDesign.type(15, 20, FontWeight.SemiBold, -.1f)
    val button = DealerDesign.type(15, 20, FontWeight.Medium, -.15f)
}
