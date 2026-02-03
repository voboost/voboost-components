package ru.voboost.components.theme;

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
     */
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
     * @param value Theme value (e.g., "free-light")
     * @return Theme enum value, or FREE_DARK as default
     */
    public static Theme fromValue(String value) {
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
}
