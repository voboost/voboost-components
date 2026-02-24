package ru.voboost.components.panel

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import ru.voboost.components.theme.Theme

/**
 * Panel component for Jetpack Compose.
 *
 * A customizable container with rounded corners and shadow.
 *
 * @param theme Theme enum value
 */
@Composable
fun Panel(theme: Theme) {
    AndroidView(
        factory = { context ->
            ru.voboost.components.panel.Panel(context).apply {
                setTheme(theme)
            }
        },
        update = { panelView ->
            panelView.setTheme(theme)
        },
    )
}
