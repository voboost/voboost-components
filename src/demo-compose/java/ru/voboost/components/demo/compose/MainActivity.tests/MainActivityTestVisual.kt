package ru.voboost.components.demo.compose

import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.lifecycle.ViewModelProvider
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowLooper

// Tab values for demo application
private val TAB_VALUES = listOf(
    "language",
    "theme",
    "car_type",
    "climate",
    "audio",
    "display",
    "system"
)

/**
 * Visual regression tests for Compose Demo Application using Roborazzi.
 *
 * These tests generate screenshots of the full demo application screen,
 * with automotive screen configuration 1920x720.
 *
 * Non-scroll tests use captureRoboImage with VoboostDemoContent composable
 * and explicit state parameters. Scroll tests use createComposeRule to render
 * the content, then access the underlying ScrollView for synchronous scrolling.
 *
 * Standard test scenarios match demo-java and demo-kotlin:
 * 1. Default state - English, Dark, Free, Language tab
 * 2. Russian language
 * 3. Light theme (switched from default dark)
 * 4. Dreamer car type
 * 5. Full combination - Russian, Light, Dreamer
 * 6. Screen lift raised/lowered
 * 7. Component hierarchy
 * 8. Climate tab with different values
 * 9. Audio tab with different values
 * 10. Display tab with different values
 * 11. System tab with different values
 * 12. Tabs scrolled to bottom
 * 13. Climate panel scrolled to bottom
 * 14. Climate panel top view
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w1920dp-h720dp-land-mdpi"
)
class MainActivityTestVisual {

    companion object {
        private const val SCREENSHOT_PATH =
            "java/ru/voboost/components/demo/compose/MainActivity.screenshots/"
    }

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /**
     * Recursively finds the first instance of a given type in a view hierarchy.
     */
    private fun findScreen(view: View): ru.voboost.components.screen.Screen? {
        if (view is ru.voboost.components.screen.Screen) return view
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val found = findScreen(view.getChildAt(i))
                if (found != null) return found
            }
        }
        return null
    }

    private fun findRadioInView(view: View): ru.voboost.components.radio.Radio? {
        if (view is ru.voboost.components.radio.Radio) return view
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val found = findRadioInView(view.getChildAt(i))
                if (found != null) return found
            }
        }
        return null
    }

    /**
     * Captures a screenshot of the actual activity with given state parameters.
     * Uses ViewModel to change state and directly manipulates Java Custom Views.
     */
    private fun captureScreenshot(
        screenshotName: String,
        selectedTab: String = "language",
        currentLanguage: String = "en",
        currentTheme: String = "dark",
        currentCarType: String = "free",
        screenLiftState: Int = 2,
        radioValue: String? = null,
    ) {
        val activity = composeTestRule.activity

        // Step 1: Apply state changes through ViewModel (triggers recomposition)
        val viewModel = ViewModelProvider(activity)[DemoViewModel::class.java]
        viewModel.onTabSelected(selectedTab)
        viewModel.onLanguageChanged(currentLanguage)
        viewModel.onThemeChanged(currentTheme)
        viewModel.onCarTypeChanged(currentCarType)
        viewModel.onScreenLiftChanged(screenLiftState)

        // Step 2: Wait for Compose recomposition to propagate to AndroidView
        composeTestRule.waitForIdle()
        ShadowLooper.idleMainLooper()

        // Step 3: Directly update Radio selected values in the Java view hierarchy
        val rootView = activity.findViewById<ViewGroup>(android.R.id.content)
        val screen = findScreen(rootView)
        if (screen != null) {
            // Update Tabs selected value
            screen.getTabs()?.setSelectedValue(selectedTab, false)

            // Find and update the Radio in the current panel
            val tabIndex = TAB_VALUES.indexOf(selectedTab)
            val panels = screen.getPanels()
            if (tabIndex >= 0 && tabIndex < panels.size) {
                val expectedValue = radioValue ?: when (selectedTab) {
                    "language" -> currentLanguage
                    "theme" -> currentTheme
                    "car_type" -> currentCarType
                    else -> null
                }
                if (expectedValue != null) {
                    findRadioInView(panels[tabIndex])?.setSelectedValue(expectedValue, false)
                }
            }

            // Propagate theme and language to ensure proper rendering
            val combinedTheme = when (currentCarType) {
                "dreamer" -> if (currentTheme == "light") ru.voboost.components.theme.Theme.DREAMER_LIGHT else ru.voboost.components.theme.Theme.DREAMER_DARK
                else -> if (currentTheme == "light") ru.voboost.components.theme.Theme.FREE_LIGHT else ru.voboost.components.theme.Theme.FREE_DARK
            }
            screen.propagateTheme(combinedTheme)
            screen.propagateLanguage(ru.voboost.components.i18n.Language.valueOf(currentLanguage.uppercase()))
        }

        // Step 4: Wait again for any pending updates
        composeTestRule.waitForIdle()
        ShadowLooper.idleMainLooper()

        // Step 5: Capture the actual rendered activity view
        val screenshotPath = "$SCREENSHOT_PATH$screenshotName.png"
        composeTestRule.onRoot().captureRoboImage(screenshotPath)
    }

    // ========== STANDARD TEST SCENARIOS ==========

    /**
     * Test 1: Default state - English, Dark, Free, Language tab
     */
    @Test
    fun demo_compose_default() {
        captureScreenshot("demo_compose_default")
    }

    /**
     * Test 2: Russian language - Russian, Dark, Free, Language tab
     */
    @Test
    fun demo_compose_russian() {
        captureScreenshot(
            "demo_compose_russian",
            currentLanguage = "ru",
        )
    }

    /**
     * Test 3: Light theme - English, Light, Free, Theme tab
     *
     * Note: Default is dark, so this tests switching TO light.
     */
    @Test
    fun demo_compose_light() {
        captureScreenshot(
            "demo_compose_light",
            selectedTab = "theme",
            currentTheme = "light",
        )
    }

    /**
     * Test 4: Dreamer car type - English, Dark, Dreamer, Car Type tab
     */
    @Test
    fun demo_compose_dreamer() {
        captureScreenshot(
            "demo_compose_dreamer",
            selectedTab = "car_type",
            currentCarType = "dreamer",
        )
    }

    /**
     * Test 5: Dreamer Light theme - English, Light, Dreamer, Theme tab
     */
    @Test
    fun demo_compose_dreamer_light() {
        captureScreenshot(
            "demo_compose_dreamer_light",
            selectedTab = "theme",
            currentTheme = "light",
            currentCarType = "dreamer",
        )
    }

    /**
     * Test 6: Full combination - Russian, Dark, Dreamer, Language tab
     */
    @Test
    fun demo_compose_full_combination() {
        captureScreenshot(
            "demo_compose_full_combination",
            currentLanguage = "ru",
            currentTheme = "dark",
            currentCarType = "dreamer",
        )
    }

    /**
     * Test 7: Screen Lift Raised
     */
    @Test
    fun demo_compose_screen_lift_raised() {
        captureScreenshot(
            "demo_compose_screen_lift_raised",
            screenLiftState = ru.voboost.components.screen.Screen.SCREEN_RAISED,
        )
    }

    /**
     * Test 8: Screen Lift Lowered
     */
    @Test
    fun demo_compose_screen_lift_lowered() {
        captureScreenshot(
            "demo_compose_screen_lift_lowered",
            screenLiftState = ru.voboost.components.screen.Screen.SCREEN_LOWERED,
        )
    }

    /**
     * Test 9: Component Hierarchy Verification
     */
    @Test
    fun demo_compose_component_hierarchy() {
        captureScreenshot("demo_compose_component_hierarchy")
    }

    // ========== TAB-SPECIFIC TESTS ==========

    /**
     * Test 10: Climate tab - manual value
     */
    @Test
    fun demo_compose_climate_manual() {
        captureScreenshot(
            "demo_compose_climate_manual",
            selectedTab = "climate",
            radioValue = "manual",
        )
    }

    /**
     * Test 11: Climate tab - eco value
     */
    @Test
    fun demo_compose_climate_eco() {
        captureScreenshot(
            "demo_compose_climate_eco",
            selectedTab = "climate",
            radioValue = "eco",
        )
    }

    /**
     * Test 12: Audio tab - premium value
     */
    @Test
    fun demo_compose_audio_premium() {
        captureScreenshot(
            "demo_compose_audio_premium",
            selectedTab = "audio",
            radioValue = "premium",
        )
    }

    /**
     * Test 13: Audio tab - surround value
     */
    @Test
    fun demo_compose_audio_surround() {
        captureScreenshot(
            "demo_compose_audio_surround",
            selectedTab = "audio",
            radioValue = "surround",
        )
    }

    /**
     * Test 14: Display tab - day value
     */
    @Test
    fun demo_compose_display_day() {
        captureScreenshot(
            "demo_compose_display_day",
            selectedTab = "display",
            radioValue = "day",
        )
    }

    /**
     * Test 15: Display tab - night value
     */
    @Test
    fun demo_compose_display_night() {
        captureScreenshot(
            "demo_compose_display_night",
            selectedTab = "display",
            radioValue = "night",
        )
    }

    /**
     * Test 16: System tab - performance value
     */
    @Test
    fun demo_compose_system_performance() {
        captureScreenshot(
            "demo_compose_system_performance",
            selectedTab = "system",
            radioValue = "performance",
        )
    }

    /**
     * Test 17: System tab - eco value
     */
    @Test
    fun demo_compose_system_eco() {
        captureScreenshot(
            "demo_compose_system_eco",
            selectedTab = "system",
            radioValue = "eco",
        )
    }

    // ========== SCROLL TESTS ==========

    /**
     * Helper: Finds the first ScrollView in a view hierarchy recursively.
     */
    private fun findScrollView(view: View): ScrollView? {
        if (view is ScrollView) return view
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val found = findScrollView(view.getChildAt(i))
                if (found != null) return found
            }
        }
        return null
    }

    /**
     * Helper: Finds all ScrollViews in a view hierarchy recursively.
     */
    private fun findAllScrollViews(view: View): List<ScrollView> {
        val result = mutableListOf<ScrollView>()
        if (view is ScrollView) result.add(view)
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                result.addAll(findAllScrollViews(view.getChildAt(i)))
            }
        }
        return result
    }

    /**
     * Test 18: Tabs scrolled to bottom
     */
    @Test
    fun demo_compose_tabs_scrolled_bottom() {
        val activity = composeTestRule.activity

        // Find and scroll tabs to bottom
        val rootView = activity.findViewById<ViewGroup>(android.R.id.content)
        val tabsScrollView = findScrollView(rootView)
        if (tabsScrollView != null) {
            tabsScrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }

        composeTestRule.waitForIdle()
        ShadowLooper.idleMainLooper()

        val screenshotPath = "$SCREENSHOT_PATH${"demo_compose_tabs_scrolled_bottom"}.png"
        composeTestRule.onRoot().captureRoboImage(screenshotPath)
    }

    /**
     * Test 19: Climate panel scrolled to bottom
     */
    @Test
    fun demo_compose_climate_scrolled_bottom() {
        val activity = composeTestRule.activity

        // Navigate to climate tab
        val viewModel = ViewModelProvider(activity)[DemoViewModel::class.java]
        viewModel.onTabSelected("climate")
        composeTestRule.waitForIdle()
        ShadowLooper.idleMainLooper()

        // Find and scroll climate panel to bottom
        val rootView = activity.findViewById<ViewGroup>(android.R.id.content)
        val screen = findScreen(rootView)
        if (screen != null) {
            val tabIndex = TAB_VALUES.indexOf("climate")
            val panels = screen.getPanels()
            if (tabIndex >= 0 && tabIndex < panels.size) {
                val climateScrollView = findScrollView(panels[tabIndex])
                if (climateScrollView != null) {
                    climateScrollView.fullScroll(ScrollView.FOCUS_DOWN)
                }
            }
        }

        composeTestRule.waitForIdle()
        ShadowLooper.idleMainLooper()

        val screenshotPath = "$SCREENSHOT_PATH${"demo_compose_climate_scrolled_bottom"}.png"
        composeTestRule.onRoot().captureRoboImage(screenshotPath)
    }

    /**
     * Test 20: Climate panel default view (top)
     */
    @Test
    fun demo_compose_climate_top() {
        captureScreenshot(
            "demo_compose_climate_top",
            selectedTab = "climate",
        )
    }
}
