package ru.voboost.components.select;

import static org.junit.Assert.*;

import android.content.Context;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.Theme;

/**
 * Unit tests for Select component.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {33})
public class SelectTestUnit {

    private Context context;
    private Select select;
    private List<SelectOption> options;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        select = new Select(context);

        options = Arrays.asList(
                new SelectOption("auto", Map.of("en", "Automatic", "ru", "Авто")),
                new SelectOption("manual", Map.of("en", "Manual", "ru", "Ручной")),
                new SelectOption("eco", Map.of("en", "Eco", "ru", "Эко"))
        );
    }

    @Test
    public void testInitialization() {
        assertNotNull("Select should be initialized", select);
    }

    @Test
    public void testSetOptions() {
        select.setOptions(options);
        assertNotNull("Select accepts options", select);
    }

    @Test
    public void testSetTheme() {
        select.setTheme(Theme.FREE_DARK);
        assertEquals(Theme.FREE_DARK, select.getCurrentTheme());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNullTheme() {
        select.setTheme(null);
    }

    @Test
    public void testSetLanguage() {
        select.setLanguage(Language.EN);
        assertEquals(Language.EN, select.getCurrentLanguage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNullLanguage() {
        select.setLanguage(null);
    }

    @Test
    public void testSetSelectedValue() {
        select.setOptions(options);
        select.setSelectedValue("manual");
        assertEquals("manual", select.getSelectedValue());
    }

    @Test
    public void testAllThemes() {
        select.setLanguage(Language.EN);
        for (Theme theme : Theme.values()) {
            select.setTheme(theme);
            assertEquals(theme, select.getCurrentTheme());
        }
    }

    @Test
    public void testSelectOptionEquals() {
        SelectOption a = new SelectOption("test", Map.of("en", "Test"));
        SelectOption b = new SelectOption("test", Map.of("en", "Test"));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void testSelectOptionGetText() {
        SelectOption opt = new SelectOption("val", Map.of("en", "English", "ru", "Русский"));
        assertEquals("English", opt.getText("en"));
        assertEquals("Русский", opt.getText("ru"));
        // Fallback to first available
        assertNotNull(opt.getText("zh"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSelectOptionBlankValue() {
        new SelectOption("", Map.of("en", "Test"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSelectOptionEmptyLabel() {
        new SelectOption("val", Map.of());
    }

    @Test
    public void testMeasurement() {
        select.setOptions(options);
        select.setLanguage(Language.EN);
        select.setTheme(Theme.FREE_DARK);
        select.setSelectedValue("auto");

        select.measure(
                View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.AT_MOST),
                View.MeasureSpec.makeMeasureSpec(200, View.MeasureSpec.AT_MOST));
        assertTrue("Width should be positive", select.getMeasuredWidth() > 0);
        assertEquals("Height should be 70px", 70, select.getMeasuredHeight());
    }

    @Test
    public void testHardwareAcceleration() {
        assertEquals(View.LAYER_TYPE_HARDWARE, select.getLayerType());
    }

    @Test
    public void testSetMode() {
        select.setMode(Select.Mode.CURVED);
        select.setMode(Select.Mode.FLAT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNullMode() {
        select.setMode(null);
    }

    @Test
    public void testSetConfirmCancelText() {
        select.setConfirmText(Map.of("en", "OK"));
        select.setCancelText(Map.of("en", "Close"));
        select.setConfirmText(null);
        select.setCancelText(null);
    }

    @Test
    public void testPopupConfirmCancelText() {
        SelectPopup popup = new SelectPopup(context);
        popup.setConfirmText("Confirm");
        popup.setCancelText("Cancel");
        popup.setTheme(Theme.FREE_DARK);
    }

    @Test
    public void testSelectWheelInitialization() {
        SelectWheel wheel = new SelectWheel(context);
        assertNotNull("WheelView should be initialized", wheel);
    }

    @Test
    public void testSelectWheelData() {
        SelectWheel wheel = new SelectWheel(context);
        wheel.setData(Arrays.asList("One", "Two", "Three"));
        assertEquals("One", wheel.getCurrentItem());
        assertEquals(0, wheel.getCurrentPosition());
    }

    @Test
    public void testSelectWheelSetPosition() {
        SelectWheel wheel = new SelectWheel(context);
        wheel.setData(Arrays.asList("One", "Two", "Three"), 2);
        assertEquals(2, wheel.getCurrentPosition());
        assertEquals("Three", wheel.getCurrentItem());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSelectOptionWithNullLanguageCode() {
        Map<String, String> labels = new java.util.HashMap<>();
        labels.put(null, "Text");
        new SelectOption("value", labels);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSelectOptionWithEmptyLanguageCode() {
        Map<String, String> labels = new java.util.HashMap<>();
        labels.put("", "Text");
        new SelectOption("value", labels);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSelectOptionWithNullLabelValue() {
        Map<String, String> labels = new java.util.HashMap<>();
        labels.put("en", null);
        new SelectOption("value", labels);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSelectOptionWithEmptyLabelValue() {
        Map<String, String> labels = new java.util.HashMap<>();
        labels.put("en", "");
        new SelectOption("value", labels);
    }

    @Test
    public void testSelectSetSelectedValueEmptyString() {
        select.setOptions(options);
        select.setSelectedValue("manual");
        assertEquals("manual", select.getSelectedValue());

        // Empty string should be ignored
        select.setSelectedValue("");
        assertEquals("manual", select.getSelectedValue());
    }

    @Test
    public void testSelectSetSelectedValueWhitespace() {
        select.setOptions(options);
        select.setSelectedValue("manual");
        assertEquals("manual", select.getSelectedValue());

        // Whitespace should be ignored
        select.setSelectedValue("   ");
        assertEquals("manual", select.getSelectedValue());
    }

    @Test
    public void testSelectWithEmptyOptions() {
        select.setTheme(Theme.FREE_DARK);
        select.setLanguage(Language.EN);
        select.setOptions(new java.util.ArrayList<>());

        // Should not crash, just show empty state
        assertEquals("", select.getSelectedValue());
    }

    @Test
    public void testSelectOptionGetTextNullLanguage() {
        SelectOption opt = new SelectOption("val", Map.of("en", "English", "ru", "Русский"));
        // Should fallback to first available when null is passed
        String text = opt.getText(null);
        assertNotNull(text);
        assertTrue(text.equals("English") || text.equals("Русский"));
    }
}
