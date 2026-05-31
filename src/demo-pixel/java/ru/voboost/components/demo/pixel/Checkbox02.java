package ru.voboost.components.demo.pixel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.app.Activity;
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
import ru.voboost.components.tabs.Tabs;
import ru.voboost.components.tabs.TabItem;
import ru.voboost.components.tabs.Tabs;
import ru.voboost.components.theme.Theme;

/**
 * Checkbox demo that replicates reference screenshots.
 *
 * Component hierarchy:
 * Screen (root)
 * +-- Tabs (5 tabs)
 * +-- Panel[] (5 panels, one per tab)
 * +-- Panel "privacy":
 * +-- Section "Privacy protection" + Checkbox[]
 *
 * Theme: FREE_DARK
 * Language: EN
 */
public class Checkbox02 extends Activity {

    private static final String TAG = "Checkbox02";

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

        Log.d(TAG, "Checkbox02 demo created successfully");
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
        List<TabItem> tabItems = new ArrayList<>();
        tabItems.add(TabItem.create("reminder", createMap("Reminder")).build());
        tabItems.add(TabItem.create("device", createMap("Device")).build());
        tabItems.add(TabItem.create("privacy", createMap("Privacy")).selected(true).build());
        tabItems.add(TabItem.create("system", createMap("System")).build());
        tabItems.add(TabItem.create("vehicle", createMap("Vehicle")).marginTop(30).more(true).build());

        Panel[] panels = createAllPanels();

        screen = Screen.create(this, THEME)
            .backgroundColorHex("#000000")
            .tabs(Tabs.create(this, THEME, LANGUAGE, tabItems).build())
            .panels(panels)
            .build();
        setContentView(screen);

        screen.getTabs().setSelectedValue("privacy", false);
    }

    /**
     * Creates one panel per tab. Each panel has its own named method.
     */
    private Panel[] createAllPanels() {
        return new Panel[] {
                new Panel(this), // Reminder
                new Panel(this), // Device
                createPrivacyPanel(), // Privacy
                new Panel(this), // System
                new Panel(this) // Vehicle
        };
    }

    /**
     * Creates the Privacy panel with various checkboxes.
     */
    private Panel createPrivacyPanel() {
        Panel panel = new Panel(this);
        panel.setTheme(THEME);

        Section section = new Section(this);
        section.setTitle(createMap("Privacy protection"));
        section.setTheme(THEME);
        section.setLanguage(LANGUAGE);
        section.setPaddingTop(23);
        section.setTitleCheckbox(true);

        section.addCheckbox(
            Checkbox.create(this, THEME, LANGUAGE, true)
                .label(createMap("Always remind me when system starts"))
                .margin(1, 4, 0, 52)
                .build()
        );

        section.addCheckbox(
            Checkbox.create(this, THEME, LANGUAGE, true)
                .label(createMap("Bluetooth call privacy"))
                .description(createMap(
                        "The incoming call number is encrypted, and will not be displayed in contact list and call history"))
                .margin(1, 3, 0, 49)
                .build()
        );

        section.addCheckbox(
            Checkbox.create(this, THEME, LANGUAGE, true)
                .label(createMap("Schedule privacy"))
                .description(createMap("Hide details when reminding of a schedule"))
                .margin(1, 3, 0, 49)
                .build()
        );

        section.addCheckbox(
            Checkbox.create(this, THEME, LANGUAGE, true)
                .label(createMap("Whereabouts privacy"))
                .description(createMap(
                        "Navigation footprints, favorites and search history disabled, and group location sharing not allowed on IVI"))
                .margin(1, 3, 0, 49)
                .build()
        );

        section.addCheckbox(
            Checkbox.create(this, THEME, LANGUAGE, true)
                .label(createMap("Disable interior camera"))
                .margin(1, 3, 0, 42)
                .build()
        );

        panel.addView(section);

        return panel;
    }

    private Map<String, String> createMap(String text) {
        Map<String, String> map = new HashMap<>();
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
