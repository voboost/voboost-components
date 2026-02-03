package ru.voboost.components.i18n;

/**
 * Shared language enum for all Voboost components.
 *
 * Java enum is used because:
 * 1. Can be used directly in Java View code
 * 2. Works seamlessly in Kotlin code
 * 3. No conversion needed between layers
 *
 * NOTE: Consuming projects (like voboost-config) should use this enum
 * instead of defining their own, since voboost-components is a standalone
 * library that can be used outside the voboost infrastructure.
 */
public enum Language {
    EN("en"),
    RU("ru");

    private final String code;

    Language(String code) {
        this.code = code;
    }

    /**
     * Get the ISO 639-1 language code (e.g., "en", "ru").
     * Used for serialization and i18n lookups.
     */
    public String getCode() {
        return code;
    }

    /**
     * Get Language from string code.
     * @param code Language code (e.g., "en", "ru")
     * @return Language enum value, or EN as default
     */
    public static Language fromCode(String code) {
        if (code == null) {
            return EN;
        }

        for (Language lang : values()) {
            if (lang.code.equalsIgnoreCase(code)) {
                return lang;
            }
        }

        return EN; // Default fallback
    }
}
