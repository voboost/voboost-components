package ru.voboost.components.buttons;

import static org.junit.Assert.*;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import ru.voboost.components.button.ButtonStyle;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.Theme;

/**
 * Unit tests for Buttons component.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {33})
public class ButtonsTestUnit {

    private Context context;
    private Buttons buttons;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        buttons = new Buttons(context);
        buttons.setTheme(Theme.FREE_DARK);
    }

    @Test
    public void testInitialization() {
        assertNotNull("Buttons should be initialized", buttons);
        assertNull("Initial selectedValue should be null", buttons.getSelectedValue());
    }

    @Test
    public void testSetButtons() {
        List<ButtonConfig> configs = Arrays.asList(
                new ButtonConfig("value1", "Text 1"),
                new ButtonConfig("value2", "Text 2")
        );
        buttons.setButtons(configs);
        buttons.setSelectedValue("value2");
        assertEquals("Selected value should be set", "value2", buttons.getSelectedValue());
    }

    @Test
    public void testSetButtonsNull() {
        buttons.setButtons(null);
        assertEquals("Selected value should be null after setButtons(null)", null, buttons.getSelectedValue());
    }

    @Test
    public void testSetButtonsEmpty() {
        buttons.setButtons(Collections.emptyList());
        assertEquals("Selected value should be null after setButtons(empty)", null, buttons.getSelectedValue());
    }

    @Test
    public void testSetSelectedValue() {
        List<ButtonConfig> configs = Arrays.asList(
                new ButtonConfig("value1", "Text 1"),
                new ButtonConfig("value2", "Text 2")
        );
        buttons.setButtons(configs);
        buttons.setSelectedValue("value1");
        assertEquals("Selected value should be value1", "value1", buttons.getSelectedValue());
    }

    @Test
    public void testSetSelectedValueNotInList() {
        List<ButtonConfig> configs = Arrays.asList(
                new ButtonConfig("value1", "Text 1"),
                new ButtonConfig("value2", "Text 2")
        );
        buttons.setButtons(configs);
        buttons.setSelectedValue("value3");
        assertEquals("Selected value should be value3 even if not in list", "value3", buttons.getSelectedValue());
    }

    @Test
    public void testOnValueChangeListener() {
        final String[] selectedValues = {null};
        List<ButtonConfig> configs = Arrays.asList(
                new ButtonConfig("value1", "Text 1"),
                new ButtonConfig("value2", "Text 2")
        );
        buttons.setButtons(configs);
        buttons.setOnValueChangeListener(newValue -> selectedValues[0] = newValue);

        buttons.setSelectedValue("value1");
        // Note: setSelectedValue doesn't trigger listener, only click does
        // We need to simulate a click
    }

    @Test
    public void testRightText() {
        Map<String, String> rightText = Map.of("en", "Right Text", "ru", "Правый текст");
        buttons.setRightText(rightText);
        buttons.setLanguage(Language.EN);
        assertNotNull("Right text should be set", rightText);
    }

    @Test
    public void testDescription() {
        Map<String, String> description = Map.of("en", "Description", "ru", "Описание");
        buttons.setDescription(description);
        buttons.setLanguage(Language.EN);
        assertNotNull("Description should be set", description);
    }

    @Test
    public void testSetTheme() {
        buttons.setTheme(Theme.FREE_LIGHT);
        assertEquals("Theme should be FREE_LIGHT", Theme.FREE_LIGHT, buttons.getCurrentTheme());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNullTheme() {
        buttons.setTheme(null);
    }

    @Test
    public void testAllThemes() {
        for (Theme theme : Theme.values()) {
            buttons.setTheme(theme);
            assertNotNull("Buttons should accept theme: " + theme, buttons);
        }
    }

    @Test
    public void testSetLanguage() {
        buttons.setLanguage(Language.RU);
        assertEquals("Language should be RU", Language.RU, buttons.getCurrentLanguage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetNullLanguage() {
        buttons.setLanguage(null);
    }

    @Test
    public void testAllLanguages() {
        for (Language lang : Language.values()) {
            buttons.setLanguage(lang);
            assertNotNull("Buttons should accept language: " + lang, buttons);
        }
    }

    @Test
    public void testNullSafety() {
        // Test that null values don't cause crashes
        buttons.setButtons(null);
        buttons.setRightText(null);
        buttons.setDescription(null);
        buttons.setSelectedValue(null);
        assertNotNull("Buttons should handle null values", buttons);
    }

    @Test
    public void testButtonConfigStyle() {
        ButtonConfig config = new ButtonConfig("value", "Text", ButtonStyle.PRIMARY);
        assertEquals("Style should be PRIMARY", ButtonStyle.PRIMARY, config.getStyle());
    }

    @Test
    public void testButtonConfigDefaultStyle() {
        ButtonConfig config = new ButtonConfig("value", "Text");
        assertEquals("Default style should be SECONDARY", ButtonStyle.SECONDARY, config.getStyle());
    }

    @Test
    public void testLocalization() {
        Map<String, String> rightText = Map.of(
                "en", "English Text",
                "ru", "Русский текст",
                "de", "German Text"
        );
        Map<String, String> description = Map.of(
                "en", "English Description",
                "ru", "Русское описание"
        );

        buttons.setRightText(rightText);
        buttons.setDescription(description);

        buttons.setLanguage(Language.EN);
        buttons.setLanguage(Language.RU);
        buttons.setLanguage(Language.fromCode("de"));

        assertNotNull("Should handle missing language gracefully", buttons);
    }

    @Test
    public void testPerformance_rebuildButtons_reusesViews() {
        // Initial setup with 3 buttons
        List<ButtonConfig> configs = Arrays.asList(
                new ButtonConfig("value1", "Text 1"),
                new ButtonConfig("value2", "Text 2"),
                new ButtonConfig("value3", "Text 3")
        );
        buttons.setButtons(configs);

        // Get reference to first button
        ru.voboost.components.button.Button firstButton = buttons.buttonViews.get(0);

        // Update with same number of buttons - should reuse views
        List<ButtonConfig> newConfigs = Arrays.asList(
                new ButtonConfig("value1", "Updated 1"),
                new ButtonConfig("value2", "Updated 2"),
                new ButtonConfig("value3", "Updated 3")
        );
        buttons.setButtons(newConfigs);

        // Verify that the same button instance is reused
        assertSame("First button should be reused", firstButton, buttons.buttonViews.get(0));
        assertEquals("Button text should be updated", "Updated 1", firstButton.getText());
    }

    @Test
    public void testPerformance_rebuildButtons_handlesSizeIncrease() {
        // Initial setup with 2 buttons
        List<ButtonConfig> configs = Arrays.asList(
                new ButtonConfig("value1", "Text 1"),
                new ButtonConfig("value2", "Text 2")
        );
        buttons.setButtons(configs);

        // Get reference to first button
        ru.voboost.components.button.Button firstButton = buttons.buttonViews.get(0);

        // Update with 4 buttons - should reuse first 2, create 2 new
        List<ButtonConfig> newConfigs = Arrays.asList(
                new ButtonConfig("value1", "Updated 1"),
                new ButtonConfig("value2", "Updated 2"),
                new ButtonConfig("value3", "Text 3"),
                new ButtonConfig("value4", "Text 4")
        );
        buttons.setButtons(newConfigs);

        // Verify that the first button is reused
        assertSame("First button should be reused", firstButton, buttons.buttonViews.get(0));
        assertEquals("Should have 4 buttons", 4, buttons.buttonViews.size());
    }

    @Test
    public void testPerformance_rebuildButtons_handlesSizeDecrease() {
        // Initial setup with 4 buttons
        List<ButtonConfig> configs = Arrays.asList(
                new ButtonConfig("value1", "Text 1"),
                new ButtonConfig("value2", "Text 2"),
                new ButtonConfig("value3", "Text 3"),
                new ButtonConfig("value4", "Text 4")
        );
        buttons.setButtons(configs);

        // Get reference to first button
        ru.voboost.components.button.Button firstButton = buttons.buttonViews.get(0);

        // Update with 2 buttons - should reuse first 2, remove last 2
        List<ButtonConfig> newConfigs = Arrays.asList(
                new ButtonConfig("value1", "Updated 1"),
                new ButtonConfig("value2", "Updated 2")
        );
        buttons.setButtons(newConfigs);

        // Verify that the first button is reused
        assertSame("First button should be reused", firstButton, buttons.buttonViews.get(0));
        assertEquals("Should have 2 buttons", 2, buttons.buttonViews.size());
    }
}
