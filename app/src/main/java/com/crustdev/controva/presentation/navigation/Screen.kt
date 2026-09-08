package com.crustdev.controva.presentation.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Connect : Screen("connect")
    object LayoutPicker : Screen("layout_picker")
    object Controller : Screen("controller/{layoutName}") {
        fun createRoute(layoutName: String): String = "controller/$layoutName"
    }
    object Settings : Screen("settings")
}
