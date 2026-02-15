package ru.voboost.components.screen;

import static com.github.takahirom.roborazzi.RoborazziKt.captureRoboImage;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.takahirom.roborazzi.RoborazziOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

import ru.voboost.components.i18n.Language;
import ru.voboost.components.panel.Panel;
import ru.voboost.components.tabs.TabItem;
import ru.voboost.components.tabs.Tabs;
import ru.voboost.components.theme.Theme;

/**
 * Visual regression tests for the Screen component using Roborazzi.
 */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
        sdk = {33},
        qualifiers = "w1920dp-h720dp-land-mdpi")
public class ScreenTestVisual {

    private static final String SCREENSHOT_BASE_PATH =
            "src/main/java/ru/voboost/components/screen/Screen.screenshots";

    private Context context;
    private FrameLayout container;
    private Activity activity;

    @Before
    public void setUp() {
        // Create an Activity to attach views to (required for Roborazzi screenshot capture)
        ActivityController<Activity> controller = Robolectric.buildActivity(Activity.class);
        controller.create();
        activity = controller.get();
        context = activity;
        container = new FrameLayout(context);
        activity.setContentView(container);
    }

    private String getScreenshotName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String testMethodName = "unknown";
        for (int i = 2; i < stackTrace.length; i++) {
            String methodName = stackTrace[i].getMethodName();
            // Test methods start with "screen_"
            if (methodName.startsWith("screen_")) {
                testMethodName = methodName;
                break;
            }
        }
        return testMethodName + ".png";
    }

    private Screen createScreen(Theme theme) {
        Screen screen = new Screen(context);
        screen.setTheme(theme);

        // Set layout parameters for proper rendering
        ViewGroup.LayoutParams params =
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        screen.setLayoutParams(params);

        // Force measurement and layout for screenshot capture with proper dimensions
        int widthMeasureSpec =
                android.view.View.MeasureSpec.makeMeasureSpec(
                        1920, android.view.View.MeasureSpec.AT_MOST);
        int heightMeasureSpec =
                android.view.View.MeasureSpec.makeMeasureSpec(
                        720, android.view.View.MeasureSpec.AT_MOST);

        screen.measure(widthMeasureSpec, heightMeasureSpec);

        // Ensure we have valid measured dimensions
        int measuredWidth = screen.getMeasuredWidth();
        int measuredHeight = screen.getMeasuredHeight();

        if (measuredWidth <= 0 || measuredHeight <= 0) {
            throw new IllegalArgumentException(
                    "Screen component has invalid measured dimensions: "
                            + measuredWidth
                            + "x"
                            + measuredHeight);
        }

        screen.layout(0, 0, measuredWidth, measuredHeight);

        // Add to container (attached to Activity) for Roborazzi screenshot capture
        container.removeAllViews();
        container.addView(screen);

        return screen;
    }

    @Test
    public void screen_freeLight() {
        Screen screen = createScreen(Theme.FREE_LIGHT);
        captureRoboImage(screen, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void screen_freeDark() {
        Screen screen = createScreen(Theme.FREE_DARK);
        captureRoboImage(screen, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void screen_dreamerLight() {
        Screen screen = createScreen(Theme.DREAMER_LIGHT);
        captureRoboImage(screen, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void screen_dreamerDark() {
        Screen screen = createScreen(Theme.DREAMER_DARK);
        captureRoboImage(screen, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void screen_withContent() {
        Screen screen = createScreen(Theme.FREE_LIGHT);
        captureRoboImage(screen, getScreenshotName(), new RoborazziOptions());
    }

    // ============================================================
    // SCREEN+TABS+PANEL VISUAL INTEGRATION TESTS
    // ============================================================

    private List<TabItem> createStandardTabItems() {
        List<TabItem> items = new ArrayList<>();

        Map<String, String> tab1Labels = new HashMap<>();
        tab1Labels.put("en", "Network");
        items.add(new TabItem("network", tab1Labels));

        Map<String, String> tab2Labels = new HashMap<>();
        tab2Labels.put("en", "Display");
        items.add(new TabItem("display", tab2Labels));

        Map<String, String> tab3Labels = new HashMap<>();
        tab3Labels.put("en", "Sound");
        items.add(new TabItem("sound", tab3Labels));

        return items;
    }

    private Screen createScreenWithTabs(Theme theme, String selectedTab) {
        Screen screen = new Screen(context);
        screen.setTheme(theme);

        Tabs tabs = new Tabs(context);
        tabs.setTheme(theme);
        tabs.setLanguage(Language.EN);
        tabs.setItems(createStandardTabItems());
        screen.setTabs(tabs);

        Panel[] panels = new Panel[3];
        for (int i = 0; i < 3; i++) {
            panels[i] = new Panel(context);
            panels[i].setTheme(theme);
        }
        screen.setPanels(panels);

        tabs.setSelectedValue(selectedTab, false);

        int widthSpec = android.view.View.MeasureSpec.makeMeasureSpec(1920, android.view.View.MeasureSpec.EXACTLY);
        int heightSpec = android.view.View.MeasureSpec.makeMeasureSpec(720, android.view.View.MeasureSpec.EXACTLY);
        screen.measure(widthSpec, heightSpec);
        screen.layout(0, 0, 1920, 720);

        container.removeAllViews();
        container.addView(screen);

        return screen;
    }

    @Test
    public void screen_withTabsFirstSelected() {
        Screen screen = createScreenWithTabs(Theme.FREE_LIGHT, "network");
        captureRoboImage(screen, SCREENSHOT_BASE_PATH + "/" + getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void screen_withTabsSecondSelected() {
        Screen screen = createScreenWithTabs(Theme.FREE_LIGHT, "display");
        captureRoboImage(screen, SCREENSHOT_BASE_PATH + "/" + getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void screen_withTabsFreeDark() {
        Screen screen = createScreenWithTabs(Theme.FREE_DARK, "display");
        captureRoboImage(screen, SCREENSHOT_BASE_PATH + "/" + getScreenshotName(), new RoborazziOptions());
    }
}
