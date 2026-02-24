package ru.voboost.components.tabs;

import static com.github.takahirom.roborazzi.RoborazziKt.captureRoboImage;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.takahirom.roborazzi.RoborazziOptions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.Theme;

/**
 * Visual regression tests for the Tabs component using Roborazzi.
 *
 * These tests generate named screenshots for all theme and language combinations,
 * with automotive screen configuration 1920x720.
 */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
        sdk = {33},
        qualifiers = "w1920dp-h720dp-land-mdpi")
public class TabsTestVisual {

    private static final String SCREENSHOT_BASE_PATH =
            "src/main/java/ru/voboost/components/tabs/Tabs.screenshots";

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

    private String getScreenshotPath() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String testMethodName = "unknown";
        for (int i = 2; i < stackTrace.length; i++) {
            String methodName = stackTrace[i].getMethodName();
            // Test methods start with "tabs_"
            if (methodName.startsWith("tabs_")) {
                testMethodName = methodName;
                break;
            }
        }
        return SCREENSHOT_BASE_PATH + "/" + testMethodName + ".png";
    }

    // ============================================================
    // TEST DATA SETS
    // ============================================================

    // Standard 5-tab test set (same as existing tests)
    private List<TabItem> createStandardTestItems() {
        List<TabItem> items = new ArrayList<>();

        Map<String, String> storeLabels = new HashMap<>();
        storeLabels.put("en", "Store");
        storeLabels.put("ru", "Магазин");
        items.add(new TabItem("store", storeLabels));

        Map<String, String> appsLabels = new HashMap<>();
        appsLabels.put("en", "Applications");
        appsLabels.put("ru", "Приложения");
        items.add(new TabItem("apps", appsLabels));

        Map<String, String> interfaceLabels = new HashMap<>();
        interfaceLabels.put("en", "Interface");
        interfaceLabels.put("ru", "Интерфейс");
        items.add(new TabItem("interface", interfaceLabels));

        Map<String, String> vehicleLabels = new HashMap<>();
        vehicleLabels.put("en", "Vehicle");
        vehicleLabels.put("ru", "Автомобиль");
        items.add(new TabItem("vehicle", vehicleLabels));

        Map<String, String> settingsLabels = new HashMap<>();
        settingsLabels.put("en", "Settings");
        settingsLabels.put("ru", "Настройки");
        items.add(new TabItem("settings", settingsLabels));

        return items;
    }

    // Two-tab test set for animation testing
    private List<TabItem> getTwoTabTestSet() {
        Map<String, String> firstLabels = new HashMap<>();
        firstLabels.put("en", "First");
        firstLabels.put("ru", "Первый");

        Map<String, String> secondLabels = new HashMap<>();
        secondLabels.put("en", "Second");
        secondLabels.put("ru", "Второй");

        return Arrays.asList(
                new TabItem("first", firstLabels), new TabItem("second", secondLabels));
    }

    // Three-tab test set for animation testing
    private List<TabItem> getThreeTabTestSet() {
        Map<String, String> firstLabels = new HashMap<>();
        firstLabels.put("en", "First");
        firstLabels.put("ru", "Первый");

        Map<String, String> secondLabels = new HashMap<>();
        secondLabels.put("en", "Second");
        secondLabels.put("ru", "Второй");

        Map<String, String> thirdLabels = new HashMap<>();
        thirdLabels.put("en", "Third");
        thirdLabels.put("ru", "Третий");

        return Arrays.asList(
                new TabItem("first", firstLabels),
                new TabItem("second", secondLabels),
                new TabItem("third", thirdLabels));
    }

    // Four-tab test set for animation testing
    private List<TabItem> getFourTabTestSet() {
        Map<String, String> firstLabels = new HashMap<>();
        firstLabels.put("en", "First");
        firstLabels.put("ru", "Первый");

        Map<String, String> secondLabels = new HashMap<>();
        secondLabels.put("en", "Second");
        secondLabels.put("ru", "Второй");

        Map<String, String> thirdLabels = new HashMap<>();
        thirdLabels.put("en", "Third");
        thirdLabels.put("ru", "Третий");

        Map<String, String> fourthLabels = new HashMap<>();
        fourthLabels.put("en", "Fourth");
        fourthLabels.put("ru", "Четвертый");

        return Arrays.asList(
                new TabItem("first", firstLabels),
                new TabItem("second", secondLabels),
                new TabItem("third", thirdLabels),
                new TabItem("fourth", fourthLabels));
    }

    // Variable width test set (different text lengths)
    private List<TabItem> getVariableWidthTestSet() {
        Map<String, String> shortLabels = new HashMap<>();
        shortLabels.put("en", "A");
        shortLabels.put("ru", "А");

        Map<String, String> mediumLabels = new HashMap<>();
        mediumLabels.put("en", "Medium");
        mediumLabels.put("ru", "Средний");

        Map<String, String> longLabels = new HashMap<>();
        longLabels.put("en", "Very Long Tab Text");
        longLabels.put("ru", "Очень длинный текст вкладки");

        return Arrays.asList(
                new TabItem("short", shortLabels),
                new TabItem("medium", mediumLabels),
                new TabItem("long", longLabels));
    }

    private Tabs createTabs(List<TabItem> items, Language lang, Theme theme, String value) {
        // Validate input parameters to prevent IllegalArgumentException
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Items list cannot be null or empty");
        }
        if (lang == null) {
            throw new IllegalArgumentException("Language cannot be null");
        }
        if (theme == null) {
            throw new IllegalArgumentException("Theme cannot be null");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }

        // Validate each item
        for (TabItem item : items) {
            if (item == null) {
                throw new IllegalArgumentException("TabItem cannot be null");
            }
            if (item.getValue() == null || item.getValue().trim().isEmpty()) {
                throw new IllegalArgumentException("TabItem value cannot be null or empty");
            }
            if (item.getLabel() == null || item.getLabel().isEmpty()) {
                throw new IllegalArgumentException("TabItem label cannot be null or empty");
            }
        }

        Tabs tabs = new Tabs(context);

        // Set properties in correct order
        tabs.setTheme(theme);
        tabs.setLanguage(lang);
        tabs.setItems(items);
        tabs.setSelectedValue(value);

        // Set layout parameters for proper rendering
        ViewGroup.LayoutParams params =
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tabs.setLayoutParams(params);

        // Force measurement and layout for screenshot capture with proper dimensions
        int widthMeasureSpec =
                android.view.View.MeasureSpec.makeMeasureSpec(
                        1920, android.view.View.MeasureSpec.AT_MOST);
        int heightMeasureSpec =
                android.view.View.MeasureSpec.makeMeasureSpec(
                        720, android.view.View.MeasureSpec.AT_MOST);

        tabs.measure(widthMeasureSpec, heightMeasureSpec);

        // Ensure we have valid measured dimensions
        int measuredWidth = tabs.getMeasuredWidth();
        int measuredHeight = tabs.getMeasuredHeight();

        if (measuredWidth <= 0 || measuredHeight <= 0) {
            throw new IllegalArgumentException(
                    "Tabs component has invalid measured dimensions: "
                            + measuredWidth
                            + "x"
                            + measuredHeight);
        }

        tabs.layout(0, 0, measuredWidth, measuredHeight);

        // Add to container (attached to Activity) for Roborazzi screenshot capture
        container.removeAllViews();
        container.addView(tabs);

        return tabs;
    }

    // ============================================================
    // ANIMATION FRAME CAPTURE HELPER
    // ============================================================

    /**
     * Helper method to capture animation at specific progress
     * @param tabs Tabs component
     * @param fromValue Starting value
     * @param toValue Target value
     * @param progress Animation progress (0.0 to 1.0)
     */
    private void captureAnimationFrame(
            Tabs tabs, String fromValue, String toValue, float progress) {
        // Get the actual test method name from stack trace before any other operations
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String testMethodName = "unknown";
        for (int i = 2; i < stackTrace.length; i++) {
            String methodName = stackTrace[i].getMethodName();
            // Test methods start with "tabs_animation_"
            if (methodName.startsWith("tabs_animation_")) {
                testMethodName = methodName;
                break;
            }
        }

        // Set initial value without animation
        tabs.setSelectedValue(fromValue);

        // Force layout to ensure proper positioning
        int widthMeasureSpec =
                android.view.View.MeasureSpec.makeMeasureSpec(
                        1920, android.view.View.MeasureSpec.AT_MOST);
        int heightMeasureSpec =
                android.view.View.MeasureSpec.makeMeasureSpec(
                        720, android.view.View.MeasureSpec.AT_MOST);
        tabs.measure(widthMeasureSpec, heightMeasureSpec);
        tabs.layout(0, 0, tabs.getMeasuredWidth(), tabs.getMeasuredHeight());

        // Use reflection to access animation fields and set progress
        try {
            // Get the animatedY and itemPositions fields
            Field animatedYField = Tabs.class.getDeclaredField("animatedY");
            Field itemPositionsField = Tabs.class.getDeclaredField("itemPositions");
            Field itemsField = Tabs.class.getDeclaredField("items");

            animatedYField.setAccessible(true);
            itemPositionsField.setAccessible(true);
            itemsField.setAccessible(true);

            @SuppressWarnings("unchecked")
            List<Float> itemPositions = (List<Float>) itemPositionsField.get(tabs);
            @SuppressWarnings("unchecked")
            List<TabItem> items = (List<TabItem>) itemsField.get(tabs);

            // Find indices
            int fromIndex = -1;
            int toIndex = -1;
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getValue().equals(fromValue)) fromIndex = i;
                if (items.get(i).getValue().equals(toValue)) toIndex = i;
            }

            if (fromIndex >= 0 && toIndex >= 0) {
                float startY = itemPositions.get(fromIndex);
                float endY = itemPositions.get(toIndex);

                // Apply overshoot interpolation manually (matches OvershootInterpolator with
                // tension=1.0f)
                float overshootProgress = calculateOvershoot(progress);

                float currentY = startY + (endY - startY) * overshootProgress;
                animatedYField.setFloat(tabs, currentY);

                // Force redraw
                tabs.invalidate();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to set animation progress", e);
        }

        // Capture screenshot using the captured test method name
        String screenshotPath = SCREENSHOT_BASE_PATH + "/" + testMethodName + ".png";
        captureRoboImage(tabs, screenshotPath, new RoborazziOptions());
    }

    /**
     * Calculate overshoot interpolation value.
     * Matches OvershootInterpolator with tension=1.0f.
     * Formula: t = (t - 1)^2 * ((tension + 1) * (t - 1) + tension) + 1
     */
    private float calculateOvershoot(float t) {
        float tension = 1.0f;
        t -= 1.0f;
        return t * t * ((tension + 1) * t + tension) + 1.0f;
    }

    // ============================================================
    // EXISTING BASIC TESTS (preserved for compatibility)
    // ============================================================

    @Test
    public void tabs_freeLightEnglish() {
        Tabs tabs = createTabs(createStandardTestItems(), Language.EN, Theme.FREE_LIGHT, "store");
        captureRoboImage(tabs, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void tabs_freeLightRussian() {
        Tabs tabs = createTabs(createStandardTestItems(), Language.RU, Theme.FREE_LIGHT, "store");
        captureRoboImage(tabs, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void tabs_freeDarkEnglish() {
        Tabs tabs = createTabs(createStandardTestItems(), Language.EN, Theme.FREE_DARK, "apps");
        captureRoboImage(tabs, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void tabs_freeDarkRussian() {
        Tabs tabs = createTabs(createStandardTestItems(), Language.RU, Theme.FREE_DARK, "apps");
        captureRoboImage(tabs, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void tabs_dreamerLightEnglish() {
        Tabs tabs =
                createTabs(
                        createStandardTestItems(), Language.EN, Theme.DREAMER_LIGHT, "interface");
        captureRoboImage(tabs, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void tabs_dreamerLightRussian() {
        Tabs tabs =
                createTabs(
                        createStandardTestItems(), Language.RU, Theme.DREAMER_LIGHT, "interface");
        captureRoboImage(tabs, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void tabs_dreamerDarkEnglish() {
        Tabs tabs =
                createTabs(createStandardTestItems(), Language.EN, Theme.DREAMER_DARK, "vehicle");
        captureRoboImage(tabs, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void tabs_dreamerDarkRussian() {
        Tabs tabs =
                createTabs(createStandardTestItems(), Language.RU, Theme.DREAMER_DARK, "vehicle");
        captureRoboImage(tabs, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void tabs_lastItemSelected() {
        Tabs tabs =
                createTabs(createStandardTestItems(), Language.EN, Theme.FREE_LIGHT, "settings");
        captureRoboImage(tabs, getScreenshotPath(), new RoborazziOptions());
    }

    // ============================================================
    // COMPREHENSIVE ANIMATION TESTS
    // ============================================================

    // Two tab set - first to second transition - free_light - en
    @Test
    public void tabs_animation_two_tab_first_to_second_0_percent_free_light_en() {
        Tabs tabs = createTabs(getTwoTabTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(tabs, "first", "second", 0.0f);
    }

    @Test
    public void tabs_animation_two_tab_first_to_second_25_percent_free_light_en() {
        Tabs tabs = createTabs(getTwoTabTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(tabs, "first", "second", 0.25f);
    }

    @Test
    public void tabs_animation_two_tab_first_to_second_50_percent_free_light_en() {
        Tabs tabs = createTabs(getTwoTabTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(tabs, "first", "second", 0.50f);
    }

    @Test
    public void tabs_animation_two_tab_first_to_second_75_percent_free_light_en() {
        Tabs tabs = createTabs(getTwoTabTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(tabs, "first", "second", 0.75f);
    }

    @Test
    public void tabs_animation_two_tab_first_to_second_100_percent_free_light_en() {
        Tabs tabs = createTabs(getTwoTabTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(tabs, "first", "second", 1.0f);
    }

    // Two tab set - first to second transition - free_light - ru
    @Test
    public void tabs_animation_two_tab_first_to_second_0_percent_free_light_ru() {
        Tabs tabs = createTabs(getTwoTabTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(tabs, "first", "second", 0.0f);
    }

    @Test
    public void tabs_animation_two_tab_first_to_second_50_percent_free_light_ru() {
        Tabs tabs = createTabs(getTwoTabTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(tabs, "first", "second", 0.50f);
    }

    @Test
    public void tabs_animation_two_tab_first_to_second_100_percent_free_light_ru() {
        Tabs tabs = createTabs(getTwoTabTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(tabs, "first", "second", 1.0f);
    }

    // Two tab set - first to second transition - free_dark - en
    @Test
    public void tabs_animation_two_tab_first_to_second_0_percent_free_dark_en() {
        Tabs tabs = createTabs(getTwoTabTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(tabs, "first", "second", 0.0f);
    }

    @Test
    public void tabs_animation_two_tab_first_to_second_50_percent_free_dark_en() {
        Tabs tabs = createTabs(getTwoTabTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(tabs, "first", "second", 0.50f);
    }

    @Test
    public void tabs_animation_two_tab_first_to_second_100_percent_free_dark_en() {
        Tabs tabs = createTabs(getTwoTabTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(tabs, "first", "second", 1.0f);
    }

    // Two tab set - first to second transition - dreamer_light - en
    @Test
    public void tabs_animation_two_tab_first_to_second_0_percent_dreamer_light_en() {
        Tabs tabs = createTabs(getTwoTabTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(tabs, "first", "second", 0.0f);
    }

    @Test
    public void tabs_animation_two_tab_first_to_second_50_percent_dreamer_light_en() {
        Tabs tabs = createTabs(getTwoTabTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(tabs, "first", "second", 0.50f);
    }

    @Test
    public void tabs_animation_two_tab_first_to_second_100_percent_dreamer_light_en() {
        Tabs tabs = createTabs(getTwoTabTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(tabs, "first", "second", 1.0f);
    }

    // Two tab set - first to second transition - dreamer_dark - en
    @Test
    public void tabs_animation_two_tab_first_to_second_0_percent_dreamer_dark_en() {
        Tabs tabs = createTabs(getTwoTabTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(tabs, "first", "second", 0.0f);
    }

    @Test
    public void tabs_animation_two_tab_first_to_second_50_percent_dreamer_dark_en() {
        Tabs tabs = createTabs(getTwoTabTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(tabs, "first", "second", 0.50f);
    }

    @Test
    public void tabs_animation_two_tab_first_to_second_100_percent_dreamer_dark_en() {
        Tabs tabs = createTabs(getTwoTabTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(tabs, "first", "second", 1.0f);
    }

    // Three tab set - first to third transition - free_light - en
    @Test
    public void tabs_animation_three_tab_first_to_third_0_percent_free_light_en() {
        Tabs tabs = createTabs(getThreeTabTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(tabs, "first", "third", 0.0f);
    }

    @Test
    public void tabs_animation_three_tab_first_to_third_25_percent_free_light_en() {
        Tabs tabs = createTabs(getThreeTabTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(tabs, "first", "third", 0.25f);
    }

    @Test
    public void tabs_animation_three_tab_first_to_third_50_percent_free_light_en() {
        Tabs tabs = createTabs(getThreeTabTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(tabs, "first", "third", 0.50f);
    }

    @Test
    public void tabs_animation_three_tab_first_to_third_75_percent_free_light_en() {
        Tabs tabs = createTabs(getThreeTabTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(tabs, "first", "third", 0.75f);
    }

    @Test
    public void tabs_animation_three_tab_first_to_third_100_percent_free_light_en() {
        Tabs tabs = createTabs(getThreeTabTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(tabs, "first", "third", 1.0f);
    }

    // Four tab set - first to fourth transition - free_light - en
    @Test
    public void tabs_animation_four_tab_first_to_fourth_0_percent_free_light_en() {
        Tabs tabs = createTabs(getFourTabTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(tabs, "first", "fourth", 0.0f);
    }

    @Test
    public void tabs_animation_four_tab_first_to_fourth_25_percent_free_light_en() {
        Tabs tabs = createTabs(getFourTabTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(tabs, "first", "fourth", 0.25f);
    }

    @Test
    public void tabs_animation_four_tab_first_to_fourth_50_percent_free_light_en() {
        Tabs tabs = createTabs(getFourTabTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(tabs, "first", "fourth", 0.50f);
    }

    @Test
    public void tabs_animation_four_tab_first_to_fourth_75_percent_free_light_en() {
        Tabs tabs = createTabs(getFourTabTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(tabs, "first", "fourth", 0.75f);
    }

    @Test
    public void tabs_animation_four_tab_first_to_fourth_100_percent_free_light_en() {
        Tabs tabs = createTabs(getFourTabTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(tabs, "first", "fourth", 1.0f);
    }

    // Variable width tab set - short to long transition - free_light - en
    @Test
    public void tabs_animation_variable_width_short_to_long_0_percent_free_light_en() {
        Tabs tabs = createTabs(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(tabs, "short", "long", 0.0f);
    }

    @Test
    public void tabs_animation_variable_width_short_to_long_25_percent_free_light_en() {
        Tabs tabs = createTabs(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(tabs, "short", "long", 0.25f);
    }

    @Test
    public void tabs_animation_variable_width_short_to_long_50_percent_free_light_en() {
        Tabs tabs = createTabs(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(tabs, "short", "long", 0.50f);
    }

    @Test
    public void tabs_animation_variable_width_short_to_long_75_percent_free_light_en() {
        Tabs tabs = createTabs(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(tabs, "short", "long", 0.75f);
    }

    @Test
    public void tabs_animation_variable_width_short_to_long_100_percent_free_light_en() {
        Tabs tabs = createTabs(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(tabs, "short", "long", 1.0f);
    }

    // Edge case tests - testing bounds and text rendering
    @Test
    public void tabs_animation_edge_first_to_last_free_light_en() {
        Tabs tabs = createTabs(createStandardTestItems(), Language.EN, Theme.FREE_LIGHT, "store");
        captureAnimationFrame(tabs, "store", "settings", 0.50f);
    }

    @Test
    public void tabs_animation_edge_last_to_first_free_light_en() {
        Tabs tabs =
                createTabs(createStandardTestItems(), Language.EN, Theme.FREE_LIGHT, "settings");
        captureAnimationFrame(tabs, "settings", "store", 0.50f);
    }

    @Test
    public void tabs_animation_edge_variable_width_first_dreamer_dark_en() {
        Tabs tabs = createTabs(getVariableWidthTestSet(), Language.EN, Theme.DREAMER_DARK, "short");
        captureAnimationFrame(tabs, "short", "long", 0.50f);
    }

    @Test
    public void tabs_animation_edge_variable_width_last_free_dark_en() {
        Tabs tabs = createTabs(getVariableWidthTestSet(), Language.EN, Theme.FREE_DARK, "long");
        captureAnimationFrame(tabs, "long", "short", 0.50f);
    }

    // Russian language animation tests
    @Test
    public void tabs_animation_russian_transition_first_dreamer_dark() {
        Tabs tabs = createTabs(getThreeTabTestSet(), Language.RU, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(tabs, "first", "third", 0.50f);
    }

    @Test
    public void tabs_animation_russian_transition_last_free_light() {
        Tabs tabs = createTabs(getThreeTabTestSet(), Language.RU, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(tabs, "third", "first", 0.50f);
    }
}
