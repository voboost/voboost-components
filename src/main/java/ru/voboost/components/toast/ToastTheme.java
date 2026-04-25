package ru.voboost.components.toast;

import android.graphics.Color;

import ru.voboost.components.theme.Theme;

/**
 * Color scheme for the Toast component.
 */
class ToastColors {
    public final int background;
    public final int text;
    public final int closeIcon;

    public ToastColors(int background, int text, int closeIcon) {
        this.background = background;
        this.text = text;
        this.closeIcon = closeIcon;
    }
}

/**
 * Dimension constants for the Toast component.
 * All values are in pixels (automotive requirement).
 */
class ToastDimensions {
    // Toast size
    public static final int WIDTH = 1260;
    public static final int HEIGHT = 100;
    public static final int CORNER_RADIUS = 20;

    // Text
    public static final int TEXT_SIZE = 36;
    public static final int MAX_LINES = 2;

    // Close button
    public static final int CLOSE_BUTTON_SIZE = 50;
    public static final int CLOSE_BUTTON_MARGIN = 40;
    public static final int CLOSE_ICON_SIZE = 24;
    public static final int CLOSE_ICON_STROKE_WIDTH = 3;

    // Padding
    public static final int PADDING_TOP = 26;
    public static final int PADDING_BOTTOM = 25;
    public static final int PADDING_HORIZONTAL = 40;

    // Positioning in Screen
    public static final int TOP_OFFSET = 10;
}

/**
 * Animation constants for the Toast component.
 */
class ToastAnimation {
    // Animation durations
    public static final int SLIDE_DURATION = 400;
    public static final int FADE_DURATION = 100;
    public static final float DECELERATE_FACTOR = 3f;

    // Auto-dismiss durations
    public static final long DURATION_SHORT = 3000;
    public static final long DURATION_LONG = 5000;
}

/**
 * Predefined color schemes for all supported themes.
 */
class ToastColorSchemes {
    private static final java.util.Map<Theme, ToastColors> SCHEMES =
            new java.util.EnumMap<>(Theme.class);

    static {
        SCHEMES.put(Theme.FREE_DARK, new ToastColors(
                Color.parseColor("#23272f"),
                Color.parseColor("#cacaca"),
                Color.parseColor("#cacaca")));

        SCHEMES.put(Theme.FREE_LIGHT, new ToastColors(
                Color.parseColor("#f9faff"),
                Color.parseColor("#2d3543"),
                Color.parseColor("#2d3543")));

        SCHEMES.put(Theme.DREAMER_DARK, new ToastColors(
                Color.parseColor("#23272f"),
                Color.parseColor("#cacaca"),
                Color.parseColor("#cacaca")));

        SCHEMES.put(Theme.DREAMER_LIGHT, new ToastColors(
                Color.parseColor("#f9faff"),
                Color.parseColor("#2d3543"),
                Color.parseColor("#2d3543")));
    }

    public static ToastColors get(Theme theme) {
        return SCHEMES.getOrDefault(theme, SCHEMES.get(Theme.FREE_DARK));
    }
}

/**
 * Unified theme management for the Toast component.
 */
public class ToastTheme {
    public static ToastColors getColors(Theme theme) {
        return ToastColorSchemes.get(theme);
    }

    // Expose constants for external use
    public static final int WIDTH = ToastDimensions.WIDTH;
    public static final int HEIGHT = ToastDimensions.HEIGHT;
    public static final int TOP_OFFSET = ToastDimensions.TOP_OFFSET;
    public static final long DURATION_SHORT = ToastAnimation.DURATION_SHORT;
    public static final long DURATION_LONG = ToastAnimation.DURATION_LONG;
}
