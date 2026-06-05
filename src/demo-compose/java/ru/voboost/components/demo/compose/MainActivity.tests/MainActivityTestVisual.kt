package ru.voboost.components.demo.compose

import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.lifecycle.ViewModelProvider
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowLooper

/**
 * Visual regression tests for the Compose demo (Roborazzi), automotive 1920x720.
 * Scenarios are identical to demo-java and demo-kotlin. State is driven through
 * the ViewModel; settings radios and component-tab values are applied directly
 * on the underlying Java views to make the highlight match the rendered state.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w1920dp-h720dp-land-mdpi",
)
class MainActivityTestVisual {

    companion object {
        private const val SCREENSHOT_PATH =
            "java/ru/voboost/components/demo/compose/MainActivity.screenshots/"
    }

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

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

    private fun findAllRadios(view: View): List<ru.voboost.components.radio.Radio> {
        val result = mutableListOf<ru.voboost.components.radio.Radio>()
        if (view is ru.voboost.components.radio.Radio) result.add(view)
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                result.addAll(findAllRadios(view.getChildAt(i)))
            }
        }
        return result
    }

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

    private fun captureScreenshot(
        screenshotName: String,
        selectedTab: String = "settings",
        currentLanguage: String = "en",
        currentTheme: String = "dark",
        currentCarType: String = "free",
        screenLiftState: Int = 2,
        radioValue: String? = null,
    ) {
        val activity = composeTestRule.activity

        val viewModel = ViewModelProvider(activity)[DemoViewModel::class.java]
        viewModel.onTabSelected(selectedTab)
        viewModel.onLanguageChanged(currentLanguage)
        viewModel.onThemeChanged(currentTheme)
        viewModel.onCarTypeChanged(currentCarType)
        viewModel.onScreenLiftChanged(screenLiftState)

        composeTestRule.waitForIdle()
        ShadowLooper.idleMainLooper()

        val rootView = activity.findViewById<ViewGroup>(android.R.id.content)
        val screen = findScreen(rootView)
        if (screen != null) {
            screen.getTabs()?.setSelectedValue(selectedTab, false)

            val tabIndex = DemoTabs.TAB_VALUES.indexOf(selectedTab)
            val panels = screen.getPanels()
            if (tabIndex in panels.indices) {
                if (selectedTab == "settings") {
                    val radios = findAllRadios(panels[tabIndex])
                    if (radios.size >= 3) {
                        radios[0].setSelectedValue(currentLanguage, false)
                        radios[1].setSelectedValue(currentTheme, false)
                        radios[2].setSelectedValue(currentCarType, false)
                    }
                } else if (radioValue != null) {
                    findRadioInView(panels[tabIndex])?.setSelectedValue(radioValue, false)
                }
            }

            val combinedTheme = when (currentCarType) {
                "dreamer" -> if (currentTheme == "light") {
                    ru.voboost.components.theme.Theme.DREAMER_LIGHT
                } else {
                    ru.voboost.components.theme.Theme.DREAMER_DARK
                }
                else -> if (currentTheme == "light") {
                    ru.voboost.components.theme.Theme.FREE_LIGHT
                } else {
                    ru.voboost.components.theme.Theme.FREE_DARK
                }
            }
            screen.propagateTheme(combinedTheme)
            screen.propagateLanguage(ru.voboost.components.i18n.Language.valueOf(currentLanguage.uppercase()))
        }

        composeTestRule.waitForIdle()
        ShadowLooper.idleMainLooper()

        composeTestRule.onRoot().captureRoboImage("$SCREENSHOT_PATH$screenshotName.png")
    }

    // ========== STATE COMBINATIONS (settings tab) ==========

    @Test
    fun demo_compose_default() {
        captureScreenshot("demo_compose_default")
    }

    @Test
    fun demo_compose_russian() {
        captureScreenshot("demo_compose_russian", currentLanguage = "ru")
    }

    @Test
    fun demo_compose_light() {
        captureScreenshot("demo_compose_light", currentTheme = "light")
    }

    @Test
    fun demo_compose_dreamer() {
        captureScreenshot("demo_compose_dreamer", currentCarType = "dreamer")
    }

    @Test
    fun demo_compose_dreamer_light() {
        captureScreenshot("demo_compose_dreamer_light", currentTheme = "light", currentCarType = "dreamer")
    }

    @Test
    fun demo_compose_full_combination() {
        captureScreenshot(
            "demo_compose_full_combination",
            currentLanguage = "ru",
            currentTheme = "dark",
            currentCarType = "dreamer",
        )
    }

    @Test
    fun demo_compose_screen_lift_raised() {
        captureScreenshot(
            "demo_compose_screen_lift_raised",
            screenLiftState = ru.voboost.components.screen.Screen.SCREEN_RAISED,
        )
    }

    @Test
    fun demo_compose_screen_lift_lowered() {
        captureScreenshot(
            "demo_compose_screen_lift_lowered",
            screenLiftState = ru.voboost.components.screen.Screen.SCREEN_LOWERED,
        )
    }

    @Test
    fun demo_compose_component_hierarchy() {
        captureScreenshot("demo_compose_component_hierarchy")
    }

    // ========== PER-TAB SCREENSHOTS ==========

    @Test
    fun demo_compose_button() {
        captureScreenshot("demo_compose_button", selectedTab = "button")
    }

    @Test
    fun demo_compose_buttons() {
        captureScreenshot("demo_compose_buttons", selectedTab = "buttons")
    }

    @Test
    fun demo_compose_checkbox() {
        captureScreenshot("demo_compose_checkbox", selectedTab = "checkbox")
    }

    @Test
    fun demo_compose_radio() {
        captureScreenshot("demo_compose_radio", selectedTab = "radio")
    }

    @Test
    fun demo_compose_select() {
        captureScreenshot("demo_compose_select", selectedTab = "select")
    }

    @Test
    fun demo_compose_dialog() {
        captureScreenshot("demo_compose_dialog", selectedTab = "dialog")
    }

    @Test
    fun demo_compose_toast() {
        captureScreenshot("demo_compose_toast", selectedTab = "toast")
    }

    // ========== SCROLL TESTS ==========

    @Test
    fun demo_compose_tabs_scrolled_bottom() {
        val activity = composeTestRule.activity

        val rootView = activity.findViewById<ViewGroup>(android.R.id.content)
        val tabsScrollView = findScrollView(rootView)
        tabsScrollView?.fullScroll(ScrollView.FOCUS_DOWN)

        composeTestRule.waitForIdle()
        ShadowLooper.idleMainLooper()

        composeTestRule.onRoot().captureRoboImage("${SCREENSHOT_PATH}demo_compose_tabs_scrolled_bottom.png")
    }

    @Test
    fun demo_compose_radio_scrolled_bottom() {
        val activity = composeTestRule.activity

        val viewModel = ViewModelProvider(activity)[DemoViewModel::class.java]
        viewModel.onTabSelected("radio")
        composeTestRule.waitForIdle()
        ShadowLooper.idleMainLooper()

        val rootView = activity.findViewById<ViewGroup>(android.R.id.content)
        val screen = findScreen(rootView)
        if (screen != null) {
            val tabIndex = DemoTabs.TAB_VALUES.indexOf("radio")
            val panels = screen.getPanels()
            if (tabIndex in panels.indices) {
                findScrollView(panels[tabIndex])?.fullScroll(ScrollView.FOCUS_DOWN)
            }
        }

        composeTestRule.waitForIdle()
        ShadowLooper.idleMainLooper()

        composeTestRule.onRoot().captureRoboImage("${SCREENSHOT_PATH}demo_compose_radio_scrolled_bottom.png")
    }
}
