package ru.voboost.components.demo.java;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import ru.voboost.components.button.Button;
import ru.voboost.components.button.ButtonStyle;
import ru.voboost.components.buttons.ButtonConfig;
import ru.voboost.components.buttons.Buttons;
import ru.voboost.components.checkbox.Checkbox;
import ru.voboost.components.demo.shared.DemoContent;
import ru.voboost.components.demo.shared.DemoState;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.panel.Panel;
import ru.voboost.components.radio.Radio;
import ru.voboost.components.radio.RadioButton;
import ru.voboost.components.screen.Screen;
import ru.voboost.components.screen.ScreenView;
import ru.voboost.components.section.Section;
import ru.voboost.components.select.Select;
import ru.voboost.components.tabs.TabItem;
import ru.voboost.components.tabs.Tabs;
import ru.voboost.components.theme.Theme;
import ru.voboost.components.toast.ToastTheme;

/**
 * Demo Java Activity showcasing voboost-components proper component hierarchy in pure Java projects.
 *
 * This demo demonstrates:
 * - Proper component hierarchy: Screen → Panel → Tabs → Section → Radio
 * - Pure Java integration with voboost-components library
 * - Automotive-oriented layout (1920x720 resolution)
 * - Multi-language support (English/Russian)
 * - Theme switching (Light/Dark)
 * - Car type selection (Free/Dreamer)
 * - 7-tab structure with dynamic content
 * - Reactive state management across all components
 * - Traditional Android Views and layouts
 * - Proper Android Activity lifecycle
 */
public class MainActivity extends Activity {

    private static final String TAG = "JavaDemo";

    // UI Components
    private Screen screen;
    private Tabs tabs;

    // Global State
    private DemoState demoState;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize demo state
        demoState = new DemoState();

        // Enable full-screen immersive mode for automotive display
        setupFullScreenMode();

        setupComponentHierarchy();
        setupDemoComponents();
        updateAllComponents();

        Log.d(TAG, "MainActivity created successfully with proper component hierarchy");
    }

    /**
     * Sets up full-screen immersive mode to hide system navigation bar
     */
    private void setupFullScreenMode() {
        try {
            // Hide system UI for automotive display
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Use modern WindowInsetsController for API 30+
                // Note: In Robolectric test environment, getInsetsController() may throw NPE
                // because DecorView is not fully initialized. We catch and ignore this.
                WindowInsetsController controller = getWindow().getInsetsController();
                if (controller != null) {
                    controller.hide(WindowInsets.Type.systemBars());
                    controller.setSystemBarsBehavior(
                            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                }
            } else {
                // Use legacy method for older APIs
                // Note: In Robolectric test environment, getDecorView() may return null
                View decorView = getWindow().getDecorView();
                if (decorView != null) {
                    decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                }
            }

            // Keep screen on for automotive use
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } catch (NullPointerException e) {
            // Ignore NPE in Robolectric test environment
            // The DecorView is not fully initialized in unit tests
            Log.d(
                    TAG,
                    "Ignoring NullPointerException in setupFullScreenMode (likely running in"
                            + " Robolectric)");
        }
    }

    /**
     * Sets up the proper component hierarchy: Screen → Tabs → Panels
     */
    private void setupComponentHierarchy() {
        // Create Screen component as root layout
        screen = new Screen(this);
        setContentView(screen);

        // Create Tabs component
        tabs = new Tabs(this);
        screen.setTabs(tabs);

        // Create all panels
        Panel[] panels = createAllPanels();
        screen.setPanels(panels);
    }

    /**
     * Sets up demo components within the proper hierarchy
     */
    private void setupDemoComponents() {
        // Configure tabs with 7 tab items
        List<TabItem> tabItems = DemoContent.getTabItems();
        tabs.setItems(tabItems);
        tabs.setTheme(Theme.fromValue(demoState.getCombinedTheme()));
        tabs.setLanguage(Language.fromCode(demoState.getCurrentLanguage()));

        // Set initial tab selection - this will trigger the listener and set active panel
        tabs.setSelectedValue(demoState.getSelectedTab(), false);

        // Set screen lift listener for component interaction
        screen.setOnScreenLiftListener(
                state -> {
                    Log.d(TAG, "Screen lift state changed to: " + state);
                    demoState.setScreenLiftState(state);
                    updateAllComponents();
                });

        // Set tab selection listener
        tabs.setOnValueChangeListener(
                selectedTab -> {
                    Log.d(TAG, "Tab changed to: " + selectedTab);
                    demoState.setSelectedTab(selectedTab);
                    updateAllComponents();
                });
    }

    /**
     * Creates all panels for all tabs
     */
    private Panel[] createAllPanels() {
        return new Panel[] {
            createPanelForTab("settings"),
            createPanelForTab("button"),
            createPanelForTab("buttons"),
            createPanelForTab("checkbox"),
            createPanelForTab("radio"),
            createPanelForTab("select"),
            createPanelForTab("dialog"),
            createPanelForTab("toast")
        };
    }

    /**
     * Creates a panel for a specific tab
     */
    private Panel createPanelForTab(String tabValue) {
        Theme theme = Theme.fromValue(demoState.getCombinedTheme());
        Language language = Language.fromCode(demoState.getCurrentLanguage());

        // Handle settings tab specially - contains 3 Radio components
        if ("settings".equals(tabValue)) {
            return createSettingsPanel(theme, language);
        }

        // Handle component-specific tabs
        if ("button".equals(tabValue)) {
            return createButtonPanel(theme, language);
        }
        if ("buttons".equals(tabValue)) {
            return createButtonsPanel(theme, language);
        }
        if ("checkbox".equals(tabValue)) {
            return createCheckboxPanel(theme, language);
        }
        if ("radio".equals(tabValue)) {
            return createRadioPanel(theme, language);
        }
        if ("select".equals(tabValue)) {
            return createSelectPanel(theme, language);
        }
        if ("dialog".equals(tabValue)) {
            return createDialogPanel(theme, language);
        }
        if ("toast".equals(tabValue)) {
            return createToastPanel(theme, language);
        }

        // Fallback
        Panel panel = new Panel(this);
        return panel;
    }

    /**
     * Updates all components with current state
     * Implements reactive behavior across all components in the hierarchy
     */
    private void updateAllComponents() {
        String combinedTheme = demoState.getCombinedTheme();
        Language language = Language.fromCode(demoState.getCurrentLanguage());

        Log.d(
                TAG,
                "Updating all components - Language: "
                        + demoState.getCurrentLanguage()
                        + ", Theme: "
                        + combinedTheme
                        + ", Selected Tab: "
                        + demoState.getSelectedTab());

        // Update background color based on theme
        updateBackgroundColor(combinedTheme);

        // Propagate to entire tree via Screen
        if (screen != null) {
            screen.setTheme(Theme.fromValue(combinedTheme));
            screen.setLanguage(language);
        }
    }

    /**
     * Updates the background color of the activity based on current theme
     */
    private void updateBackgroundColor(String combinedTheme) {
        int backgroundColor =
                combinedTheme.endsWith("-dark")
                        ? Color.parseColor("#000000") // Black background for dark themes
                        : Color.parseColor("#f1f5fb"); // Light background for light themes

        // Apply background color to screen
        if (screen != null) {
            screen.setBackgroundColor(backgroundColor);
        }
    }

    /**
     * Gets panels from screen
     */
    private Panel[] getPanelsFromScreen() {
        return screen != null ? screen.getPanels() : null;
    }

    // Getter methods for testing
    public Tabs getTabs() {
        return tabs;
    }

    public Screen getScreen() {
        return screen;
    }

    public DemoState getDemoState() {
        return demoState;
    }

    public Section getCurrentSection() {
        // Get the current panel based on selected tab
        Panel panel = getPanel();

        // Get the section from the current panel
        if (panel != null) {
            return findSection(panel);
        }
        return null;
    }

    private Section findSection(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof Section) {
                return (Section) child;
            } else if (child instanceof ViewGroup) {
                Section found = findSection((ViewGroup) child);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    public Radio getCurrentRadio() {
        // Search directly from the current panel
        Panel panel = getPanel();
        if (panel == null) return null;

        return findRadio(panel);
    }

    private Radio findRadio(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (child instanceof Radio) {
                return (Radio) child;
            } else if (child instanceof ViewGroup) {
                Radio found = findRadio((ViewGroup) child);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    public Panel getPanel() {
        // Get the current panel based on selected tab
        String selectedTab = demoState.getSelectedTab();
        Panel[] panels = getPanelsFromScreen();
        int tabIndex = getTabIndex(selectedTab);

        return (panels != null && tabIndex < panels.length) ? panels[tabIndex] : null;
    }

    /**
     * Returns the ScrollView inside the climate panel (for testing scroll behavior).
     *
     * @return the ScrollView, or null if not found
     */
    public ScrollView getClimatePanelScrollView() {
        Panel[] panels = getPanelsFromScreen();
        if (panels == null || panels.length <= 3) return null;

        // Ensure the climate panel (index 3) wrapper is created
        screen.setActivePanel(3);
        ScreenView wrapper = screen.getPanelWrapper(3);
        return wrapper;
    }

    private int getTabIndex(String tabValue) {
        switch (tabValue) {
            case "settings":
                return 0;
            case "button":
                return 1;
            case "buttons":
                return 2;
            case "checkbox":
                return 3;
            case "radio":
                return 4;
            case "select":
                return 5;
            case "dialog":
                return 6;
            case "toast":
                return 7;
            default:
                return 0;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity resumed with component hierarchy");
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

    // ============================================================
    // Panel creators for each tab
    // ============================================================

    private Panel createSettingsPanel(Theme theme, Language language) {
        Panel panel = new Panel(this);

        Section section = new Section(this);
        section.setTitle(DemoContent.getSectionTitle("settings"));
        section.setTheme(theme);
        section.setLanguage(language);

        // Language Radio
        Radio languageRadio = new Radio(this);
        languageRadio.setButtons(DemoContent.getRadioButtons("language"));
        languageRadio.setSelectedValue(demoState.getCurrentLanguage());
        languageRadio.setTheme(theme);
        languageRadio.setLanguage(language);
        languageRadio.setOnValueChangeListener(newValue -> {
            demoState.setCurrentLanguage(newValue);
            updateAllComponents();
        });
        section.addView(languageRadio);

        // Theme Radio
        Radio themeRadio = new Radio(this);
        themeRadio.setButtons(DemoContent.getRadioButtons("theme"));
        themeRadio.setSelectedValue(demoState.getCurrentTheme());
        themeRadio.setTheme(theme);
        themeRadio.setLanguage(language);
        themeRadio.setOnValueChangeListener(newValue -> {
            demoState.setCurrentTheme(newValue);
            updateAllComponents();
        });
        section.addView(themeRadio);

        // Car Type Radio
        Radio carTypeRadio = new Radio(this);
        carTypeRadio.setButtons(DemoContent.getRadioButtons("car_type"));
        carTypeRadio.setSelectedValue(demoState.getCurrentCarType());
        carTypeRadio.setTheme(theme);
        carTypeRadio.setLanguage(language);
        carTypeRadio.setOnValueChangeListener(newValue -> {
            demoState.setCurrentCarType(newValue);
            updateAllComponents();
        });
        section.addView(carTypeRadio);

        panel.addView(section);
        return panel;
    }

    private Panel createButtonPanel(Theme theme, Language language) {
        Panel panel = new Panel(this);

        int sectionCount = DemoContent.getButtonSectionCount();
        for (int i = 0; i < sectionCount; i++) {
            Section section = new Section(this);
            section.setTitle(DemoContent.getButtonSectionTitle(i));
            section.setTheme(theme);
            section.setLanguage(language);

            if (i == 0) {
                LinearLayout buttonRow = new LinearLayout(this);
                buttonRow.setOrientation(LinearLayout.HORIZONTAL);
                buttonRow.setPadding(0, 10, 0, 10);

                Button primaryBtn = new Button(this);
                primaryBtn.setTheme(theme);
                primaryBtn.setStyle(ButtonStyle.PRIMARY);
                primaryBtn.setText("Primary");
                primaryBtn.setLayoutParams(new LinearLayout.LayoutParams(0, 80, 1f));

                Button secondaryBtn = new Button(this);
                secondaryBtn.setTheme(theme);
                secondaryBtn.setStyle(ButtonStyle.SECONDARY);
                secondaryBtn.setText("Secondary");
                LinearLayout.LayoutParams secParams = new LinearLayout.LayoutParams(0, 80, 1f);
                secParams.leftMargin = 20;
                secondaryBtn.setLayoutParams(secParams);

                buttonRow.addView(primaryBtn);
                buttonRow.addView(secondaryBtn);
                section.addView(buttonRow);
            } else {
                Button button = new Button(this);
                button.setTheme(theme);
                button.setStyle(ButtonStyle.PRIMARY);
                button.setText("Calibrate");

                Map<String, String> desc = new HashMap<>();
                desc.put("en", "Run camera calibration.\nDrive straight for 2 minutes.");
                desc.put("ru", "Запустить калибровку камеры.\nДвигайтесь прямо 2 минуты.");
                button.setDescription(desc);

                section.addView(button);
            }

            panel.addView(section);
        }

        return panel;
    }

    private Panel createButtonsPanel(Theme theme, Language language) {
        Panel panel = new Panel(this);

        int sectionCount = DemoContent.getButtonsSectionCount();
        for (int i = 0; i < sectionCount; i++) {
            Section section = new Section(this);
            section.setTitle(DemoContent.getButtonsSectionTitle(i));
            section.setTheme(theme);
            section.setLanguage(language);

            Buttons buttons = new Buttons(this);
            buttons.setTheme(theme);
            buttons.setLanguage(language);
            buttons.setButtons(DemoContent.getButtonsConfig(i));
            buttons.setSelectedValue(DemoContent.getButtonsDefaultValue(i));

            if (i == 2) {
                Map<String, String> rightText = new HashMap<>();
                rightText.put("en", "Auto headlamp");
                rightText.put("ru", "Авто фары");
                buttons.setRightText(rightText);
            }

            section.addView(buttons);
            panel.addView(section);
        }

        return panel;
    }

    private Panel createCheckboxPanel(Theme theme, Language language) {
        Panel panel = new Panel(this);

        int sectionCount = DemoContent.getCheckboxSectionCount();
        for (int i = 0; i < sectionCount; i++) {
            Section section = new Section(this);
            section.setTitle(DemoContent.getCheckboxSectionTitle(i));
            section.setTheme(theme);
            section.setLanguage(language);

            if (i == 0) {
                Map<String, String> label = new HashMap<>();
                label.put("en", "Auto-fold mirrors");
                label.put("ru", "Автоскладывание зеркал");
                section.addCheckbox(Checkbox.create(this, theme, language, true).label(label).build());
            } else if (i == 1) {
                Map<String, String> label = new HashMap<>();
                label.put("en", "Tow mode");
                label.put("ru", "Режим буксировки");

                Map<String, String> desc = new HashMap<>();
                desc.put("en", "Maintain N gear when vehicle is rescued");
                desc.put("ru", "Поддерживать нейтраль при буксировке");

                section.addCheckbox(Checkbox.create(this, theme, language, false).label(label).description(desc).build());
            } else {
                Map<String, String> label1 = new HashMap<>();
                label1.put("en", "Welcome lamp");
                label1.put("ru", "Приветственная подсветка");
                section.addCheckbox(Checkbox.create(this, theme, language, true).label(label1).build());

                Map<String, String> label2 = new HashMap<>();
                label2.put("en", "Auto-tilt mirrors");
                label2.put("ru", "Автонаклон зеркал");
                section.addCheckbox(Checkbox.create(this, theme, language, false).label(label2).build());
            }

            panel.addView(section);
        }

        return panel;
    }

    private Panel createRadioPanel(Theme theme, Language language) {
        Panel panel = new Panel(this);

        int sectionCount = DemoContent.getRadioSectionCount();
        for (int i = 0; i < sectionCount; i++) {
            Section section = new Section(this);
            section.setTitle(DemoContent.getRadioSectionTitle(i));
            section.setTheme(theme);
            section.setLanguage(language);

            Radio radio = new Radio(this);
            radio.setTheme(theme);
            radio.setLanguage(language);
            radio.setButtons(DemoContent.getRadioSubRadioButtons(i));
            radio.setSelectedValue(DemoContent.getRadioSubDefaultValue(i));

            if (i == 1) {
                Map<String, String> title = new HashMap<>();
                title.put("en", "Energy recovery");
                title.put("ru", "Рекуперация энергии");
                radio.setTitle(title);

                Map<String, String> descAbove = new HashMap<>();
                descAbove.put("en", "Adjusts braking energy recovery level");
                descAbove.put("ru", "Регулирует уровень рекуперации торможения");
                radio.setDescriptionAbove(descAbove);
            } else if (i == 2) {
                Map<String, String> title = new HashMap<>();
                title.put("en", "Come home lights");
                title.put("ru", "Подсветка дороги домой");
                radio.setTitle(title);

                Map<String, String> descBelow = new HashMap<>();
                descBelow.put("en", "Headlights stay on after locking");
                descBelow.put("ru", "Фары остаются включёнными после блокировки");
                radio.setDescription(descBelow);
            } else {
                Map<String, String> title = new HashMap<>();
                title.put("en", "Anti-theft alarm");
                title.put("ru", "Противоугонная сигнализация");
                radio.setTitle(title);
            }

            section.addView(radio);
            panel.addView(section);
        }

        return panel;
    }

    private Panel createSelectPanel(Theme theme, Language language) {
        Panel panel = new Panel(this);

        Section section = new Section(this);
        section.setTitle(DemoContent.getSelectSectionTitle());
        section.setTheme(theme);
        section.setLanguage(language);

        Select select = new Select(this);
        select.setTheme(theme);
        select.setLanguage(language);
        select.setOptions(DemoContent.getSelectOptions());
        select.setSelectedValue("auto");

        section.addView(select);
        panel.addView(section);

        return panel;
    }

    private Panel createDialogPanel(Theme theme, Language language) {
        Panel panel = new Panel(this);

        Section section = new Section(this);
        section.setTitle(DemoContent.getDialogSectionTitle());
        section.setTheme(theme);
        section.setLanguage(language);

        Button dialogTrigger = new Button(this);
        dialogTrigger.setTheme(theme);
        dialogTrigger.setStyle(ButtonStyle.PRIMARY);

        Map<String, Map<String, String>> dialogContent = DemoContent.getDialogContent();
        String lang = demoState.getCurrentLanguage();
        dialogTrigger.setText(dialogContent.get("title").getOrDefault(lang, "Show Dialog"));

        dialogTrigger.setOnClickListener(v -> {
            ru.voboost.components.dialog.Dialog dialog = new ru.voboost.components.dialog.Dialog(this);
            dialog.setTheme(Theme.fromValue(demoState.getCombinedTheme()));
            dialog.setTitle(dialogContent.get("title").getOrDefault(demoState.getCurrentLanguage(), "Reset"));
            dialog.setMessage(dialogContent.get("message").getOrDefault(demoState.getCurrentLanguage(), "Are you sure?"));
            dialog.setConfirmButton(
                    dialogContent.get("confirm").getOrDefault(demoState.getCurrentLanguage(), "OK"),
                    () -> Log.d(TAG, "Dialog confirmed"));
            dialog.setCancelButton(
                    dialogContent.get("cancel").getOrDefault(demoState.getCurrentLanguage(), "Cancel"),
                    () -> Log.d(TAG, "Dialog cancelled"));
            dialog.show();
        });

        section.addView(dialogTrigger);
        panel.addView(section);

        return panel;
    }

    private Panel createToastPanel(Theme theme, Language language) {
        Panel panel = new Panel(this);

        int sectionCount = DemoContent.getToastSectionCount();
        for (int i = 0; i < sectionCount; i++) {
            Section section = new Section(this);
            section.setTitle(DemoContent.getToastSectionTitle(i));
            section.setTheme(theme);
            section.setLanguage(language);

            Button toastButton = new Button(this);
            toastButton.setTheme(theme);
            toastButton.setStyle(ButtonStyle.SECONDARY);

            Map<String, String> buttonText = DemoContent.getToastButtonText(i);
            String lang = demoState.getCurrentLanguage();
            toastButton.setText(buttonText.getOrDefault(lang, buttonText.values().iterator().next()));

            final long duration = (i == 0) ? ToastTheme.DURATION_SHORT : ToastTheme.DURATION_LONG;
            final int sectionIndex = i;  // Create effectively final copy for lambda

            toastButton.setOnClickListener(v -> {
                Map<String, String> message = new HashMap<>();
                if (sectionIndex == 0) {
                    message.put("en", "Settings saved");
                    message.put("ru", "Настройки сохранены");
                } else {
                    message.put("en", "Your settings have been successfully saved");
                    message.put("ru", "Ваши настройки успешно сохранены");
                }
                screen.showToast(message.get(demoState.getCurrentLanguage()), duration);
            });

            section.addView(toastButton);
            panel.addView(section);
        }

        return panel;
    }
}
