package ru.voboost.components.demo.pixel;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import ru.voboost.components.i18n.Language;
import ru.voboost.components.panel.Panel;
import ru.voboost.components.radio.Radio;
import ru.voboost.components.screen.Screen;
import ru.voboost.components.section.Section;
import ru.voboost.components.tabs.Tabs;
import ru.voboost.components.theme.Theme;

/**
 * Pixel demo that replicates the reference screenshot (interface-2-display.png).
 *
 * Component hierarchy:
 *   Screen (root, offsetX=145, offsetY=50)
 *   +-- Tabs (5 tabs, "Display" selected)
 *   +-- Panel[] (5 panels, one per tab)
 *       +-- Panel "display":
 *           +-- Section "Language" + Radio
 *           +-- Section "Language" + Radio
 *           +-- Section "Language" + Radio
 *
 * Theme: FREE_DARK
 * Language: EN
 */
public class MainActivity extends Activity {

    private static final String TAG = "PixelDemo";

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

        Log.d(TAG, "Pixel demo created successfully");
    }

    /**
     * Sets up full-screen immersive mode for automotive display.
     */
    private void setupFullScreenMode() {
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
    }

    /**
     * Builds the full component hierarchy matching the reference screenshot.
     */
    private void setupComponentHierarchy() {
        // 1. Create Screen (root container)
        screen = new Screen(this);
        screen.setTheme(THEME);
        screen.setBackgroundColor(Color.parseColor("#000000"));
        setContentView(screen);

        // 2. Create Tabs
        tabs = new Tabs(this);
        tabs.setItems(PixelContent.getTabItems());
        tabs.setTheme(THEME);
        tabs.setLanguage(LANGUAGE);
        screen.setTabs(tabs);

        // 3. Create panels (one per tab, each with its own named method)
        Panel[] panels = createAllPanels();
        screen.setPanels(panels);

        // 4. Select initial tab AFTER setTabs and setPanels
        // This triggers onTabChangeListener which calls screen.setActivePanel()
        tabs.setSelectedValue(PixelContent.getSelectedTab(), false);

        // 5. Tab change listener
        tabs.setOnValueChangeListener(
                selectedTab -> {
                    Log.d(TAG, "Tab changed to: " + selectedTab);
                });
    }

    /**
     * Creates one panel per tab. Each panel has its own named method.
     */
    private Panel[] createAllPanels() {
        return new Panel[] {
            createNetworkPanel(),
            createDisplayPanel(),
            createVoicePanel(),
            createSoundPanel(),
            createReminderPanel()
        };
    }

    /**
     * Creates the Network panel (empty in the reference screenshot).
     */
    private Panel createNetworkPanel() {
        Panel panel = new Panel(this);
        panel.setTheme(THEME);
        return panel;
    }

    /**
     * Creates the Display panel with 3 Language sections.
     * This is the active panel in the reference screenshot.
     */
    private Panel createDisplayPanel() {
        Panel panel = new Panel(this);
        panel.setTheme(THEME);

        for (int i = 0; i < PixelContent.getSectionCount(); i++) {
            Section section = new Section(this);
            section.setTitle(PixelContent.getLanguageSectionTitle());
            section.setTheme(THEME);
            section.setLanguage(LANGUAGE);

            Radio radio = new Radio(this);
            List<ru.voboost.components.radio.RadioButton> buttons =
                    PixelContent.getLanguageRadioButtons();

            radio.setButtons(buttons);
            radio.setSelectedValue(PixelContent.getSelectedLanguage());
            radio.setTheme(THEME);
            radio.setLanguage(LANGUAGE);

            section.addView(radio);
            panel.addView(section);
        }

        return panel;
    }

    /**
     * Creates the Voice panel (empty in the reference screenshot).
     */
    private Panel createVoicePanel() {
        Panel panel = new Panel(this);
        panel.setTheme(THEME);
        return panel;
    }

    /**
     * Creates the Sound panel (empty in the reference screenshot).
     */
    private Panel createSoundPanel() {
        Panel panel = new Panel(this);
        panel.setTheme(THEME);
        return panel;
    }

    /**
     * Creates the Reminder panel (empty in the reference screenshot).
     */
    private Panel createReminderPanel() {
        Panel panel = new Panel(this);
        panel.setTheme(THEME);
        return panel;
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
