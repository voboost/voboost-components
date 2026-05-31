package ru.voboost.components.demo.pixel;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import ru.voboost.components.button.Button;
import ru.voboost.components.button.ButtonStyle;
import ru.voboost.components.checkbox.Checkbox;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.panel.Panel;
import ru.voboost.components.radio.Radio;
import ru.voboost.components.radio.RadioButton;
import ru.voboost.components.screen.Screen;
import ru.voboost.components.section.Section;
import ru.voboost.components.tabs.TabItem;
import ru.voboost.components.tabs.Tabs;
import ru.voboost.components.theme.Theme;

/**
 * Button, Checkbox, Radio demo that replicates reference screenshot (button-checkbox-radio_1original.png).
 *
 * Component hierarchy:
 * Screen (root)
 * +-- Tabs (5 tabs)
 * +-- Panel[] (5 panels, one per tab)
 * +-- Panel "system" (selected):
 * +-- Section "Factory reset" + Button
 * +-- Section "Wireless Charging For Phone" + Checkbox
 * +-- Section "Language" + Radio
 *
 * Theme: FREE_DARK
 * Language: EN
 */
public class ButtonCheckboxRadio extends Activity {

    private static final String TAG = "ButtonCheckboxRadio";

    // Fixed settings matching the reference screenshot
    private static final Theme THEME = Theme.FREE_DARK;
    private static final Language LANGUAGE = Language.EN;

    // UI components (accessible for testing)
    private Screen screen;
    private Tabs tabs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupFullScreenMode();
        setupComponentHierarchy();

        Log.d(TAG, "ButtonCheckboxRadio demo created successfully");
    }

    /**
     * Sets up full-screen immersive mode for automotive display.
     */
    private void setupFullScreenMode() {
        try {
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } catch (Exception e) {
            // Ignore under Robolectric where getDecorView() may return null
        }
    }

    /**
     * Builds the full component hierarchy matching the reference screenshot.
     */
    private void setupComponentHierarchy() {
        List<TabItem> tabItems = new java.util.ArrayList<>();
        tabItems.add(TabItem.create("sound", createMap("Sound")).build());
        tabItems.add(TabItem.create("reminder", createMap("Reminder")).build());
        tabItems.add(TabItem.create("device", createMap("Device")).build());
        tabItems.add(TabItem.create("privacy", createMap("Privacy")).build());
        tabItems.add(TabItem.create("system", createMap("System")).build());

        Panel[] panels = createAllPanels();

        screen = Screen.create(this, THEME)
            .backgroundColorHex("#000000")
            .tabs(Tabs.create(this, THEME, LANGUAGE, tabItems).build())
            .panels(panels)
            .build();
        setContentView(screen);

        screen.getTabs().setSelectedValue("system", false);
    }

    /**
     * Creates one panel per tab. Each panel has its own named method.
     */
    private Panel[] createAllPanels() {
        return new Panel[] {
                new Panel(this), // Sound
                new Panel(this), // Reminder
                new Panel(this), // Device
                new Panel(this), // Privacy
                createSystemPanel() // System (selected)
        };
    }

    /**
     * Creates the System panel with button, checkbox, and radio.
     */
    private Panel createSystemPanel() {
        Panel panel = new Panel(this);
        panel.setTheme(THEME);

        // Section 1: Factory reset + Button
        Section section1 = new Section(this);
        section1.setTitle(createMap("Factory reset"));
        section1.setTheme(THEME);
        section1.setLanguage(LANGUAGE);

        section1.addButton(
            Button.create(this, THEME, LANGUAGE, "Factory reset", ButtonStyle.SECONDARY)
                .padding(45, 1, 44, 0)
                .margin(0, 0, 0, 42)
                .build()
        );
        panel.addView(section1);

        // Section 2: Wireless Charging For Phone + Checkbox
        Section section2 = new Section(this);
        section2.setTitle(createMap("Wireless Charging For Phone"));
        section2.setTheme(THEME);
        section2.setLanguage(LANGUAGE);

        section2.addCheckbox(
            Checkbox.create(this, THEME, LANGUAGE, true)
                .label(createMap("Wireless Charging For Mobile Devices"))
                .build()
        );
        panel.addView(section2);

        // Section 3: Language + Radio
        Section section3 = new Section(this);
        section3.setTitle(createMap("Language"));
        section3.setTheme(THEME);
        section3.setLanguage(LANGUAGE);

        List<RadioButton> radioButtons = new java.util.ArrayList<>();
        radioButtons.add(new RadioButton("zh", createMap("Chinese (Simplified)")));
        radioButtons.add(new RadioButton("en", createMap("English")));

        section3.addRadio(
            Radio.create(this, THEME, LANGUAGE, radioButtons, "en").build()
        );
        panel.addView(section3);

        return panel;
    }

    private java.util.Map<String, String> createMap(String text) {
        java.util.Map<String, String> map = new java.util.HashMap<>();
        map.put("en", text);
        map.put("ru", text);
        return map;
    }

    // ================================================================
    // GETTERS FOR TESTING
    // ================================================================

    /** Returns the Screen component (for test access). */
    public Screen getScreen() {
        return screen;
    }

    /** Returns the Tabs component (for test access). */
    public Tabs getTabs() {
        return tabs;
    }
}
