package ru.voboost.components.button;

import static org.junit.Assert.*;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import ru.voboost.components.theme.Theme;

/**
 * Unit tests for Button component Java implementation.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {33})
public class ButtonTestUnit {

    private Context context;
    private Button button;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        button = new Button(context);
        button.setTheme(Theme.FREE_DARK);
    }

    @Test
    public void testInitialization() {
        assertNotNull("Button should be initialized", button);
        assertEquals("Initial text should be empty", "", button.getText());
        assertEquals("Default style should be PRIMARY", ButtonStyle.PRIMARY, button.getStyle());
        assertTrue("Button should be enabled by default", button.isEnabled());
    }

    @Test
    public void testSetText() {
        button.setText("Confirm");
        assertEquals("Text should be set", "Confirm", button.getText());
    }

    @Test
    public void testSetNullText() {
        button.setText(null);
        assertEquals("Null text should become empty", "", button.getText());
    }

    @Test
    public void testSetStyle() {
        button.setStyle(ButtonStyle.SECONDARY);
        assertEquals("Style should be SECONDARY", ButtonStyle.SECONDARY, button.getStyle());

        button.setStyle(ButtonStyle.PRIMARY);
        assertEquals("Style should be PRIMARY", ButtonStyle.PRIMARY, button.getStyle());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNullStyle() {
        button.setStyle(null);
    }

    @Test
    public void testSetTheme() {
        button.setTheme(Theme.FREE_LIGHT);
        assertEquals("Theme should be FREE_LIGHT", Theme.FREE_LIGHT, button.getCurrentTheme());

        button.setTheme(Theme.DREAMER_DARK);
        assertEquals("Theme should be DREAMER_DARK", Theme.DREAMER_DARK, button.getCurrentTheme());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNullTheme() {
        button.setTheme(null);
    }

    @Test
    public void testAllThemes() {
        for (Theme theme : Theme.values()) {
            button.setTheme(theme);
            assertNotNull("Button should accept theme: " + theme, button);
        }
    }

    @Test
    public void testAllStylesWithAllThemes() {
        for (Theme theme : Theme.values()) {
            for (ButtonStyle style : ButtonStyle.values()) {
                button.setTheme(theme);
                button.setStyle(style);
                assertNotNull("Should work with " + theme + " + " + style, button);
            }
        }
    }

    @Test
    public void testEnabled() {
        button.setEnabled(false);
        assertFalse("Button should be disabled", button.isEnabled());

        button.setEnabled(true);
        assertTrue("Button should be enabled", button.isEnabled());
    }

    @Test
    public void testClickCallback() {
        final int[] clickCount = {0};
        button.setOnClickListener(v -> clickCount[0]++);

        button.setText("Test");

        // Measure and layout
        button.measure(
                View.MeasureSpec.makeMeasureSpec(500, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        // Simulate touch down + up inside bounds
        // Use coordinates that are likely within the primitive's bounds
        // The primitive is positioned at the left side of the container
        MotionEvent down = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 40f, 50f, 0);
        MotionEvent up = MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, 40f, 50f, 0);

        button.dispatchTouchEvent(down);
        button.dispatchTouchEvent(up);

        assertEquals("Click should be triggered", 1, clickCount[0]);

        down.recycle();
        up.recycle();
    }

    @Test
    public void testDisabledIgnoresTouch() {
        final int[] clickCount = {0};
        button.setOnClickListener(v -> clickCount[0]++);
        button.setEnabled(false);

        button.measure(
                View.MeasureSpec.makeMeasureSpec(500, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        MotionEvent down = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 40f, 50f, 0);
        boolean handled = button.dispatchTouchEvent(down);

        assertFalse("Disabled button should not handle touch", handled);
        assertEquals("Click should not be triggered", 0, clickCount[0]);

        down.recycle();
    }

    @Test
    public void testMeasurement() {
        button.setText("Test Button");
        button.measure(
                View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(200, View.MeasureSpec.AT_MOST));

        assertTrue("Width should be positive", button.getMeasuredWidth() > 0);
        // Actual height: button.getMeasuredHeight() = 70px (ButtonPrimitive.HEIGHT_PX)
        assertEquals("Height should be 70px (ButtonPrimitive.HEIGHT_PX)",
                70, button.getMeasuredHeight());
    }


    @Test
    public void testTouchCancel() {
        final int[] clickCount = {0};
        button.setOnClickListener(v -> clickCount[0]++);

        button.measure(
                View.MeasureSpec.makeMeasureSpec(500, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        MotionEvent down = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 40f, 50f, 0);
        MotionEvent cancel = MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 40f, 50f, 0);

        button.dispatchTouchEvent(down);
        button.dispatchTouchEvent(cancel);

        assertEquals("Click should not be triggered on cancel", 0, clickCount[0]);

        down.recycle();
        cancel.recycle();
    }

    @Test
    public void testMultipleClicks() {
        final int[] clickCount = {0};
        button.setOnClickListener(v -> clickCount[0]++);

        button.measure(
                View.MeasureSpec.makeMeasureSpec(500, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        // Simulate multiple clicks
        for (int i = 0; i < 5; i++) {
            MotionEvent down = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 40f, 50f, 0);
            MotionEvent up = MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, 40f, 50f, 0);

            button.dispatchTouchEvent(down);
            button.dispatchTouchEvent(up);

            down.recycle();
            up.recycle();
        }

        assertEquals("All clicks should be triggered", 5, clickCount[0]);
    }

    @Test
    public void testTouchOutsideBounds() {
        final int[] clickCount = {0};
        button.setOnClickListener(v -> clickCount[0]++);

        button.measure(
                View.MeasureSpec.makeMeasureSpec(500, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY));
        button.layout(0, 0, 500, 100);

        // Simulate touch outside button bounds
        MotionEvent down = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 500f, 150f, 0);
        MotionEvent up = MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, 500f, 150f, 0);

        button.dispatchTouchEvent(down);
        button.dispatchTouchEvent(up);

        assertEquals("Click should not be triggered for touch outside bounds", 0, clickCount[0]);

        down.recycle();
        up.recycle();
    }

    @Test
    public void testSetTextUpdates() {
        button.setText("Initial");
        assertEquals("Text should be set", "Initial", button.getText());

        button.setText("Updated");
        assertEquals("Text should be updated", "Updated", button.getText());
    }

    @Test
    public void testStyleUpdates() {
        button.setStyle(ButtonStyle.PRIMARY);
        assertEquals("Style should be PRIMARY", ButtonStyle.PRIMARY, button.getStyle());

        button.setStyle(ButtonStyle.SECONDARY);
        assertEquals("Style should be SECONDARY", ButtonStyle.SECONDARY, button.getStyle());
    }

    @Test
    public void testBuilderDefaultMarginNotSet() {
        android.content.Context ctx = androidx.test.core.app.ApplicationProvider.getApplicationContext();
        Button btn = Button.create(ctx, Theme.FREE_LIGHT, ru.voboost.components.i18n.Language.EN, "Test", ButtonStyle.SECONDARY)
                .build();
        assertFalse("Builder without margin methods must not set marginSet", btn.isMarginSet());
    }

    @Test
    public void testBuilderExplicitMarginSet() {
        android.content.Context ctx = androidx.test.core.app.ApplicationProvider.getApplicationContext();
        Button btn = Button.create(ctx, Theme.FREE_LIGHT, ru.voboost.components.i18n.Language.EN, "Test", ButtonStyle.SECONDARY)
                .marginBottom(44)
                .build();
        assertTrue("Builder with marginBottom must set marginSet", btn.isMarginSet());
        assertEquals(44, btn.getMarginBottom());
    }
}
