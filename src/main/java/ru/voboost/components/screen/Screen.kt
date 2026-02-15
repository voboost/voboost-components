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
 * @param panel Panel component to display in the main content area (optional)
 * @param offsetX Horizontal offset for content positioning in pixels (default: 175)
 * @param offsetY Vertical offset for content positioning in pixels (default: 50)
 * @param gapX Horizontal gap between Tabs and Panel in pixels (default: 42)
 * @param screenLiftState Screen lift state (1 for lowered, 2 for raised, default: 2)
 * @param theme Theme identifier (e.g., "free-light", "dreamer-dark")
 * @param onScreenLift Callback when screen lift state changes (optional)
 * @param content Composable content to display inside the screen (optional)
 */
@Composable
fun Screen(
    tabs: List<TabItem>? = null,
    panels: Array<Panel>? = null,
    offsetX: Int = 175,
    offsetY: Int = 50,
    gapX: Int = 42,
    screenLiftState: Int = 2,
    theme: String,
    onScreenLift: ((Int) -> Unit)? = null,
    content: @Composable () -> Unit = {},
) {
    AndroidView(
        factory = { context ->
            ru.voboost.components.screen.Screen(context).apply {
                setTheme(Theme.fromValue(theme))
                setOffsetX(offsetX)
                setOffsetY(offsetY)
                setGapX(gapX)

                // Set tabs if provided
                if (tabs != null) {
                    val tabsView =
                        Tabs(context)
                            .apply {
                                setTheme(Theme.fromValue(theme))
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
            screenView.setTheme(Theme.fromValue(theme))
            screenView.setOffsetX(offsetX)
            screenView.setOffsetY(offsetY)
            screenView.setGapX(gapX)

            // Update tabs if provided
            if (tabs != null) {
                val tabsView = screenView.getTabs()
                if (tabsView != null) {
                    tabsView.setTheme(Theme.fromValue(theme))
                    tabsView.setItems(tabs)
                } else {
                    // Create new tabs if not already set
                    val newTabsView =
                        Tabs(screenView.context)
                            .apply {
                                setTheme(Theme.fromValue(theme))
                                setItems(tabs)
                            }
                    screenView.setTabs(newTabsView)
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

    // Note: content parameter is not used in this implementation
    // as the Java Screen component handles its own content
}

