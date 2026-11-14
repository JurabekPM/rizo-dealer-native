package com.example.dealerappnative.navigation

import androidx.annotation.DrawableRes
import com.example.dealerappnative.R

/** Declaration order is the order shown in the bottom navigation. */
internal enum class DealerDestination(
    val title: String,
    @param:DrawableRes @get:DrawableRes val iconRes: Int
) {
    Visits("Визиты", R.drawable.ic_visit_route),
    Requests("Заявки", R.drawable.ic_visit),
    Deliveries("Доставки", R.drawable.ic_box),
    Collections("Инкассация", R.drawable.ic_money),
    Menu("Меню", R.drawable.ic_menu)
}
