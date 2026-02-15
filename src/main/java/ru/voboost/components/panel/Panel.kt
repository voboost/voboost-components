package ru.voboost.components.panel

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import ru.voboost.components.theme.Theme

/**
 * Panel component for Jetpack Compose.
 *
 * A customizable container with rounded corners and shadow.
 *
 * @param theme Theme identifier (e.g., "free-light", "dreamer-dark")
 * @param content Composable content to display inside the panel
 */
@Composable
fun Panel(
    theme: String,
    content: @Composable () -> Unit,
) {
    AndroidView(
        factory = { context ->
            ru.voboost.components.panel.Panel(context).apply {
                setTheme(Theme.fromValue(theme))
            }
        },
        update = { panelView ->
            panelView.setTheme(Theme.fromValue(theme))
        },
    )

    // Note: content parameter is not used in this implementation
    // as the Java Panel component handles its own content
}

