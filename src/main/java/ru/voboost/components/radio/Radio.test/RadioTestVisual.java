package ru.voboost.components.radio;

import static com.github.takahirom.roborazzi.RoborazziKt.captureRoboImage;

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
 * Visual regression tests for Radio component Java implementation using Roborazzi.
 *
 * These tests generate named screenshots for all theme and language combinations,
 * with automotive screen configuration 1920x720.
 */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
        sdk = {33},
        qualifiers = "w1920dp-h720dp-land-mdpi")
public class RadioTestVisual {

    private static final String SCREENSHOT_BASE_PATH =
            "src/main/java/ru/voboost/components/radio/Radio.screenshots";

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
            // Test methods start with "radio_"
            if (methodName.startsWith("radio_")) {
                testMethodName = methodName;
                break;
            }
        }
        return SCREENSHOT_BASE_PATH + "/" + testMethodName + ".png";
    }

    // Test data for Yes/No buttons (short labels)
    private List<RadioButton> getYesNoButtons() {
        Map<String, String> yesLabels = new HashMap<>();
        yesLabels.put("en", "Yes");

        Map<String, String> noLabels = new HashMap<>();
        noLabels.put("en", "No");

        return Arrays.asList(new RadioButton("yes", yesLabels), new RadioButton("no", noLabels));
    }

    // Test data for climate control buttons (long labels)
    private List<RadioButton> getClimateButtons() {
        Map<String, String> automaticLabels = new HashMap<>();
        automaticLabels.put("en", "Automatic Climate Control");
        automaticLabels.put("ru", "Автоматический климат-контроль");

        Map<String, String> magicLabels = new HashMap<>();
        magicLabels.put("en", "Magic Temperature Setting");
        magicLabels.put("ru", "Магическая настройка температуры");

        Map<String, String> customLabels = new HashMap<>();
        customLabels.put("en", "Custom Climate Mode");
        customLabels.put("ru", "Пользовательский режим климата");

        return Arrays.asList(
                new RadioButton("automatic", automaticLabels),
                new RadioButton("magic", magicLabels),
                new RadioButton("custom", customLabels));
    }

    // Test data for language selection buttons (English interface)
    private List<RadioButton> getLanguageButtonsEn() {
        return Arrays.asList(
                new RadioButton("en", createSingleLangMap("en", "English")),
                new RadioButton("de", createSingleLangMap("en", "Deutsche")),
                new RadioButton("ru", createSingleLangMap("en", "Russian")),
                new RadioButton("zh", createSingleLangMap("en", "Chinese")));
    }

    // Test data for language selection buttons (Russian interface)
    private List<RadioButton> getLanguageButtonsRu() {
        return Arrays.asList(
                new RadioButton("en", createSingleLangMap("ru", "English")),
                new RadioButton("de", createSingleLangMap("ru", "Deutsch")),
                new RadioButton("ru", createSingleLangMap("ru", "Русский")),
                new RadioButton("zh", createSingleLangMap("ru", "中文")));
    }

    private Map<String, String> createSingleLangMap(String lang, String label) {
        Map<String, String> map = new HashMap<>();
        map.put(lang, label);
        return map;
    }

    private Radio createRadio(List<RadioButton> buttons, Language lang, Theme theme, String value) {
        // Validate input parameters to prevent IllegalArgumentException
        if (buttons == null || buttons.isEmpty()) {
            throw new IllegalArgumentException("Buttons list cannot be null or empty");
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

        // Validate each button
        for (RadioButton button : buttons) {
            if (button == null) {
                throw new IllegalArgumentException("Button cannot be null");
            }
            if (button.getValue() == null || button.getValue().trim().isEmpty()) {
                throw new IllegalArgumentException("Button value cannot be null or empty");
            }
            if (button.getLabel() == null || button.getLabel().isEmpty()) {
                throw new IllegalArgumentException("Button label cannot be null or empty");
            }
        }

        Radio radio = new Radio(context);

        // Set properties in correct order
        radio.setTheme(theme);
        radio.setLanguage(lang);
        radio.setButtons(buttons);
        radio.setSelectedValue(value);

        // Set layout parameters for proper rendering
        ViewGroup.LayoutParams params =
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        radio.setLayoutParams(params);

        // Force measurement and layout for screenshot capture with proper dimensions
        int widthMeasureSpec =
                android.view.View.MeasureSpec.makeMeasureSpec(
                        1920, android.view.View.MeasureSpec.AT_MOST);
        int heightMeasureSpec =
                android.view.View.MeasureSpec.makeMeasureSpec(
                        720, android.view.View.MeasureSpec.AT_MOST);

        radio.measure(widthMeasureSpec, heightMeasureSpec);

        // Ensure we have valid measured dimensions
        int measuredWidth = radio.getMeasuredWidth();
        int measuredHeight = radio.getMeasuredHeight();

        if (measuredWidth <= 0 || measuredHeight <= 0) {
            throw new IllegalArgumentException(
                    "Radio component has invalid measured dimensions: "
                            + measuredWidth
                            + "x"
                            + measuredHeight);
        }

        radio.layout(0, 0, measuredWidth, measuredHeight);

        // Add to container (attached to Activity) for Roborazzi screenshot capture
        container.removeAllViews();
        container.addView(radio);

        return radio;
    }

    // Test 1: Yes/No with all themes - Yes selected
    @Test
    public void radio_yes_no_yes_free_light() {
        Radio radio = createRadio(getYesNoButtons(), Language.EN, Theme.FREE_LIGHT, "yes");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_yes_no_yes_free_dark() {
        Radio radio = createRadio(getYesNoButtons(), Language.EN, Theme.FREE_DARK, "yes");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_yes_no_yes_dreamer_light() {
        Radio radio = createRadio(getYesNoButtons(), Language.EN, Theme.DREAMER_LIGHT, "yes");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_yes_no_yes_dreamer_dark() {
        Radio radio = createRadio(getYesNoButtons(), Language.EN, Theme.DREAMER_DARK, "yes");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    // Test 1: Yes/No with all themes - No selected
    @Test
    public void radio_yes_no_no_free_light() {
        Radio radio = createRadio(getYesNoButtons(), Language.EN, Theme.FREE_LIGHT, "no");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_yes_no_no_free_dark() {
        Radio radio = createRadio(getYesNoButtons(), Language.EN, Theme.FREE_DARK, "no");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_yes_no_no_dreamer_light() {
        Radio radio = createRadio(getYesNoButtons(), Language.EN, Theme.DREAMER_LIGHT, "no");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_yes_no_no_dreamer_dark() {
        Radio radio = createRadio(getYesNoButtons(), Language.EN, Theme.DREAMER_DARK, "no");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    // Test 2: Climate control - English - First option (1)
    @Test
    public void radio_climate_en_1_free_light() {
        Radio radio = createRadio(getClimateButtons(), Language.EN, Theme.FREE_LIGHT, "automatic");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_climate_en_1_free_dark() {
        Radio radio = createRadio(getClimateButtons(), Language.EN, Theme.FREE_DARK, "automatic");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_climate_en_1_dreamer_light() {
        Radio radio =
                createRadio(getClimateButtons(), Language.EN, Theme.DREAMER_LIGHT, "automatic");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_climate_en_1_dreamer_dark() {
        Radio radio =
                createRadio(getClimateButtons(), Language.EN, Theme.DREAMER_DARK, "automatic");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    // Test 2: Climate control - English - Second option (2)
    @Test
    public void radio_climate_en_2_free_light() {
        Radio radio = createRadio(getClimateButtons(), Language.EN, Theme.FREE_LIGHT, "magic");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_climate_en_2_free_dark() {
        Radio radio = createRadio(getClimateButtons(), Language.EN, Theme.FREE_DARK, "magic");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_climate_en_2_dreamer_light() {
        Radio radio = createRadio(getClimateButtons(), Language.EN, Theme.DREAMER_LIGHT, "magic");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_climate_en_2_dreamer_dark() {
        Radio radio = createRadio(getClimateButtons(), Language.EN, Theme.DREAMER_DARK, "magic");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    // Test 2: Climate control - English - Third option (3)
    @Test
    public void radio_climate_en_3_free_light() {
        Radio radio = createRadio(getClimateButtons(), Language.EN, Theme.FREE_LIGHT, "custom");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_climate_en_3_free_dark() {
        Radio radio = createRadio(getClimateButtons(), Language.EN, Theme.FREE_DARK, "custom");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_climate_en_3_dreamer_light() {
        Radio radio = createRadio(getClimateButtons(), Language.EN, Theme.DREAMER_LIGHT, "custom");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_climate_en_3_dreamer_dark() {
        Radio radio = createRadio(getClimateButtons(), Language.EN, Theme.DREAMER_DARK, "custom");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    // Test 2: Climate control - Russian - First option (1)
    @Test
    public void radio_climate_ru_1_free_light() {
        Radio radio = createRadio(getClimateButtons(), Language.RU, Theme.FREE_LIGHT, "automatic");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_climate_ru_1_free_dark() {
        Radio radio = createRadio(getClimateButtons(), Language.RU, Theme.FREE_DARK, "automatic");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_climate_ru_1_dreamer_light() {
        Radio radio =
                createRadio(getClimateButtons(), Language.RU, Theme.DREAMER_LIGHT, "automatic");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_climate_ru_1_dreamer_dark() {
        Radio radio =
                createRadio(getClimateButtons(), Language.RU, Theme.DREAMER_DARK, "automatic");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    // Test 2: Climate control - Russian - Second option (2)
    @Test
    public void radio_climate_ru_2_free_light() {
        Radio radio = createRadio(getClimateButtons(), Language.RU, Theme.FREE_LIGHT, "magic");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_climate_ru_2_free_dark() {
        Radio radio = createRadio(getClimateButtons(), Language.RU, Theme.FREE_DARK, "magic");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_climate_ru_2_dreamer_light() {
        Radio radio = createRadio(getClimateButtons(), Language.RU, Theme.DREAMER_LIGHT, "magic");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_climate_ru_2_dreamer_dark() {
        Radio radio = createRadio(getClimateButtons(), Language.RU, Theme.DREAMER_DARK, "magic");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    // Test 2: Climate control - Russian - Third option (3)
    @Test
    public void radio_climate_ru_3_free_light() {
        Radio radio = createRadio(getClimateButtons(), Language.RU, Theme.FREE_LIGHT, "custom");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_climate_ru_3_free_dark() {
        Radio radio = createRadio(getClimateButtons(), Language.RU, Theme.FREE_DARK, "custom");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_climate_ru_3_dreamer_light() {
        Radio radio = createRadio(getClimateButtons(), Language.RU, Theme.DREAMER_LIGHT, "custom");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_climate_ru_3_dreamer_dark() {
        Radio radio = createRadio(getClimateButtons(), Language.RU, Theme.DREAMER_DARK, "custom");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    // Test 3: Language selection in all four themes (English interface)
    @Test
    public void radio_languages_en_free_light() {
        Radio radio = createRadio(getLanguageButtonsEn(), Language.EN, Theme.FREE_LIGHT, "en");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_languages_en_free_dark() {
        Radio radio = createRadio(getLanguageButtonsEn(), Language.EN, Theme.FREE_DARK, "de");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_languages_en_dreamer_light() {
        Radio radio = createRadio(getLanguageButtonsEn(), Language.EN, Theme.DREAMER_LIGHT, "ru");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_languages_en_dreamer_dark() {
        Radio radio = createRadio(getLanguageButtonsEn(), Language.EN, Theme.DREAMER_DARK, "zh");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    // Test 4: Language selection in all four themes (Russian interface)
    @Test
    public void radio_languages_ru_free_light() {
        Radio radio = createRadio(getLanguageButtonsRu(), Language.RU, Theme.FREE_LIGHT, "en");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_languages_ru_free_dark() {
        Radio radio = createRadio(getLanguageButtonsRu(), Language.RU, Theme.FREE_DARK, "de");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_languages_ru_dreamer_light() {
        Radio radio = createRadio(getLanguageButtonsRu(), Language.RU, Theme.DREAMER_LIGHT, "ru");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_languages_ru_dreamer_dark() {
        Radio radio = createRadio(getLanguageButtonsRu(), Language.RU, Theme.DREAMER_DARK, "zh");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    // Animation edge case tests - testing overshoot bounds and text color transitions

    // Test data for animation edge cases (varying widths)
    private List<RadioButton> getAnimationTestButtons() {
        Map<String, String> shortLabels = new HashMap<>();
        shortLabels.put("en", "A");
        shortLabels.put("ru", "А");

        Map<String, String> mediumLabels = new HashMap<>();
        mediumLabels.put("en", "Medium");
        mediumLabels.put("ru", "Средний");

        Map<String, String> longLabels = new HashMap<>();
        longLabels.put("en", "Very Long Button Text");
        longLabels.put("ru", "Очень длинный текст кнопки");

        return Arrays.asList(
                new RadioButton("short", shortLabels),
                new RadioButton("medium", mediumLabels),
                new RadioButton("long", longLabels));
    }

    // Test animation bounds - short to long transition (maximum overshoot)
    @Test
    public void radio_animation_bounds_short_to_long_free_light() {
        Radio radio =
                createRadio(getAnimationTestButtons(), Language.EN, Theme.FREE_LIGHT, "short");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_animation_bounds_long_to_short_free_light() {
        Radio radio = createRadio(getAnimationTestButtons(), Language.EN, Theme.FREE_LIGHT, "long");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    // Test animation bounds in dreamer theme (horizontal gradient)
    @Test
    public void radio_animation_bounds_short_to_long_dreamer_dark() {
        Radio radio =
                createRadio(getAnimationTestButtons(), Language.EN, Theme.DREAMER_DARK, "short");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_animation_bounds_long_to_short_dreamer_dark() {
        Radio radio =
                createRadio(getAnimationTestButtons(), Language.EN, Theme.DREAMER_DARK, "long");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    // Test language switching with different text widths (potential jitter scenarios)
    @Test
    public void radio_language_switch_width_change_en_to_ru() {
        Radio radio =
                createRadio(getAnimationTestButtons(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_language_switch_width_change_ru_to_en() {
        Radio radio =
                createRadio(getAnimationTestButtons(), Language.RU, Theme.FREE_LIGHT, "medium");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    // ANIMATION TESTS - Testing animation behavior with unique test data
    // These tests use special animation test buttons to create unique screenshots

    // Test data for animation testing (unique values to avoid duplicates)
    private List<RadioButton> getAnimationButtons() {
        Map<String, String> firstLabels = new HashMap<>();
        firstLabels.put("en", "First Option");
        firstLabels.put("ru", "Первый вариант");

        Map<String, String> secondLabels = new HashMap<>();
        secondLabels.put("en", "Second Option");
        secondLabels.put("ru", "Второй вариант");

        Map<String, String> thirdLabels = new HashMap<>();
        thirdLabels.put("en", "Third Option");
        thirdLabels.put("ru", "Третий вариант");

        return Arrays.asList(
                new RadioButton("first", firstLabels),
                new RadioButton("second", secondLabels),
                new RadioButton("third", thirdLabels));
    }

    // Animation Test 1: Selection transitions (1->2) - Shows animation from first to second element
    @Test
    public void radio_animation_transition_1_to_2_free_light() {
        Radio radio = createRadio(getAnimationButtons(), Language.EN, Theme.FREE_LIGHT, "second");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_animation_transition_1_to_2_free_dark() {
        Radio radio = createRadio(getAnimationButtons(), Language.EN, Theme.FREE_DARK, "second");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_animation_transition_1_to_2_dreamer_light() {
        Radio radio =
                createRadio(getAnimationButtons(), Language.EN, Theme.DREAMER_LIGHT, "second");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_animation_transition_1_to_2_dreamer_dark() {
        Radio radio = createRadio(getAnimationButtons(), Language.EN, Theme.DREAMER_DARK, "second");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    // Animation Test 2: Maximum distance transitions (1->3) - Shows animation from first to last
    // element
    @Test
    public void radio_animation_transition_1_to_3_free_light() {
        Radio radio = createRadio(getAnimationButtons(), Language.EN, Theme.FREE_LIGHT, "third");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_animation_transition_1_to_3_free_dark() {
        Radio radio = createRadio(getAnimationButtons(), Language.EN, Theme.FREE_DARK, "third");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_animation_transition_1_to_3_dreamer_light() {
        Radio radio = createRadio(getAnimationButtons(), Language.EN, Theme.DREAMER_LIGHT, "third");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_animation_transition_1_to_3_dreamer_dark() {
        Radio radio = createRadio(getAnimationButtons(), Language.EN, Theme.DREAMER_DARK, "third");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    // Animation Test 3: Reverse maximum distance transitions (3->1) - Shows animation from last to
    // first element
    @Test
    public void radio_animation_transition_3_to_1_free_light() {
        Radio radio = createRadio(getAnimationButtons(), Language.EN, Theme.FREE_LIGHT, "first");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_animation_transition_3_to_1_free_dark() {
        Radio radio = createRadio(getAnimationButtons(), Language.EN, Theme.FREE_DARK, "first");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_animation_transition_3_to_1_dreamer_light() {
        Radio radio = createRadio(getAnimationButtons(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_animation_transition_3_to_1_dreamer_dark() {
        Radio radio = createRadio(getAnimationButtons(), Language.EN, Theme.DREAMER_DARK, "first");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    // Animation bounds tests - verify selection doesn't get clipped with overshoot animation
    // These tests use animation test buttons to show proper bounds handling

    @Test
    public void radio_animation_overshoot_first_element_free_light() {
        Radio radio = createRadio(getAnimationButtons(), Language.EN, Theme.FREE_LIGHT, "first");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_animation_overshoot_last_element_free_light() {
        Radio radio = createRadio(getAnimationButtons(), Language.EN, Theme.FREE_LIGHT, "third");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_animation_overshoot_first_element_dreamer_dark() {
        Radio radio = createRadio(getAnimationButtons(), Language.EN, Theme.DREAMER_DARK, "first");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_animation_overshoot_last_element_dreamer_dark() {
        Radio radio = createRadio(getAnimationButtons(), Language.EN, Theme.DREAMER_DARK, "third");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    // Russian language animation tests - verify long text doesn't affect animation bounds
    @Test
    public void radio_animation_russian_transition_first_free_light() {
        Radio radio = createRadio(getAnimationButtons(), Language.RU, Theme.FREE_LIGHT, "first");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void radio_animation_russian_transition_last_dreamer_dark() {
        Radio radio = createRadio(getAnimationButtons(), Language.RU, Theme.DREAMER_DARK, "third");
        captureRoboImage(radio, getScreenshotPath(), new RoborazziOptions());
    }

    // ============================================================
    // ANIMATION FRAME CAPTURE TESTS
    // These tests capture the animation at specific progress points
    // ============================================================

    /**
     * Helper method to capture animation at specific progress
     * @param radio Radio component
     * @param fromValue Starting value
     * @param toValue Target value
     * @param progress Animation progress (0.0 to 1.0)
     */
    private void captureAnimationFrame(
            Radio radio, String fromValue, String toValue, float progress) {
        // Get the actual test method name from stack trace before any other operations
        // We need to find the correct stack index for the test method
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String testMethodName = "unknown";
        for (int i = 2; i < stackTrace.length; i++) {
            String methodName = stackTrace[i].getMethodName();
            // Test methods start with "radio_animation_"
            if (methodName.startsWith("radio_animation_")) {
                testMethodName = methodName;
                break;
            }
        }

        // Set initial value without animation
        radio.setSelectedValue(fromValue);

        // Force layout to ensure proper positioning
        int widthMeasureSpec =
                android.view.View.MeasureSpec.makeMeasureSpec(
                        1920, android.view.View.MeasureSpec.AT_MOST);
        int heightMeasureSpec =
                android.view.View.MeasureSpec.makeMeasureSpec(
                        720, android.view.View.MeasureSpec.AT_MOST);
        radio.measure(widthMeasureSpec, heightMeasureSpec);
        radio.layout(0, 0, radio.getMeasuredWidth(), radio.getMeasuredHeight());

        // Use reflection to access animation fields and set progress
        try {
            // Get the animatedX and animatedWidth fields
            java.lang.reflect.Field animatedXField = Radio.class.getDeclaredField("animatedX");
            java.lang.reflect.Field animatedWidthField =
                    Radio.class.getDeclaredField("animatedWidth");
            java.lang.reflect.Field itemPositionsField =
                    Radio.class.getDeclaredField("itemPositions");
            java.lang.reflect.Field itemWidthsField = Radio.class.getDeclaredField("itemWidths");

            animatedXField.setAccessible(true);
            animatedWidthField.setAccessible(true);
            itemPositionsField.setAccessible(true);
            itemWidthsField.setAccessible(true);

            @SuppressWarnings("unchecked")
            java.util.List<Float> itemPositions =
                    (java.util.List<Float>) itemPositionsField.get(radio);
            @SuppressWarnings("unchecked")
            java.util.List<Float> itemWidths = (java.util.List<Float>) itemWidthsField.get(radio);

            // Find indices
            int fromIndex = -1;
            int toIndex = -1;
            java.lang.reflect.Field buttonsField = Radio.class.getDeclaredField("buttons");
            buttonsField.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.List<RadioButton> buttons =
                    (java.util.List<RadioButton>) buttonsField.get(radio);

            for (int i = 0; i < buttons.size(); i++) {
                if (buttons.get(i).getValue().equals(fromValue)) fromIndex = i;
                if (buttons.get(i).getValue().equals(toValue)) toIndex = i;
            }

            if (fromIndex >= 0 && toIndex >= 0) {
                float startX = itemPositions.get(fromIndex);
                float endX = itemPositions.get(toIndex);
                float startWidth = itemWidths.get(fromIndex);
                float endWidth = itemWidths.get(toIndex);

                // Apply overshoot interpolation manually
                float overshootProgress = calculateOvershoot(progress, 1.0f);

                float currentX = startX + (endX - startX) * overshootProgress;
                float currentWidth = startWidth + (endWidth - startWidth) * overshootProgress;

                animatedXField.setFloat(radio, currentX);
                animatedWidthField.setFloat(radio, currentWidth);

                // Force redraw
                radio.invalidate();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to set animation progress", e);
        }

        // Capture screenshot using the captured test method name
        String screenshotPath = SCREENSHOT_BASE_PATH + "/" + testMethodName + ".png";
        captureRoboImage(radio, screenshotPath, new RoborazziOptions());
    }

    /**
     * Calculate overshoot interpolation value
     * Matches OvershootInterpolator with tension 1.0
     */
    private float calculateOvershoot(float t, float tension) {
        t = t - 1.0f;
        return t * t * ((tension + 1) * t + tension) + 1.0f;
    }

    // Animation frame tests - 25% progress
    @Test
    public void radio_animation_frame_25_percent_free_light() {
        Radio radio = createRadio(getAnimationButtons(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.25f);
    }

    // Animation frame tests - 50% progress
    @Test
    public void radio_animation_frame_50_percent_free_light() {
        Radio radio = createRadio(getAnimationButtons(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.50f);
    }

    // Animation frame tests - 75% progress (before overshoot peak)
    @Test
    public void radio_animation_frame_75_percent_free_light() {
        Radio radio = createRadio(getAnimationButtons(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.75f);
    }

    // Animation frame tests - 100% progress (at overshoot peak)
    @Test
    public void radio_animation_frame_100_percent_overshoot_free_light() {
        Radio radio = createRadio(getAnimationButtons(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 1.0f);
    }

    // Animation frame tests for dreamer theme
    @Test
    public void radio_animation_frame_50_percent_dreamer_dark() {
        Radio radio = createRadio(getAnimationButtons(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "third", 0.50f);
    }

    @Test
    public void radio_animation_frame_100_percent_overshoot_dreamer_dark() {
        Radio radio = createRadio(getAnimationButtons(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "third", 1.0f);
    }

    // ============================================================
    // COMPREHENSIVE ANIMATION TESTS - 276 SCREENSHOTS
    // Systematic testing of all animation scenarios
    // ============================================================

    // Test data for comprehensive animation testing
    private List<RadioButton> getTwoButtonTestSet() {
        Map<String, String> firstLabels = new HashMap<>();
        firstLabels.put("en", "First");
        firstLabels.put("ru", "Первый");

        Map<String, String> secondLabels = new HashMap<>();
        secondLabels.put("en", "Second");
        secondLabels.put("ru", "Второй");

        return Arrays.asList(
                new RadioButton("first", firstLabels), new RadioButton("second", secondLabels));
    }

    private List<RadioButton> getThreeButtonTestSet() {
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
                new RadioButton("first", firstLabels),
                new RadioButton("second", secondLabels),
                new RadioButton("third", thirdLabels));
    }

    private List<RadioButton> getFourButtonTestSet() {
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
                new RadioButton("first", firstLabels),
                new RadioButton("second", secondLabels),
                new RadioButton("third", thirdLabels),
                new RadioButton("fourth", fourthLabels));
    }

    private List<RadioButton> getVariableWidthTestSet() {
        Map<String, String> shortLabels = new HashMap<>();
        shortLabels.put("en", "A");
        shortLabels.put("ru", "А");

        Map<String, String> mediumLabels = new HashMap<>();
        mediumLabels.put("en", "Medium");
        mediumLabels.put("ru", "Средний");

        Map<String, String> longLabels = new HashMap<>();
        longLabels.put("en", "Very Long Button Text");
        longLabels.put("ru", "Очень длинный текст кнопки");

        return Arrays.asList(
                new RadioButton("short", shortLabels),
                new RadioButton("medium", mediumLabels),
                new RadioButton("long", longLabels));
    }

    // Comprehensive animation tests with 5% progress steps
    // Creating individual test methods for each screenshot to ensure proper capture

    // Two button set - first to second transition - free_light - en
    @Test
    public void radio_animation_two_button_first_to_second_0_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.0f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_5_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.05f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_10_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.10f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_15_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.15f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_20_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.20f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_25_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.25f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_30_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.30f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_35_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.35f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_40_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.40f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_45_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.45f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_50_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.50f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_55_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.55f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_60_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.60f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_65_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.65f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_70_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.70f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_75_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.75f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_80_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.80f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_85_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.85f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_90_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.90f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_95_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.95f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_100_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 1.0f);
    }

    // ============================================================
    // COMPREHENSIVE 5% STEP ANIMATION TESTS
    // Testing all combinations with 5% progress steps (0%, 5%, 10%, ..., 100%)
    // ============================================================

    // TWO BUTTON SET - ALL COMBINATIONS (already implemented above)

    // THREE BUTTON SET - ALL COMBINATIONS (already implemented above)

    // FOUR BUTTON SET - ALL COMBINATIONS (already implemented above)

    // VARIABLE WIDTH BUTTON SET - ALL COMBINATIONS (already implemented above)

    // ============================================================
    // ADDITIONAL THEME AND LANGUAGE COMBINATIONS FOR ALL BUTTON SETS
    // ============================================================

    // Three button set - all transitions with all themes and languages

    // Three button set - first to second transition - free_light - en
    @Test
    public void radio_animation_three_button_first_to_second_0_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.0f);
    }

    @Test
    public void radio_animation_three_button_first_to_second_5_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.05f);
    }

    @Test
    public void radio_animation_three_button_first_to_second_10_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.10f);
    }

    @Test
    public void radio_animation_three_button_first_to_second_15_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.15f);
    }

    @Test
    public void radio_animation_three_button_first_to_second_20_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.20f);
    }

    @Test
    public void radio_animation_three_button_first_to_second_25_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.25f);
    }

    @Test
    public void radio_animation_three_button_first_to_second_30_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.30f);
    }

    @Test
    public void radio_animation_three_button_first_to_second_35_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.35f);
    }

    @Test
    public void radio_animation_three_button_first_to_second_40_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.40f);
    }

    @Test
    public void radio_animation_three_button_first_to_second_45_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.45f);
    }

    @Test
    public void radio_animation_three_button_first_to_second_50_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.50f);
    }

    @Test
    public void radio_animation_three_button_first_to_second_55_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.55f);
    }

    @Test
    public void radio_animation_three_button_first_to_second_60_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.60f);
    }

    @Test
    public void radio_animation_three_button_first_to_second_65_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.65f);
    }

    @Test
    public void radio_animation_three_button_first_to_second_70_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.70f);
    }

    @Test
    public void radio_animation_three_button_first_to_second_75_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.75f);
    }

    @Test
    public void radio_animation_three_button_first_to_second_80_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.80f);
    }

    @Test
    public void radio_animation_three_button_first_to_second_85_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.85f);
    }

    @Test
    public void radio_animation_three_button_first_to_second_90_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.90f);
    }

    @Test
    public void radio_animation_three_button_first_to_second_95_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.95f);
    }

    @Test
    public void radio_animation_three_button_first_to_second_100_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 1.0f);
    }

    // Three button set - first to third transition - free_light - en
    @Test
    public void radio_animation_three_button_first_to_third_0_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.0f);
    }

    @Test
    public void radio_animation_three_button_first_to_third_5_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.05f);
    }

    @Test
    public void radio_animation_three_button_first_to_third_10_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.10f);
    }

    @Test
    public void radio_animation_three_button_first_to_third_15_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.15f);
    }

    @Test
    public void radio_animation_three_button_first_to_third_20_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.20f);
    }

    @Test
    public void radio_animation_three_button_first_to_third_25_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.25f);
    }

    @Test
    public void radio_animation_three_button_first_to_third_30_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.30f);
    }

    @Test
    public void radio_animation_three_button_first_to_third_35_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.35f);
    }

    @Test
    public void radio_animation_three_button_first_to_third_40_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.40f);
    }

    @Test
    public void radio_animation_three_button_first_to_third_45_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.45f);
    }

    @Test
    public void radio_animation_three_button_first_to_third_50_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.50f);
    }

    @Test
    public void radio_animation_three_button_first_to_third_55_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.55f);
    }

    @Test
    public void radio_animation_three_button_first_to_third_60_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.60f);
    }

    @Test
    public void radio_animation_three_button_first_to_third_65_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.65f);
    }

    @Test
    public void radio_animation_three_button_first_to_third_70_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.70f);
    }

    @Test
    public void radio_animation_three_button_first_to_third_75_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.75f);
    }

    @Test
    public void radio_animation_three_button_first_to_third_80_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.80f);
    }

    @Test
    public void radio_animation_three_button_first_to_third_85_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.85f);
    }

    @Test
    public void radio_animation_three_button_first_to_third_90_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.90f);
    }

    @Test
    public void radio_animation_three_button_first_to_third_95_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.95f);
    }

    @Test
    public void radio_animation_three_button_first_to_third_100_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 1.0f);
    }

    // Three button set - second to first transition - free_light - en
    @Test
    public void radio_animation_three_button_second_to_first_0_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.0f);
    }

    @Test
    public void radio_animation_three_button_second_to_first_5_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.05f);
    }

    @Test
    public void radio_animation_three_button_second_to_first_10_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.10f);
    }

    @Test
    public void radio_animation_three_button_second_to_first_15_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.15f);
    }

    @Test
    public void radio_animation_three_button_second_to_first_20_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.20f);
    }

    @Test
    public void radio_animation_three_button_second_to_first_25_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.25f);
    }

    @Test
    public void radio_animation_three_button_second_to_first_30_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.30f);
    }

    @Test
    public void radio_animation_three_button_second_to_first_35_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.35f);
    }

    @Test
    public void radio_animation_three_button_second_to_first_40_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.40f);
    }

    @Test
    public void radio_animation_three_button_second_to_first_45_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.45f);
    }

    @Test
    public void radio_animation_three_button_second_to_first_50_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.50f);
    }

    @Test
    public void radio_animation_three_button_second_to_first_55_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.55f);
    }

    @Test
    public void radio_animation_three_button_second_to_first_60_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.60f);
    }

    @Test
    public void radio_animation_three_button_second_to_first_65_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.65f);
    }

    @Test
    public void radio_animation_three_button_second_to_first_70_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.70f);
    }

    @Test
    public void radio_animation_three_button_second_to_first_75_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.75f);
    }

    @Test
    public void radio_animation_three_button_second_to_first_80_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.80f);
    }

    @Test
    public void radio_animation_three_button_second_to_first_85_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.85f);
    }

    @Test
    public void radio_animation_three_button_second_to_first_90_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.90f);
    }

    @Test
    public void radio_animation_three_button_second_to_first_95_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.95f);
    }

    @Test
    public void radio_animation_three_button_second_to_first_100_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 1.0f);
    }

    // Three button set - second to third transition - free_light - en
    @Test
    public void radio_animation_three_button_second_to_third_0_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "third", 0.0f);
    }

    @Test
    public void radio_animation_three_button_second_to_third_5_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "third", 0.05f);
    }

    @Test
    public void radio_animation_three_button_second_to_third_10_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "third", 0.10f);
    }

    @Test
    public void radio_animation_three_button_second_to_third_15_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "third", 0.15f);
    }

    @Test
    public void radio_animation_three_button_second_to_third_20_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "third", 0.20f);
    }

    @Test
    public void radio_animation_three_button_second_to_third_25_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "third", 0.25f);
    }

    @Test
    public void radio_animation_three_button_second_to_third_30_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "third", 0.30f);
    }

    @Test
    public void radio_animation_three_button_second_to_third_35_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "third", 0.35f);
    }

    @Test
    public void radio_animation_three_button_second_to_third_40_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "third", 0.40f);
    }

    @Test
    public void radio_animation_three_button_second_to_third_45_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "third", 0.45f);
    }

    @Test
    public void radio_animation_three_button_second_to_third_50_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "third", 0.50f);
    }

    @Test
    public void radio_animation_three_button_second_to_third_55_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "third", 0.55f);
    }

    @Test
    public void radio_animation_three_button_second_to_third_60_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "third", 0.60f);
    }

    @Test
    public void radio_animation_three_button_second_to_third_65_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "third", 0.65f);
    }

    @Test
    public void radio_animation_three_button_second_to_third_70_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "third", 0.70f);
    }

    @Test
    public void radio_animation_three_button_second_to_third_75_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "third", 0.75f);
    }

    @Test
    public void radio_animation_three_button_second_to_third_80_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "third", 0.80f);
    }

    @Test
    public void radio_animation_three_button_second_to_third_85_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "third", 0.85f);
    }

    @Test
    public void radio_animation_three_button_second_to_third_90_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "third", 0.90f);
    }

    @Test
    public void radio_animation_three_button_second_to_third_95_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "third", 0.95f);
    }

    @Test
    public void radio_animation_three_button_second_to_third_100_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "third", 1.0f);
    }

    // Three button set - third to first transition - free_light - en
    @Test
    public void radio_animation_three_button_third_to_first_0_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "first", 0.0f);
    }

    @Test
    public void radio_animation_three_button_third_to_first_5_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "first", 0.05f);
    }

    @Test
    public void radio_animation_three_button_third_to_first_10_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "first", 0.10f);
    }

    @Test
    public void radio_animation_three_button_third_to_first_15_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "first", 0.15f);
    }

    @Test
    public void radio_animation_three_button_third_to_first_20_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "first", 0.20f);
    }

    @Test
    public void radio_animation_three_button_third_to_first_25_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "first", 0.25f);
    }

    @Test
    public void radio_animation_three_button_third_to_first_30_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "first", 0.30f);
    }

    @Test
    public void radio_animation_three_button_third_to_first_35_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "first", 0.35f);
    }

    @Test
    public void radio_animation_three_button_third_to_first_40_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "first", 0.40f);
    }

    @Test
    public void radio_animation_three_button_third_to_first_45_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "first", 0.45f);
    }

    @Test
    public void radio_animation_three_button_third_to_first_50_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "first", 0.50f);
    }

    @Test
    public void radio_animation_three_button_third_to_first_55_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "first", 0.55f);
    }

    @Test
    public void radio_animation_three_button_third_to_first_60_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "first", 0.60f);
    }

    @Test
    public void radio_animation_three_button_third_to_first_65_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "first", 0.65f);
    }

    @Test
    public void radio_animation_three_button_third_to_first_70_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "first", 0.70f);
    }

    @Test
    public void radio_animation_three_button_third_to_first_75_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "first", 0.75f);
    }

    @Test
    public void radio_animation_three_button_third_to_first_80_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "first", 0.80f);
    }

    @Test
    public void radio_animation_three_button_third_to_first_85_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "first", 0.85f);
    }

    @Test
    public void radio_animation_three_button_third_to_first_90_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "first", 0.90f);
    }

    @Test
    public void radio_animation_three_button_third_to_first_95_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "first", 0.95f);
    }

    @Test
    public void radio_animation_three_button_third_to_first_100_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "first", 1.0f);
    }

    // Three button set - third to second transition - free_light - en
    @Test
    public void radio_animation_three_button_third_to_second_0_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "second", 0.0f);
    }

    @Test
    public void radio_animation_three_button_third_to_second_5_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "second", 0.05f);
    }

    @Test
    public void radio_animation_three_button_third_to_second_10_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "second", 0.10f);
    }

    @Test
    public void radio_animation_three_button_third_to_second_15_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "second", 0.15f);
    }

    @Test
    public void radio_animation_three_button_third_to_second_20_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "second", 0.20f);
    }

    @Test
    public void radio_animation_three_button_third_to_second_25_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "second", 0.25f);
    }

    @Test
    public void radio_animation_three_button_third_to_second_30_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "second", 0.30f);
    }

    @Test
    public void radio_animation_three_button_third_to_second_35_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "second", 0.35f);
    }

    @Test
    public void radio_animation_three_button_third_to_second_40_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "second", 0.40f);
    }

    @Test
    public void radio_animation_three_button_third_to_second_45_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "second", 0.45f);
    }

    @Test
    public void radio_animation_three_button_third_to_second_50_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "second", 0.50f);
    }

    @Test
    public void radio_animation_three_button_third_to_second_55_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "second", 0.55f);
    }

    @Test
    public void radio_animation_three_button_third_to_second_60_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "second", 0.60f);
    }

    @Test
    public void radio_animation_three_button_third_to_second_65_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "second", 0.65f);
    }

    @Test
    public void radio_animation_three_button_third_to_second_70_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "second", 0.70f);
    }

    @Test
    public void radio_animation_three_button_third_to_second_75_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "second", 0.75f);
    }

    @Test
    public void radio_animation_three_button_third_to_second_80_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "second", 0.80f);
    }

    @Test
    public void radio_animation_three_button_third_to_second_85_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "second", 0.85f);
    }

    @Test
    public void radio_animation_three_button_third_to_second_90_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "second", 0.90f);
    }

    @Test
    public void radio_animation_three_button_third_to_second_95_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "second", 0.95f);
    }

    @Test
    public void radio_animation_three_button_third_to_second_100_percent_free_light_en() {
        Radio radio = createRadio(getThreeButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "third");
        captureAnimationFrame(radio, "third", "second", 1.0f);
    }

    // FOUR BUTTON SET - ALL COMBINATIONS

    // Four button set - first to second transition - free_light - en
    @Test
    public void radio_animation_four_button_first_to_second_0_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.0f);
    }

    @Test
    public void radio_animation_four_button_first_to_second_5_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.05f);
    }

    @Test
    public void radio_animation_four_button_first_to_second_10_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.10f);
    }

    @Test
    public void radio_animation_four_button_first_to_second_15_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.15f);
    }

    @Test
    public void radio_animation_four_button_first_to_second_20_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.20f);
    }

    @Test
    public void radio_animation_four_button_first_to_second_25_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.25f);
    }

    @Test
    public void radio_animation_four_button_first_to_second_30_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.30f);
    }

    @Test
    public void radio_animation_four_button_first_to_second_35_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.35f);
    }

    @Test
    public void radio_animation_four_button_first_to_second_40_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.40f);
    }

    @Test
    public void radio_animation_four_button_first_to_second_45_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.45f);
    }

    @Test
    public void radio_animation_four_button_first_to_second_50_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.50f);
    }

    @Test
    public void radio_animation_four_button_first_to_second_55_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.55f);
    }

    @Test
    public void radio_animation_four_button_first_to_second_60_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.60f);
    }

    @Test
    public void radio_animation_four_button_first_to_second_65_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.65f);
    }

    @Test
    public void radio_animation_four_button_first_to_second_70_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.70f);
    }

    @Test
    public void radio_animation_four_button_first_to_second_75_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.75f);
    }

    @Test
    public void radio_animation_four_button_first_to_second_80_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.80f);
    }

    @Test
    public void radio_animation_four_button_first_to_second_85_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.85f);
    }

    @Test
    public void radio_animation_four_button_first_to_second_90_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.90f);
    }

    @Test
    public void radio_animation_four_button_first_to_second_95_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.95f);
    }

    @Test
    public void radio_animation_four_button_first_to_second_100_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 1.0f);
    }

    // Four button set - first to third transition - free_light - en
    @Test
    public void radio_animation_four_button_first_to_third_0_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.0f);
    }

    @Test
    public void radio_animation_four_button_first_to_third_5_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.05f);
    }

    @Test
    public void radio_animation_four_button_first_to_third_10_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.10f);
    }

    @Test
    public void radio_animation_four_button_first_to_third_15_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.15f);
    }

    @Test
    public void radio_animation_four_button_first_to_third_20_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.20f);
    }

    @Test
    public void radio_animation_four_button_first_to_third_25_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.25f);
    }

    @Test
    public void radio_animation_four_button_first_to_third_30_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.30f);
    }

    @Test
    public void radio_animation_four_button_first_to_third_35_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.35f);
    }

    @Test
    public void radio_animation_four_button_first_to_third_40_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.40f);
    }

    @Test
    public void radio_animation_four_button_first_to_third_45_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.45f);
    }

    @Test
    public void radio_animation_four_button_first_to_third_50_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.50f);
    }

    @Test
    public void radio_animation_four_button_first_to_third_55_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.55f);
    }

    @Test
    public void radio_animation_four_button_first_to_third_60_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.60f);
    }

    @Test
    public void radio_animation_four_button_first_to_third_65_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.65f);
    }

    @Test
    public void radio_animation_four_button_first_to_third_70_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.70f);
    }

    @Test
    public void radio_animation_four_button_first_to_third_75_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.75f);
    }

    @Test
    public void radio_animation_four_button_first_to_third_80_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.80f);
    }

    @Test
    public void radio_animation_four_button_first_to_third_85_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.85f);
    }

    @Test
    public void radio_animation_four_button_first_to_third_90_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.90f);
    }

    @Test
    public void radio_animation_four_button_first_to_third_95_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 0.95f);
    }

    @Test
    public void radio_animation_four_button_first_to_third_100_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "third", 1.0f);
    }

    // Four button set - first to fourth transition - free_light - en
    @Test
    public void radio_animation_four_button_first_to_fourth_0_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "fourth", 0.0f);
    }

    @Test
    public void radio_animation_four_button_first_to_fourth_5_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "fourth", 0.05f);
    }

    @Test
    public void radio_animation_four_button_first_to_fourth_10_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "fourth", 0.10f);
    }

    @Test
    public void radio_animation_four_button_first_to_fourth_15_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "fourth", 0.15f);
    }

    @Test
    public void radio_animation_four_button_first_to_fourth_20_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "fourth", 0.20f);
    }

    @Test
    public void radio_animation_four_button_first_to_fourth_25_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "fourth", 0.25f);
    }

    @Test
    public void radio_animation_four_button_first_to_fourth_30_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "fourth", 0.30f);
    }

    @Test
    public void radio_animation_four_button_first_to_fourth_35_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "fourth", 0.35f);
    }

    @Test
    public void radio_animation_four_button_first_to_fourth_40_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "fourth", 0.40f);
    }

    @Test
    public void radio_animation_four_button_first_to_fourth_45_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "fourth", 0.45f);
    }

    @Test
    public void radio_animation_four_button_first_to_fourth_50_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "fourth", 0.50f);
    }

    @Test
    public void radio_animation_four_button_first_to_fourth_55_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "fourth", 0.55f);
    }

    @Test
    public void radio_animation_four_button_first_to_fourth_60_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "fourth", 0.60f);
    }

    @Test
    public void radio_animation_four_button_first_to_fourth_65_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "fourth", 0.65f);
    }

    @Test
    public void radio_animation_four_button_first_to_fourth_70_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "fourth", 0.70f);
    }

    @Test
    public void radio_animation_four_button_first_to_fourth_75_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "fourth", 0.75f);
    }

    @Test
    public void radio_animation_four_button_first_to_fourth_80_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "fourth", 0.80f);
    }

    @Test
    public void radio_animation_four_button_first_to_fourth_85_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "fourth", 0.85f);
    }

    @Test
    public void radio_animation_four_button_first_to_fourth_90_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "fourth", 0.90f);
    }

    @Test
    public void radio_animation_four_button_first_to_fourth_95_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "fourth", 0.95f);
    }

    @Test
    public void radio_animation_four_button_first_to_fourth_100_percent_free_light_en() {
        Radio radio = createRadio(getFourButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "fourth", 1.0f);
    }

    // VARIABLE WIDTH BUTTON SET - ALL COMBINATIONS

    // Variable width button set - short to medium transition - free_light - en
    @Test
    public void radio_animation_variable_width_short_to_medium_0_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "medium", 0.0f);
    }

    @Test
    public void radio_animation_variable_width_short_to_medium_5_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "medium", 0.05f);
    }

    @Test
    public void radio_animation_variable_width_short_to_medium_10_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "medium", 0.10f);
    }

    @Test
    public void radio_animation_variable_width_short_to_medium_15_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "medium", 0.15f);
    }

    @Test
    public void radio_animation_variable_width_short_to_medium_20_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "medium", 0.20f);
    }

    @Test
    public void radio_animation_variable_width_short_to_medium_25_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "medium", 0.25f);
    }

    @Test
    public void radio_animation_variable_width_short_to_medium_30_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "medium", 0.30f);
    }

    @Test
    public void radio_animation_variable_width_short_to_medium_35_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "medium", 0.35f);
    }

    @Test
    public void radio_animation_variable_width_short_to_medium_40_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "medium", 0.40f);
    }

    @Test
    public void radio_animation_variable_width_short_to_medium_45_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "medium", 0.45f);
    }

    @Test
    public void radio_animation_variable_width_short_to_medium_50_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "medium", 0.50f);
    }

    @Test
    public void radio_animation_variable_width_short_to_medium_55_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "medium", 0.55f);
    }

    @Test
    public void radio_animation_variable_width_short_to_medium_60_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "medium", 0.60f);
    }

    @Test
    public void radio_animation_variable_width_short_to_medium_65_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "medium", 0.65f);
    }

    @Test
    public void radio_animation_variable_width_short_to_medium_70_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "medium", 0.70f);
    }

    @Test
    public void radio_animation_variable_width_short_to_medium_75_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "medium", 0.75f);
    }

    @Test
    public void radio_animation_variable_width_short_to_medium_80_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "medium", 0.80f);
    }

    @Test
    public void radio_animation_variable_width_short_to_medium_85_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "medium", 0.85f);
    }

    @Test
    public void radio_animation_variable_width_short_to_medium_90_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "medium", 0.90f);
    }

    @Test
    public void radio_animation_variable_width_short_to_medium_95_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "medium", 0.95f);
    }

    @Test
    public void radio_animation_variable_width_short_to_medium_100_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "medium", 1.0f);
    }

    // Variable width button set - short to long transition - free_light - en
    @Test
    public void radio_animation_variable_width_short_to_long_0_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "long", 0.0f);
    }

    @Test
    public void radio_animation_variable_width_short_to_long_5_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "long", 0.05f);
    }

    @Test
    public void radio_animation_variable_width_short_to_long_10_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "long", 0.10f);
    }

    @Test
    public void radio_animation_variable_width_short_to_long_15_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "long", 0.15f);
    }

    @Test
    public void radio_animation_variable_width_short_to_long_20_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "long", 0.20f);
    }

    @Test
    public void radio_animation_variable_width_short_to_long_25_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "long", 0.25f);
    }

    @Test
    public void radio_animation_variable_width_short_to_long_30_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "long", 0.30f);
    }

    @Test
    public void radio_animation_variable_width_short_to_long_35_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "long", 0.35f);
    }

    @Test
    public void radio_animation_variable_width_short_to_long_40_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "long", 0.40f);
    }

    @Test
    public void radio_animation_variable_width_short_to_long_45_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "long", 0.45f);
    }

    @Test
    public void radio_animation_variable_width_short_to_long_50_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "long", 0.50f);
    }

    @Test
    public void radio_animation_variable_width_short_to_long_55_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "long", 0.55f);
    }

    @Test
    public void radio_animation_variable_width_short_to_long_60_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "long", 0.60f);
    }

    @Test
    public void radio_animation_variable_width_short_to_long_65_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "long", 0.65f);
    }

    @Test
    public void radio_animation_variable_width_short_to_long_70_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "long", 0.70f);
    }

    @Test
    public void radio_animation_variable_width_short_to_long_75_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "long", 0.75f);
    }

    @Test
    public void radio_animation_variable_width_short_to_long_80_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "long", 0.80f);
    }

    @Test
    public void radio_animation_variable_width_short_to_long_85_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "long", 0.85f);
    }

    @Test
    public void radio_animation_variable_width_short_to_long_90_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "long", 0.90f);
    }

    @Test
    public void radio_animation_variable_width_short_to_long_95_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "long", 0.95f);
    }

    @Test
    public void radio_animation_variable_width_short_to_long_100_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "short");
        captureAnimationFrame(radio, "short", "long", 1.0f);
    }

    // Variable width button set - medium to short transition - free_light - en
    @Test
    public void radio_animation_variable_width_medium_to_short_0_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "short", 0.0f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_short_5_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "short", 0.05f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_short_10_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "short", 0.10f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_short_15_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "short", 0.15f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_short_20_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "short", 0.20f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_short_25_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "short", 0.25f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_short_30_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "short", 0.30f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_short_35_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "short", 0.35f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_short_40_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "short", 0.40f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_short_45_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "short", 0.45f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_short_50_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "short", 0.50f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_short_55_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "short", 0.55f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_short_60_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "short", 0.60f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_short_65_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "short", 0.65f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_short_70_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "short", 0.70f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_short_75_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "short", 0.75f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_short_80_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "short", 0.80f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_short_85_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "short", 0.85f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_short_90_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "short", 0.90f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_short_95_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "short", 0.95f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_short_100_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "short", 1.0f);
    }

    // Variable width button set - medium to long transition - free_light - en
    @Test
    public void radio_animation_variable_width_medium_to_long_0_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "long", 0.0f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_long_5_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "long", 0.05f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_long_10_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "long", 0.10f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_long_15_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "long", 0.15f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_long_20_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "long", 0.20f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_long_25_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "long", 0.25f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_long_30_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "long", 0.30f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_long_35_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "long", 0.35f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_long_40_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "long", 0.40f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_long_45_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "long", 0.45f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_long_50_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "long", 0.50f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_long_55_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "long", 0.55f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_long_60_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "long", 0.60f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_long_65_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "long", 0.65f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_long_70_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "long", 0.70f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_long_75_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "long", 0.75f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_long_80_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "long", 0.80f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_long_85_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "long", 0.85f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_long_90_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "long", 0.90f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_long_95_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "long", 0.95f);
    }

    @Test
    public void radio_animation_variable_width_medium_to_long_100_percent_free_light_en() {
        Radio radio =
                createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "medium");
        captureAnimationFrame(radio, "medium", "long", 1.0f);
    }

    // Variable width button set - long to short transition - free_light - en
    @Test
    public void radio_animation_variable_width_long_to_short_0_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "short", 0.0f);
    }

    @Test
    public void radio_animation_variable_width_long_to_short_5_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "short", 0.05f);
    }

    @Test
    public void radio_animation_variable_width_long_to_short_10_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "short", 0.10f);
    }

    @Test
    public void radio_animation_variable_width_long_to_short_15_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "short", 0.15f);
    }

    @Test
    public void radio_animation_variable_width_long_to_short_20_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "short", 0.20f);
    }

    @Test
    public void radio_animation_variable_width_long_to_short_25_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "short", 0.25f);
    }

    @Test
    public void radio_animation_variable_width_long_to_short_30_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "short", 0.30f);
    }

    @Test
    public void radio_animation_variable_width_long_to_short_35_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "short", 0.35f);
    }

    @Test
    public void radio_animation_variable_width_long_to_short_40_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "short", 0.40f);
    }

    @Test
    public void radio_animation_variable_width_long_to_short_45_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "short", 0.45f);
    }

    @Test
    public void radio_animation_variable_width_long_to_short_50_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "short", 0.50f);
    }

    @Test
    public void radio_animation_variable_width_long_to_short_55_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "short", 0.55f);
    }

    @Test
    public void radio_animation_variable_width_long_to_short_60_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "short", 0.60f);
    }

    @Test
    public void radio_animation_variable_width_long_to_short_65_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "short", 0.65f);
    }

    @Test
    public void radio_animation_variable_width_long_to_short_70_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "short", 0.70f);
    }

    @Test
    public void radio_animation_variable_width_long_to_short_75_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "short", 0.75f);
    }

    @Test
    public void radio_animation_variable_width_long_to_short_80_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "short", 0.80f);
    }

    @Test
    public void radio_animation_variable_width_long_to_short_85_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "short", 0.85f);
    }

    @Test
    public void radio_animation_variable_width_long_to_short_90_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "short", 0.90f);
    }

    @Test
    public void radio_animation_variable_width_long_to_short_95_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "short", 0.95f);
    }

    @Test
    public void radio_animation_variable_width_long_to_short_100_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "short", 1.0f);
    }

    // Variable width button set - long to medium transition - free_light - en
    @Test
    public void radio_animation_variable_width_long_to_medium_0_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "medium", 0.0f);
    }

    @Test
    public void radio_animation_variable_width_long_to_medium_5_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "medium", 0.05f);
    }

    @Test
    public void radio_animation_variable_width_long_to_medium_10_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "medium", 0.10f);
    }

    @Test
    public void radio_animation_variable_width_long_to_medium_15_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "medium", 0.15f);
    }

    @Test
    public void radio_animation_variable_width_long_to_medium_20_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "medium", 0.20f);
    }

    @Test
    public void radio_animation_variable_width_long_to_medium_25_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "medium", 0.25f);
    }

    @Test
    public void radio_animation_variable_width_long_to_medium_30_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "medium", 0.30f);
    }

    @Test
    public void radio_animation_variable_width_long_to_medium_35_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "medium", 0.35f);
    }

    @Test
    public void radio_animation_variable_width_long_to_medium_40_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "medium", 0.40f);
    }

    @Test
    public void radio_animation_variable_width_long_to_medium_45_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "medium", 0.45f);
    }

    @Test
    public void radio_animation_variable_width_long_to_medium_50_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "medium", 0.50f);
    }

    @Test
    public void radio_animation_variable_width_long_to_medium_55_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "medium", 0.55f);
    }

    @Test
    public void radio_animation_variable_width_long_to_medium_60_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "medium", 0.60f);
    }

    @Test
    public void radio_animation_variable_width_long_to_medium_65_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "medium", 0.65f);
    }

    @Test
    public void radio_animation_variable_width_long_to_medium_70_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "medium", 0.70f);
    }

    @Test
    public void radio_animation_variable_width_long_to_medium_75_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "medium", 0.75f);
    }

    @Test
    public void radio_animation_variable_width_long_to_medium_80_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "medium", 0.80f);
    }

    @Test
    public void radio_animation_variable_width_long_to_medium_85_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "medium", 0.85f);
    }

    @Test
    public void radio_animation_variable_width_long_to_medium_90_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "medium", 0.90f);
    }

    @Test
    public void radio_animation_variable_width_long_to_medium_95_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "medium", 0.95f);
    }

    @Test
    public void radio_animation_variable_width_long_to_medium_100_percent_free_light_en() {
        Radio radio = createRadio(getVariableWidthTestSet(), Language.EN, Theme.FREE_LIGHT, "long");
        captureAnimationFrame(radio, "long", "medium", 1.0f);
    }

    // Two button set - first to second transition - free_light - ru
    @Test
    public void radio_animation_two_button_first_to_second_0_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.0f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_5_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.05f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_10_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.10f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_15_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.15f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_20_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.20f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_25_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.25f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_30_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.30f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_35_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.35f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_40_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.40f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_45_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.45f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_50_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.50f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_55_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.55f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_60_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.60f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_65_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.65f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_70_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.70f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_75_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.75f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_80_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.80f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_85_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.85f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_90_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.90f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_95_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.95f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_100_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 1.0f);
    }

    // Two button set - first to second transition - free_dark - en
    @Test
    public void radio_animation_two_button_first_to_second_0_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.0f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_5_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.05f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_10_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.10f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_15_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.15f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_20_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.20f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_25_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.25f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_30_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.30f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_35_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.35f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_40_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.40f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_45_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.45f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_50_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.50f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_55_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.55f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_60_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.60f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_65_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.65f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_70_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.70f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_75_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.75f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_80_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.80f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_85_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.85f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_90_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.90f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_95_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.95f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_100_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 1.0f);
    }

    // Two button set - first to second transition - free_dark - ru
    @Test
    public void radio_animation_two_button_first_to_second_0_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.0f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_5_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.05f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_10_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.10f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_15_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.15f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_20_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.20f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_25_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.25f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_30_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.30f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_35_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.35f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_40_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.40f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_45_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.45f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_50_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.50f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_55_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.55f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_60_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.60f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_65_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.65f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_70_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.70f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_75_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.75f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_80_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.80f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_85_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.85f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_90_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.90f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_95_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.95f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_100_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 1.0f);
    }

    // Two button set - first to second transition - dreamer_light - en
    @Test
    public void radio_animation_two_button_first_to_second_0_percent_dreamer_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.0f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_5_percent_dreamer_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.05f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_10_percent_dreamer_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.10f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_15_percent_dreamer_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.15f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_20_percent_dreamer_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.20f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_25_percent_dreamer_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.25f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_30_percent_dreamer_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.30f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_35_percent_dreamer_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.35f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_40_percent_dreamer_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.40f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_45_percent_dreamer_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.45f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_50_percent_dreamer_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.50f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_55_percent_dreamer_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.55f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_60_percent_dreamer_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.60f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_65_percent_dreamer_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.65f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_70_percent_dreamer_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.70f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_75_percent_dreamer_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.75f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_80_percent_dreamer_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.80f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_85_percent_dreamer_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.85f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_90_percent_dreamer_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.90f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_95_percent_dreamer_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.95f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_100_percent_dreamer_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 1.0f);
    }

    // Two button set - first to second transition - dreamer_light - ru
    @Test
    public void radio_animation_two_button_first_to_second_0_percent_dreamer_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.0f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_5_percent_dreamer_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.05f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_10_percent_dreamer_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.10f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_15_percent_dreamer_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.15f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_20_percent_dreamer_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.20f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_25_percent_dreamer_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.25f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_30_percent_dreamer_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.30f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_35_percent_dreamer_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.35f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_40_percent_dreamer_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.40f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_45_percent_dreamer_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.45f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_50_percent_dreamer_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.50f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_55_percent_dreamer_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.55f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_60_percent_dreamer_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.60f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_65_percent_dreamer_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.65f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_70_percent_dreamer_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.70f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_75_percent_dreamer_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.75f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_80_percent_dreamer_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.80f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_85_percent_dreamer_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.85f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_90_percent_dreamer_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.90f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_95_percent_dreamer_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 0.95f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_100_percent_dreamer_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 1.0f);
    }

    // Two button set - first to second transition - dreamer_dark - en
    @Test
    public void radio_animation_two_button_first_to_second_0_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.0f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_5_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.05f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_10_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.10f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_15_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.15f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_20_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.20f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_25_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.25f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_30_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.30f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_35_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.35f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_40_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.40f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_45_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.45f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_50_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.50f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_55_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.55f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_60_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.60f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_65_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.65f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_70_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.70f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_75_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.75f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_80_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.80f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_85_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.85f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_90_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.90f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_95_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.95f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_100_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 1.0f);
    }

    // Two button set - first to second transition - dreamer_dark - ru
    @Test
    public void radio_animation_two_button_first_to_second_0_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.0f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_5_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.05f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_10_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.10f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_15_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.15f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_20_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.20f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_25_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.25f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_30_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.30f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_35_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.35f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_40_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.40f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_45_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.45f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_50_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.50f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_55_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.55f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_60_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.60f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_65_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.65f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_70_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.70f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_75_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.75f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_80_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.80f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_85_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.85f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_90_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.90f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_95_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 0.95f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_100_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 1.0f);
    }

    // Two button set - second to first transition - free_light - en
    @Test
    public void radio_animation_two_button_second_to_first_0_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.0f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_5_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.05f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_10_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.10f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_15_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.15f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_20_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.20f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_25_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.25f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_30_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.30f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_35_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.35f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_40_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.40f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_45_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.45f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_50_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.50f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_55_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.55f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_60_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.60f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_65_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.65f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_70_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.70f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_75_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.75f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_80_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.80f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_85_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.85f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_90_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.90f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_95_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.95f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_100_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 1.0f);
    }

    // Two button set - second to first transition - free_light - ru
    @Test
    public void radio_animation_two_button_second_to_first_0_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.0f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_5_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.05f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_10_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.10f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_15_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.15f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_20_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.20f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_25_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.25f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_30_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.30f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_35_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.35f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_40_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.40f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_45_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.45f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_50_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.50f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_55_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.55f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_60_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.60f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_65_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.65f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_70_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.70f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_75_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.75f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_80_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.80f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_85_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.85f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_90_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.90f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_95_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.95f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_100_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 1.0f);
    }

    // Two button set - second to first transition - free_dark - en
    @Test
    public void radio_animation_two_button_second_to_first_0_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.0f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_5_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.05f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_10_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.10f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_15_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.15f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_20_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.20f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_25_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.25f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_30_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.30f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_35_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.35f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_40_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.40f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_45_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.45f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_50_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.50f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_55_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.55f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_60_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.60f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_65_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.65f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_70_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.70f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_75_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.75f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_80_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.80f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_85_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.85f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_90_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.90f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_95_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.95f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_100_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 1.0f);
    }

    // Two button set - second to first transition - free_dark - ru
    @Test
    public void radio_animation_two_button_second_to_first_0_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.0f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_5_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.05f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_10_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.10f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_15_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.15f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_20_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.20f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_25_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.25f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_30_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.30f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_35_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.35f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_40_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.40f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_45_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.45f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_50_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.50f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_55_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.55f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_60_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.60f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_65_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.65f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_70_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.70f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_75_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.75f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_80_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.80f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_85_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.85f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_90_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.90f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_95_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.95f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_100_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 1.0f);
    }

    // Two button set - second to first transition - dreamer_light - en
    @Test
    public void radio_animation_two_button_second_to_first_0_percent_dreamer_light_en() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.0f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_5_percent_dreamer_light_en() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.05f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_10_percent_dreamer_light_en() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.10f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_15_percent_dreamer_light_en() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.15f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_20_percent_dreamer_light_en() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.20f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_25_percent_dreamer_light_en() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.25f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_30_percent_dreamer_light_en() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.30f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_35_percent_dreamer_light_en() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.35f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_40_percent_dreamer_light_en() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.40f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_45_percent_dreamer_light_en() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.45f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_50_percent_dreamer_light_en() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.50f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_55_percent_dreamer_light_en() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.55f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_60_percent_dreamer_light_en() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.60f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_65_percent_dreamer_light_en() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.65f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_70_percent_dreamer_light_en() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.70f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_75_percent_dreamer_light_en() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.75f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_80_percent_dreamer_light_en() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.80f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_85_percent_dreamer_light_en() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.85f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_90_percent_dreamer_light_en() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.90f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_95_percent_dreamer_light_en() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.95f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_100_percent_dreamer_light_en() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 1.0f);
    }

    // Two button set - second to first transition - dreamer_light - ru
    @Test
    public void radio_animation_two_button_second_to_first_0_percent_dreamer_light_ru() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.0f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_5_percent_dreamer_light_ru() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.05f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_10_percent_dreamer_light_ru() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.10f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_15_percent_dreamer_light_ru() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.15f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_20_percent_dreamer_light_ru() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.20f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_25_percent_dreamer_light_ru() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.25f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_30_percent_dreamer_light_ru() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.30f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_35_percent_dreamer_light_ru() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.35f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_40_percent_dreamer_light_ru() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.40f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_45_percent_dreamer_light_ru() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.45f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_50_percent_dreamer_light_ru() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.50f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_55_percent_dreamer_light_ru() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.55f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_60_percent_dreamer_light_ru() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.60f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_65_percent_dreamer_light_ru() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.65f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_70_percent_dreamer_light_ru() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.70f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_75_percent_dreamer_light_ru() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.75f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_80_percent_dreamer_light_ru() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.80f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_85_percent_dreamer_light_ru() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.85f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_90_percent_dreamer_light_ru() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.90f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_95_percent_dreamer_light_ru() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 0.95f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_100_percent_dreamer_light_ru() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 1.0f);
    }

    // Two button set - second to first transition - dreamer_dark - en
    @Test
    public void radio_animation_two_button_second_to_first_0_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.0f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_5_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.05f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_10_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.10f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_15_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.15f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_20_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.20f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_25_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.25f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_30_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.30f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_35_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.35f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_40_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.40f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_45_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.45f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_50_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.50f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_55_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.55f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_60_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.60f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_65_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.65f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_70_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.70f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_75_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.75f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_80_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.80f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_85_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.85f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_90_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.90f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_95_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.95f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_100_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 1.0f);
    }

    // Two button set - second to first transition - dreamer_dark - ru
    @Test
    public void radio_animation_two_button_second_to_first_0_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.0f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_5_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.05f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_10_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.10f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_15_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.15f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_20_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.20f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_25_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.25f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_30_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.30f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_35_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.35f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_40_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.40f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_45_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.45f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_50_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.50f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_55_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.55f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_60_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.60f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_65_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.65f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_70_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.70f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_75_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.75f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_80_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.80f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_85_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.85f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_90_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.90f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_95_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 0.95f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_100_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 1.0f);
    }

    // ============================================================
    // OVERSHOOT ANIMATION TESTS (105% and 110%)
    // These test the overshoot behavior beyond 100% progress
    // ============================================================

    // Two button set - first to second transition - 105% - all themes and languages
    @Test
    public void radio_animation_two_button_first_to_second_105_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 1.05f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_105_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 1.05f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_105_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 1.05f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_105_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 1.05f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_105_percent_dreamer_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 1.05f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_105_percent_dreamer_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 1.05f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_105_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 1.05f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_105_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 1.05f);
    }

    // Two button set - first to second transition - 110% - all themes and languages
    @Test
    public void radio_animation_two_button_first_to_second_110_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 1.10f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_110_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 1.10f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_110_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 1.10f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_110_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 1.10f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_110_percent_dreamer_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 1.10f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_110_percent_dreamer_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "first");
        captureAnimationFrame(radio, "first", "second", 1.10f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_110_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 1.10f);
    }

    @Test
    public void radio_animation_two_button_first_to_second_110_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "first");
        captureAnimationFrame(radio, "first", "second", 1.10f);
    }

    // Two button set - second to first transition - 105% - all themes and languages
    @Test
    public void radio_animation_two_button_second_to_first_105_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 1.05f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_105_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 1.05f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_105_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 1.05f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_105_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 1.05f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_105_percent_dreamer_light_en() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 1.05f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_105_percent_dreamer_light_ru() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 1.05f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_105_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 1.05f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_105_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 1.05f);
    }

    // Two button set - second to first transition - 110% - all themes and languages
    @Test
    public void radio_animation_two_button_second_to_first_110_percent_free_light_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 1.10f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_110_percent_free_light_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 1.10f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_110_percent_free_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 1.10f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_110_percent_free_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.FREE_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 1.10f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_110_percent_dreamer_light_en() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 1.10f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_110_percent_dreamer_light_ru() {
        Radio radio =
                createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_LIGHT, "second");
        captureAnimationFrame(radio, "second", "first", 1.10f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_110_percent_dreamer_dark_en() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.EN, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 1.10f);
    }

    @Test
    public void radio_animation_two_button_second_to_first_110_percent_dreamer_dark_ru() {
        Radio radio = createRadio(getTwoButtonTestSet(), Language.RU, Theme.DREAMER_DARK, "second");
        captureAnimationFrame(radio, "second", "first", 1.10f);
    }
}
