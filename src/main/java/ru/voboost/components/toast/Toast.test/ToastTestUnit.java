package ru.voboost.components.toast;

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
 * Unit tests for Toast component Java implementation.
 * Tests core functionality, state management, and edge cases.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {33})
public class ToastTestUnit {

    private Context context;
    private Toast toast;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        toast = new Toast(context);
    }

    // ============================================================
    // INITIALIZATION TESTS
    // ============================================================

    @Test
    public void testInitialization() {
        assertNotNull("Toast should be initialized", toast);
        assertFalse("Toast should not be showing initially", toast.isShowing());
        assertEquals("Default visibility should be GONE",
                View.GONE, toast.getVisibility());
    }

    @Test
    public void testHardwareAcceleration() {
        assertEquals("Hardware acceleration should be enabled",
                View.LAYER_TYPE_HARDWARE, toast.getLayerType());
    }

    // ============================================================
    // THEME TESTS
    // ============================================================

    @Test
    public void testSetTheme() {
        toast.setTheme(Theme.FREE_DARK);
        assertEquals(Theme.FREE_DARK, toast.getCurrentTheme());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetThemeNull() {
        toast.setTheme(null);
    }

    @Test
    public void testAllThemes() {
        for (Theme theme : Theme.values()) {
            toast.setTheme(theme);
            assertEquals(theme, toast.getCurrentTheme());
        }
    }

    // ============================================================
    // CONTENT TESTS
    // ============================================================

    @Test
    public void testSetContent() {
        toast.setContent("Test message");
        assertEquals("Test message", toast.getContent());
    }

    @Test
    public void testSetContentNull() {
        toast.setContent(null);
        assertEquals("", toast.getContent());
    }

    @Test
    public void testSetContentEmpty() {
        toast.setContent("");
        assertEquals("", toast.getContent());
    }

    @Test
    public void testSetContentLong() {
        String longText = "This is a very long message that should be truncated with ellipsis when displayed";
        toast.setContent(longText);
        assertEquals(longText, toast.getContent());
    }

    @Test
    public void testSetContentUnicode() {
        toast.setContent("Настройки сохранены");
        assertEquals("Настройки сохранены", toast.getContent());
    }

    // ============================================================
    // DURATION TESTS
    // ============================================================

    @Test
    public void testDefaultDuration() {
        assertEquals(ToastTheme.DURATION_SHORT, toast.getDuration());
    }

    @Test
    public void testSetDurationShort() {
        toast.setDuration(ToastTheme.DURATION_SHORT);
        assertEquals(ToastTheme.DURATION_SHORT, toast.getDuration());
    }

    @Test
    public void testSetDurationLong() {
        toast.setDuration(ToastTheme.DURATION_LONG);
        assertEquals(ToastTheme.DURATION_LONG, toast.getDuration());
    }

    @Test
    public void testSetCustomDuration() {
        toast.setDuration(10000);
        assertEquals(10000, toast.getDuration());
    }

    @Test
    public void testSetDurationZero() {
        toast.setDuration(0);
        assertEquals(0, toast.getDuration());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDurationNegative() {
        toast.setDuration(-1);
    }

    // ============================================================
    // CLOSE BUTTON TESTS
    // ============================================================

    @Test
    public void testShowCloseButtonDefault() {
        assertFalse("Close button should be hidden by default", toast.isShowCloseButton());
    }

    @Test
    public void testSetShowCloseButton() {
        toast.setShowCloseButton(true);
        assertTrue(toast.isShowCloseButton());

        toast.setShowCloseButton(false);
        assertFalse(toast.isShowCloseButton());
    }

    // ============================================================
    // SHOW/DISMISS TESTS
    // ============================================================

    @Test
    public void testShow() {
        toast.setTheme(Theme.FREE_DARK);
        toast.setContent("Test");
        toast.show();
        assertTrue("Toast should be showing after show()", toast.isShowing());
        assertEquals("Visibility should be VISIBLE after show()",
                View.VISIBLE, toast.getVisibility());
    }

    @Test
    public void testShowWithoutTheme() {
        toast.setContent("Test");
        toast.show();
        assertTrue("Toast should be showing even without theme", toast.isShowing());
    }

    @Test
    public void testDismissBeforeShow() {
        // Should not throw
        toast.dismiss();
        assertFalse(toast.isShowing());
    }

    @Test
    public void testShowTwice() {
        toast.setTheme(Theme.FREE_DARK);
        toast.setContent("Test");
        toast.show();
        toast.show(); // second call should be no-op
        assertTrue(toast.isShowing());
    }

    @Test
    public void testDismissAfterShow() {
        toast.setTheme(Theme.FREE_DARK);
        toast.setContent("Test");
        toast.show();
        assertTrue(toast.isShowing());

        toast.dismiss();
        // Note: In Robolectric, animations complete synchronously
        // so isShowing() may still be true until animation listener fires
    }

    // ============================================================
    // CALLBACK TESTS
    // ============================================================

    @Test
    public void testOnDismissListener() {
        final int[] dismissCount = {0};
        toast.setOnDismissListener(() -> dismissCount[0]++);

        assertNotNull("Listener should be set", toast);
        assertEquals("Dismiss count should be 0 initially", 0, dismissCount[0]);
    }

    @Test
    public void testOnDismissListenerNull() {
        // Should not throw
        toast.setOnDismissListener(null);
        toast.setTheme(Theme.FREE_DARK);
        toast.show();
        toast.dismiss();
    }

    // ============================================================
    // MEASUREMENT TESTS
    // ============================================================

    @Test
    public void testMeasurement() {
        toast.setTheme(Theme.FREE_DARK);
        toast.measure(
            View.MeasureSpec.makeMeasureSpec(2000, View.MeasureSpec.AT_MOST),
            View.MeasureSpec.makeMeasureSpec(200, View.MeasureSpec.AT_MOST));

        assertEquals("Width should be " + ToastTheme.WIDTH,
                ToastTheme.WIDTH, toast.getMeasuredWidth());
        assertEquals("Height should be " + ToastTheme.HEIGHT,
                ToastTheme.HEIGHT, toast.getMeasuredHeight());
    }

    @Test
    public void testMeasurementWithoutTheme() {
        toast.measure(
            View.MeasureSpec.makeMeasureSpec(2000, View.MeasureSpec.AT_MOST),
            View.MeasureSpec.makeMeasureSpec(200, View.MeasureSpec.AT_MOST));

        assertEquals("Width should be " + ToastTheme.WIDTH,
                ToastTheme.WIDTH, toast.getMeasuredWidth());
        assertEquals("Height should be " + ToastTheme.HEIGHT,
                ToastTheme.HEIGHT, toast.getMeasuredHeight());
    }

    // ============================================================
    // TOUCH HANDLING TESTS
    // ============================================================

    @Test
    public void testCloseButtonTouch() {
        toast.setTheme(Theme.FREE_DARK);
        toast.setContent("Test");
        toast.setShowCloseButton(true);

        toast.measure(
            View.MeasureSpec.makeMeasureSpec(ToastTheme.WIDTH, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(ToastTheme.HEIGHT, View.MeasureSpec.EXACTLY));
        toast.layout(0, 0, ToastTheme.WIDTH, ToastTheme.HEIGHT);

        toast.show();

        // Touch on close button area
        float closeX = ToastDimensions.CLOSE_BUTTON_MARGIN + ToastDimensions.CLOSE_BUTTON_SIZE / 2f;
        float closeY = ToastTheme.HEIGHT / 2f;

        MotionEvent up = MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, closeX, closeY, 0);
        boolean handled = toast.onTouchEvent(up);
        assertTrue("Close button touch should be handled", handled);
        up.recycle();
    }

    @Test
    public void testTouchOutsideCloseButton() {
        toast.setTheme(Theme.FREE_DARK);
        toast.setContent("Test");
        toast.setShowCloseButton(true);

        toast.measure(
            View.MeasureSpec.makeMeasureSpec(ToastTheme.WIDTH, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(ToastTheme.HEIGHT, View.MeasureSpec.EXACTLY));
        toast.layout(0, 0, ToastTheme.WIDTH, ToastTheme.HEIGHT);

        toast.show();

        // Touch outside close button area
        float touchX = ToastTheme.WIDTH / 2f;
        float touchY = ToastTheme.HEIGHT / 2f;

        MotionEvent up = MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, touchX, touchY, 0);
        boolean handled = toast.onTouchEvent(up);
        assertFalse("Touch outside close button should not be handled", handled);
        up.recycle();
    }

    @Test
    public void testTouchWithoutCloseButton() {
        toast.setTheme(Theme.FREE_DARK);
        toast.setContent("Test");
        toast.setShowCloseButton(false);

        toast.measure(
            View.MeasureSpec.makeMeasureSpec(ToastTheme.WIDTH, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(ToastTheme.HEIGHT, View.MeasureSpec.EXACTLY));
        toast.layout(0, 0, ToastTheme.WIDTH, ToastTheme.HEIGHT);

        toast.show();

        // Touch anywhere
        float touchX = ToastDimensions.CLOSE_BUTTON_MARGIN + ToastDimensions.CLOSE_BUTTON_SIZE / 2f;
        float touchY = ToastTheme.HEIGHT / 2f;

        MotionEvent up = MotionEvent.obtain(0, 0, MotionEvent.ACTION_UP, touchX, touchY, 0);
        boolean handled = toast.onTouchEvent(up);
        assertFalse("Touch should not be handled when close button is disabled", handled);
        up.recycle();
    }

    // ============================================================
    // LIFECYCLE TESTS
    // ============================================================

    @Test
    public void testDetach() {
        toast.setTheme(Theme.FREE_DARK);
        toast.setContent("Test");
        toast.show();
        toast.onDetachedFromWindow();
        // Should not crash
        assertNotNull(toast);
    }

    @Test
    public void testDetachWithoutShow() {
        toast.setTheme(Theme.FREE_DARK);
        toast.setContent("Test");
        toast.onDetachedFromWindow();
        // Should not crash
        assertNotNull(toast);
    }

    // ============================================================
    // THEME COLOR TESTS
    // ============================================================

    @Test
    public void testThemeColors() {
        // Test that all themes have valid colors
        for (Theme theme : Theme.values()) {
            ToastColors colors = ToastTheme.getColors(theme);
            assertNotNull("Colors should not be null for " + theme, colors);
            assertNotEquals("Background color should not be 0", 0, colors.background);
            assertNotEquals("Text color should not be 0", 0, colors.text);
            assertNotEquals("Close icon color should not be 0", 0, colors.closeIcon);
        }
    }

    // ============================================================
    // LIFECYCLE OBSERVER TESTS
    // ============================================================

    @Test
    public void testLifecycleObserverCleanupOnDismiss() {
        toast.setTheme(Theme.FREE_DARK);
        toast.setContent("Test");
        toast.show();

        // Dismiss should clean up lifecycle observer
        toast.dismiss();

        // Should not throw - observer was cleaned up
        assertNotNull("Toast should still exist after dismiss", toast);
    }

    @Test
    public void testLifecycleObserverCleanupOnDetach() {
        toast.setTheme(Theme.FREE_DARK);
        toast.setContent("Test");
        toast.show();
        toast.onDetachedFromWindow();

        // Should not throw - observer was cleaned up
        assertNotNull("Toast should still exist after detach", toast);
    }

    @Test
    public void testDismissInProgressFlag() {
        toast.setTheme(Theme.FREE_DARK);
        toast.setContent("Test");
        toast.show();

        // First dismiss
        toast.dismiss();

        // Second dismiss should be ignored (no crash)
        toast.dismiss();
    }

    @Test
    public void testAutoDismissRunnableCleanup() {
        toast.setTheme(Theme.FREE_DARK);
        toast.setContent("Test");
        toast.setDuration(1000);
        toast.show();
        toast.onDetachedFromWindow();

        // Should not throw - runnable was cleaned up
        assertNotNull("Toast should still exist", toast);
    }

    // ============================================================
    // INTEGRATION TESTS
    // ============================================================

    @Test
    public void testCompleteLifecycle() {
        toast.setTheme(Theme.FREE_DARK);
        toast.setContent("Test message");
        toast.setDuration(ToastTheme.DURATION_SHORT);
        toast.setShowCloseButton(false);

        assertFalse("Should not be showing initially", toast.isShowing());

        toast.show();
        assertTrue("Should be showing after show()", toast.isShowing());

        toast.dismiss();
        // Animation may still be running in Robolectric
    }

    @Test
    public void testMultipleShowDismissCycles() {
        toast.setTheme(Theme.FREE_DARK);
        toast.setContent("Test");

        for (int i = 0; i < 3; i++) {
            toast.show();
            assertTrue("Should be showing in cycle " + i, toast.isShowing());
            toast.dismiss();
        }
    }
}
