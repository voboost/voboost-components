package ru.voboost.components.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import ru.voboost.components.panel.Panel
import ru.voboost.components.tabs.TabItem
import ru.voboost.components.tabs.Tabs
import ru.voboost.components.theme.Theme

/**
 * Screen component for Jetpack Compose.
 *
 * A full-screen container that can contain tabs and a panel.
 *
 * @param tabs List of TabItem objects for the sidebar navigation (optional)
 * @param panels Array of Panel components to display in the main content area (optional)
 * @param offsetX Horizontal offset for content positioning in pixels (default: 175)
 * @param offsetY Vertical offset for content positioning in pixels (default: 50)
 * @param gapX Horizontal gap between Tabs and Panel in pixels (default: 42)
 * @param screenLiftState Screen lift state (1 for lowered, 2 for raised, default: 2)
 * @param theme Theme enum value
 * @param onScreenLift Callback when screen lift state changes (optional)
 */
@Composable
fun Screen(
    tabs: List<TabItem>? = null,
    panels: Array<Panel>? = null,
    offsetX: Int = 175,
    offsetY: Int = 50,
    gapX: Int = 42,
    screenLiftState: Int = 2,
    theme: Theme,
    onScreenLift: ((Int) -> Unit)? = null,
) {
    AndroidView(
        factory = { context ->
            ru.voboost.components.screen.Screen(context).apply {
                setTheme(theme)
                setOffsetX(offsetX)
                setOffsetY(offsetY)
                setGapX(gapX)

                // Set tabs if provided
                if (tabs != null) {
                    val tabsView =
                        Tabs(context).apply {
                            setTheme(theme)
                            setItems(tabs)
                        }
                    setTabs(tabsView)
                }

                // Set panels if provided
                if (panels != null) {
                    setPanels(panels)
                }

                // Set screen lift state
                onScreenLift(screenLiftState)

                // Set screen lift listener if provided
                setOnScreenLiftListener { state ->
                    onScreenLift?.invoke(state)
                }
            }
        },
        update = { screenView ->
            screenView.setTheme(theme)
            screenView.setOffsetX(offsetX)
            screenView.setOffsetY(offsetY)
            screenView.setGapX(gapX)

            // Update tabs if provided
            if (tabs != null) {
                screenView.getTabs()?.apply {
                    setTheme(theme)
                    setItems(tabs)
                }
            } else {
                // Remove tabs if no longer provided
                screenView.setTabs(null)
            }

            // Update panels if provided
            if (panels != null) {
                screenView.setPanels(panels)
            }

            // Update screen lift state
            screenView.onScreenLift(screenLiftState)

            // Update screen lift listener
            screenView.setOnScreenLiftListener { state ->
                onScreenLift?.invoke(state)
            }
        },
    )
}
