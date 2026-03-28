package ru.voboost.components.i18n;

import java.util.Map;

/**
 * Utility class for internationalization operations.
 *
 * <p>
 * Provides helper methods for working with localized strings and
 * reducing code duplication across components.
 */
public final class I18nUtils {

    private I18nUtils() {
        // Utility class - prevent instantiation
        throw new AssertionError("Utility class should not be instantiated");
    }

    /**
     * Gets a localized string from a map of language codes to strings.
     *
     * @param localizedStrings Map of language code to localized string
     * @param language Current language
     * @param defaultValue Default value if string not found
     * @return Localized string, or defaultValue if not found
     */
    public static String getLocalizedString(
        Map<String, String> localizedStrings,
        Language language,
        String defaultValue
    ) {
        if (localizedStrings == null || language == null) {
            return defaultValue;
        }
        String langCode = language.getCode();
        return localizedStrings.getOrDefault(langCode, defaultValue);
    }
}
