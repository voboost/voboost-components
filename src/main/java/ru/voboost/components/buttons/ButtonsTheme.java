package ru.voboost.components.buttons;

import android.graphics.Color;

import ru.voboost.components.theme.Theme;

class ButtonsColors {
    public final int textColor;

    public ButtonsColors(int textColor) {
        this.textColor = textColor;
    }
}

class ButtonsDimensions {
    public static final float TEXT_SIZE_PX = 24f;
    /** Gap between buttons */
    public static final int BUTTON_GAP_PX = 10;
    /** Buttons row to right text gap */
    public static final int BUTTONS_TO_TEXT_GAP_PX = 20;
    /** Buttons to description below gap */
    public static final int BUTTONS_TO_DESCRIPTION_GAP_PX = 14;
}

class ButtonsColorSchemes {
    private static final java.util.Map<Theme, ButtonsColors> SCHEMES =
            new java.util.EnumMap<>(Theme.class);

    static {
        SCHEMES.put(Theme.FREE_DARK, new ButtonsColors(Color.parseColor("#ccffffff")));
        SCHEMES.put(Theme.DREAMER_DARK, new ButtonsColors(Color.parseColor("#ccffffff")));
        SCHEMES.put(Theme.FREE_LIGHT, new ButtonsColors(Color.parseColor("#cc1a1e28")));
        SCHEMES.put(Theme.DREAMER_LIGHT, new ButtonsColors(Color.parseColor("#cc1a1e28")));
    }

    public static ButtonsColors get(Theme theme) {
        return SCHEMES.getOrDefault(theme, SCHEMES.get(Theme.FREE_DARK));
    }
}

public class ButtonsTheme {
    public static ButtonsColors getColors(Theme theme) {
        return ButtonsColorSchemes.get(theme);
    }
}
