package com.example.dealerappnative.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

/** SF Pro measurements from the new Figma; native sans-serif is the Android adaptation. */
internal object AgentVisitDesign {
    val tertiary: Color @Composable get() = DealerDesign.tertiary
    val title = DealerDesign.type(20, 24, FontWeight.SemiBold, .38f)
    val name = DealerDesign.type(16, 21, FontWeight.SemiBold, -.24f)
    val detail = DealerDesign.type(14, 19, FontWeight.Medium, -.078f)
    val emphasis = DealerDesign.type(16, 21, FontWeight.SemiBold, -.24f)
    val status = DealerDesign.type(14, 19, FontWeight.SemiBold, -.078f)
    val screenTitle = DealerDesign.type(17, 22, FontWeight.SemiBold)
    val sheetTitle = DealerDesign.type(20, 24, FontWeight.SemiBold, .38f)
    val button = DealerDesign.type(16, 21, FontWeight.SemiBold, -.5f)
    val navigation = DealerDesign.type(12, 15, FontWeight.Medium, .06f)
}
