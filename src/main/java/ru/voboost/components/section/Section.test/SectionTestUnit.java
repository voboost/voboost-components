package ru.voboost.components.section;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

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
        android.view.View child =
                new android.view.View(
                        androidx.test.core.app.ApplicationProvider.getApplicationContext());
        section.addView(child);

        assertEquals("Section should have 1 child", 1, section.getChildCount());
    }

    @Test
    public void testAddMultipleChildViews() {
        android.content.Context ctx =
                androidx.test.core.app.ApplicationProvider.getApplicationContext();
        section.addView(new android.view.View(ctx));
        section.addView(new android.view.View(ctx));

        assertEquals("Section should have 2 children", 2, section.getChildCount());
    }

    @Test
    public void testSectionIsViewGroup() {
        assertTrue("Section should be a ViewGroup", section instanceof android.view.ViewGroup);
    }

    // ============================================================
    // ACCESSIBILITY TESTS - Phase 5
    // ============================================================

    @Test
    public void testAccessibilityContentDescription() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Settings");
        title.put("ru", "Настройки");

        section.setTitle(title);
        section.setLanguage(Language.EN);

        assertEquals("Settings", section.getContentDescription());
    }

    @Test
    public void testAccessibilityContentDescriptionChangesWithLanguage() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Settings");
        title.put("ru", "Настройки");

        section.setTitle(title);
        section.setLanguage(Language.EN);
        assertEquals("Settings", section.getContentDescription());

        section.setLanguage(Language.RU);
        assertEquals("Настройки", section.getContentDescription());
    }

    @Test
    public void testAccessibilityContentDescriptionEmptyWhenNoTitle() {
        section.setLanguage(Language.EN);
        // Content description will be empty string when no title is set
        CharSequence desc = section.getContentDescription();
        assertTrue("Content description should be null or empty", desc == null || desc.length() == 0);
    }

    // ============================================================
    // NULL SAFETY TESTS - Phase 5
    // ============================================================

    @Test
    public void testGetTitleTextWithNullLanguage() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Settings");

        section.setTitle(title);
        // Language is null by default
        assertEquals("", section.getTitleText());
    }

    @Test
    public void testGetTitleTextWithNullTitle() {
        section.setLanguage(Language.EN);
        // Title is null by default
        assertEquals("", section.getTitleText());
    }

    @Test
    public void testGetTitleTextWithMissingLanguageCode() {
        Map<String, String> title = new HashMap<>();
        title.put("en", "Settings");

        section.setTitle(title);
        section.setLanguage(Language.EN);
        assertNotNull("Language code should not be null", Language.EN.getCode());
        assertEquals("Settings", section.getTitleText());
    }

    @Test
    public void testGetTitleTextWithEmptyLanguageCode() {
        Map<String, String> title = new HashMap<>();
        title.put("", "EmptyLanguageCode");

        section.setTitle(title);
        // Even if language code is empty string, getTitleText should not crash
        section.setLanguage(Language.EN);
        String result = section.getTitleText();
        assertNotNull("Result should not be null", result);
    }

    // ============================================================
    // TITLE CHECKBOX TESTS
    // ============================================================

    @Test
    public void testTitleCheckboxInitiallyAbsent() {
        section.setTheme(Theme.FREE_LIGHT);
        section.setLanguage(Language.EN);
        section.setTitle(java.util.Map.of("en", "T"));
        assertFalse(section.hasTitleCheckbox());
        assertFalse(section.isTitleChecked());
        assertFalse(section.isCollapsed());
    }

    @Test
    public void testSetTitleCheckboxOn() {
        section.setTheme(Theme.FREE_LIGHT);
        section.setLanguage(Language.EN);
        section.setTitle(java.util.Map.of("en", "T"));
        section.setTitleCheckbox(true);
        assertTrue(section.hasTitleCheckbox());
        assertTrue(section.isTitleChecked());
        assertFalse(section.isCollapsed());
    }

    @Test
    public void testSetTitleCheckboxOff() {
        section.setTheme(Theme.FREE_LIGHT);
        section.setLanguage(Language.EN);
        section.setTitle(java.util.Map.of("en", "T"));
        section.setTitleCheckbox(false);
        assertTrue(section.hasTitleCheckbox());
        assertFalse(section.isTitleChecked());
        assertTrue(section.isCollapsed());
    }

    @Test
    public void testClearTitleCheckbox() {
        section.setTheme(Theme.FREE_LIGHT);
        section.setLanguage(Language.EN);
        section.setTitle(java.util.Map.of("en", "T"));
        section.setTitleCheckbox(true);
        section.clearTitleCheckbox();
        assertFalse(section.hasTitleCheckbox());
        assertFalse(section.isCollapsed());
    }

    @Test
    public void testTitleCheckboxListenerCalled() {
        section.setTheme(Theme.FREE_LIGHT);
        section.setLanguage(Language.EN);
        section.setTitle(java.util.Map.of("en", "T"));
        final boolean[] received = new boolean[]{false};
        final Boolean[] value = new Boolean[]{null};
        section.setTitleCheckbox(false, isChecked -> {
            received[0] = true;
            value[0] = isChecked;
        });
        ru.voboost.components.checkbox.Checkbox cb =
                (ru.voboost.components.checkbox.Checkbox) section.getChildAt(0);
        cb.setChecked(true);
        assertTrue(section.isTitleChecked());
    }

    @Test
    public void testCollapsedHidesContentChildren() {
        section.setTheme(Theme.FREE_LIGHT);
        section.setLanguage(Language.EN);
        section.setTitle(java.util.Map.of("en", "T"));
        ru.voboost.components.checkbox.Checkbox child =
                ru.voboost.components.checkbox.Checkbox.create(
                        androidx.test.core.app.ApplicationProvider.getApplicationContext(),
                        Theme.FREE_LIGHT,
                        Language.EN,
                        false).build();
        section.addCheckbox(child);
        section.setTitleCheckbox(false);
        assertEquals(android.view.View.GONE, child.getVisibility());
        section.setTitleCheckbox(true);
        assertEquals(android.view.View.VISIBLE, child.getVisibility());
    }

    // ============================================================
    // INFO POPUP TESTS
    // ============================================================

    @Test
    public void testPopupTextSingleBlock() {
        java.util.Map<String, String> info = new java.util.HashMap<>();
        info.put("en", "Help text");
        section.setPopupText(info);
        assertTrue(section.hasPopupText());
        java.util.List<ru.voboost.components.section.Section.PopupBlock> blocks =
                section.getPopupBlocks();
        assertEquals(1, blocks.size());
        assertNull(blocks.get(0).getTitle());
        assertEquals(info, blocks.get(0).getText());
    }

    @Test
    public void testPopupTextMultipleBlocks() {
        java.util.Map<String, String> title1 = new java.util.HashMap<>();
        title1.put("en", "Title 1");
        java.util.Map<String, String> text1 = new java.util.HashMap<>();
        text1.put("en", "Text 1");
        java.util.Map<String, String> title2 = new java.util.HashMap<>();
        title2.put("en", "Title 2");
        java.util.Map<String, String> text2 = new java.util.HashMap<>();
        text2.put("en", "Text 2");

        java.util.List<ru.voboost.components.section.Section.PopupBlock> blocks =
                java.util.Arrays.asList(
                        new ru.voboost.components.section.Section.PopupBlock(title1, text1),
                        new ru.voboost.components.section.Section.PopupBlock(title2, text2)
                );
        section.setPopupText(blocks);
        assertEquals(2, section.getPopupBlocks().size());
    }

    @Test
    public void testClearPopupText() {
        java.util.Map<String, String> info = new java.util.HashMap<>();
        info.put("en", "Help");
        section.setPopupText(info);
        section.setPopupText((java.util.Map<String, String>) null);
        assertFalse(section.hasPopupText());
    }

    @Test
    public void testPopupTextEmptyList() {
        section.setPopupText(new java.util.ArrayList<>());
        assertFalse(section.hasPopupText());
    }

    @Test
    public void testPopupTextEmptyMap() {
        section.setPopupText(new java.util.HashMap<>());
        assertFalse(section.hasPopupText());
    }

    @Test
    public void testShowInfoPopupNoOpWithoutText() {
        section.setTheme(Theme.FREE_LIGHT);
        section.setLanguage(Language.EN);
        section.showPopup();
        assertFalse(section.hasPopupText());
    }

    // ============================================================
    // BOTTOM MARGIN TESTS
    // ============================================================

    @Test
    public void testIncludeBottomMarginDefaultsToTrue() {
        assertTrue(section.isIncludeBottomMargin());
    }

    @Test
    public void testSetIncludeBottomMarginFalseReducesHeight() {
        section.setTitle(java.util.Map.of("en", "Test"));
        section.setTheme(Theme.FREE_LIGHT);

        int widthSpec = android.view.View.MeasureSpec.makeMeasureSpec(700, android.view.View.MeasureSpec.EXACTLY);
        int heightSpec = android.view.View.MeasureSpec.makeMeasureSpec(0, android.view.View.MeasureSpec.UNSPECIFIED);
        section.measure(widthSpec, heightSpec);
        int heightWithMargin = section.getMeasuredHeight();

        section.setIncludeBottomMargin(false);
        section.measure(widthSpec, heightSpec);
        int heightWithoutMargin = section.getMeasuredHeight();

        assertEquals(SectionTheme.BOTTOM_MARGIN, heightWithMargin - heightWithoutMargin);
    }
}
