package ru.voboost.components.demo.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.voboost.components.demo.shared.DemoContent
import ru.voboost.components.i18n.Language
import ru.voboost.components.theme.Theme

/**
 * Stateful entry point. Collects UI state from the ViewModel and forwards it to
 * the stateless content composable. Uses collectAsStateWithLifecycle (modern
 * lifecycle-aware StateFlow collection).
 */
@Composable
fun VoboostDemoApp(viewModel: DemoViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    VoboostDemoContent(
        selectedTab = uiState.selectedTab,
        combinedTheme = uiState.combinedTheme,
        currentLanguage = uiState.currentLanguage,
        currentTheme = uiState.currentTheme,
        screenLiftState = uiState.screenLiftState,
        onTabSelected = viewModel::onTabSelected,
        onScreenLift = viewModel::onScreenLiftChanged,
        onValueChange = viewModel::onValueChange,
    )
}

/**
 * Stateless content composable: pure UI that receives all state and callbacks.
 */
@Composable
fun VoboostDemoContent(
    selectedTab: String,
    combinedTheme: String,
    currentLanguage: String,
    currentTheme: String,
    screenLiftState: Int,
    onTabSelected: (String) -> Unit,
    onScreenLift: (Int) -> Unit,
    onValueChange: (String, String) -> Unit,
) {
    val backgroundColor = if (currentTheme == "dark") DemoColors.DarkBackground else DemoColors.LightBackground

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .semantics {
                testTag = "demo_root"
                contentDescription = "Voboost Components Demo"
            },
    ) {
        ScreenWrapper(
            selectedTab = selectedTab,
            combinedTheme = combinedTheme,
            currentLanguage = currentLanguage,
            screenLiftState = screenLiftState,
            onTabSelected = onTabSelected,
            onScreenLift = onScreenLift,
            onValueChange = onValueChange,
        )
    }
}

/**
 * Embeds the library Screen Compose wrapper. Panels are created once; theme and
 * language are propagated by the wrapper. The underlying Screen view is captured
 * via onScreenReady so toast actions can be dispatched imperatively.
 */
@Composable
fun ScreenWrapper(
    selectedTab: String,
    combinedTheme: String,
    currentLanguage: String,
    screenLiftState: Int,
    onTabSelected: (String) -> Unit,
    onScreenLift: (Int) -> Unit,
    onValueChange: (String, String) -> Unit,
) {
    val context = LocalContext.current

    val theme = remember(combinedTheme) { Theme.fromValue(combinedTheme) }
    val language = remember(currentLanguage) { Language.fromCode(currentLanguage) }

    // Always-current providers so dialog/toast read the latest theme and language.
    val latestTheme = rememberUpdatedState(theme)
    val latestLanguage = rememberUpdatedState(language)

    // Reference to the underlying Screen view for imperative toast dispatch.
    val screenRef = remember { mutableStateOf<ru.voboost.components.screen.Screen?>(null) }
    val showToast: (String, Long) -> Unit = remember {
        { text, duration -> screenRef.value?.showToast(text, duration) }
    }

    // Build panels once; the Screen wrapper propagates theme/language afterwards.
    val panels = remember {
        DemoTabs.TAB_VALUES.map { tabValue ->
            createPanelForTab(
                context = context,
                tabValue = tabValue,
                theme = theme,
                language = language,
                onValueChange = onValueChange,
                themeProvider = { latestTheme.value },
                languageProvider = { latestLanguage.value },
                showToast = showToast,
            )
        }.toTypedArray()
    }

    ru.voboost.components.screen.Screen(
        tabs = DemoContent.getTabItems(),
        panels = panels,
        selectedTab = selectedTab,
        onTabSelected = onTabSelected,
        theme = theme,
        language = language,
        screenLiftState = screenLiftState,
        onScreenLift = onScreenLift,
        onScreenReady = { screenRef.value = it },
    )
}
