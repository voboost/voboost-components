package ru.voboost.components.toast;

import static com.github.takahirom.roborazzi.RoborazziKt.captureRoboImage;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.github.takahirom.roborazzi.RoborazziOptions;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

import ru.voboost.components.theme.Theme;

/**
 * Visual regression tests for Toast component Java implementation using Roborazzi.
 *
 * These tests generate named screenshots for all theme combinations,
 * with automotive screen configuration 1920x720.
 */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
        sdk = {33},
        qualifiers = "w1920dp-h720dp-land-mdpi")
public class ToastTestVisual {

    private static final String SCREENSHOT_BASE_PATH =
            "src/main/java/ru/voboost/components/toast/Toast.screenshots";

    @Rule
    public TestName testName = new TestName();

    private Context context;
    private FrameLayout container;
    private Activity activity;

    @Before
    public void setUp() {
        // Create an Activity to attach views to (required for Roborazzi screenshot capture)
        ActivityController<Activity> controller = Robolectric.buildActivity(Activity.class);
        controller.create().start().resume();
        activity = controller.get();
        context = activity;
        container = new FrameLayout(context);
        activity.setContentView(container);
    }

    private String getScreenshotPath() {
        return SCREENSHOT_BASE_PATH + "/" + testName.getMethodName() + ".png";
    }

    private Toast createToast(Theme theme, String text, boolean showCloseButton) {
        Toast toast = new Toast(context);
        toast.setTheme(theme);
        toast.setContent(text);
        toast.setShowCloseButton(showCloseButton);

        // Add to container
        container.addView(toast);

        // Make visible (skip animation for screenshot)
        toast.setVisibility(View.VISIBLE);
        toast.setAlpha(1f);

        // Measure and layout
        int widthSpec = View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY);
        container.measure(widthSpec, heightSpec);
        container.layout(0, 0, 1920, 720);

        return toast;
    }

    // ============================================================
    // FREE_DARK THEME TESTS
    // ============================================================

    @Test
    public void toast_freeDark_short() {
        createToast(Theme.FREE_DARK, "Settings saved", false);
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void toast_freeDark_long() {
        createToast(Theme.FREE_DARK, "Your settings have been successfully saved to the system", false);
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void toast_freeDark_closeButton() {
        createToast(Theme.FREE_DARK, "Loading data...", true);
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void toast_freeDark_unicode() {
        createToast(Theme.FREE_DARK, "Настройки сохранены", false);
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    // ============================================================
    // FREE_LIGHT THEME TESTS
    // ============================================================

    @Test
    public void toast_freeLight_short() {
        createToast(Theme.FREE_LIGHT, "Settings saved", false);
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void toast_freeLight_long() {
        createToast(Theme.FREE_LIGHT, "Your settings have been successfully saved to the system", false);
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void toast_freeLight_closeButton() {
        createToast(Theme.FREE_LIGHT, "Loading data...", true);
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void toast_freeLight_unicode() {
        createToast(Theme.FREE_LIGHT, "Настройки сохранены", false);
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    // ============================================================
    // DREAMER_DARK THEME TESTS
    // ============================================================

    @Test
    public void toast_dreamerDark_short() {
        createToast(Theme.DREAMER_DARK, "Settings saved", false);
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void toast_dreamerDark_long() {
        createToast(Theme.DREAMER_DARK, "Your settings have been successfully saved to the system", false);
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void toast_dreamerDark_closeButton() {
        createToast(Theme.DREAMER_DARK, "Loading data...", true);
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void toast_dreamerDark_unicode() {
        createToast(Theme.DREAMER_DARK, "Настройки сохранены", false);
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    // ============================================================
    // DREAMER_LIGHT THEME TESTS
    // ============================================================

    @Test
    public void toast_dreamerLight_short() {
        createToast(Theme.DREAMER_LIGHT, "Settings saved", false);
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void toast_dreamerLight_long() {
        createToast(Theme.DREAMER_LIGHT, "Your settings have been successfully saved to the system", false);
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void toast_dreamerLight_closeButton() {
        createToast(Theme.DREAMER_LIGHT, "Loading data...", true);
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void toast_dreamerLight_unicode() {
        createToast(Theme.DREAMER_LIGHT, "Настройки сохранены", false);
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    // ============================================================
    // EDGE CASE TESTS
    // ============================================================

    @Test
    public void toast_empty() {
        createToast(Theme.FREE_DARK, "", false);
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void toast_veryLong() {
        createToast(Theme.FREE_DARK,
                "This is an extremely long message that should definitely be truncated with ellipsis because it exceeds the maximum width available for the toast component",
                false);
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void toast_closeButton_longText() {
        createToast(Theme.FREE_DARK,
                "This is a long message with a close button that should be properly positioned",
                true);
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }
}
