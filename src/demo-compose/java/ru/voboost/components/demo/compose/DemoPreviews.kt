package ru.voboost.components.demo.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Settings - English Dark Free", showBackground = true, widthDp = 1920, heightDp = 720)
@Composable
fun VoboostDemoContentPreviewDefault() {
    VoboostDemoContent(
        selectedTab = "settings",
        combinedTheme = "free-dark",
        currentLanguage = "en",
        currentTheme = "dark",
        screenLiftState = 2,
        onTabSelected = {},
        onScreenLift = {},
        onValueChange = { _, _ -> },
    )
}

@Preview(name = "Settings - Russian Dark Free", showBackground = true, widthDp = 1920, heightDp = 720)
@Composable
fun VoboostDemoContentPreviewRussian() {
    VoboostDemoContent(
        selectedTab = "settings",
        combinedTheme = "free-dark",
        currentLanguage = "ru",
        currentTheme = "dark",
        screenLiftState = 2,
        onTabSelected = {},
        onScreenLift = {},
        onValueChange = { _, _ -> },
    )
}

@Preview(name = "Settings - English Light Free", showBackground = true, widthDp = 1920, heightDp = 720)
@Composable
fun VoboostDemoContentPreviewLight() {
    VoboostDemoContent(
        selectedTab = "settings",
        combinedTheme = "free-light",
        currentLanguage = "en",
        currentTheme = "light",
        screenLiftState = 2,
        onTabSelected = {},
        onScreenLift = {},
        onValueChange = { _, _ -> },
    )
}

@Preview(name = "Settings - English Dark Dreamer", showBackground = true, widthDp = 1920, heightDp = 720)
@Composable
fun VoboostDemoContentPreviewDreamer() {
    VoboostDemoContent(
        selectedTab = "settings",
        combinedTheme = "dreamer-dark",
        currentLanguage = "en",
        currentTheme = "dark",
        screenLiftState = 2,
        onTabSelected = {},
        onScreenLift = {},
        onValueChange = { _, _ -> },
    )
}

@Preview(name = "Button tab - English Dark Free", showBackground = true, widthDp = 1920, heightDp = 720)
@Composable
fun VoboostDemoContentPreviewButton() {
    VoboostDemoContent(
        selectedTab = "button",
        combinedTheme = "free-dark",
        currentLanguage = "en",
        currentTheme = "dark",
        screenLiftState = 2,
        onTabSelected = {},
        onScreenLift = {},
        onValueChange = { _, _ -> },
    )
}

@Preview(name = "Checkbox tab - English Dark Free", showBackground = true, widthDp = 1920, heightDp = 720)
@Composable
fun VoboostDemoContentPreviewCheckbox() {
    VoboostDemoContent(
        selectedTab = "checkbox",
        combinedTheme = "free-dark",
        currentLanguage = "en",
        currentTheme = "dark",
        screenLiftState = 2,
        onTabSelected = {},
        onScreenLift = {},
        onValueChange = { _, _ -> },
    )
}
