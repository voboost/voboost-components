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
 * Dimension constants for Radio component
 * Java equivalent of the Kotlin RadioDimensions object
 * Note: ALL values are in pixels as required by .roorules for automotive precision
 */
class RadioDimensions {
    // Component dimensions (in pixels)
    public static final float HEIGHT_PX = 70f;
    public static final float CORNER_RADIUS_PX = 34f;
    public static final float BORDER_WIDTH_PX = 2f;

    // Text dimensions (in pixels)
    public static final float TEXT_SIZE_PX = 24f;

    // Item dimensions (in pixels)
    public static final float ITEM_PADDING_HORIZONTAL_PX = 32f;
    public static final float ITEM_MIN_WIDTH_PX = 120f;

    // Animation padding (in pixels) - increased for OVERSHOOT_TENSION
    public static final float ANIMATION_PADDING_PX = 35f;

    // Animation constants
    public static final int ANIMATION_DURATION = 400;
    public static final float OVERSHOOT_TENSION = 1.0f;

    // Spring animation constants (dimensionless)
    public static final float ANIMATION_DAMPING_RATIO = 0.7f;
    public static final float ANIMATION_STIFFNESS = 850f;
    public static final float ANIMATION_VISIBILITY_THRESHOLD = 0.4f;
}

/**
 * Predefined color schemes for all supported themes
 * Java equivalent of the Kotlin RadioColorSchemes object
 */
class RadioColorSchemes {
    private static final java.util.Map<Theme, RadioColors> SCHEMES = new java.util.EnumMap<>(Theme.class);

    static {
        SCHEMES.put(Theme.FREE_LIGHT, new RadioColors(
            Color.parseColor("#ffffff"), // background
            Color.parseColor("#ffffff"), // selectedText
            Color.parseColor("#2d3442"), // unselectedText
            Color.parseColor("#55a2ef"), // selectedGradientStart
            Color.parseColor("#2681dd"), // selectedGradientEnd
            Color.parseColor("#8dc6ff"), // selectedBorderTop
            Color.parseColor("#519ae5"), // selectedBorderSide
            Color.parseColor("#1875d2") // selectedBorderBottom
        ));
        SCHEMES.put(Theme.FREE_DARK, new RadioColors(
            Color.parseColor("#373f4a"), // background
            Color.parseColor("#ffffff"), // selectedText
            Color.parseColor("#cacaca"), // unselectedText
            Color.parseColor("#55a2ef"), // selectedGradientStart
            Color.parseColor("#2681dd"), // selectedGradientEnd
            Color.parseColor("#8dc6ff"), // selectedBorderTop
            Color.parseColor("#519ae5"), // selectedBorderSide
            Color.parseColor("#1875d2") // selectedBorderBottom
        ));
        SCHEMES.put(Theme.DREAMER_LIGHT, new RadioColors(
            Color.parseColor("#ffffff"), // background
            Color.parseColor("#2f2e36"), // selectedText
            Color.parseColor("#2d3442"), // unselectedText
            Color.parseColor("#eadac8"), // selectedGradientStart
            Color.parseColor("#9c8069"), // selectedGradientEnd
            Color.parseColor("#eadac8"), // selectedBorderTop
            Color.parseColor("#9c8069"), // selectedBorderSide
            Color.parseColor("#9c8069") // selectedBorderBottom
        ));
        SCHEMES.put(Theme.DREAMER_DARK, new RadioColors(
            Color.parseColor("#40444a"), // background
            Color.parseColor("#2f2e36"), // selectedText
            Color.parseColor("#cacaca"), // unselectedText
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

    // Keep backward compatibility
    public static RadioColors get(String themeValue) {
        return get(Theme.fromValue(themeValue));
    }
}

/**
 * Unified theme management for Radio component
 * Java equivalent of the Kotlin RadioTheme object
 */
public class RadioTheme {
    /**
     * Get color scheme by theme enum
     * @param theme Theme enum value
     * @return Corresponding color scheme
     */
    public static RadioColors getColors(Theme theme) {
        return RadioColorSchemes.get(theme);
    }

    /**
     * Get color scheme by theme name (backward compatibility)
     * @param theme Theme name: "free-light", "free-dark", "dreamer-light", "dreamer-dark"
     * @return Corresponding color scheme
     */
    public static RadioColors getColors(String theme) {
        return getColors(Theme.fromValue(theme));
    }

    /**
     * Get dimensions for Radio component
     * @return RadioDimensions class with all size constants
     */
    public static RadioDimensions getDimensions() {
        return new RadioDimensions();
    }
}
