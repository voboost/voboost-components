package ru.voboost.components.select

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import ru.voboost.components.i18n.Language
import ru.voboost.components.theme.Theme

/**
 * Select component for Jetpack Compose.
 * Trigger button that opens a 3D WheelView picker popup.
 *
 * @param options List of SelectOption items
 * @param language Current display language
 * @param theme Theme enum value
 * @param value Currently selected value
 * @param onValueChange Callback when a new value is selected
 * @param onViewCreated Optional callback when the AndroidView is created
 */
@Composable
fun Select(
    options: List<SelectOption>,
    language: Language,
    theme: Theme,
    value: String,
    onValueChange: (String) -> Unit,
    onViewCreated: ((ru.voboost.components.select.Select) -> Unit)? = null,
) {
    // Memoize options to avoid unnecessary recomposition
    val memoizedOptions = remember(options) { options }

    AndroidView(
        factory = { context ->
            ru.voboost.components.select.Select(context).apply {
                configureSelect(
                    this,
                    memoizedOptions,
                    language,
                    theme,
                    value,
                    onValueChange,
                    onViewCreated,
                    true,
                )
            }
        },
        update = { selectView ->
            configureSelect(
                selectView,
                memoizedOptions,
                language,
                theme,
                value,
                onValueChange,
                onViewCreated,
                false,
            )
        },
    )
}

/**
 * Configures a Select component with the given parameters.
 * Internal helper to avoid code duplication between factory and update blocks.
 */
private fun configureSelect(
    select: ru.voboost.components.select.Select,
    options: List<SelectOption>,
    language: Language,
    theme: Theme,
    value: String,
    onValueChange: (String) -> Unit,
    onViewCreated: ((ru.voboost.components.select.Select) -> Unit)?,
    callOnViewCreated: Boolean,
) {
    select.apply {
        setOptions(options)
        setLanguage(language)
        setTheme(theme)
        if (getSelectedValue() != value) {
            setSelectedValue(value)
        }
        setOnValueChangeListener { newValue -> onValueChange(newValue) }
    }

    if (callOnViewCreated) {
        onViewCreated?.invoke(select)
    }
}
