package ru.voboost.components.tabs

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import ru.voboost.components.i18n.Language
import ru.voboost.components.theme.Theme

/**
 * Tabs component for Jetpack Compose.
 *
 * A vertical navigation sidebar with animated selection indicator.
 *
 * @param items List of TabItem objects representing the tabs
 * @param lang Language enum value for localization
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
                setOnValueChangeListener { newValue ->
                    onValueChange(newValue)
                }
            }
        },
        update = { tabsView ->
            tabsView.setTheme(theme)
            tabsView.setLanguage(lang)
            tabsView.setItems(items)
            tabsView.setSelectedValue(value)
        },
    )
}
