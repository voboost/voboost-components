package ru.voboost.components.demo.kotlin

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import ru.voboost.components.radio.Radio as RadioView
import ru.voboost.components.radio.RadioButton
import ru.voboost.components.i18n.Language
import ru.voboost.components.theme.Theme
import ru.voboost.components.screen.Screen
import ru.voboost.components.panel.Panel
import ru.voboost.components.section.Section
import ru.voboost.components.tabs.Tabs
import ru.voboost.components.tabs.TabItem

import ru.voboost.components.demo.shared.DemoContent
import ru.voboost.components.demo.shared.DemoState
import ru.voboost.components.demo.shared.DemoHelpers

import android.util.Log

/**
 * Demo Kotlin Activity showcasing voboost-components proper component hierarchy in pure Kotlin projects.
 *
 * This demo demonstrates:
 * - Proper component hierarchy: Screen → Panel → Tabs → Section → Radio
 * - Pure Kotlin integration with voboost-components library
 * - Seamless Kotlin-Java interoperability with Java Custom View components
 * - Automotive-oriented layout (1920x720 resolution)
 * - Multi-language support (English/Russian) with reactive updates
 * - Theme switching (Light/Dark) with car type variants (Free/Dreamer)
 * - 7-tab structure with dynamic content
 * - Reactive state management across all components
 * - Kotlin-specific features: data classes, when expressions, extension functions, property delegates
 * - Comprehensive Android lifecycle management
 * - Advanced error handling with Kotlin null safety
 */
class MainActivity : Activity() {
    companion object {
        private const val TAG = "KotlinDemo"

        // Automotive display constants (in pixels, not dp!)
        private const val CONTAINER_PADDING_HORIZONTAL = 64
        private const val CONTAINER_PADDING_VERTICAL = 48
        private const val SECTION_SPACING = 24
        private const val TITLE_BOTTOM_MARGIN = 32
        private const val SECTION_TITLE_BOTTOM_MARGIN = 16
    }

    // UI Components with late initialization
    private lateinit var screen: Screen
    private lateinit var tabs: Tabs

    // Global State using DemoState from shared module
    private lateinit var demoState: DemoState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MainActivity onCreate started")

        try {
            // Initialize demo state
            demoState = DemoState()

            // Enable full-screen immersive mode for automotive display
            setupFullScreenMode()

            // Setup everything
            setup()

            // Initial update of all components
            updateAllComponents()
            Log.d(TAG, "MainActivity created successfully with proper component hierarchy")
        } catch (e: Exception) {
            Log.e(TAG, "Error during MainActivity creation", e)
            throw e
        }
    }

    /**
     * Sets up full-screen immersive mode to hide system navigation bar
     */
    @Suppress("DEPRECATION")
    private fun setupFullScreenMode() {
        // Hide system UI for automotive display
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

        // Keep screen on for automotive use
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    /**
     * Sets up the proper component hierarchy: Screen → Tabs → Panels
     */
    private fun setup() {
        // Create Screen component as root layout
        screen = Screen(this)
        setContentView(screen)

        // Create Tabs component
        tabs = Tabs(this)
        screen.setTabs(tabs)

        // Create all panels
        val panels = createAllPanels()
        screen.setPanels(panels)

        // Configure tabs with 7 tab items
        tabs.setItems(DemoContent.getTabItems())
        tabs.setTheme(Theme.fromValue(demoState.getCombinedTheme()))
        tabs.setLanguage(Language.fromCode(demoState.getCurrentLanguage()))

        // Set initial tab selection - this will trigger the listener and set active panel
        tabs.setSelectedValue(demoState.getSelectedTab(), false)

        // Set screen lift listener for component interaction
        screen.setOnScreenLiftListener { state ->
            Log.d(TAG, "Screen lift state changed to: $state")
            demoState.setScreenLiftState(state)
            updateAllComponents()
        }

        // Set tab selection listener
        tabs.setOnValueChangeListener { selectedTab ->
            Log.d(TAG, "Tab changed to: $selectedTab")
            demoState.setSelectedTab(selectedTab)
            updateAllComponents()
        }
    }

    /**
     * Creates all panels for all tabs
     */
    private fun createAllPanels(): Array<Panel> {
        return arrayOf(
            createPanelForTab("language"),
            createPanelForTab("theme"),
            createPanelForTab("car_type"),
            createPanelForTab("climate"),
            createPanelForTab("audio"),
            createPanelForTab("display"),
            createPanelForTab("system")
        )
    }

    /**
     * Creates a panel for a specific tab
     */
    private fun createPanelForTab(tabValue: String): Panel {
        // Create Panel
        val panel = Panel(this)

        // Create Section
        val section = Section(this).apply {
            setTitle(DemoContent.getSectionTitle(tabValue))
        }

        // Create Radio with options for this tab
        val radioButtons = DemoContent.getRadioButtons(tabValue)
        val radio = RadioView(this).apply {
            setButtons(radioButtons)
            setSelectedValue(demoState.getSelectedValueForTab(tabValue))
            setOnValueChangeListener { newValue ->
                Log.d(TAG, "Tab $tabValue value changed to: $newValue")
                demoState.setSelectedValueForTab(tabValue, newValue)

                // Special handling for language, theme, and car type tabs
                when (tabValue) {
                    "language" -> demoState.setCurrentLanguage(newValue)
                    "theme" -> demoState.setCurrentTheme(newValue)
                    "car_type" -> demoState.setCurrentCarType(newValue)
                }

                updateAllComponents()
            }
        }

        // Add Radio as child of Section (Section is now a ViewGroup)
        section.addView(radio)

        // Add Section to Panel
        panel.addView(section)

        return panel
    }

    /**
     * Updates all components with current state using Kotlin when expressions and safe calls
     * Implements reactive behavior across all components in the hierarchy
     */
    private fun updateAllComponents() {
        val combinedTheme = demoState.getCombinedTheme()

        Log.d(TAG, "Updating all components - Language: ${demoState.getCurrentLanguage()}, " +
                "Theme: $combinedTheme, Selected Tab: ${demoState.getSelectedTab()}")

        try {
            // Update background color based on theme
            updateBackgroundColor(combinedTheme)

            // Update Screen component
            screen.takeIf { ::screen.isInitialized }?.setTheme(Theme.fromValue(combinedTheme))

            // Update Tabs component
            tabs.takeIf { ::tabs.isInitialized }?.apply {
                setLanguage(Language.fromCode(demoState.getCurrentLanguage()))
                setTheme(Theme.fromValue(combinedTheme))
            }

            // Update all panels
            val panels = screen.takeIf { ::screen.isInitialized }?.getPanels()
            panels?.forEach { panel ->
                panel.setTheme(Theme.fromValue(combinedTheme))

                // Update child views in panel
                for (i in 0 until panel.childCount) {
                    val child = panel.getChildAt(i)
                    when (child) {
                        is Section -> {
                            child.setLanguage(Language.fromCode(demoState.getCurrentLanguage()))
                            child.setTheme(Theme.fromValue(combinedTheme))

                            // Update Radio inside Section (Radio is now a child of Section)
                            for (j in 0 until child.childCount) {
                                val sectionChild = child.getChildAt(j)
                                if (sectionChild is RadioView) {
                                    sectionChild.setLanguage(Language.fromCode(demoState.getCurrentLanguage()))
                                    sectionChild.setTheme(Theme.fromValue(combinedTheme))
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating components", e)
        }
    }

    /**
     * Updates the background color based on current theme
     */
    private fun updateBackgroundColor(combinedTheme: String) {
        val backgroundColor = if (combinedTheme.endsWith("-dark")) {
            Color.parseColor("#000000") // Black background for dark themes
        } else {
            Color.parseColor("#f1f5fb") // Light background for light themes
        }

        // Apply background color to screen
        screen.takeIf { ::screen.isInitialized }?.setBackgroundColor(backgroundColor)
    }


    // Getter methods for testing
    internal fun getTabs(): Tabs = tabs
    internal fun getScreen(): Screen = screen
    internal fun getDemoState(): DemoState = demoState
    internal fun getCurrentSection(): Section? {
        // Get the current panel based on selected tab
        val selectedTab = demoState.getSelectedTab()
        val panels = screen.getPanels()
        val tabIndex = when (selectedTab) {
            "language" -> 0
            "theme" -> 1
            "car_type" -> 2
            "climate" -> 3
            "audio" -> 4
            "display" -> 5
            "system" -> 6
            else -> 0
        }

        // Get the section from the current panel
        return if (tabIndex < panels.size) {
            val panel = panels[tabIndex]
            if (panel.childCount > 0) {
                val firstChild = panel.getChildAt(0)
                if (firstChild is Section) firstChild else null
            } else null
        } else null
    }

    internal fun getCurrentRadio(): RadioView? {
        // Get the current section first
        val section = getCurrentSection()
        if (section == null) return null

        // Radio is now a child of Section
        for (i in 0 until section.childCount) {
            val child = section.getChildAt(i)
            if (child is RadioView) {
                return child
            }
        }
        return null
    }

    internal fun getPanel(): Panel? {
        // Get the current panel based on selected tab
        val selectedTab = demoState.getSelectedTab()
        val panels = screen.getPanels()
        val tabIndex = when (selectedTab) {
            "language" -> 0
            "theme" -> 1
            "car_type" -> 2
            "climate" -> 3
            "audio" -> 4
            "display" -> 5
            "system" -> 6
            else -> 0
        }

        return if (tabIndex < panels.size) panels[tabIndex] else null
    }

    /**
     * Android lifecycle methods with enhanced logging
     */
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "MainActivity resumed with component hierarchy - Current state: ${demoState.toString()}")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "MainActivity paused - Current state: ${demoState.toString()}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MainActivity destroyed")
    }
}
