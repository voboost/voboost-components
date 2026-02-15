package ru.voboost.components.demo.compose

import android.view.View
import android.view.ViewGroup
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import ru.voboost.components.demo.shared.DemoContent
import ru.voboost.components.tabs.TabItem

/**
 * Visual regression tests for Compose Demo Application using Roborazzi.
 *
 * These tests generate screenshots of the full demo application screen,
 * with automotive screen configuration 1920x720.
 *
 * Note: After rewrite, demo-compose uses pure View approach (same as demo-kotlin).
 * Tests use Robolectric + View-based screenshot capture.
 *
 * Standard test scenarios:
 * 1. Default state - English, Light, Free, Language tab
 * 2. Russian language - Russian, Light, Free, Language tab
 * 3. Dark theme - English, Dark, Free, Theme tab
 * 4. Dreamer car type - English, Light, Dreamer, Car Type tab
 * 5. Full combination - Russian, Dark, Dreamer, Language tab
 * 6. Screen lift states
 * 7. Component hierarchy verification
 * 8. Individual tab tests
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

    @Test
    fun testMainActivityCreation() {
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        assertNotNull("MainActivity should be created successfully", activity)
        assertNotNull("Window should be available", activity.window)
        assertNotNull("DecorView should be available", activity.window.decorView)
    }

    @Test
    fun testMainActivityLifecycle() {
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        controller.pause()
        controller.resume()
        controller.stop()
        controller.restart()
        controller.start()
        controller.resume()

        assertNotNull("Activity should survive lifecycle changes", activity)
    }

    /**
     * Captures a screenshot of the activity with proper layout measurement.
     */
    private fun captureScreenshot(activity: MainActivity, screenshotName: String) {
        val contentView = activity.findViewById<ViewGroup>(android.R.id.content)
        val rootView = contentView.getChildAt(0)

        // Measure and layout the view
        rootView.measure(
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY)
        )
        rootView.layout(0, 0, 1920, 720)

        val screenshotPath = "$SCREENSHOT_PATH$screenshotName.png"
        rootView.captureRoboImage(screenshotPath, RoborazziOptions())
    }

    /**
     * Helper methods to set state
     */
    private fun setLanguage(activity: MainActivity, language: String) {
        activity.getDemoState().setCurrentLanguage(language)
        activity.getTabs().setSelectedValue("language")
        activity.getCurrentRadio()?.setSelectedValue(language, true)
    }

    private fun setTheme(activity: MainActivity, theme: String) {
        activity.getDemoState().setCurrentTheme(theme)
        activity.getTabs().setSelectedValue("theme")
        activity.getCurrentRadio()?.setSelectedValue(theme, true)
    }

    private fun setCarType(activity: MainActivity, carType: String) {
        activity.getDemoState().setCurrentCarType(carType)
        activity.getTabs().setSelectedValue("car_type")
        activity.getCurrentRadio()?.setSelectedValue(carType, true)
    }

    private fun setSelectedTab(activity: MainActivity, tabValue: String) {
        activity.getDemoState().setSelectedTab(tabValue)
        activity.getTabs().setSelectedValue(tabValue)
    }

    private fun setScreenLiftState(activity: MainActivity, state: Int) {
        activity.getDemoState().setScreenLiftState(state)
        activity.getScreen().onScreenLift(state)
    }

    // ========== STANDARD TEST SCENARIOS ==========

    /**
     * Test 1: Default state - English, Light, Free, Language tab
     */
    @Test
    fun demo_compose_default() {
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()
        captureScreenshot(activity, "demo_compose_default")
    }

    /**
     * Test 2: Russian language - Russian, Light, Free, Language tab
     */
    @Test
    fun demo_compose_russian() {
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        setLanguage(activity, "ru")
        captureScreenshot(activity, "demo_compose_russian")
    }

    /**
     * Test 3: Dark theme - English, Dark, Free, Theme tab
     */
    @Test
    fun demo_compose_dark() {
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        setTheme(activity, "dark")
        setSelectedTab(activity, "theme")
        captureScreenshot(activity, "demo_compose_dark")
    }

    /**
     * Test 4: Dreamer car type - English, Light, Dreamer, Car Type tab
     */
    @Test
    fun demo_compose_dreamer() {
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        setCarType(activity, "dreamer")
        setSelectedTab(activity, "car_type")
        captureScreenshot(activity, "demo_compose_dreamer")
    }

    /**
     * Test 5: Full combination - Russian, Dark, Dreamer, Language tab
     */
    @Test
    fun demo_compose_full_combination() {
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        setLanguage(activity, "ru")
        setTheme(activity, "dark")
        setCarType(activity, "dreamer")
        captureScreenshot(activity, "demo_compose_full_combination")
    }

    /**
     * Test 6: Screen Lift Raised
     */
    @Test
    fun demo_compose_screen_lift_raised() {
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        setScreenLiftState(activity, ru.voboost.components.screen.Screen.SCREEN_RAISED)
        captureScreenshot(activity, "demo_compose_screen_lift_raised")
    }

    /**
     * Test 7: Screen Lift Lowered
     */
    @Test
    fun demo_compose_screen_lift_lowered() {
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        setScreenLiftState(activity, ru.voboost.components.screen.Screen.SCREEN_LOWERED)
        captureScreenshot(activity, "demo_compose_screen_lift_lowered")
    }

    /**
     * Test 8: Component Hierarchy Verification
     */
    @Test
    fun demo_compose_component_hierarchy() {
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        // Verify component hierarchy exists
        assertNotNull("Tabs component should exist", activity.getTabs())
        assertNotNull("Current Section should exist", activity.getCurrentSection())
        assertNotNull("Current Radio should exist", activity.getCurrentRadio())
        assertNotNull("Screen component should exist", activity.getScreen())
        assertNotNull("Panel component should exist", activity.getPanel())
        assertNotNull("Demo State should exist", activity.getDemoState())

        captureScreenshot(activity, "demo_compose_component_hierarchy")
    }

    /**
     * Test 9: All tabs (disabled)
     */
    @Test
    @org.junit.Ignore
    fun demo_compose_all_tabs() {
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        val tabItems = DemoContent.getTabItems()
        val languages = listOf("en", "ru")
        val themes = listOf("free-light", "free-dark", "dreamer-light", "dreamer-dark")

        for (tabItem in tabItems) {
            val tabValue = tabItem.getValue()
            for (language in languages) {
                for (theme in themes) {
                    setLanguage(activity, language)
                    setTheme(activity, theme.split("-")[1])
                    if (theme.startsWith("dreamer")) {
                        setCarType(activity, "dreamer")
                    } else {
                        setCarType(activity, "free")
                    }
                    setSelectedTab(activity, tabValue)
                    val screenshotName = "demo_compose_${tabValue}_${language}_${theme}"
                    captureScreenshot(activity, screenshotName)
                }
            }
        }
    }

    /**
     * Test 10: Climate tab
     */
    @Test
    fun demo_compose_climate_tab() {
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        setSelectedTab(activity, "climate")

        activity.getCurrentRadio()?.setSelectedValue("manual", true)
        captureScreenshot(activity, "demo_compose_climate_manual")

        activity.getCurrentRadio()?.setSelectedValue("eco", true)
        captureScreenshot(activity, "demo_compose_climate_eco")
    }

    /**
     * Test 11: Audio tab
     */
    @Test
    fun demo_compose_audio_tab() {
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        setSelectedTab(activity, "audio")

        activity.getCurrentRadio()?.setSelectedValue("premium", true)
        captureScreenshot(activity, "demo_compose_audio_premium")

        activity.getCurrentRadio()?.setSelectedValue("surround", true)
        captureScreenshot(activity, "demo_compose_audio_surround")
    }

    /**
     * Test 12: Display tab
     */
    @Test
    fun demo_compose_display_tab() {
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        setSelectedTab(activity, "display")

        activity.getCurrentRadio()?.setSelectedValue("day", true)
        captureScreenshot(activity, "demo_compose_display_day")

        activity.getCurrentRadio()?.setSelectedValue("night", true)
        captureScreenshot(activity, "demo_compose_display_night")
    }

    /**
     * Test 13: System tab
     */
    @Test
    fun demo_compose_system_tab() {
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        setSelectedTab(activity, "system")

        activity.getCurrentRadio()?.setSelectedValue("performance", true)
        captureScreenshot(activity, "demo_compose_system_performance")

        activity.getCurrentRadio()?.setSelectedValue("eco", true)
        captureScreenshot(activity, "demo_compose_system_eco")
    }
}
