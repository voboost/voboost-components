package ru.voboost.components.demo.shared;

import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.Theme;

/**
 * Utility methods for demo applications.
 *
 * <p>This class provides helper methods for:
 * - Language and theme conversions
 * - Display name formatting
 * - Validation and utility functions
 */
public class DemoHelpers {

    private DemoHelpers() {
        // Prevent instantiation
    }

    /**
     * Converts a language code to a Language enum.
     *
     * @param languageCode the language code (e.g., "en", "ru")
     * @return the corresponding Language enum
     */
    public static Language convertToLanguage(String languageCode) {
        if (languageCode == null) {
            return Language.EN;
        }

        switch (languageCode.toLowerCase()) {
            case "ru":
                return Language.RU;
            case "en":
            default:
                return Language.EN;
        }
    }

    /**
     * Converts a combined theme string to a Theme enum.
     *
     * @param combinedTheme the combined theme (e.g., "free-light", "dreamer-dark")
     * @return the corresponding Theme enum
     */
    public static Theme convertToTheme(String combinedTheme) {
        if (combinedTheme == null) {
            return Theme.FREE_LIGHT;
        }

        switch (combinedTheme.toLowerCase()) {
            case "free-light":
                return Theme.FREE_LIGHT;
            case "free-dark":
                return Theme.FREE_DARK;
            case "dreamer-light":
                return Theme.DREAMER_LIGHT;
            case "dreamer-dark":
                return Theme.DREAMER_DARK;
            default:
                return Theme.FREE_LIGHT;
        }
    }

    /**
     * Returns the display name for a language code.
     *
     * @param languageCode the language code (e.g., "en", "ru")
     * @param currentLanguage the current language for display
     * @return the display name for the language
     */
    public static String getLanguageDisplayName(String languageCode, String currentLanguage) {
        if (languageCode == null) {
            return "";
        }

        if ("ru".equals(currentLanguage)) {
            return "ru".equals(languageCode) ? "Русский" : "English";
        } else {
            return "ru".equals(languageCode) ? "Russian" : "English";
        }
    }

    /**
     * Returns the display name for a theme value.
     *
     * @param themeValue the theme value (e.g., "light", "dark")
     * @param currentLanguage the current language for display
     * @return the display name for the theme
     */
    public static String getThemeDisplayName(String themeValue, String currentLanguage) {
        if (themeValue == null) {
            return "";
        }

        if ("ru".equals(currentLanguage)) {
            return "light".equals(themeValue) ? "Светлая" : "Тёмная";
        } else {
            return "light".equals(themeValue) ? "Light" : "Dark";
        }
    }

    /**
     * Returns the display name for a car type value.
     *
     * @param carTypeValue the car type value (e.g., "free", "dreamer")
     * @param currentLanguage the current language for display
     * @return the display name for the car type
     */
    public static String getCarTypeDisplayName(String carTypeValue, String currentLanguage) {
        if (carTypeValue == null) {
            return "";
        }

        if ("ru".equals(currentLanguage)) {
            return "free".equals(carTypeValue) ? "Фри" : "Дример";
        } else {
            return "free".equals(carTypeValue) ? "Free" : "Dreamer";
        }
    }

    /**
     * Returns the display name for a climate control value.
     *
     * @param climateValue the climate value (e.g., "auto", "manual")
     * @param currentLanguage the current language for display
     * @return the display name for the climate control
     */
    public static String getClimateDisplayName(String climateValue, String currentLanguage) {
        if (climateValue == null) {
            return "";
        }

        if ("ru".equals(currentLanguage)) {
            switch (climateValue) {
                case "auto":
                    return "Автоматический";
                case "manual":
                    return "Ручной";
                case "eco":
                    return "Эко режим";
                case "sport":
                    return "Спорт режим";
                default:
                    return climateValue;
            }
        } else {
            switch (climateValue) {
                case "auto":
                    return "Automatic";
                case "manual":
                    return "Manual";
                case "eco":
                    return "Eco Mode";
                case "sport":
                    return "Sport Mode";
                default:
                    return climateValue;
            }
        }
    }

    /**
     * Returns the display name for an audio setting value.
     *
     * @param audioValue the audio value (e.g., "standard", "premium")
     * @param currentLanguage the current language for display
     * @return the display name for the audio setting
     */
    public static String getAudioDisplayName(String audioValue, String currentLanguage) {
        if (audioValue == null) {
            return "";
        }

        if ("ru".equals(currentLanguage)) {
            switch (audioValue) {
                case "standard":
                    return "Стандартный";
                case "premium":
                    return "Премиум";
                case "surround":
                    return "Объёмный звук";
                case "off":
                    return "Выключено";
                default:
                    return audioValue;
            }
        } else {
            switch (audioValue) {
                case "standard":
                    return "Standard";
                case "premium":
                    return "Premium";
                case "surround":
                    return "Surround";
                case "off":
                    return "Off";
                default:
                    return audioValue;
            }
        }
    }

    /**
     * Returns the display name for a display setting value.
     *
     * @param displayValue the display value (e.g., "auto", "day")
     * @param currentLanguage the current language for display
     * @return the display name for the display setting
     */
    public static String getDisplayDisplayName(String displayValue, String currentLanguage) {
        if (displayValue == null) {
            return "";
        }

        if ("ru".equals(currentLanguage)) {
            switch (displayValue) {
                case "auto":
                    return "Автоматический";
                case "day":
                    return "Дневной режим";
                case "night":
                    return "Ночной режим";
                case "adaptive":
                    return "Адаптивный";
                default:
                    return displayValue;
            }
        } else {
            switch (displayValue) {
                case "auto":
                    return "Automatic";
                case "day":
                    return "Day Mode";
                case "night":
                    return "Night Mode";
                case "adaptive":
                    return "Adaptive";
                default:
                    return displayValue;
            }
        }
    }

    /**
     * Returns the display name for a system setting value.
     *
     * @param systemValue the system value (e.g., "normal", "performance")
     * @param currentLanguage the current language for display
     * @return the display name for the system setting
     */
    public static String getSystemDisplayName(String systemValue, String currentLanguage) {
        if (systemValue == null) {
            return "";
        }

        if ("ru".equals(currentLanguage)) {
            switch (systemValue) {
                case "normal":
                    return "Обычный";
                case "performance":
                    return "Производительность";
                case "eco":
                    return "Эко режим";
                case "custom":
                    return "Пользовательский";
                default:
                    return systemValue;
            }
        } else {
            switch (systemValue) {
                case "normal":
                    return "Normal";
                case "performance":
                    return "Performance";
                case "eco":
                    return "Eco Mode";
                case "custom":
                    return "Custom";
                default:
                    return systemValue;
            }
        }
    }

    /**
     * Returns the display name for a screen lift state.
     *
     * @param screenLiftState the screen lift state (1 for lowered, 2 for raised)
     * @param currentLanguage the current language for display
     * @return the display name for the screen lift state
     */
    public static String getScreenLiftDisplayName(int screenLiftState, String currentLanguage) {
        if ("ru".equals(currentLanguage)) {
            return screenLiftState == 1 ? "Опущен" : "Поднят";
        } else {
            return screenLiftState == 1 ? "Lowered" : "Raised";
        }
    }

    /**
     * Validates if a tab value is valid.
     *
     * @param tabValue the tab value to validate
     * @return true if the tab value is valid, false otherwise
     */
    public static boolean isValidTabValue(String tabValue) {
        if (tabValue == null) {
            return false;
        }

        return tabValue.equals("language")
                || tabValue.equals("theme")
                || tabValue.equals("car_type")
                || tabValue.equals("climate")
                || tabValue.equals("audio")
                || tabValue.equals("display")
                || tabValue.equals("system");
    }

    /**
     * Validates if a language code is valid.
     *
     * @param languageCode the language code to validate
     * @return true if the language code is valid, false otherwise
     */
    public static boolean isValidLanguageCode(String languageCode) {
        return languageCode != null && (languageCode.equals("en") || languageCode.equals("ru"));
    }

    /**
     * Validates if a theme value is valid.
     *
     * @param themeValue the theme value to validate
     * @return true if the theme value is valid, false otherwise
     */
    public static boolean isValidThemeValue(String themeValue) {
        return themeValue != null && (themeValue.equals("light") || themeValue.equals("dark"));
    }

    /**
     * Validates if a car type value is valid.
     *
     * @param carTypeValue the car type value to validate
     * @return true if the car type value is valid, false otherwise
     */
    public static boolean isValidCarTypeValue(String carTypeValue) {
        return carTypeValue != null
                && (carTypeValue.equals("free") || carTypeValue.equals("dreamer"));
    }
}
