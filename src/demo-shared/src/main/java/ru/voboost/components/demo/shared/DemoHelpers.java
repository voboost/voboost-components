package ru.voboost.components.demo.shared;

import android.graphics.Color;

/**
 * Utility methods shared by demo applications.
 */
public class DemoHelpers {

    private DemoHelpers() {
        // Prevent instantiation
    }

    /**
     * Returns the activity background color for a combined theme string.
     *
     * @param combinedTheme combined theme (e.g. "free-dark", "dreamer-light")
     * @return ARGB color: black for dark themes, light blue-gray otherwise
     */
    public static int getBackgroundColor(String combinedTheme) {
        if (combinedTheme != null && combinedTheme.endsWith("-dark")) {
            return Color.parseColor("#000000");
        }
        return Color.parseColor("#f1f5fb");
    }
}
