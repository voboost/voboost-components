package ru.voboost.components.section;

import static com.github.takahirom.roborazzi.RoborazziKt.captureRoboImage;

import java.util.HashMap;
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
import ru.voboost.components.radio.Radio;
import ru.voboost.components.theme.Theme;

/**
 * Visual regression tests for the Section component using Roborazzi.
 */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
        sdk = {33},
        qualifiers = "w1920dp-h720dp-land-mdpi")
public class SectionTestVisual {

    private static final String SCREENSHOT_BASE_PATH =
            "src/main/java/ru/voboost/components/section/Section.screenshots";

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
            // Test methods start with "section_"
            if (methodName.startsWith("section_")) {
                testMethodName = methodName;
                break;
            }
        }
        return SCREENSHOT_BASE_PATH + "/" + testMethodName + ".png";
    }

    private Section createSection(Theme theme, Language language, Map<String, String> title) {
        Section section = new Section(context);
        section.setTheme(theme);
        section.setLanguage(language);
        section.setTitle(title);

        // Set layout parameters for proper rendering
        ViewGroup.LayoutParams params =
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        section.setLayoutParams(params);

        // Force measurement and layout for screenshot capture with proper dimensions
        int widthMeasureSpec =
                android.view.View.MeasureSpec.makeMeasureSpec(
                        1920, android.view.View.MeasureSpec.AT_MOST);
        int heightMeasureSpec =
                android.view.View.MeasureSpec.makeMeasureSpec(
                        720, android.view.View.MeasureSpec.AT_MOST);

        section.measure(widthMeasureSpec, heightMeasureSpec);

        // Ensure we have valid measured dimensions
        int measuredWidth = section.getMeasuredWidth();
        int measuredHeight = section.getMeasuredHeight();

        if (measuredWidth <= 0 || measuredHeight <= 0) {
            throw new IllegalArgumentException(
                    "Section component has invalid measured dimensions: "
                            + measuredWidth
                            + "x"
                            + measuredHeight);
        }

        section.layout(0, 0, measuredWidth, measuredHeight);

        // Add to container (attached to Activity) for Roborazzi screenshot capture
        container.removeAllViews();
        container.addView(section);

        return section;
    }

    @Test
    public void section_freeLightEnglish() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Settings");
        title.put("ru", "Настройки");

        Section section = createSection(Theme.FREE_LIGHT, Language.EN, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_freeLightRussian() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Settings");
        title.put("ru", "Настройки");

        Section section = createSection(Theme.FREE_LIGHT, Language.RU, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_freeDarkEnglish() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Vehicle Settings");
        title.put("ru", "Настройки автомобиля");

        Section section = createSection(Theme.FREE_DARK, Language.EN, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_dreamerLightEnglish() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Applications");
        title.put("ru", "Приложения");

        Section section = createSection(Theme.DREAMER_LIGHT, Language.EN, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_dreamerDarkRussian() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Interface");
        title.put("ru", "Интерфейс");

        Section section = createSection(Theme.DREAMER_DARK, Language.RU, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_withContent() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Content Section");
        title.put("ru", "Раздел с содержимым");

        Section section = createSection(Theme.FREE_LIGHT, Language.EN, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    // ============================================================
    // ENHANCED CONTENT SCENARIOS - PHASE 3
    // ============================================================

    @Test
    public void section_longTitleWrapping() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "This is a very long section title that should wrap to multiple lines");
        title.put(
                "ru",
                "Это очень длинный заголовок раздела, который должен переноситься на несколько"
                        + " строк");

        Section section = createSection(Theme.FREE_LIGHT, Language.EN, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_minimalContent() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "A");
        title.put("ru", "А");

        Section section = createSection(Theme.FREE_DARK, Language.EN, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_specialCharacters() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Settings & Options! @#$%^&*()");
        title.put("ru", "Настройки & Опции! @#$%^&*()");

        Section section = createSection(Theme.DREAMER_LIGHT, Language.EN, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_multiLanguageComplex() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Advanced Vehicle Configuration Panel");
        title.put("ru", "Панель расширенной конфигурации автомобиля");

        Section section = createSection(Theme.DREAMER_DARK, Language.RU, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_edgeCaseEmptyTitle() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "");
        title.put("ru", "");

        Section section = createSection(Theme.FREE_LIGHT, Language.EN, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_maximalContentDreamer() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Comprehensive System Settings and Configuration Management Interface");
        title.put("ru", "Комплексный интерфейс управления настройками и конфигурацией системы");

        Section section = createSection(Theme.DREAMER_LIGHT, Language.EN, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_mixedContentFreeDark() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Audio/Video Settings + Navigation + Climate Control");
        title.put("ru", "Аудио/Видео настройки + Навигация + Климат-контроль");

        Section section = createSection(Theme.FREE_DARK, Language.RU, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_technicalTerms() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "CAN Bus Diagnostics & ECU Parameters");
        title.put("ru", "Диагностика CAN-шины & Параметры ЭБУ");

        Section section = createSection(Theme.FREE_LIGHT, Language.EN, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_fullCombinationRussian() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Complete System Overview with All Available Options and Settings");
        title.put("ru", "Полный обзор системы со всеми доступными опциями и настройками");

        Section section = createSection(Theme.DREAMER_DARK, Language.RU, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_shortTitleFreeLight() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Info");
        title.put("ru", "Инфо");

        Section section = createSection(Theme.FREE_LIGHT, Language.EN, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_shortTitleFreeDark() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Info");
        title.put("ru", "Инфо");

        Section section = createSection(Theme.FREE_DARK, Language.EN, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_shortTitleDreamerLight() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Info");
        title.put("ru", "Инфо");

        Section section = createSection(Theme.DREAMER_LIGHT, Language.EN, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_shortTitleDreamerDark() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Info");
        title.put("ru", "Инфо");

        Section section = createSection(Theme.DREAMER_DARK, Language.EN, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    // ============================================================
    // SECTION WITH RADIO CHILD TESTS
    // ============================================================

    /**
     * Creates a Section with a Radio child for visual testing.
     */
    private Section createSectionWithRadio(
            Theme theme,
            Language language,
            Map<String, String> title,
            java.util.List<ru.voboost.components.radio.RadioButton> radioButtons,
            String selectedValue) {
        Section section = new Section(context);
        section.setTheme(theme);
        section.setLanguage(language);
        section.setTitle(title);

        // Add Radio using the typed API
        Radio radio = Radio.create(context, theme, language, radioButtons, selectedValue).build();
        section.addRadio(radio);

        // Set layout parameters for proper rendering
        ViewGroup.LayoutParams params =
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        section.setLayoutParams(params);

        // Force measurement and layout
        int widthMeasureSpec =
                android.view.View.MeasureSpec.makeMeasureSpec(
                        1920, android.view.View.MeasureSpec.AT_MOST);
        int heightMeasureSpec =
                android.view.View.MeasureSpec.makeMeasureSpec(
                        720, android.view.View.MeasureSpec.AT_MOST);

        section.measure(widthMeasureSpec, heightMeasureSpec);

        int measuredWidth = section.getMeasuredWidth();
        int measuredHeight = section.getMeasuredHeight();

        if (measuredWidth <= 0 || measuredHeight <= 0) {
            throw new IllegalArgumentException(
                    "Section component has invalid measured dimensions: "
                            + measuredWidth
                            + "x"
                            + measuredHeight);
        }

        section.layout(0, 0, measuredWidth, measuredHeight);

        container.removeAllViews();
        container.addView(section);

        return section;
    }

    /**
     * Helper to create sample radio buttons for testing.
     */
    private java.util.List<ru.voboost.components.radio.RadioButton> createSampleRadioButtons() {
        java.util.List<ru.voboost.components.radio.RadioButton> buttons =
                new java.util.ArrayList<>();

        Map<String, String> label1 = new HashMap<>();
        label1.put("en", "English");
        label1.put("ru", "Английский");
        buttons.add(new ru.voboost.components.radio.RadioButton("en", label1));

        Map<String, String> label2 = new HashMap<>();
        label2.put("en", "Russian");
        label2.put("ru", "Русский");
        buttons.add(new ru.voboost.components.radio.RadioButton("ru", label2));

        return buttons;
    }

    @Test
    public void section_withRadioFreeLightEnglish() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Language Selection");
        title.put("ru", "Выбор языка");

        Section section =
                createSectionWithRadio(
                        Theme.FREE_LIGHT, Language.EN, title, createSampleRadioButtons(), "en");
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_withRadioFreeDarkRussian() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Language Selection");
        title.put("ru", "Выбор языка");

        Section section =
                createSectionWithRadio(
                        Theme.FREE_DARK, Language.RU, title, createSampleRadioButtons(), "ru");
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_withRadioDreamerLightEnglish() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Theme");
        title.put("ru", "Тема");

        Section section =
                createSectionWithRadio(
                        Theme.DREAMER_LIGHT, Language.EN, title, createSampleRadioButtons(), "en");
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_withRadioDreamerDarkRussian() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Theme");
        title.put("ru", "Тема");

        Section section =
                createSectionWithRadio(
                        Theme.DREAMER_DARK, Language.RU, title, createSampleRadioButtons(), "ru");
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    // ============================================================
    // EDGE CASE VISUAL TESTS - Phase 5
    // ============================================================

    /**
     * Test Section with very long title that wraps to multiple lines.
     */
    @Test
    public void section_veryLongTitleMultipleLines() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "This is an extremely long section title that should definitely wrap to multiple lines and test the layout engine");
        title.put("ru", "Это чрезвычайно длинный заголовок раздела, который точно должен переноситься на несколько строк и проверять движок верстки");

        Section section = createSection(Theme.FREE_LIGHT, Language.EN, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    /**
     * Test Section with title containing only special characters.
     */
    @Test
    public void section_specialCharactersOnly() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "@#$%^&*()_+-=[]{}|;':\",./<>?");
        title.put("ru", "@#$%^&*()_+-=[]{}|;':\",./<>?");

        Section section = createSection(Theme.FREE_DARK, Language.EN, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    /**
     * Test Section with title containing emoji.
     */
    @Test
    public void section_withEmoji() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Settings ⚙️ 🎛️ 🔧");
        title.put("ru", "Настройки ⚙️ 🎛️ 🔧");

        Section section = createSection(Theme.DREAMER_LIGHT, Language.EN, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    /**
     * Test Section with mixed RTL/LTR text.
     */
    @Test
    public void section_mixedRtlLtr() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "English Settings الإعدادات");
        title.put("ru", "Русский Настройки הגדרות");

        Section section = createSection(Theme.FREE_LIGHT, Language.EN, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    /**
     * Test Section with title containing numbers and special formatting.
     */
    @Test
    public void section_withNumbersAndFormatting() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Version 2.0.1 (Build #1234) [Stable]");
        title.put("ru", "Версия 2.0.1 (Сборка #1234) [Стабильная]");

        Section section = createSection(Theme.DREAMER_DARK, Language.EN, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    /**
     * Test Section with single character title.
     */
    @Test
    public void section_singleCharacterTitle() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "A");
        title.put("ru", "А");

        Section section = createSection(Theme.FREE_LIGHT, Language.EN, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    /**
     * Test Section with title containing newlines (should be handled gracefully).
     */
    @Test
    public void section_titleWithNewlines() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Line 1\nLine 2\nLine 3");

        Section section = createSection(Theme.FREE_DARK, Language.EN, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    /**
     * Test Section with title containing tabs (should be handled gracefully).
     */
    @Test
    public void section_titleWithTabs() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Tab\tSeparated\tWords");

        Section section = createSection(Theme.DREAMER_LIGHT, Language.EN, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    /**
     * Test Section with all theme combinations for very short title.
     */
    @Test
    public void section_shortTitleAllThemes() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "OK");
        title.put("ru", "ОК");

        // Test all 4 theme variants
        Section section1 = createSection(Theme.FREE_LIGHT, Language.EN, title);
        captureRoboImage(section1, getScreenshotName() + "_free_light", new RoborazziOptions());

        Section section2 = createSection(Theme.FREE_DARK, Language.EN, title);
        captureRoboImage(section2, getScreenshotName() + "_free_dark", new RoborazziOptions());

        Section section3 = createSection(Theme.DREAMER_LIGHT, Language.EN, title);
        captureRoboImage(section3, getScreenshotName() + "_dreamer_light", new RoborazziOptions());

        Section section4 = createSection(Theme.DREAMER_DARK, Language.EN, title);
        captureRoboImage(section4, getScreenshotName() + "_dreamer_dark", new RoborazziOptions());
    }

    /**
     * Test Section with title at maximum reasonable length.
     */
    @Test
    public void section_maximumLengthTitle() {
        Map<String, String> title = new HashMap<>();
        StringBuilder longTitle = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            longTitle.append("Word");
            if (i < 199) longTitle.append(" ");
        }
        title.put("en", longTitle.toString());

        Section section = createSection(Theme.FREE_LIGHT, Language.EN, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    /**
     * Test Section with title containing only spaces.
     */
    @Test
    public void section_titleOnlySpaces() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "     ");

        Section section = createSection(Theme.FREE_DARK, Language.EN, title);
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    // ============================================================
    // TITLE CHECKBOX + INFO ICON VISUAL TESTS
    // ============================================================

    @Test
    public void section_titleCheckboxOnFreeLight() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Privacy protection");
        Section section = createSection(Theme.FREE_LIGHT, Language.EN, title);
        section.setTitleCheckbox(true);
        section.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(1920, android.view.View.MeasureSpec.AT_MOST),
                android.view.View.MeasureSpec.makeMeasureSpec(720, android.view.View.MeasureSpec.AT_MOST));
        section.layout(0, 0, section.getMeasuredWidth(), section.getMeasuredHeight());
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_titleCheckboxOnFreeDark() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Privacy protection");
        Section section = createSection(Theme.FREE_DARK, Language.EN, title);
        section.setTitleCheckbox(true);
        section.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(1920, android.view.View.MeasureSpec.AT_MOST),
                android.view.View.MeasureSpec.makeMeasureSpec(720, android.view.View.MeasureSpec.AT_MOST));
        section.layout(0, 0, section.getMeasuredWidth(), section.getMeasuredHeight());
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_titleCheckboxOffCollapsedFreeDark() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Privacy protection");
        Section section = createSection(Theme.FREE_DARK, Language.EN, title);
        section.setTitleCheckbox(false);
        section.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(1920, android.view.View.MeasureSpec.AT_MOST),
                android.view.View.MeasureSpec.makeMeasureSpec(720, android.view.View.MeasureSpec.AT_MOST));
        section.layout(0, 0, section.getMeasuredWidth(), section.getMeasuredHeight());
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_withInfoIconFreeLight() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Power mode");
        Map<String, String> info = new HashMap<>();
        info.put("en", "Help");
        Section section = createSection(Theme.FREE_LIGHT, Language.EN, title);
        section.setPopupText(info);
        section.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(1920, android.view.View.MeasureSpec.AT_MOST),
                android.view.View.MeasureSpec.makeMeasureSpec(720, android.view.View.MeasureSpec.AT_MOST));
        section.layout(0, 0, section.getMeasuredWidth(), section.getMeasuredHeight());
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_withInfoIconFreeDark() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Power mode");
        Map<String, String> info = new HashMap<>();
        info.put("en", "Help");
        Section section = createSection(Theme.FREE_DARK, Language.EN, title);
        section.setPopupText(info);
        section.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(1920, android.view.View.MeasureSpec.AT_MOST),
                android.view.View.MeasureSpec.makeMeasureSpec(720, android.view.View.MeasureSpec.AT_MOST));
        section.layout(0, 0, section.getMeasuredWidth(), section.getMeasuredHeight());
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_withInfoIconAndCheckboxOn() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Privacy protection");
        Map<String, String> info = new HashMap<>();
        info.put("en", "Help");
        Section section = createSection(Theme.FREE_DARK, Language.EN, title);
        section.setPopupText(info);
        section.setTitleCheckbox(true);
        section.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(1920, android.view.View.MeasureSpec.AT_MOST),
                android.view.View.MeasureSpec.makeMeasureSpec(720, android.view.View.MeasureSpec.AT_MOST));
        section.layout(0, 0, section.getMeasuredWidth(), section.getMeasuredHeight());
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }

    @Test
    public void section_withInfoIconAndCheckboxOffCollapsed() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Privacy protection");
        Map<String, String> info = new HashMap<>();
        info.put("en", "Help");
        Section section = createSection(Theme.FREE_DARK, Language.EN, title);
        section.setPopupText(info);
        section.setTitleCheckbox(false);
        section.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(1920, android.view.View.MeasureSpec.AT_MOST),
                android.view.View.MeasureSpec.makeMeasureSpec(720, android.view.View.MeasureSpec.AT_MOST));
        section.layout(0, 0, section.getMeasuredWidth(), section.getMeasuredHeight());
        captureRoboImage(section, getScreenshotName(), new RoborazziOptions());
    }
}
