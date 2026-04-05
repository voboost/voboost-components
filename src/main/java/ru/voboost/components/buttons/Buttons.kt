package ru.voboost.components.buttons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import ru.voboost.components.i18n.Language
import ru.voboost.components.theme.Theme

/**
 * Buttons component for Jetpack Compose.
 * Multiple buttons in a row with optional text.
 *
 * @param buttons List of ButtonConfig
 * @param theme Theme enum value
 * @param selectedValue Currently selected value
 * @param onValueChange Callback when selection changes
 * @param rightText Optional text to the right of buttons (localized)
 * @param description Optional description below (localized)
 * @param lang Optional language
 * @param onViewCreated Optional callback when created
 */
@Composable
fun Buttons(
    buttons: List<ButtonConfig>,
    theme: Theme,
    selectedValue: String? = null,
    onValueChange: ((String) -> Unit)? = null,
    rightText: Map<String, String>? = null,
    description: Map<String, String>? = null,
    lang: Language? = null,
    onViewCreated: ((ru.voboost.components.buttons.Buttons) -> Unit)? = null,
) {
    val valueChangeListener =
        remember(onValueChange) {
            ru.voboost.components.buttons.Buttons.OnValueChangeListener { newValue ->
                onValueChange?.invoke(newValue)
            }
        }

    AndroidView(
        factory = { context ->
            ru.voboost.components.buttons.Buttons(context).apply {
                setButtons(buttons)
                setTheme(theme)
                selectedValue?.let { setSelectedValue(it) }
                rightText?.let { setRightText(it) }
                description?.let { setDescription(it) }
                lang?.let { setLanguage(it) }
                setOnValueChangeListener(valueChangeListener)
                onViewCreated?.invoke(this)
            }
        },
        update = { view ->
            view.setButtons(buttons)
            view.setTheme(theme)
            selectedValue?.let { view.setSelectedValue(it) }
            rightText?.let { view.setRightText(it) }
            description?.let { view.setDescription(it) }
            lang?.let { view.setLanguage(it) }
            // Note: valueChangeListener doesn't change on recomposition
        },
    )
}
