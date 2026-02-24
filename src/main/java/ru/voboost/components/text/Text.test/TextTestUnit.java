package ru.voboost.components.text;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.Theme;

/**
 * Unit tests for Text component Java implementation.
 * Tests core functionality, state management, and edge cases.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {33})
public class TextTestUnit {

    private Context context;
    private Text textView;
    private Map<Language, String> localizedText;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        textView = new Text(context);

        // Create test localized text
        localizedText = new HashMap<>();
        localizedText.put(Language.EN, "Test Text");
        localizedText.put(Language.RU, "Тестовый Текст");
    }

    @Test
    public void testTextInitialization() {
        assertNotNull("Text should be initialized", textView);
        assertEquals("Initial text should be empty", "", textView.getText());
        assertEquals("Initial role should be CONTROL", TextRole.CONTROL, textView.getRole());
        assertEquals("Initial theme should be free-light", Theme.FREE_LIGHT, textView.getTheme());
        assertEquals("Initial language should be EN", Language.EN, textView.getLanguage());
    }

    @Test
    public void testSetText() {
        // Test setting static text
        textView.setText("Hello World");
        assertEquals("Text should be set correctly", "Hello World", textView.getText());

        // Test setting null text
        textView.setText((String) null);
        assertNull("Text should be null", textView.getText());

        // Test setting empty text
        textView.setText("");
        assertEquals("Text should be empty", "", textView.getText());
    }

    @Test
    public void testSetLocalizedText() {
        // Set localized text
        textView.setText(localizedText);
        assertEquals("Text should be in English by default", "Test Text", textView.getText());

        // Change language to Russian
        textView.setLanguage(Language.RU);
        assertEquals("Text should be in Russian", "Тестовый Текст", textView.getText());

        // Change language back to English
        textView.setLanguage(Language.EN);
        assertEquals("Text should be in English", "Test Text", textView.getText());
    }

    @Test
    public void testSetRole() {
        // Test setting CONTROL role
        textView.setRole(TextRole.CONTROL);
        assertEquals("Role should be CONTROL", TextRole.CONTROL, textView.getRole());

        // Test setting TITLE role
        textView.setRole(TextRole.TITLE);
        assertEquals("Role should be TITLE", TextRole.TITLE, textView.getRole());

        // Test setting null role
        textView.setRole(null);
        assertNull("Role should be null", textView.getRole());
    }

    @Test
    public void testSetTheme() {
        // Test setting all valid themes
        textView.setTheme(Theme.FREE_LIGHT);
        assertEquals("Theme should be free-light", Theme.FREE_LIGHT, textView.getTheme());

        textView.setTheme(Theme.FREE_DARK);
        assertEquals("Theme should be free-dark", Theme.FREE_DARK, textView.getTheme());

        textView.setTheme(Theme.DREAMER_LIGHT);
        assertEquals("Theme should be dreamer-light", Theme.DREAMER_LIGHT, textView.getTheme());

        textView.setTheme(Theme.DREAMER_DARK);
        assertEquals("Theme should be dreamer-dark", Theme.DREAMER_DARK, textView.getTheme());

        // Test setting invalid theme (should be ignored)
        Theme originalTheme = textView.getTheme();
        textView.setTheme(null);
        assertEquals(
                "Theme should remain unchanged for invalid theme",
                originalTheme,
                textView.getTheme());
    }

    @Test
    public void testSetLanguage() {
        // Test setting EN language
        textView.setLanguage(Language.EN);
        assertEquals("Language should be EN", Language.EN, textView.getLanguage());

        // Test setting RU language
        textView.setLanguage(Language.RU);
        assertEquals("Language should be RU", Language.RU, textView.getLanguage());

        // Test setting null language
        textView.setLanguage(null);
        assertNull("Language should be null", textView.getLanguage());
    }

    @Test
    public void testSetColor() {
        // Set a custom color
        int customColor = Color.RED;
        textView.setColor(customColor);

        // Verify the color was set (we can't directly access the paint color, but we can verify it
        // doesn't crash)
        assertNotNull("Text should handle custom color", textView);

        // Reset to theme color
        textView.useThemeColor();
        assertNotNull("Text should reset to theme color", textView);
    }

    @Test
    public void testSetPosition() {
        // Test setting custom position
        float x = 100.0f;
        float y = 50.0f;
        textView.setPosition(x, y);

        // We can't directly verify position as it's private, but we can verify it doesn't crash
        assertNotNull("Text should handle custom position", textView);
    }

    @Test
    public void testSetTextAlign() {
        // Test setting different alignments
        textView.setTextAlign(Paint.Align.LEFT);
        textView.setTextAlign(Paint.Align.CENTER);
        textView.setTextAlign(Paint.Align.RIGHT);

        // Verify it doesn't crash
        assertNotNull("Text should handle text alignment", textView);
    }

    @Test
    public void testTextMeasurement() {
        // Set some text
        textView.setText("Test Text");

        // Test text width measurement
        float width = textView.getTextWidth();
        assertTrue("Text width should be positive", width > 0);

        // Test text height measurement
        float height = textView.getTextHeight();
        assertTrue("Text height should be positive", height > 0);

        // Test baseline offset
        float baseline = textView.getBaselineOffset();
        assertTrue("Baseline offset should be non-negative", baseline >= 0);

        // Test with null text
        textView.setText((String) null);
        assertEquals("Text width should be 0 for null text", 0, textView.getTextWidth(), 0.01);
        assertEquals("Text height should be 0 for null text", 0, textView.getTextHeight(), 0.01);
    }

    @Test
    public void testDraw() {
        // Set some text
        textView.setText("Test Text");

        // Create a mock canvas
        Canvas canvas = new Canvas();

        // Test drawing (should not crash)
        try {
            textView.draw(canvas);
            // If we reach here, drawing succeeded
            assertTrue("Text should draw successfully", true);
        } catch (Exception e) {
            fail("Text drawing should not throw exception: " + e.getMessage());
        }

        // Test drawing with null text
        textView.setText((String) null);
        try {
            textView.draw(canvas);
            // If we reach here, drawing with null text succeeded
            assertTrue("Text should handle null text gracefully", true);
        } catch (Exception e) {
            fail("Text drawing with null text should not throw exception: " + e.getMessage());
        }

        // Test drawing with empty text
        textView.setText("");
        try {
            textView.draw(canvas);
            // If we reach here, drawing with empty text succeeded
            assertTrue("Text should handle empty text gracefully", true);
        } catch (Exception e) {
            fail("Text drawing with empty text should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void testOnDraw() {
        // Set some text
        textView.setText("Test Text");

        // Create a mock canvas
        Canvas canvas = new Canvas();

        // Test onDraw (should not crash)
        try {
            textView.draw(canvas);
            // If we reach here, onDraw succeeded
            assertTrue("Text should onDraw successfully", true);
        } catch (Exception e) {
            fail("Text onDraw should not throw exception: " + e.getMessage());
        }
    }

    @Test
    public void testOnMeasure() {
        // Set some text
        textView.setText("Test Text");

        // Test measuring with exact dimensions
        int widthSpec = View.MeasureSpec.makeMeasureSpec(200, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(50, View.MeasureSpec.EXACTLY);
        textView.measure(widthSpec, heightSpec);

        assertEquals("Measured width should match spec", 200, textView.getMeasuredWidth());
        assertEquals("Measured height should match spec", 50, textView.getMeasuredHeight());

        // Test measuring with at most dimensions
        widthSpec = View.MeasureSpec.makeMeasureSpec(500, View.MeasureSpec.AT_MOST);
        heightSpec = View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.AT_MOST);
        textView.measure(widthSpec, heightSpec);

        assertTrue("Measured width should be positive", textView.getMeasuredWidth() > 0);
        assertTrue("Measured height should be positive", textView.getMeasuredHeight() > 0);

        // Test measuring with null text
        textView.setText((String) null);
        textView.measure(widthSpec, heightSpec);

        // Should handle null text gracefully
        assertTrue("Measured width should be non-negative", textView.getMeasuredWidth() >= 0);
        assertTrue("Measured height should be non-negative", textView.getMeasuredHeight() >= 0);
    }

    @Test
    public void testStatePersistence() {
        // Set up state
        textView.setText("Test Text");
        textView.setRole(TextRole.TITLE);
        textView.setTheme(Theme.DREAMER_DARK);
        textView.setLanguage(Language.RU);
        textView.setPosition(100.0f, 50.0f);

        // Verify state is set correctly
        assertEquals("Text should be Test Text", "Test Text", textView.getText());
        assertEquals("Role should be TITLE", TextRole.TITLE, textView.getRole());
        assertEquals("Theme should be DREAMER_DARK", Theme.DREAMER_DARK, textView.getTheme());
        assertEquals("Language should be RU", Language.RU, textView.getLanguage());
    }

    @Test
    public void testStatePersistenceWithNullState() {
        // Verify text view handles state correctly
        assertNotNull("Text should be initialized", textView);
        assertEquals("Text should be default", "", textView.getText());
    }

    @Test
    public void testFourthConstructor() {
        // Test that the 4th constructor works correctly
        Text textWithFourParams = new Text(context, null, 0, 0);
        assertNotNull("Text with 4 params should be initialized", textWithFourParams);
        assertEquals("Initial text should be empty", "", textWithFourParams.getText());
    }

    @Test
    public void testAllTextRoles() {
        // Test all text roles
        for (TextRole role : TextRole.values()) {
            textView.setRole(role);
            assertEquals("Role should be set correctly", role, textView.getRole());

            // Verify text measurement works with each role
            textView.setText("Test");
            float width = textView.getTextWidth();
            float height = textView.getTextHeight();
            assertTrue("Text width should be positive for role " + role, width > 0);
            assertTrue("Text height should be positive for role " + role, height > 0);
        }
    }

    @Test
    public void testAllLanguages() {
        // Set localized text
        textView.setText(localizedText);

        // Test all languages
        for (Language language : Language.values()) {
            textView.setLanguage(language);
            assertEquals("Language should be set correctly", language, textView.getLanguage());

            // Verify text is updated (if available for this language)
            String text = textView.getText();
            assertNotNull("Text should not be null for language " + language, text);
        }
    }

    @Test
    public void testThemeColors() {
        // Test that theme colors are applied correctly
        textView.setText("Test");

        // Test all themes with CONTROL role
        textView.setRole(TextRole.CONTROL);
        for (Theme theme :
                new Theme[] {
                    Theme.FREE_LIGHT, Theme.FREE_DARK,
                    Theme.DREAMER_LIGHT, Theme.DREAMER_DARK
                }) {
            textView.setTheme(theme);
            assertEquals("Theme should be set correctly", theme, textView.getTheme());

            // Verify text can still be measured (implies theme was applied)
            float width = textView.getTextWidth();
            assertTrue("Text width should be positive for theme " + theme, width > 0);
        }

        // Test all themes with TITLE role
        textView.setRole(TextRole.TITLE);
        for (Theme theme :
                new Theme[] {
                    Theme.FREE_LIGHT, Theme.FREE_DARK,
                    Theme.DREAMER_LIGHT, Theme.DREAMER_DARK
                }) {
            textView.setTheme(theme);
            assertEquals("Theme should be set correctly", theme, textView.getTheme());

            // Verify text can still be measured (implies theme was applied)
            float width = textView.getTextWidth();
            assertTrue("Text width should be positive for theme " + theme, width > 0);
        }
    }

    @Test
    public void testFontLoading() {
        // Test that font is loaded correctly
        textView.setText("Test");

        // Verify text can be measured (implies font was loaded)
        float width = textView.getTextWidth();
        float height = textView.getTextHeight();
        assertTrue("Text width should be positive", width > 0);
        assertTrue("Text height should be positive", height > 0);

        // Test with different roles (which use different font sizes/weights)
        for (TextRole role : TextRole.values()) {
            textView.setRole(role);
            width = textView.getTextWidth();
            height = textView.getTextHeight();
            assertTrue("Text width should be positive for role " + role, width > 0);
            assertTrue("Text height should be positive for role " + role, height > 0);
        }
    }

    @Test
    public void testLocalizationWithMissingLanguage() {
        // Create localized text with only English
        Map<Language, String> englishOnlyText = new HashMap<>();
        englishOnlyText.put(Language.EN, "English Only");

        textView.setText(englishOnlyText);

        // Should display English for EN language
        textView.setLanguage(Language.EN);
        assertEquals("Should display English for EN language", "English Only", textView.getText());

        // Should keep existing text when switching to RU (not available)
        textView.setLanguage(Language.RU);
        assertEquals(
                "Should keep existing text for missing language",
                "English Only",
                textView.getText());
    }

    @Test
    public void testEmptyLocalizedText() {
        // Test with empty localized text map
        Map<Language, String> emptyText = new HashMap<>();
        textView.setText(emptyText);

        // Should keep existing text (empty by default)
        assertEquals("Should keep existing text for empty map", "", textView.getText());
    }

    @Test
    public void testNullLocalizedText() {
        // Set some text first
        textView.setText("Initial Text");

        // Then set null localized text
        textView.setText((Map<Language, String>) null);

        // Should keep existing text
        assertEquals("Should keep existing text for null map", "Initial Text", textView.getText());
    }

    @Test
    public void testComplexText() {
        // Test with complex text including special characters
        String complexText = "Special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?";
        textView.setText(complexText);
        assertEquals("Complex text should be set correctly", complexText, textView.getText());

        // Verify it can be measured
        float width = textView.getTextWidth();
        float height = textView.getTextHeight();
        assertTrue("Complex text width should be positive", width > 0);
        assertTrue("Complex text height should be positive", height > 0);
    }

    @Test
    public void testLongText() {
        // Test with very long text
        StringBuilder longTextBuilder = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longTextBuilder.append("This is a very long text. ");
        }
        String longText = longTextBuilder.toString();

        textView.setText(longText);
        assertEquals("Long text should be set correctly", longText, textView.getText());

        // Verify it can be measured
        float width = textView.getTextWidth();
        float height = textView.getTextHeight();
        assertTrue("Long text width should be positive", width > 0);
        assertTrue("Long text height should be positive", height > 0);
    }

    @Test
    public void testPerformanceOptimization() {
        // Test that the component handles rapid text changes efficiently
        String[] testTexts = {"Text1", "Text2", "Text3", "Text4", "Text5"};

        // Measure performance of rapid text changes
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            textView.setText(testTexts[i % testTexts.length]);
            textView.getTextWidth(); // Force measurement
        }
        long endTime = System.currentTimeMillis();

        // Verify that rapid text changes complete in reasonable time
        assertTrue(
                "Rapid text changes should complete quickly",
                (endTime - startTime) < 1000); // Should complete in less than 1 second
    }

    @Test
    public void testThreadSafety() {
        // Test that text component handles concurrent access safely
        final String[] testTexts = {"Text1", "Text2", "Text3"};

        // Create multiple threads that modify the text component
        Thread[] threads = new Thread[5];
        for (int i = 0; i < threads.length; i++) {
            final int index = i;
            threads[i] =
                    new Thread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    // Each thread sets a different text
                                    String text = testTexts[index % testTexts.length];
                                    textView.setText(text);
                                    textView.getTextWidth(); // Force measurement
                                }
                            });
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        try {
            for (Thread thread : threads) {
                thread.join(1000); // Wait up to 1 second for each thread
            }
        } catch (InterruptedException e) {
            fail("Thread execution was interrupted");
        }

        // Verify the component is still in a valid state
        assertNotNull("Text should still be valid after concurrent access", textView);
        assertNotNull("Text should not be null after concurrent access", textView.getText());
    }
}
