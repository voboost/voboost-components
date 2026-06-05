package ru.voboost.components.demo.compose

import androidx.compose.ui.graphics.Color

/**
 * Theme colors for the demo application.
 * Defined as constants to avoid recreation on recomposition.
 */
object DemoColors {
    val DarkBackground = Color(0xff000000)
    val LightBackground = Color(0xfff1f5fb)
}

/**
 * Tab configuration constants (single source of truth for the tab order).
 */
object DemoTabs {
    val TAB_VALUES = listOf(
        "settings",
        "button",
        "buttons",
        "checkbox",
        "radio",
        "select",
        "dialog",
        "toast",
    )
}
