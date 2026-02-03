package ru.voboost.components.demo.kotlin

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
import ru.voboost.components.radio.Radio

/**
 * Visual regression tests for Kotlin Demo Application using Roborazzi.
 *
 * These tests generate screenshots of the full demo application screen,
 * with automotive screen configuration 1920x720.
 *
 * Standard test scenarios:
 * 1. Default state - English, Light, Free
 * 2. Russian language - Russian, Light, Free
 * 3. Dark theme - English, Dark, Free
 * 4. Dreamer car type - English, Light, Dreamer
 * 5. Full combination - Russian, Dark, Dreamer
 *
 * Note: Kotlin demo uses Java Custom View components directly.
 * State changes are applied through reflection to access private fields.
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
            "java/ru/voboost/components/demo/kotlin/MainActivity.screenshots/"
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
     *
     * @param activity The MainActivity instance to capture
     * @param screenshotName The name for the screenshot file (without extension)
     */
    private fun captureScreenshot(activity: MainActivity, screenshotName: String) {
        val rootView = (activity.findViewById<ViewGroup>(android.R.id.content)).getChildAt(0)

        rootView.measure(
            View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY)
        )
        rootView.layout(0, 0, 1920, 720)

        val screenshotPath = "$SCREENSHOT_PATH$screenshotName.png"
        rootView.captureRoboImage(screenshotPath, RoborazziOptions())
    }

    /**
     * Helper methods to set radio values using direct API calls
     */
    private fun setLanguage(activity: MainActivity, language: String) {
        activity.languageRadio.setSelectedValue(language, true)
    }

    private fun setTheme(activity: MainActivity, theme: String) {
        activity.themeRadio.setSelectedValue(theme, true)
    }

    private fun setCarType(activity: MainActivity, carType: String) {
        activity.carTypeRadio.setSelectedValue(carType, true)
    }

    // ========== STANDARD TEST SCENARIOS ==========

    /**
     * Test 1: Default state - English, Light, Free
     *
     * This is the initial state when the app launches.
     * No interaction needed.
     */
    @Test
    fun demo_kotlin_default() {
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()
        captureScreenshot(activity, "demo_kotlin_default")
    }

    /**
     * Test 2: Russian language - Russian, Light, Free
     *
     * Change language to Russian using the language radio.
     */
    @Test
    fun demo_kotlin_russian() {
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        setLanguage(activity, "ru")
        captureScreenshot(activity, "demo_kotlin_russian")
    }

    /**
     * Test 3: Dark theme - English, Dark, Free
     *
     * Change theme to Dark using the theme radio.
     */
    @Test
    fun demo_kotlin_dark() {
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        setTheme(activity, "dark")
        captureScreenshot(activity, "demo_kotlin_dark")
    }

    /**
     * Test 4: Dreamer car type - English, Light, Dreamer
     *
     * Change car type to Dreamer using the car type radio.
     */
    @Test
    fun demo_kotlin_dreamer() {
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        setCarType(activity, "dreamer")
        captureScreenshot(activity, "demo_kotlin_dreamer")
    }

    /**
     * Test 5: Full combination - Russian, Dark, Dreamer
     *
     * Apply all three changes: Russian language, Dark theme, Dreamer car type.
     */
    @Test
    fun demo_kotlin_full_combination() {
        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        setLanguage(activity, "ru")
        setTheme(activity, "dark")
        setCarType(activity, "dreamer")
        captureScreenshot(activity, "demo_kotlin_full_combination")
    }
}
