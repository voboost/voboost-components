package ru.voboost.components.demo.java;

import static com.github.takahirom.roborazzi.RoborazziKt.captureRoboImage;
import static org.junit.Assert.assertNotNull;

import android.view.View;
import android.view.ViewGroup;

import com.github.takahirom.roborazzi.RoborazziOptions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

/**
 * Visual regression tests for Java Demo Application using Roborazzi.
 *
 * These tests generate screenshots of the full demo application screen,
 * with automotive screen configuration 1920x720.
 *
 * Standard test scenarios:
 * 1. Default state (English, Light, Free)
 * 2. Russian language (Russian, Light, Free)
 * 3. Dark theme (English, Dark, Free)
 * 4. Dreamer car type (English, Light, Dreamer)
 * 5. Full combination (Russian, Dark, Dreamer)
 */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
        sdk = {33},
        qualifiers = "w1920dp-h720dp-land-mdpi")
public class MainActivityTestVisual {

    private static final String SCREENSHOT_PATH =
            "java/ru/voboost/components/demo/java/MainActivity.screenshots/";

    @Test
    public void testMainActivityCreation() {
        ActivityController<MainActivity> controller = Robolectric.buildActivity(MainActivity.class);
        MainActivity activity = controller.create().start().resume().get();

        assertNotNull("MainActivity should be created successfully", activity);
        assertNotNull("Window should be available", activity.getWindow());
        assertNotNull("DecorView should be available", activity.getWindow().getDecorView());
    }

    @Test
    public void testMainActivityLifecycle() {
        ActivityController<MainActivity> controller = Robolectric.buildActivity(MainActivity.class);
        MainActivity activity = controller.create().start().resume().get();

        controller.pause();
        controller.resume();
        controller.stop();
        controller.restart();
        controller.start();
        controller.resume();

        assertNotNull("Activity should survive lifecycle changes", activity);
    }

    // Helper method to capture screenshot with proper layout
    private void captureScreenshot(MainActivity activity, String screenshotName) {
        View rootView = ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);

        rootView.measure(
                View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY));
        rootView.layout(0, 0, 1920, 720);

        String screenshotPath = SCREENSHOT_PATH + screenshotName + ".png";
        captureRoboImage(rootView, screenshotPath, new RoborazziOptions());
    }

    // Helper methods to set radio values using direct API calls
    private void setLanguage(MainActivity activity, String language) {
        activity.getLanguageRadio().setSelectedValue(language, true);
    }

    private void setTheme(MainActivity activity, String theme) {
        activity.getThemeRadio().setSelectedValue(theme, true);
    }

    private void setCarType(MainActivity activity, String carType) {
        activity.getCarTypeRadio().setSelectedValue(carType, true);
    }

    // ========== STANDARD TEST SCENARIOS ==========

    /**
     * Test 1: Default state - English, Light, Free
     */
    @Test
    public void demo_java_default() {
        ActivityController<MainActivity> controller = Robolectric.buildActivity(MainActivity.class);
        MainActivity activity = controller.create().start().resume().get();
        captureScreenshot(activity, "demo_java_default");
    }

    /**
     * Test 2: Russian language - Russian, Light, Free
     */
    @Test
    public void demo_java_russian() {
        ActivityController<MainActivity> controller = Robolectric.buildActivity(MainActivity.class);
        MainActivity activity = controller.create().start().resume().get();

        setLanguage(activity, "ru");
        captureScreenshot(activity, "demo_java_russian");
    }

    /**
     * Test 3: Dark theme - English, Dark, Free
     */
    @Test
    public void demo_java_dark() {
        ActivityController<MainActivity> controller = Robolectric.buildActivity(MainActivity.class);
        MainActivity activity = controller.create().start().resume().get();

        setTheme(activity, "dark");
        captureScreenshot(activity, "demo_java_dark");
    }

    /**
     * Test 4: Dreamer car type - English, Light, Dreamer
     */
    @Test
    public void demo_java_dreamer() {
        ActivityController<MainActivity> controller = Robolectric.buildActivity(MainActivity.class);
        MainActivity activity = controller.create().start().resume().get();

        setCarType(activity, "dreamer");
        captureScreenshot(activity, "demo_java_dreamer");
    }

    /**
     * Test 5: Full combination - Russian, Dark, Dreamer
     */
    @Test
    public void demo_java_full_combination() {
        ActivityController<MainActivity> controller = Robolectric.buildActivity(MainActivity.class);
        MainActivity activity = controller.create().start().resume().get();

        setLanguage(activity, "ru");
        setTheme(activity, "dark");
        setCarType(activity, "dreamer");
        captureScreenshot(activity, "demo_java_full_combination");
    }
}
