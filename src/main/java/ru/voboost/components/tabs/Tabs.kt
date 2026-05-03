package ru.voboost.components.tabs

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import ru.voboost.components.i18n.Language
import ru.voboost.components.theme.Theme

/**
 * Tabs component for Jetpack Compose.
 *
 * Vertical navigation sidebar with animated selection indicator.
 *
 * @param items List of TabItem representing the tabs
 * @param lang Language for localization (Language.EN, Language.RU)
 * @param theme Theme enum value
 * @param value Currently selected tab value
 * @param onValueChange Callback when tab selection changes
 */
@Composable
fun Tabs(
    items: List<TabItem>,
    lang: Language,
    theme: Theme,
    value: String,
    onValueChange: (String) -> Unit,
) {
    AndroidView(
        factory = { context ->
            ru.voboost.components.tabs.Tabs(context).apply {
                setTheme(theme)
                setLanguage(lang)
                setItems(items)
                setSelectedValue(value)
                setOnValueChangeListener { onValueChange(it) }
            }
        },
        update = { tabsView ->
            // Only update if values actually changed
            if (tabsView.getCurrentTheme() != theme) {
                tabsView.setTheme(theme)
            }
            if (tabsView.getCurrentLanguage() != lang) {
                tabsView.setLanguage(lang)
            }
            // Always update items and value (no cheap comparison available)
            tabsView.setItems(items)
            tabsView.setSelectedValue(value)
        },
    )
}
