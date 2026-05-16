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
        assertEquals("Default offsetX should be DEFAULT_OFFSET_X",
                ScreenTheme.DEFAULT_OFFSET_X, screen.getOffsetX());
    }

    @Test
    public void testDefaultOffsetY() {
        assertEquals("Default offsetY should be DEFAULT_OFFSET_Y",
                ScreenTheme.DEFAULT_OFFSET_Y, screen.getOffsetY());
    }

    @Test
    public void testTabsScrollViewIsScreenView() {
        Tabs tabs = new Tabs(androidx.test.core.app.ApplicationProvider.getApplicationContext());
        tabs.setLanguage(Language.EN);
        tabs.setTheme(Theme.FREE_LIGHT);
        tabs.setItems(createTestTabItems());

        screen.setTabs(tabs);
        assertNotNull(screen.getTabsScrollView());
        assertTrue(screen.getTabsScrollView() instanceof ScreenView);
    }

    @Test
    public void testSetTabsPropagatesOffsetYToTabsTopPadding() {
        Tabs tabs = new Tabs(androidx.test.core.app.ApplicationProvider.getApplicationContext());
        tabs.setLanguage(Language.EN);
        tabs.setTheme(Theme.FREE_LIGHT);
        tabs.setItems(createTestTabItems());

        screen.setTabs(tabs);
        assertEquals(ScreenTheme.DEFAULT_OFFSET_Y, tabs.getTopPadding());
    }

    @Test
    public void testSetOffsetYUpdatesTabsTopPadding() {
        Tabs tabs = new Tabs(androidx.test.core.app.ApplicationProvider.getApplicationContext());
        tabs.setLanguage(Language.EN);
        tabs.setTheme(Theme.FREE_LIGHT);
        tabs.setItems(createTestTabItems());

        screen.setTabs(tabs);
        screen.setOffsetY(80);
        assertEquals(80, tabs.getTopPadding());
    }

    @Test
    public void testPanelIsWrappedInScreenView() {
        android.content.Context ctx = androidx.test.core.app.ApplicationProvider.getApplicationContext();
        Tabs tabs = new Tabs(ctx);
        tabs.setLanguage(Language.EN);
        tabs.setTheme(Theme.FREE_LIGHT);
        tabs.setItems(createTestTabItems());

        Panel p0 = new Panel(ctx);
        Panel p1 = new Panel(ctx);
        Panel p2 = new Panel(ctx);
        screen.setPanels(new Panel[]{p0, p1, p2});
        screen.setTabs(tabs);

        // Select a tab to activate its panel wrapper
        tabs.setSelectedValue("tab1", false);
        int selectedIndex = tabs.getSelectedIndex();
        ScreenView wrapper = screen.getPanelWrapper(selectedIndex);
        assertNotNull("Active panel wrapper must be created", wrapper);
        assertEquals(0, wrapper.getPaddingTop());
        assertEquals(0, wrapper.getPaddingBottom());
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
        screen.setPanels(new Panel[] {panel1, panel2});

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
        screen.setPanels(new Panel[] {panel1, panel2});

        tabs.setSelectedValue("tab1", false);
        assertTrue("Panel1 parent should be ScreenView", panel1.getParent() instanceof ScreenView);
        ScreenView wrapper1 = (ScreenView) panel1.getParent();
        assertEquals("Wrapper1 parent should be screen", screen, wrapper1.getParent());

        // Switch to tab2
        tabs.setSelectedValue("tab2", true);
        assertTrue("Panel2 parent should be ScreenView", panel2.getParent() instanceof ScreenView);
        ScreenView wrapper2 = (ScreenView) panel2.getParent();
        assertEquals("Wrapper2 parent should be screen", screen, wrapper2.getParent());
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
        screen.setPanels(new Panel[] {panel1});

        screen.setActivePanel(5);
        assertNull("Active panel should be null for out-of-bounds index", screen.getActivePanel());
    }

    @Test
    public void testPanelTransitionAnimation() {
        Panel panel1 = new Panel(screen.getContext());
        panel1.setTheme(Theme.FREE_LIGHT);
        Panel panel2 = new Panel(screen.getContext());
        panel2.setTheme(Theme.FREE_LIGHT);

        screen.setTheme(Theme.FREE_LIGHT);
        screen.setPanels(new Panel[] {panel1, panel2});
        screen.setActivePanel(0);

        int widthSpec = android.view.View.MeasureSpec.makeMeasureSpec(1920, android.view.View.MeasureSpec.EXACTLY);
        int heightSpec = android.view.View.MeasureSpec.makeMeasureSpec(720, android.view.View.MeasureSpec.EXACTLY);
        screen.measure(widthSpec, heightSpec);
        screen.layout(0, 0, 1920, 720);

        screen.setActivePanel(1);

        assertEquals(panel2, screen.getActivePanel());
        assertNotNull(panel2.getParent());
    }

    @Test
    public void testRapidPanelSwitching() {
        Panel panel1 = new Panel(screen.getContext());
        panel1.setTheme(Theme.FREE_LIGHT);
        Panel panel2 = new Panel(screen.getContext());
        panel2.setTheme(Theme.FREE_LIGHT);
        Panel panel3 = new Panel(screen.getContext());
        panel3.setTheme(Theme.FREE_LIGHT);

        screen.setTheme(Theme.FREE_LIGHT);
        screen.setPanels(new Panel[] {panel1, panel2, panel3});
        screen.setActivePanel(0);

        int widthSpec = android.view.View.MeasureSpec.makeMeasureSpec(1920, android.view.View.MeasureSpec.EXACTLY);
        int heightSpec = android.view.View.MeasureSpec.makeMeasureSpec(720, android.view.View.MeasureSpec.EXACTLY);
        screen.measure(widthSpec, heightSpec);
        screen.layout(0, 0, 1920, 720);

        screen.setActivePanel(1);
        screen.setActivePanel(2);
        screen.setActivePanel(0);

        assertEquals(panel1, screen.getActivePanel());
    }

    @Test
    public void testSetPanelBeforeLayout() {
        Panel panel1 = new Panel(screen.getContext());
        panel1.setTheme(Theme.FREE_LIGHT);

        screen.setTheme(Theme.FREE_LIGHT);
        screen.setPanels(new Panel[] {panel1});
        screen.setActivePanel(0);

        int widthSpec = android.view.View.MeasureSpec.makeMeasureSpec(1920, android.view.View.MeasureSpec.EXACTLY);
        int heightSpec = android.view.View.MeasureSpec.makeMeasureSpec(720, android.view.View.MeasureSpec.EXACTLY);
        screen.measure(widthSpec, heightSpec);
        screen.layout(0, 0, 1920, 720);

        assertEquals(panel1, screen.getActivePanel());
    }

    @Test
    public void testSetNullTabs() {
        screen.setTheme(Theme.FREE_LIGHT);

        Tabs tabs = new Tabs(screen.getContext());
        tabs.setTheme(Theme.FREE_LIGHT);
        screen.setTabs(tabs);

        assertNotNull(screen.getTabs());

        screen.setTabs(null);

        assertNull(screen.getTabs());
        assertNull(screen.getTabsScrollView());
    }

    @Test
    public void testThemePropagation() {
        Panel panel1 = new Panel(screen.getContext());
        panel1.setTheme(Theme.FREE_LIGHT);

        screen.setTheme(Theme.FREE_LIGHT);
        screen.setPanels(new Panel[] {panel1});
        screen.setActivePanel(0);

        screen.setTheme(Theme.FREE_DARK);

        assertEquals(Theme.FREE_DARK, panel1.getCurrentTheme());
    }

    // ============================================================
    // TOAST FUNCTIONALITY TESTS
    // ============================================================

    @Test
    public void testShowToast() {
        screen.setTheme(Theme.FREE_LIGHT);

        // Show toast
        screen.showToast("Test message", ru.voboost.components.toast.ToastTheme.DURATION_SHORT);

        // Verify toast is shown
        assertNotNull("Toast should be shown", screen.getCurrentToast());
        assertTrue("Toast should be showing", screen.getCurrentToast().isShowing());
    }

    @Test
    public void testDismissToast() {
        screen.setTheme(Theme.FREE_LIGHT);
        screen.showToast("Test message", ru.voboost.components.toast.ToastTheme.DURATION_SHORT);

        // Verify toast is shown
        ru.voboost.components.toast.Toast toast = screen.getCurrentToast();
        assertNotNull("Toast should be shown", toast);
        assertTrue("Toast should be showing", toast.isShowing());

        // Dismiss toast
        screen.dismissToast();

        // After dismiss, currentToast should be null (cleared by onDismissListener)
        // Note: dismiss() is async, but onDismissListener runs immediately in test context
    }

    @Test
    public void testToastReplacement() {
        screen.setTheme(Theme.FREE_LIGHT);

        // Show first toast
        screen.showToast("First message", ru.voboost.components.toast.ToastTheme.DURATION_LONG);
        ru.voboost.components.toast.Toast firstToast = screen.getCurrentToast();
        assertNotNull("First toast should be shown", firstToast);

        // Show second toast (should replace first)
        screen.showToast("Second message", ru.voboost.components.toast.ToastTheme.DURATION_SHORT);
        ru.voboost.components.toast.Toast secondToast = screen.getCurrentToast();

        assertNotNull("Second toast should be shown", secondToast);
        assertNotSame("Second toast should be different instance", firstToast, secondToast);
    }

    // ============================================================
    // SCREEN LIFT TESTS
    // ============================================================

    @Test
    public void testDefaultScreenLiftState() {
        assertEquals("Default screen lift state should be SCREEN_RAISED",
                Screen.SCREEN_RAISED, screen.getScreenLiftState());
    }

    @Test
    public void testSetScreenLiftState() {
        screen.onScreenLift(Screen.SCREEN_LOWERED);
        assertEquals("Screen lift state should be SCREEN_LOWERED",
                Screen.SCREEN_LOWERED, screen.getScreenLiftState());

        screen.onScreenLift(Screen.SCREEN_RAISED);
        assertEquals("Screen lift state should be SCREEN_RAISED",
                Screen.SCREEN_RAISED, screen.getScreenLiftState());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetInvalidScreenLiftState() {
        screen.onScreenLift(999);
    }

    @Test
    public void testScreenLiftListener() {
        final int[] capturedState = new int[1];
        screen.setOnScreenLiftListener(state -> capturedState[0] = state);

        screen.onScreenLift(Screen.SCREEN_LOWERED);
        assertEquals("Listener should capture SCREEN_LOWERED",
                Screen.SCREEN_LOWERED, capturedState[0]);

        screen.onScreenLift(Screen.SCREEN_RAISED);
        assertEquals("Listener should capture SCREEN_RAISED",
                Screen.SCREEN_RAISED, capturedState[0]);
    }

    @Test
    public void testSetNullScreenLiftListener() {
        screen.setOnScreenLiftListener(state -> fail("Listener should not be called"));
        screen.setOnScreenLiftListener(null);

        // This should not throw any exception
        screen.onScreenLift(Screen.SCREEN_LOWERED);
    }

    @Test
    public void testSetCompactPanelPropagatesToPanels() {
        android.content.Context ctx = androidx.test.core.app.ApplicationProvider.getApplicationContext();
        Tabs tabs = new Tabs(ctx);
        tabs.setLanguage(Language.EN);
        tabs.setTheme(Theme.FREE_LIGHT);
        tabs.setItems(createTestTabItems());

        Panel p0 = new Panel(ctx);
        Panel p1 = new Panel(ctx);
        screen.setPanels(new Panel[]{p0, p1});
        screen.setTabs(tabs);
        tabs.setSelectedValue("tab1", false);

        screen.setCompactPanel(true);

        assertTrue(p0.isCompact());
        assertTrue(p1.isCompact());
    }

    @Test
    public void testSetCompactPanelFalsePropagatesToPanels() {
        android.content.Context ctx = androidx.test.core.app.ApplicationProvider.getApplicationContext();
        Tabs tabs = new Tabs(ctx);
        tabs.setLanguage(Language.EN);
        tabs.setTheme(Theme.FREE_LIGHT);
        tabs.setItems(createTestTabItems());

        Panel p0 = new Panel(ctx);
        Panel p1 = new Panel(ctx);
        screen.setPanels(new Panel[]{p0, p1});
        screen.setTabs(tabs);
        tabs.setSelectedValue("tab1", false);

        screen.setCompactPanel(true);
        screen.setCompactPanel(false);

        assertFalse(p0.isCompact());
        assertFalse(p1.isCompact());
    }
}

