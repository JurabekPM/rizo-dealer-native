package com.example.dealerappnative.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.dealerappnative.model.StatementKind

/** Local to the statement flow; other Admin/Agent typography stays unchanged. */
internal object StatementDesign {
    val text: Color @Composable get() = dealerColor(Color(0xFF232B32), DealerDesign.text)
    val secondary: Color @Composable get() = dealerColor(Color(0xFF697781), DealerDesign.secondary)
    val surface: Color @Composable get() = dealerColor(Color(0xFFF5F5F5), Color(0xFF2D2A35))
    val border: Color @Composable get() = dealerColor(Color(0xFFE9EDF0), DealerDesign.divider)
    val selected: Color @Composable get() = dealerColor(Color(0xFFF8F2FD), Color(0xFF372845))
    val order: Color @Composable get() = dealerColor(Color(0xFF1D4ED8), Color(0xFF91B8FF))
    val payment: Color @Composable get() = dealerColor(Color(0xFF15803D), Color(0xFF83D7A3))
    val returned: Color @Composable get() = dealerColor(Color(0xFFB91C1C), Color(0xFFFF939D))
    @Composable
    fun operationColor(kind: StatementKind): Color = when (kind) {
        StatementKind.Order -> order
        StatementKind.Payment -> payment
        StatementKind.Return -> returned
        StatementKind.Request -> text
    }
    val title = DealerDesign.type(20, 26, FontWeight.SemiBold, -.25f)
    val appBarName = DealerDesign.type(14, 20, FontWeight.SemiBold, -.15f)
    val name = DealerDesign.type(15, 21, FontWeight.SemiBold, -.15f)
    val amount = DealerDesign.type(16, 21, FontWeight.SemiBold, -.2f)
    val body = DealerDesign.type(14, 19, FontWeight.Medium, -.05f)
    val label = DealerDesign.type(13, 17, FontWeight.SemiBold, 0f)
    val caption = DealerDesign.type(13, 17, FontWeight.Medium, 0f)
    val choice = DealerDesign.type(14, 19, FontWeight.SemiBold, -.08f)
    val button = DealerDesign.type(16, 21, FontWeight.SemiBold, -.15f)
}
