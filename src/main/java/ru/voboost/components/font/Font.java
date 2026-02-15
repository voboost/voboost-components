package ru.voboost.components.font;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Font component provides the project typeface for all components.
 *
 * <p>Follows BEM structure with co-located font files in the same package as the Font class.
 *
 * <p>Font files are loaded from assets via build.gradle.kts configuration:
 * <pre>
 * sourceSets {
 *     getByName("main") {
 *         assets {
 *             srcDir("src/main/java") // BEM structure: fonts co-located with Font class
 *         }
 *     }
 * }
 * </pre>
 *
 * <p>Bold fonts come in two variants:
 * <ul>
 *   <li>Font_bold_ascii.ttf — for ASCII-only text (English, numbers)</li>
 *   <li>Font_bold_unicode.ttf — for text with non-ASCII characters (Russian, etc.)</li>
 * </ul>
 *
 * <p>Use {@link #getBold(Context, String)} to automatically select the correct
 * bold font based on the text content.
 *
 * <p>Caches the Typeface instances to avoid repeated loading.
 *
 * <p>If the font file is not available, throws RuntimeException.
 * There is NO fallback to system default fonts.
 */
public final class Font {

    private static Typeface regular;
    private static Typeface boldAscii;
    private static Typeface boldUnicode;

    private Font() {
        // Prevent instantiation
    }

    /**
     * Returns the regular-weight typeface.
     *
     * @param context Android context (used for cache directory access)
     * @return Regular typeface
     * @throws RuntimeException if the font file is not available
     */
    public static Typeface getRegular(Context context) {
        if (regular == null) {
            regular = loadFont(context, "Font.ttf");
        }
        return regular;
    }

    /**
     * Returns the bold-weight typeface appropriate for the given text.
     *
     * <p>If the text contains only ASCII characters, returns the ASCII bold font.
     * If the text contains any non-ASCII characters (e.g., Cyrillic, CJK),
     * returns the Unicode bold font which renders these characters correctly.
     *
     * @param context Android context (used for cache directory access)
     * @param text the text that will be rendered with this font
     * @return Bold typeface appropriate for the text content
     * @throws RuntimeException if the font file is not available
     */
    public static Typeface getBold(Context context, String text) {
        if (isAsciiOnly(text)) {
            if (boldAscii == null) {
                boldAscii = loadFont(context, "Font_bold_ascii.ttf");
            }
            return boldAscii;
        } else {
            if (boldUnicode == null) {
                boldUnicode = loadFont(context, "Font_bold_unicode.ttf");
            }

            return boldUnicode;
        }
    }

    /**
     * Clears the font cache. Call this in tests if needed.
     */
    public static void clearCache() {
        regular = null;
        boldAscii = null;
        boldUnicode = null;
    }

    /**
     * Checks if a string contains only ASCII characters (0-127).
     *
     * @param text the text to check
     * @return true if the text is null, empty, or contains only ASCII characters
     */
    private static boolean isAsciiOnly(String text) {
        if (text == null || text.isEmpty()) {
            return true;
        }

        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) > 127) {
                return false;
            }
        }

        return true;
    }

    /**
     * Loads a font from assets.
     *
     * <p>Font files are loaded from assets (BEM structure: src/main/java is added to assets.srcDirs).
     *
     * @param context Android context (used for assets access)
     * @param fontName Name of the font file (e.g., "Font.ttf")
     * @return the loaded Typeface
     * @throws RuntimeException if the font file cannot be loaded
     */
    private static Typeface loadFont(Context context, String fontName) {
        try {
            // Load font from assets (BEM structure: src/main/java is added to assets.srcDirs)
            return Typeface.createFromAsset(context.getAssets(), "ru/voboost/components/font/" + fontName);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to load " + fontName + " from assets. "
                            + "Ensure the font file is available at src/main/java/ru/voboost/components/font/" + fontName
                            + " and that build.gradle.kts has assets.srcDirs = [\"src/main/java\"]", e);
        }
    }
}
