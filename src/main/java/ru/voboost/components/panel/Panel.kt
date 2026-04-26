package ru.voboost.components.panel

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import ru.voboost.components.i18n.Language
import ru.voboost.components.theme.Theme

/**
 * Panel component for Jetpack Compose.
 *
 * A transparent container with built-in scrolling for grouping Section components.
 *
 * Note: For adding children, use the Java API or create extension functions.
 * Example:
 * ```kotlin
 * val panel = Panel(context).apply { setTheme(theme); setLanguage(lang) }
 * val section = Section(context).apply { ... }
 * panel.addView(section)
 * ```
 *
 * @param theme Theme enum value
 * @param lang Language enum value (default: EN)
 */
@Composable
fun Panel(
    theme: Theme,
    lang: Language = Language.EN,
) {
    AndroidView(
        factory = { context ->
            ru.voboost.components.panel.Panel(context).apply {
                setTheme(theme)
                setLanguage(lang)
            }
        },
        update = { panelView ->
            panelView.setTheme(theme)
            panelView.setLanguage(lang)
        },
    )
}

/**
 * Extension function to add a Section to Panel with DSL-style configuration.
 *
 * Usage:
 * ```kotlin
 * val panel = Panel(context).apply { setTheme(theme) }
 * panel.addSection(
 *     title = mapOf("en" to "Language", "ru" to "Язык"),
 *     theme = Theme.FREE_LIGHT,
 *     language = Language.EN
 * ) {
 *     val radio = Radio(context).apply { ... }
 *     addView(radio)
 * }
 * ```
 *
 * @param title Section title as language map
 * @param theme Theme for the section
 * @param language Language for the section
 * @param content Lambda to configure the section (add children, etc.)
 * @return The created and added Section
 */
fun ru.voboost.components.panel.Panel.addSection(
    title: Map<String, String>,
    theme: Theme,
    language: Language,
    content: ru.voboost.components.section.Section.() -> Unit,
): ru.voboost.components.section.Section {
    val section =
        ru.voboost.components.section.Section(context).apply {
            setTheme(theme)
            setLanguage(language)
            setTitle(title)
            content()
        }
    addView(section)
    return section
}

/**
 * Extension function to add a pre-configured Section to Panel.
 *
 * Usage:
 * ```kotlin
 * val panel = Panel(context).apply { setTheme(theme) }
 * val section = Section(context).apply { ... }
 * panel.addSection(section)
 * ```
 *
 * @param section The Section to add
 * @return The added Section
 */
fun ru.voboost.components.panel.Panel.addSection(
    section: ru.voboost.components.section.Section,
): ru.voboost.components.section.Section {
    addView(section)
    return section
}
