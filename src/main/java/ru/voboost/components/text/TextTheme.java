package ru.voboost.components.text;

import android.graphics.Color;

/**
 * Theme constants and color management for the Text component.
 * Supports all four theme variants: free-light, free-dark, dreamer-light, dreamer-dark.
 */
public class TextTheme {

    // Theme identifiers
    public static final String FREE_LIGHT = "free-light";
    public static final String FREE_DARK = "free-dark";
    public static final String DREAMER_LIGHT = "dreamer-light";
    public static final String DREAMER_DARK = "dreamer-dark";

    // Free Light Theme Colors
    private static final int FREE_LIGHT_CONTROL = Color.parseColor("#1a1a1a");
    private static final int FREE_LIGHT_TITLE = Color.parseColor("#000000");

    // Free Dark Theme Colors
    private static final int FREE_DARK_CONTROL = Color.parseColor("#ffffff");
    private static final int FREE_DARK_TITLE = Color.parseColor("#f1f5fb");

    // Dreamer Light Theme Colors
    private static final int DREAMER_LIGHT_CONTROL = Color.parseColor("#1a1a1a");
    private static final int DREAMER_LIGHT_TITLE = Color.parseColor("#000000");

    // Dreamer Dark Theme Colors
    private static final int DREAMER_DARK_CONTROL = Color.parseColor("#ffffff");
    private static final int DREAMER_DARK_TITLE = Color.parseColor("#f1f5fb");

    /**
     * Get the color for a specific text role and theme.
     *
     * @param role The text role (CONTROL or TITLE)
     * @param theme The theme identifier
     * @return The color as an Android Color integer
     */
    public static int getColor(TextRole role, String theme) {
        if (role == null || theme == null) {
            return FREE_LIGHT_CONTROL; // Default fallback
        }

        switch (theme) {
            case FREE_LIGHT:
                return getFreeLightColor(role);
            case FREE_DARK:
                return getFreeDarkColor(role);
            case DREAMER_LIGHT:
                return getDreamerLightColor(role);
            case DREAMER_DARK:
                return getDreamerDarkColor(role);
            default:
                return FREE_LIGHT_CONTROL; // Default fallback
        }
    }

    /**
     * Get color for Free Light theme.
     */
    private static int getFreeLightColor(TextRole role) {
        switch (role) {
            case CONTROL:
                return FREE_LIGHT_CONTROL;
            case TITLE:
                return FREE_LIGHT_TITLE;
            default:
                return FREE_LIGHT_CONTROL;
        }
    }

    /**
     * Get color for Free Dark theme.
     */
    private static int getFreeDarkColor(TextRole role) {
        switch (role) {
            case CONTROL:
                return FREE_DARK_CONTROL;
            case TITLE:
                return FREE_DARK_TITLE;
            default:
                return FREE_DARK_CONTROL;
        }
    }

    /**
     * Get color for Dreamer Light theme.
     */
    private static int getDreamerLightColor(TextRole role) {
        switch (role) {
            case CONTROL:
                return DREAMER_LIGHT_CONTROL;
            case TITLE:
                return DREAMER_LIGHT_TITLE;
            default:
                return DREAMER_LIGHT_CONTROL;
        }
    }

    /**
     * Get color for Dreamer Dark theme.
     */
    private static int getDreamerDarkColor(TextRole role) {
        switch (role) {
            case CONTROL:
                return DREAMER_DARK_CONTROL;
            case TITLE:
                return DREAMER_DARK_TITLE;
            default:
                return DREAMER_DARK_CONTROL;
        }
    }

    /**
     * Check if a theme identifier is valid.
     *
     * @param theme The theme identifier to check
     * @return true if the theme is valid, false otherwise
     */
    public static boolean isValidTheme(String theme) {
        return theme != null && (
            theme.equals(FREE_LIGHT) ||
            theme.equals(FREE_DARK) ||
            theme.equals(DREAMER_LIGHT) ||
            theme.equals(DREAMER_DARK)
        );
    }
}
