package ru.voboost.components.theme;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Visual regression tests for Theme component.
 *
 * Note: Theme is an enum, so visual tests are limited.
 * This test class exists for BEM architecture compliance.
 *
 * Theme values are used by other components which have
 * their own visual tests.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = {33})
public class ThemeTestVisual {

    @Test
    public void testThemeEnumExists() {
        assertNotNull("Theme enum should exist", Theme.values());
    }

    @Test
    public void testAllThemeValuesAvailable() {
        assertEquals("Should have 4 theme values", 4, Theme.values().length);
    }

    @Test
    public void testAllThemeValuesHaveNonNullStringRepresentation() {
        for (Theme theme : Theme.values()) {
            assertNotNull("Theme " + theme + " should have non-null value",
                    theme.getValue());
            assertFalse("Theme " + theme + " should have non-empty value",
                    theme.getValue().isEmpty());
        }
    }
}
