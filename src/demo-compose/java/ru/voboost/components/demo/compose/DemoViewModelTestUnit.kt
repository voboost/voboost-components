package ru.voboost.components.demo.compose

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for DemoViewModel logic.
 *
 * These tests verify the ViewModel's state management and business logic.
 */
class DemoViewModelTest {

    private lateinit var viewModel: DemoViewModel

    @Before
    fun setup() {
        viewModel = DemoViewModel()
    }

    /**
     * Test: Verify initial state values
     */
    @Test
    fun testInitialState() {
        val uiState = viewModel.uiState.value

        assertEquals("language", uiState.selectedTab)
        assertEquals("en", uiState.currentLanguage)
        assertEquals("dark", uiState.currentTheme)
        assertEquals("free", uiState.currentCarType)
        assertEquals(2, uiState.screenLiftState)
        assertEquals("free-dark", uiState.combinedTheme)
    }

    /**
     * Test: Verify theme combination logic
     */
    @Test
    fun testThemeCombination() {
        val uiState = viewModel.uiState.value
        assertEquals("free-dark", uiState.combinedTheme)

        viewModel.onCarTypeChanged("dreamer")
        assertEquals("dreamer-dark", viewModel.uiState.value.combinedTheme)

        viewModel.onThemeChanged("light")
        assertEquals("dreamer-light", viewModel.uiState.value.combinedTheme)

        viewModel.onCarTypeChanged("free")
        assertEquals("free-light", viewModel.uiState.value.combinedTheme)

        viewModel.onThemeChanged("dark")
        assertEquals("free-dark", viewModel.uiState.value.combinedTheme)
    }

    /**
     * Test: Verify tab selection
     */
    @Test
    fun testTabSelection() {
        viewModel.onTabSelected("theme")
        assertEquals("theme", viewModel.uiState.value.selectedTab)

        viewModel.onTabSelected("car_type")
        assertEquals("car_type", viewModel.uiState.value.selectedTab)

        viewModel.onTabSelected("language")
        assertEquals("language", viewModel.uiState.value.selectedTab)
    }

    /**
     * Test: Verify language change
     */
    @Test
    fun testLanguageChange() {
        viewModel.onLanguageChanged("ru")
        assertEquals("ru", viewModel.uiState.value.currentLanguage)

        viewModel.onLanguageChanged("en")
        assertEquals("en", viewModel.uiState.value.currentLanguage)
    }

    /**
     * Test: Verify theme change
     */
    @Test
    fun testThemeChange() {
        viewModel.onThemeChanged("dark")
        assertEquals("dark", viewModel.uiState.value.currentTheme)

        viewModel.onThemeChanged("light")
        assertEquals("light", viewModel.uiState.value.currentTheme)
    }

    /**
     * Test: Verify car type change
     */
    @Test
    fun testCarTypeChange() {
        viewModel.onCarTypeChanged("dreamer")
        assertEquals("dreamer", viewModel.uiState.value.currentCarType)

        viewModel.onCarTypeChanged("free")
        assertEquals("free", viewModel.uiState.value.currentCarType)
    }

    /**
     * Test: Verify screen lift state change
     */
    @Test
    fun testScreenLiftStateChange() {
        viewModel.onScreenLiftChanged(1)
        assertEquals(1, viewModel.uiState.value.screenLiftState)

        viewModel.onScreenLiftChanged(2)
        assertEquals(2, viewModel.uiState.value.screenLiftState)
    }

    /**
     * Test: Verify value change routing
     */
    @Test
    fun testValueChangeRouting() {
        viewModel.onValueChange("language", "ru")
        assertEquals("ru", viewModel.uiState.value.currentLanguage)

        viewModel.onValueChange("theme", "dark")
        assertEquals("dark", viewModel.uiState.value.currentTheme)

        viewModel.onValueChange("car_type", "dreamer")
        assertEquals("dreamer", viewModel.uiState.value.currentCarType)

        // Verify other tabs don't affect state
        viewModel.onValueChange("climate", "manual")
        assertEquals("dreamer", viewModel.uiState.value.currentCarType)
    }

    /**
     * Test: Verify state immutability
     */
    @Test
    fun testStateImmutability() {
        val initialState = viewModel.uiState.value

        // Modify state through ViewModel
        viewModel.onLanguageChanged("ru")
        val newState = viewModel.uiState.value

        // Verify state changed
        assertNotEquals(initialState, newState)
        assertEquals("ru", newState.currentLanguage)

        // Initial state should remain unchanged
        assertEquals("en", initialState.currentLanguage)
    }

    /**
     * Test: Verify multiple state changes
     */
    @Test
    fun testMultipleStateChanges() {
        viewModel.onLanguageChanged("ru")
        viewModel.onThemeChanged("dark")
        viewModel.onCarTypeChanged("dreamer")
        viewModel.onTabSelected("theme")

        val uiState = viewModel.uiState.value
        assertEquals("theme", uiState.selectedTab)
        assertEquals("ru", uiState.currentLanguage)
        assertEquals("dark", uiState.currentTheme)
        assertEquals("dreamer", uiState.currentCarType)
        assertEquals("dreamer-dark", uiState.combinedTheme)
    }
}
