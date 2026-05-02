package ru.voboost.components.radio

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import ru.voboost.components.i18n.Language
import ru.voboost.components.theme.Theme

/**
 * Radio component for Jetpack Compose.
 * Wraps Java LinearLayout container with optional title and descriptions.
 *
 * @param buttons List of radio button options with localized labels
 * @param lang Language enum value
 * @param theme Theme enum value
 * @param value Currently selected value
 * @param onValueChange Callback when selection changes
 * @param title Optional title text (localized)
 * @param descriptionAbove Optional description above radio (localized)
 * @param descriptionBelow Optional description below radio (localized)
 * @param onViewCreated Optional callback when the AndroidView is created
 */
@Composable
fun Radio(
    buttons: List<RadioButton>,
    lang: Language,
    theme: Theme,
    value: String,
    onValueChange: (String) -> Unit,
    title: Map<String, String>? = null,
    descriptionAbove: Map<String, String>? = null,
    descriptionBelow: Map<String, String>? = null,
    onViewCreated: ((Radio) -> Unit)? = null,
) {
    AndroidView(
        factory = { context ->
            Radio(context).apply {
                setButtons(buttons)
                setLanguage(lang)
                setTheme(theme)
                setSelectedValue(value)
                title?.let { setTitle(it) }
                descriptionAbove?.let { setDescriptionAbove(it) }
                descriptionBelow?.let { setDescription(it) }
                setOnValueChangeListener { newValue -> onValueChange(newValue) }
                onViewCreated?.invoke(this)
            }
        },
        update = { radioView ->
            radioView.setButtons(buttons)
            radioView.setLanguage(lang)
            radioView.setTheme(theme)
            radioView.setSelectedValue(value)
            title?.let { radioView.setTitle(it) }
            descriptionAbove?.let { radioView.setDescriptionAbove(it) }
            descriptionBelow?.let { radioView.setDescription(it) }
        },
    )
}
