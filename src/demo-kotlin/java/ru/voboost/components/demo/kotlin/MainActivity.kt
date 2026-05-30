package ru.voboost.components.demo.kotlin

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import ru.voboost.components.radio.Radio as RadioView
import ru.voboost.components.radio.RadioButton
import ru.voboost.components.button.Button as ButtonView
import ru.voboost.components.button.ButtonStyle
import ru.voboost.components.buttons.ButtonConfig
import ru.voboost.components.buttons.Buttons as ButtonsView

import ru.voboost.components.select.Select as SelectView
import ru.voboost.components.i18n.Language
import ru.voboost.components.theme.Theme
import ru.voboost.components.screen.Screen
import ru.voboost.components.panel.Panel
import ru.voboost.components.toast.ToastTheme

import ru.voboost.components.section.Section
import ru.voboost.components.section.addCheckbox
import ru.voboost.components.section.addRadio
import ru.voboost.components.section.addButton
import ru.voboost.components.section.addButtons
import ru.voboost.components.section.addSelect
import ru.voboost.components.tabs.Tabs
import ru.voboost.components.tabs.TabItem

import ru.voboost.components.demo.shared.DemoContent
import ru.voboost.components.demo.shared.DemoState
import ru.voboost.components.demo.shared.DemoHelpers

import android.util.Log

/**
 * Demo Kotlin Activity showcasing voboost-components proper component hierarchy in pure Kotlin projects.
 *
 * This demo demonstrates:
 * - Proper component hierarchy: Screen → Panel → Tabs → Section → Radio
 * - Pure Kotlin integration with voboost-components library
 * - Seamless Kotlin-Java interoperability with Java Custom View components
 * - Automotive-oriented layout (1920x720 resolution)
 * - Multi-language support (English/Russian) with reactive updates
 * - Theme switching (Light/Dark) with car type variants (Free/Dreamer)
 * - 7-tab structure with dynamic content
 * - Reactive state management across all components
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
        private const val TITLE_BOTTOM_MARGIN = 32
        private const val SECTION_TITLE_BOTTOM_MARGIN = 16
    }

    // UI Components with late initialization
    private lateinit var screen: Screen
    private lateinit var tabs: Tabs

    // Global State using DemoState from shared module
    private lateinit var demoState: DemoState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MainActivity onCreate started")

        try {
            // Initialize demo state
            demoState = DemoState()

            // Enable full-screen immersive mode for automotive display
            setupFullScreenMode()

            // Setup everything
            setup()

            // Initial update of all components
            updateAllComponents()
            Log.d(TAG, "MainActivity created successfully with proper component hierarchy")
        } catch (e: Exception) {
            Log.e(TAG, "Error during MainActivity creation", e)
            throw e
        }
    }

    /**
     * Sets up full-screen immersive mode to hide system navigation bar
     */
    private fun setupFullScreenMode() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.insetsController?.let { controller ->
                    controller.hide(WindowInsets.Type.systemBars())
                    controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            } else {
                @Suppress("DEPRECATION")
                window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        )
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } catch (e: NullPointerException) {
            // Ignore NPE in Robolectric test environment
            // The DecorView is not fully initialized in unit tests
            Log.d(TAG, "Ignoring NullPointerException in setupFullScreenMode (likely running in Robolectric)", e)
        }
    }

    /**
     * Sets up the proper component hierarchy: Screen → Tabs → Panels
     */
    private fun setup() {
        // Create Screen component as root layout
        screen = Screen(this)
        setContentView(screen)

        // Create Tabs component
        tabs = Tabs(this)
        screen.setTabs(tabs)

        // Create all panels
        val panels = createAllPanels()
        screen.setPanels(panels)

        // Configure tabs with 7 tab items
        tabs.setItems(DemoContent.getTabItems())
        tabs.setTheme(Theme.fromValue(demoState.getCombinedTheme()))
        tabs.setLanguage(Language.fromCode(demoState.getCurrentLanguage()))

        // Set initial tab selection - this will trigger the listener and set active panel
        tabs.setSelectedValue(demoState.getSelectedTab(), false)

        // Set screen lift listener for component interaction
        screen.setOnScreenLiftListener { state ->
            Log.d(TAG, "Screen lift state changed to: $state")
            demoState.setScreenLiftState(state)
            updateAllComponents()
        }

        // Set tab selection listener
        tabs.setOnValueChangeListener { selectedTab ->
            Log.d(TAG, "Tab changed to: $selectedTab")
            demoState.setSelectedTab(selectedTab)
            updateAllComponents()
        }
    }

    /**
     * Creates all panels for all tabs
     */
    private fun createAllPanels(): Array<Panel> {
        return arrayOf(
            createPanelForTab("language"),
            createPanelForTab("theme"),
            createPanelForTab("car_type"),
            createPanelForTab("climate"),
            createPanelForTab("audio"),
            createPanelForTab("display"),
            createPanelForTab("system"),
            createPanelForTab("components")
        )
    }

    /**
     * Creates a panel for a specific tab
     */
    private fun createPanelForTab(tabValue: String): Panel {
        if (tabValue == "climate") {
            return createClimatePanelWithMultipleRadios()
        }
        if (tabValue == "components") {
            return createComponentsPanel()
        }

        // Get current theme and language
        val theme = Theme.fromValue(demoState.getCombinedTheme())
        val language = Language.fromCode(demoState.getCurrentLanguage())

        // Create Panel
        val panel = Panel(this)

        // Create Section
        val section = Section(this).apply {
            setTitle(DemoContent.getSectionTitle(tabValue))
            setTheme(theme)
            setLanguage(language)
        }

        // Create Radio with options for this tab
        val radioButtons = DemoContent.getRadioButtons(tabValue)
        val radio = RadioView(this).apply {
            setButtons(radioButtons)
            setSelectedValue(demoState.getSelectedValueForTab(tabValue))
            setTheme(theme)
            setLanguage(language)
            setOnValueChangeListener { newValue ->
                Log.d(TAG, "Tab $tabValue value changed to: $newValue")
                demoState.setSelectedValueForTab(tabValue, newValue)

                // Special handling for language, theme, and car type tabs
                when (tabValue) {
                    "language" -> demoState.setCurrentLanguage(newValue)
                    "theme" -> demoState.setCurrentTheme(newValue)
                    "car_type" -> demoState.setCurrentCarType(newValue)
                }

                updateAllComponents()
            }
        }

        // Add Radio as child of Section (Section is now a ViewGroup)
        section.addView(radio)

        // Add Section to Panel
        panel.addView(section)

        return panel
    }

    /**
     * Creates the climate panel with multiple sections and radio groups.
     * 5 sections × ~283px each = ~1415px total, overflowing the ~670px panel.
     */
    private fun createClimatePanelWithMultipleRadios(): Panel {
        val panel = Panel(this)

        val theme = Theme.fromValue(demoState.getCombinedTheme())
        val language = Language.fromCode(demoState.getCurrentLanguage())

        val sectionCount = DemoContent.getClimateSectionCount()

        for (i in 0 until sectionCount) {
            val section = Section(this).apply {
                setTitle(DemoContent.getClimateSectionTitle(i))
                setTheme(theme)
                setLanguage(language)
            }

            // Use addRadio extension function for consistency
            section.addRadio(
                buttons = DemoContent.getClimateSubRadioButtons(i),
                selectedValue = DemoContent.getClimateSubDefaultValue(i),
                theme = theme,
                language = language,
                onValueChange = { newValue ->
                    Log.d(TAG, "Climate section $i value changed to: $newValue")
                }
            )

            // Add Section directly to Panel's built-in ScrollView
            panel.addView(section)
        }

        return panel
    }

    /**
     * Updates a single Section and all its child components (Radio, Button, Checkbox, Select, Buttons).
     */
    private fun updateSection(section: Section, combinedTheme: String) {
        val language = Language.fromCode(demoState.getCurrentLanguage())
        val theme = Theme.fromValue(combinedTheme)

        section.setLanguage(language)
        section.setTheme(theme)

        for (j in 0 until section.childCount) {
            val sectionChild = section.getChildAt(j)
            when (sectionChild) {
                is RadioView -> {
                    sectionChild.setLanguage(language)
                    sectionChild.setTheme(theme)
                }
                is ButtonView -> {
                    sectionChild.setLanguage(language)
                    sectionChild.setTheme(theme)
                }
                is ru.voboost.components.checkbox.Checkbox -> {
                    sectionChild.setLanguage(language)
                    sectionChild.setTheme(theme)
                }
                is SelectView -> {
                    sectionChild.setLanguage(language)
                    sectionChild.setTheme(theme)
                }
                is ButtonsView -> {
                    sectionChild.setLanguage(language)
                    sectionChild.setTheme(theme)
                }
                is ViewGroup -> {
                    // Handle nested layouts (e.g., LinearLayout containing buttons)
                    updateViewGroup(sectionChild, language, theme)
                }
            }
        }
    }

    /**
     * Recursively updates all themable/localizable components in a ViewGroup.
     */
    private fun updateViewGroup(viewGroup: ViewGroup, language: Language, theme: Theme) {
        for (i in 0 until viewGroup.childCount) {
            when (val child = viewGroup.getChildAt(i)) {
                is RadioView -> {
                    child.setLanguage(language)
                    child.setTheme(theme)
                }
                is ButtonView -> {
                    child.setLanguage(language)
                    child.setTheme(theme)
                }
                is ru.voboost.components.checkbox.Checkbox -> {
                    child.setLanguage(language)
                    child.setTheme(theme)
                }
                is SelectView -> {
                    child.setLanguage(language)
                    child.setTheme(theme)
                }
                is ButtonsView -> {
                    child.setLanguage(language)
                    child.setTheme(theme)
                }
                is ViewGroup -> updateViewGroup(child, language, theme)
            }
        }
    }

    /**
     * Updates all components with current state using Kotlin when expressions and safe calls
     * Implements reactive behavior across all components in the hierarchy
     */
    private fun updateAllComponents() {
        val combinedTheme = demoState.getCombinedTheme()

        Log.d(TAG, "Updating all components - Language: ${demoState.getCurrentLanguage()}, " +
                "Theme: $combinedTheme, Selected Tab: ${demoState.getSelectedTab()}")

        try {
            // Update background color based on theme
            updateBackgroundColor(combinedTheme)

            // Update Screen component
            screen.takeIf { ::screen.isInitialized }?.setTheme(Theme.fromValue(combinedTheme))

            // Update Tabs component
            tabs.takeIf { ::tabs.isInitialized }?.apply {
                setLanguage(Language.fromCode(demoState.getCurrentLanguage()))
                setTheme(Theme.fromValue(combinedTheme))
            }

            // Update all panels
            val panels = screen.takeIf { ::screen.isInitialized }?.getPanels()
            panels?.forEach { panel ->
                panel.setTheme(Theme.fromValue(combinedTheme))

                // Update child views in panel (Sections are added directly)
                for (i in 0 until panel.childCount) {
                    val child = panel.getChildAt(i)
                    if (child is Section) {
                        updateSection(child, combinedTheme)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating components", e)
        }
    }

    /**
     * Updates the background color based on current theme
     */
    private fun updateBackgroundColor(combinedTheme: String) {
        val backgroundColor = if (combinedTheme.endsWith("-dark")) {
            Color.parseColor("#000000") // Black background for dark themes
        } else {
            Color.parseColor("#f1f5fb") // Light background for light themes
        }

        // Apply background color to screen
        screen.takeIf { ::screen.isInitialized }?.setBackgroundColor(backgroundColor)
    }


    // Getter methods for testing
    internal fun getTabs(): Tabs = tabs
    internal fun getScreen(): Screen = screen
    internal fun getDemoState(): DemoState = demoState
    internal fun getCurrentSection(): Section? {
        val panel = getPanel() ?: return null
        return findSection(panel)
    }

    private fun findSection(viewGroup: ViewGroup): Section? {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is Section) {
                return child
            } else if (child is ViewGroup) {
                val found = findSection(child)
                if (found != null) return found
            }
        }
        return null
    }

    internal fun getCurrentRadio(): RadioView? {
        val panel = getPanel() ?: return null
        return findRadio(panel)
    }

    private fun findRadio(viewGroup: ViewGroup): RadioView? {
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)
            if (child is RadioView) {
                return child
            } else if (child is ViewGroup) {
                val found = findRadio(child)
                if (found != null) return found
            }
        }
        return null
    }

    internal fun getPanel(): Panel? {
        // Get the current panel based on selected tab
        val selectedTab = demoState.getSelectedTab()
        val panels = screen.getPanels()
        val tabIndex = when (selectedTab) {
            "language" -> 0
            "theme" -> 1
            "car_type" -> 2
            "climate" -> 3
            "audio" -> 4
            "display" -> 5
            "system" -> 6
            "components" -> 7
            else -> 0
        }

        return if (tabIndex < panels.size) panels[tabIndex] else null
    }

    /**
     * Returns the ScrollView inside the climate panel (for testing scroll behavior).
     *
     * @return the ScrollView, or null if not found
     */
    internal fun getClimatePanelScrollView(): android.widget.ScrollView? {
        val panels = screen.getPanels()
        if (panels == null || panels.size <= 3) return null

        // Ensure the climate panel (index 3) wrapper is created
        screen.setActivePanel(3)
        return screen.getPanelWrapper(3)
    }

    /**
     * Android lifecycle methods with enhanced logging
     */
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "MainActivity resumed with component hierarchy - Current state: ${demoState.toString()}")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "MainActivity paused - Current state: ${demoState.toString()}")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "MainActivity destroyed")
    }

    /**
     * Creates the components panel showcasing Button, Checkbox, Select, and Dialog.
     */
    private fun createComponentsPanel(): Panel {
        val panel = Panel(this)

        val theme = Theme.fromValue(demoState.getCombinedTheme())
        val language = Language.fromCode(demoState.getCurrentLanguage())

        // Button section
        val buttonSection = Section(this).apply {
            setTitle(mapOf("en" to "Button", "ru" to "Кнопка"))
            setTheme(theme)
            setLanguage(language)
        }

        val buttonRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 10, 0, 10)
        }

        val primaryBtn = ButtonView(this).apply {
            setTheme(theme)
            setStyle(ButtonStyle.PRIMARY)
            setText("Primary")
            layoutParams = LinearLayout.LayoutParams(
                0, 80, 1f)
        }

        val secondaryBtn = ButtonView(this).apply {
            setTheme(theme)
            setStyle(ButtonStyle.SECONDARY)
            setText("Secondary")
            layoutParams = LinearLayout.LayoutParams(
                0, 80, 1f).also {
                it.leftMargin = 20
            }
        }

        buttonRow.addView(primaryBtn)
        buttonRow.addView(secondaryBtn)
        buttonSection.addView(buttonRow)
        panel.addView(buttonSection)

        // Checkbox section
        val checkboxSection = Section(this).apply {
            setTitle(mapOf("en" to "Checkbox", "ru" to "Переключатель"))
            setTheme(theme)
            setLanguage(language)
        }

        val cbRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 10, 0, 10)
        }

        // Create checkboxes directly (not using addCheckbox extension to avoid double addView)
        val checkboxOn = ru.voboost.components.checkbox.Checkbox(this).apply {
            setChecked(true)
            setTheme(theme)
            setLanguage(language)
        }
        val checkboxOff = ru.voboost.components.checkbox.Checkbox(this).apply {
            setChecked(false)
            setTheme(theme)
            setLanguage(language)
        }
        checkboxOff.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT).also {
            it.leftMargin = 40
        }
        cbRow.addView(checkboxOn)
        cbRow.addView(checkboxOff)
        checkboxSection.addView(cbRow)
        panel.addView(checkboxSection)

        // Select section
        val selectSection = Section(this).apply {
            setTitle(mapOf("en" to "Select", "ru" to "Выбор"))
            setTheme(theme)
            setLanguage(language)
        }

        // Create select directly and add to section
        val selectView = ru.voboost.components.select.Select(this).apply {
            setOptions(DemoContent.getSelectOptions())
            setSelectedValue("auto")
            setTheme(theme)
            setLanguage(language)
            setOnValueChangeListener { newValue ->
                Log.d(TAG, "Select value changed to: $newValue")
            }
        }
        selectSection.addView(selectView)

        panel.addView(selectSection)

        // Dialog section
        val dialogSection = Section(this).apply {
            setTitle(mapOf("en" to "Dialog", "ru" to "Диалог"))
            setTheme(theme)
            setLanguage(language)
        }

        val dialogContent = DemoContent.getDialogContent()
        val lang = demoState.getCurrentLanguage()

        val dialogTrigger = ButtonView(this).apply {
            setTheme(theme)
            setStyle(ButtonStyle.SECONDARY)
            setText(dialogContent["title"]?.getOrDefault(lang, "Show Dialog") ?: "Show Dialog")
            setOnClickListener { v ->
                val dialog = ru.voboost.components.dialog.Dialog(this@MainActivity)
                dialog.setTheme(Theme.fromValue(demoState.getCombinedTheme()))
                dialog.setTitle(dialogContent["title"]?.getOrDefault(demoState.getCurrentLanguage(), "Reset") ?: "Reset")
                dialog.setMessage(dialogContent["message"]?.getOrDefault(demoState.getCurrentLanguage(), "Are you sure?") ?: "Are you sure?")
                dialog.setConfirmButton(
                    dialogContent["confirm"]?.getOrDefault(demoState.getCurrentLanguage(), "OK") ?: "OK"
                ) { Log.d(TAG, "Dialog confirmed") }
                dialog.setCancelButton(
                    dialogContent["cancel"]?.getOrDefault(demoState.getCurrentLanguage(), "Cancel") ?: "Cancel"
                ) { Log.d(TAG, "Dialog cancelled") }
                dialog.show()
            }
        }

        dialogSection.addView(dialogTrigger)
        panel.addView(dialogSection)

        // Toast section
        val toastSection = Section(this).apply {
            setTitle(mapOf("en" to "Toast", "ru" to "Уведомление"))
            setTheme(theme)
            setLanguage(language)
        }

        val toastShortButton = ButtonView(this).apply {
            setTheme(theme)
            setStyle(ButtonStyle.SECONDARY)
            setText(mapOf("en" to "Show Short Toast", "ru" to "Короткое уведомление")[language.code] ?: "Show Short Toast")
            setOnClickListener {
                screen.showToast(
                    mapOf("en" to "Settings saved", "ru" to "Настройки сохранены")[language.code] ?: "Settings saved",
                    ru.voboost.components.toast.ToastTheme.DURATION_SHORT
                )
            }
        }

        val toastLongButton = ButtonView(this).apply {
            setTheme(theme)
            setStyle(ButtonStyle.SECONDARY)
            setText(mapOf("en" to "Show Long Toast", "ru" to "Длинное уведомление")[language.code] ?: "Show Long Toast")
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).also { it.topMargin = 14 }
            setOnClickListener {
                screen.showToast(
                    mapOf("en" to "Your settings have been successfully saved", "ru" to "Ваши настройки успешно сохранены")[language.code] ?: "Your settings have been successfully saved",
                    ru.voboost.components.toast.ToastTheme.DURATION_LONG
                )
            }
        }

        toastSection.addView(toastShortButton)
        toastSection.addView(toastLongButton)
        panel.addView(toastSection)

        // Radio + Text section
        val rwtSection = Section(this).apply {
            setTitle(mapOf("en" to "Radio + Text", "ru" to "Radio + Text"))
            setTheme(theme); setLanguage(language)
        }

        // Use addRadio extension for consistency
        val rwt1 = rwtSection.addRadio(
            buttons = listOf(
                RadioButton("off", mapOf("en" to "Off", "ru" to "Выкл")),
                RadioButton("15", mapOf("en" to "15s", "ru" to "15с")),
                RadioButton("30", mapOf("en" to "30s", "ru" to "30с")),
                RadioButton("60", mapOf("en" to "60s", "ru" to "60с"))),
            selectedValue = "30",
            theme = theme,
            language = language
        )
        rwt1.setTitle(mapOf("en" to "Come home lights", "ru" to "Подсветка дороги домой"))
        rwt1.setDescription(mapOf("en" to "Headlights stay on after locking", "ru" to "Фары остаются включёнными после блокировки"))

        val rwt2 = rwtSection.addRadio(
            buttons = listOf(
                RadioButton("light", mapOf("en" to "Light", "ru" to "Свет")),
                RadioButton("light_sound", mapOf("en" to "Light + Sound", "ru" to "Свет + Звук"))),
            selectedValue = "light",
            theme = theme,
            language = language
        )
        rwt2.setTitle(mapOf("en" to "Anti-theft alarm", "ru" to "Противоугонная сигнализация"))

        val rwt3 = rwtSection.addRadio(
            buttons = listOf(
                RadioButton("low", mapOf("en" to "Low", "ru" to "Низ")),
                RadioButton("med", mapOf("en" to "Med", "ru" to "Сред")),
                RadioButton("high", mapOf("en" to "High", "ru" to "Выс"))),
            selectedValue = "med",
            theme = theme,
            language = language
        )
        rwt3.setTitle(mapOf("en" to "Energy recovery", "ru" to "Рекуперация энергии"))
        rwt3.setDescriptionAbove(mapOf("en" to "Adjusts braking energy recovery level", "ru" to "Регулирует уровень рекуперации торможения"))
        rwt3.setDescription(mapOf("en" to "Higher levels increase range but feel stronger braking", "ru" to "Более высокие уровни увеличивают запас хода"))

        panel.addView(rwtSection)

        // Checkbox + Label section
        val cwtSection = Section(this).apply {
            setTitle(mapOf("en" to "Checkbox + Label", "ru" to "Checkbox + Label"))
            setTheme(theme); setLanguage(language)
        }

        val cwt1 = cwtSection.addCheckbox(
            theme = theme,
            language = language,
            checked = false,
            label = mapOf("en" to "Tow mode", "ru" to "Режим буксировки"),
            description = mapOf("en" to "Maintain N gear when vehicle is rescued", "ru" to "Поддерживать нейтраль при буксировке")
        )

        val cwt2 = cwtSection.addCheckbox(
            theme = theme,
            language = language,
            checked = true,
            label = mapOf("en" to "Auto-fold mirrors", "ru" to "Автоскладывание зеркал")
        )

        val cwt3 = cwtSection.addCheckbox(
            theme = theme,
            language = language,
            checked = true,
            label = mapOf("en" to "Welcome lamp", "ru" to "Приветственная подсветка"),
            description = mapOf("en" to "Lights activate when approaching the vehicle", "ru" to "Подсветка при приближении к автомобилю")
        )
        panel.addView(cwtSection)

        // Button + Description section
        val bwtSection = Section(this).apply {
            setTitle(mapOf("en" to "Button + Description", "ru" to "Button + Description"))
            setTheme(theme); setLanguage(language)
        }

        // Use addButton extension for consistency
        val bwt1 = bwtSection.addButton(
            theme = theme,
            language = language,
            text = "Settings",
            description = mapOf("en" to "Open advanced settings", "ru" to "Открыть расширенные настройки"),
            style = ButtonStyle.SECONDARY
        )

        val bwt2 = bwtSection.addButton(
            theme = theme,
            language = language,
            text = "Calibrate",
            description = mapOf("en" to "Run camera calibration.\nDrive straight for 2 minutes.", "ru" to "Запустить калибровку камеры.\nДвигайтесь прямо 2 минуты."),
            style = ButtonStyle.PRIMARY
        )
        bwt2.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).also { it.topMargin = 14 })

        panel.addView(bwtSection)

        // Buttons section
        val bswSection = Section(this).apply {
            setTitle(mapOf("en" to "Buttons", "ru" to "Buttons"))
            setTheme(theme); setLanguage(language)
        }

        // Use addButtons extension for consistency
        val bsw1 = bswSection.addButtons(
            buttons = listOf(ButtonConfig("lower", "Lower"), ButtonConfig("restore", "Restore")),
            selectedValue = "restore",
            theme = theme,
            language = language,
            rightText = mapOf("en" to "Suspension level", "ru" to "Уровень подвески"),
            description = mapOf("en" to "Lower suspension when parked for easy entry", "ru" to "Понизить подвеску при парковке для удобной посадки")
        )

        val bsw2 = bswSection.addButtons(
            buttons = listOf(ButtonConfig("eco", "ECO"), ButtonConfig("comfort", "Comfort"), ButtonConfig("sport", "Sport")),
            selectedValue = "comfort",
            theme = theme,
            language = language,
            description = mapOf("en" to "Select preferred driving mode", "ru" to "Выберите предпочтительный режим вождения")
        )
        bsw2.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).also { it.topMargin = 14 })

        val bsw3 = bswSection.addButtons(
            buttons = listOf(ButtonConfig("on", "ON"), ButtonConfig("off", "OFF")),
            selectedValue = "on",
            theme = theme,
            language = language,
            rightText = mapOf("en" to "Auto headlamp", "ru" to "Авто фары")
        )
        bsw3.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT).also { it.topMargin = 14 })

        panel.addView(bswSection)

        return panel
    }
}
