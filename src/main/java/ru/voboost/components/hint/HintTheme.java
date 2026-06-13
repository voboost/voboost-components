package ru.voboost.components.hint;

import android.graphics.Color;

import ru.voboost.components.theme.Theme;

/**
 * Theme constants for the Hint component.
 *
 * <p>Description/hint text uses muted color (50% alpha of primary text)
 * at 24px, matching the description style of Checkbox and Radio.
 */
final class HintTheme {

    private HintTheme() {
        // Prevent instantiation
    }

    /** Text size in pixels - matches description text in other components. */
    public static final float TEXT_SIZE_PX = 24f;

    /** Top padding in pixels. */
    public static final int PADDING_TOP_PX = 8;

    /** Bottom padding in pixels. */
    public static final int PADDING_BOTTOM_PX = 8;

    /**
     * Returns hint text color for the given theme.
     *
     * @param theme the current theme
     * @return ARGB color for hint text
     */
    public static int getTextColor(Theme theme) {
        if (theme == null) {
            return Color.parseColor("#801a1e28");
        }
        String value = theme.getValue();
        if (value != null && value.endsWith("-dark")) {
            return Color.parseColor("#80ffffff");
        }
        return Color.parseColor("#801a1e28");
    }
}
