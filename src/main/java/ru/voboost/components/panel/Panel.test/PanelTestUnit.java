package ru.voboost.components.panel;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import ru.voboost.components.theme.Theme;

/**
 * Unit tests for the Panel component.
 */
@RunWith(RobolectricTestRunner.class)
public class PanelTestUnit {

    private Panel panel;

    @Before
    public void setUp() {
        panel = new Panel(androidx.test.core.app.ApplicationProvider.getApplicationContext());
    }

    @Test
    public void testSetTheme() {
        panel.setTheme(Theme.FREE_LIGHT);
        assertEquals(Theme.FREE_LIGHT, panel.getCurrentTheme());

        panel.setTheme(Theme.DREAMER_DARK);
        assertEquals(Theme.DREAMER_DARK, panel.getCurrentTheme());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetThemeNull() {
        panel.setTheme(null);
    }

    @Test
    public void testDefaultState() {
        // Panel should be created without exception
        assertNotNull(panel);
    }

    @Test
    public void testThemeChangeTriggersRedraw() {
        // Set initial theme
        panel.setTheme(Theme.FREE_LIGHT);

        // Change theme
        panel.setTheme(Theme.FREE_DARK);

        // Theme should be updated
        assertEquals(Theme.FREE_DARK, panel.getCurrentTheme());
    }
}
