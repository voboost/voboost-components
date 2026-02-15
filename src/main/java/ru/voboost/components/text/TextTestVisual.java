package ru.voboost.components.text;

import android.content.Context;
import android.graphics.Bitmap;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ru.voboost.components.i18n.Language;

/**
 * Visual regression tests for Text component Java implementation.
 * Tests visual appearance across different themes, roles, languages, and content variations.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {33})
public class TextTestVisual {

    private Context context;
    private static final int SCREENSHOT_WIDTH = 400;
    private static final int SCREENSHOT_HEIGHT = 100;
    private static final String SCREENSHOT_DIR = "src/main/java/ru/voboost/components/text/Text.screenshots/";

    // Test data
    private Map<Language, String> localizedText;
    private String shortText = "Short";
    private String mediumText = "Medium Length Text";
    private String longText = "This is a very long text that should test text rendering and wrapping capabilities";

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();

        // Create test localized text
        localizedText = new HashMap<>();
        localizedText.put(Language.EN, "Test Text");
        localizedText.put(Language.RU, "Тестовый Текст");
    }

    @Test
    public void testTextControlRoleFreeLight() throws IOException {
        Text textView = new Text(context);
        textView.setText("Control Text");
        textView.setRole(TextRole.CONTROL);
        textView.setTheme(TextTheme.FREE_LIGHT);
        textView.setLanguage(Language.EN);

        captureScreenshot(textView, "text_control_free_light_en.png");
    }

    @Test
    public void testTextControlRoleFreeLightRussian() throws IOException {
        Text textView = new Text(context);
        textView.setText("Текст Управления");
        textView.setRole(TextRole.CONTROL);
        textView.setTheme(TextTheme.FREE_LIGHT);
        textView.setLanguage(Language.RU);

        captureScreenshot(textView, "text_control_free_light_ru.png");
    }

    @Test
    public void testTextControlRoleFreeDark() throws IOException {
        Text textView = new Text(context);
        textView.setText("Control Text");
        textView.setRole(TextRole.CONTROL);
        textView.setTheme(TextTheme.FREE_DARK);
        textView.setLanguage(Language.EN);

        captureScreenshot(textView, "text_control_free_dark_en.png");
    }

    @Test
    public void testTextControlRoleFreeDarkRussian() throws IOException {
        Text textView = new Text(context);
        textView.setText("Текст Управления");
        textView.setRole(TextRole.CONTROL);
        textView.setTheme(TextTheme.FREE_DARK);
        textView.setLanguage(Language.RU);

        captureScreenshot(textView, "text_control_free_dark_ru.png");
    }

    @Test
    public void testTextControlRoleDreamerLight() throws IOException {
        Text textView = new Text(context);
        textView.setText("Control Text");
        textView.setRole(TextRole.CONTROL);
        textView.setTheme(TextTheme.DREAMER_LIGHT);
        textView.setLanguage(Language.EN);

        captureScreenshot(textView, "text_control_dreamer_light_en.png");
    }

    @Test
    public void testTextControlRoleDreamerLightRussian() throws IOException {
        Text textView = new Text(context);
        textView.setText("Текст Управления");
        textView.setRole(TextRole.CONTROL);
        textView.setTheme(TextTheme.DREAMER_LIGHT);
        textView.setLanguage(Language.RU);

        captureScreenshot(textView, "text_control_dreamer_light_ru.png");
    }

    @Test
    public void testTextControlRoleDreamerDark() throws IOException {
        Text textView = new Text(context);
        textView.setText("Control Text");
        textView.setRole(TextRole.CONTROL);
        textView.setTheme(TextTheme.DREAMER_DARK);
        textView.setLanguage(Language.EN);

        captureScreenshot(textView, "text_control_dreamer_dark_en.png");
    }

    @Test
    public void testTextControlRoleDreamerDarkRussian() throws IOException {
        Text textView = new Text(context);
        textView.setText("Текст Управления");
        textView.setRole(TextRole.CONTROL);
        textView.setTheme(TextTheme.DREAMER_DARK);
        textView.setLanguage(Language.RU);

        captureScreenshot(textView, "text_control_dreamer_dark_ru.png");
    }

    @Test
    public void testTextTitleRoleFreeLight() throws IOException {
        Text textView = new Text(context);
        textView.setText("Title Text");
        textView.setRole(TextRole.TITLE);
        textView.setTheme(TextTheme.FREE_LIGHT);
        textView.setLanguage(Language.EN);

        captureScreenshot(textView, "text_title_free_light_en.png");
    }

    @Test
    public void testTextTitleRoleFreeLightRussian() throws IOException {
        Text textView = new Text(context);
        textView.setText("Заголовок");
        textView.setRole(TextRole.TITLE);
        textView.setTheme(TextTheme.FREE_LIGHT);
        textView.setLanguage(Language.RU);

        captureScreenshot(textView, "text_title_free_light_ru.png");
    }

    @Test
    public void testTextTitleRoleFreeDark() throws IOException {
        Text textView = new Text(context);
        textView.setText("Title Text");
        textView.setRole(TextRole.TITLE);
        textView.setTheme(TextTheme.FREE_DARK);
        textView.setLanguage(Language.EN);

        captureScreenshot(textView, "text_title_free_dark_en.png");
    }

    @Test
    public void testTextTitleRoleFreeDarkRussian() throws IOException {
        Text textView = new Text(context);
        textView.setText("Заголовок");
        textView.setRole(TextRole.TITLE);
        textView.setTheme(TextTheme.FREE_DARK);
        textView.setLanguage(Language.RU);

        captureScreenshot(textView, "text_title_free_dark_ru.png");
    }

    @Test
    public void testTextTitleRoleDreamerLight() throws IOException {
        Text textView = new Text(context);
        textView.setText("Title Text");
        textView.setRole(TextRole.TITLE);
        textView.setTheme(TextTheme.DREAMER_LIGHT);
        textView.setLanguage(Language.EN);

        captureScreenshot(textView, "text_title_dreamer_light_en.png");
    }

    @Test
    public void testTextTitleRoleDreamerLightRussian() throws IOException {
        Text textView = new Text(context);
        textView.setText("Заголовок");
        textView.setRole(TextRole.TITLE);
        textView.setTheme(TextTheme.DREAMER_LIGHT);
        textView.setLanguage(Language.RU);

        captureScreenshot(textView, "text_title_dreamer_light_ru.png");
    }

    @Test
    public void testTextTitleRoleDreamerDark() throws IOException {
        Text textView = new Text(context);
        textView.setText("Title Text");
        textView.setRole(TextRole.TITLE);
        textView.setTheme(TextTheme.DREAMER_DARK);
        textView.setLanguage(Language.EN);

        captureScreenshot(textView, "text_title_dreamer_dark_en.png");
    }

    @Test
    public void testTextTitleRoleDreamerDarkRussian() throws IOException {
        Text textView = new Text(context);
        textView.setText("Заголовок");
        textView.setRole(TextRole.TITLE);
        textView.setTheme(TextTheme.DREAMER_DARK);
        textView.setLanguage(Language.RU);

        captureScreenshot(textView, "text_title_dreamer_dark_ru.png");
    }

    @Test
    public void testTextLocalizedEnglish() throws IOException {
        Text textView = new Text(context);
        textView.setText(localizedText);
        textView.setRole(TextRole.CONTROL);
        textView.setTheme(TextTheme.FREE_LIGHT);
        textView.setLanguage(Language.EN);

        captureScreenshot(textView, "text_localized_en.png");
    }

    @Test
    public void testTextLocalizedRussian() throws IOException {
        Text textView = new Text(context);
        textView.setText(localizedText);
        textView.setRole(TextRole.CONTROL);
        textView.setTheme(TextTheme.FREE_LIGHT);
        textView.setLanguage(Language.RU);

        captureScreenshot(textView, "text_localized_ru.png");
    }

    @Test
    public void testTextShortContent() throws IOException {
        Text textView = new Text(context);
        textView.setText(shortText);
        textView.setRole(TextRole.CONTROL);
        textView.setTheme(TextTheme.FREE_LIGHT);
        textView.setLanguage(Language.EN);

        captureScreenshot(textView, "text_short_content.png");
    }

    @Test
    public void testTextMediumContent() throws IOException {
        Text textView = new Text(context);
        textView.setText(mediumText);
        textView.setRole(TextRole.CONTROL);
        textView.setTheme(TextTheme.FREE_LIGHT);
        textView.setLanguage(Language.EN);

        captureScreenshot(textView, "text_medium_content.png");
    }

    @Test
    public void testTextLongContent() throws IOException {
        Text textView = new Text(context);
        textView.setText(longText);
        textView.setRole(TextRole.CONTROL);
        textView.setTheme(TextTheme.FREE_LIGHT);
        textView.setLanguage(Language.EN);

        // Use larger canvas for long text
        captureScreenshot(textView, "text_long_content.png", 800, 150);
    }

    @Test
    public void testTextPositionLeft() throws IOException {
        Text textView = new Text(context);
        textView.setText("Position Test");
        textView.setRole(TextRole.CONTROL);
        textView.setTheme(TextTheme.FREE_LIGHT);
        textView.setLanguage(Language.EN);
        textView.setTextAlign(Paint.Align.LEFT);

        captureScreenshot(textView, "text_position_left.png");
    }

    @Test
    public void testTextPositionCenter() throws IOException {
        Text textView = new Text(context);
        textView.setText("Position Test");
        textView.setRole(TextRole.CONTROL);
        textView.setTheme(TextTheme.FREE_LIGHT);
        textView.setLanguage(Language.EN);
        textView.setTextAlign(Paint.Align.CENTER);

        captureScreenshot(textView, "text_position_center.png");
    }

    @Test
    public void testTextPositionRight() throws IOException {
        Text textView = new Text(context);
        textView.setText("Position Test");
        textView.setRole(TextRole.CONTROL);
        textView.setTheme(TextTheme.FREE_LIGHT);
        textView.setLanguage(Language.EN);
        textView.setTextAlign(Paint.Align.RIGHT);

        captureScreenshot(textView, "text_position_right.png");
    }

    @Test
    public void testTextCustomPosition() throws IOException {
        Text textView = new Text(context);
        textView.setText("Custom Position");
        textView.setRole(TextRole.CONTROL);
        textView.setTheme(TextTheme.FREE_LIGHT);
        textView.setLanguage(Language.EN);
        textView.setPosition(50, 50);

        captureScreenshot(textView, "text_custom_position.png");
    }

    @Test
    public void testTextCustomColor() throws IOException {
        Text textView = new Text(context);
        textView.setText("Custom Color");
        textView.setRole(TextRole.CONTROL);
        textView.setTheme(TextTheme.FREE_LIGHT);
        textView.setLanguage(Language.EN);
        textView.setColor(Color.RED);

        captureScreenshot(textView, "text_custom_color.png");
    }

    @Test
    public void testTextEmpty() throws IOException {
        Text textView = new Text(context);
        textView.setText("");
        textView.setRole(TextRole.CONTROL);
        textView.setTheme(TextTheme.FREE_LIGHT);
        textView.setLanguage(Language.EN);

        captureScreenshot(textView, "text_empty.png");
    }

    @Test
    public void testTextSpecialCharacters() throws IOException {
        Text textView = new Text(context);
        textView.setText("Special: !@#$%^&*()");
        textView.setRole(TextRole.CONTROL);
        textView.setTheme(TextTheme.FREE_LIGHT);
        textView.setLanguage(Language.EN);

        captureScreenshot(textView, "text_special_chars.png");
    }

    /**
     * Capture a screenshot of the Text component and save it to the screenshots directory.
     */
    private void captureScreenshot(Text textView, String filename) throws IOException {
        captureScreenshot(textView, filename, SCREENSHOT_WIDTH, SCREENSHOT_HEIGHT);
    }

    /**
     * Capture a screenshot of the Text component with custom dimensions and save it.
     */
    private void captureScreenshot(Text textView, String filename, int width, int height) throws IOException {
        // Measure the view
        textView.measure(
            View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
        );
        textView.layout(0, 0, width, height);

        // Create a bitmap and canvas
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw background
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.WHITE);
        canvas.drawRect(0, 0, width, height, backgroundPaint);

        // Draw the text view
        textView.draw(canvas);

        // Save the bitmap
        File screenshotDir = new File(SCREENSHOT_DIR);
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs();
        }

        File file = new File(screenshotDir, filename);
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        out.close();

        // Clean up
        bitmap.recycle();
    }
}
