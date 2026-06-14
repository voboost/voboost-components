package ru.voboost.components.select;

import android.graphics.Color;

import ru.voboost.components.theme.Theme;

/**
 * Color scheme for the Select component.
 */
class SelectColors {
    // Trigger button colors
    public final int triggerBackground;
    public final int triggerText;
    public final int triggerArrow;

    // WheelView colors
    public final int wheelText;
    public final int wheelSelectedText;
    public final int wheelIndicator;
    public final int wheelCurtain;

    public SelectColors(
            int triggerBackground, int triggerText, int triggerArrow,
            int wheelText, int wheelSelectedText, int wheelIndicator, int wheelCurtain) {
        this.triggerBackground = triggerBackground;
        this.triggerText = triggerText;
        this.triggerArrow = triggerArrow;
        this.wheelText = wheelText;
        this.wheelSelectedText = wheelSelectedText;
        this.wheelIndicator = wheelIndicator;
        this.wheelCurtain = wheelCurtain;
    }
}

/**
 * Dimension constants for the Select component.
 * All values are in pixels (automotive requirement).
 */
class SelectDimensions {
    // Trigger button
    public static final float TRIGGER_HEIGHT_PX = 70f;
    public static final float TRIGGER_CORNER_RADIUS_PX = 35f;
    public static final float TRIGGER_TEXT_SIZE_PX = 28f;
    public static final float TRIGGER_PADDING_HORIZONTAL_PX = 26f;
    public static final float TRIGGER_MIN_WIDTH_PX = 200f;
    public static final float ARROW_SIZE_PX = 60f;
    public static final float ARROW_MARGIN_PX = 10f;

    // WheelView
    public static final int WHEEL_VISIBLE_ITEMS = 3;
    public static final float WHEEL_TEXT_SIZE_PX = 48f;
    public static final float WHEEL_SELECTED_TEXT_SIZE_PX = 56f;
    public static final float WHEEL_ITEM_SPACE_PX = 48f;
    public static final float WHEEL_HEIGHT_PX = 296f;
    public static final float POPUP_BUTTON_HEIGHT_PX = 66f;
    public static final float POPUP_BUTTON_GAP_PX = 40f;
    public static final float POPUP_BUTTON_TOP_MARGIN_PX = 30f;
    public static final int WHEEL_CURVED_MAX_ANGLE = 90;
    public static final float WHEEL_INDICATOR_SIZE_PX = 2f;
    public static final float WHEEL_CURTAIN_RADIUS_PX = 12f;
}

/**
 * Predefined color schemes for all supported themes.
 */
class SelectColorSchemes {
    private static final java.util.Map<Theme, SelectColors> SCHEMES =
            new java.util.EnumMap<>(Theme.class);

    static {
        SCHEMES.put(Theme.FREE_DARK, new SelectColors(
                Color.parseColor("#373f4a"),  // triggerBackground
                Color.parseColor("#ffffff"),  // triggerText
                Color.parseColor("#80ffffff"),// triggerChevron
                Color.parseColor("#80cacaca"),// wheelText
                Color.parseColor("#ffffff"),  // wheelSelectedText
                Color.parseColor("#40ffffff"),// wheelIndicator
                Color.parseColor("#0187f0")   // wheelCurtain
        ));

        SCHEMES.put(Theme.FREE_LIGHT, new SelectColors(
                Color.parseColor("#e8ecf0"),
                Color.parseColor("#2d3442"),
                Color.parseColor("#802d3442"),
                Color.parseColor("#802d3442"),
                Color.parseColor("#2d3442"),
                Color.parseColor("#402d3442"),
                Color.parseColor("#0187f0")
        ));

        SCHEMES.put(Theme.DREAMER_DARK, new SelectColors(
                Color.parseColor("#40444a"),
                Color.parseColor("#ffffff"),
                Color.parseColor("#80ffffff"),
                Color.parseColor("#80cacaca"),
                Color.parseColor("#eadac8"),
                Color.parseColor("#40eadac8"),
                Color.parseColor("#e9d2b4")
        ));

        SCHEMES.put(Theme.DREAMER_LIGHT, new SelectColors(
                Color.parseColor("#e8ecf0"),
                Color.parseColor("#2d3442"),
                Color.parseColor("#802d3442"),
                Color.parseColor("#802d3442"),
                Color.parseColor("#9c8069"),
                Color.parseColor("#409c8069"),
                Color.parseColor("#e9d2b4")
        ));
    }

    public static SelectColors get(Theme theme) {
        return SCHEMES.getOrDefault(theme, SCHEMES.get(Theme.FREE_DARK));
    }
}

/**
 * Unified theme management for the Select component.
 */
public class SelectTheme {
    public static SelectColors getColors(Theme theme) {
        return SelectColorSchemes.get(theme);
    }
}
