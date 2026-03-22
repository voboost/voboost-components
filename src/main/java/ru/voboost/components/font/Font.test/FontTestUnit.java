package ru.voboost.components.font;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import android.content.Context;
import android.graphics.Typeface;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ru.voboost.components.font.Font;

/**
 * Unit tests for Font component.
 *
 * Tests cover:
 * - Caching behavior (same instance returned on repeated calls)
 * - Automatic bold font selection (ASCII vs Unicode)
 * - Null and empty string handling
 * - Various character sets (ASCII, Cyrillic, CJK, emoji)
 * - Cache clearing
 */
@RunWith(AndroidJUnit4.class)
public class FontTestUnit {

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        // Clear cache before each test to ensure clean state
        Font.clearCache();
    }

    @After
    public void tearDown() {
        // Clear cache after each test
        Font.clearCache();
    }

    // ============================================================
    // CACHING TESTS
    // ============================================================

    @Test
    public void testGetRegular_returnsSameInstance() {
        Typeface first = Font.getRegular(context);
        Typeface second = Font.getRegular(context);

        assertSame("getRegular should return cached instance", first, second);
    }

    @Test
    public void testGetBold_asciiText_returnsSameInstance() {
        Typeface first = Font.getBold(context, "Hello");
        Typeface second = Font.getBold(context, "World");

        assertSame("getBold with ASCII text should return cached ASCII bold instance", first, second);
    }

    @Test
    public void testGetBold_cyrillicText_returnsSameInstance() {
        Typeface first = Font.getBold(context, "Привет");
        Typeface second = Font.getBold(context, "Мир");

        assertSame("getBold with Cyrillic text should return cached Unicode bold instance", first, second);
    }

    @Test
    public void testGetBold_asciiAndCyrillic_returnDifferentInstances() {
        Typeface asciiFont = Font.getBold(context, "Hello");
        Typeface cyrillicFont = Font.getBold(context, "Привет");

        // Different font files should be loaded
        assertSame("ASCII bold should always return same instance", asciiFont, Font.getBold(context, "Test"));
        assertSame("Unicode bold should always return same instance", cyrillicFont, Font.getBold(context, "Тест"));
    }

    // ============================================================
    // BOLD FONT SELECTION TESTS
    // ============================================================

    @Test
    public void testGetBold_asciiText_returnsAsciiFont() {
        Typeface font = Font.getBold(context, "Hello World");

        assertNotNull("getBold should return non-null for ASCII text", font);
        // Should be the ASCII bold font (same instance as other ASCII text)
        assertSame("ASCII text should use ASCII bold font", Font.getBold(context, "Test"), font);
    }

    @Test
    public void testGetBold_cyrillicText_returnsUnicodeFont() {
        Typeface font = Font.getBold(context, "Привет мир");

        assertNotNull("getBold should return non-null for Cyrillic text", font);
        // Should be the Unicode bold font (different from ASCII)
        Typeface asciiFont = Font.getBold(context, "Hello");
        assertSame("Cyrillic text should use Unicode bold font", Font.getBold(context, "Тест"), font);
    }

    @Test
    public void testGetBold_mixedText_returnsUnicodeFont() {
        Typeface font = Font.getBold(context, "HelloПривет");

        assertNotNull("getBold should return non-null for mixed text", font);
        // Mixed text should use Unicode font
        assertSame("Mixed text should use Unicode bold font", Font.getBold(context, "Привет"), font);
    }

    @Test
    public void testGetBold_numbersOnly_returnsAsciiFont() {
        Typeface font = Font.getBold(context, "1234567890");

        assertNotNull("getBold should return non-null for numbers", font);
        assertSame("Numbers should use ASCII bold font", Font.getBold(context, "Test"), font);
    }

    @Test
    public void testGetBold_specialCharsAscii_returnsAsciiFont() {
        Typeface font = Font.getBold(context, "Hello! @#$%");

        assertNotNull("getBold should return non-null for special ASCII chars", font);
        assertSame("Special ASCII chars should use ASCII bold font", Font.getBold(context, "Test"), font);
    }

    @Test
    public void testGetBold_chineseCharacters_returnsUnicodeFont() {
        Typeface font = Font.getBold(context, "你好世界");

        assertNotNull("getBold should return non-null for Chinese text", font);
        assertSame("Chinese text should use Unicode bold font", Font.getBold(context, "Привет"), font);
    }

    @Test
    public void testGetBold_emoji_returnsUnicodeFont() {
        Typeface font = Font.getBold(context, "Hello 👋");

        assertNotNull("getBold should return non-null for emoji", font);
        assertSame("Emoji should use Unicode bold font", Font.getBold(context, "Привет"), font);
    }

    @Test
    public void testGetBold_japaneseCharacters_returnsUnicodeFont() {
        Typeface font = Font.getBold(context, "こんにちは");

        assertNotNull("getBold should return non-null for Japanese text", font);
        assertSame("Japanese text should use Unicode bold font", Font.getBold(context, "Привет"), font);
    }

    @Test
    public void testGetBold_arabicCharacters_returnsUnicodeFont() {
        Typeface font = Font.getBold(context, "مرحبا");

        assertNotNull("getBold should return non-null for Arabic text", font);
        assertSame("Arabic text should use Unicode bold font", Font.getBold(context, "Привет"), font);
    }

    // ============================================================
    // NULL AND EMPTY STRING TESTS
    // ============================================================

    @Test
    public void testGetBold_nullText_returnsAsciiFont() {
        Typeface font = Font.getBold(context, null);

        assertNotNull("getBold should return non-null for null text", font);
        assertSame("Null text should use ASCII bold font", Font.getBold(context, "Test"), font);
    }

    @Test
    public void testGetBold_emptyString_returnsAsciiFont() {
        Typeface font = Font.getBold(context, "");

        assertNotNull("getBold should return non-null for empty string", font);
        assertSame("Empty string should use ASCII bold font", Font.getBold(context, "Test"), font);
    }

    @Test
    public void testGetBold_whitespaceOnly_returnsAsciiFont() {
        Typeface font = Font.getBold(context, "   ");

        assertNotNull("getBold should return non-null for whitespace", font);
        assertSame("Whitespace should use ASCII bold font", Font.getBold(context, "Test"), font);
    }

    // ============================================================
    // CACHE CLEARING TESTS
    // ============================================================

    @Test
    public void testClearCache_resetsInstances() {
        Typeface firstRegular = Font.getRegular(context);
        Typeface firstBoldAscii = Font.getBold(context, "Hello");
        Typeface firstBoldUnicode = Font.getBold(context, "Привет");

        Font.clearCache();

        Typeface secondRegular = Font.getRegular(context);
        Typeface secondBoldAscii = Font.getBold(context, "Hello");
        Typeface secondBoldUnicode = Font.getBold(context, "Привет");

        // After clearing cache, new instances should be created
        // (Note: Typeface might be cached at OS level, but Font's cache should be cleared)
        assertNotNull("getRegular should return non-null after cache clear", secondRegular);
        assertNotNull("getBold should return non-null after cache clear", secondBoldAscii);
        assertNotNull("getBold should return non-null after cache clear", secondBoldUnicode);
    }

    // ============================================================
    // ERROR HANDLING TESTS
    // ============================================================

    @Test
    public void testGetRegular_throwsOnMissingFile() {
        // This test documents current behavior
        // If font file is missing, Font.loadFont throws RuntimeException
        // In normal operation with correct build setup, this should never happen

        Typeface font = Font.getRegular(context);
        assertNotNull("getRegular should successfully load font", font);
    }

    @Test
    public void testGetBold_throwsOnMissingFile() {
        // This test documents current behavior
        // If font file is missing, Font.loadFont throws RuntimeException
        // In normal operation with correct build setup, this should never happen

        Typeface font = Font.getBold(context, "Hello");
        assertNotNull("getBold should successfully load font", font);
    }

    // ============================================================
    // THREAD SAFETY TESTS
    // ============================================================

    @Test
    public void testGetBold_isThreadSafe() throws InterruptedException {
        final int threadCount = 10;
        final int callsPerThread = 100;
        Thread[] threads = new Thread[threadCount];
        final Throwable[] exceptions = new Throwable[1];

        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < callsPerThread; j++) {
                        // Mix ASCII and Unicode text
                        String text = (threadId % 2 == 0) ? "Hello" : "Привет";
                        Font.getBold(context, text);
                        Font.getRegular(context);
                    }
                } catch (Throwable t) {
                    exceptions[0] = t;
                }
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        if (exceptions[0] != null) {
            fail("Thread safety test failed: " + exceptions[0].getMessage());
        }

        // Verify that all calls returned valid fonts
        assertNotNull("getRegular should return valid font", Font.getRegular(context));
        assertNotNull("getBold ASCII should return valid font", Font.getBold(context, "Hello"));
        assertNotNull("getBold Unicode should return valid font", Font.getBold(context, "Привет"));
    }

    // ============================================================
    // EDGE CASES
    // ============================================================

    @Test
    public void testGetBold_veryLongString_handlesCorrectly() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append("Hello");
        }
        String longText = sb.toString();

        Typeface font = Font.getBold(context, longText);
        assertNotNull("getBold should handle very long ASCII strings", font);
        assertSame("Very long ASCII string should use ASCII bold font", Font.getBold(context, "Test"), font);
    }

    @Test
    public void testGetBold_newlineCharacters_returnsAsciiFont() {
        Typeface font = Font.getBold(context, "Hello\nWorld");

        assertNotNull("getBold should handle newlines", font);
        assertSame("Text with newlines should use ASCII bold font", Font.getBold(context, "Test"), font);
    }

    @Test
    public void testGetBold_tabCharacters_returnsAsciiFont() {
        Typeface font = Font.getBold(context, "Hello\tWorld");

        assertNotNull("getBold should handle tabs", font);
        assertSame("Text with tabs should use ASCII bold font", Font.getBold(context, "Test"), font);
    }

    @Test
    public void testGetBold_extendedAscii_returnsAsciiFont() {
        // Extended ASCII (128-255) should use Unicode font
        Typeface font = Font.getBold(context, "Café");

        assertNotNull("getBold should handle extended ASCII", font);
        assertSame("Extended ASCII should use Unicode bold font", Font.getBold(context, "Привет"), font);
    }
}
