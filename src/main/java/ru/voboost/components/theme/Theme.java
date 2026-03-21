package ru.voboost.components.theme;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Shared theme enum for all Voboost components.
 *
 * Java enum is used because:
 * 1. Can be used directly in Java View code (Radio.java)
 * 2. Works seamlessly in Kotlin code
 * 3. No conversion needed between layers
 *
 * NOTE: Consuming projects (like voboost-config) should use this enum
 * instead of defining their own, since voboost-components is a standalone
 * library that can be used outside the voboost infrastructure.
 *
 * <p><b>Thread Safety:</b> This enum is immutable and thread-safe.
 * All methods can be safely called concurrently.
 */
public enum Theme {
    FREE_LIGHT("free-light"),
    FREE_DARK("free-dark"),
    DREAMER_LIGHT("dreamer-light"),
    DREAMER_DARK("dreamer-dark");

    private final String value;

    Theme(String value) {
        this.value = value;
    }

    /**
     * Get the string value for this theme (e.g., "free-light").
     * Used for serialization and backward compatibility.
     *
     * @return Non-null string representation of this theme
     */
    @NonNull
    public String getValue() {
        return value;
    }

    /**
     * Check if this is a light theme variant.
     */
    public boolean isLight() {
        return this == FREE_LIGHT || this == DREAMER_LIGHT;
    }

    /**
     * Check if this is a dark theme variant.
     */
    public boolean isDark() {
        return this == FREE_DARK || this == DREAMER_DARK;
    }

    /**
     * Check if this is a Free theme variant.
     */
    public boolean isFree() {
        return this == FREE_LIGHT || this == FREE_DARK;
    }

    /**
     * Check if this is a Dreamer theme variant.
     */
    public boolean isDreamer() {
        return this == DREAMER_LIGHT || this == DREAMER_DARK;
    }

    /**
     * Get Theme from string value.
     *
     * <p>Parsing is case-insensitive for backward compatibility.
     * Invalid values (null, empty, unknown) return FREE_DARK as default.
     *
     * @param value Theme value (e.g., "free-light"), or null
     * @return Theme enum value, never null (returns FREE_DARK as fallback)
     */
    @NonNull
    public static Theme fromValue(@Nullable String value) {
        if (value == null) {
            return FREE_DARK;
        }

        for (Theme theme : values()) {
            if (theme.value.equalsIgnoreCase(value)) {
                return theme;
            }
        }

        return FREE_DARK; // Default fallback
    }

    /**
     * Checks if a string value is a valid theme identifier.
     *
     * <p>This method performs strict case-sensitive matching.
     * Use this to validate user input before parsing.
     *
     * @param value The string value to check
     * @return true if the value is a valid theme identifier, false otherwise
     */
    public static boolean isValidThemeValue(@Nullable String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        for (Theme theme : values()) {
            if (theme.value.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
