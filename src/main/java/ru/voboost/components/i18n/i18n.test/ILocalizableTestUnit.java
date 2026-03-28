package ru.voboost.components.i18n;

import android.content.Context;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class ILocalizableTestUnit {

    @Test
    public void testSection_propagatesLanguageToChildren() {
        Context context = RuntimeEnvironment.getApplication();
        ru.voboost.components.section.Section section =
            new ru.voboost.components.section.Section(context);
        ru.voboost.components.radio.Radio radio =
            new ru.voboost.components.radio.Radio(context);

        section.setLanguage(Language.EN);
        assertEquals(Language.EN, section.getCurrentLanguage());

        section.addView(radio);
        section.propagateLanguage(Language.RU);

        assertEquals(Language.RU, radio.getCurrentLanguage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSection_setLanguageNull_throwsException() {
        Context context = RuntimeEnvironment.getApplication();
        ru.voboost.components.section.Section section =
            new ru.voboost.components.section.Section(context);
        section.setLanguage(null);
    }

    @Test
    public void testPropagateLanguage_withNull_doesNotThrow() {
        Context context = RuntimeEnvironment.getApplication();
        ru.voboost.components.section.Section section =
            new ru.voboost.components.section.Section(context);
        ru.voboost.components.radio.Radio radio =
            new ru.voboost.components.radio.Radio(context);

        section.addView(radio);
        section.setLanguage(Language.EN);
        section.propagateLanguage(Language.EN); // Propagate to children

        // Should not throw exception
        section.propagateLanguage(null);

        // Language should remain EN
        assertEquals(Language.EN, section.getCurrentLanguage());
        assertEquals(Language.EN, radio.getCurrentLanguage());
    }
}
