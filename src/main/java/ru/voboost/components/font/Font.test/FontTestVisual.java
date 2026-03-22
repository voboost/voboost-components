package ru.voboost.components.font;

import static com.github.takahirom.roborazzi.RoborazziKt.captureRoboImage;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.github.takahirom.roborazzi.RoborazziOptions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

import ru.voboost.components.font.Font;

/**
 * Visual regression tests for Font component using Roborazzi.
 *
 * Tests font rendering for:
 * - Regular font (various languages and character sets)
 * - Bold ASCII font (English text, numbers, special characters)
 * - Bold Unicode font (Russian, Chinese, Japanese, Arabic, emoji, mixed)
 * - Different text sizes
 *
 * Follows the same pattern as RadioTestVisual (reference component).
 */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
        sdk = {33},
        qualifiers = "w1920dp-h720dp-land-mdpi")
public class FontTestVisual {

    private static final String SCREENSHOT_BASE_PATH =
            "src/main/java/ru/voboost/components/font/Font.screenshots";

    private Context context;
    private FrameLayout container;
    private Activity activity;

    @Before
    public void setUp() {
        // Create an Activity to attach views to (required for Roborazzi screenshot capture)
        ActivityController<Activity> controller = Robolectric.buildActivity(Activity.class);
        controller.create().start().resume();
        activity = controller.get();
        context = activity;
        container = new FrameLayout(context);
        activity.setContentView(container);
        Font.clearCache();
    }

    private String getScreenshotPath() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String testMethodName = "unknown";
        for (int i = 2; i < stackTrace.length; i++) {
            String methodName = stackTrace[i].getMethodName();
            // Test methods start with "font_"
            if (methodName.startsWith("font_")) {
                testMethodName = methodName;
                break;
            }
        }
        return SCREENSHOT_BASE_PATH + "/" + testMethodName + ".png";
    }

    /**
     * Creates a TextView with the specified text and font, configured for Roborazzi screenshot.
     */
    private TextView createFontView(String text, Typeface typeface, float textSize) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTypeface(typeface);
        textView.setTextSize(textSize);
        textView.setTextColor(Color.BLACK);
        textView.setPadding(40, 40, 40, 40);

        // Set layout parameters for proper rendering
        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);

        // Force measurement and layout for screenshot capture
        int widthMeasureSpec =
                android.view.View.MeasureSpec.makeMeasureSpec(
                        1920, android.view.View.MeasureSpec.AT_MOST);
        int heightMeasureSpec =
                android.view.View.MeasureSpec.makeMeasureSpec(
                        720, android.view.View.MeasureSpec.AT_MOST);

        textView.measure(widthMeasureSpec, heightMeasureSpec);

        // Ensure we have valid measured dimensions
        int measuredWidth = textView.getMeasuredWidth();
        int measuredHeight = textView.getMeasuredHeight();

        if (measuredWidth <= 0 || measuredHeight <= 0) {
            throw new IllegalArgumentException(
                    "TextView has invalid measured dimensions: "
                            + measuredWidth
                            + "x"
                            + measuredHeight);
        }

        textView.layout(0, 0, measuredWidth, measuredHeight);

        // Add to container (attached to Activity) for Roborazzi screenshot capture
        container.removeAllViews();
        container.addView(textView);

        return textView;
    }

    // ============================================================
    // REGULAR FONT TESTS
    // ============================================================

    @Test
    public void font_regular_english() {
        Typeface font = Font.getRegular(context);
        TextView view = createFontView("The quick brown fox jumps over the lazy dog.", font, 48);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_regular_russian() {
        Typeface font = Font.getRegular(context);
        TextView view = createFontView("Быстрая коричневая лиса перепрыгивает через ленивую собаку.", font, 48);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_regular_numbers() {
        Typeface font = Font.getRegular(context);
        TextView view = createFontView("0123456789", font, 64);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_regular_chinese() {
        Typeface font = Font.getRegular(context);
        TextView view = createFontView("你好世界", font, 48);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    // ============================================================
    // BOLD ASCII FONT TESTS
    // ============================================================

    @Test
    public void font_bold_ascii_english() {
        Typeface font = Font.getBold(context, "Hello");
        TextView view = createFontView("The quick brown fox jumps over the lazy dog.", font, 48);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_bold_ascii_numbers() {
        Typeface font = Font.getBold(context, "123");
        TextView view = createFontView("0123456789", font, 64);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_bold_ascii_special() {
        Typeface font = Font.getBold(context, "Hello!");
        TextView view = createFontView("Hello! @#$%^&*()_+-=[]{}|;':\",./<>?", font, 36);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_bold_ascii_lowercase() {
        Typeface font = Font.getBold(context, "abc");
        TextView view = createFontView("abcdefghijklmnopqrstuvwxyz", font, 48);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_bold_ascii_uppercase() {
        Typeface font = Font.getBold(context, "ABC");
        TextView view = createFontView("ABCDEFGHIJKLMNOPQRSTUVWXYZ", font, 48);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    // ============================================================
    // BOLD UNICODE FONT TESTS
    // ============================================================

    @Test
    public void font_bold_unicode_russian() {
        Typeface font = Font.getBold(context, "Привет");
        TextView view = createFontView("Быстрая коричневая лиса перепрыгивает через ленивую собаку.", font, 48);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_bold_unicode_chinese() {
        Typeface font = Font.getBold(context, "你好");
        TextView view = createFontView("你好世界！这是一个测试。", font, 48);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_bold_unicode_japanese() {
        Typeface font = Font.getBold(context, "こんにちは");
        TextView view = createFontView("こんにちは世界！これはテストです。", font, 48);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_bold_unicode_arabic() {
        Typeface font = Font.getBold(context, "مرحبا");
        TextView view = createFontView("مرحبا بالعالم! هذا اختبار.", font, 48);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_bold_unicode_emoji() {
        Typeface font = Font.getBold(context, "👋");
        TextView view = createFontView("Hello 👋 World 🌍 Test 🎉", font, 48);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_bold_unicode_mixed() {
        Typeface font = Font.getBold(context, "HelloПривет");
        TextView view = createFontView("Hello Привет 你世界 🌍", font, 48);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    // ============================================================
    // NULL AND EMPTY STRING TESTS
    // ============================================================

    @Test
    public void font_bold_null() {
        // null should return ASCII bold font
        Typeface font = Font.getBold(context, null);
        TextView view = createFontView("Test", font, 48);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_bold_empty() {
        // empty string should return ASCII bold font
        Typeface font = Font.getBold(context, "");
        TextView view = createFontView("Test", font, 48);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    // ============================================================
    // FONT SIZE TESTS
    // ============================================================

    @Test
    public void font_regular_size_12() {
        Typeface font = Font.getRegular(context);
        TextView view = createFontView("Font Size Test", font, 12);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_regular_size_24() {
        Typeface font = Font.getRegular(context);
        TextView view = createFontView("Font Size Test", font, 24);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_regular_size_36() {
        Typeface font = Font.getRegular(context);
        TextView view = createFontView("Font Size Test", font, 36);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_regular_size_48() {
        Typeface font = Font.getRegular(context);
        TextView view = createFontView("Font Size Test", font, 48);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_regular_size_72() {
        Typeface font = Font.getRegular(context);
        TextView view = createFontView("Font Size Test", font, 72);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_bold_ascii_size_12() {
        Typeface font = Font.getBold(context, "Size");
        TextView view = createFontView("Bold Size Test", font, 12);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_bold_ascii_size_24() {
        Typeface font = Font.getBold(context, "Size");
        TextView view = createFontView("Bold Size Test", font, 24);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_bold_ascii_size_36() {
        Typeface font = Font.getBold(context, "Size");
        TextView view = createFontView("Bold Size Test", font, 36);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_bold_ascii_size_48() {
        Typeface font = Font.getBold(context, "Size");
        TextView view = createFontView("Bold Size Test", font, 48);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_bold_ascii_size_72() {
        Typeface font = Font.getBold(context, "Size");
        TextView view = createFontView("Bold Size Test", font, 72);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_bold_unicode_size_12() {
        Typeface font = Font.getBold(context, "Размер");
        TextView view = createFontView("Тест размера шрифта", font, 12);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_bold_unicode_size_24() {
        Typeface font = Font.getBold(context, "Размер");
        TextView view = createFontView("Тест размера шрифта", font, 24);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_bold_unicode_size_36() {
        Typeface font = Font.getBold(context, "Размер");
        TextView view = createFontView("Тест размера шрифта", font, 36);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_bold_unicode_size_48() {
        Typeface font = Font.getBold(context, "Размер");
        TextView view = createFontView("Тест размера шрифта", font, 48);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }

    @Test
    public void font_bold_unicode_size_72() {
        Typeface font = Font.getBold(context, "Размер");
        TextView view = createFontView("Тест размера шрифта", font, 72);
        captureRoboImage(view, getScreenshotPath(), new RoborazziOptions());
    }
}
