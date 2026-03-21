package ru.voboost.components.theme;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Unit tests for Theme enum.
 *
 * Tests cover:
 * - Enum values and their string representations
 * - Type checking methods (isLight, isDark, isFree, isDreamer)
 * - fromValue() parsing with valid, invalid, and edge cases
 * - isValidThemeValue() validation
 * - Thread-safety
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {33})
public class ThemeTestUnit {

    // ============================================================
    // Enum values tests
    // ============================================================

    @Test
    public void testAllEnumValuesHaveCorrectStringRepresentation() {
        assertEquals("free-light", Theme.FREE_LIGHT.getValue());
        assertEquals("free-dark", Theme.FREE_DARK.getValue());
        assertEquals("dreamer-light", Theme.DREAMER_LIGHT.getValue());
        assertEquals("dreamer-dark", Theme.DREAMER_DARK.getValue());
    }

    @Test
    public void testEnumValuesCount() {
        assertEquals("Should have 4 theme values", 4, Theme.values().length);
    }

    @Test
    public void testEnumValuesAreDistinct() {
        assertNotEquals("FREE_LIGHT and FREE_DARK should be different",
                Theme.FREE_LIGHT, Theme.FREE_DARK);
        assertNotEquals("FREE_LIGHT and DREAMER_LIGHT should be different",
                Theme.FREE_LIGHT, Theme.DREAMER_LIGHT);
        assertNotEquals("FREE_DARK and DREAMER_DARK should be different",
                Theme.FREE_DARK, Theme.DREAMER_DARK);
    }

    // ============================================================
    // isLight() tests
    // ============================================================

    @Test
    public void testIsLightForFreeLight() {
        assertTrue("FREE_LIGHT should be light", Theme.FREE_LIGHT.isLight());
    }

    @Test
    public void testIsLightForDreamerLight() {
        assertTrue("DREAMER_LIGHT should be light", Theme.DREAMER_LIGHT.isLight());
    }

    @Test
    public void testIsLightForFreeDark() {
        assertFalse("FREE_DARK should not be light", Theme.FREE_DARK.isLight());
    }

    @Test
    public void testIsLightForDreamerDark() {
        assertFalse("DREAMER_DARK should not be light", Theme.DREAMER_DARK.isLight());
    }

    // ============================================================
    // isDark() tests
    // ============================================================

    @Test
    public void testIsDarkForFreeDark() {
        assertTrue("FREE_DARK should be dark", Theme.FREE_DARK.isDark());
    }

    @Test
    public void testIsDarkForDreamerDark() {
        assertTrue("DREAMER_DARK should be dark", Theme.DREAMER_DARK.isDark());
    }

    @Test
    public void testIsDarkForFreeLight() {
        assertFalse("FREE_LIGHT should not be dark", Theme.FREE_LIGHT.isDark());
    }

    @Test
    public void testIsDarkForDreamerLight() {
        assertFalse("DREAMER_LIGHT should not be dark", Theme.DREAMER_LIGHT.isDark());
    }

    // ============================================================
    // isFree() tests
    // ============================================================

    @Test
    public void testIsFreeForFreeLight() {
        assertTrue("FREE_LIGHT should be free", Theme.FREE_LIGHT.isFree());
    }

    @Test
    public void testIsFreeForFreeDark() {
        assertTrue("FREE_DARK should be free", Theme.FREE_DARK.isFree());
    }

    @Test
    public void testIsFreeForDreamerLight() {
        assertFalse("DREAMER_LIGHT should not be free", Theme.DREAMER_LIGHT.isFree());
    }

    @Test
    public void testIsFreeForDreamerDark() {
        assertFalse("DREAMER_DARK should not be free", Theme.DREAMER_DARK.isFree());
    }

    // ============================================================
    // isDreamer() tests
    // ============================================================

    @Test
    public void testIsDreamerForDreamerLight() {
        assertTrue("DREAMER_LIGHT should be dreamer", Theme.DREAMER_LIGHT.isDreamer());
    }

    @Test
    public void testIsDreamerForDreamerDark() {
        assertTrue("DREAMER_DARK should be dreamer", Theme.DREAMER_DARK.isDreamer());
    }

    @Test
    public void testIsDreamerForFreeLight() {
        assertFalse("FREE_LIGHT should not be dreamer", Theme.FREE_LIGHT.isDreamer());
    }

    @Test
    public void testIsDreamerForFreeDark() {
        assertFalse("FREE_DARK should not be dreamer", Theme.FREE_DARK.isDreamer());
    }

    // ============================================================
    // fromValue() with valid data
    // ============================================================

    @Test
    public void testFromValueFreeLight() {
        assertEquals("free-light", Theme.FREE_LIGHT,
                Theme.fromValue("free-light"));
    }

    @Test
    public void testFromValueFreeDark() {
        assertEquals("free-dark", Theme.FREE_DARK,
                Theme.fromValue("free-dark"));
    }

    @Test
    public void testFromValueDreamerLight() {
        assertEquals("dreamer-light", Theme.DREAMER_LIGHT,
                Theme.fromValue("dreamer-light"));
    }

    @Test
    public void testFromValueDreamerDark() {
        assertEquals("dreamer-dark", Theme.DREAMER_DARK,
                Theme.fromValue("dreamer-dark"));
    }

    @Test
    public void testFromValueUpperCase() {
        // Current implementation uses equalsIgnoreCase - this should work
        assertEquals("FREE-LIGHT should be recognized (case-insensitive)",
                Theme.FREE_LIGHT, Theme.fromValue("FREE-LIGHT"));
    }

    @Test
    public void testFromValueMixedCase() {
        assertEquals("Free-Light should be recognized (case-insensitive)",
                Theme.FREE_LIGHT, Theme.fromValue("Free-Light"));
    }

    // ============================================================
    // fromValue() with edge cases
    // ============================================================

    @Test
    public void testFromValueNull() {
        assertEquals("null should return FREE_DARK (default fallback)",
                Theme.FREE_DARK, Theme.fromValue(null));
    }

    @Test
    public void testFromValueEmptyString() {
        assertEquals("empty string should return FREE_DARK (default fallback)",
                Theme.FREE_DARK, Theme.fromValue(""));
    }

    @Test
    public void testFromValueWhitespace() {
        assertEquals("whitespace should return FREE_DARK (no match)",
                Theme.FREE_DARK, Theme.fromValue("   "));
    }

    @Test
    public void testFromValueInvalidString() {
        assertEquals("invalid string should return FREE_DARK (default fallback)",
                Theme.FREE_DARK, Theme.fromValue("invalid-theme"));
    }

    @Test
    public void testFromValuePartialMatch() {
        assertEquals("partial match should return FREE_DARK (no exact match)",
                Theme.FREE_DARK, Theme.fromValue("free"));
    }

    @Test
    public void testFromValueWithWhitespace() {
        // Current implementation doesn't trim, so this won't match
        assertEquals("value with leading/trailing whitespace should return FREE_DARK",
                Theme.FREE_DARK, Theme.fromValue("  free-light  "));
    }

    // ============================================================
    // Exhaustive coverage tests
    // ============================================================

    @Test
    public void testAllThemeValues() {
        // Iterate over all enum values and verify they work
        for (Theme theme : Theme.values()) {
            assertNotNull("Theme value should not be null", theme);
            assertNotNull("Theme getValue() should not be null", theme.getValue());
            assertNotNull("Theme fromValue(getValue()) should return same theme",
                    Theme.fromValue(theme.getValue()));

            // Verify fromValue returns the same theme
            assertEquals("fromValue(getValue()) should be idempotent",
                    theme, Theme.fromValue(theme.getValue()));
        }
    }

    @Test
    public void testThemeValuesAreImmutable() {
        // Theme enum values are singletons and immutable
        Theme theme1 = Theme.FREE_LIGHT;
        Theme theme2 = Theme.FREE_LIGHT;
        assertSame("Same enum value should be same instance", theme1, theme2);
    }

    // ============================================================
    // Thread-safety tests
    // ============================================================

    @Test
    public void testFromValueThreadSafety() throws InterruptedException {
        final int threadCount = 10;
        final int iterationsPerThread = 100;
        Thread[] threads = new Thread[threadCount];
        final java.util.concurrent.atomic.AtomicBoolean hasError = new java.util.concurrent.atomic.AtomicBoolean(false);

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < iterationsPerThread; j++) {
                        // Test different values from different threads
                        Theme result = Theme.fromValue("free-light");
                        if (result != Theme.FREE_LIGHT) {
                            hasError.set(true);
                        }

                        result = Theme.fromValue("dreamer-dark");
                        if (result != Theme.DREAMER_DARK) {
                            hasError.set(true);
                        }

                        result = Theme.fromValue(null);
                        if (result != Theme.FREE_DARK) {
                            hasError.set(true);
                        }
                    }
                } catch (Exception e) {
                    hasError.set(true);
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        assertFalse("No errors should occur during concurrent access", hasError.get());
    }

    // ============================================================
    // isValidThemeValue() tests
    // ============================================================

    @Test
    public void testIsValidThemeValueFreeLight() {
        assertTrue("free-light should be valid",
                Theme.isValidThemeValue("free-light"));
    }

    @Test
    public void testIsValidThemeValueFreeDark() {
        assertTrue("free-dark should be valid",
                Theme.isValidThemeValue("free-dark"));
    }

    @Test
    public void testIsValidThemeValueDreamerLight() {
        assertTrue("dreamer-light should be valid",
                Theme.isValidThemeValue("dreamer-light"));
    }

    @Test
    public void testIsValidThemeValueDreamerDark() {
        assertTrue("dreamer-dark should be valid",
                Theme.isValidThemeValue("dreamer-dark"));
    }

    @Test
    public void testIsValidThemeValueNull() {
        assertFalse("null should not be valid",
                Theme.isValidThemeValue(null));
    }

    @Test
    public void testIsValidThemeValueEmptyString() {
        assertFalse("empty string should not be valid",
                Theme.isValidThemeValue(""));
    }

    @Test
    public void testIsValidThemeValueWhitespace() {
        assertFalse("whitespace should not be valid",
                Theme.isValidThemeValue("   "));
    }

    @Test
    public void testIsValidThemeValueInvalidString() {
        assertFalse("invalid string should not be valid",
                Theme.isValidThemeValue("invalid-theme"));
    }

    @Test
    public void testIsValidThemeValuePartialMatch() {
        assertFalse("partial match should not be valid",
                Theme.isValidThemeValue("free"));
    }

    @Test
    public void testIsValidThemeValueWithWhitespace() {
        // Strict validation - whitespace matters
        assertFalse("value with leading/trailing whitespace should not be valid",
                Theme.isValidThemeValue("  free-light  "));
    }

    @Test
    public void testIsValidThemeValueIsCaseSensitive() {
        // Strict validation - case matters
        assertFalse("FREE-LIGHT (uppercase) should not be valid in strict validation",
                Theme.isValidThemeValue("FREE-LIGHT"));
        assertFalse("Free-Light (mixed case) should not be valid in strict validation",
                Theme.isValidThemeValue("Free-Light"));
    }

    @Test
    public void testIsValidThemeValueForAllEnumValues() {
        // All enum values should be valid
        for (Theme theme : Theme.values()) {
            assertTrue("Theme value " + theme.getValue() + " should be valid",
                    Theme.isValidThemeValue(theme.getValue()));
        }
    }
}
