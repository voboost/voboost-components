package ru.voboost.components.screen;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import ru.voboost.components.i18n.Language;
import ru.voboost.components.panel.Panel;
import ru.voboost.components.tabs.TabItem;
import ru.voboost.components.tabs.Tabs;
import ru.voboost.components.theme.Theme;

/**
 * Unit tests for the Screen component.
 */
@RunWith(RobolectricTestRunner.class)
public class ScreenTestUnit {

    private Screen screen;

    @Before
    public void setUp() {
        screen = new Screen(androidx.test.core.app.ApplicationProvider.getApplicationContext());
    }

    @Test
    public void testSetTheme() {
        screen.setTheme(Theme.FREE_LIGHT);
        assertEquals(Theme.FREE_LIGHT, screen.getCurrentTheme());

        screen.setTheme(Theme.DREAMER_DARK);
        assertEquals(Theme.DREAMER_DARK, screen.getCurrentTheme());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetThemeNull() {
        screen.setTheme(null);
    }

    @Test
    public void testDefaultState() {
        // Screen should be created without exception
        assertNotNull(screen);
    }

    @Test
    public void testThemeChangeTriggersRedraw() {
        // Set initial theme
        screen.setTheme(Theme.FREE_LIGHT);

        // Change theme
        screen.setTheme(Theme.FREE_DARK);

        // Theme should be updated
        assertEquals(Theme.FREE_DARK, screen.getCurrentTheme());
    }

    @Test
    public void testDefaultGapX() {
        assertEquals("Default gapX should be 0", 0, screen.getGapX());
    }

    @Test
    public void testSetGapX() {
        screen.setGapX(100);
        assertEquals("GapX should be 100", 100, screen.getGapX());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetGapXNegative() {
        screen.setGapX(-1);
    }

    @Test
    public void testDefaultOffsetX() {
        assertEquals("Default offsetX should be 145", 145, screen.getOffsetX());
    }

    @Test
    public void testDefaultOffsetY() {
        assertEquals("Default offsetY should be 50", 50, screen.getOffsetY());
    }

    // ============================================================
    // SCREEN+TABS+PANEL INTEGRATION TESTS
    // ============================================================

    private List<TabItem> createTestTabItems() {
        List<TabItem> items = new ArrayList<>();

        Map<String, String> tab1Labels = new HashMap<>();
        tab1Labels.put("en", "Tab 1");
        items.add(new TabItem("tab1", tab1Labels));

        Map<String, String> tab2Labels = new HashMap<>();
        tab2Labels.put("en", "Tab 2");
        items.add(new TabItem("tab2", tab2Labels));

        return items;
    }

    @Test
    public void testTabChangeSwitchesPanel() {
        Tabs tabs = new Tabs(screen.getContext());
        tabs.setTheme(Theme.FREE_LIGHT);
        tabs.setLanguage(Language.EN);
        tabs.setItems(createTestTabItems());

        Panel panel1 = new Panel(screen.getContext());
        panel1.setTheme(Theme.FREE_LIGHT);
        Panel panel2 = new Panel(screen.getContext());
        panel2.setTheme(Theme.FREE_LIGHT);

        screen.setTheme(Theme.FREE_LIGHT);
        screen.setTabs(tabs);
        screen.setPanels(new Panel[]{panel1, panel2});

        // Select first tab
        tabs.setSelectedValue("tab1", true);
        assertEquals("Active panel should be panel1", panel1, screen.getActivePanel());

        // Switch to second tab
        tabs.setSelectedValue("tab2", true);
        assertEquals("Active panel should be panel2", panel2, screen.getActivePanel());
    }

    @Test
    public void testPanelAddedToScreenViewGroup() {
        Tabs tabs = new Tabs(screen.getContext());
        tabs.setTheme(Theme.FREE_LIGHT);
        tabs.setLanguage(Language.EN);
        tabs.setItems(createTestTabItems());

        Panel panel1 = new Panel(screen.getContext());
        panel1.setTheme(Theme.FREE_LIGHT);
        Panel panel2 = new Panel(screen.getContext());
        panel2.setTheme(Theme.FREE_LIGHT);

        screen.setTheme(Theme.FREE_LIGHT);
        screen.setTabs(tabs);
        screen.setPanels(new Panel[]{panel1, panel2});

        tabs.setSelectedValue("tab1", false);
        assertEquals("Panel1 parent should be screen", screen, panel1.getParent());

        // Switch to tab2
        tabs.setSelectedValue("tab2", true);
        assertEquals("Panel2 parent should be screen", screen, panel2.getParent());
        assertNull("Panel1 should be removed from screen", panel1.getParent());
    }

    @Test
    public void testSetActivePanelWithNoPanels() {
        screen.setTheme(Theme.FREE_LIGHT);
        screen.setActivePanel(0);
        assertNull("Active panel should be null when no panels set", screen.getActivePanel());
    }

    @Test
    public void testSetActivePanelOutOfBounds() {
        Panel panel1 = new Panel(screen.getContext());
        panel1.setTheme(Theme.FREE_LIGHT);
        screen.setTheme(Theme.FREE_LIGHT);
        screen.setPanels(new Panel[]{panel1});

        screen.setActivePanel(5);
        assertNull("Active panel should be null for out-of-bounds index", screen.getActivePanel());
    }
}
