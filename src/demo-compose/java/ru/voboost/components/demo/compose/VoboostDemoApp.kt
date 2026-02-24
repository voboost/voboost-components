package ru.voboost.components.demo.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.voboost.components.demo.shared.DemoContent

/**
 * Main Composable function for the Voboost Demo application.
 * Uses ViewModel for state management following Compose best practices.
 *
 * This is the stateful composable that manages state through ViewModel.
 */
@Composable
fun VoboostDemoApp(viewModel: DemoViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    // Cleanup when the composable leaves the composition
    DisposableEffect(Unit) {
        onDispose {
            // Cleanup any resources if needed
            // Currently no resources need explicit cleanup
        }
    }

    VoboostDemoContent(
        selectedTab = uiState.selectedTab,
        combinedTheme = uiState.combinedTheme,
        currentLanguage = uiState.currentLanguage,
        currentTheme = uiState.currentTheme,
        currentCarType = uiState.currentCarType,
        screenLiftState = uiState.screenLiftState,
        onTabSelected = viewModel::onTabSelected,
        onScreenLift = viewModel::onScreenLiftChanged,
        onValueChange = viewModel::onValueChange,
    )
}

/**
 * Stateless content composable for the Voboost Demo application.
 * Pure UI component that receives all state and callbacks from parent.
 *
 * @param selectedTab Currently selected tab value
 * @param combinedTheme Combined theme string (e.g., "free-light")
 * @param currentLanguage Current language code
 * @param currentTheme Current theme value
 * @param currentCarType Current car type value
 * @param screenLiftState Current screen lift state
 * @param onTabSelected Callback when tab selection changes
 * @param onScreenLift Callback when screen lift state changes
 * @param onValueChange Callback when a tab's value changes
 */
@Composable
fun VoboostDemoContent(
    selectedTab: String,
    combinedTheme: String,
    currentLanguage: String,
    currentTheme: String,
    currentCarType: String,
    screenLiftState: Int,
    onTabSelected: (String) -> Unit,
    onScreenLift: (Int) -> Unit,
    onValueChange: (String, String) -> Unit,
) {
    // Recomposition tracking for development
    if (BuildConfig.DEBUG) {
        SideEffect {
            android.util.Log.d("Recomposition", "VoboostDemoContent recomposed")
        }
    }

    // Background color based on theme - simple conditional, no remember needed for Color
    val isDarkTheme = currentTheme == "dark"
    val backgroundColor = if (isDarkTheme) DemoColors.DarkBackground else DemoColors.LightBackground

    // Root container with background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .semantics {
                testTag = "demo_root"
                contentDescription = "Voboost Components Demo"
            }
    ) {
        // Use the Screen Compose wrapper from the library
        ScreenWrapper(
            selectedTab = selectedTab,
            combinedTheme = combinedTheme,
            currentLanguage = currentLanguage,
            screenLiftState = screenLiftState,
            onTabSelected = onTabSelected,
            onScreenLift = onScreenLift,
            onValueChange = onValueChange,
        )
    }
}

/**
 * Wrapper for the Screen component using AndroidView.
 * This demonstrates how to use the Java Custom View in Compose.
 *
 * @param selectedTab Currently selected tab value
 * @param combinedTheme Combined theme string (e.g., "free-light")
 * @param currentLanguage Current language code
 * @param screenLiftState Current screen lift state
 * @param onTabSelected Callback when tab selection changes
 * @param onScreenLift Callback when screen lift state changes
 * @param onValueChange Callback when a tab's value changes
 */
@Composable
fun ScreenWrapper(
    selectedTab: String,
    combinedTheme: String,
    currentLanguage: String,
    screenLiftState: Int,
    onTabSelected: (String) -> Unit,
    onScreenLift: (Int) -> Unit,
    onValueChange: (String, String) -> Unit,
) {
    // Use tab configurations directly - it's already a static constant
    val tabConfigs = DemoTabs.TAB_VALUES

    // Cache Theme and Language enums to avoid recreation on every recomposition
    val theme = remember(combinedTheme) { ru.voboost.components.theme.Theme.fromValue(combinedTheme) }
    val language = remember(currentLanguage) { ru.voboost.components.i18n.Language.fromCode(currentLanguage) }

    // Use rememberUpdatedState to keep callbacks fresh without recreating the view
    val currentOnTabSelected by rememberUpdatedState(onTabSelected)
    val currentOnScreenLift by rememberUpdatedState(onScreenLift)
    val currentOnValueChange by rememberUpdatedState(onValueChange)

    AndroidView(
        modifier = Modifier.semantics { testTag = "screen_wrapper" },
        factory = { context ->
            // Create Screen component
            val screen = ru.voboost.components.screen.Screen(context)

            // Create Tabs component
            val tabs = ru.voboost.components.tabs.Tabs(context).apply {
                setTheme(theme)
                setLanguage(language)
                setItems(DemoContent.getTabItems())
                setSelectedValue(selectedTab, false)
                setOnValueChangeListener { newTab ->
                    currentOnTabSelected(newTab)
                }
            }
            screen.setTabs(tabs)

            // Create panels for all tabs
            val panels = tabConfigs.map { tabValue ->
                createPanelForTab(
                    context = context,
                    tabValue = tabValue,
                    theme = theme,
                    language = language,
                    onValueChange = currentOnValueChange
                )
            }.toTypedArray()

            screen.setPanels(panels)

            // Set screen theme
            screen.setTheme(theme)

            // Set screen lift listener
            screen.setOnScreenLiftListener { state ->
                currentOnScreenLift(state)
            }

            screen
        },
        update = { view ->
            // Update screen theme
            view.setTheme(theme)

            // Update tabs
            view.getTabs()?.apply {
                setTheme(theme)
                setLanguage(language)
                setSelectedValue(selectedTab, false)
            }

            // Propagate theme and language to all child components
            view.propagateTheme(theme)
            view.propagateLanguage(language)
        },
        onReset = { view ->
            // Cleanup listeners to prevent memory leaks
            view.getTabs()?.setOnValueChangeListener(null)
            view.setOnScreenLiftListener(null)
        }
    )
}

/**
 * Creates a Panel for a specific tab with Section and Radio components.
 *
 * @param context Android context
 * @param tabValue The tab value identifier
 * @param theme Theme enum value
 * @param language Language enum value
 * @param onValueChange Callback when value changes
 * @return Configured Panel instance
 */
private fun createPanelForTab(
    context: android.content.Context,
    tabValue: String,
    theme: ru.voboost.components.theme.Theme,
    language: ru.voboost.components.i18n.Language,
    onValueChange: (String, String) -> Unit
): ru.voboost.components.panel.Panel {
    if (tabValue == "climate") {
        return createClimatePanelWithMultipleRadios(context, theme, language, onValueChange)
    }

    val panel = ru.voboost.components.panel.Panel(context)

    val section = ru.voboost.components.section.Section(context).apply {
        setTheme(theme)
        setLanguage(language)
        setTitle(DemoContent.getSectionTitle(tabValue))
    }

    val radio = ru.voboost.components.radio.Radio(context).apply {
        setTheme(theme)
        setLanguage(language)
        setButtons(DemoContent.getRadioButtons(tabValue))
        // Set initial value based on current state
        setSelectedValue(DemoContent.getDefaultValue(tabValue))
        setOnValueChangeListener { newValue ->
            onValueChange(tabValue, newValue)
        }
    }

    section.addView(radio)
    panel.addView(section)

    return panel
}

/**
 * Creates the climate panel with multiple sections and radio groups.
 * 5 sections × ~283px each = ~1415px total, overflowing the ~670px panel.
 *
 * @param context Android context
 * @param theme Theme enum value
 * @param language Language enum value
 * @param onValueChange Callback when value changes
 * @return Configured Panel instance with ScrollView
 */
private fun createClimatePanelWithMultipleRadios(
    context: android.content.Context,
    theme: ru.voboost.components.theme.Theme,
    language: ru.voboost.components.i18n.Language,
    onValueChange: (String, String) -> Unit
): ru.voboost.components.panel.Panel {
    val panel = ru.voboost.components.panel.Panel(context)

    val scrollView = android.widget.ScrollView(context).apply {
        isVerticalScrollBarEnabled = false
        overScrollMode = android.view.View.OVER_SCROLL_NEVER
    }

    val contentLayout = android.widget.LinearLayout(context).apply {
        orientation = android.widget.LinearLayout.VERTICAL
    }

    val sectionCount = DemoContent.getClimateSectionCount()

    for (i in 0 until sectionCount) {
        val section = ru.voboost.components.section.Section(context).apply {
            setTheme(theme)
            setLanguage(language)
            setTitle(DemoContent.getClimateSectionTitle(i))
        }

        val radio = ru.voboost.components.radio.Radio(context).apply {
            setTheme(theme)
            setLanguage(language)
            setButtons(DemoContent.getClimateSubRadioButtons(i))
            setSelectedValue(DemoContent.getClimateSubDefaultValue(i))
            setOnValueChangeListener { newValue ->
                onValueChange("climate_$i", newValue)
            }
        }

        section.addView(radio)
        contentLayout.addView(section)
    }

    scrollView.addView(contentLayout)
    panel.addView(scrollView)

    return panel
}
