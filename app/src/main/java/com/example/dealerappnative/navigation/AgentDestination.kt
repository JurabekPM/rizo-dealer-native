package com.example.dealerappnative.navigation

import androidx.annotation.DrawableRes
import com.example.dealerappnative.R

/** Figma order with the requested Stores tab inserted after Visits. */
internal enum class AgentDestination(val title: String, @param:DrawableRes @get:DrawableRes val iconRes: Int, val available: Boolean = true) {
    Visits("Визиты", R.drawable.ic_visit_route),
    Stores("Магазины", R.drawable.ic_store),
    Statistics("Статистика", R.drawable.ic_agent_statistics),
    History("История", R.drawable.ic_agent_history),
    More("Меню", R.drawable.ic_agent_menu)
}
