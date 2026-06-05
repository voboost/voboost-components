package ru.voboost.components.demo.java;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import ru.voboost.components.button.Button;
import ru.voboost.components.button.ButtonStyle;
import ru.voboost.components.buttons.Buttons;
import ru.voboost.components.checkbox.Checkbox;
import ru.voboost.components.demo.shared.DemoContent;
import ru.voboost.components.demo.shared.DemoHelpers;
import ru.voboost.components.demo.shared.DemoState;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.panel.Panel;
import ru.voboost.components.radio.Radio;
import ru.voboost.components.screen.Screen;
import ru.voboost.components.section.Section;
import ru.voboost.components.select.Select;
import ru.voboost.components.tabs.TabItem;
import ru.voboost.components.tabs.Tabs;
import ru.voboost.components.theme.Theme;
import ru.voboost.components.toast.ToastTheme;

/**
 * Demo Java Activity showcasing voboost-components in a pure Java project.
 *
 * Demonstrates the component hierarchy Screen -> Tabs -> Panel -> Section -> components,
 * traditional Android Views, multi-language support and theme switching. Eight tabs
 * (settings, button, buttons, checkbox, radio, select, dialog, toast) mirror the
 * Kotlin and Compose demos one-to-one; all localized content comes from DemoContent.
 */
public class MainActivity extends Activity {

    private static final String TAG = "JavaDemo";

    private static final List<String> TAB_VALUES =
            Arrays.asList(
                    "settings", "button", "buttons", "checkbox",
                    "radio", "select", "dialog", "toast");

    private Screen screen;
    private DemoState demoState;

    // Settings radios kept for state application in tests
    private Radio languageRadio;
    private Radio themeRadio;
    private Radio carTypeRadio;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        demoState = new DemoState();

        setupFullScreenMode();
        setupComponentHierarchy();
        setupDemoComponents();
        updateAllComponents();
    }

    /**
     * Enables full-screen immersive mode for the automotive display.
     */
    private void setupFullScreenMode() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Note: In Robolectric, getInsetsController() may throw NPE because
                // DecorView is not fully initialized; we catch and ignore it below.
                WindowInsetsController controller = getWindow().getInsetsController();
                if (controller != null) {
                    controller.hide(WindowInsets.Type.systemBars());
                    controller.setSystemBarsBehavior(
                            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                }
            } else {
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
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } catch (NullPointerException e) {
            Log.d(TAG, "Ignoring NullPointerException in setupFullScreenMode (Robolectric)");
        }
    }

    /**
     * Builds the Screen -> Tabs -> Panels hierarchy.
     */
    private void setupComponentHierarchy() {
        screen = new Screen(this);
        setContentView(screen);

        Tabs tabs = new Tabs(this);
        screen.setTabs(tabs);

        screen.setPanels(createAllPanels());
    }

    /**
     * Configures tabs content, initial selection and listeners.
     */
    private void setupDemoComponents() {
        Tabs tabs = screen.getTabs();

        List<TabItem> tabItems = DemoContent.getTabItems();
        tabs.setItems(tabItems);
        tabs.setTheme(Theme.fromValue(demoState.getCombinedTheme()));
        tabs.setLanguage(Language.fromCode(demoState.getCurrentLanguage()));

        tabs.setSelectedValue(demoState.getSelectedTab(), false);

        screen.setOnScreenLiftListener(
                state -> {
                    demoState.setScreenLiftState(state);
                    updateAllComponents();
                });

        tabs.setOnValueChangeListener(
                selectedTab -> {
                    demoState.setSelectedTab(selectedTab);
                    updateAllComponents();
                });
    }

    private Panel[] createAllPanels() {
        Panel[] panels = new Panel[TAB_VALUES.size()];
        for (int i = 0; i < TAB_VALUES.size(); i++) {
            panels[i] = createPanelForTab(TAB_VALUES.get(i));
        }
        return panels;
    }

    private Panel createPanelForTab(String tabValue) {
        Theme theme = Theme.fromValue(demoState.getCombinedTheme());
        Language language = Language.fromCode(demoState.getCurrentLanguage());

        switch (tabValue) {
            case "settings":
                return createSettingsPanel(theme, language);
            case "button":
                return createButtonPanel(theme, language);
            case "buttons":
                return createButtonsPanel(theme, language);
            case "checkbox":
                return createCheckboxPanel(theme, language);
            case "radio":
                return createRadioPanel(theme, language);
            case "select":
                return createSelectPanel(theme, language);
            case "dialog":
                return createDialogPanel(theme, language);
            case "toast":
                return createToastPanel(theme, language);
            default:
                throw new IllegalStateException("Unknown tab: " + tabValue);
        }
    }

    /**
     * Propagates current state to the whole component tree.
     */
    private void updateAllComponents() {
        String combinedTheme = demoState.getCombinedTheme();
        Language language = Language.fromCode(demoState.getCurrentLanguage());

        if (screen != null) {
            screen.setBackgroundColor(DemoHelpers.getBackgroundColor(combinedTheme));
            screen.setTheme(Theme.fromValue(combinedTheme));
            screen.setLanguage(language);
        }
    }

    /**
     * Applies a full settings state. Used by visual tests to render combinations.
     */
    public void applyState(String language, String theme, String carType) {
        demoState.setCurrentLanguage(language);
        demoState.setCurrentTheme(theme);
        demoState.setCurrentCarType(carType);
        if (languageRadio != null) {
            languageRadio.setSelectedValue(language);
        }
        if (themeRadio != null) {
            themeRadio.setSelectedValue(theme);
        }
        if (carTypeRadio != null) {
            carTypeRadio.setSelectedValue(carType);
        }
        updateAllComponents();
    }

    // Getters for testing
    public Tabs getTabs() {
        return screen != null ? screen.getTabs() : null;
    }

    public Screen getScreen() {
        return screen;
    }

    public DemoState getDemoState() {
        return demoState;
    }

    public Panel getPanel() {
        Panel[] panels = screen != null ? screen.getPanels() : null;
        int tabIndex = TAB_VALUES.indexOf(demoState.getSelectedTab());
        return (panels != null && tabIndex >= 0 && tabIndex < panels.length) ? panels[tabIndex] : null;
    }

    // ============================================================
    // Panel creators
    // ============================================================

    private Panel createSettingsPanel(Theme theme, Language language) {
        Panel panel = new Panel(this);

        Section section = new Section(this);
        section.setTitle(DemoContent.getSectionTitle("settings"));
        section.setTheme(theme);
        section.setLanguage(language);

        languageRadio = new Radio(this);
        languageRadio.setButtons(DemoContent.getRadioButtons("language"));
        languageRadio.setSelectedValue(demoState.getCurrentLanguage());
        languageRadio.setTheme(theme);
        languageRadio.setLanguage(language);
        languageRadio.setOnValueChangeListener(newValue -> {
            demoState.setCurrentLanguage(newValue);
            updateAllComponents();
        });
        section.addView(languageRadio);

        themeRadio = new Radio(this);
        themeRadio.setButtons(DemoContent.getRadioButtons("theme"));
        themeRadio.setSelectedValue(demoState.getCurrentTheme());
        themeRadio.setTheme(theme);
        themeRadio.setLanguage(language);
        themeRadio.setOnValueChangeListener(newValue -> {
            demoState.setCurrentTheme(newValue);
            updateAllComponents();
        });
        section.addView(themeRadio);

        carTypeRadio = new Radio(this);
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
                primaryBtn.setText(DemoContent.getButtonPrimaryText());
                primaryBtn.setLayoutParams(new LinearLayout.LayoutParams(0, 80, 1f));

                Button secondaryBtn = new Button(this);
                secondaryBtn.setTheme(theme);
                secondaryBtn.setStyle(ButtonStyle.SECONDARY);
                secondaryBtn.setText(DemoContent.getButtonSecondaryText());
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
                button.setText(DemoContent.getButtonWithDescriptionText());
                button.setDescription(DemoContent.getButtonWithDescriptionDescription());
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

            Map<String, String> rightText = DemoContent.getButtonsRightText(i);
            if (!rightText.isEmpty()) {
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

            if (i == 2) {
                section.addCheckbox(
                        Checkbox.create(this, theme, language, DemoContent.getCheckboxChecked(i))
                                .label(DemoContent.getCheckboxLabel(i))
                                .build());
                section.addCheckbox(
                        Checkbox.create(this, theme, language, DemoContent.getCheckboxExtraChecked())
                                .label(DemoContent.getCheckboxExtraLabel())
                                .build());
            } else {
                Checkbox.Builder builder =
                        Checkbox.create(this, theme, language, DemoContent.getCheckboxChecked(i))
                                .label(DemoContent.getCheckboxLabel(i));
                Map<String, String> desc = DemoContent.getCheckboxDescription(i);
                if (!desc.isEmpty()) {
                    builder.description(desc);
                }
                section.addCheckbox(builder.build());
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
            radio.setTitle(DemoContent.getRadioSubTitle(i));

            Map<String, String> descAbove = DemoContent.getRadioSubDescriptionAbove(i);
            if (!descAbove.isEmpty()) {
                radio.setDescriptionAbove(descAbove);
            }
            Map<String, String> descBelow = DemoContent.getRadioSubDescriptionBelow(i);
            if (!descBelow.isEmpty()) {
                radio.setDescription(descBelow);
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

        Map<String, Map<String, String>> dialogContent = DemoContent.getDialogContent();

        Button dialogTrigger = new Button(this);
        dialogTrigger.setTheme(theme);
        dialogTrigger.setStyle(ButtonStyle.PRIMARY);
        dialogTrigger.setText(
                dialogContent.get("title").getOrDefault(demoState.getCurrentLanguage(), "Show Dialog"));

        dialogTrigger.setOnClickListener(v -> {
            String lang = demoState.getCurrentLanguage();
            ru.voboost.components.dialog.Dialog dialog = new ru.voboost.components.dialog.Dialog(this);
            dialog.setTheme(Theme.fromValue(demoState.getCombinedTheme()));
            dialog.setTitle(dialogContent.get("title").getOrDefault(lang, "Reset"));
            dialog.setMessage(dialogContent.get("message").getOrDefault(lang, "Are you sure?"));
            dialog.setConfirmButton(
                    dialogContent.get("confirm").getOrDefault(lang, "OK"),
                    () -> Log.d(TAG, "Dialog confirmed"));
            dialog.setCancelButton(
                    dialogContent.get("cancel").getOrDefault(lang, "Cancel"),
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
            toastButton.setText(
                    DemoContent.getToastButtonText(i).getOrDefault(demoState.getCurrentLanguage(), "Show Toast"));

            final long duration = (i == 0) ? ToastTheme.DURATION_SHORT : ToastTheme.DURATION_LONG;
            final int sectionIndex = i;

            toastButton.setOnClickListener(v -> {
                Map<String, String> message = DemoContent.getToastMessage(sectionIndex);
                screen.showToast(
                        message.getOrDefault(demoState.getCurrentLanguage(), ""), duration);
            });

            section.addView(toastButton);
            panel.addView(section);
        }

        return panel;
    }
}
