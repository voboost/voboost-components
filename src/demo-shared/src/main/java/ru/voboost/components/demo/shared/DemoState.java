package ru.voboost.components.demo.shared;

import java.util.HashMap;
import java.util.Map;

/**
 * Global state management for demo applications.
 *
 * <p>This class manages the shared state across all demo applications:
 * - Selected tab
 * - Language selection
 * - Theme selection
 * - Car type selection
 * - Radio values for each tab
 * - Screen lift state
 */
public class DemoState {

    // Default values
    private static final String DEFAULT_LANGUAGE = "en";
    private static final String DEFAULT_THEME = "dark";
    private static final String DEFAULT_CAR_TYPE = "free";
    private static final String DEFAULT_SELECTED_TAB = "language";
    private static final int DEFAULT_SCREEN_LIFT_STATE = 2; // SCREEN_RAISED

    // State fields
    private String selectedTab;
    private String currentLanguage;
    private String currentTheme;
    private String currentCarType;
    private int screenLiftState;
    private Map<String, String> tabValues;

    /**
     * Creates a new DemoState with default values.
     */
    public DemoState() {
        this.selectedTab = DEFAULT_SELECTED_TAB;
        this.currentLanguage = DEFAULT_LANGUAGE;
        this.currentTheme = DEFAULT_THEME;
        this.currentCarType = DEFAULT_CAR_TYPE;
        this.screenLiftState = DEFAULT_SCREEN_LIFT_STATE;
        this.tabValues = new HashMap<>();

        // Initialize default values for all tabs
        initializeDefaultTabValues();
    }

    /**
     * Initializes default values for all tabs.
     */
    private void initializeDefaultTabValues() {
        tabValues.put("language", DemoContent.getDefaultValue("language"));
        tabValues.put("theme", DemoContent.getDefaultValue("theme"));
        tabValues.put("car_type", DemoContent.getDefaultValue("car_type"));
        tabValues.put("climate", DemoContent.getDefaultValue("climate"));
        tabValues.put("audio", DemoContent.getDefaultValue("audio"));
        tabValues.put("display", DemoContent.getDefaultValue("display"));
        tabValues.put("system", DemoContent.getDefaultValue("system"));
    }

    // Getters and setters

    /**
     * Returns the currently selected tab.
     *
     * @return the selected tab value
     */
    public String getSelectedTab() {
        return selectedTab;
    }

    /**
     * Sets the currently selected tab.
     *
     * @param selectedTab the tab value to select
     */
    public void setSelectedTab(String selectedTab) {
        if (selectedTab != null && !selectedTab.trim().isEmpty()) {
            this.selectedTab = selectedTab;
        }
    }

    /**
     * Returns the current language.
     *
     * @return the language code (e.g., "en", "ru")
     */
    public String getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * Sets the current language.
     *
     * @param currentLanguage the language code (e.g., "en", "ru")
     */
    public void setCurrentLanguage(String currentLanguage) {
        if (currentLanguage != null && !currentLanguage.trim().isEmpty()) {
            this.currentLanguage = currentLanguage;
        }
    }

    /**
     * Returns the current theme.
     *
     * @return the theme value (e.g., "light", "dark")
     */
    public String getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Sets the current theme.
     *
     * @param currentTheme the theme value (e.g., "light", "dark")
     */
    public void setCurrentTheme(String currentTheme) {
        if (currentTheme != null && !currentTheme.trim().isEmpty()) {
            this.currentTheme = currentTheme;
        }
    }

    /**
     * Returns the current car type.
     *
     * @return the car type value (e.g., "free", "dreamer")
     */
    public String getCurrentCarType() {
        return currentCarType;
    }

    /**
     * Sets the current car type.
     *
     * @param currentCarType the car type value (e.g., "free", "dreamer")
     */
    public void setCurrentCarType(String currentCarType) {
        if (currentCarType != null && !currentCarType.trim().isEmpty()) {
            this.currentCarType = currentCarType;
        }
    }

    /**
     * Returns the screen lift state.
     *
     * @return the screen lift state (1 for lowered, 2 for raised)
     */
    public int getScreenLiftState() {
        return screenLiftState;
    }

    /**
     * Sets the screen lift state.
     *
     * @param screenLiftState the screen lift state (1 for lowered, 2 for raised)
     */
    public void setScreenLiftState(int screenLiftState) {
        if (screenLiftState == 1 || screenLiftState == 2) {
            this.screenLiftState = screenLiftState;
        }
    }

    /**
     * Returns the selected value for the specified tab.
     *
     * @param tabValue the tab value (e.g., "language", "theme")
     * @return the selected value for the tab
     */
    public String getSelectedValueForTab(String tabValue) {
        return tabValues.getOrDefault(tabValue, "");
    }

    /**
     * Sets the selected value for the specified tab.
     *
     * @param tabValue the tab value (e.g., "language", "theme")
     * @param value the value to set
     */
    public void setSelectedValueForTab(String tabValue, String value) {
        if (tabValue != null
                && value != null
                && !tabValue.trim().isEmpty()
                && !value.trim().isEmpty()) {
            tabValues.put(tabValue, value);
        }
    }

    /**
     * Returns the combined theme (carType-theme).
     *
     * @return the combined theme string (e.g., "free-light", "dreamer-dark")
     */
    public String getCombinedTheme() {
        return currentCarType + "-" + currentTheme;
    }

    /**
     * Resets all state to default values.
     */
    public void reset() {
        this.selectedTab = DEFAULT_SELECTED_TAB;
        this.currentLanguage = DEFAULT_LANGUAGE;
        this.currentTheme = DEFAULT_THEME;
        this.currentCarType = DEFAULT_CAR_TYPE;
        this.screenLiftState = DEFAULT_SCREEN_LIFT_STATE;
        initializeDefaultTabValues();
    }

    /**
     * Returns a string representation of the current state.
     *
     * @return string representation of the state
     */
    @Override
    public String toString() {
        return "DemoState{"
                + "selectedTab='"
                + selectedTab
                + '\''
                + ", currentLanguage='"
                + currentLanguage
                + '\''
                + ", currentTheme='"
                + currentTheme
                + '\''
                + ", currentCarType='"
                + currentCarType
                + '\''
                + ", screenLiftState="
                + screenLiftState
                + ", tabValues="
                + tabValues
                + ", combinedTheme='"
                + getCombinedTheme()
                + '\''
                + '}';
    }
}
