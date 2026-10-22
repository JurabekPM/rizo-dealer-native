package com.example.dealerappnative.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.dealerappnative.data.HistorySection
import com.example.dealerappnative.data.adminRevisionHistoryEntries

/** Menu -> Revisions uses the same month/document hierarchy as Agent History. */
@Composable
internal fun AdminRevisionsScreen(onBack: () -> Unit) {
    val entries = remember { adminRevisionHistoryEntries() }
    MonthlyOperationsScreen(HistorySection.Revisions, entries, onBack, showRevisionBalanceLabel = true)
}
