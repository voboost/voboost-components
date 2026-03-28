package ru.voboost.components.i18n;

import org.junit.Test;
import static org.junit.Assert.*;

public class LanguageTestUnit {

    @Test
    public void testGetCode_returnsCorrectCode() {
        assertEquals("en", Language.EN.getCode());
        assertEquals("ru", Language.RU.getCode());
    }

    @Test
    public void testFromCode_withValidCode_returnsCorrectLanguage() {
        assertEquals(Language.EN, Language.fromCode("en"));
        assertEquals(Language.RU, Language.fromCode("ru"));
    }

    @Test
    public void testFromCode_withNull_returnsEN() {
        assertEquals(Language.EN, Language.fromCode(null));
    }

    @Test
    public void testFromCode_withInvalidCode_returnsEN() {
        assertEquals(Language.EN, Language.fromCode("invalid"));
        assertEquals(Language.EN, Language.fromCode(""));
        assertEquals(Language.EN, Language.fromCode("de"));
    }

    @Test
    public void testFromCode_isCaseInsensitive() {
        assertEquals(Language.EN, Language.fromCode("EN"));
        assertEquals(Language.EN, Language.fromCode("En"));
        assertEquals(Language.RU, Language.fromCode("RU"));
        assertEquals(Language.RU, Language.fromCode("Ru"));
    }
}
