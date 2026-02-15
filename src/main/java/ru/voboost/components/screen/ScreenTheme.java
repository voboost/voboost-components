package ru.voboost.components.screen;

import android.graphics.Color;

import ru.voboost.components.theme.Theme;

/**
 * Theme constants for the Screen component.
 *
 * <p>Contains all visual styling parameters including:
 * <ul>
 *   <li>Dimensions (in pixels)</li>
 *   <li>Colors for each theme variant</li>
 * </ul>
 *
 * <p>All dimensions are in PIXELS, not dp. This is a critical automotive requirement.
 */
public final class ScreenTheme {

    private ScreenTheme() {
        // Prevent instantiation
    }

    // ============================================================
    // DIMENSIONS (all values in PIXELS)
    // ============================================================

    /** Padding inside the screen */
    public static final int PADDING = 32;

    /** Horizontal gap between Tabs and Panel */
    public static final int GAP_X = 42;

    // ============================================================
    // COLORS - FREE LIGHT THEME
    // ============================================================

    /** Background color for the screen - Free Light */
    public static final int FREE_LIGHT_BACKGROUND = Color.parseColor("#ffffff");

    // ============================================================
    // COLORS - FREE DARK THEME
    // ============================================================

    /** Background color for the screen - Free Dark */
    public static final int FREE_DARK_BACKGROUND = Color.parseColor("#121212");

    // ============================================================
    // COLORS - DREAMER LIGHT THEME
    // ============================================================

    /** Background color for the screen - Dreamer Light */
    public static final int DREAMER_LIGHT_BACKGROUND = Color.parseColor("#fafafa");

    // ============================================================
    // COLORS - DREAMER DARK THEME
    // ============================================================

    /** Background color for the screen - Dreamer Dark */
    public static final int DREAMER_DARK_BACKGROUND = Color.parseColor("#000000");

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
            default:
                return FREE_LIGHT_BACKGROUND;
        }
    }
}
