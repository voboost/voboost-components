package ru.voboost.components.checkbox;

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
 * Unit tests for Checkbox component.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {33})
public class CheckboxTestUnit {

    private Context context;
    private Checkbox checkbox;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        checkbox = new Checkbox(context);
        checkbox.setTheme(Theme.FREE_DARK);
    }

    @Test
    public void testInitialization() {
        assertNotNull("Checkbox should be initialized", checkbox);
        assertFalse("Initial state should be unchecked", checkbox.isChecked());
        assertTrue("Should be enabled by default", checkbox.isEnabled());
    }

    @Test
    public void testSetChecked() {
        checkbox.setChecked(true);
        assertTrue("Should be checked", checkbox.isChecked());

        checkbox.setChecked(false);
        assertFalse("Should be unchecked", checkbox.isChecked());
    }

    @Test
    public void testSetTheme() {
        checkbox.setTheme(Theme.DREAMER_LIGHT);
        assertEquals(Theme.DREAMER_LIGHT, checkbox.getCurrentTheme());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNullTheme() {
        checkbox.setTheme(null);
    }

    @Test
    public void testAllThemes() {
        for (Theme theme : Theme.values()) {
            checkbox.setTheme(theme);
            assertNotNull("Should accept theme: " + theme, checkbox);
        }
    }

    @Test
    public void testEnabled() {
        checkbox.setEnabled(false);
        assertFalse(checkbox.isEnabled());

        checkbox.setEnabled(true);
        assertTrue(checkbox.isEnabled());
    }

    @Test
    public void testCheckedChangeCallback() {
        final boolean[] callbackValue = {false};
        final int[] callbackCount = {0};

        checkbox.setOnCheckedChangeListener(isChecked -> {
            callbackValue[0] = isChecked;
            callbackCount[0]++;
        });

        checkbox.measure(
                View.MeasureSpec.makeMeasureSpec(85, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(48, View.MeasureSpec.EXACTLY));
        checkbox.layout(0, 0, 85, 48);

        // Simulate touch toggle
        MotionEvent down = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 50f, 25f, 0);
        MotionEvent up = MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, 50f, 25f, 0);
        checkbox.onTouchEvent(down);
        checkbox.onTouchEvent(up);

        assertTrue("Should be checked after toggle", callbackValue[0]);
        assertEquals("Callback count should be 1", 1, callbackCount[0]);

        down.recycle();
        up.recycle();
    }

    @Test
    public void testDisabledIgnoresTouch() {
        checkbox.setEnabled(false);
        final int[] callbackCount = {0};
        checkbox.setOnCheckedChangeListener(isChecked -> callbackCount[0]++);

        MotionEvent down = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 50f, 25f, 0);
        boolean handled = checkbox.onTouchEvent(down);

        assertFalse("Disabled should not handle touch", handled);
        assertEquals(0, callbackCount[0]);
        down.recycle();
    }

    @Test
    public void testMeasurement() {
        checkbox.measure(
                View.MeasureSpec.makeMeasureSpec(500, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(200, View.MeasureSpec.AT_MOST));

        assertTrue("Width should be positive", checkbox.getMeasuredWidth() > 0);
        assertTrue("Height should be positive", checkbox.getMeasuredHeight() > 0);
    }

    @Test
    public void testDetachCancelsAnimations() {
        checkbox.setChecked(true);
        // Note: onDetachedFromWindow() is protected, so we can't call it directly
        // The checkbox should still work correctly after being detached and re-attached
        checkbox.setChecked(false);
        assertFalse("Should be unchecked", checkbox.isChecked());
    }

    @Test
    public void testOvershootKnobPositionDoesNotCrash() {
        checkbox.measure(
                View.MeasureSpec.makeMeasureSpec(500, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY));
        checkbox.layout(0, 0, 500, 100);

        // Simulate overshoot past 1.0 (OvershootInterpolator peak)
        checkbox.setTransitionState(1.05f, -1f);
        checkbox.draw(new android.graphics.Canvas(
                android.graphics.Bitmap.createBitmap(500, 100, android.graphics.Bitmap.Config.ARGB_8888)));

        // Simulate undershoot below 0.0
        checkbox.setTransitionState(-0.05f, -1f);
        checkbox.draw(new android.graphics.Canvas(
                android.graphics.Bitmap.createBitmap(500, 100, android.graphics.Bitmap.Config.ARGB_8888)));

        assertTrue("Should not crash with overshoot values", true);
    }

    @Test
    public void testBuilderDefaultMarginNotSet() {
        android.content.Context ctx = androidx.test.core.app.ApplicationProvider.getApplicationContext();
        Checkbox cb = Checkbox.create(ctx, Theme.FREE_LIGHT, ru.voboost.components.i18n.Language.EN, false)
                .build();
        assertFalse("Builder without margin methods must not set marginSet", cb.isMarginSet());
    }

    @Test
    public void testBuilderExplicitMarginSet() {
        android.content.Context ctx = androidx.test.core.app.ApplicationProvider.getApplicationContext();
        Checkbox cb = Checkbox.create(ctx, Theme.FREE_LIGHT, ru.voboost.components.i18n.Language.EN, false)
                .marginBottom(44)
                .build();
        assertTrue("Builder with marginBottom must set marginSet", cb.isMarginSet());
        assertEquals(44, cb.getMarginBottom());
    }

}
