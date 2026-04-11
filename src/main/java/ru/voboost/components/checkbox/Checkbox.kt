package ru.voboost.components.checkbox

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import ru.voboost.components.i18n.Language
import ru.voboost.components.theme.Theme

/**
 * Checkbox component for Jetpack Compose.
 * Wraps Java LinearLayout container with optional label and description.
 *
 * @param theme Theme enum value
 * @param checked Current checked state
 * @param onCheckedChange Callback when checked state changes
 * @param enabled Whether the checkbox is interactive
 * @param label Optional label text (localized)
 * @param description Optional description text below (localized)
 * @param language Optional language for text rendering
 * @param onViewCreated Optional callback when the AndroidView is created
 */
@Composable
fun Checkbox(
    theme: Theme,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    label: Map<String, String>? = null,
    description: Map<String, String>? = null,
    language: Language? = null,
    onViewCreated: ((ru.voboost.components.checkbox.Checkbox) -> Unit)? = null,
) {
    AndroidView(
        factory = { context ->
            ru.voboost.components.checkbox.Checkbox(context).apply {
                setTheme(theme)
                setChecked(checked)
                isEnabled = enabled
                label?.let { setLabel(it) }
                description?.let { setDescription(it) }
                language?.let { setLanguage(it) }
                setOnCheckedChangeListener { isChecked -> onCheckedChange(isChecked) }
                onViewCreated?.invoke(this)
            }
        },
        update = { checkboxView ->
            checkboxView.setTheme(theme)
            checkboxView.isEnabled = enabled
            label?.let { checkboxView.setLabel(it) }
            description?.let { checkboxView.setDescription(it) }
            language?.let { checkboxView.setLanguage(it) }
            if (checkboxView.isChecked != checked) {
                checkboxView.setCheckedAnimated(checked)
            }
        },
    )
}
