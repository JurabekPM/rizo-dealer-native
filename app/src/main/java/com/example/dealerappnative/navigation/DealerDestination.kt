package com.example.dealerappnative.navigation

import androidx.annotation.DrawableRes
import com.example.dealerappnative.R

/** Declaration order is the order shown in the bottom navigation. */
internal enum class DealerDestination(
    val title: String,
    @param:DrawableRes @get:DrawableRes val iconRes: Int,
    val iconMotion: NavigationIconMotion
) {
    Visits("Визиты", R.drawable.ic_visit_route, NavigationIconMotion.Route),
    Requests("Заявки", R.drawable.ic_visit, NavigationIconMotion.Lift),
    Deliveries("Доставки", R.drawable.ic_box, NavigationIconMotion.Turn),
    Collections("Инкассация", R.drawable.ic_money, NavigationIconMotion.Lift),
    Menu("Меню", R.drawable.ic_menu, NavigationIconMotion.Squeeze)
}
