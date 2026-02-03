package ru.voboost.components.demo.compose

import android.graphics.Color as AndroidColor
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.ComposeView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.voboost.components.radio.Radio
import ru.voboost.components.radio.RadioButton
import ru.voboost.components.i18n.Language
import ru.voboost.components.theme.Theme

/**
 * Demo Compose Activity showcasing voboost-components Radio component usage in Jetpack Compose projects.
 *
 * This demo demonstrates:
 * - Jetpack Compose integration with voboost-components library
 * - Automotive-oriented layout (1920x720 resolution)
 * - Multi-language support (English/Russian)
 * - Theme switching (Light/Dark)
 * - Car type selection (Free/Dreamer)
 * - Reactive state management using Compose state
 * - Modern Compose patterns and best practices
 */
class MainActivity : ComponentActivity() {
    /**
     * Root layout for screenshot capture in tests.
     * This FrameLayout wraps the ComposeView to enable View-based screenshot capture.
     */
    lateinit var rootLayout: FrameLayout
        private set

    // Radio view references for testing
    var languageRadioView: ru.voboost.components.radio.Radio? = null
    var themeRadioView: ru.voboost.components.radio.Radio? = null
    var carTypeRadioView: ru.voboost.components.radio.Radio? = null
    var testRadioView: ru.voboost.components.radio.Radio? = null

    companion object {
        private const val TAG = "ComposeDemo"

        // Layout constants (using dp that approximate px on automotive displays)
        const val CONTAINER_PADDING_HORIZONTAL = 64
        const val CONTAINER_PADDING_VERTICAL = 48
        const val SECTION_SPACING = 24
        const val TITLE_BOTTOM_MARGIN = 32
        const val SECTION_TITLE_BOTTOM_MARGIN = 16
        const val RADIO_MARGIN_BOTTOM = 32

        // Background colors (lowercase hex!)
        const val BACKGROUND_COLOR_LIGHT = 0xFFF1F5FB
        const val BACKGROUND_COLOR_DARK = 0xFF000000

        // Text colors (lowercase hex!)
        const val TEXT_COLOR_LIGHT = 0xFF1A1A1A
        const val TEXT_COLOR_DARK = 0xFFFFFFFF
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable full-screen immersive mode for automotive display
        setupFullScreenMode()

        // Create FrameLayout as root container
        rootLayout = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Create ComposeView and add it to FrameLayout
        val composeView = ComposeView(this).apply {
            setContent {
                DemoTheme {
                    DemoScreen(activity = this@MainActivity)
                }
            }
        }

        // Add ComposeView to FrameLayout
        rootLayout.addView(composeView)

        // Set FrameLayout as content view
        setContentView(rootLayout)
    }

    /**
     * Sets up full-screen immersive mode to hide system navigation bar
     */
    @Suppress("DEPRECATION")
    private fun setupFullScreenMode() {
        // Hide system UI for automotive display
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        )

        // Keep screen on for automotive use
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
}

@Composable
fun DemoScreen(activity: MainActivity? = null) {
    // Compose state management using remember and mutableStateOf
    var currentLanguage by remember { mutableStateOf("en") }
    var currentTheme by remember { mutableStateOf("light") }
    var currentCarType by remember { mutableStateOf("free") }
    var currentTestValue by remember { mutableStateOf("close") }

    // Computed property for combined theme
    val combinedTheme = "$currentCarType-$currentTheme"

    // Determine colors based on theme
    val backgroundColor = if (currentTheme == "dark") {
        Color(MainActivity.BACKGROUND_COLOR_DARK)
    } else {
        Color(MainActivity.BACKGROUND_COLOR_LIGHT)
    }

    val textColor = if (currentTheme == "dark") {
        Color(MainActivity.TEXT_COLOR_DARK)
    } else {
        Color(MainActivity.TEXT_COLOR_LIGHT)
    }

    // Log state changes for debugging
    Log.d("ComposeDemo", "State - Language: $currentLanguage, Theme: $combinedTheme")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            // Automotive-appropriate spacing (using dp that approximate px)
            .padding(MainActivity.CONTAINER_PADDING_HORIZONTAL.dp, MainActivity.CONTAINER_PADDING_VERTICAL.dp)
    ) {
        // Title with reactive text based on language
        Text(
            text = when (currentLanguage) {
                "ru" -> "Voboost Components - Compose Демо"
                else -> "Voboost Components - Compose Demo"
            },
            fontSize = 24.sp,
            color = textColor,
            modifier = Modifier.padding(bottom = MainActivity.TITLE_BOTTOM_MARGIN.dp)
        )

        // Language Radio Section
        LanguageRadioSection(
            currentLanguage = currentLanguage,
            combinedTheme = combinedTheme,
            textColor = textColor,
            onLanguageChange = { newLanguage ->
                Log.d("ComposeDemo", "Language changed to: $newLanguage")
                currentLanguage = newLanguage
            },
            onViewCreated = { activity?.languageRadioView = it }
        )

        Spacer(modifier = Modifier.height(MainActivity.SECTION_SPACING.dp))

        // Theme Radio Section
        ThemeRadioSection(
            currentLanguage = currentLanguage,
            currentTheme = currentTheme,
            combinedTheme = combinedTheme,
            textColor = textColor,
            onThemeChange = { newTheme ->
                Log.d("ComposeDemo", "Theme changed to: $newTheme")
                currentTheme = newTheme
            },
            onViewCreated = { activity?.themeRadioView = it }
        )

        Spacer(modifier = Modifier.height(MainActivity.SECTION_SPACING.dp))

        // Car Type Radio Section
        CarTypeRadioSection(
            currentLanguage = currentLanguage,
            currentCarType = currentCarType,
            combinedTheme = combinedTheme,
            textColor = textColor,
            onCarTypeChange = { newCarType ->
                Log.d("ComposeDemo", "Car type changed to: $newCarType")
                currentCarType = newCarType
            },
            onViewCreated = { activity?.carTypeRadioView = it }
        )

        Spacer(modifier = Modifier.height(MainActivity.SECTION_SPACING.dp))

        // Test Radio Section
        TestRadioSection(
            currentLanguage = currentLanguage,
            currentTestValue = currentTestValue,
            combinedTheme = combinedTheme,
            textColor = textColor,
            onTestValueChange = { newTestValue ->
                Log.d("ComposeDemo", "Test value changed to: $newTestValue")
                currentTestValue = newTestValue
            },
            onViewCreated = { activity?.testRadioView = it }
        )

        Spacer(modifier = Modifier.height(MainActivity.TITLE_BOTTOM_MARGIN.dp))

        // Current State Display
        StateDisplaySection(
            currentLanguage = currentLanguage,
            currentTheme = currentTheme,
            currentCarType = currentCarType,
            currentTestValue = currentTestValue,
            combinedTheme = combinedTheme,
            textColor = textColor
        )
    }
}

@Composable
fun LanguageRadioSection(
    currentLanguage: String,
    combinedTheme: String,
    textColor: Color,
    onLanguageChange: (String) -> Unit,
    onViewCreated: ((ru.voboost.components.radio.Radio) -> Unit)? = null
) {
    Column {
        Text(
            text = when (currentLanguage) {
                "ru" -> "Выбор языка:"
                else -> "Language Selection:"
            },
            fontSize = 18.sp,
            color = textColor,
            modifier = Modifier.padding(bottom = MainActivity.SECTION_TITLE_BOTTOM_MARGIN.dp)
        )

        // Create language radio buttons using Compose-friendly approach
        val languageButtons = remember {
            listOf(
                RadioButton(
                    "en",
                    mapOf(
                        "en" to "English",
                        "ru" to "English"
                    )
                ),
                RadioButton(
                    "ru",
                    mapOf(
                        "en" to "Русский",
                        "ru" to "Русский"
                    )
                )
            )
        }

        Radio(
            buttons = languageButtons,
            lang = Language.fromCode(currentLanguage),
            theme = Theme.fromValue(combinedTheme),
            value = currentLanguage,
            onValueChange = onLanguageChange,
            onViewCreated = onViewCreated
        )
    }
}

@Composable
fun ThemeRadioSection(
    currentLanguage: String,
    currentTheme: String,
    combinedTheme: String,
    textColor: Color,
    onThemeChange: (String) -> Unit,
    onViewCreated: ((ru.voboost.components.radio.Radio) -> Unit)? = null
) {
    Column {
        Text(
            text = when (currentLanguage) {
                "ru" -> "Выбор темы:"
                else -> "Theme Selection:"
            },
            fontSize = 18.sp,
            color = textColor,
            modifier = Modifier.padding(bottom = MainActivity.SECTION_TITLE_BOTTOM_MARGIN.dp)
        )

        // Create theme radio buttons using Compose-friendly approach
        val themeButtons = remember {
            listOf(
                RadioButton(
                    "light",
                    mapOf(
                        "en" to "Light",
                        "ru" to "Светлая"
                    )
                ),
                RadioButton(
                    "dark",
                    mapOf(
                        "en" to "Dark",
                        "ru" to "Тёмная"
                    )
                )
            )
        }

        Radio(
            buttons = themeButtons,
            lang = Language.fromCode(currentLanguage),
            theme = Theme.fromValue(combinedTheme),
            value = currentTheme,
            onValueChange = onThemeChange,
            onViewCreated = onViewCreated
        )
    }
}

@Composable
fun CarTypeRadioSection(
    currentLanguage: String,
    currentCarType: String,
    combinedTheme: String,
    textColor: Color,
    onCarTypeChange: (String) -> Unit,
    onViewCreated: ((ru.voboost.components.radio.Radio) -> Unit)? = null
) {
    Column {
        Text(
            text = when (currentLanguage) {
                "ru" -> "Выбор типа авто:"
                else -> "Car Type Selection:"
            },
            fontSize = 18.sp,
            color = textColor,
            modifier = Modifier.padding(bottom = MainActivity.SECTION_TITLE_BOTTOM_MARGIN.dp)
        )

        // Create car type radio buttons using Compose-friendly approach
        val carTypeButtons = remember {
            listOf(
                RadioButton(
                    "free",
                    mapOf(
                        "en" to "Free",
                        "ru" to "Фри"
                    )
                ),
                RadioButton(
                    "dreamer",
                    mapOf(
                        "en" to "Dreamer",
                        "ru" to "Дример"
                    )
                )
            )
        }

        Radio(
            buttons = carTypeButtons,
            lang = Language.fromCode(currentLanguage),
            theme = Theme.fromValue(combinedTheme),
            value = currentCarType,
            onValueChange = onCarTypeChange,
            onViewCreated = onViewCreated
        )
    }
}

@Composable
fun TestRadioSection(
    currentLanguage: String,
    currentTestValue: String,
    combinedTheme: String,
    textColor: Color,
    onTestValueChange: (String) -> Unit,
    onViewCreated: ((ru.voboost.components.radio.Radio) -> Unit)? = null
) {
    Column {
        Text(
            text = when (currentLanguage) {
                "ru" -> "Тестовый выбор:"
                else -> "Test Selection:"
            },
            fontSize = 18.sp,
            color = textColor,
            modifier = Modifier.padding(bottom = MainActivity.SECTION_TITLE_BOTTOM_MARGIN.dp)
        )

        // Create test radio buttons with long text for animation testing
        val testButtons = remember {
            listOf(
                RadioButton(
                    "close",
                    mapOf(
                        "en" to "Close",
                        "ru" to "Закрыть"
                    )
                ),
                RadioButton(
                    "normal",
                    mapOf(
                        "en" to "Normal",
                        "ru" to "Обычный"
                    )
                ),
                RadioButton(
                    "sync_music",
                    mapOf(
                        "en" to "Sync with Music",
                        "ru" to "Синхронизация с музыкой"
                    )
                ),
                RadioButton(
                    "sync_driving",
                    mapOf(
                        "en" to "Sync with Driving",
                        "ru" to "Синхронизация с вождением"
                    )
                )
            )
        }

        Radio(
            buttons = testButtons,
            lang = Language.fromCode(currentLanguage),
            theme = Theme.fromValue(combinedTheme),
            value = currentTestValue,
            onValueChange = onTestValueChange,
            onViewCreated = onViewCreated
        )
    }
}

@Composable
fun StateDisplaySection(
    currentLanguage: String,
    currentTheme: String,
    currentCarType: String,
    currentTestValue: String,
    combinedTheme: String,
    textColor: Color
) {
    val stateText = when (currentLanguage) {
        "ru" -> buildString {
            appendLine("Текущее состояние:")
            appendLine("Язык: ${if (currentLanguage == "ru") "Русский" else "English"}")
            appendLine("Тема: ${if (currentTheme == "light") "Светлая" else "Тёмная"}")
            appendLine("Тип авто: ${if (currentCarType == "free") "Фри" else "Дример"}")
            appendLine("Тест: ${getTestValueDisplayName(currentTestValue, currentLanguage)}")
            append("Комбинированная тема: $combinedTheme")
        }
        else -> buildString {
            appendLine("Current State:")
            appendLine("Language: ${if (currentLanguage == "ru") "Russian" else "English"}")
            appendLine("Theme: ${if (currentTheme == "light") "Light" else "Dark"}")
            appendLine("Car Type: ${if (currentCarType == "free") "Free" else "Dreamer"}")
            appendLine("Test: ${getTestValueDisplayName(currentTestValue, currentLanguage)}")
            append("Combined Theme: $combinedTheme")
        }
    }

    Text(
        text = stateText,
        fontSize = 16.sp,
        color = textColor
    )
}

/**
 * Gets display name for test value based on language
 */
private fun getTestValueDisplayName(testValue: String, language: String): String {
    return if (language == "ru") {
        when (testValue) {
            "close" -> "Закрыть"
            "normal" -> "Обычный"
            "sync_music" -> "Синхронизация с музыкой"
            "sync_driving" -> "Синхронизация с вождением"
            else -> testValue
        }
    } else {
        when (testValue) {
            "close" -> "Close"
            "normal" -> "Normal"
            "sync_music" -> "Sync with Music"
            "sync_driving" -> "Sync with Driving"
            else -> testValue
        }
    }
}

@Composable
fun DemoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content
    )
}

@Preview(showBackground = true, widthDp = 1920, heightDp = 720)
@Composable
fun DemoScreenPreview() {
    DemoTheme {
        DemoScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun LanguageRadioPreview() {
    DemoTheme {
        LanguageRadioSection(
            currentLanguage = "en",
            combinedTheme = "free-light",
            textColor = Color(MainActivity.TEXT_COLOR_LIGHT),
            onLanguageChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ThemeRadioPreview() {
    DemoTheme {
        ThemeRadioSection(
            currentLanguage = "en",
            currentTheme = "light",
            combinedTheme = "free-light",
            textColor = Color(MainActivity.TEXT_COLOR_LIGHT),
            onThemeChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CarTypeRadioPreview() {
    DemoTheme {
        CarTypeRadioSection(
            currentLanguage = "en",
            currentCarType = "free",
            combinedTheme = "free-light",
            textColor = Color(MainActivity.TEXT_COLOR_LIGHT),
            onCarTypeChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TestRadioPreview() {
    DemoTheme {
        TestRadioSection(
            currentLanguage = "en",
            currentTestValue = "close",
            combinedTheme = "free-light",
            textColor = Color(MainActivity.TEXT_COLOR_LIGHT),
            onTestValueChange = {}
        )
    }
}
