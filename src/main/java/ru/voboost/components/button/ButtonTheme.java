package ru.voboost.components.button;

import android.graphics.Color;

import ru.voboost.components.theme.Theme;

/**
 * Color scheme for a single Button style within a theme.
 */
class ButtonColors {
    public final int backgroundNormal;
    public final int backgroundPressed;
    public final int backgroundDisabled;
    public final int text;
    public final int textDisabled;

    public ButtonColors(
            int backgroundNormal,
            int backgroundPressed,
            int backgroundDisabled,
            int text,
            int textDisabled) {
        this.backgroundNormal = backgroundNormal;
        this.backgroundPressed = backgroundPressed;
        this.backgroundDisabled = backgroundDisabled;
        this.text = text;
        this.textDisabled = textDisabled;
    }
}

/**
 * Dimension constants for the Button component.
 * All values are in pixels (automotive requirement).
 */
class ButtonDimensions {
    public static final float HEIGHT_PX = 70f;
    public static final float CORNER_RADIUS_PX = 35f;
    public static final float TEXT_SIZE_PX = 28f;
    public static final float PADDING_HORIZONTAL_PX = 38f;
}

/**
 * Dimension constants for Button container padding.
 * All values are in pixels (automotive requirement).
 */
class ButtonContainerDimensions {
    /** Container left padding */
    public static final int CONTAINER_PADDING_LEFT_PX = 0;
    /** Container bottom padding */
    public static final int CONTAINER_PADDING_BOTTOM_PX = 42;
}

/**
 * Predefined color schemes for all supported themes and styles.
 */
class ButtonColorSchemes {
    private static final java.util.Map<String, ButtonColors> SCHEMES = new java.util.HashMap<>();

    static {
        // FREE_DARK + PRIMARY
        SCHEMES.put(key(Theme.FREE_DARK, ButtonStyle.PRIMARY), new ButtonColors(
                Color.parseColor("#4099f3"),
                Color.parseColor("#3080e0"),
                Color.parseColor("#404099f3"),
                Color.parseColor("#ffffff"),
                Color.parseColor("#80ffffff")));

        // FREE_DARK + SECONDARY
        SCHEMES.put(key(Theme.FREE_DARK, ButtonStyle.SECONDARY), new ButtonColors(
                Color.parseColor("#373f4a"),
                Color.parseColor("#80373f4a"),
                Color.parseColor("#2d333d"),
                Color.parseColor("#ffffff"),
                Color.parseColor("#919397")));

        // FREE_LIGHT + PRIMARY
        SCHEMES.put(key(Theme.FREE_LIGHT, ButtonStyle.PRIMARY), new ButtonColors(
                Color.parseColor("#4099f3"),
                Color.parseColor("#3080e0"),
                Color.parseColor("#404099f3"),
                Color.parseColor("#ffffff"),
                Color.parseColor("#80ffffff")));

        // FREE_LIGHT + SECONDARY
        SCHEMES.put(key(Theme.FREE_LIGHT, ButtonStyle.SECONDARY), new ButtonColors(
                Color.parseColor("#ffffff"),
                Color.parseColor("#f8fafd"),
                Color.parseColor("#f5f8fc"),
                Color.parseColor("#2d3442"),
                Color.parseColor("#b6bcc4")));

        // DREAMER_DARK + PRIMARY
        SCHEMES.put(key(Theme.DREAMER_DARK, ButtonStyle.PRIMARY), new ButtonColors(
                Color.parseColor("#9c8069"),
                Color.parseColor("#7a6050"),
                Color.parseColor("#409c8069"),
                Color.parseColor("#ffffff"),
                Color.parseColor("#80ffffff")));

        // DREAMER_DARK + SECONDARY
        SCHEMES.put(key(Theme.DREAMER_DARK, ButtonStyle.SECONDARY), new ButtonColors(
                Color.parseColor("#40444a"),
                Color.parseColor("#9c8069"),
                Color.parseColor("#4040444a"),
                Color.parseColor("#ffffff"),
                Color.parseColor("#80ffffff")));

        // DREAMER_LIGHT + PRIMARY
        SCHEMES.put(key(Theme.DREAMER_LIGHT, ButtonStyle.PRIMARY), new ButtonColors(
                Color.parseColor("#9c8069"),
                Color.parseColor("#7a6050"),
                Color.parseColor("#409c8069"),
                Color.parseColor("#ffffff"),
                Color.parseColor("#80ffffff")));

        // DREAMER_LIGHT + SECONDARY
        SCHEMES.put(key(Theme.DREAMER_LIGHT, ButtonStyle.SECONDARY), new ButtonColors(
                Color.parseColor("#ffffff"),
                Color.parseColor("#f8fafd"),
                Color.parseColor("#f5f8fc"),
                Color.parseColor("#2d3442"),
                Color.parseColor("#b6bcc4")));
    }

    private static String key(Theme theme, ButtonStyle style) {
        return theme.name() + "_" + style.name();
    }

    public static ButtonColors get(Theme theme, ButtonStyle style) {
        ButtonColors colors = SCHEMES.get(key(theme, style));
        if (colors != null) {
            return colors;
        }
        return SCHEMES.get(key(Theme.FREE_DARK, ButtonStyle.PRIMARY));
    }
}

/**
 * Unified theme management for the Button component.
 */
public class ButtonTheme {
    /**
     * Returns the color scheme for the given theme and style.
     */
    public static ButtonColors getColors(Theme theme, ButtonStyle style) {
        return ButtonColorSchemes.get(theme, style);
    }

    /**
     * Returns text color scheme for descriptions.
     */
    public static ButtonDescriptionColors getDescriptionColors(Theme theme) {
        return ButtonDescriptionColorSchemes.get(theme);
    }
}

/**
 * Text colors for Button description text.
 */
class ButtonDescriptionColors {
    public final int textColor;

    public ButtonDescriptionColors(int textColor) {
        this.textColor = textColor;
    }
}

/**
 * Dimension constants for Button description text.
 */
class ButtonDescriptionDimensions {
    /** Description text size — setting_text_size_24 */
    public static final float TEXT_SIZE_PX = 24f;
    /** Gap between ButtonPrimitive and description text */
    public static final int BUTTON_TO_TEXT_GAP_PX = 20;
}

/**
 * Description color schemes per theme.
 */
class ButtonDescriptionColorSchemes {
    private static final java.util.Map<Theme, ButtonDescriptionColors> SCHEMES =
            new java.util.EnumMap<>(Theme.class);

    static {
        SCHEMES.put(Theme.FREE_DARK, new ButtonDescriptionColors(Color.parseColor("#919397")));
        SCHEMES.put(Theme.DREAMER_DARK, new ButtonDescriptionColors(Color.parseColor("#ccffffff")));
        SCHEMES.put(Theme.FREE_LIGHT, new ButtonDescriptionColors(Color.parseColor("#802d3442")));
        SCHEMES.put(Theme.DREAMER_LIGHT, new ButtonDescriptionColors(Color.parseColor("#cc1a1e28")));
    }

    public static ButtonDescriptionColors get(Theme theme) {
        return SCHEMES.getOrDefault(theme, SCHEMES.get(Theme.FREE_DARK));
    }
}
