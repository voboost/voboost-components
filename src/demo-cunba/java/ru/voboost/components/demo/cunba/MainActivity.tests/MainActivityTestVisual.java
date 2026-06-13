package ru.voboost.components.demo.cunba;

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

import ru.voboost.components.screen.Screen;
import ru.voboost.components.tabs.Tabs;

/**
 * Visual regression tests for the CunBA 3 demo (Roborazzi), automotive 1920x720.
 *
 * <p>State combinations on the settings tab and per-tab screenshots.
 */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
        sdk = {33},
        qualifiers = "w1920dp-h720dp-land-mdpi")
public class MainActivityTestVisual {

    private static final String SCREENSHOT_PATH =
            "java/ru/voboost/components/demo/cunba/MainActivity.screenshots/";

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

    // ========== STATE COMBINATIONS (settings tab) ==========

    @Test
    public void demo_cunba_default() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        captureScreenshot(activity, "demo_cunba_default");
    }

    @Test
    public void demo_cunba_english() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        activity.applyState("en", "dark", "free");
        captureScreenshot(activity, "demo_cunba_english");
    }

    @Test
    public void demo_cunba_light() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        activity.applyState("ru", "light", "free");
        captureScreenshot(activity, "demo_cunba_light");
    }

    @Test
    public void demo_cunba_dreamer() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        activity.applyState("ru", "dark", "dreamer");
        captureScreenshot(activity, "demo_cunba_dreamer");
    }

    @Test
    public void demo_cunba_dreamer_light() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        activity.applyState("en", "light", "dreamer");
        captureScreenshot(activity, "demo_cunba_dreamer_light");
    }

    @Test
    public void demo_cunba_full_combination() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        activity.applyState("ru", "dark", "dreamer");
        captureScreenshot(activity, "demo_cunba_full_combination");
    }

    @Test
    public void demo_cunba_screen_lift_raised() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        activity.getDemoState().setScreenLiftState(Screen.SCREEN_RAISED);
        activity.getScreen().onScreenLift(Screen.SCREEN_RAISED);
        captureScreenshot(activity, "demo_cunba_screen_lift_raised");
    }

    @Test
    public void demo_cunba_screen_lift_lowered() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        activity.getDemoState().setScreenLiftState(Screen.SCREEN_LOWERED);
        activity.getScreen().onScreenLift(Screen.SCREEN_LOWERED);
        captureScreenshot(activity, "demo_cunba_screen_lift_lowered");
    }

    @Test
    public void demo_cunba_component_hierarchy() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();

        assertNotNull("Screen component should exist", activity.getScreen());
        assertNotNull("Tabs component should exist", activity.getTabs());
        assertNotNull("Demo State should exist", activity.getDemoState());

        captureScreenshot(activity, "demo_cunba_component_hierarchy");
    }

    // ========== PER-TAB SCREENSHOTS ==========

    @Test
    public void demo_cunba_launcher() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        selectTab(activity, "launcher");
        captureScreenshot(activity, "demo_cunba_launcher");
    }

    @Test
    public void demo_cunba_applications() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        selectTab(activity, "applications");
        captureScreenshot(activity, "demo_cunba_applications");
    }

    @Test
    public void demo_cunba_interface() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        selectTab(activity, "interface");
        captureScreenshot(activity, "demo_cunba_interface");
    }

    @Test
    public void demo_cunba_vehicle() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        selectTab(activity, "vehicle");
        captureScreenshot(activity, "demo_cunba_vehicle");
    }

    @Test
    public void demo_cunba_settings() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).create().start().resume().get();
        selectTab(activity, "settings");
        captureScreenshot(activity, "demo_cunba_settings");
    }

    // ========== SCROLL TESTS ==========

    @Test
    public void demo_cunba_tabs_scrolled_bottom() {
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

        org.robolectric.shadows.ShadowLooper.idleMainLooper();

        rootView.measure(
                View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY));
        rootView.layout(0, 0, 1920, 720);

        captureScreenshot(activity, "demo_cunba_tabs_scrolled_bottom");
    }
}
