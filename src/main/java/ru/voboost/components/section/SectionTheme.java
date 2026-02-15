package ru.voboost.components.section;

import android.graphics.Color;

import ru.voboost.components.theme.Theme;

/**
 * Theme constants for the Section component.
 *
 * <p>Contains all visual styling parameters including:
 * <ul>
 *   <li>Dimensions (in pixels)</li>
 *   <li>Colors for each theme variant</li>
 *   <li>Text styling</li>
 * </ul>
 *
 * <p>All dimensions are in PIXELS, not dp. This is a critical automotive requirement.
 */
public final class SectionTheme {

    private SectionTheme() {
        // Prevent instantiation
    }

    // ============================================================
    // DIMENSIONS (all values in PIXELS)
    // ============================================================

    /** Corner radius for the section */
    public static final int CORNER_RADIUS = 20;

    /** Fixed width for the section */
    public static final int SECTION_WIDTH = 1364;

    /** Horizontal margin outside the section (left and right) */
    public static final int HORIZONTAL_MARGIN = 0;

    /** Bottom margin below the section (spacing between sections) */
    public static final int BOTTOM_MARGIN = 25;

    /** Title text left margin inside the section */
    public static final int TITLE_MARGIN_START = 30;

    /** Title text top margin inside the section */
    public static final int TITLE_MARGIN_TOP = 25;

    /** Spacing between title and content area */
    public static final int TITLE_SPACING = 40;

    /** Content padding bottom inside the section */
    public static final int CONTENT_PADDING_BOTTOM = 50;

    /** Content padding horizontal (left and right) inside the section */
    public static final int CONTENT_PADDING_HORIZONTAL = 36;

    /** Title text size */
    public static final int TITLE_TEXT_SIZE = 32;

    /** Border width - vendor has no border, set to 0 */
    public static final int BORDER_WIDTH = 0;

    // ============================================================
    // COLORS - FREE LIGHT THEME
    // ============================================================

    /** Background color for the section - Free Light */
    public static final int FREE_LIGHT_BACKGROUND = Color.parseColor("#f1f5fb");

    /** Title text color - Free Light */
    public static final int FREE_LIGHT_TITLE_TEXT = Color.parseColor("#1a1a1a");

    // ============================================================
    // COLORS - FREE DARK THEME
    // ============================================================

    /** Background color for the section - Free Dark */
    public static final int FREE_DARK_BACKGROUND = Color.parseColor("#23272f");

    /** Title gradient start color (left edge) - Free Dark */
    public static final int FREE_DARK_TITLE_GRADIENT_START = Color.parseColor("#181b21");

    /** Title text color - Free Dark */
    public static final int FREE_DARK_TITLE_TEXT = Color.parseColor("#ffffff");

    // ============================================================
    // COLORS - DREAMER LIGHT THEME
    // ============================================================

    /** Background color for the section - Dreamer Light */
    public static final int DREAMER_LIGHT_BACKGROUND = Color.parseColor("#f5f0eb");

    /** Title text color - Dreamer Light */
    public static final int DREAMER_LIGHT_TITLE_TEXT = Color.parseColor("#1a1a1a");

    // ============================================================
    // COLORS - DREAMER DARK THEME
    // ============================================================

    /** Background color for the section - Dreamer Dark */
    public static final int DREAMER_DARK_BACKGROUND = Color.parseColor("#25272b");

    /** Title gradient start color (left edge) - Dreamer Dark */
    public static final int DREAMER_DARK_TITLE_GRADIENT_START = Color.parseColor("#18191e");

    /** Title text color - Dreamer Dark */
    public static final int DREAMER_DARK_TITLE_TEXT = Color.parseColor("#ffffff");

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

    /**
     * Returns the title text color for the specified theme.
     *
     * @param theme the current theme
     * @return the title text color
     */
    public static int getTitleTextColor(Theme theme) {
        switch (theme) {
            case FREE_LIGHT:
                return FREE_LIGHT_TITLE_TEXT;
            case FREE_DARK:
                return FREE_DARK_TITLE_TEXT;
            case DREAMER_LIGHT:
                return DREAMER_LIGHT_TITLE_TEXT;
            case DREAMER_DARK:
                return DREAMER_DARK_TITLE_TEXT;
            default:
                return FREE_LIGHT_TITLE_TEXT;
        }
    }

    /**
     * Returns the title gradient start color (left edge) for the specified theme.
     * Returns 0 (transparent) for light themes where no gradient is used.
     *
     * @param theme the current theme
     * @return the gradient start color, or 0 if no gradient for this theme
     */
    public static int getTitleGradientStart(Theme theme) {
        switch (theme) {
            case FREE_DARK:
                return FREE_DARK_TITLE_GRADIENT_START;
            case DREAMER_DARK:
                return DREAMER_DARK_TITLE_GRADIENT_START;
            default:
                return 0;
        }
    }

    /**
     * Returns whether the title gradient should be drawn for the specified theme.
     *
     * @param theme the current theme
     * @return true if gradient should be drawn
     */
    public static boolean hasTitleGradient(Theme theme) {
        return theme == Theme.FREE_DARK || theme == Theme.DREAMER_DARK;
    }
}
