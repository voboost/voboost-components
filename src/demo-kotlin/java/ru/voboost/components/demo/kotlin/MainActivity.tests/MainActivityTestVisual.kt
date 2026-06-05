package ru.voboost.components.demo.kotlin

import android.view.View
import android.widget.ScrollView
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowLooper

/**
 * Visual regression tests for the Kotlin demo (Roborazzi), automotive 1920x720.
 * Scenarios are identical to demo-java and demo-compose.
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
            "java/ru/voboost/components/demo/kotlin/MainActivity.screenshots/"

        // Index of the radio tab inside the 8-tab model
        private const val RADIO_TAB_INDEX = 4
    }

    @Test
    fun testMainActivityCreation() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
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

    private fun captureScreenshot(activity: MainActivity, screenshotName: String) {
        val rootView = activity.getScreen()
        assertNotNull("Screen should not be null", rootView)

        rootView.measure(
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY),
        )
        rootView.layout(0, 0, 1920, 720)

        rootView.captureRoboImage("$SCREENSHOT_PATH$screenshotName.png", RoborazziOptions())
    }

    private fun selectTab(activity: MainActivity, tabValue: String) {
        activity.getDemoState().selectedTab = tabValue
        activity.getTabs().setSelectedValue(tabValue)
    }

    private fun setScreenLiftState(activity: MainActivity, state: Int) {
        activity.getDemoState().screenLiftState = state
        activity.getScreen().onScreenLift(state)
    }

    // ========== STATE COMBINATIONS (settings tab) ==========

    @Test
    fun demo_kotlin_default() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        captureScreenshot(activity, "demo_kotlin_default")
    }

    @Test
    fun demo_kotlin_russian() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        activity.applyState("ru", "dark", "free")
        captureScreenshot(activity, "demo_kotlin_russian")
    }

    @Test
    fun demo_kotlin_light() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        activity.applyState("en", "light", "free")
        captureScreenshot(activity, "demo_kotlin_light")
    }

    @Test
    fun demo_kotlin_dreamer() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        activity.applyState("en", "dark", "dreamer")
        captureScreenshot(activity, "demo_kotlin_dreamer")
    }

    @Test
    fun demo_kotlin_dreamer_light() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        activity.applyState("en", "light", "dreamer")
        captureScreenshot(activity, "demo_kotlin_dreamer_light")
    }

    @Test
    fun demo_kotlin_full_combination() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        activity.applyState("ru", "dark", "dreamer")
        captureScreenshot(activity, "demo_kotlin_full_combination")
    }

    @Test
    fun demo_kotlin_screen_lift_raised() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        setScreenLiftState(activity, ru.voboost.components.screen.Screen.SCREEN_RAISED)
        captureScreenshot(activity, "demo_kotlin_screen_lift_raised")
    }

    @Test
    fun demo_kotlin_screen_lift_lowered() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        setScreenLiftState(activity, ru.voboost.components.screen.Screen.SCREEN_LOWERED)
        captureScreenshot(activity, "demo_kotlin_screen_lift_lowered")
    }

    @Test
    fun demo_kotlin_component_hierarchy() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()

        assertNotNull("Screen component should exist", activity.getScreen())
        assertNotNull("Tabs component should exist", activity.getTabs())
        assertNotNull("Demo State should exist", activity.getDemoState())

        captureScreenshot(activity, "demo_kotlin_component_hierarchy")
    }

    // ========== PER-TAB SCREENSHOTS ==========

    @Test
    fun demo_kotlin_button() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        selectTab(activity, "button")
        captureScreenshot(activity, "demo_kotlin_button")
    }

    @Test
    fun demo_kotlin_buttons() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        selectTab(activity, "buttons")
        captureScreenshot(activity, "demo_kotlin_buttons")
    }

    @Test
    fun demo_kotlin_checkbox() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        selectTab(activity, "checkbox")
        captureScreenshot(activity, "demo_kotlin_checkbox")
    }

    @Test
    fun demo_kotlin_radio() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        selectTab(activity, "radio")
        captureScreenshot(activity, "demo_kotlin_radio")
    }

    @Test
    fun demo_kotlin_select() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        selectTab(activity, "select")
        captureScreenshot(activity, "demo_kotlin_select")
    }

    @Test
    fun demo_kotlin_dialog() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        selectTab(activity, "dialog")
        captureScreenshot(activity, "demo_kotlin_dialog")
    }

    @Test
    fun demo_kotlin_toast() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        selectTab(activity, "toast")
        captureScreenshot(activity, "demo_kotlin_toast")
    }

    // ========== SCROLL TESTS ==========

    @Test
    fun demo_kotlin_tabs_scrolled_bottom() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()

        val tabsScrollView = activity.getScreen().getTabsScrollView()
        assertNotNull("Tabs ScrollView should exist", tabsScrollView)

        val rootView = activity.getScreen()
        rootView.measure(
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY),
        )
        rootView.layout(0, 0, 1920, 720)

        val scrollChild = tabsScrollView!!.getChildAt(0)
        val scrollRange = scrollChild.measuredHeight - tabsScrollView.measuredHeight
        if (scrollRange > 0) {
            tabsScrollView.scrollTo(0, scrollRange)
        }

        ShadowLooper.idleMainLooper()

        rootView.measure(
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY),
        )
        rootView.layout(0, 0, 1920, 720)

        captureScreenshot(activity, "demo_kotlin_tabs_scrolled_bottom")
    }

    @Test
    fun demo_kotlin_radio_scrolled_bottom() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()

        selectTab(activity, "radio")

        val rootView = activity.getScreen()
        rootView.measure(
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY),
        )
        rootView.layout(0, 0, 1920, 720)

        activity.getScreen().setActivePanel(RADIO_TAB_INDEX)
        val panelScrollView: ScrollView? = activity.getScreen().getPanelWrapper(RADIO_TAB_INDEX)
        assertNotNull("Radio panel ScrollView should exist", panelScrollView)

        val scrollChild = panelScrollView!!.getChildAt(0)
        val scrollRange = scrollChild.measuredHeight - panelScrollView.measuredHeight
        if (scrollRange > 0) {
            panelScrollView.scrollTo(0, scrollRange)
        }

        ShadowLooper.idleMainLooper()

        rootView.measure(
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY),
        )
        rootView.layout(0, 0, 1920, 720)

        captureScreenshot(activity, "demo_kotlin_radio_scrolled_bottom")
    }
}
