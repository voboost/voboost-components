package ru.voboost.components.button

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.viewinterop.AndroidView
import ru.voboost.components.i18n.Language
import ru.voboost.components.theme.Theme

/**
 * Button component for Jetpack Compose.
 * Wraps Java LinearLayout container with optional description.
 *
 * @param text Button display text
 * @param style Visual style (PRIMARY or SECONDARY)
 * @param theme Theme enum value
 * @param enabled Whether the button is enabled
 * @param onClick Callback when button is clicked
 * @param description Optional description text to the right (localized)
 * @param lang Optional language for description rendering
 * @param onViewCreated Optional callback when the AndroidView is created
 */
@Composable
fun Button(
    text: String,
    style: ButtonStyle = ButtonStyle.PRIMARY,
    theme: Theme,
    enabled: Boolean = true,
    onClick: () -> Unit,
    description: Map<String, String>? = null,
    lang: Language? = null,
    paddingTop: Int? = null,
    paddingBottom: Int? = null,
    paddingLeft: Int? = null,
    paddingRight: Int? = null,
    onViewCreated: ((ru.voboost.components.button.Button) -> Unit)? = null,
) {
    val clickListener =
        remember(enabled) {
            android.view.View.OnClickListener { onClick() }
        }

    AndroidView(
        factory = { context ->
            ru.voboost.components.button.Button(context).apply {
                setText(text)
                setStyle(style)
                setTheme(theme)
                isEnabled = enabled
                description?.let { setDescription(it) }
                lang?.let { setLanguage(it) }
                if (
                    paddingTop != null ||
                    paddingBottom != null ||
                    paddingLeft != null ||
                    paddingRight != null
                ) {
                    padding(
                        paddingLeft ?: 0,
                        paddingTop ?: 0,
                        paddingRight ?: 0,
                        paddingBottom ?: 0,
                    )
                }
                setOnClickListener(clickListener)
                onViewCreated?.invoke(this)
            }
        },
        update = { buttonView ->
            buttonView.setText(text)
            buttonView.setStyle(style)
            buttonView.setTheme(theme)
            buttonView.isEnabled = enabled
            description?.let { buttonView.setDescription(it) }
            lang?.let { buttonView.setLanguage(it) }
            if (
                paddingTop != null ||
                paddingBottom != null ||
                paddingLeft != null ||
                paddingRight != null
            ) {
                buttonView.padding(
                    paddingLeft ?: 0,
                    paddingTop ?: 0,
                    paddingRight ?: 0,
                    paddingBottom ?: 0,
                )
            }
            // Note: clickListener doesn't change on recomposition
        },
    )
}
