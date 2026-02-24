package ru.voboost.components.demo.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

/**
 * Preview of the VoboostDemoContent in default state.
 * English, Dark theme, Free car type.
 */
@Preview(name = "Default - English Dark Free", showBackground = true, widthDp = 1920, heightDp = 720)
@Composable
fun VoboostDemoContentPreviewDefault() {
    VoboostDemoContent(
        selectedTab = "language",
        combinedTheme = "free-dark",
        currentLanguage = "en",
        currentTheme = "dark",
        currentCarType = "free",
        screenLiftState = 2,
        onTabSelected = {},
        onScreenLift = {},
        onValueChange = { _, _ -> }
    )
}

/**
 * Preview of the VoboostDemoContent in Russian language.
 */
@Preview(name = "Russian - Dark Free", showBackground = true, widthDp = 1920, heightDp = 720)
@Composable
fun VoboostDemoContentPreviewRussian() {
    VoboostDemoContent(
        selectedTab = "language",
        combinedTheme = "free-dark",
        currentLanguage = "ru",
        currentTheme = "dark",
        currentCarType = "free",
        screenLiftState = 2,
        onTabSelected = {},
        onScreenLift = {},
        onValueChange = { _, _ -> }
    )
}

/**
 * Preview of the VoboostDemoContent in Dark theme.
 */
@Preview(name = "Dark Theme - English Free", showBackground = true, widthDp = 1920, heightDp = 720)
@Composable
fun VoboostDemoContentPreviewDark() {
    VoboostDemoContent(
        selectedTab = "theme",
        combinedTheme = "free-dark",
        currentLanguage = "en",
        currentTheme = "dark",
        currentCarType = "free",
        screenLiftState = 2,
        onTabSelected = {},
        onScreenLift = {},
        onValueChange = { _, _ -> }
    )
}

/**
 * Preview of the VoboostDemoContent with Dreamer car type.
 */
@Preview(name = "Dreamer - English Dark", showBackground = true, widthDp = 1920, heightDp = 720)
@Composable
fun VoboostDemoContentPreviewDreamer() {
    VoboostDemoContent(
        selectedTab = "car_type",
        combinedTheme = "dreamer-dark",
        currentLanguage = "en",
        currentTheme = "dark",
        currentCarType = "dreamer",
        screenLiftState = 2,
        onTabSelected = {},
        onScreenLift = {},
        onValueChange = { _, _ -> }
    )
}

/**
 * Preview of the VoboostDemoContent with full combination.
 * Russian, Dark theme, Dreamer car type.
 */
@Preview(name = "Full Combination - Russian Dark Dreamer", showBackground = true, widthDp = 1920, heightDp = 720)
@Composable
fun VoboostDemoContentPreviewFullCombination() {
    VoboostDemoContent(
        selectedTab = "language",
        combinedTheme = "dreamer-dark",
        currentLanguage = "ru",
        currentTheme = "dark",
        currentCarType = "dreamer",
        screenLiftState = 2,
        onTabSelected = {},
        onScreenLift = {},
        onValueChange = { _, _ -> }
    )
}

/**
 * Preview of the VoboostDemoContent with Dreamer Dark theme.
 * English, Dark theme, Dreamer car type.
 */
@Preview(name = "Dreamer Dark - English Dark Dreamer", showBackground = true, widthDp = 1920, heightDp = 720)
@Composable
fun VoboostDemoContentPreviewDreamerDark() {
    VoboostDemoContent(
        selectedTab = "car_type",
        combinedTheme = "dreamer-dark",
        currentLanguage = "en",
        currentTheme = "dark",
        currentCarType = "dreamer",
        screenLiftState = 2,
        onTabSelected = {},
        onScreenLift = {},
        onValueChange = { _, _ -> }
    )
}

/**
 * Preview of the VoboostDemoContent with Russian Dark Free.
 * Russian, Dark theme, Free car type.
 */
@Preview(name = "Russian Dark Free - Russian Dark Free", showBackground = true, widthDp = 1920, heightDp = 720)
@Composable
fun VoboostDemoContentPreviewRussianDarkFree() {
    VoboostDemoContent(
        selectedTab = "language",
        combinedTheme = "free-dark",
        currentLanguage = "ru",
        currentTheme = "dark",
        currentCarType = "free",
        screenLiftState = 2,
        onTabSelected = {},
        onScreenLift = {},
        onValueChange = { _, _ -> }
    )
}
