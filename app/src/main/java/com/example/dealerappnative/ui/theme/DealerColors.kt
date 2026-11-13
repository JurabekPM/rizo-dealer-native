package com.example.dealerappnative.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.dealerappnative.model.DebtDirection
import com.example.dealerappnative.model.VisitStatus

internal val LocalDealerDarkTheme = compositionLocalOf { false }

@Composable
internal fun dealerColor(light: Color, dark: Color): Color =
    if (LocalDealerDarkTheme.current) dark else light

internal val DebtDirection.displayColor: Color
    @Composable get() = dealerColor(color, when (this) {
        DebtDirection.DealerOwesStore -> Color(0xFF83D7A3)
        DebtDirection.StoreOwesDealer -> Color(0xFFFF939D)
    })

internal val VisitStatus.displayColor: Color
    @Composable get() = dealerColor(color, when (this) {
        VisitStatus.Active -> Color(0xFF91B8FF)
        VisitStatus.Attempted -> Color(0xFFFFCF81)
        VisitStatus.Completed -> Color(0xFF83D7A3)
        VisitStatus.NotVisited -> DealerDesign.secondary
    })

internal val platformPickerTheme: Int
    @Composable get() = if (LocalDealerDarkTheme.current) android.R.style.Theme_Material_Dialog_Alert
        else android.R.style.Theme_Material_Light_Dialog_Alert
