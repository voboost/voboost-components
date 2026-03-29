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
        assertEquals("Initial role should be CONTROL", TextRole.CONTROL, textView.getRole());
        // Note: theme and language are null by default, must be explicitly set
        assertNull("Initial theme should be null", textView.getTheme());
        assertNull("Initial language should be null", textView.getLanguage());
    }

    @Test
    public void testSetText() {
        // Test setting static text
        textView.setText("Hello World");
        assertEquals("Text should be set correctly", "Hello World", textView.getText().toString());

        // Test setting empty text
        textView.setText("");
        assertEquals("Text should be empty", "", textView.getText().toString());
    }

    @Test
    public void testSetLocalizedText() {
        // Set language first
        textView.setLanguage(Language.EN);

        // Set localized text
        textView.setText(localizedText);
        assertEquals("Text should be in English", "Test Text", textView.getText().toString());

        // Change language to Russian
        textView.setLanguage(Language.RU);
        assertEquals("Text should be in Russian", "Тестовый Текст", textView.getText().toString());

        // Change language back to English
        textView.setLanguage(Language.EN);
        assertEquals("Text should be in English", "Test Text", textView.getText().toString());
    }

    @Test
    public void testSetRole() {
        // Test setting CONTROL role
        textView.setRole(TextRole.CONTROL);
        assertEquals("Role should be CONTROL", TextRole.CONTROL, textView.getRole());

        // Test setting TITLE role
        textView.setRole(TextRole.TITLE);
        assertEquals("Role should be TITLE", TextRole.TITLE, textView.getRole());

        // Test setting null role should throw IllegalArgumentException
        try {
            textView.setRole(null);
            fail("setRole(null) should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Role cannot be null", e.getMessage());
        }
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

        // Test setting null theme should throw IllegalArgumentException
        try {
            textView.setTheme(null);
            fail("setTheme(null) should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Theme cannot be null", e.getMessage());
        }
    }

    @Test
    public void testSetLanguage() {
        // Test setting EN language
        textView.setLanguage(Language.EN);
        assertEquals("Language should be EN", Language.EN, textView.getLanguage());

        // Test setting RU language
        textView.setLanguage(Language.RU);
        assertEquals("Language should be RU", Language.RU, textView.getLanguage());

        // Test setting null language should throw IllegalArgumentException
        try {
            textView.setLanguage(null);
            fail("setLanguage(null) should throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Language cannot be null", e.getMessage());
        }
    }

    @Test
    public void testDraw() {
        // Set layout params to avoid NPE when changing text
        textView.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Set some text
        textView.setText("Test Text");

        // Create a canvas with a bitmap for drawing
        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(200, 50, android.graphics.Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Test drawing (should not crash)
        try {
            textView.draw(canvas);
            // If we reach here, drawing succeeded
            assertTrue("Text should draw successfully", true);
        } catch (Exception e) {
            fail("Text drawing should not throw exception: " + e.getMessage());
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

        // Create a canvas with a bitmap for drawing
        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(200, 50, android.graphics.Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

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
        // Set layout params to avoid NPE when changing text
        textView.setLayoutParams(new android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
        ));

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

        // Test measuring with empty text
        textView.setText("");
        textView.measure(widthSpec, heightSpec);

        // Should handle empty text gracefully
        assertTrue("Measured width should be non-negative", textView.getMeasuredWidth() >= 0);
        assertTrue("Measured height should be non-negative", textView.getMeasuredHeight() >= 0);
    }

    @Test
    public void testStatePersistenceWithNullState() {
        // Verify text view handles state correctly
        assertNotNull("Text should be initialized", textView);
        assertEquals("Text should be default", "", textView.getText() != null ? textView.getText().toString() : "");
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
            CharSequence text = textView.getText();
            assertNotNull("Text should not be null for language " + language, text);
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
        assertEquals("Should display English for EN language", "English Only", textView.getText().toString());

        // Should keep existing text when switching to RU (not available)
        textView.setLanguage(Language.RU);
        assertEquals(
                "Should keep existing text for missing language",
                "English Only",
                textView.getText().toString());
    }

    @Test
    public void testEmptyLocalizedText() {
        // Test with empty localized text map
        Map<Language, String> emptyText = new HashMap<>();
        textView.setText(emptyText);

        // Should keep existing text (empty by default)
        assertEquals("Should keep existing text for empty map", "", textView.getText() != null ? textView.getText().toString() : "");
    }

    @Test
    public void testNullLocalizedText() {
        // Set some text first
        textView.setText("Initial Text");

        // Then set null localized text
        textView.setText((Map<Language, String>) null);

        // Should keep existing text
        assertEquals("Should keep existing text for null map", "Initial Text", textView.getText().toString());
    }

}
