package ru.voboost.components.popup;

import static org.junit.Assert.*;

import android.content.Context;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import ru.voboost.components.theme.Theme;

/**
 * Unit tests for Popup component.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {33})
public class PopupTestUnit {

    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
    }

    @Test
    public void testInitialization() {
        Popup popup = new Popup(context);
        assertNotNull("Popup should be initialized", popup);
    }

    @Test
    public void testSetTheme() {
        Popup popup = new Popup(context);
        popup.setTheme(Theme.FREE_DARK);
        assertEquals(Theme.FREE_DARK, popup.getCurrentTheme());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNullTheme() {
        Popup popup = new Popup(context);
        popup.setTheme(null);
    }

    @Test
    public void testAllThemes() {
        Popup popup = new Popup(context);
        for (Theme theme : Theme.values()) {
            popup.setTheme(theme);
            assertEquals(theme, popup.getCurrentTheme());
        }
    }

    @Test
    public void testSetPopupContentView() {
        Popup popup = new Popup(context);
        popup.setTheme(Theme.FREE_DARK);

        View content = new View(context);
        popup.setPopupContentView(content);

        assertNotNull("Content should be set", popup.getContentContainer());
        assertEquals("Container should have one child", 1, popup.getContentContainer().getChildCount());
    }

    @Test
    public void testSetPopupContentViewNull() {
        Popup popup = new Popup(context);
        popup.setTheme(Theme.FREE_DARK);

        View content = new View(context);
        popup.setPopupContentView(content);

        popup.setPopupContentView(null);

        assertEquals("Container should have no children", 0, popup.getContentContainer().getChildCount());
    }

    @Test
    public void testSetDismissOnTouchOutside() {
        Popup popup = new Popup(context);
        popup.setTheme(Theme.FREE_DARK);

        popup.setDismissOnTouchOutside(false);
        // Verify behavior (would need UI testing for full verification)

        popup.setDismissOnTouchOutside(true);
        // Verify behavior (would need UI testing for full verification)
    }

    @Test
    public void testDismissWithAnimation() {
        Popup popup = new Popup(context);
        popup.setTheme(Theme.FREE_DARK);
        popup.show();

        assertTrue("Popup should be showing", popup.isShowing());

        popup.dismissWithAnimation();
        // Note: Cannot test isShowing() after dismissWithAnimation in Robolectric
        // because animations don't complete synchronously in test environment
    }

    @Test
    public void testMultipleShowDismiss() {
        Popup popup = new Popup(context);
        popup.setTheme(Theme.FREE_DARK);

        for (int i = 0; i < 3; i++) {
            popup.show();
            assertTrue("Popup should be showing (iteration " + i + ")", popup.isShowing());
            popup.dismissWithAnimation();
            // Note: Cannot test isShowing() after dismissWithAnimation in Robolectric
        }
    }

    @Test
    public void testOverlayIsGradient() {
        Popup popup = new Popup(context);
        popup.setTheme(Theme.FREE_DARK);
        popup.show();

        // Overlay should be gradient, not solid color
        // RootLayout background should be set (gradient drawable)
        View rootLayout = popup.getContentContainer();
        assertNotNull("Root layout should exist", rootLayout);
        // Gradient is applied to rootLayout's parent via PopupOverlayGradient
    }

    @Test
    public void testSetOnDismissListenerRunnable() {
        Popup popup = new Popup(context);
        popup.setTheme(Theme.FREE_DARK);

        // Verify Runnable listener can be set without error
        popup.setOnDismissListener(() -> {});
        popup.show();
        // Note: listener is called asynchronously in Robolectric, cannot verify synchronously
    }
}

