package ru.voboost.components.tabs;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.Theme;

/**
 * Unit tests for the Tabs component.
 */
@RunWith(RobolectricTestRunner.class)
public class TabsTestUnit {

    private Tabs tabs;
    private List<TabItem> testItems;

    @Before
    public void setUp() {
        tabs = new Tabs(RuntimeEnvironment.getApplication());

        // Create test items
        testItems = new ArrayList<>();

        Map<String, String> storeLabels = new HashMap<>();
        storeLabels.put("en", "Store");
        storeLabels.put("ru", "Магазин");
        testItems.add(new TabItem("store", storeLabels));

        Map<String, String> settingsLabels = new HashMap<>();
        settingsLabels.put("en", "Settings");
        settingsLabels.put("ru", "Настройки");
        testItems.add(new TabItem("settings", settingsLabels));

        Map<String, String> vehicleLabels = new HashMap<>();
        vehicleLabels.put("en", "Vehicle");
        vehicleLabels.put("ru", "Автомобиль");
        testItems.add(new TabItem("vehicle", vehicleLabels));
    }

    @Test
    public void testSetItems() {
        tabs.setItems(testItems);
        // Items should be set without exception
        assertNotNull(tabs);
    }

    @Test
    public void testSetSelectedValue() {
        tabs.setItems(testItems);
        tabs.setSelectedValue("settings");
        assertEquals("settings", tabs.getSelectedValue());
    }

    @Test
    public void testSetTheme() {
        tabs.setTheme(Theme.FREE_LIGHT);
        assertEquals(Theme.FREE_LIGHT, tabs.getCurrentTheme());

        tabs.setTheme(Theme.DREAMER_DARK);
        assertEquals(Theme.DREAMER_DARK, tabs.getCurrentTheme());
    }

    @Test
    public void testSetLanguage() {
        tabs.setLanguage(Language.EN);
        assertEquals(Language.EN, tabs.getCurrentLanguage());

        tabs.setLanguage(Language.RU);
        assertEquals(Language.RU, tabs.getCurrentLanguage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetThemeNull() {
        tabs.setTheme(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetLanguageNull() {
        tabs.setLanguage(null);
    }

    @Test
    public void testOnValueChangeListener() {
        final String[] receivedValue = {null};

        tabs.setItems(testItems);
        tabs.setSelectedValue("store");
        tabs.setOnValueChangeListener(value -> receivedValue[0] = value);

        tabs.setSelectedValue("settings", true);

        assertEquals("settings", receivedValue[0]);
    }

    @Test
    public void testOnValueChangeListenerNotTriggeredWithoutFlag() {
        final String[] receivedValue = {null};

        tabs.setItems(testItems);
        tabs.setSelectedValue("store");
        tabs.setOnValueChangeListener(value -> receivedValue[0] = value);

        tabs.setSelectedValue("settings", false);

        assertNull(receivedValue[0]);
    }

    @Test
    public void testEmptyItems() {
        tabs.setItems(new ArrayList<>());
        tabs.setSelectedValue("nonexistent");
        assertEquals("nonexistent", tabs.getSelectedValue());
    }

    @Test
    public void testNullItems() {
        tabs.setItems(null);
        // Should not throw exception
        assertNotNull(tabs);
    }
}
