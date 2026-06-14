package ru.voboost.components.demo.kotlin.cunba

import android.view.View
import android.widget.ScrollView

import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.android.controller.ActivityController
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import org.robolectric.shadows.ShadowLooper

import ru.voboost.components.screen.Screen

/**
 * Visual regression tests for the CunBA 3 demo (Roborazzi), automotive 1920x720.
 *
 * <p>State combinations on the settings tab and per-tab screenshots.
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
            "java/ru/voboost/components/demo/kotlin/cunba/MainActivity.screenshots/"
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

    private fun captureScreenshot(activity: MainActivity, screenshotName: String) {
        val rootView = activity.getScreen()
        assertNotNull("Screen should not be null", rootView)

        rootView.measure(
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY)
        )
        rootView.layout(0, 0, 1920, 720)

        rootView.captureRoboImage("$SCREENSHOT_PATH$screenshotName.png", RoborazziOptions())
    }

    private fun selectTab(activity: MainActivity, tabValue: String) {
        activity.getDemoState().setSelectedTab(tabValue)
        activity.getTabs()?.setSelectedValue(tabValue)
    }

    // ========== STATE COMBINATIONS (settings tab) ==========

    @Test
    fun demo_cunba_default() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        captureScreenshot(activity, "demo_cunba_default")
    }

    @Test
    fun demo_cunba_english() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        activity.applyState("en", "dark", "free")
        captureScreenshot(activity, "demo_cunba_english")
    }

    @Test
    fun demo_cunba_light() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        activity.applyState("ru", "light", "free")
        captureScreenshot(activity, "demo_cunba_light")
    }

    @Test
    fun demo_cunba_dreamer() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        activity.applyState("ru", "dark", "dreamer")
        captureScreenshot(activity, "demo_cunba_dreamer")
    }

    @Test
    fun demo_cunba_dreamer_light() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        activity.applyState("en", "light", "dreamer")
        captureScreenshot(activity, "demo_cunba_dreamer_light")
    }

    @Test
    fun demo_cunba_full_combination() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        activity.applyState("ru", "dark", "dreamer")
        captureScreenshot(activity, "demo_cunba_full_combination")
    }

    @Test
    fun demo_cunba_screen_lift_raised() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        activity.getDemoState().setScreenLiftState(Screen.SCREEN_RAISED)
        activity.getScreen().onScreenLift(Screen.SCREEN_RAISED)
        captureScreenshot(activity, "demo_cunba_screen_lift_raised")
    }

    @Test
    fun demo_cunba_screen_lift_lowered() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        activity.getDemoState().setScreenLiftState(Screen.SCREEN_LOWERED)
        activity.getScreen().onScreenLift(Screen.SCREEN_LOWERED)
        captureScreenshot(activity, "demo_cunba_screen_lift_lowered")
    }

    @Test
    fun demo_cunba_component_hierarchy() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()

        assertNotNull("Screen component should exist", activity.getScreen())
        assertNotNull("Tabs component should exist", activity.getTabs())
        assertNotNull("Demo State should exist", activity.getDemoState())

        captureScreenshot(activity, "demo_cunba_component_hierarchy")
    }

    // ========== PER-TAB SCREENSHOTS ==========

    @Test
    fun demo_cunba_launcher() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        selectTab(activity, "launcher")
        captureScreenshot(activity, "demo_cunba_launcher")
    }

    @Test
    fun demo_cunba_applications() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        selectTab(activity, "applications")
        captureScreenshot(activity, "demo_cunba_applications")
    }

    @Test
    fun demo_cunba_interface() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        selectTab(activity, "interface")
        captureScreenshot(activity, "demo_cunba_interface")
    }

    @Test
    fun demo_cunba_vehicle() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        selectTab(activity, "vehicle")
        captureScreenshot(activity, "demo_cunba_vehicle")
    }

    @Test
    fun demo_cunba_settings() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()
        selectTab(activity, "settings")
        captureScreenshot(activity, "demo_cunba_settings")
    }

    // ========== SCROLL TESTS ==========

    @Test
    fun demo_cunba_tabs_scrolled_bottom() {
        val activity = Robolectric.buildActivity(MainActivity::class.java).create().start().resume().get()

        val tabsScrollView = activity.getScreen().getTabsScrollView()
        assertNotNull("Tabs ScrollView should exist", tabsScrollView)

        val rootView = activity.getScreen()
        rootView.measure(
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY)
        )
        rootView.layout(0, 0, 1920, 720)

        val scrollChild = tabsScrollView.getChildAt(0)
        val scrollRange = scrollChild.measuredHeight - tabsScrollView.measuredHeight
        if (scrollRange > 0) {
            tabsScrollView.scrollTo(0, scrollRange)
        }

        ShadowLooper.idleMainLooper()

        rootView.measure(
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY)
        )
        rootView.layout(0, 0, 1920, 720)

        captureScreenshot(activity, "demo_cunba_tabs_scrolled_bottom")
    }
}
