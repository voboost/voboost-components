package ru.voboost.components.radio;

import android.graphics.Color;

import ru.voboost.components.theme.Theme;

/**
 * Color scheme for Radio component
 */
class RadioColors {
    public final int background;
    public final int selectedText;
    public final int unselectedText;
    public final int selectedGradientStart;
    public final int selectedGradientEnd;
    public final int selectedBorderTop;
    public final int selectedBorderSide;
    public final int selectedBorderBottom;

    public RadioColors(
            int background,
            int selectedText,
            int unselectedText,
            int selectedGradientStart,
            int selectedGradientEnd,
            int selectedBorderTop,
            int selectedBorderSide,
            int selectedBorderBottom) {
        this.background = background;
        this.selectedText = selectedText;
        this.unselectedText = unselectedText;
        this.selectedGradientStart = selectedGradientStart;
        this.selectedGradientEnd = selectedGradientEnd;
        this.selectedBorderTop = selectedBorderTop;
        this.selectedBorderSide = selectedBorderSide;
        this.selectedBorderBottom = selectedBorderBottom;
    }
}

/**
 * Dimension constants for the Radio component.
 * All values are in pixels (automotive requirement).
 */
class RadioDimensions {
    // Component dimensions (in pixels)
    public static final float HEIGHT_PX = 70f;
    public static final float CORNER_RADIUS_PX = 35f;
    public static final float BORDER_WIDTH_PX = 2f;

    // Text dimensions (in pixels)
    public static final float TEXT_SIZE_PX = 28f;

    // Item dimensions (in pixels)
    public static final float ITEM_PADDING_HORIZONTAL_PX = 26f;
    public static final float ITEM_MIN_WIDTH_PX = 120f;

    // Animation padding (in pixels) - increased for OVERSHOOT_TENSION
    public static final float ANIMATION_PADDING_PX = 35f;

    // Animation constants
    public static final int ANIMATION_DURATION = 400;
    public static final int VALUE_CHANGE_DELAY = 300;
    public static final float OVERSHOOT_TENSION = 1.0f;

}

/**
 * Predefined color schemes for all supported themes.
 */
class RadioColorSchemes {
    private static final java.util.Map<Theme, RadioColors> SCHEMES = new java.util.EnumMap<>(Theme.class);

    static {
        SCHEMES.put(
                Theme.FREE_LIGHT,
                new RadioColors(
                        Color.parseColor("#ffffff"), // background
                        Color.parseColor("#ffffff"), // selectedText
                        Color.parseColor("#2d3442"), // unselectedText
                        Color.parseColor("#79bbfd"), // selectedGradientStart
                        Color.parseColor("#2781dd"), // selectedGradientEnd
                        Color.parseColor("#8dc6ff"), // selectedBorderTop
                        Color.parseColor("#519ae5"), // selectedBorderSide
                        Color.parseColor("#1875d2") // selectedBorderBottom
                ));
        SCHEMES.put(
                Theme.FREE_DARK,
                new RadioColors(
                        Color.parseColor("#373f4a"), // background
                        Color.parseColor("#ffffff"), // selectedText
                        Color.parseColor("#cacaca"), // unselectedText
                        Color.parseColor("#79bbfd"), // selectedGradientStart
                        Color.parseColor("#2781dd"), // selectedGradientEnd
                        Color.parseColor("#8dc6ff"), // selectedBorderTop
                        Color.parseColor("#519ae5"), // selectedBorderSide
                        Color.parseColor("#1875d2") // selectedBorderBottom
                ));
        SCHEMES.put(
                Theme.DREAMER_LIGHT,
                new RadioColors(
                        Color.parseColor("#ffffff"), // background
                        Color.parseColor("#2f2e36"), // selectedText
                        Color.parseColor("#2d3442"), // unselectedText
                        Color.parseColor("#eadac8"), // selectedGradientStart
                        Color.parseColor("#9c8069"), // selectedGradientEnd
                        Color.parseColor("#eadac8"), // selectedBorderTop
                        Color.parseColor("#9c8069"), // selectedBorderSide
                        Color.parseColor("#9c8069") // selectedBorderBottom
                ));
        SCHEMES.put(
                Theme.DREAMER_DARK,
                new RadioColors(
                        Color.parseColor("#40444a"), // background
                        Color.parseColor("#2f2e36"), // selectedText
                        Color.parseColor("#80ffffff"), // unselectedText
                        Color.parseColor("#eadac8"), // selectedGradientStart
                        Color.parseColor("#9c8069"), // selectedGradientEnd
                        Color.parseColor("#eadac8"), // selectedBorderTop
                        Color.parseColor("#9c8069"), // selectedBorderSide
                        Color.parseColor("#9c8069") // selectedBorderBottom
                ));
    }

    public static RadioColors get(Theme theme) {
        return SCHEMES.getOrDefault(theme, SCHEMES.get(Theme.FREE_LIGHT));
    }

}

/**
 * Unified theme management for the Radio component.
 */
public class RadioTheme {
    /**
     * Returns the color scheme for the given theme.
     *
     * @param theme theme enum value
     * @return corresponding color scheme
     */
    public static RadioColors getColors(Theme theme) {
        return RadioColorSchemes.get(theme);
    }

    /**
     * Returns text color scheme for the given theme.
     */
    public static RadioTextColors getTextColors(Theme theme) {
        return RadioTextColorSchemes.get(theme);
    }

    /**
     * Returns dimension constants.
     *
     * @return RadioDimensions instance
     */
    public static RadioDimensions getDimensions() {
        return new RadioDimensions();
    }
}

/**
 * Text colors for Radio container (title and descriptions).
 */
class RadioTextColors {
    public final int titleColor;
    public final int descriptionColor;

    public RadioTextColors(int titleColor, int descriptionColor) {
        this.titleColor = titleColor;
        this.descriptionColor = descriptionColor;
    }
}

/**
 * Dimension constants for Radio container text elements.
 */
class RadioTextDimensions {
    /** Container left padding */
    public static final int CONTAINER_PADDING_LEFT_PX = 2;
    /** Title text size in px — matches setting_text_size_32 */
    public static final float TITLE_TEXT_SIZE_PX = 32f;
    /** Description text size in px — matches setting_text_size_24 */
    public static final float DESCRIPTION_TEXT_SIZE_PX = 24f;
    /** Gap between title and description above */
    public static final int TITLE_TO_DESCRIPTION_GAP_PX = 37;
    /** Gap between description above and RadioPrimitive */
    public static final int DESCRIPTION_ABOVE_TO_PRIMITIVE_GAP_PX = 47;
    /** Gap between RadioPrimitive and description below */
    public static final int PRIMITIVE_TO_DESCRIPTION_GAP_PX = 20;
    /** Gap after description below (bottom margin of the component) */
    public static final int DESCRIPTION_BELOW_BOTTOM_GAP_PX = 47;
    /** Container bottom padding */
    public static final int CONTAINER_PADDING_BOTTOM_PX = 42;
}

/**
 * Text color schemes per theme.
 */
class RadioTextColorSchemes {
    private static final java.util.Map<Theme, RadioTextColors> SCHEMES = new java.util.EnumMap<>(Theme.class);

    static {
        SCHEMES.put(Theme.FREE_DARK, new RadioTextColors(
                Color.parseColor("#ffffff"),
                Color.parseColor("#919397")));
        SCHEMES.put(Theme.DREAMER_DARK, new RadioTextColors(
                Color.parseColor("#ffffff"),
                Color.parseColor("#ffcacaca")));
        SCHEMES.put(Theme.FREE_LIGHT, new RadioTextColors(
                Color.parseColor("#ff2d3442"),
                Color.parseColor("#802d3442")));
        SCHEMES.put(Theme.DREAMER_LIGHT, new RadioTextColors(
                Color.parseColor("#1a1e28"),
                Color.parseColor("#cc1a1e28")));
    }

    public static RadioTextColors get(Theme theme) {
        return SCHEMES.getOrDefault(theme, SCHEMES.get(Theme.FREE_DARK));
    }
}
