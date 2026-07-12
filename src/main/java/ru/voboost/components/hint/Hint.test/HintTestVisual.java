package ru.voboost.components.hint;

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

import java.util.HashMap;
import java.util.Map;

import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.Theme;

/**
 * Visual regression tests for Hint component Java implementation using Roborazzi.
 *
 * <p>Generates named screenshots for all theme/language combinations,
 * with automotive screen configuration 1920x720.
 */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
        sdk = {33},
        qualifiers = "w1920dp-h720dp-land-mdpi")
public class HintTestVisual {

    private static final String SCREENSHOT_BASE_PATH =
            "src/main/java/ru/voboost/components/hint/Hint.screenshots";

    @Rule
    public TestName testName = new TestName();

    private Context context;
    private FrameLayout container;
    private Activity activity;

    @Before
    public void setUp() {
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

    private Hint createHint(Theme theme, Language language, Map<String, String> text) {
        Hint hint = new Hint(context);
        hint.setTheme(theme);
        hint.setLanguage(language);
        hint.setText(text);

        container.addView(hint);
        hint.setVisibility(View.VISIBLE);
        hint.setAlpha(1f);

        int widthSpec = View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY);
        container.measure(widthSpec, heightSpec);
        container.layout(0, 0, 1920, 720);

        return hint;
    }

    private Map<String, String> text(String en, String ru) {
        Map<String, String> map = new HashMap<>();
        map.put("en", en);
        map.put("ru", ru);
        return map;
    }

    // ============================================================
    // FREE LIGHT THEME TESTS
    // ============================================================

    @Test
    public void hint_freeLight_short_en() {
        createHint(Theme.FREE_LIGHT, Language.EN,
                text("Stop all running applications to free memory.",
                        "Остановить все запущенные приложения."));
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void hint_freeLight_short_ru() {
        createHint(Theme.FREE_LIGHT, Language.RU,
                text("Stop all running applications to free memory.",
                        "Остановить все запущенные приложения."));
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void hint_freeLight_wrapped_en() {
        createHint(Theme.FREE_LIGHT, Language.EN,
                text("Change scaling for all applications. It can be overridden per application in the launcher by long-pressing the application and choosing a different scale.",
                        "Изменить масштаб для всех приложений. Можно переопределить его для конкретного приложения в лаунчере."));
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    // ============================================================
    // FREE DARK THEME TESTS
    // ============================================================

    @Test
    public void hint_freeDark_short_en() {
        createHint(Theme.FREE_DARK, Language.EN,
                text("Stop all running applications to free memory.",
                        "Остановить все запущенные приложения."));
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void hint_freeDark_wrapped_ru() {
        createHint(Theme.FREE_DARK, Language.RU,
                text("Change scaling for all applications. It can be overridden per application in the launcher by long-pressing the application and choosing a different scale.",
                        "Изменить масштаб для всех приложений. Можно переопределить его для конкретного приложения в лаунчере, сделав долгое нажатие на приложении и выбрав другой масштаб."));
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    // ============================================================
    // DREAMER THEME TESTS
    // ============================================================

    @Test
    public void hint_dreamerLight_short_en() {
        createHint(Theme.DREAMER_LIGHT, Language.EN,
                text("Stop all running applications to free memory.",
                        "Остановить все запущенные приложения."));
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void hint_dreamerDark_wrapped_en() {
        createHint(Theme.DREAMER_DARK, Language.EN,
                text("Change scaling for all applications. It can be overridden per application in the launcher by long-pressing the application and choosing a different scale.",
                        "Изменить масштаб для всех приложений."));
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    // ============================================================
    // EDGE CASES
    // ============================================================

    @Test
    public void hint_freeLight_empty() {
        createHint(Theme.FREE_LIGHT, Language.EN, new HashMap<>());
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void hint_freeDark_multiline_en() {
        Map<String, String> text = new HashMap<>();
        text.put("en", "Line one.\nLine two.\nLine three.");
        createHint(Theme.FREE_DARK, Language.EN, text);
        captureRoboImage(container, getScreenshotPath(), new RoborazziOptions());
    }
}
