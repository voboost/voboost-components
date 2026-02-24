package ru.voboost.components.demo.compose

/**
 * UI state for the Voboost Demo application.
 *
 * This data class holds all the state needed for the demo UI.
 * It's designed to be used with StateFlow in a ViewModel for proper
 * state management in Jetpack Compose.
 *
 * @property selectedTab Currently selected tab value
 * @property currentLanguage Current language code (e.g., "en", "ru")
 * @property currentTheme Current theme value (e.g., "light", "dark")
 * @property currentCarType Current car type value (e.g., "free", "dreamer")
 * @property screenLiftState Current screen lift state (1 for lowered, 2 for raised)
 */
data class DemoUiState(
    val selectedTab: String = "language",
    val currentLanguage: String = "en",
    val currentTheme: String = "dark",
    val currentCarType: String = "free",
    val screenLiftState: Int = 2,
) {
    /**
     * Combined theme string for use with the library components.
     * Format: "{carType}-{theme}" (e.g., "free-light", "dreamer-dark")
     */
    val combinedTheme: String get() = "$currentCarType-$currentTheme"
}
