package ru.voboost.components.demo.kotlin

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import ru.voboost.components.radio.Radio
import ru.voboost.components.radio.RadioButton
import ru.voboost.components.i18n.Language
import ru.voboost.components.theme.Theme

/**
 * Demo Kotlin Activity showcasing voboost-components Radio component usage in pure Kotlin projects.
 *
 * This demo demonstrates:
 * - Pure Kotlin integration with voboost-components library
 * - Seamless Kotlin-Java interoperability with Java Custom View components
 * - Automotive-oriented layout (1920x720 resolution) with ScrollView compatibility
 * - Multi-language support (English/Russian) with reactive updates
 * - Theme switching (Light/Dark) with car type variants (Free/Dreamer)
 * - Reactive state management across multiple Radio components
 * - Kotlin-specific features: data classes, when expressions, extension functions, property delegates
 * - Comprehensive Android lifecycle management
 * - Advanced error handling with Kotlin null safety
 */
class MainActivity : Activity() {
    companion object {
        private const val TAG = "KotlinDemo"

        // Automotive display constants (in pixels, not dp!)
        private const val CONTAINER_PADDING_HORIZONTAL = 64
        private const val CONTAINER_PADDING_VERTICAL = 48
        private const val SECTION_SPACING = 24
        private const val TITLE_BOTTOM_MARGIN = 32
        private const val SECTION_TITLE_BOTTOM_MARGIN = 16
        private const val RADIO_MARGIN_BOTTOM = 32

        // Background colors (lowercase hex!)
        private const val BACKGROUND_COLOR_LIGHT = "#f1f5fb"
        private const val BACKGROUND_COLOR_DARK = "#000000"

        // Text colors (lowercase hex!)
        private const val TEXT_COLOR_LIGHT = "#1a1a1a"
        private const val TEXT_COLOR_DARK = "#ffffff"

        // Default values
        private const val DEFAULT_LANGUAGE = "en"
        private const val DEFAULT_THEME = "light"
        private const val DEFAULT_CAR_TYPE = "free"
        private const val DEFAULT_TEST_VALUE = "close"
    }

    // UI Components with late initialization
    private lateinit var scrollView: ScrollView
    private lateinit var rootLayout: LinearLayout
    private lateinit var titleText: TextView
    private lateinit var languageTitle: TextView
    private lateinit var themeTitle: TextView
    private lateinit var carTypeTitle: TextView
    private lateinit var testTitle: TextView
    internal lateinit var languageRadio: Radio
    internal lateinit var themeRadio: Radio
    internal lateinit var carTypeRadio: Radio
    internal lateinit var testRadio: Radio
    private lateinit var stateDisplay: TextView

    // Global State using Kotlin custom property setters with reactive updates
    private var currentLanguage: String = DEFAULT_LANGUAGE
        set(value) {
            if (field != value) {
                Log.d(TAG, "Language changing from $field to $value")
                field = value
                updateAllComponents()
            }
        }

    private var currentTheme: String = DEFAULT_THEME
        set(value) {
            if (field != value) {
                Log.d(TAG, "Theme changing from $field to $value")
                field = value
                updateAllComponents()
            }
        }

    private var currentCarType: String = DEFAULT_CAR_TYPE
        set(value) {
            if (field != value) {
                Log.d(TAG, "Car type changing from $field to $value")
                field = value
                updateAllComponents()
            }
        }

    private var currentTestValue: String = DEFAULT_TEST_VALUE
        set(value) {
            if (field != value) {
                Log.d(TAG, "Test value changing from $field to $value")
                field = value
                updateAllComponents()
            }
        }

    // Computed property for combined theme using Kotlin getter
    private val combinedTheme: String
        get() = "$currentCarType-$currentTheme"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MainActivity onCreate started")

        try {
            // Enable full-screen immersive mode for automotive display
            setupFullScreenMode()

            setupAutomotiveLayout()
            setupDemoComponents()
            updateAllComponents()
            Log.d(TAG, "MainActivity created successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error during MainActivity creation", e)
            throw e
        }
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

    /**
     * Sets up automotive-oriented full-screen layout with ScrollView for better compatibility
     * Uses Kotlin apply scope function for concise initialization
     */
    private fun setupAutomotiveLayout() {
        // Create ScrollView for automotive display compatibility
        scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            isFillViewport = true
            // CRITICAL: Disable clipping at parent container level
            clipChildren = false
            clipToPadding = false
        }

        // Create main layout container using Kotlin apply
        rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(
                CONTAINER_PADDING_HORIZONTAL,
                CONTAINER_PADDING_VERTICAL,
                CONTAINER_PADDING_HORIZONTAL,
                CONTAINER_PADDING_VERTICAL
            )
            // CRITICAL: Turn off clipping on LinearLayout
            clipChildren = false
            clipToPadding = false
        }

        scrollView.addView(rootLayout)
        setContentView(scrollView)
    }

    /**
     * Sets up demo components for showcasing Radio functionality
     * Enhanced with separate section titles and better organization
     */
    private fun setupDemoComponents() {
        // Main title
        setupTitle()

        // Language Radio Section
        setupLanguageRadio()

        // Theme Radio Section
        setupThemeRadio()

        // Car Type Radio Section
        setupCarTypeRadio()

        // Test Radio Section
        setupTestRadio()

        // State Display Section
        setupStateDisplay()
    }

    /**
     * Sets up the main title with Kotlin apply scope function
     */
    private fun setupTitle() {
        titleText =
            TextView(this).apply {
                text = "Voboost Components - Kotlin Demo"
                textSize = 24f
                layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 0, 0, TITLE_BOTTOM_MARGIN)
                    }
            }
        rootLayout.addView(titleText)
    }

    /**
     * Sets up Language Radio component using Kotlin features and enhanced error handling
     */
    private fun setupLanguageRadio() {
        // Create section title (separate TextView like Java demo)
        languageTitle = createSectionTitle("Language Selection:")
        rootLayout.addView(languageTitle)

        // Create language radio buttons using Kotlin collections and mapOf
        val languageButtons = createLanguageButtons()

        // Create and configure language radio with enhanced error handling
        val currentLang = currentLanguage
        languageRadio =
            Radio(this).apply {
                try {
                    setButtons(languageButtons)
                    setSelectedValue(currentLang)
                    setOnValueChangeListener { newValue ->
                        Log.d(TAG, "Language changed to: $newValue")
                        this@MainActivity.currentLanguage = newValue
                    }
                    layoutParams = createRadioLayoutParams()
                } catch (e: Exception) {
                    Log.e(TAG, "Error setting up language radio", e)
                    throw e
                }
            }
        rootLayout.addView(languageRadio)
    }

    /**
     * Sets up Theme Radio component using Kotlin features and enhanced error handling
     */
    private fun setupThemeRadio() {
        // Create section title (separate TextView like Java demo)
        themeTitle = createSectionTitle("Theme Selection:")
        rootLayout.addView(themeTitle)

        // Create theme radio buttons using Kotlin collections
        val themeButtons = createThemeButtons()

        // Create and configure theme radio with enhanced error handling
        val currentThemeVal = currentTheme
        themeRadio =
            Radio(this).apply {
                try {
                    setButtons(themeButtons)
                    setSelectedValue(currentThemeVal)
                    setOnValueChangeListener { newValue ->
                        Log.d(TAG, "Theme changed to: $newValue")
                        this@MainActivity.currentTheme = newValue
                    }
                    layoutParams = createRadioLayoutParams()
                } catch (e: Exception) {
                    Log.e(TAG, "Error setting up theme radio", e)
                    throw e
                }
            }
        rootLayout.addView(themeRadio)
    }

    /**
     * Sets up Car Type Radio component using Kotlin features and enhanced error handling
     */
    private fun setupCarTypeRadio() {
        // Create section title (separate TextView like Java demo)
        carTypeTitle = createSectionTitle("Car Type Selection:")
        rootLayout.addView(carTypeTitle)

        // Create car type radio buttons using Kotlin collections
        val carTypeButtons = createCarTypeButtons()

        // Create and configure car type radio with enhanced error handling
        carTypeRadio =
            Radio(this).apply {
                try {
                    setButtons(carTypeButtons)
                    setSelectedValue(currentCarType)
                    setOnValueChangeListener { newValue ->
                        Log.d(TAG, "Car type changed to: $newValue")
                        currentCarType = newValue
                    }
                    layoutParams = createRadioLayoutParams()
                } catch (e: Exception) {
                    Log.e(TAG, "Error setting up car type radio", e)
                    throw e
                }
            }
        rootLayout.addView(carTypeRadio)
    }

    /**
     * Sets up Test Radio component for animation testing
     */
    private fun setupTestRadio() {
        // Create section title
        testTitle = createSectionTitle("Test Selection:")
        rootLayout.addView(testTitle)

        // Create test radio buttons
        val testButtons = createTestButtons()

        // Create and configure test radio with enhanced error handling
        testRadio = Radio(this).apply {
            try {
                setButtons(testButtons)
                setSelectedValue(currentTestValue)
                setOnValueChangeListener { newValue ->
                    Log.d(TAG, "Test value changed to: $newValue")
                    currentTestValue = newValue
                }
                layoutParams = createRadioLayoutParams()
            } catch (e: Exception) {
                Log.e(TAG, "Error setting up test radio", e)
                throw e
            }
        }
        rootLayout.addView(testRadio)
    }

    /**
     * Sets up the state display section with enhanced layout
     */
    private fun setupStateDisplay() {
        stateDisplay =
            TextView(this).apply {
                textSize = 16f
                layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, TITLE_BOTTOM_MARGIN, 0, SECTION_TITLE_BOTTOM_MARGIN)
                    }
            }
        rootLayout.addView(stateDisplay)
    }

    /**
     * Creates language radio buttons using Kotlin collections and mapOf
     */
    private fun createLanguageButtons(): List<RadioButton> =
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

    /**
     * Creates theme radio buttons using Kotlin collections and mapOf
     */
    private fun createThemeButtons(): List<RadioButton> =
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

    /**
     * Creates car type radio buttons using Kotlin collections and mapOf
     */
    private fun createCarTypeButtons(): List<RadioButton> =
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

    /**
     * Creates test radio buttons with long text for animation testing
     */
    private fun createTestButtons(): List<RadioButton> = listOf(
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

    /**
     * Creates section titles with consistent styling and layout parameters
     * Uses Kotlin apply scope function for concise initialization
     */
    private fun createSectionTitle(text: String): TextView =
        TextView(this).apply {
            this.text = text
            textSize = 18f
            layoutParams =
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, SECTION_SPACING, 0, SECTION_TITLE_BOTTOM_MARGIN)
                }
        }

    /**
     * Creates radio layout parameters with consistent spacing
     * Uses Kotlin apply scope function for concise initialization
     */
    private fun createRadioLayoutParams(): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(0, 0, 0, SECTION_SPACING)
        }

    /**
     * Updates all components with current state using Kotlin when expressions and safe calls
     * Implements reactive behavior across all Radio components with enhanced error handling
     */
    private fun updateAllComponents() {
        Log.d(TAG, "Updating all components - Language: $currentLanguage, Theme: $combinedTheme")

        try {
            // Update background color based on theme
            updateBackgroundColor()

            // Update radio components with current language and theme
            updateRadioComponents()

            // Update all text elements based on current language
            updateTextElements()

            // Update state display
            updateStateDisplay()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating components", e)
        }
    }

    /**
     * Updates the background color based on current theme
     */
    private fun updateBackgroundColor() {
        val backgroundColor = if (combinedTheme.endsWith("-dark")) {
            Color.parseColor(BACKGROUND_COLOR_DARK)
        } else {
            Color.parseColor(BACKGROUND_COLOR_LIGHT)
        }

        scrollView.takeIf { ::scrollView.isInitialized }?.setBackgroundColor(backgroundColor)
        rootLayout.takeIf { ::rootLayout.isInitialized }?.setBackgroundColor(backgroundColor)
    }

    /**
     * Updates all radio components with current language and theme
     * Uses Kotlin safe call operator and enhanced null safety
     */
    private fun updateRadioComponents() {
        // Update language for all radio components using safe calls
        languageRadio.takeIf { ::languageRadio.isInitialized }?.setLanguage(Language.fromCode(currentLanguage))
        themeRadio.takeIf { ::themeRadio.isInitialized }?.setLanguage(Language.fromCode(currentLanguage))
        carTypeRadio.takeIf { ::carTypeRadio.isInitialized }?.setLanguage(Language.fromCode(currentLanguage))
        testRadio.takeIf { ::testRadio.isInitialized }?.setLanguage(Language.fromCode(currentLanguage))

        // Update theme for all radio components using safe calls
        languageRadio.takeIf { ::languageRadio.isInitialized }?.setTheme(Theme.fromValue(combinedTheme))
        themeRadio.takeIf { ::themeRadio.isInitialized }?.setTheme(Theme.fromValue(combinedTheme))
        carTypeRadio.takeIf { ::carTypeRadio.isInitialized }?.setTheme(Theme.fromValue(combinedTheme))
        testRadio.takeIf { ::testRadio.isInitialized }?.setTheme(Theme.fromValue(combinedTheme))
    }

    /**
     * Updates all text elements based on current language and theme
     */
    private fun updateTextElements() {
        // Determine text color based on theme
        val textColor = if (currentTheme == "dark") {
            Color.parseColor(TEXT_COLOR_DARK)
        } else {
            Color.parseColor(TEXT_COLOR_LIGHT)
        }

        // Update title text based on language using when expression and safe calls
        titleText.takeIf { ::titleText.isInitialized }?.apply {
            text = when (currentLanguage) {
                "ru" -> "Voboost Components - Kotlin Демо"
                else -> "Voboost Components - Kotlin Demo"
            }
            setTextColor(textColor)
        }

        // Update section titles using when expressions and safe calls
        languageTitle.takeIf { ::languageTitle.isInitialized }?.apply {
            text = when (currentLanguage) {
                "ru" -> "Выбор языка:"
                else -> "Language Selection:"
            }
            setTextColor(textColor)
        }

        themeTitle.takeIf { ::themeTitle.isInitialized }?.apply {
            text = when (currentLanguage) {
                "ru" -> "Выбор темы:"
                else -> "Theme Selection:"
            }
            setTextColor(textColor)
        }

        carTypeTitle.takeIf { ::carTypeTitle.isInitialized }?.apply {
            text = when (currentLanguage) {
                "ru" -> "Выбор типа авто:"
                else -> "Car Type Selection:"
            }
            setTextColor(textColor)
        }

        testTitle.takeIf { ::testTitle.isInitialized }?.apply {
            text = when (currentLanguage) {
                "ru" -> "Тестовый выбор:"
                else -> "Test Selection:"
            }
            setTextColor(textColor)
        }
    }

    /**
     * Updates state display using Kotlin string templates, when expressions, and buildString
     */
    private fun updateStateDisplay() {
        val textColor = if (currentTheme == "dark") {
            Color.parseColor(TEXT_COLOR_DARK)
        } else {
            Color.parseColor(TEXT_COLOR_LIGHT)
        }

        stateDisplay.takeIf { ::stateDisplay.isInitialized }?.apply {
            text = buildStateText()
            setTextColor(textColor)
        }
    }

    /**
     * Builds the state display text using Kotlin buildString and when expressions
     */
    private fun buildStateText(): String =
        when (currentLanguage) {
            "ru" -> buildString {
                appendLine("Текущее состояние:")
                appendLine("Язык: ${currentLanguage.toDisplayLanguage()}")
                appendLine("Тема: ${currentTheme.toDisplayTheme()}")
                appendLine("Тип авто: ${currentCarType.toDisplayCarType()}")
                appendLine("Тест: ${currentTestValue.toDisplayTestValue()}")
                append("Комбинированная тема: $combinedTheme")
            }
            else -> buildString {
                appendLine("Current State:")
                appendLine("Language: ${currentLanguage.toDisplayLanguage()}")
                appendLine("Theme: ${currentTheme.toDisplayTheme()}")
                appendLine("Car Type: ${currentCarType.toDisplayCarType()}")
                appendLine("Test: ${currentTestValue.toDisplayTestValue()}")
                append("Combined Theme: $combinedTheme")
            }
        }

    /**
     * Extension functions for converting internal values to display strings (Kotlin-specific)
     */
    private fun String.toDisplayLanguage(): String =
        when (this) {
            "ru" -> if (currentLanguage == "ru") "Русский" else "Russian"
            else -> if (currentLanguage == "ru") "English" else "English"
        }

    private fun String.toDisplayTheme(): String =
        when (this) {
            "light" -> if (currentLanguage == "ru") "Светлая" else "Light"
            else -> if (currentLanguage == "ru") "Тёмная" else "Dark"
        }

    private fun String.toDisplayCarType(): String =
        when (this) {
            "free" -> if (currentLanguage == "ru") "Фри" else "Free"
            else -> if (currentLanguage == "ru") "Дример" else "Dreamer"
        }

    private fun String.toDisplayTestValue(): String =
        when (this) {
            "close" -> if (currentLanguage == "ru") "Закрыть" else "Close"
            "normal" -> if (currentLanguage == "ru") "Обычный" else "Normal"
            "sync_music" -> if (currentLanguage == "ru") "Синхронизация с музыкой" else "Sync with Music"
            "sync_driving" -> if (currentLanguage == "ru") "Синхронизация с вождением" else "Sync with Driving"
            else -> this
        }

    /**
     * Android lifecycle methods with enhanced logging
     */
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "MainActivity resumed - Current state: ${getCurrentState()}")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "MainActivity paused - Current state: ${getCurrentState()}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MainActivity destroyed")
    }

    /**
     * Data class for representing demo state (Kotlin-specific feature)
     * Enhanced with additional computed properties and validation
     */
    data class DemoState(
        val language: String,
        val theme: String,
        val carType: String,
        val testValue: String
    ) {
        val combinedTheme: String
            get() = "$carType-$theme"

        val isValidState: Boolean
            get() = language in listOf("en", "ru") &&
                theme in listOf("light", "dark") &&
                carType in listOf("free", "dreamer") &&
                testValue in listOf("close", "normal", "sync_music", "sync_driving")

        fun toLogString(): String =
            "DemoState(lang=$language, theme=$theme, car=$carType, test=$testValue, combined=$combinedTheme)"
    }

    /**
     * Gets current state as data class (Kotlin-specific feature)
     */
    private fun getCurrentState(): DemoState =
        DemoState(
            currentLanguage,
            currentTheme,
            currentCarType,
            currentTestValue
        )

    /**
     * Extension function for logging state changes (Kotlin-specific feature)
     */
    private fun DemoState.logStateChange() {
        Log.d(TAG, "State changed: ${this.toLogString()}")
        if (!this.isValidState) {
            Log.w(TAG, "Invalid state detected: ${this.toLogString()}")
        }
    }
}
