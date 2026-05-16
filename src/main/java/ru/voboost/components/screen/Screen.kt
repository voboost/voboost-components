package ru.voboost.components.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView
import ru.voboost.components.panel.Panel
import ru.voboost.components.tabs.TabItem
import ru.voboost.components.tabs.Tabs
import ru.voboost.components.theme.Theme

/**
 * Screen component for Jetpack Compose.
 *
 * Full-screen container with tabs and panels. Automatically manages Tabs creation
 * and panel switching based on selectedTab state.
 *
 * @param tabs List of TabItem for sidebar navigation
 * @param panels Array of Panel components for each tab
 * @param selectedTab Currently selected tab value
 * @param onTabSelected Callback when tab selection changes
 * @param offsetX Horizontal offset in pixels (default: 175)
 * @param offsetY Vertical offset in pixels (default: 50)
 * @param gapX Horizontal gap between tabs and panels in pixels (default: 0)
 * @param screenLiftState Screen lift state (1=lowered, 2=raised, default: 2)
 * @param theme Theme enum value
 * @param onScreenLift Callback when screen lift state changes
 */
@Composable
fun Screen(
    tabs: List<TabItem>,
    panels: Array<Panel>,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    theme: Theme,
    offsetX: Int = 175,
    offsetY: Int = 50,
    gapX: Int = 0,
    screenLiftState: Int = 2,
    onScreenLift: ((Int) -> Unit)? = null,
) {
    // Track last selected tab to avoid redundant updates
    var lastSelectedTab by remember { mutableStateOf(selectedTab) }

    AndroidView(
        factory = { context ->
            ru.voboost.components.screen.Screen(context).apply {
                setTheme(theme)
                setOffsetX(offsetX)
                setOffsetY(offsetY)
                setGapX(gapX)

                // Create and configure Tabs
                val tabsView =
                    Tabs(context).apply {
                        setTheme(theme)
                        setItems(tabs)
                        setSelectedValue(selectedTab, false)
                        setOnValueChangeListener { newValue ->
                            onTabSelected(newValue)
                            lastSelectedTab = newValue
                        }
                    }
                setTabs(tabsView)

                // Set panels
                setPanels(panels)

                // Configure screen lift
                onScreenLift(screenLiftState)
                if (onScreenLift != null) {
                    setOnScreenLiftListener { onScreenLift(it) }
                }
            }
        },
        update = { screenView ->
            screenView.setTheme(theme)
            screenView.setOffsetX(offsetX)
            screenView.setOffsetY(offsetY)
            screenView.setGapX(gapX)

            // Update tabs
            screenView.getTabs()?.apply {
                setTheme(theme)
                setItems(tabs)

                if (selectedTab != lastSelectedTab) {
                    setSelectedValue(selectedTab, false)
                    lastSelectedTab = selectedTab
                }
            }

            // Update screen lift state
            screenView.onScreenLift(screenLiftState)
            if (onScreenLift != null) {
                screenView.setOnScreenLiftListener { onScreenLift(it) }
            }
        },
        onReset = { view ->
            // Cleanup listeners to prevent memory leaks
            view.getTabs()?.setOnValueChangeListener(null)
            view.setOnScreenLiftListener(null)
        },
    )
}
