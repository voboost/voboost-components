package ru.voboost.components.demo.java;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;

import ru.voboost.components.demo.shared.DemoContent;
import ru.voboost.components.demo.shared.DemoState;
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
            createPanelForTab("language"),
            createPanelForTab("theme"),
            createPanelForTab("car_type"),
            createPanelForTab("climate"),
            createPanelForTab("audio"),
            createPanelForTab("display"),
            createPanelForTab("system")
        };
    }

    /**
     * Creates a panel for a specific tab
     */
    private Panel createPanelForTab(String tabValue) {
        if ("climate".equals(tabValue)) {
            return createClimatePanelWithMultipleRadios();
        }

        // Create Panel
        Panel panel = new Panel(this);

        // Create Section
        Section section = new Section(this);
        section.setTitle(DemoContent.getSectionTitle(tabValue));

        // Create Radio with options for this tab
        List<RadioButton> radioButtons = DemoContent.getRadioButtons(tabValue);
        Radio radio = new Radio(this);
        radio.setButtons(radioButtons);
        radio.setSelectedValue(demoState.getSelectedValueForTab(tabValue));
        radio.setOnValueChangeListener(
                newValue -> {
                    Log.d(TAG, "Tab " + tabValue + " value changed to: " + newValue);
                    demoState.setSelectedValueForTab(tabValue, newValue);

                    // Special handling for language, theme, and car type tabs
                    if ("language".equals(tabValue)) {
                        demoState.setCurrentLanguage(newValue);
                    } else if ("theme".equals(tabValue)) {
                        demoState.setCurrentTheme(newValue);
                    } else if ("car_type".equals(tabValue)) {
                        demoState.setCurrentCarType(newValue);
                    }

                    updateAllComponents();
                });

        // Add Radio as child of Section (Section is now a ViewGroup)
        section.addView(radio);

        // Add Section to Panel
        panel.addView(section);

        return panel;
    }

    /**
     * Creates the climate panel with multiple sections and radio groups.
     * Demonstrates multiple Radio components stacked vertically with scrolling.
     * 5 sections × ~283px each = ~1415px total, overflowing the ~670px panel.
     */
    private Panel createClimatePanelWithMultipleRadios() {
        Panel panel = new Panel(this);

        // Create a ScrollView to hold multiple sections
        ScrollView scrollView = new ScrollView(this);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        // Create a LinearLayout inside ScrollView (ScrollView can only have one child)
        LinearLayout contentLayout = new LinearLayout(this);
        contentLayout.setOrientation(LinearLayout.VERTICAL);

        int sectionCount = DemoContent.getClimateSectionCount();

        for (int i = 0; i < sectionCount; i++) {
            // Create Section with title
            Section section = new Section(this);
            section.setTitle(DemoContent.getClimateSectionTitle(i));

            // Create Radio with options for this sub-section
            List<RadioButton> radioButtons = DemoContent.getClimateSubRadioButtons(i);
            Radio radio = new Radio(this);
            radio.setButtons(radioButtons);
            radio.setSelectedValue(DemoContent.getClimateSubDefaultValue(i));

            final int sectionIndex = i;
            radio.setOnValueChangeListener(
                    newValue -> {
                        Log.d(
                                TAG,
                                "Climate section "
                                        + sectionIndex
                                        + " value changed to: "
                                        + newValue);
                    });

            // Add Radio as child of Section
            section.addView(radio);

            // Add Section to LinearLayout
            contentLayout.addView(section);
        }

        scrollView.addView(contentLayout);
        panel.addView(scrollView);

        return panel;
    }

    /**
     * Updates a single Section and its child Radio components.
     */
    private void updateSection(Section section, String combinedTheme) {
        section.setLanguage(Language.fromCode(demoState.getCurrentLanguage()));
        section.setTheme(Theme.fromValue(combinedTheme));

        for (int j = 0; j < section.getChildCount(); j++) {
            View sectionChild = section.getChildAt(j);
            if (sectionChild instanceof Radio) {
                Radio radio = (Radio) sectionChild;
                radio.setLanguage(Language.fromCode(demoState.getCurrentLanguage()));
                radio.setTheme(Theme.fromValue(combinedTheme));
            }
        }
    }

    /**
     * Updates all components with current state
     * Implements reactive behavior across all components in the hierarchy
     */
    private void updateAllComponents() {
        String combinedTheme = demoState.getCombinedTheme();

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

        // Update Screen component
        if (screen != null) {
            screen.setTheme(Theme.fromValue(combinedTheme));
        }

        // Update Tabs component
        if (tabs != null) {
            tabs.setLanguage(Language.fromCode(demoState.getCurrentLanguage()));
            tabs.setTheme(Theme.fromValue(combinedTheme));
        }

        // Update all panels
        Panel[] panels = getPanelsFromScreen();
        if (panels != null) {
            for (Panel panel : panels) {
                if (panel != null) {
                    panel.setTheme(Theme.fromValue(combinedTheme));

                    // Update child views in panel (handles both direct Section children
                    // and ScrollView → LinearLayout → Section hierarchy)
                    for (int i = 0; i < panel.getChildCount(); i++) {
                        View child = panel.getChildAt(i);
                        if (child instanceof Section) {
                            updateSection((Section) child, combinedTheme);
                        } else if (child instanceof ScrollView) {
                            ScrollView scrollView = (ScrollView) child;
                            if (scrollView.getChildCount() > 0) {
                                View scrollChild = scrollView.getChildAt(0);
                                if (scrollChild instanceof LinearLayout) {
                                    LinearLayout layout = (LinearLayout) scrollChild;
                                    for (int j = 0; j < layout.getChildCount(); j++) {
                                        View layoutChild = layout.getChildAt(j);
                                        if (layoutChild instanceof Section) {
                                            updateSection((Section) layoutChild, combinedTheme);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
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
        String selectedTab = demoState.getSelectedTab();
        Panel[] panels = getPanelsFromScreen();
        int tabIndex = getTabIndex(selectedTab);

        // Get the section from the current panel
        if (panels != null && tabIndex < panels.length) {
            Panel panel = panels[tabIndex];
            if (panel.getChildCount() > 0) {
                View firstChild = panel.getChildAt(0);
                return firstChild instanceof Section ? (Section) firstChild : null;
            }
        }
        return null;
    }

    public Radio getCurrentRadio() {
        // Get the current section first
        Section section = getCurrentSection();
        if (section == null) return null;

        // Radio is now a child of Section
        for (int i = 0; i < section.getChildCount(); i++) {
            View child = section.getChildAt(i);
            if (child instanceof Radio) {
                return (Radio) child;
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

        Panel climatePanel = panels[3]; // climate is index 3
        for (int i = 0; i < climatePanel.getChildCount(); i++) {
            View child = climatePanel.getChildAt(i);
            if (child instanceof ScrollView) {
                return (ScrollView) child;
            }
        }
        return null;
    }

    private int getTabIndex(String tabValue) {
        switch (tabValue) {
            case "language":
                return 0;
            case "theme":
                return 1;
            case "car_type":
                return 2;
            case "climate":
                return 3;
            case "audio":
                return 4;
            case "display":
                return 5;
            case "system":
                return 6;
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
}
