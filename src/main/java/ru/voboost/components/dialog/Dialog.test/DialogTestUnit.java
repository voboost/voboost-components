package ru.voboost.components.dialog;

import static org.junit.Assert.*;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import ru.voboost.components.theme.Theme;

/**
 * Unit tests for Dialog component.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {33})
public class DialogTestUnit {

    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
    }

    @Test
    public void testInitialization() {
        Dialog dialog = new Dialog(context);
        assertNotNull("Dialog should be initialized", dialog);
    }

    @Test
    public void testSetTheme() {
        Dialog dialog = new Dialog(context);
        dialog.setTheme(Theme.FREE_DARK);
        assertEquals(Theme.FREE_DARK, dialog.getCurrentTheme());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNullTheme() {
        Dialog dialog = new Dialog(context);
        dialog.setTheme(null);
    }

    @Test
    public void testSetTitle() {
        Dialog dialog = new Dialog(context);
        dialog.setTitle("Test Title");
        assertEquals("Test Title", dialog.getTitle());
    }

    @Test
    public void testSetMessage() {
        Dialog dialog = new Dialog(context);
        dialog.setMessage("Test Message");
        assertEquals("Test Message", dialog.getMessage());
    }

    @Test
    public void testAllThemes() {
        Dialog dialog = new Dialog(context);
        for (Theme theme : Theme.values()) {
            dialog.setTheme(theme);
            assertEquals(theme, dialog.getCurrentTheme());
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testShowWithoutTheme() {
        Dialog dialog = new Dialog(context);
        dialog.setTitle("Test");
        dialog.show(); // Should throw because theme is not set
    }

    @Test
    public void testConfirmButtonCallback() {
        final boolean[] confirmed = {false};
        Dialog dialog = new Dialog(context);
        dialog.setTheme(Theme.FREE_DARK);
        dialog.setConfirmButton("OK", () -> confirmed[0] = true);
        dialog.show();
        // Note: callback tested via button click in visual tests
        assertFalse("Not confirmed yet", confirmed[0]);
    }

    @Test
    public void testCancelButtonCallback() {
        final boolean[] cancelled = {false};
        Dialog dialog = new Dialog(context);
        dialog.setTheme(Theme.FREE_DARK);
        dialog.setCancelButton("Cancel", () -> cancelled[0] = true);
        dialog.show();
        // Note: callback tested via button click in visual tests
        assertFalse("Not cancelled yet", cancelled[0]);
    }

    @Test
    public void testContentUpdateAfterShow() {
        Dialog dialog = new Dialog(context);
        dialog.setTheme(Theme.FREE_DARK);
        dialog.setTitle("Original Title");
        dialog.show();

        // Update content after show
        dialog.setTitle("Updated Title");
        dialog.show();

        assertEquals("Title should be updated", "Updated Title", dialog.getTitle());
    }

    @Test
    public void testMessageUpdate() {
        Dialog dialog = new Dialog(context);
        dialog.setTheme(Theme.FREE_DARK);
        dialog.setMessage("Original Message");
        assertEquals("Message should be set", "Original Message", dialog.getMessage());

        dialog.setMessage("Updated Message");
        assertEquals("Message should be updated", "Updated Message", dialog.getMessage());
    }

    @Test
    public void testButtonsUpdate() {
        Dialog dialog = new Dialog(context);
        dialog.setTheme(Theme.FREE_DARK);
        dialog.setConfirmButton("OK", () -> {});
        dialog.setCancelButton("Cancel", () -> {});
        dialog.show();

        // Update buttons
        dialog.setConfirmButton("Yes", () -> {});
        dialog.setCancelButton("No", () -> {});
        dialog.show();

        assertNotNull("Dialog should still work after button update", dialog);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetConfirmButtonWithNullText() {
        Dialog dialog = new Dialog(context);
        dialog.setConfirmButton(null, () -> {});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetConfirmButtonWithEmptyText() {
        Dialog dialog = new Dialog(context);
        dialog.setConfirmButton("", () -> {});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetCancelButtonWithNullText() {
        Dialog dialog = new Dialog(context);
        dialog.setCancelButton(null, () -> {});
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetCancelButtonWithEmptyText() {
        Dialog dialog = new Dialog(context);
        dialog.setCancelButton("", () -> {});
    }

    @Test
    public void testSetNullTitle() {
        Dialog dialog = new Dialog(context);
        dialog.setTheme(Theme.FREE_DARK);
        dialog.setTitle(null);
        assertNull("Null title should be accepted", dialog.getTitle());
    }

    @Test
    public void testSetNullMessage() {
        Dialog dialog = new Dialog(context);
        dialog.setTheme(Theme.FREE_DARK);
        dialog.setMessage(null);
        assertNull("Null message should be accepted", dialog.getMessage());
    }

    @Test
    public void testShowTwice() {
        Dialog dialog = new Dialog(context);
        dialog.setTheme(Theme.FREE_DARK);
        dialog.show();
        assertTrue("Dialog should be showing", dialog.isShowing());

        dialog.show(); // Show again
        assertTrue("Dialog should still be showing", dialog.isShowing());
        dialog.dismiss();
    }
}
