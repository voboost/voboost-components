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
import ru.voboost.components.screen.Screen;
import ru.voboost.components.section.Section;
import ru.voboost.components.tabs.TabItem;
import ru.voboost.components.tabs.Tabs;
import ru.voboost.components.theme.Theme;

/**
 * Checkbox demo that replicates reference screenshot (checkbox-01_1original.png).
 *
 * Component hierarchy:
 * Screen (root)
 * +-- Tabs (5 tabs)
 * +-- Panel[] (5 panels, one per tab)
 * +-- Panel "mobile":
 * +-- Section "Mobile data network" + Radio
 * +-- Section "Mobile data network" + Checkbox[]
 *
 * Theme: FREE_DARK
 * Language: EN
 */
public class Checkbox01 extends Activity {

    private static final String TAG = "Checkbox01";

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

        Log.d(TAG, "Checkbox01 demo created successfully");
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
        tabItems.add(TabItem.create("network", createMap("Network")).build());
        tabItems.add(TabItem.create("display", createMap("Display")).build());
        tabItems.add(TabItem.create("voice", createMap("Voice")).build());
        tabItems.add(TabItem.create("sound", createMap("Sound")).build());
        tabItems.add(TabItem.create("reminder", createMap("Reminder")).build());

        Panel[] panels = createAllPanels();

        screen = Screen.create(this, THEME)
            .backgroundColorHex("#000000")
            .tabs(Tabs.create(this, THEME, LANGUAGE, tabItems).build())
            .panels(panels)
            .build();
        setContentView(screen);

        screen.getTabs().setSelectedValue("network", false);
    }

    /**
     * Creates one panel per tab. Each panel has its own named method.
     */
    private Panel[] createAllPanels() {
        return new Panel[] {
                createNetworkPanel(),
                new Panel(this),
                new Panel(this),
                new Panel(this),
                new Panel(this),
        };
    }

    /**
     * Creates the Network panel with radio and checkboxes.
     */
    private Panel createNetworkPanel() {
        Panel panel = new Panel(this);
        panel.setTheme(THEME);

        // Section 1: Mobile data network + Checkbox (Enable 5G)
        Section section1 = new Section(this);
        section1.setTitle(createMap("Mobile data network"));
        section1.setTheme(THEME);
        section1.setLanguage(LANGUAGE);

        section1.addCheckbox(
            Checkbox.create(this, THEME, LANGUAGE, false)
                .label(createMap("Enable 5G"))
                .description(createMap("When enabled, 5G will be automatically used in the 5G mobile network environment."))
                .build()
        );
        panel.addView(section1);

        // Section 2: Mobile data network + Checkboxes
        Section section2 = new Section(this);
        section2.setTitle(createMap("Mobile data network"));
        section2.setTheme(THEME);
        section2.setLanguage(LANGUAGE);

        section2.addCheckbox(
            Checkbox.create(this, THEME, LANGUAGE, false)
                .label(createMap("Reduce media volume during navigation broadcast"))
                .description(createMap("If too high during navigation broadcast, media volume will be automatically lowered."))
                .build()
        );

        section2.addCheckbox(
            Checkbox.create(this, THEME, LANGUAGE, true)
                .label(createMap("Speed compensated volume"))
                .description(createMap("The volume increases or decreases as the vehicle goes faster or slower. You no longer have to adjust the\nvolume frequently, thus able to focus on driving."))
                .build()
        );

        panel.addView(section2);

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
