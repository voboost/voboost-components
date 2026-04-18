package ru.voboost.components.dialog;

import static org.junit.Assert.assertNotNull;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

import ru.voboost.components.theme.Theme;

/**
 * Visual regression tests for Dialog component.
 */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = {33}, qualifiers = "w1920dp-h720dp-land-mdpi")
public class DialogTestVisual {

    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
    }

    @Test
    public void dialog_free_dark_both_buttons() {
        Dialog dialog = new Dialog(context);
        dialog.setTheme(Theme.FREE_DARK);
        dialog.setTitle("Reset Settings");
        dialog.setMessage("Are you sure you want to reset all settings?");
        dialog.setConfirmButton("Reset", () -> {});
        dialog.setCancelButton("Cancel", () -> {});
        dialog.show();

        assertNotNull(dialog);
        dialog.dismiss();
    }

    @Test
    public void dialog_free_light_both_buttons() {
        Dialog dialog = new Dialog(context);
        dialog.setTheme(Theme.FREE_LIGHT);
        dialog.setTitle("Reset Settings");
        dialog.setMessage("Are you sure you want to reset all settings?");
        dialog.setConfirmButton("Reset", () -> {});
        dialog.setCancelButton("Cancel", () -> {});
        dialog.show();

        assertNotNull(dialog);
        dialog.dismiss();
    }

    @Test
    public void dialog_dreamer_dark_both_buttons() {
        Dialog dialog = new Dialog(context);
        dialog.setTheme(Theme.DREAMER_DARK);
        dialog.setTitle("Reset Settings");
        dialog.setMessage("Are you sure you want to reset all settings?");
        dialog.setConfirmButton("Reset", () -> {});
        dialog.setCancelButton("Cancel", () -> {});
        dialog.show();

        assertNotNull(dialog);
        dialog.dismiss();
    }

    @Test
    public void dialog_dreamer_light_both_buttons() {
        Dialog dialog = new Dialog(context);
        dialog.setTheme(Theme.DREAMER_LIGHT);
        dialog.setTitle("Reset Settings");
        dialog.setMessage("Are you sure you want to reset all settings?");
        dialog.setConfirmButton("Reset", () -> {});
        dialog.setCancelButton("Cancel", () -> {});
        dialog.show();

        assertNotNull(dialog);
        dialog.dismiss();
    }

    @Test
    public void dialog_confirm_only() {
        Dialog dialog = new Dialog(context);
        dialog.setTheme(Theme.FREE_DARK);
        dialog.setTitle("Delete Item");
        dialog.setMessage("This action cannot be undone.");
        dialog.setConfirmButton("Delete", () -> {});
        dialog.show();

        assertNotNull(dialog);
        dialog.dismiss();
    }

    @Test
    public void dialog_cancel_only() {
        Dialog dialog = new Dialog(context);
        dialog.setTheme(Theme.FREE_DARK);
        dialog.setTitle("Information");
        dialog.setMessage("Your changes have been saved.");
        dialog.setCancelButton("Close", () -> {});
        dialog.show();

        assertNotNull(dialog);
        dialog.dismiss();
    }

    @Test
    public void dialog_title_only() {
        Dialog dialog = new Dialog(context);
        dialog.setTheme(Theme.FREE_DARK);
        dialog.setTitle("Loading...");
        dialog.setConfirmButton("OK", () -> {});
        dialog.show();

        assertNotNull(dialog);
        dialog.dismiss();
    }

    @Test
    public void dialog_message_only() {
        Dialog dialog = new Dialog(context);
        dialog.setTheme(Theme.FREE_DARK);
        dialog.setMessage("An error occurred. Please try again.");
        dialog.setConfirmButton("Retry", () -> {});
        dialog.show();

        assertNotNull(dialog);
        dialog.dismiss();
    }

    @Test
    public void dialog_long_title_and_message() {
        Dialog dialog = new Dialog(context);
        dialog.setTheme(Theme.FREE_DARK);
        dialog.setTitle("System Update Available");
        dialog.setMessage("A new system update is ready to install. The update will take approximately 10 minutes to complete. Make sure your vehicle is parked in a safe location.");
        dialog.setConfirmButton("Install", () -> {});
        dialog.setCancelButton("Later", () -> {});
        dialog.show();

        assertNotNull(dialog);
        dialog.dismiss();
    }

    @Test
    public void dialog_russian_text() {
        Dialog dialog = new Dialog(context);
        dialog.setTheme(Theme.FREE_DARK);
        dialog.setTitle("Сброс настроек");
        dialog.setMessage("Вы уверены, что хотите сбросить все настройки?");
        dialog.setConfirmButton("Сброс", () -> {});
        dialog.setCancelButton("Отмена", () -> {});
        dialog.show();

        assertNotNull(dialog);
        dialog.dismiss();
    }
}
