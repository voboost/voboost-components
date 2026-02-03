package ru.voboost.components.demo.compose

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Visual regression tests for Compose Demo Application using Roborazzi.
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
 * Note: Uses Radio view references exposed by MainActivity to change state.
 * The setSelectedValue(value, isTriggerCallback=true) method triggers the
 * onValueChangeListener which updates Compose state.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [33],
    qualifiers = "w1920dp-h720dp-land-mdpi"
)
class MainActivityTestVisual {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    companion object {
        private const val SCREENSHOT_PATH =
            "java/ru/voboost/components/demo/compose/MainActivity.screenshots/"
    }

    @Test
    fun testMainActivityCreation() {
        val activity = composeTestRule.activity

        assertNotNull("MainActivity should be created successfully", activity)
        assertNotNull("Window should be available", activity.window)
        assertNotNull("DecorView should be available", activity.window.decorView)
    }

    @Test
    fun testMainActivityLifecycle() {
        val activity = composeTestRule.activity
        assertNotNull("MainActivity should be available", activity)
    }

    /**
     * Captures a screenshot of the current Compose UI state.
     */
    private fun captureScreenshot(screenshotName: String) {
        val screenshotPath = "$SCREENSHOT_PATH$screenshotName.png"
        composeTestRule.onRoot().captureRoboImage(screenshotPath, RoborazziOptions())
    }

    /**
     * Sets the language using the Radio view reference.
     * Uses isTriggerCallback=true to update Compose state.
     */
    private fun setLanguage(language: String) {
        composeTestRule.activity.languageRadioView?.setSelectedValue(language, true)
        composeTestRule.waitForIdle()
    }

    /**
     * Sets the theme using the Radio view reference.
     * Uses isTriggerCallback=true to update Compose state.
     */
    private fun setTheme(theme: String) {
        composeTestRule.activity.themeRadioView?.setSelectedValue(theme, true)
        composeTestRule.waitForIdle()
    }

    /**
     * Sets the car type using the Radio view reference.
     * Uses isTriggerCallback=true to update Compose state.
     */
    private fun setCarType(carType: String) {
        composeTestRule.activity.carTypeRadioView?.setSelectedValue(carType, true)
        composeTestRule.waitForIdle()
    }

    // ========== STANDARD TEST SCENARIOS ==========

    /**
     * Test 1: Default state - English, Light, Free
     */
    @Test
    fun demo_compose_default() {
        captureScreenshot("demo_compose_default")
    }

    /**
     * Test 2: Russian language - Russian, Light, Free
     */
    @Test
    fun demo_compose_russian() {
        setLanguage("ru")
        captureScreenshot("demo_compose_russian")
    }

    /**
     * Test 3: Dark theme - English, Dark, Free
     */
    @Test
    fun demo_compose_dark() {
        setTheme("dark")
        captureScreenshot("demo_compose_dark")
    }

    /**
     * Test 4: Dreamer car type - English, Light, Dreamer
     */
    @Test
    fun demo_compose_dreamer() {
        setCarType("dreamer")
        captureScreenshot("demo_compose_dreamer")
    }

    /**
     * Test 5: Full combination - Russian, Dark, Dreamer
     */
    @Test
    fun demo_compose_full_combination() {
        setLanguage("ru")
        setTheme("dark")
        setCarType("dreamer")
        captureScreenshot("demo_compose_full_combination")
    }
}
