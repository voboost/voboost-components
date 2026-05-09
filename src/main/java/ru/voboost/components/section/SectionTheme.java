package ru.voboost.components.section;

import android.graphics.Color;

import ru.voboost.components.theme.Theme;

/**
 * Theme constants for the Section component.
 *
 * <p>
 * Contains all visual styling parameters including:
 * <ul>
 * <li>Dimensions (in pixels)</li>
 * <li>Colors for each theme variant</li>
 * <li>Text styling</li>
 * </ul>
 *
 * <p>
 * All dimensions are in PIXELS, not dp. This is a critical automotive
 * requirement.
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

    /** Section width in compact panel mode */
    public static final int COMPACT_SECTION_WIDTH = 705;

    /** Horizontal margin outside the section (left and right) */
    public static final int HORIZONTAL_MARGIN = 0;

    /** Bottom margin below the section (spacing between sections) */
    public static final int BOTTOM_MARGIN = 25;

    /** Title text left margin inside the section */
    public static final int TITLE_MARGIN_START = 30;

    /** Title text top margin inside the section */
    public static final int TITLE_MARGIN_TOP = 25;

    /** Spacing between title and content area */
    public static final int CONTENT_PADDING_TOP = 40;

    /** Content padding bottom inside the section */
    public static final int CONTENT_PADDING_BOTTOM = 2;

    /** Content padding horizontal (left and right) inside the section */
    public static final int CONTENT_PADDING_HORIZONTAL = 30;

    /** Title text size */
    public static final int TITLE_TEXT_SIZE = 32;

    /** Border width - vendor has no border, set to 0 */
    public static final int BORDER_WIDTH = 0;

    /** Width/height of the info icon (i) bitmap drawn after the title */
    public static final int INFO_ICON_SIZE = 26;

    /** Gap between the title text (or title-checkbox) and the (i) icon */
    public static final int INFO_ICON_GAP = 20;

    /** Hit-area inflation around the (i) icon for finger touch */
    public static final int INFO_ICON_HIT_INFLATE = 20;

    // ============================================================
    // COLORS - FREE LIGHT THEME
    // ============================================================

    /** Background color for the section - Free Light */
    public static final int FREE_LIGHT_BACKGROUND = Color.parseColor("#f1f5fb");

    /** Title text color - Free Light */
    public static final int FREE_LIGHT_TITLE_TEXT = Color.parseColor("#ff2d3442");

    /** Info icon tint - Free Light (PNG is white+alpha, tinted to dark for light theme) */
    public static final int FREE_LIGHT_INFO_ICON_TINT = Color.parseColor("#ff2d3442");

    /** Title gradient start color (left edge) - Free Light (solid white) */
    public static final int FREE_LIGHT_TITLE_GRADIENT_START = Color.parseColor("#ffffff");

    /** Title gradient start color (left edge) - Dreamer Light (solid white) */
    public static final int DREAMER_LIGHT_TITLE_GRADIENT_START = Color.parseColor("#ffffff");

    // ============================================================
    // COLORS - FREE DARK THEME
    // ============================================================

    /** Background color for the section - Free Dark */
    public static final int FREE_DARK_BACKGROUND = Color.parseColor("#23272f");

    /** Title gradient start color (left edge) - Free Dark */
    public static final int FREE_DARK_TITLE_GRADIENT_START = Color.parseColor("#181b21");

    /** Title text color - Free Dark */
    public static final int FREE_DARK_TITLE_TEXT = Color.parseColor("#ffffff");

    // Info icon is drawn as-is on dark themes (white + alpha PNG matches design).

    // ============================================================
    // COLORS - DREAMER LIGHT THEME
    // ============================================================

    /** Background color for the section - Dreamer Light */
    public static final int DREAMER_LIGHT_BACKGROUND = Color.parseColor("#f1f5fb");

    /** Title text color - Dreamer Light */
    public static final int DREAMER_LIGHT_TITLE_TEXT = Color.parseColor("#ff2d3442");

    /** Info icon tint - Dreamer Light */
    public static final int DREAMER_LIGHT_INFO_ICON_TINT = Color.parseColor("#ff2d3442");

    // ============================================================
    // COLORS - DREAMER DARK THEME
    // ============================================================

    /** Background color for the section - Dreamer Dark */
    public static final int DREAMER_DARK_BACKGROUND = Color.parseColor("#23272f");

    /** Title gradient start color (left edge) - Dreamer Dark */
    public static final int DREAMER_DARK_TITLE_GRADIENT_START = Color.parseColor("#181b21");

    /** Title text color - Dreamer Dark */
    public static final int DREAMER_DARK_TITLE_TEXT = Color.parseColor("#ffffff");

    // Dreamer Dark: info icon is drawn as-is (no tint).

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
     * Returns the title gradient end color (right edge) for the specified theme.
     * For dark themes this is the section background; for light themes — white.
     */
    public static int getTitleGradientEnd(Theme theme) {
        switch (theme) {
            case FREE_LIGHT:
                return FREE_LIGHT_TITLE_GRADIENT_START;
            case DREAMER_LIGHT:
                return DREAMER_LIGHT_TITLE_GRADIENT_START;
            default:
                return getBackground(theme);
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
            case FREE_LIGHT:
                return FREE_LIGHT_TITLE_GRADIENT_START;
            case DREAMER_LIGHT:
                return DREAMER_LIGHT_TITLE_GRADIENT_START;
            default:
                return 0;
        }
    }

    /**
     * Returns whether the title gradient should be drawn for the specified theme.
     */
    public static boolean hasTitleGradient(Theme theme) {
        return true;
    }

    /**
     * Returns the tint color to apply to the info icon PNG for the specified theme,
     * or 0 (no tint) when the bitmap should be drawn as-is.
     *
     * <p>The PNG is white + alpha. Dark themes draw it without tint; light themes
     * apply a SRC_IN tint to recolor the white pixels while keeping alpha.
     */
    public static int getInfoIconTint(Theme theme) {
        switch (theme) {
            case FREE_LIGHT: return FREE_LIGHT_INFO_ICON_TINT;
            case DREAMER_LIGHT: return DREAMER_LIGHT_INFO_ICON_TINT;
            case FREE_DARK:
            case DREAMER_DARK:
            default:
                return 0;
        }
    }
}
