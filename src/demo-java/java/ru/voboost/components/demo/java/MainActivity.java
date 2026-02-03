package ru.voboost.components.demo.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import ru.voboost.components.radio.Radio;
import ru.voboost.components.radio.RadioButton;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.Theme;

/**
 * Demo Java Activity showcasing voboost-components Radio component usage in pure Java projects.
 *
 * This demo demonstrates:
 * - Pure Java integration with voboost-components library
 * - Automotive-oriented layout (1920x720 resolution)
 * - Multi-language support (English/Russian)
 * - Theme switching (Light/Dark)
 * - Car type selection (Free/Dreamer)
 * - Reactive state management across multiple Radio components
 * - Traditional Android Views and layouts
 * - Proper Android Activity lifecycle
 */
public class MainActivity extends Activity {

    private static final String TAG = "JavaDemo";

    // Layout constants
    private static final int CONTAINER_PADDING_HORIZONTAL = 64;
    private static final int CONTAINER_PADDING_VERTICAL = 48;
    private static final int RADIO_MARGIN_TOP_FIRST = 24;
    private static final int RADIO_MARGIN_RIGHT = 8;
    private static final int RADIO_MARGIN_BOTTOM = 32;
    private static final int SECTION_SPACING = 24;
    private static final int TITLE_BOTTOM_MARGIN = 32;
    private static final int SECTION_TITLE_BOTTOM_MARGIN = 16;

    // Background colors
    private static final String BACKGROUND_COLOR_LIGHT = "#f1f5fb";
    private static final String BACKGROUND_COLOR_DARK = "#000000";

    // Text colors
    private static final String TEXT_COLOR_LIGHT = "#1a1a1a";
    private static final String TEXT_COLOR_DARK = "#ffffff";

    // Default values
    private static final String DEFAULT_LANGUAGE = "en";
    private static final String DEFAULT_THEME = "light";
    private static final String DEFAULT_CAR_TYPE = "free";
    private static final String DEFAULT_TEST_VALUE = "close";

    // UI Components
    private ScrollView scrollView;
    private LinearLayout rootLayout;
    private TextView titleText;
    private TextView languageSectionTitle;
    private TextView themeSectionTitle;
    private TextView carTypeSectionTitle;
    private TextView testSectionTitle;
    private TextView stateDisplay;
    private Radio languageRadio;
    private Radio themeRadio;
    private Radio carTypeRadio;
    private Radio testRadio;

    // Global State
    private String currentLanguage = DEFAULT_LANGUAGE;
    private String currentTheme = DEFAULT_THEME;
    private String currentCarType = DEFAULT_CAR_TYPE;
    private String currentTestValue = DEFAULT_TEST_VALUE;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable full-screen immersive mode for automotive display
        setupFullScreenMode();

        setupAutomotiveLayout();
        setupDemoComponents();
        updateAllComponents();

        Log.d(TAG, "MainActivity created successfully");
    }

    /**
     * Sets up full-screen immersive mode to hide system navigation bar
     */
    private void setupFullScreenMode() {
        // Hide system UI for automotive display
        getWindow()
                .getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        // Keep screen on for automotive use
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * Sets up automotive-oriented full-screen layout with ScrollView for better compatibility
     */
    private void setupAutomotiveLayout() {
        // Create ScrollView for automotive display compatibility
        scrollView = new ScrollView(this);
        scrollView.setLayoutParams(
                new ScrollView.LayoutParams(
                        ScrollView.LayoutParams.MATCH_PARENT,
                        ScrollView.LayoutParams.MATCH_PARENT));
        scrollView.setFillViewport(true);

        // CRITICAL: Disable clipping at parent container level
        scrollView.setClipChildren(false);
        scrollView.setClipToPadding(false);

        // Create main layout container
        rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
        // Increased padding to prevent component clipping
        rootLayout.setPadding(
                CONTAINER_PADDING_HORIZONTAL,
                CONTAINER_PADDING_VERTICAL,
                CONTAINER_PADDING_HORIZONTAL,
                CONTAINER_PADDING_VERTICAL); // More generous automotive spacing

        // CRITICAL: Turn off clipping on LinearLayout
        rootLayout.setClipChildren(false);
        rootLayout.setClipToPadding(false);

        scrollView.addView(rootLayout);
        setContentView(scrollView);
    }

    /**
     * Sets up demo components for showcasing Radio functionality
     */
    private void setupDemoComponents() {
        // Main title
        setupTitle();

        // Language Radio Section
        languageSectionTitle = createSectionTitle();
        rootLayout.addView(languageSectionTitle);
        setupLanguageRadio();

        // Theme Radio Section
        themeSectionTitle = createSectionTitle();
        rootLayout.addView(themeSectionTitle);
        setupThemeRadio();

        // Car Type Radio Section
        carTypeSectionTitle = createSectionTitle();
        rootLayout.addView(carTypeSectionTitle);
        setupCarTypeRadio();

        // Test Radio Section
        testSectionTitle = createSectionTitle();
        rootLayout.addView(testSectionTitle);
        setupTestRadio();

        // State Display Section
        setupStateDisplay();
    }

    /**
     * Sets up the main application title
     */
    private void setupTitle() {
        titleText = new TextView(this);
        titleText.setTextSize(24f);

        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        titleParams.setMargins(0, 0, 0, TITLE_BOTTOM_MARGIN);
        titleText.setLayoutParams(titleParams);

        rootLayout.addView(titleText);
    }

    /**
     * Creates a section title TextView with consistent styling
     */
    private TextView createSectionTitle() {
        TextView sectionTitle = new TextView(this);
        sectionTitle.setTextSize(18f);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, SECTION_SPACING, 0, SECTION_TITLE_BOTTOM_MARGIN);
        sectionTitle.setLayoutParams(params);

        return sectionTitle;
    }

    /**
     * Sets up the state display section
     */
    private void setupStateDisplay() {
        stateDisplay = new TextView(this);
        stateDisplay.setTextSize(16f);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, TITLE_BOTTOM_MARGIN, 0, SECTION_TITLE_BOTTOM_MARGIN);
        stateDisplay.setLayoutParams(params);

        rootLayout.addView(stateDisplay);
    }

    /**
     * Sets up Language Radio component section
     */
    private void setupLanguageRadio() {
        // Create language radio buttons
        List<RadioButton> languageButtons = createLanguageButtons();

        // Create and configure language radio
        languageRadio = new Radio(this);
        languageRadio.setButtons(languageButtons);
        languageRadio.setSelectedValue(currentLanguage);
        languageRadio.setOnValueChangeListener(
                newValue -> {
                    Log.d(TAG, "Language changed to: " + newValue);
                    currentLanguage = newValue;
                    updateAllComponents();
                });

        languageRadio.setLayoutParams(createRadioLayoutParams(0));
        rootLayout.addView(languageRadio);
    }

    /**
     * Sets up Theme Radio component section
     */
    private void setupThemeRadio() {
        // Create theme radio buttons
        List<RadioButton> themeButtons = createThemeButtons();

        // Create and configure theme radio
        themeRadio = new Radio(this);
        themeRadio.setButtons(themeButtons);
        themeRadio.setSelectedValue(currentTheme);
        themeRadio.setOnValueChangeListener(
                newValue -> {
                    Log.d(TAG, "Theme changed to: " + newValue);
                    currentTheme = newValue;
                    updateAllComponents();
                });

        themeRadio.setLayoutParams(createRadioLayoutParams(0));
        rootLayout.addView(themeRadio);
    }

    /**
     * Sets up Car Type Radio component section
     */
    private void setupCarTypeRadio() {
        // Create car type radio buttons
        List<RadioButton> carTypeButtons = createCarTypeButtons();

        // Create and configure car type radio
        carTypeRadio = new Radio(this);
        carTypeRadio.setButtons(carTypeButtons);
        carTypeRadio.setSelectedValue(currentCarType);
        carTypeRadio.setOnValueChangeListener(
                newValue -> {
                    Log.d(TAG, "Car type changed to: " + newValue);
                    currentCarType = newValue;
                    updateAllComponents();
                });

        carTypeRadio.setLayoutParams(createRadioLayoutParams(0));
        rootLayout.addView(carTypeRadio);
    }

    /**
     * Creates language radio buttons with proper localization
     */
    private List<RadioButton> createLanguageButtons() {
        List<RadioButton> languageButtons = new ArrayList<>();

        Map<String, String> englishLabels = new HashMap<>();
        englishLabels.put("en", "English");
        englishLabels.put("ru", "English");
        languageButtons.add(new RadioButton("en", englishLabels));

        Map<String, String> russianLabels = new HashMap<>();
        russianLabels.put("en", "Русский");
        russianLabels.put("ru", "Русский");
        languageButtons.add(new RadioButton("ru", russianLabels));

        return languageButtons;
    }

    /**
     * Creates theme radio buttons with proper localization
     */
    private List<RadioButton> createThemeButtons() {
        List<RadioButton> themeButtons = new ArrayList<>();

        Map<String, String> lightLabels = new HashMap<>();
        lightLabels.put("en", "Light");
        lightLabels.put("ru", "Светлая");
        themeButtons.add(new RadioButton("light", lightLabels));

        Map<String, String> darkLabels = new HashMap<>();
        darkLabels.put("en", "Dark");
        darkLabels.put("ru", "Тёмная");
        themeButtons.add(new RadioButton("dark", darkLabels));

        return themeButtons;
    }

    /**
     * Creates car type radio buttons with proper localization
     */
    private List<RadioButton> createCarTypeButtons() {
        List<RadioButton> carTypeButtons = new ArrayList<>();

        Map<String, String> freeLabels = new HashMap<>();
        freeLabels.put("en", "Free");
        freeLabels.put("ru", "Фри");
        carTypeButtons.add(new RadioButton("free", freeLabels));

        Map<String, String> dreamerLabels = new HashMap<>();
        dreamerLabels.put("en", "Dreamer");
        dreamerLabels.put("ru", "Дример");
        carTypeButtons.add(new RadioButton("dreamer", dreamerLabels));

        return carTypeButtons;
    }

    /**
     * Creates test radio buttons with long text for animation testing
     */
    private List<RadioButton> createTestButtons() {
        List<RadioButton> testButtons = new ArrayList<>();

        Map<String, String> closeLabels = new HashMap<>();
        closeLabels.put("en", "Close");
        closeLabels.put("ru", "Закрыть");
        testButtons.add(new RadioButton("close", closeLabels));

        Map<String, String> normalLabels = new HashMap<>();
        normalLabels.put("en", "Normal");
        normalLabels.put("ru", "Обычный");
        testButtons.add(new RadioButton("normal", normalLabels));

        Map<String, String> syncMusicLabels = new HashMap<>();
        syncMusicLabels.put("en", "Sync with Music");
        syncMusicLabels.put("ru", "Синхронизация с музыкой");
        testButtons.add(new RadioButton("sync_music", syncMusicLabels));

        Map<String, String> syncDrivingLabels = new HashMap<>();
        syncDrivingLabels.put("en", "Sync with Driving");
        syncDrivingLabels.put("ru", "Синхронизация с вождением");
        testButtons.add(new RadioButton("sync_driving", syncDrivingLabels));

        return testButtons;
    }

    /**
     * Sets up Test Radio component section for animation testing
     */
    private void setupTestRadio() {
        // Create test radio buttons
        List<RadioButton> testButtons = createTestButtons();

        // Create and configure test radio
        testRadio = new Radio(this);
        testRadio.setButtons(testButtons);
        testRadio.setSelectedValue(currentTestValue);
        testRadio.setOnValueChangeListener(
                newValue -> {
                    Log.d(TAG, "Test value changed to: " + newValue);
                    currentTestValue = newValue;
                    updateAllComponents();
                });

        testRadio.setLayoutParams(createRadioLayoutParams(0));
        rootLayout.addView(testRadio);
    }

    /**
     * Updates all components with current state
     * Implements reactive behavior across all Radio components and UI elements
     */
    private void updateAllComponents() {
        // Form combined theme: {carType}-{theme}
        String combinedTheme = currentCarType + "-" + currentTheme;

        Log.d(
                TAG,
                "Updating all components - Language: "
                        + currentLanguage
                        + ", Theme: "
                        + combinedTheme);

        // Update background color based on theme
        updateBackgroundColor(combinedTheme);

        // Update language and theme for all radio components
        updateRadioComponents(combinedTheme);

        // Update all text elements based on current language
        updateTextElements();
    }

    /**
     * Updates the background color of the activity based on current theme
     */
    private void updateBackgroundColor(String combinedTheme) {
        int backgroundColor =
                combinedTheme.endsWith("-dark")
                        ? Color.parseColor(
                                BACKGROUND_COLOR_DARK) // Black background for dark themes
                        : Color.parseColor(
                                BACKGROUND_COLOR_LIGHT); // Light background for light themes

        // Apply background color to root layout and scroll view
        if (rootLayout != null) {
            rootLayout.setBackgroundColor(backgroundColor);
        }
        if (scrollView != null) {
            scrollView.setBackgroundColor(backgroundColor);
        }
    }

    /**
     * Updates all radio components with current language and theme
     */
    private void updateRadioComponents(String combinedTheme) {
        if (languageRadio != null) {
            languageRadio.setLanguage(Language.fromCode(currentLanguage));
            languageRadio.setTheme(Theme.fromValue(combinedTheme));
        }
        if (themeRadio != null) {
            themeRadio.setLanguage(Language.fromCode(currentLanguage));
            themeRadio.setTheme(Theme.fromValue(combinedTheme));
        }
        if (carTypeRadio != null) {
            carTypeRadio.setLanguage(Language.fromCode(currentLanguage));
            carTypeRadio.setTheme(Theme.fromValue(combinedTheme));
        }
        if (testRadio != null) {
            testRadio.setLanguage(Language.fromCode(currentLanguage));
            testRadio.setTheme(Theme.fromValue(combinedTheme));
        }
    }

    /**
     * Creates standardized layout parameters for Radio components
     */
    private LinearLayout.LayoutParams createRadioLayoutParams(int topMargin) {
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, topMargin, RADIO_MARGIN_RIGHT, RADIO_MARGIN_BOTTOM);
        return params;
    }

    /**
     * Updates all text elements based on current language and theme
     */
    private void updateTextElements() {
        // Determine text color based on theme
        int textColor = currentTheme.equals("dark")
                ? Color.parseColor(TEXT_COLOR_DARK)
                : Color.parseColor(TEXT_COLOR_LIGHT);

        // Update title
        if (titleText != null) {
            titleText.setText(currentLanguage.equals("ru")
                    ? "Voboost Components - Java Демо"
                    : "Voboost Components - Java Demo");
            titleText.setTextColor(textColor);
        }

        // Update section titles
        if (languageSectionTitle != null) {
            languageSectionTitle.setText(currentLanguage.equals("ru")
                    ? "Выбор языка:"
                    : "Language Selection:");
            languageSectionTitle.setTextColor(textColor);
        }

        if (themeSectionTitle != null) {
            themeSectionTitle.setText(currentLanguage.equals("ru")
                    ? "Выбор темы:"
                    : "Theme Selection:");
            themeSectionTitle.setTextColor(textColor);
        }

        if (carTypeSectionTitle != null) {
            carTypeSectionTitle.setText(currentLanguage.equals("ru")
                    ? "Выбор типа авто:"
                    : "Car Type Selection:");
            carTypeSectionTitle.setTextColor(textColor);
        }

        if (testSectionTitle != null) {
            testSectionTitle.setText(currentLanguage.equals("ru")
                    ? "Тестовый выбор:"
                    : "Test Selection:");
            testSectionTitle.setTextColor(textColor);
        }

        // Update state display
        if (stateDisplay != null) {
            stateDisplay.setText(buildStateText());
            stateDisplay.setTextColor(textColor);
        }
    }

    /**
     * Builds the state display text based on current language
     */
    private String buildStateText() {
        String combinedTheme = currentCarType + "-" + currentTheme;

        if (currentLanguage.equals("ru")) {
            return "Текущее состояние:\n" +
                    "Язык: " + (currentLanguage.equals("ru") ? "Русский" : "English") + "\n" +
                    "Тема: " + (currentTheme.equals("light") ? "Светлая" : "Тёмная") + "\n" +
                    "Тип авто: " + (currentCarType.equals("free") ? "Фри" : "Дример") + "\n" +
                    "Тест: " + getTestValueDisplayName() + "\n" +
                    "Комбинированная тема: " + combinedTheme;
        } else {
            return "Current State:\n" +
                    "Language: " + (currentLanguage.equals("ru") ? "Russian" : "English") + "\n" +
                    "Theme: " + (currentTheme.equals("light") ? "Light" : "Dark") + "\n" +
                    "Car Type: " + (currentCarType.equals("free") ? "Free" : "Dreamer") + "\n" +
                    "Test: " + getTestValueDisplayName() + "\n" +
                    "Combined Theme: " + combinedTheme;
        }
    }

    /**
     * Gets display name for current test value
     */
    private String getTestValueDisplayName() {
        if (currentLanguage.equals("ru")) {
            switch (currentTestValue) {
                case "close": return "Закрыть";
                case "normal": return "Обычный";
                case "sync_music": return "Синхронизация с музыкой";
                case "sync_driving": return "Синхронизация с вождением";
                default: return currentTestValue;
            }
        } else {
            switch (currentTestValue) {
                case "close": return "Close";
                case "normal": return "Normal";
                case "sync_music": return "Sync with Music";
                case "sync_driving": return "Sync with Driving";
                default: return currentTestValue;
            }
        }
    }

    // Getter methods for testing
    public Radio getLanguageRadio() { return languageRadio; }
    public Radio getThemeRadio() { return themeRadio; }
    public Radio getCarTypeRadio() { return carTypeRadio; }
    public Radio getTestRadio() { return testRadio; }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity resumed");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "MainActivity paused");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MainActivity destroyed");
    }
}
