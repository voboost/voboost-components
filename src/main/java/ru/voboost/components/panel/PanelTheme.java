package ru.voboost.components.panel;

import android.graphics.Color;

import ru.voboost.components.theme.Theme;

/**
 * Theme constants for the Panel component.
 *
 * <p>Contains all visual styling parameters including:
 * <ul>
 *   <li>Dimensions (in pixels)</li>
 *   <li>Colors for each theme variant</li>
 *   <li>Animation parameters</li>
 * </ul>
 */
public final class PanelTheme {

    private PanelTheme() {
        // Prevent instantiation
    }

    // ============================================================
    // DIMENSIONS (all values in PIXELS)
    // ============================================================

    /** Corner radius for the panel */
    public static final int CORNER_RADIUS = 0;

    /** Padding inside the panel */
    public static final int PADDING = 0;

    /** Shadow elevation */
    public static final int ELEVATION = 0;

    /** Border width */
    public static final int BORDER_WIDTH = 0;

    // ============================================================
    // COLORS - FREE LIGHT THEME
    // ============================================================

    /** Background color for the panel - Free Light */
    public static final int FREE_LIGHT_BACKGROUND = Color.TRANSPARENT;

    /** Border color for the panel - Free Light */
    public static final int FREE_LIGHT_BORDER = Color.TRANSPARENT;

    /** Shadow color for the panel - Free Light */
    public static final int FREE_LIGHT_SHADOW = Color.TRANSPARENT;

    // ============================================================
    // COLORS - FREE DARK THEME
    // ============================================================

    /** Background color for the panel - Free Dark */
    public static final int FREE_DARK_BACKGROUND = Color.TRANSPARENT;

    /** Border color for the panel - Free Dark */
    public static final int FREE_DARK_BORDER = Color.TRANSPARENT;

    /** Shadow color for the panel - Free Dark */
    public static final int FREE_DARK_SHADOW = Color.TRANSPARENT;

    // ============================================================
    // COLORS - DREAMER LIGHT THEME
    // ============================================================

    /** Background color for the panel - Dreamer Light */
    public static final int DREAMER_LIGHT_BACKGROUND = Color.TRANSPARENT;

    /** Border color for the panel - Dreamer Light */
    public static final int DREAMER_LIGHT_BORDER = Color.TRANSPARENT;

    /** Shadow color for the panel - Dreamer Light */
    public static final int DREAMER_LIGHT_SHADOW = Color.TRANSPARENT;

    // ============================================================
    // COLORS - DREAMER DARK THEME
    // ============================================================

    /** Background color for the panel - Dreamer Dark */
    public static final int DREAMER_DARK_BACKGROUND = Color.TRANSPARENT;

    /** Border color for the panel - Dreamer Dark */
    public static final int DREAMER_DARK_BORDER = Color.TRANSPARENT;

    /** Shadow color for the panel - Dreamer Dark */
    public static final int DREAMER_DARK_SHADOW = Color.TRANSPARENT;

    // ============================================================
    // COLOR GETTERS
    // ============================================================

    /**
     * Returns the background color for the specified theme.
     *
     * @param theme the current theme
     * @return the background color
     */
    public static int getBackground(Theme theme) {
        switch (theme) {
            case FREE_LIGHT:
                return FREE_LIGHT_BACKGROUND;
            case FREE_DARK:
                return FREE_DARK_BACKGROUND;
            case DREAMER_LIGHT:
                return DREAMER_LIGHT_BACKGROUND;
            case DREAMER_DARK:
                return DREAMER_DARK_BACKGROUND;
        }
        // All enum cases are covered above - this should never be reached
        throw new IllegalArgumentException("Unknown theme: " + theme);
    }

    /**
     * Returns the border color for the specified theme.
     *
     * @param theme the current theme
     * @return the border color
     */
    public static int getBorder(Theme theme) {
        switch (theme) {
            case FREE_LIGHT:
                return FREE_LIGHT_BORDER;
            case FREE_DARK:
                return FREE_DARK_BORDER;
            case DREAMER_LIGHT:
                return DREAMER_LIGHT_BORDER;
            case DREAMER_DARK:
                return DREAMER_DARK_BORDER;
        }
        // All enum cases are covered above - this should never be reached
        throw new IllegalArgumentException("Unknown theme: " + theme);
    }

    /**
     * Returns the shadow color for the specified theme.
     *
     * @param theme the current theme
     * @return the shadow color
     */
    public static int getShadow(Theme theme) {
        switch (theme) {
            case FREE_LIGHT:
                return FREE_LIGHT_SHADOW;
            case FREE_DARK:
                return FREE_DARK_SHADOW;
            case DREAMER_LIGHT:
                return DREAMER_LIGHT_SHADOW;
            case DREAMER_DARK:
                return DREAMER_DARK_SHADOW;
        }
        // All enum cases are covered above - this should never be reached
        throw new IllegalArgumentException("Unknown theme: " + theme);
    }
}
