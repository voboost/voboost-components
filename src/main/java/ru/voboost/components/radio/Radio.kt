package ru.voboost.components.radio

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import ru.voboost.components.i18n.Language
import ru.voboost.components.theme.Theme

/**
 * Radio component with internal theming and external localization.
 * Minimal Kotlin Compose wrapper around Java Custom View implementation.
 *
 * @param buttons List of radio button options with localized labels
 * @param lang Language enum value
 * @param theme Theme enum value
 * @param value Currently selected value
 * @param onValueChange Callback when selection changes
 * @param onViewCreated Optional callback when the AndroidView is created (useful for testing)
 */
@Composable
fun Radio(
    buttons: List<RadioButton>,
    lang: Language,
    theme: Theme,
    value: String,
    onValueChange: (String) -> Unit,
    onViewCreated: ((ru.voboost.components.radio.Radio) -> Unit)? = null,
) {
    AndroidView(
        factory = { context ->
            ru.voboost.components.radio.Radio(context).apply {
                // Set initial values
                setButtons(buttons)
                setLanguage(lang)
                setTheme(theme)
                setSelectedValue(value)

                // Set up value change listener
                setOnValueChangeListener { newValue ->
                    onValueChange(newValue)
                }

                // Notify caller about view creation (useful for testing)
                onViewCreated?.invoke(this)
            }
        },
        update = { radioView ->
            // Update parameters when they change
            radioView.setButtons(buttons)
            radioView.setLanguage(lang)
            radioView.setTheme(theme)
            radioView.setSelectedValue(value)
        },
    )
}
