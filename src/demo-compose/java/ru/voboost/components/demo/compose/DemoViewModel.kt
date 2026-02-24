package ru.voboost.components.demo.compose

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel for the Voboost Demo application.
 *
 * This ViewModel manages the UI state for the demo application using StateFlow.
 * It follows the Compose best practices for state management with ViewModels.
 *
 * The ViewModel handles:
 * - Tab selection
 * - Language switching
 * - Theme switching
 * - Car type selection
 * - Screen lift state
 * - Value changes for different tabs
 */
class DemoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DemoUiState())
    val uiState: StateFlow<DemoUiState> = _uiState.asStateFlow()

    /**
     * Updates the selected tab.
     *
     * @param tab The tab value to select
     */
    fun onTabSelected(tab: String) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    /**
     * Updates the current language.
     *
     * @param lang The language code (e.g., "en", "ru")
     */
    fun onLanguageChanged(lang: String) {
        _uiState.update { it.copy(currentLanguage = lang) }
    }

    /**
     * Updates the current theme.
     *
     * @param theme The theme value (e.g., "light", "dark")
     */
    fun onThemeChanged(theme: String) {
        _uiState.update { it.copy(currentTheme = theme) }
    }

    /**
     * Updates the current car type.
     *
     * @param carType The car type value (e.g., "free", "dreamer")
     */
    fun onCarTypeChanged(carType: String) {
        _uiState.update { it.copy(currentCarType = carType) }
    }

    /**
     * Updates the screen lift state.
     *
     * @param state The screen lift state (1 for lowered, 2 for raised)
     */
    fun onScreenLiftChanged(state: Int) {
        _uiState.update { it.copy(screenLiftState = state) }
    }

    /**
     * Handles value changes for different tabs.
     * Routes the value change to the appropriate state update.
     *
     * @param tab The tab that triggered the value change
     * @param newValue The new value
     */
    fun onValueChange(tab: String, newValue: String) {
        when (tab) {
            "language" -> onLanguageChanged(newValue)
            "theme" -> onThemeChanged(newValue)
            "car_type" -> onCarTypeChanged(newValue)
            // Other tabs (climate, audio, display, system) don't update state
            // but could be logged or tracked if needed
        }
    }
}
