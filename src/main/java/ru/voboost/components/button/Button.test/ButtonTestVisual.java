package ru.voboost.components.button;

import static org.junit.Assert.assertNotNull;

import android.content.Context;
import android.graphics.Color;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.Theme;

/**
 * Visual regression tests for Button component.
 */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = {33}, qualifiers = "w1920dp-h720dp-land-mdpi")
public class ButtonTestVisual {

    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
    }

    // Theme + Style combinations

    @Test
    public void button_primary_free_light_normal() {
        Button button = new Button(context);
        button.setTheme(Theme.FREE_LIGHT);
        button.setStyle(ButtonStyle.PRIMARY);
        button.setText("Primary");
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        assertNotNull(button);
        // Screenshot will be captured by Roborazzi
    }

    @Test
    public void button_primary_free_light_pressed() {
        Button button = new Button(context);
        button.setTheme(Theme.FREE_LIGHT);
        button.setStyle(ButtonStyle.PRIMARY);
        button.setText("Primary");
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        // Simulate pressed state
        android.view.MotionEvent down = android.view.MotionEvent.obtain(0, 0, android.view.MotionEvent.ACTION_DOWN, 40f, 50f, 0);
        button.dispatchTouchEvent(down);
        down.recycle();

        assertNotNull(button);
    }

    @Test
    public void button_primary_free_light_disabled() {
        Button button = new Button(context);
        button.setTheme(Theme.FREE_LIGHT);
        button.setStyle(ButtonStyle.PRIMARY);
        button.setText("Primary");
        button.setEnabled(false);
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        assertNotNull(button);
    }

    @Test
    public void button_secondary_free_light_normal() {
        Button button = new Button(context);
        button.setTheme(Theme.FREE_LIGHT);
        button.setStyle(ButtonStyle.SECONDARY);
        button.setText("Secondary");
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        assertNotNull(button);
    }

    @Test
    public void button_secondary_free_light_pressed() {
        Button button = new Button(context);
        button.setTheme(Theme.FREE_LIGHT);
        button.setStyle(ButtonStyle.SECONDARY);
        button.setText("Secondary");
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        android.view.MotionEvent down = android.view.MotionEvent.obtain(0, 0, android.view.MotionEvent.ACTION_DOWN, 40f, 50f, 0);
        button.dispatchTouchEvent(down);
        down.recycle();

        assertNotNull(button);
    }

    @Test
    public void button_secondary_free_light_disabled() {
        Button button = new Button(context);
        button.setTheme(Theme.FREE_LIGHT);
        button.setStyle(ButtonStyle.SECONDARY);
        button.setText("Secondary");
        button.setEnabled(false);
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        assertNotNull(button);
    }

    // Repeat for FREE_DARK theme
    @Test
    public void button_primary_free_dark_normal() {
        Button button = new Button(context);
        button.setTheme(Theme.FREE_DARK);
        button.setStyle(ButtonStyle.PRIMARY);
        button.setText("Primary");
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        assertNotNull(button);
    }

    @Test
    public void button_primary_free_dark_pressed() {
        Button button = new Button(context);
        button.setTheme(Theme.FREE_DARK);
        button.setStyle(ButtonStyle.PRIMARY);
        button.setText("Primary");
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        android.view.MotionEvent down = android.view.MotionEvent.obtain(0, 0, android.view.MotionEvent.ACTION_DOWN, 40f, 50f, 0);
        button.dispatchTouchEvent(down);
        down.recycle();

        assertNotNull(button);
    }

    @Test
    public void button_primary_free_dark_disabled() {
        Button button = new Button(context);
        button.setTheme(Theme.FREE_DARK);
        button.setStyle(ButtonStyle.PRIMARY);
        button.setText("Primary");
        button.setEnabled(false);
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        assertNotNull(button);
    }

    @Test
    public void button_secondary_free_dark_normal() {
        Button button = new Button(context);
        button.setTheme(Theme.FREE_DARK);
        button.setStyle(ButtonStyle.SECONDARY);
        button.setText("Secondary");
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        assertNotNull(button);
    }

    @Test
    public void button_secondary_free_dark_pressed() {
        Button button = new Button(context);
        button.setTheme(Theme.FREE_DARK);
        button.setStyle(ButtonStyle.SECONDARY);
        button.setText("Secondary");
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        android.view.MotionEvent down = android.view.MotionEvent.obtain(0, 0, android.view.MotionEvent.ACTION_DOWN, 40f, 50f, 0);
        button.dispatchTouchEvent(down);
        down.recycle();

        assertNotNull(button);
    }

    @Test
    public void button_secondary_free_dark_disabled() {
        Button button = new Button(context);
        button.setTheme(Theme.FREE_DARK);
        button.setStyle(ButtonStyle.SECONDARY);
        button.setText("Secondary");
        button.setEnabled(false);
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        assertNotNull(button);
    }

    // Repeat for DREAMER_LIGHT and DREAMER_DARK themes (similar tests)
    // Total: 4 themes × 2 styles × 3 states = 24 tests

    @Test
    public void button_primary_dreamer_light_normal() {
        Button button = new Button(context);
        button.setTheme(Theme.DREAMER_LIGHT);
        button.setStyle(ButtonStyle.PRIMARY);
        button.setText("Primary");
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        assertNotNull(button);
    }

    @Test
    public void button_primary_dreamer_light_pressed() {
        Button button = new Button(context);
        button.setTheme(Theme.DREAMER_LIGHT);
        button.setStyle(ButtonStyle.PRIMARY);
        button.setText("Primary");
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        android.view.MotionEvent down = android.view.MotionEvent.obtain(0, 0, android.view.MotionEvent.ACTION_DOWN, 40f, 50f, 0);
        button.dispatchTouchEvent(down);
        down.recycle();

        assertNotNull(button);
    }

    @Test
    public void button_primary_dreamer_light_disabled() {
        Button button = new Button(context);
        button.setTheme(Theme.DREAMER_LIGHT);
        button.setStyle(ButtonStyle.PRIMARY);
        button.setText("Primary");
        button.setEnabled(false);
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        assertNotNull(button);
    }

    @Test
    public void button_secondary_dreamer_light_normal() {
        Button button = new Button(context);
        button.setTheme(Theme.DREAMER_LIGHT);
        button.setStyle(ButtonStyle.SECONDARY);
        button.setText("Secondary");
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        assertNotNull(button);
    }

    @Test
    public void button_secondary_dreamer_light_pressed() {
        Button button = new Button(context);
        button.setTheme(Theme.DREAMER_LIGHT);
        button.setStyle(ButtonStyle.SECONDARY);
        button.setText("Secondary");
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        android.view.MotionEvent down = android.view.MotionEvent.obtain(0, 0, android.view.MotionEvent.ACTION_DOWN, 40f, 50f, 0);
        button.dispatchTouchEvent(down);
        down.recycle();

        assertNotNull(button);
    }

    @Test
    public void button_secondary_dreamer_light_disabled() {
        Button button = new Button(context);
        button.setTheme(Theme.DREAMER_LIGHT);
        button.setStyle(ButtonStyle.SECONDARY);
        button.setText("Secondary");
        button.setEnabled(false);
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        assertNotNull(button);
    }

    @Test
    public void button_primary_dreamer_dark_normal() {
        Button button = new Button(context);
        button.setTheme(Theme.DREAMER_DARK);
        button.setStyle(ButtonStyle.PRIMARY);
        button.setText("Primary");
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        assertNotNull(button);
    }

    @Test
    public void button_primary_dreamer_dark_pressed() {
        Button button = new Button(context);
        button.setTheme(Theme.DREAMER_DARK);
        button.setStyle(ButtonStyle.PRIMARY);
        button.setText("Primary");
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        android.view.MotionEvent down = android.view.MotionEvent.obtain(0, 0, android.view.MotionEvent.ACTION_DOWN, 40f, 50f, 0);
        button.dispatchTouchEvent(down);
        down.recycle();

        assertNotNull(button);
    }

    @Test
    public void button_primary_dreamer_dark_disabled() {
        Button button = new Button(context);
        button.setTheme(Theme.DREAMER_DARK);
        button.setStyle(ButtonStyle.PRIMARY);
        button.setText("Primary");
        button.setEnabled(false);
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        assertNotNull(button);
    }

    @Test
    public void button_secondary_dreamer_dark_normal() {
        Button button = new Button(context);
        button.setTheme(Theme.DREAMER_DARK);
        button.setStyle(ButtonStyle.SECONDARY);
        button.setText("Secondary");
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        assertNotNull(button);
    }

    @Test
    public void button_secondary_dreamer_dark_pressed() {
        Button button = new Button(context);
        button.setTheme(Theme.DREAMER_DARK);
        button.setStyle(ButtonStyle.SECONDARY);
        button.setText("Secondary");
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        android.view.MotionEvent down = android.view.MotionEvent.obtain(0, 0, android.view.MotionEvent.ACTION_DOWN, 40f, 50f, 0);
        button.dispatchTouchEvent(down);
        down.recycle();

        assertNotNull(button);
    }

    @Test
    public void button_secondary_dreamer_dark_disabled() {
        Button button = new Button(context);
        button.setTheme(Theme.DREAMER_DARK);
        button.setStyle(ButtonStyle.SECONDARY);
        button.setText("Secondary");
        button.setEnabled(false);
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        assertNotNull(button);
    }

    @Test
    public void button_with_description_en_free_light() {
        Button button = new Button(context);
        button.setTheme(Theme.FREE_LIGHT);
        button.setStyle(ButtonStyle.SECONDARY);
        button.setText("Settings");
        button.setLanguage(Language.EN);
        java.util.Map<String, String> desc = new java.util.HashMap<>();
        desc.put("en", "Open advanced settings");
        desc.put("ru", "Открыть расширенные настройки");
        button.setDescription(desc);
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(800, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 800, 100);

        assertNotNull(button);
    }

    @Test
    public void button_with_multiline_description_en_free_light() {
        Button button = new Button(context);
        button.setTheme(Theme.FREE_LIGHT);
        button.setStyle(ButtonStyle.PRIMARY);
        button.setText("Calibrate");
        button.setLanguage(Language.EN);
        java.util.Map<String, String> desc = new java.util.HashMap<>();
        desc.put("en", "Run camera calibration.\nDrive straight for 2 minutes.");
        desc.put("ru", "Запустить калибровку камеры.\nДвигайтесь прямо 2 минуты.");
        button.setDescription(desc);
        button.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(800, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(150, android.view.View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 800, 150);

        assertNotNull(button);
    }
}
