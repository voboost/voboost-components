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
                        Color.parseColor("#CACACA"), // unselectedText
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
                        Color.parseColor("#80ffffff"), // unselectedText - 50% white opacity
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
     * Returns dimension constants.
     *
     * @return RadioDimensions instance
     */
    public static RadioDimensions getDimensions() {
        return new RadioDimensions();
    }
}
