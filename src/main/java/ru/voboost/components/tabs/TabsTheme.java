package ru.voboost.components.tabs;

import android.graphics.Color;

import ru.voboost.components.theme.Theme;

/**
 * Theme constants for the Tabs component.
 *
 * <p>Contains all visual styling parameters including:
 * <ul>
 *   <li>Dimensions (in pixels)</li>
 *   <li>Colors for each theme variant</li>
 *   <li>Animation parameters</li>
 * </ul>
 *
 * <p>All dimensions are in PIXELS, not dp. This is a critical automotive requirement.
 */
public final class TabsTheme {

    private TabsTheme() {
        // Prevent instantiation
    }

    // ============================================================
    // DIMENSIONS (all values in PIXELS)
    // ============================================================

    /** Width of the entire tabs sidebar (includes left padding + tab items + right padding) */
    public static final int SIDEBAR_WIDTH = 340;

    /** Height of a single tab item */
    public static final int TAB_ITEM_HEIGHT = 100;

    /** Width of a single tab item */
    public static final int TAB_ITEM_WIDTH = 268;

    /** Corner radius for the selection indicator */
    public static final int CORNER_RADIUS = 20;

    /** Left padding inside the sidebar (space before tab items) */
    public static final int SIDEBAR_PADDING_LEFT = 30;

    /** Right padding inside the sidebar (visual gap between tab items and content panel) */
    public static final int SIDEBAR_PADDING_RIGHT = 42;

    /** Text size for tab labels */
    public static final int TEXT_SIZE = 34;

    /** Baseline offset for vertical text centering */
    public static final float TEXT_BASELINE_OFFSET = -6f;

    /** Default top padding inside Tabs (visual offset of first item + overshoot buffer for selection slider) */
    public static final int DEFAULT_TOP_PADDING = 50;

    /** Default bottom padding inside Tabs (overshoot buffer for selection slider) */
    public static final int DEFAULT_BOTTOM_PADDING = 30;

    /** Animation duration in milliseconds */
    public static final int ANIMATION_DURATION = 400;

    /** Visual bounding box of the trailing more-indicator ">" (px, matches source PNG size). */
    public static final int MORE_SIZE = 30;

    /** Distance from tab-item right edge to more-indicator right edge (px). */
    public static final int MORE_MARGIN_END = 26;

    // ============================================================
    // COLORS - FREE LIGHT THEME
    // ============================================================

    /** Background color for the sidebar - Free Light */
    public static final int FREE_LIGHT_SIDEBAR_BACKGROUND = Color.TRANSPARENT;

    /** Background color for selected tab indicator - Free Light */
    public static final int FREE_LIGHT_SELECTED_BACKGROUND = Color.parseColor("#bfffffff");

    /** Text color for selected tab - Free Light */
    public static final int FREE_LIGHT_SELECTED_TEXT = Color.parseColor("#4099F3");

    /** Text color for unselected tab - Free Light */
    public static final int FREE_LIGHT_UNSELECTED_TEXT = Color.parseColor("#2d3442");

    /** Text color for disabled tab - Free Light */
    public static final int FREE_LIGHT_DISABLED_TEXT = Color.parseColor("#b32d3442");

    /** Text color for pressed tab - Free Light */
    public static final int FREE_LIGHT_PRESSED_TEXT = Color.parseColor("#802d3442");

    // ============================================================
    // COLORS - FREE DARK THEME
    // ============================================================

    /** Background color for the sidebar - Free Dark */
    public static final int FREE_DARK_SIDEBAR_BACKGROUND = Color.TRANSPARENT;

    /** Background color for selected tab indicator - Free Dark */
    public static final int FREE_DARK_SELECTED_BACKGROUND = Color.parseColor("#23272f");

    /** Text color for selected tab - Free Dark */
    public static final int FREE_DARK_SELECTED_TEXT = Color.parseColor("#4099F3");

    /** Text color for unselected tab - Free Dark */
    public static final int FREE_DARK_UNSELECTED_TEXT = Color.parseColor("#CACACA");

    /** Text color for disabled tab - Free Dark */
    public static final int FREE_DARK_DISABLED_TEXT = Color.parseColor("#666666");

    /** Text color for pressed tab - Free Dark */
    public static final int FREE_DARK_PRESSED_TEXT = Color.parseColor("#80cacaca");

    // ============================================================
    // COLORS - DREAMER LIGHT THEME
    // ============================================================

    /** Background color for the sidebar - Dreamer Light */
    public static final int DREAMER_LIGHT_SIDEBAR_BACKGROUND = Color.TRANSPARENT;

    /** Background color for selected tab indicator - Dreamer Light */
    public static final int DREAMER_LIGHT_SELECTED_BACKGROUND = Color.parseColor("#ffffff");

    /** Text color for selected tab - Dreamer Light */
    public static final int DREAMER_LIGHT_SELECTED_TEXT = Color.parseColor("#1a1a1a");

    /** Text color for unselected tab - Dreamer Light */
    public static final int DREAMER_LIGHT_UNSELECTED_TEXT = Color.parseColor("#2d3442");

    /** Text color for disabled tab - Dreamer Light */
    public static final int DREAMER_LIGHT_DISABLED_TEXT = Color.parseColor("#999999");

    /** Text color for pressed tab - Dreamer Light */
    public static final int DREAMER_LIGHT_PRESSED_TEXT = Color.parseColor("#80666666");

    // ============================================================
    // COLORS - DREAMER DARK THEME
    // ============================================================

    /** Background color for the sidebar - Dreamer Dark */
    public static final int DREAMER_DARK_SIDEBAR_BACKGROUND = Color.TRANSPARENT;

    /** Background color for selected tab indicator - Dreamer Dark */
    public static final int DREAMER_DARK_SELECTED_BACKGROUND = Color.parseColor("#2a2a2a");

    /** Text color for selected tab - Dreamer Dark */
    public static final int DREAMER_DARK_SELECTED_TEXT = Color.parseColor("#ffffff");

    /** Text color for unselected tab - Dreamer Dark */
    public static final int DREAMER_DARK_UNSELECTED_TEXT = Color.parseColor("#888888");

    /** Text color for disabled tab - Dreamer Dark */
    public static final int DREAMER_DARK_DISABLED_TEXT = Color.parseColor("#555555");

    /** Text color for pressed tab - Dreamer Dark */
    public static final int DREAMER_DARK_PRESSED_TEXT = Color.parseColor("#80888888");

    // ============================================================
    // COLOR GETTERS
    // ============================================================

    /**
     * Returns the sidebar background color for the specified theme.
     *
     * @param theme the current theme
     * @return the sidebar background color
     */
    public static int getSidebarBackground(Theme theme) {
        switch (theme) {
            case FREE_LIGHT:
                return FREE_LIGHT_SIDEBAR_BACKGROUND;
            case FREE_DARK:
                return FREE_DARK_SIDEBAR_BACKGROUND;
            case DREAMER_LIGHT:
                return DREAMER_LIGHT_SIDEBAR_BACKGROUND;
            case DREAMER_DARK:
                return DREAMER_DARK_SIDEBAR_BACKGROUND;
            default:
                return FREE_LIGHT_SIDEBAR_BACKGROUND;
        }
    }

    /**
     * Returns the selected tab background color for the specified theme.
     *
     * @param theme the current theme
     * @return the selected tab background color
     */
    public static int getSelectedBackground(Theme theme) {
        switch (theme) {
            case FREE_LIGHT:
                return FREE_LIGHT_SELECTED_BACKGROUND;
            case FREE_DARK:
                return FREE_DARK_SELECTED_BACKGROUND;
            case DREAMER_LIGHT:
                return DREAMER_LIGHT_SELECTED_BACKGROUND;
            case DREAMER_DARK:
                return DREAMER_DARK_SELECTED_BACKGROUND;
            default:
                return FREE_LIGHT_SELECTED_BACKGROUND;
        }
    }

    /**
     * Returns the selected tab text color for the specified theme.
     *
     * @param theme the current theme
     * @return the selected tab text color
     */
    public static int getSelectedTextColor(Theme theme) {
        switch (theme) {
            case FREE_LIGHT:
                return FREE_LIGHT_SELECTED_TEXT;
            case FREE_DARK:
                return FREE_DARK_SELECTED_TEXT;
            case DREAMER_LIGHT:
                return DREAMER_LIGHT_SELECTED_TEXT;
            case DREAMER_DARK:
                return DREAMER_DARK_SELECTED_TEXT;
            default:
                return FREE_LIGHT_SELECTED_TEXT;
        }
    }

    /**
     * Returns the unselected tab text color for the specified theme.
     *
     * @param theme the current theme
     * @return the unselected tab text color
     */
    public static int getUnselectedTextColor(Theme theme) {
        switch (theme) {
            case FREE_LIGHT:
                return FREE_LIGHT_UNSELECTED_TEXT;
            case FREE_DARK:
                return FREE_DARK_UNSELECTED_TEXT;
            case DREAMER_LIGHT:
                return DREAMER_LIGHT_UNSELECTED_TEXT;
            case DREAMER_DARK:
                return DREAMER_DARK_UNSELECTED_TEXT;
            default:
                return FREE_LIGHT_UNSELECTED_TEXT;
        }
    }

    /**
     * Returns the disabled tab text color for the specified theme.
     *
     * @param theme the current theme
     * @return the disabled tab text color
     */
    public static int getDisabledTextColor(Theme theme) {
        switch (theme) {
            case FREE_LIGHT:
                return FREE_LIGHT_DISABLED_TEXT;
            case FREE_DARK:
                return FREE_DARK_DISABLED_TEXT;
            case DREAMER_LIGHT:
                return DREAMER_LIGHT_DISABLED_TEXT;
            case DREAMER_DARK:
                return DREAMER_DARK_DISABLED_TEXT;
            default:
                return FREE_LIGHT_DISABLED_TEXT;
        }
    }

    /**
     * Returns the pressed tab text color for the specified theme.
     */
    public static int getPressedTextColor(Theme theme) {
        switch (theme) {
            case FREE_LIGHT:
                return FREE_LIGHT_PRESSED_TEXT;
            case FREE_DARK:
                return FREE_DARK_PRESSED_TEXT;
            case DREAMER_LIGHT:
                return DREAMER_LIGHT_PRESSED_TEXT;
            case DREAMER_DARK:
                return DREAMER_DARK_PRESSED_TEXT;
            default:
                return FREE_LIGHT_PRESSED_TEXT;
        }
    }

}
