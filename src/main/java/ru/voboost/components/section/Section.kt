package ru.voboost.components.section

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import ru.voboost.components.i18n.Language
import ru.voboost.components.theme.Theme

/**
 * Section component for Jetpack Compose.
 *
 * A titled container with rounded corners that can contain child views.
 * Now wraps a ViewGroup-based Section that supports addView().
 *
 * @param title Map of language code to localized title text
 * @param lang Language code for localization (e.g., "en", "ru")
 * @param theme Theme identifier (e.g., "free-light", "dreamer-dark")
 * @param content Lambda that receives the Section ViewGroup to add child views
 */
@Composable
fun Section(
    title: Map<String, String>,
    lang: String,
    theme: String,
    content: ((ru.voboost.components.section.Section) -> Unit)? = null,
) {
    AndroidView(
        factory = { context ->
            ru.voboost.components.section.Section(context).apply {
                setTheme(Theme.fromValue(theme))
                setLanguage(Language.fromCode(lang))
                setTitle(title)
                content?.invoke(this)
            }
        },
        update = { sectionView ->
            sectionView.setTheme(Theme.fromValue(theme))
            sectionView.setLanguage(Language.fromCode(lang))
            sectionView.setTitle(title)
        },
    )
}
