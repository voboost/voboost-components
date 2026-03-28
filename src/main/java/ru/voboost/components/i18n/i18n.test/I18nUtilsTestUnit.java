package ru.voboost.components.i18n;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Map;

public class I18nUtilsTestUnit {

    @Test
    public void testGetLocalizedString_withValidLanguage_returnsCorrectString() {
        Map<String, String> strings = Map.of("en", "Hello", "ru", "Привет");
        assertEquals("Hello", I18nUtils.getLocalizedString(strings, Language.EN, ""));
        assertEquals("Привет", I18nUtils.getLocalizedString(strings, Language.RU, ""));
    }

    @Test
    public void testGetLocalizedString_withMissingLanguage_returnsDefault() {
        Map<String, String> strings = Map.of("en", "Hello");
        assertEquals("default", I18nUtils.getLocalizedString(strings, Language.RU, "default"));
    }

    @Test
    public void testGetLocalizedString_withNullMap_returnsDefault() {
        assertEquals("default", I18nUtils.getLocalizedString(null, Language.EN, "default"));
    }

    @Test
    public void testGetLocalizedString_withNullLanguage_returnsDefault() {
        Map<String, String> strings = Map.of("en", "Hello");
        assertEquals("default", I18nUtils.getLocalizedString(strings, null, "default"));
    }
}
