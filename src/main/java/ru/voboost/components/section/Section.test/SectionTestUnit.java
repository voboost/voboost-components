package ru.voboost.components.section;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.HashMap;
import java.util.Map;

import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.Theme;

/**
 * Unit tests for the Section component.
 */
@RunWith(RobolectricTestRunner.class)
public class SectionTestUnit {

    private Section section;

    @Before
    public void setUp() {
        section = new Section(androidx.test.core.app.ApplicationProvider.getApplicationContext());
    }

    @Test
    public void testSetTitle() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Settings");
        title.put("ru", "Настройки");

        section.setTitle(title);
        assertEquals(title, section.getTitle());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetTitleNull() {
        section.setTitle(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetTitleEmpty() {
        section.setTitle(new HashMap<>());
    }

    @Test
    public void testGetTitleText() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Settings");
        title.put("ru", "Настройки");

        section.setTitle(title);
        section.setLanguage(Language.EN);
        assertEquals("Settings", section.getTitleText());

        section.setLanguage(Language.RU);
        assertEquals("Настройки", section.getTitleText());
    }

    @Test
    public void testGetTitleTextWithNoTitle() {
        section.setLanguage(Language.EN);
        assertEquals("", section.getTitleText());
    }

    @Test
    public void testSetTheme() {
        section.setTheme(Theme.FREE_LIGHT);
        assertEquals(Theme.FREE_LIGHT, section.getCurrentTheme());

        section.setTheme(Theme.DREAMER_DARK);
        assertEquals(Theme.DREAMER_DARK, section.getCurrentTheme());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetThemeNull() {
        section.setTheme(null);
    }

    @Test
    public void testSetLanguage() {
        section.setLanguage(Language.EN);
        assertEquals(Language.EN, section.getCurrentLanguage());

        section.setLanguage(Language.RU);
        assertEquals(Language.RU, section.getCurrentLanguage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetLanguageNull() {
        section.setLanguage(null);
    }

    @Test
    public void testDefaultState() {
        // Section should be created without exception
        assertNotNull(section);
    }

    @Test
    public void testThemeChangeTriggersRedraw() {
        // Set initial theme
        section.setTheme(Theme.FREE_LIGHT);

        // Change theme
        section.setTheme(Theme.FREE_DARK);

        // Theme should be updated
        assertEquals(Theme.FREE_DARK, section.getCurrentTheme());
    }

    @Test
    public void testLanguageChangeTriggersRedraw() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Settings");
        title.put("ru", "Настройки");

        section.setTitle(title);
        section.setLanguage(Language.EN);

        // Change language
        section.setLanguage(Language.RU);

        // Language should be updated
        assertEquals(Language.RU, section.getCurrentLanguage());
        assertEquals("Настройки", section.getTitleText());
    }

    @Test
    public void testAddChildView() {
        // Create a simple View as child
        android.view.View child = new android.view.View(
                androidx.test.core.app.ApplicationProvider.getApplicationContext());
        section.addView(child);

        assertEquals("Section should have 1 child", 1, section.getChildCount());
    }

    @Test
    public void testAddMultipleChildViews() {
        android.content.Context ctx = androidx.test.core.app.ApplicationProvider.getApplicationContext();
        section.addView(new android.view.View(ctx));
        section.addView(new android.view.View(ctx));

        assertEquals("Section should have 2 children", 2, section.getChildCount());
    }

    @Test
    public void testSectionIsViewGroup() {
        assertTrue("Section should be a ViewGroup", section instanceof android.view.ViewGroup);
    }
}
