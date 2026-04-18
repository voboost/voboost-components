package ru.voboost.components.dialog;

import android.graphics.Color;

import ru.voboost.components.theme.Theme;

/**
 * Color scheme for the Dialog component.
 */
class DialogColors {
    public final int titleText;
    public final int messageText;

    public DialogColors(int titleText, int messageText) {
        this.titleText = titleText;
        this.messageText = messageText;
    }
}

/**
 * Dimension constants for the Dialog component.
 * All values are in pixels (automotive requirement).
 */
class DialogDimensions {
    public static final float TITLE_TEXT_SIZE_PX = 32f;
    public static final float MESSAGE_TEXT_SIZE_PX = 28f;
    public static final float BUTTON_HEIGHT_PX = 66f;
    public static final float BUTTON_GAP_PX = 40f;
    public static final float TITLE_BOTTOM_MARGIN_PX = 20f;
    public static final float MESSAGE_BOTTOM_MARGIN_PX = 40f;
}

/**
 * Predefined color schemes for all supported themes.
 */
class DialogColorSchemes {
    private static final java.util.Map<Theme, DialogColors> SCHEMES =
            new java.util.EnumMap<>(Theme.class);

    static {
        SCHEMES.put(Theme.FREE_DARK, new DialogColors(
                Color.parseColor("#ffffff"),
                Color.parseColor("#80ffffff")));

        SCHEMES.put(Theme.FREE_LIGHT, new DialogColors(
                Color.parseColor("#2d3442"),
                Color.parseColor("#802d3442")));

        SCHEMES.put(Theme.DREAMER_DARK, new DialogColors(
                Color.parseColor("#ffffff"),
                Color.parseColor("#80ffffff")));

        SCHEMES.put(Theme.DREAMER_LIGHT, new DialogColors(
                Color.parseColor("#2d3442"),
                Color.parseColor("#802d3442")));
    }

    public static DialogColors get(Theme theme) {
        return SCHEMES.getOrDefault(theme, SCHEMES.get(Theme.FREE_DARK));
    }
}

/**
 * Unified theme management for the Dialog component.
 */
public class DialogTheme {
    public static DialogColors getColors(Theme theme) {
        return DialogColorSchemes.get(theme);
    }
}
