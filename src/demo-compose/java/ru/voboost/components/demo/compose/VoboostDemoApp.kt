package ru.voboost.components.demo.compose

import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.voboost.components.demo.shared.DemoContent
import ru.voboost.components.section.addRadio
import ru.voboost.components.section.addButton
import ru.voboost.components.section.addCheckbox
import ru.voboost.components.section.addSelect
import ru.voboost.components.section.addButtons

/**
 * Main Composable function for the Voboost Demo application.
 * Uses ViewModel for state management following Compose best practices.
 *
 * This is the stateful composable that manages state through ViewModel.
 */
@Composable
fun VoboostDemoApp(viewModel: DemoViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    // Cleanup when the composable leaves the composition
    DisposableEffect(Unit) {
        onDispose {
            // Cleanup any resources if needed
            // Currently no resources need explicit cleanup
        }
    }

    VoboostDemoContent(
        selectedTab = uiState.selectedTab,
        combinedTheme = uiState.combinedTheme,
        currentLanguage = uiState.currentLanguage,
        currentTheme = uiState.currentTheme,
        currentCarType = uiState.currentCarType,
        screenLiftState = uiState.screenLiftState,
        onTabSelected = viewModel::onTabSelected,
        onScreenLift = viewModel::onScreenLiftChanged,
        onValueChange = viewModel::onValueChange,
    )
}

/**
 * Stateless content composable for the Voboost Demo application.
 * Pure UI component that receives all state and callbacks from parent.
 *
 * @param selectedTab Currently selected tab value
 * @param combinedTheme Combined theme string (e.g., "free-light")
 * @param currentLanguage Current language code
 * @param currentTheme Current theme value
 * @param currentCarType Current car type value
 * @param screenLiftState Current screen lift state
 * @param onTabSelected Callback when tab selection changes
 * @param onScreenLift Callback when screen lift state changes
 * @param onValueChange Callback when a tab's value changes
 */
@Composable
fun VoboostDemoContent(
    selectedTab: String,
    combinedTheme: String,
    currentLanguage: String,
    currentTheme: String,
    currentCarType: String,
    screenLiftState: Int,
    onTabSelected: (String) -> Unit,
    onScreenLift: (Int) -> Unit,
    onValueChange: (String, String) -> Unit,
) {
    // Recomposition tracking for development
    if (BuildConfig.DEBUG) {
        SideEffect {
            android.util.Log.d("Recomposition", "VoboostDemoContent recomposed")
        }
    }

    // Background color based on theme - simple conditional, no remember needed for Color
    val isDarkTheme = currentTheme == "dark"
    val backgroundColor = if (isDarkTheme) DemoColors.DarkBackground else DemoColors.LightBackground

    // Root container with background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .semantics {
                testTag = "demo_root"
                contentDescription = "Voboost Components Demo"
            }
    ) {
        // Use the Screen Compose wrapper from the library
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
 * Wrapper for the Screen component using AndroidView.
 * This demonstrates how to use the Java Custom View in Compose.
 *
 * @param selectedTab Currently selected tab value
 * @param combinedTheme Combined theme string (e.g., "free-light")
 * @param currentLanguage Current language code
 * @param screenLiftState Current screen lift state
 * @param onTabSelected Callback when tab selection changes
 * @param onScreenLift Callback when screen lift state changes
 * @param onValueChange Callback when a tab's value changes
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
    val tabConfigs = DemoTabs.TAB_VALUES

    val theme = remember(combinedTheme) { ru.voboost.components.theme.Theme.fromValue(combinedTheme) }
    val language = remember(currentLanguage) { ru.voboost.components.i18n.Language.fromCode(currentLanguage) }

    // Create panels once
    val context = androidx.compose.ui.platform.LocalContext.current
    val panels = remember(tabConfigs, theme, language, combinedTheme) {
        tabConfigs.map { tabValue ->
            createPanelForTab(
                context = context,
                tabValue = tabValue,
                theme = theme,
                language = language,
                combinedTheme = combinedTheme,
                onValueChange = onValueChange
            )
        }.toTypedArray()
    }

    // Use the library Screen composable with new API
    ru.voboost.components.screen.Screen(
        tabs = ru.voboost.components.demo.shared.DemoContent.getTabItems(),
        panels = panels,
        selectedTab = selectedTab,
        onTabSelected = onTabSelected,
        theme = theme,
        screenLiftState = screenLiftState,
        onScreenLift = onScreenLift
    )
}

/**
 * Creates a Panel for a specific tab with Section and Radio components.
 *
 * @param context Android context
 * @param tabValue The tab value identifier
 * @param theme Theme enum value
 * @param language Language enum value
 * @param onValueChange Callback when value changes
 * @return Configured Panel instance
 */
private fun createPanelForTab(
    context: android.content.Context,
    tabValue: String,
    theme: ru.voboost.components.theme.Theme,
    language: ru.voboost.components.i18n.Language,
    combinedTheme: String,
    onValueChange: (String, String) -> Unit
): ru.voboost.components.panel.Panel {
    if (tabValue == "climate") {
        return createClimatePanelWithMultipleRadios(context, theme, language, onValueChange)
    }
    if (tabValue == "components") {
        return createComponentsPanel(context, theme, language, combinedTheme)
    }

    val panel = ru.voboost.components.panel.Panel(context)

    // Create Section using Java API
    val section = ru.voboost.components.section.Section(context).apply {
        setTheme(theme)
        setLanguage(language)
        setTitle(DemoContent.getSectionTitle(tabValue))
    }

    // Use addRadio extension function
    section.addRadio(
        buttons = DemoContent.getRadioButtons(tabValue),
        selectedValue = DemoContent.getDefaultValue(tabValue),
        theme = theme,
        language = language,
        onValueChange = { newValue ->
            onValueChange(tabValue, newValue)
        }
    )

    panel.addView(section)
    return panel
}

/**
 * Creates the climate panel with multiple sections and radio groups.
 * 5 sections × ~283px each = ~1415px total, overflowing the ~670px panel.
 *
 * @param context Android context
 * @param theme Theme enum value
 * @param language Language enum value
 * @param onValueChange Callback when value changes
 * @return Configured Panel instance
 */
private fun createClimatePanelWithMultipleRadios(
    context: android.content.Context,
    theme: ru.voboost.components.theme.Theme,
    language: ru.voboost.components.i18n.Language,
    onValueChange: (String, String) -> Unit
): ru.voboost.components.panel.Panel {
    val panel = ru.voboost.components.panel.Panel(context)

    val sectionCount = DemoContent.getRadioSectionCount()

    for (i in 0 until sectionCount) {
        val section = ru.voboost.components.section.Section(context).apply {
            setTheme(theme)
            setLanguage(language)
            setTitle(DemoContent.getRadioSectionTitle(i))
        }

        val radio = ru.voboost.components.radio.Radio(context).apply {
            setTheme(theme)
            setLanguage(language)
            setButtons(DemoContent.getRadioSubRadioButtons(i))
            setSelectedValue(DemoContent.getRadioSubDefaultValue(i))
            setOnValueChangeListener { newValue ->
                onValueChange("radio_$i", newValue)
            }
        }

        section.addView(radio)
        // Add Section directly to Panel's built-in ScrollView
        panel.addView(section)
    }

    return panel
}

/**
 * Creates the components panel showcasing Button, Checkbox, Select, and Dialog.
 */
private fun createComponentsPanel(
    context: android.content.Context,
    theme: ru.voboost.components.theme.Theme,
    language: ru.voboost.components.i18n.Language,
    combinedTheme: String,
): ru.voboost.components.panel.Panel {
    val panel = ru.voboost.components.panel.Panel(context)

    // Button section
    val buttonSection = ru.voboost.components.section.Section(context).apply {
        setTitle(mapOf("en" to "Button", "ru" to "Кнопка"))
        setTheme(theme)
        setLanguage(language)
    }

    val buttonRow = android.widget.LinearLayout(context).apply {
        orientation = android.widget.LinearLayout.HORIZONTAL
        setPadding(0, 10, 0, 10)
    }

    val primaryBtn = ru.voboost.components.button.Button(context).apply {
        setTheme(theme)
        setStyle(ru.voboost.components.button.ButtonStyle.PRIMARY)
        setText("Primary")
        layoutParams = android.widget.LinearLayout.LayoutParams(
            0, 80, 1f)
    }

    val secondaryBtn = ru.voboost.components.button.Button(context).apply {
        setTheme(theme)
        setStyle(ru.voboost.components.button.ButtonStyle.SECONDARY)
        setText("Secondary")
        layoutParams = android.widget.LinearLayout.LayoutParams(
            0, 80, 1f).also {
            it.leftMargin = 20
        }
    }

    buttonRow.addView(primaryBtn)
    buttonRow.addView(secondaryBtn)
    buttonSection.addView(buttonRow)
    panel.addView(buttonSection)

    // Checkbox section
    val checkboxSection = ru.voboost.components.section.Section(context).apply {
        setTitle(mapOf("en" to "Checkbox", "ru" to "Переключатель"))
        setTheme(theme)
        setLanguage(language)
    }

    val cbRow = android.widget.LinearLayout(context).apply {
        orientation = android.widget.LinearLayout.HORIZONTAL
        setPadding(0, 10, 0, 10)
    }

    val checkboxOn = ru.voboost.components.checkbox.Checkbox(context).apply {
        setTheme(theme)
        setChecked(true)
    }

    val checkboxOff = ru.voboost.components.checkbox.Checkbox(context).apply {
        setTheme(theme)
        setChecked(false)
        layoutParams = android.widget.LinearLayout.LayoutParams(
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT,
            android.widget.LinearLayout.LayoutParams.WRAP_CONTENT).also {
            it.leftMargin = 40
        }
    }

    cbRow.addView(checkboxOn)
    cbRow.addView(checkboxOff)
    checkboxSection.addView(cbRow)
    panel.addView(checkboxSection)

    // Select section
    val selectSection = ru.voboost.components.section.Section(context).apply {
        setTitle(mapOf("en" to "Select", "ru" to "Выбор"))
        setTheme(theme)
        setLanguage(language)
    }

    // Use Select.kt wrapper through section extension
    val select = selectSection.addSelect(
        options = ru.voboost.components.demo.shared.DemoContent.getSelectOptions(),
        selectedValue = "auto",
        theme = theme,
        language = language,
        onValueChange = { newValue ->
            android.util.Log.d("ComposeDemo", "Select value changed to: $newValue")
        }
    )

    panel.addView(selectSection)

    // Dialog section
    val dialogSection = ru.voboost.components.section.Section(context).apply {
        setTitle(mapOf("en" to "Dialog", "ru" to "Диалог"))
        setTheme(theme)
        setLanguage(language)
    }

    val dialogContent = ru.voboost.components.demo.shared.DemoContent.getDialogContent()

    val dialogTrigger = ru.voboost.components.button.Button(context).apply {
        setTheme(theme)
        setStyle(ru.voboost.components.button.ButtonStyle.SECONDARY)
        setText(dialogContent["title"]?.getOrDefault(language.code, "Show Dialog") ?: "Show Dialog")
        setOnClickListener { v ->
            val dialog = ru.voboost.components.dialog.Dialog(context)
            dialog.setTheme(ru.voboost.components.theme.Theme.fromValue(combinedTheme))
            dialog.setTitle(dialogContent["title"]?.getOrDefault(language.code, "Reset") ?: "Reset")
            dialog.setMessage(dialogContent["message"]?.getOrDefault(language.code, "Are you sure?") ?: "Are you sure?")
            dialog.setConfirmButton(
                dialogContent["confirm"]?.getOrDefault(language.code, "OK") ?: "OK"
            ) { android.util.Log.d("ComposeDemo", "Dialog confirmed") }
            dialog.setCancelButton(
                dialogContent["cancel"]?.getOrDefault(language.code, "Cancel") ?: "Cancel"
            ) { android.util.Log.d("ComposeDemo", "Dialog cancelled") }
            dialog.show()
        }
    }

    dialogSection.addView(dialogTrigger)
    panel.addView(dialogSection)

    // Toast section
    val toastSection = ru.voboost.components.section.Section(context).apply {
        setTitle(mapOf("en" to "Toast", "ru" to "Уведомление"))
        setTheme(theme); setLanguage(language)
    }

    val toastShortButton = ru.voboost.components.button.Button(context).apply {
        setTheme(theme); setStyle(ru.voboost.components.button.ButtonStyle.SECONDARY)
        setText(mapOf("en" to "Show Short Toast", "ru" to "Короткое уведомление")[language.code] ?: "Show Short Toast")
        setOnClickListener {
            val message = mapOf("en" to "Settings saved", "ru" to "Настройки сохранены")
            android.util.Log.d("ComposeDemo", "Toast: ${message[language.code]}")
        }
    }

    val toastLongButton = ru.voboost.components.button.Button(context).apply {
        setTheme(theme); setStyle(ru.voboost.components.button.ButtonStyle.SECONDARY)
        setText(mapOf("en" to "Show Long Toast", "ru" to "Длинное уведомление")[language.code] ?: "Show Long Toast")
        layoutParams = android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT).also { it.topMargin = 14 }
        setOnClickListener {
            val message = mapOf("en" to "Your settings have been successfully saved", "ru" to "Ваши настройки успешно сохранены")
            android.util.Log.d("ComposeDemo", "Toast: ${message[language.code]}")
        }
    }

    toastSection.addView(toastShortButton)
    toastSection.addView(toastLongButton)
    panel.addView(toastSection)

    // Radio + Text section
    val rwtSection = ru.voboost.components.section.Section(context).apply {
        setTitle(mapOf("en" to "Radio + Text", "ru" to "Radio + Text"))
        setTheme(theme); setLanguage(language)
    }

    val rwt1 = ru.voboost.components.radio.Radio(context).apply {
        setTheme(theme); setLanguage(language)
        setTitle(mapOf("en" to "Come home lights", "ru" to "Подсветка дороги домой"))
        setDescription(mapOf("en" to "Headlights stay on after locking", "ru" to "Фары остаются включёнными после блокировки"))
        setButtons(listOf(
            ru.voboost.components.radio.RadioButton("off", mapOf("en" to "Off", "ru" to "Выкл")),
            ru.voboost.components.radio.RadioButton("15", mapOf("en" to "15s", "ru" to "15с")),
            ru.voboost.components.radio.RadioButton("30", mapOf("en" to "30s", "ru" to "30с")),
            ru.voboost.components.radio.RadioButton("60", mapOf("en" to "60s", "ru" to "60с"))))
        setSelectedValue("30")
    }
    rwtSection.addView(rwt1)

    val rwt2 = ru.voboost.components.radio.Radio(context).apply {
        setTheme(theme); setLanguage(language)
        setTitle(mapOf("en" to "Anti-theft alarm", "ru" to "Противоугонная сигнализация"))
        setButtons(listOf(
            ru.voboost.components.radio.RadioButton("light", mapOf("en" to "Light", "ru" to "Свет")),
            ru.voboost.components.radio.RadioButton("light_sound", mapOf("en" to "Light + Sound", "ru" to "Свет + Звук"))))
        setSelectedValue("light")
    }
    rwtSection.addView(rwt2)

    val rwt3 = ru.voboost.components.radio.Radio(context).apply {
        setTheme(theme); setLanguage(language)
        setTitle(mapOf("en" to "Energy recovery", "ru" to "Рекуперация энергии"))
        setDescriptionAbove(mapOf("en" to "Adjusts braking energy recovery level", "ru" to "Регулирует уровень рекуперации торможения"))
        setDescription(mapOf("en" to "Higher levels increase range but feel stronger braking", "ru" to "Более высокие уровни увеличивают запас хода"))
        setButtons(listOf(
            ru.voboost.components.radio.RadioButton("low", mapOf("en" to "Low", "ru" to "Низ")),
            ru.voboost.components.radio.RadioButton("med", mapOf("en" to "Med", "ru" to "Сред")),
            ru.voboost.components.radio.RadioButton("high", mapOf("en" to "High", "ru" to "Выс"))))
        setSelectedValue("med")
    }
    rwtSection.addView(rwt3)
    panel.addView(rwtSection)

    // Checkbox + Label section
    val cwtSection = ru.voboost.components.section.Section(context).apply {
        setTitle(mapOf("en" to "Checkbox + Label", "ru" to "Checkbox + Label"))
        setTheme(theme); setLanguage(language)
    }

    val cwt1 = cwtSection.addCheckbox(
        checked = false,
        theme = theme,
        language = language,
        label = mapOf("en" to "Tow mode", "ru" to "Режим буксировки"),
        description = mapOf("en" to "Maintain N gear when vehicle is rescued", "ru" to "Поддерживать нейтраль при буксировке")
    )

    val cwt2 = cwtSection.addCheckbox(
        checked = true,
        theme = theme,
        language = language,
        label = mapOf("en" to "Auto-fold mirrors", "ru" to "Автоскладывание зеркал")
    )

    val cwt3 = cwtSection.addCheckbox(
        checked = true,
        theme = theme,
        language = language,
        label = mapOf("en" to "Welcome lamp", "ru" to "Приветственная подсветка"),
        description = mapOf("en" to "Lights activate when approaching the vehicle", "ru" to "Подсветка при приближении к автомобилю")
    )
    panel.addView(cwtSection)

    // Button + Description section
    val bwtSection = ru.voboost.components.section.Section(context).apply {
        setTitle(mapOf("en" to "Button + Description", "ru" to "Button + Description"))
        setTheme(theme); setLanguage(language)
    }

    val bwt1 = ru.voboost.components.button.Button(context).apply {
        setTheme(theme); setLanguage(language)
        setText("Settings"); setStyle(ru.voboost.components.button.ButtonStyle.SECONDARY)
        setDescription(mapOf("en" to "Open advanced settings", "ru" to "Открыть расширенные настройки"))
    }
    bwtSection.addView(bwt1)

    val bwt2 = ru.voboost.components.button.Button(context).apply {
        setTheme(theme); setLanguage(language)
        setText("Calibrate"); setStyle(ru.voboost.components.button.ButtonStyle.PRIMARY)
        setDescription(mapOf("en" to "Run camera calibration.\nDrive straight for 2 minutes.", "ru" to "Запустить калибровку камеры.\nДвигайтесь прямо 2 минуты."))
        layoutParams = android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT).also { it.topMargin = 14 }
    }
    bwtSection.addView(bwt2)
    panel.addView(bwtSection)

    // Buttons section
    val bswSection = ru.voboost.components.section.Section(context).apply {
        setTitle(mapOf("en" to "Buttons", "ru" to "Buttons"))
        setTheme(theme); setLanguage(language)
    }

    val bsw1 = ru.voboost.components.buttons.Buttons(context).apply {
        setTheme(theme); setLanguage(language)
        setButtons(listOf(ru.voboost.components.buttons.ButtonConfig("lower", "Lower"), ru.voboost.components.buttons.ButtonConfig("restore", "Restore")))
        setSelectedValue("restore")
        setRightText(mapOf("en" to "Suspension level", "ru" to "Уровень подвески"))
        setDescription(mapOf("en" to "Lower suspension when parked for easy entry", "ru" to "Понизить подвеску при парковке для удобной посадки"))
    }
    bswSection.addView(bsw1)

    val bsw2 = ru.voboost.components.buttons.Buttons(context).apply {
        setTheme(theme); setLanguage(language)
        setButtons(listOf(ru.voboost.components.buttons.ButtonConfig("eco", "ECO"), ru.voboost.components.buttons.ButtonConfig("comfort", "Comfort"), ru.voboost.components.buttons.ButtonConfig("sport", "Sport")))
        setSelectedValue("comfort")
        setDescription(mapOf("en" to "Select preferred driving mode", "ru" to "Выберите предпочтительный режим вождения"))
        layoutParams = android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT).also { it.topMargin = 14 }
    }
    bswSection.addView(bsw2)

    val bsw3 = ru.voboost.components.buttons.Buttons(context).apply {
        setTheme(theme); setLanguage(language)
        setButtons(listOf(ru.voboost.components.buttons.ButtonConfig("on", "ON"), ru.voboost.components.buttons.ButtonConfig("off", "OFF")))
        setSelectedValue("on")
        setRightText(mapOf("en" to "Auto headlamp", "ru" to "Авто фары"))
        layoutParams = android.widget.LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT).also { it.topMargin = 14 }
    }
    bswSection.addView(bsw3)
    panel.addView(bswSection)

    return panel
}
