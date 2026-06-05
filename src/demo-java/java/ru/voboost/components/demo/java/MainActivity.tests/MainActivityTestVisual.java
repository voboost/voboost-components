package ru.voboost.components.demo.java;

import static com.github.takahirom.roborazzi.RoborazziKt.captureRoboImage;
import static org.junit.Assert.assertNotNull;

import android.view.View;
import android.widget.ScrollView;

import com.github.takahirom.roborazzi.RoborazziOptions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;
import org.robolectric.shadows.ShadowLooper;

/**
 * Visual regression tests for the Java demo (Roborazzi), automotive 1920x720.
 *
 * Scenarios are identical across demo-java, demo-kotlin and demo-compose:
 * state combinations on the settings tab, screen lift, per-tab screenshots,
 * and tab/panel scrolling.
 */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
        sdk = {33},
        qualifiers = "w1920dp-h720dp-land-mdpi")
public class MainActivityTestVisual {

    private static final String SCREENSHOT_PATH =
            "java/ru/voboost/components/demo/java/MainActivity.screenshots/";

    // Index of the radio tab inside the 8-tab model (settings, button, buttons, checkbox, radio, ...)
    private static final int RADIO_TAB_INDEX = 4;

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

    private void captureScreenshot(MainActivity activity, String screenshotName) {
        View rootView = activity.getScreen();
        assertNotNull("Screen should not be null", rootView);

        rootView.measure(
                View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY));
        rootView.layout(0, 0, 1920, 720);

        captureRoboImage(rootView, SCREENSHOT_PATH + screenshotName + ".png", new RoborazziOptions());
    }

    private void selectTab(MainActivity activity, String tabValue) {
        activity.getDemoState().setSelectedTab(tabValue);
        activity.getTabs().setSelectedValue(tabValue);
    }

    private void setScreenLiftState(MainActivity activity, int state) {
        activity.getDemoState().setScreenLiftState(state);
        activity.getScreen().onScreenLift(state);
    }

    // ========== STATE COMBINATIONS (settings tab) ==========

    @Test
    public void demo_java_default() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        captureScreenshot(activity, "demo_java_default");
    }

    @Test
    public void demo_java_russian() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        activity.applyState("ru", "dark", "free");
        captureScreenshot(activity, "demo_java_russian");
    }

    @Test
    public void demo_java_light() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        activity.applyState("en", "light", "free");
        captureScreenshot(activity, "demo_java_light");
    }

    @Test
    public void demo_java_dreamer() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        activity.applyState("en", "dark", "dreamer");
        captureScreenshot(activity, "demo_java_dreamer");
    }

    @Test
    public void demo_java_dreamer_light() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        activity.applyState("en", "light", "dreamer");
        captureScreenshot(activity, "demo_java_dreamer_light");
    }

    @Test
    public void demo_java_full_combination() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        activity.applyState("ru", "dark", "dreamer");
        captureScreenshot(activity, "demo_java_full_combination");
    }

    @Test
    public void demo_java_screen_lift_raised() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        setScreenLiftState(activity, ru.voboost.components.screen.Screen.SCREEN_RAISED);
        captureScreenshot(activity, "demo_java_screen_lift_raised");
    }

    @Test
    public void demo_java_screen_lift_lowered() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        setScreenLiftState(activity, ru.voboost.components.screen.Screen.SCREEN_LOWERED);
        captureScreenshot(activity, "demo_java_screen_lift_lowered");
    }

    @Test
    public void demo_java_component_hierarchy() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();

        assertNotNull("Screen component should exist", activity.getScreen());
        assertNotNull("Tabs component should exist", activity.getTabs());
        assertNotNull("Demo State should exist", activity.getDemoState());

        captureScreenshot(activity, "demo_java_component_hierarchy");
    }

    // ========== PER-TAB SCREENSHOTS ==========

    @Test
    public void demo_java_button() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        selectTab(activity, "button");
        captureScreenshot(activity, "demo_java_button");
    }

    @Test
    public void demo_java_buttons() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        selectTab(activity, "buttons");
        captureScreenshot(activity, "demo_java_buttons");
    }

    @Test
    public void demo_java_checkbox() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        selectTab(activity, "checkbox");
        captureScreenshot(activity, "demo_java_checkbox");
    }

    @Test
    public void demo_java_radio() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        selectTab(activity, "radio");
        captureScreenshot(activity, "demo_java_radio");
    }

    @Test
    public void demo_java_select() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        selectTab(activity, "select");
        captureScreenshot(activity, "demo_java_select");
    }

    @Test
    public void demo_java_dialog() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        selectTab(activity, "dialog");
        captureScreenshot(activity, "demo_java_dialog");
    }

    @Test
    public void demo_java_toast() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        selectTab(activity, "toast");
        captureScreenshot(activity, "demo_java_toast");
    }

    // ========== SCROLL TESTS ==========

    @Test
    public void demo_java_tabs_scrolled_bottom() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();

        ScrollView tabsScrollView = activity.getScreen().getTabsScrollView();
        assertNotNull("Tabs ScrollView should exist", tabsScrollView);

        View rootView = activity.getScreen();
        rootView.measure(
                View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY));
        rootView.layout(0, 0, 1920, 720);

        View scrollChild = tabsScrollView.getChildAt(0);
        int scrollRange = scrollChild.getMeasuredHeight() - tabsScrollView.getMeasuredHeight();
        if (scrollRange > 0) {
            tabsScrollView.scrollTo(0, scrollRange);
        }

        ShadowLooper.idleMainLooper();

        rootView.measure(
                View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY));
        rootView.layout(0, 0, 1920, 720);

        captureScreenshot(activity, "demo_java_tabs_scrolled_bottom");
    }

    @Test
    public void demo_java_radio_scrolled_bottom() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();

        selectTab(activity, "radio");

        View rootView = activity.getScreen();
        rootView.measure(
                View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY));
        rootView.layout(0, 0, 1920, 720);

        activity.getScreen().setActivePanel(RADIO_TAB_INDEX);
        ScrollView panelScrollView = activity.getScreen().getPanelWrapper(RADIO_TAB_INDEX);
        assertNotNull("Radio panel ScrollView should exist", panelScrollView);

        View scrollChild = panelScrollView.getChildAt(0);
        int scrollRange = scrollChild.getMeasuredHeight() - panelScrollView.getMeasuredHeight();
        if (scrollRange > 0) {
            panelScrollView.scrollTo(0, scrollRange);
        }

        ShadowLooper.idleMainLooper();

        rootView.measure(
                View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY));
        rootView.layout(0, 0, 1920, 720);

        captureScreenshot(activity, "demo_java_radio_scrolled_bottom");
    }
}
