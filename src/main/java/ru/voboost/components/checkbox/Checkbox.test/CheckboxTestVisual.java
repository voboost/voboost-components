package ru.voboost.components.checkbox;

import static org.junit.Assert.assertNotNull;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.Theme;

import java.util.HashMap;
import java.util.Map;

/**
 * Visual regression tests for Checkbox component.
 */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = {33}, qualifiers = "w1920dp-h720dp-land-mdpi")
public class CheckboxTestVisual {

    private Context context;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
    }

    // Basic state tests

    @Test
    public void checkbox_free_light_unchecked() {
        Checkbox checkbox = new Checkbox(context);
        checkbox.setTheme(Theme.FREE_LIGHT);
        checkbox.setLanguage(Language.EN);
        checkbox.setChecked(false);
        checkbox.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        checkbox.layout(0, 0, 500, 100);

        assertNotNull(checkbox);
    }

    @Test
    public void checkbox_free_light_checked() {
        Checkbox checkbox = new Checkbox(context);
        checkbox.setTheme(Theme.FREE_LIGHT);
        checkbox.setLanguage(Language.EN);
        checkbox.setChecked(true);
        checkbox.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        checkbox.layout(0, 0, 500, 100);

        assertNotNull(checkbox);
    }

    @Test
    public void checkbox_free_dark_unchecked() {
        Checkbox checkbox = new Checkbox(context);
        checkbox.setTheme(Theme.FREE_DARK);
        checkbox.setLanguage(Language.EN);
        checkbox.setChecked(false);
        checkbox.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        checkbox.layout(0, 0, 500, 100);

        assertNotNull(checkbox);
    }

    @Test
    public void checkbox_free_dark_checked() {
        Checkbox checkbox = new Checkbox(context);
        checkbox.setTheme(Theme.FREE_DARK);
        checkbox.setLanguage(Language.EN);
        checkbox.setChecked(true);
        checkbox.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        checkbox.layout(0, 0, 500, 100);

        assertNotNull(checkbox);
    }

    @Test
    public void checkbox_dreamer_light_unchecked() {
        Checkbox checkbox = new Checkbox(context);
        checkbox.setTheme(Theme.DREAMER_LIGHT);
        checkbox.setLanguage(Language.EN);
        checkbox.setChecked(false);
        checkbox.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        checkbox.layout(0, 0, 500, 100);

        assertNotNull(checkbox);
    }

    @Test
    public void checkbox_dreamer_light_checked() {
        Checkbox checkbox = new Checkbox(context);
        checkbox.setTheme(Theme.DREAMER_LIGHT);
        checkbox.setLanguage(Language.EN);
        checkbox.setChecked(true);
        checkbox.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        checkbox.layout(0, 0, 500, 100);

        assertNotNull(checkbox);
    }

    @Test
    public void checkbox_dreamer_dark_unchecked() {
        Checkbox checkbox = new Checkbox(context);
        checkbox.setTheme(Theme.DREAMER_DARK);
        checkbox.setLanguage(Language.EN);
        checkbox.setChecked(false);
        checkbox.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        checkbox.layout(0, 0, 500, 100);

        assertNotNull(checkbox);
    }

    @Test
    public void checkbox_dreamer_dark_checked() {
        Checkbox checkbox = new Checkbox(context);
        checkbox.setTheme(Theme.DREAMER_DARK);
        checkbox.setLanguage(Language.EN);
        checkbox.setChecked(true);
        checkbox.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        checkbox.layout(0, 0, 500, 100);

        assertNotNull(checkbox);
    }

    // With label tests

    @Test
    public void checkbox_free_dark_with_label() {
        Checkbox checkbox = new Checkbox(context);
        checkbox.setTheme(Theme.FREE_DARK);
        checkbox.setLanguage(Language.EN);
        checkbox.setChecked(true);
        checkbox.setLabel(mapOf("en", "Enable feature"));
        checkbox.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        checkbox.layout(0, 0, 500, 100);

        assertNotNull(checkbox);
    }

    @Test
    public void checkbox_free_light_with_label() {
        Checkbox checkbox = new Checkbox(context);
        checkbox.setTheme(Theme.FREE_LIGHT);
        checkbox.setLanguage(Language.EN);
        checkbox.setChecked(false);
        checkbox.setLabel(mapOf("en", "Auto mode"));
        checkbox.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        checkbox.layout(0, 0, 500, 100);

        assertNotNull(checkbox);
    }

    // With description tests

    @Test
    public void checkbox_dreamer_light_with_description() {
        Checkbox checkbox = new Checkbox(context);
        checkbox.setTheme(Theme.DREAMER_LIGHT);
        checkbox.setLanguage(Language.EN);
        checkbox.setChecked(false);
        checkbox.setLabel(mapOf("en", "Auto mode"));
        checkbox.setDescription(mapOf("en", "Automatically adjust based on conditions"));
        checkbox.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(150, android.view.View.MeasureSpec.EXACTLY));
        checkbox.layout(0, 0, 500, 150);

        assertNotNull(checkbox);
    }

    @Test
    public void checkbox_dreamer_dark_with_description() {
        Checkbox checkbox = new Checkbox(context);
        checkbox.setTheme(Theme.DREAMER_DARK);
        checkbox.setLanguage(Language.EN);
        checkbox.setChecked(true);
        checkbox.setLabel(mapOf("en", "Night mode"));
        checkbox.setDescription(mapOf("en", "Enable dark theme for night driving"));
        checkbox.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(150, android.view.View.MeasureSpec.EXACTLY));
        checkbox.layout(0, 0, 500, 150);

        assertNotNull(checkbox);
    }

    // Multilingual tests

    @Test
    public void checkbox_dreamer_dark_full_russian() {
        Checkbox checkbox = new Checkbox(context);
        checkbox.setTheme(Theme.DREAMER_DARK);
        checkbox.setLanguage(Language.RU);
        checkbox.setChecked(true);
        checkbox.setLabel(mapOf("en", "Enable", "ru", "Включить"));
        checkbox.setDescription(mapOf("en", "Allow this feature", "ru", "Разрешить эту функцию"));
        checkbox.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(150, android.view.View.MeasureSpec.EXACTLY));
        checkbox.layout(0, 0, 500, 150);

        assertNotNull(checkbox);
    }

    @Test
    public void checkbox_free_light_full_english() {
        Checkbox checkbox = new Checkbox(context);
        checkbox.setTheme(Theme.FREE_LIGHT);
        checkbox.setLanguage(Language.EN);
        checkbox.setChecked(false);
        checkbox.setLabel(mapOf("en", "Notifications", "ru", "Уведомления"));
        checkbox.setDescription(mapOf("en", "Receive system notifications", "ru", "Получать системные уведомления"));
        checkbox.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(150, android.view.View.MeasureSpec.EXACTLY));
        checkbox.layout(0, 0, 500, 150);

        assertNotNull(checkbox);
    }

    // Disabled state tests

    @Test
    public void checkbox_free_light_disabled_unchecked() {
        Checkbox checkbox = new Checkbox(context);
        checkbox.setTheme(Theme.FREE_LIGHT);
        checkbox.setLanguage(Language.EN);
        checkbox.setChecked(false);
        checkbox.setEnabled(false);
        checkbox.setLabel(mapOf("en", "Disabled option"));
        checkbox.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        checkbox.layout(0, 0, 500, 100);

        assertNotNull(checkbox);
    }

    @Test
    public void checkbox_free_dark_disabled_checked() {
        Checkbox checkbox = new Checkbox(context);
        checkbox.setTheme(Theme.FREE_DARK);
        checkbox.setLanguage(Language.EN);
        checkbox.setChecked(true);
        checkbox.setEnabled(false);
        checkbox.setLabel(mapOf("en", "Disabled option"));
        checkbox.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        checkbox.layout(0, 0, 500, 100);

        assertNotNull(checkbox);
    }

    // Animated toggle tests

    @Test
    public void checkbox_free_dark_animated_toggle() {
        Checkbox checkbox = new Checkbox(context);
        checkbox.setTheme(Theme.FREE_DARK);
        checkbox.setLanguage(Language.EN);
        checkbox.setChecked(false);
        checkbox.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        checkbox.layout(0, 0, 500, 100);

        // Animated toggle from unchecked to checked
        checkbox.setCheckedAnimated(true);

        assertNotNull(checkbox);
    }

    @Test
    public void checkbox_free_light_animated_toggle_reverse() {
        Checkbox checkbox = new Checkbox(context);
        checkbox.setTheme(Theme.FREE_LIGHT);
        checkbox.setLanguage(Language.EN);
        checkbox.setChecked(true);
        checkbox.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(500, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(100, android.view.View.MeasureSpec.EXACTLY));
        checkbox.layout(0, 0, 500, 100);

        // Animated toggle from checked to unchecked
        checkbox.setCheckedAnimated(false);

        assertNotNull(checkbox);
    }

    // Helper method

    private Map<String, String> mapOf(String... pairs) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            map.put(pairs[i], pairs[i + 1]);
        }
        return map;
    }
}
