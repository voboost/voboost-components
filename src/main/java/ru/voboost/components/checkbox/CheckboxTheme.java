package ru.voboost.components.checkbox;

import android.graphics.Color;

import ru.voboost.components.theme.Theme;

/**
 * Color scheme for the Checkbox component.
 */
class CheckboxColors {
    public final int trackOff;
    public final int trackOnStart;
    public final int trackOnEnd;
    public final float disabledAlpha;

    public CheckboxColors(
            int trackOff,
            int trackOnStart,
            int trackOnEnd,
            float disabledAlpha) {
        this.trackOff = trackOff;
        this.trackOnStart = trackOnStart;
        this.trackOnEnd = trackOnEnd;
        this.disabledAlpha = disabledAlpha;
    }
}

/**
 * Dimension constants for the Checkbox component.
 * All values are in pixels (automotive requirement).
 */
class CheckboxDimensions {
    public static final float TRACK_WIDTH_PX = 83f;
    public static final float TRACK_HEIGHT_PX = 42f;
    public static final float CORNER_RADIUS_PX = 21f;
    public static final float KNOB_SIZE_PX = 34f;
    public static final float KNOB_PADDING_PX = 5f;
    public static final float MASK_HEIGHT_PX = 40f;

    public static final int ANIMATION_DURATION = 400;
    public static final float OVERSHOOT_TENSION = 1.0f;
}

/**
 * Predefined color schemes for all supported themes.
 */
class CheckboxColorSchemes {
    private static final java.util.Map<Theme, CheckboxColors> SCHEMES = new java.util.EnumMap<>(Theme.class);

    static {
        SCHEMES.put(Theme.FREE_DARK, new CheckboxColors(
                Color.parseColor("#24808795"),
                Color.parseColor("#004099f3"),
                Color.parseColor("#ff4099f3"),
                0.3f));

        SCHEMES.put(Theme.FREE_LIGHT, new CheckboxColors(
                Color.parseColor("#ffd5deec"),
                Color.parseColor("#004099f3"),
                Color.parseColor("#ff4099f3"),
                0.3f));

        SCHEMES.put(Theme.DREAMER_DARK, new CheckboxColors(
                Color.parseColor("#24808795"),
                Color.parseColor("#00f9d4b1"),
                Color.parseColor("#fff9d4b1"),
                0.3f));

        SCHEMES.put(Theme.DREAMER_LIGHT, new CheckboxColors(
                Color.parseColor("#ffd5deec"),
                Color.parseColor("#00f9d4b1"),
                Color.parseColor("#fff9d4b1"),
                0.3f));
    }

    public static CheckboxColors get(Theme theme) {
        return SCHEMES.getOrDefault(theme, SCHEMES.get(Theme.FREE_DARK));
    }
}

/**
 * Unified theme management for the Checkbox component.
 */
public class CheckboxTheme {
    public static CheckboxColors getColors(Theme theme) {
        return CheckboxColorSchemes.get(theme);
    }

    public static CheckboxTextColors getTextColors(Theme theme) {
        return CheckboxTextColorSchemes.get(theme);
    }
}

/**
 * Text colors for Checkbox container (label and description).
 */
class CheckboxTextColors {
    public final int labelColor;
    public final int descriptionColor;

    public CheckboxTextColors(int labelColor, int descriptionColor) {
        this.labelColor = labelColor;
        this.descriptionColor = descriptionColor;
    }
}

/**
 * Dimension constants for Checkbox container text elements.
 */
class CheckboxTextDimensions {
    /** Container left padding */
    public static final int CONTAINER_PADDING_LEFT_PX = 1;
    public static final float LABEL_TEXT_SIZE_PX = 30f;
    public static final float DESCRIPTION_TEXT_SIZE_PX = 24f;
    public static final int CHECKBOX_MARGIN_TOP_PX = 0;
    public static final int CHECKBOX_MARGIN_START_PX = 0;
    public static final int CHECKBOX_TO_LABEL_GAP_PX = 31;
    public static final int LABEL_TO_DESCRIPTION_GAP_PX = 9;
    public static final int DESCRIPTION_BOTTOM_COMPENSATION_PX = -7;
    public static final int CONTAINER_PADDING_TOP_PX = 3;
    public static final int CONTAINER_PADDING_BOTTOM_PX = 42;
    public static final int DESCRIPTION_MARGIN_END_PX = 30;
    public static final int LABEL_MARGIN_END_PX = 10;
}

/**
 * Text color schemes per theme.
 */
class CheckboxTextColorSchemes {
    private static final java.util.Map<Theme, CheckboxTextColors> SCHEMES = new java.util.EnumMap<>(Theme.class);

    static {
        SCHEMES.put(Theme.FREE_DARK, new CheckboxTextColors(
                Color.parseColor("#ffffff"), Color.parseColor("#80ffffff")));
        SCHEMES.put(Theme.DREAMER_DARK, new CheckboxTextColors(
                Color.parseColor("#ffffff"), Color.parseColor("#80ffffff")));
        SCHEMES.put(Theme.FREE_LIGHT, new CheckboxTextColors(
                Color.parseColor("#1a1e28"), Color.parseColor("#801a1e28")));
        SCHEMES.put(Theme.DREAMER_LIGHT, new CheckboxTextColors(
                Color.parseColor("#1a1e28"), Color.parseColor("#801a1e28")));
    }

    public static CheckboxTextColors get(Theme theme) {
        return SCHEMES.getOrDefault(theme, SCHEMES.get(Theme.FREE_DARK));
    }
}
