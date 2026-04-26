package ru.voboost.components.panel;

import static com.github.takahirom.roborazzi.RoborazziKt.captureRoboImage;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
import ru.voboost.components.radio.Radio;
import ru.voboost.components.radio.RadioButton;
import ru.voboost.components.section.Section;
import ru.voboost.components.theme.Theme;

/**
 * Visual regression tests for the Panel component using Roborazzi.
 */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
        sdk = {33},
        qualifiers = "w1920dp-h720dp-land-mdpi")
public class PanelTestVisual {

    private static final String SCREENSHOT_BASE_PATH =
            "src/main/java/ru/voboost/components/panel/Panel.screenshots";

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

    private String getScreenshotName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String testMethodName = "unknown";
        for (int i = 2; i < stackTrace.length; i++) {
            String methodName = stackTrace[i].getMethodName();
            // Test methods start with "panel_"
            if (methodName.startsWith("panel_")) {
                testMethodName = methodName;
                break;
            }
        }
        return SCREENSHOT_BASE_PATH + "/" + testMethodName + ".png";
    }

    private Panel createPanel(Theme theme) {
        Panel panel = new Panel(context);
        panel.setTheme(theme);

        // Set layout parameters for proper rendering
        // Panel is transparent and fills available space
        ViewGroup.LayoutParams params =
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        panel.setLayoutParams(params);

        // Force measurement and layout for screenshot capture with proper dimensions
        int widthMeasureSpec =
                android.view.View.MeasureSpec.makeMeasureSpec(
                        1920, android.view.View.MeasureSpec.EXACTLY);
        int heightMeasureSpec =
                android.view.View.MeasureSpec.makeMeasureSpec(
                        720, android.view.View.MeasureSpec.EXACTLY);

        panel.measure(widthMeasureSpec, heightMeasureSpec);

        // Ensure we have valid measured dimensions
        int measuredWidth = panel.getMeasuredWidth();
        int measuredHeight = panel.getMeasuredHeight();

        if (measuredWidth <= 0 || measuredHeight <= 0) {
            throw new IllegalArgumentException(
                    "Panel component has invalid measured dimensions: "
                            + measuredWidth
                            + "x"
                            + measuredHeight);
        }

        panel.layout(0, 0, measuredWidth, measuredHeight);

        // Add to container (attached to Activity) for Roborazzi screenshot capture
        container.removeAllViews();
        container.addView(panel);

        return panel;
    }

    @Test
    public void panel_freeLight() {
        Panel panel = createPanel(Theme.FREE_LIGHT);
        captureRoboImage(panel, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void panel_freeDark() {
        Panel panel = createPanel(Theme.FREE_DARK);
        captureRoboImage(panel, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void panel_dreamerLight() {
        Panel panel = createPanel(Theme.DREAMER_LIGHT);
        captureRoboImage(panel, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void panel_dreamerDark() {
        Panel panel = createPanel(Theme.DREAMER_DARK);
        captureRoboImage(panel, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void panel_withContent() {
        Panel panel = createPanel(Theme.FREE_LIGHT);

        // Add Section with content (как в демо)
        Map<String, String> sectionTitle = new HashMap<String, String>() {{
            put("en", "Test Section");
            put("ru", "Тестовая секция");
        }};
        Section section = Section.create(context, Theme.FREE_LIGHT, Language.EN, sectionTitle).build();

        // Add Radio component to Section using typed API
        Radio radio = Radio.create(
                context,
                Theme.FREE_LIGHT,
                Language.EN,
                Arrays.asList(
                        new RadioButton("option1", new HashMap<String, String>() {{
                            put("en", "Option 1");
                            put("ru", "Опция 1");
                        }}),
                        new RadioButton("option2", new HashMap<String, String>() {{
                            put("en", "Option 2");
                            put("ru", "Опция 2");
                        }})),
                "option1").build();
        section.addRadio(radio);
        panel.addView(section);

        // Force remeasure and relayout
        panel.measure(
            android.view.View.MeasureSpec.makeMeasureSpec(1920, android.view.View.MeasureSpec.EXACTLY),
            android.view.View.MeasureSpec.makeMeasureSpec(720, android.view.View.MeasureSpec.EXACTLY)
        );
        panel.layout(0, 0, panel.getMeasuredWidth(), panel.getMeasuredHeight());

        captureRoboImage(panel, getScreenshotName(), new RoborazziOptions());
    }
}
