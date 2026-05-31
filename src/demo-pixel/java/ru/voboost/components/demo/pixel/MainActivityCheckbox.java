package ru.voboost.components.demo.pixel;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import ru.voboost.components.checkbox.Checkbox;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.panel.Panel;
import ru.voboost.components.radio.Radio;
import ru.voboost.components.screen.Screen;
import ru.voboost.components.section.Section;
import ru.voboost.components.tabs.TabItem;
import ru.voboost.components.tabs.Tabs;
import ru.voboost.components.theme.Theme;

/**
 * Pixel demo that replicates reference screenshots.
 *
 * Component hierarchy:
 * Screen (root)
 * +-- Tabs (5 tabs)
 * +-- Panel[] (5 panels, one per tab)
 * +-- Panel "mobile":
 * +-- Section "Mobile" + Checkbox[]
 *
 * Theme: FREE_DARK
 * Language: EN
 */
public class MainActivityCheckbox extends Activity {

    private static final String TAG = "MainActivityCheckbox";

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
        List<TabItem> tabItems = PixelContent.getTabItems();

        screen = Screen.create(this, THEME)
            .backgroundColorHex("#000000")
            .tabs(Tabs.create(this, THEME, LANGUAGE, tabItems).build())
            .panels(createAllPanels())
            .build();
        setContentView(screen);

        screen.getTabs().setSelectedValue(PixelContent.getSelectedTab(), false);
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

        Section section = new Section(this);
        section.setTitle(createMap("Mobile"));
        section.setTheme(THEME);
        section.setLanguage(LANGUAGE);

        section.addCheckbox(
            Checkbox.create(this, THEME, LANGUAGE, false)
                .label(createMap("Enable 5G"))
                .description(createMap("When enabled, 5G will be automatically used in the 5G mobile network environment."))
                .build()
        );

        Checkbox cb2 = section.addCheckbox(
            Checkbox.create(this, THEME, LANGUAGE, false)
                .label(createMap("System key t"))
                .build()
        );
        cb2.setPrimitiveAlpha(0.5f);

        Checkbox cb3 = section.addCheckbox(
            Checkbox.create(this, THEME, LANGUAGE, true)
                .label(createMap("Reduce media volume during navigation br"))
                .description(createMap("If too high during navigation br"))
                .build()
        );
        cb3.setTransitionState(1.0f, 0.45f);

        section.addCheckbox(
            Checkbox.create(this, THEME, LANGUAGE, true)
                .label(createMap("Speed compensated volume"))
                .description(createMap("The volume increases or decr\nvolume frequently"))
                .build()
        );

        panel.addView(section);

        return panel;
    }

    private java.util.Map<String, String> createMap(String text) {
        java.util.Map<String, String> map = new java.util.HashMap<>();
        map.put("en", text);
        map.put("ru", text);
        return map;
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
