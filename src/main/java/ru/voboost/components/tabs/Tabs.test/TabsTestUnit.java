package ru.voboost.components.tabs;

import static org.junit.Assert.*;

import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Test
    public void testDisabledTabNotSelected() {
        Map<String, String> labels = new HashMap<>();
        labels.put("en", "Test");

        TabItem enabledItem = new TabItem("enabled", labels, true);
        TabItem disabledItem = new TabItem("disabled", labels, false);

        tabs.setItems(Arrays.asList(enabledItem, disabledItem));
        tabs.setSelectedValue("enabled");

        tabs.setSelectedValue("disabled", true);

        // Should still be on enabled tab
        assertEquals("enabled", tabs.getSelectedValue());
    }

    @Test
    public void testDisabledTabNotSelectedViaTouch() {
        Map<String, String> labels = new HashMap<>();
        labels.put("en", "Test");

        TabItem enabledItem = new TabItem("enabled", labels, true);
        TabItem disabledItem = new TabItem("disabled", labels, false);

        tabs.setItems(Arrays.asList(enabledItem, disabledItem));
        tabs.setSelectedValue("enabled");

        // Measure and layout tabs
        int width = TabsTheme.SIDEBAR_WIDTH;
        int height = 1000;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, height);
        tabs.setLayoutParams(params);
        tabs.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(width, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(height, android.view.View.MeasureSpec.EXACTLY));
        tabs.layout(0, 0, width, height);

        // Simulate touch on disabled tab
        float y = tabs.getTopPadding() + TabsTheme.TAB_ITEM_HEIGHT + 40 + 1;
        MotionEvent event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_DOWN, 50f, y, 0);
        tabs.onTouchEvent(event);
        event.recycle();

        // Should still be on enabled tab
        assertEquals("enabled", tabs.getSelectedValue());
    }

    @Test
    public void testNullValueInSelectedValue() {
        tabs.setItems(testItems);
        tabs.setSelectedValue(null);
        assertEquals("", tabs.getSelectedValue());
    }

    @Test
    public void testSetFontCacheCleared() {
        tabs.setItems(testItems);
        tabs.setLanguage(Language.EN);
        tabs.setLanguage(Language.RU);
        // Should not throw exception
        assertNotNull(tabs);
    }

    @Test
    public void testTabItemDefaultNoMore() {
        Map<String, String> labels = new HashMap<>();
        labels.put("en", "Test");
        TabItem item = new TabItem("test", labels);
        assertFalse(item.hasMore());
    }

    @Test
    public void testTabItemMoreViaConstructor() {
        Map<String, String> labels = new HashMap<>();
        labels.put("en", "Test");
        TabItem item = new TabItem("test", labels, true, 40, true);
        assertTrue(item.hasMore());
    }

    @Test
    public void testTabItemBuilderMore() {
        Map<String, String> labels = new HashMap<>();
        labels.put("en", "Test");
        TabItem item = TabItem.create("test", labels).more(true).build();
        assertTrue(item.hasMore());
    }

    @Test
    public void testTabItemBuilderMoreDefaultFalse() {
        Map<String, String> labels = new HashMap<>();
        labels.put("en", "Test");
        TabItem item = TabItem.create("test", labels).build();
        assertFalse(item.hasMore());
    }

    @Test
    public void testTabItemEqualsDiffersByMore() {
        Map<String, String> labels = new HashMap<>();
        labels.put("en", "Test");
        TabItem a = new TabItem("test", labels, true, 40, true);
        TabItem b = new TabItem("test", labels, true, 40, false);
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void testDefaultTopPadding() {
        assertEquals(TabsTheme.DEFAULT_TOP_PADDING, tabs.getTopPadding());
    }

    @Test
    public void testSetTopPadding() {
        tabs.setTopPadding(80);
        assertEquals(80, tabs.getTopPadding());
    }

    @Test
    public void testDefaultBottomPadding() {
        assertEquals(5, tabs.getPaddingBottom());
    }

    @Test
    public void testMeasuredHeightIncludesPaddings() {
        tabs.setItems(testItems);
        tabs.measure(
                android.view.View.MeasureSpec.makeMeasureSpec(TabsTheme.SIDEBAR_WIDTH, android.view.View.MeasureSpec.EXACTLY),
                android.view.View.MeasureSpec.makeMeasureSpec(9999, android.view.View.MeasureSpec.AT_MOST));

        int expected = TabsTheme.DEFAULT_TOP_PADDING
                + testItems.size() * TabsTheme.TAB_ITEM_HEIGHT
                + (testItems.size() - 1) * 40
                + tabs.getPaddingBottom();
        assertEquals(expected, tabs.getMeasuredHeight());
    }
}
