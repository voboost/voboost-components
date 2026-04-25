package ru.voboost.components.toast

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import ru.voboost.components.theme.Theme

/**
 * Toast component for Jetpack Compose.
 *
 * Displays a notification message at the top of the screen with slide-in animation.
 *
 * @param theme the theme to apply
 * @param text the message text to display
 * @param duration auto-dismiss duration in milliseconds (use ToastTheme.DURATION_SHORT or DURATION_LONG)
 * @param showCloseButton whether to show the close button
 * @param modifier the modifier to apply to this layout
 */
@Composable
fun Toast(
    theme: Theme,
    text: String = "",
    duration: Long = ToastTheme.DURATION_SHORT,
    showCloseButton: Boolean = false,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            ru.voboost.components.toast.Toast(context).apply {
                applyConfig(theme, text, duration, showCloseButton)
            }
        },
        update = { toast ->
            toast.applyConfig(theme, text, duration, showCloseButton)
        },
    )
}

private fun ru.voboost.components.toast.Toast.applyConfig(
    theme: Theme,
    text: String,
    duration: Long,
    showCloseButton: Boolean,
) {
    setTheme(theme)
    setContent(text)
    setDuration(duration)
    setShowCloseButton(showCloseButton)
}
