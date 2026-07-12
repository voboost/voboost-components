package ru.voboost.components.hint;

import static org.junit.Assert.*;

import android.content.Context;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.Theme;

/**
 * Unit tests for Hint component Java implementation.
 * Tests core functionality, theme/language handling, and edge cases.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {33})
public class HintTestUnit {

    private Context context;
    private Hint hint;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        hint = new Hint(context);
    }

    // ============================================================
    // INITIALIZATION TESTS
    // ============================================================

    @Test
    public void testInitialization() {
        assertNotNull("Hint should be initialized", hint);
    }

    @Test
    public void testDefaultLanguageIsNull() {
        assertNull("Default language should be null until set",
                hint.getCurrentLanguage());
    }

    // ============================================================
    // TEXT TESTS
    // ============================================================

    @Test
    public void testSetText() {
        Map<String, String> text = new HashMap<>();
        text.put("en", "Stop all apps");
        text.put("ru", "Остановить все приложения");
        hint.setText(text);
        // No exception means success; displayText is private but resolved via measure
        hint.measure(
                View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY)
        );
        assertTrue("Measured height should be positive with text",
                hint.getMeasuredHeight() > 0);
    }

    @Test
    public void testSetTextNullDoesNotCrash() {
        hint.setText(null);
        hint.measure(
                View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY)
        );
        assertEquals("Height should be 0 with no text",
                0, hint.getMeasuredHeight());
    }

    @Test
    public void testSetTextEmptyMap() {
        hint.setText(new HashMap<>());
        hint.measure(
                View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY)
        );
        assertEquals("Height should be 0 with empty text map",
                0, hint.getMeasuredHeight());
    }

    @Test
    public void testSetTextUnicode() {
        Map<String, String> text = new HashMap<>();
        text.put("ru", "Настройки сохранены");
        hint.setLanguage(Language.RU);
        hint.setText(text);
        hint.measure(
                View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY)
        );
        assertTrue("Height should be positive with unicode text",
                hint.getMeasuredHeight() > 0);
    }

    // ============================================================
    // LANGUAGE TESTS
    // ============================================================

    @Test
    public void testSetLanguage() {
        hint.setLanguage(Language.EN);
        assertEquals(Language.EN, hint.getCurrentLanguage());
    }

    @Test
    public void testSetLanguageRu() {
        hint.setLanguage(Language.RU);
        assertEquals(Language.RU, hint.getCurrentLanguage());
    }

    @Test
    public void testLanguageSwitchResolvesText() {
        Map<String, String> text = new HashMap<>();
        text.put("en", "English text");
        text.put("ru", "Русский текст");
        hint.setText(text);

        hint.setLanguage(Language.EN);
        hint.measure(
                View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY)
        );
        int enHeight = hint.getMeasuredHeight();

        hint.setLanguage(Language.RU);
        hint.measure(
                View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY)
        );
        int ruHeight = hint.getMeasuredHeight();

        assertTrue("Both languages should produce positive height",
                enHeight > 0 && ruHeight > 0);
    }

    @Test
    public void testFallbackToFirstEntryWhenLanguageMissing() {
        Map<String, String> text = new HashMap<>();
        text.put("ru", "Только русский");
        hint.setLanguage(Language.EN); // No "en" entry
        hint.setText(text);
        hint.measure(
                View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY)
        );
        assertTrue("Should fall back to first available entry",
                hint.getMeasuredHeight() > 0);
    }

    // ============================================================
    // THEME TESTS
    // ============================================================

    @Test
    public void testSetTheme() {
        hint.setTheme(Theme.FREE_DARK);
        // No exception means success; theme is applied to paint color
    }

    @Test
    public void testSetThemeNull() {
        // Hint allows null theme (uses default color), should not crash
        hint.setTheme(null);
    }

    @Test
    public void testAllThemes() {
        for (Theme theme : Theme.values()) {
            hint.setTheme(theme);
        }
    }

    @Test
    public void testPropagateTheme() {
        hint.propagateTheme(Theme.FREE_LIGHT);
        // propagateTheme delegates to setTheme; no exception means success
    }

    @Test
    public void testPropagateLanguage() {
        hint.propagateLanguage(Language.RU);
        assertEquals(Language.RU, hint.getCurrentLanguage());
    }

    // ============================================================
    // MEASURE TESTS — verify the actual height-calculation algorithm
    // ============================================================

    /**
     * Single-line text must produce exactly:
     * PADDING_TOP + (1 * lineHeight) + PADDING_BOTTOM
     * where lineHeight = TEXT_SIZE_PX * 1.3f.
     */
    @Test
    public void testSingleLineHeightMatchesFormula() {
        Map<String, String> text = new HashMap<>();
        text.put("en", "Short");
        hint.setLanguage(Language.EN);
        hint.setText(text);
        hint.measure(
                View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY)
        );
        int lineHeight = (int) (HintTheme.TEXT_SIZE_PX * 1.3f);
        int expected = HintTheme.PADDING_TOP_PX + lineHeight + HintTheme.PADDING_BOTTOM_PX;
        assertEquals("Single-line height must match the documented formula",
                expected, hint.getMeasuredHeight());
    }

    /**
     * Height must never be negative and must include padding for any non-empty text.
     * (Robolectric's textPaint.measureText may return 0, so we verify the
     * invariant: non-empty text always yields height >= padding + one line.)
     */
    @Test
    public void testNonEmptyTextHeightIncludesPaddingAndLine() {
        Map<String, String> text = new HashMap<>();
        text.put("en", "Any non-empty text");
        hint.setLanguage(Language.EN);
        hint.setText(text);
        hint.measure(
                View.MeasureSpec.makeMeasureSpec(400, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY)
        );
        int minExpected = HintTheme.PADDING_TOP_PX
                + (int) (HintTheme.TEXT_SIZE_PX * 1.3f)
                + HintTheme.PADDING_BOTTOM_PX;
        assertTrue("Non-empty text height must be at least padding + one line: "
                + hint.getMeasuredHeight() + " >= " + minExpected,
                hint.getMeasuredHeight() >= minExpected);
    }

    /**
     * Empty text must produce zero height (component collapses).
     */
    @Test
    public void testEmptyTextProducesZeroHeight() {
        hint.setLanguage(Language.EN);
        hint.setText(new HashMap<>());
        hint.measure(
                View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY)
        );
        assertEquals("Empty text must collapse to 0 height", 0, hint.getMeasuredHeight());
    }

    /**
     * Measured width must always equal the width spec size (fills available width).
     */
    @Test
    public void testMeasuredWidthEqualsSpec() {
        Map<String, String> text = new HashMap<>();
        text.put("en", "Any text");
        hint.setLanguage(Language.EN);
        hint.setText(text);
        hint.measure(
                View.MeasureSpec.makeMeasureSpec(800, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY)
        );
        assertEquals("Width must match spec", 800, hint.getMeasuredWidth());
    }

    /**
     * Re-measuring after a language change must produce a valid (non-zero) height
     * when text is present, and the height must be consistent across re-measures
     * (idempotent measure for the same input).
     */
    @Test
    public void testMeasureIsIdempotentForSameInput() {
        Map<String, String> text = new HashMap<>();
        text.put("en", "Consistent text");
        hint.setLanguage(Language.EN);
        hint.setText(text);

        hint.measure(
                View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY)
        );
        int firstHeight = hint.getMeasuredHeight();

        hint.measure(
                View.MeasureSpec.makeMeasureSpec(1920, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY)
        );
        int secondHeight = hint.getMeasuredHeight();

        assertEquals("Re-measuring same input must yield same height",
                firstHeight, secondHeight);
        assertTrue("Height must be positive for non-empty text", firstHeight > 0);
    }
}
