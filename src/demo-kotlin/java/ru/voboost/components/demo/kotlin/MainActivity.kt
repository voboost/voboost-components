package ru.voboost.components.demo.kotlin

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.LinearLayout
import ru.voboost.components.button.Button as ButtonView
import ru.voboost.components.button.ButtonStyle
import ru.voboost.components.demo.shared.DemoContent
import ru.voboost.components.demo.shared.DemoHelpers
import ru.voboost.components.demo.shared.DemoState
import ru.voboost.components.dialog.Dialog
import ru.voboost.components.i18n.Language
import ru.voboost.components.panel.Panel
import ru.voboost.components.radio.Radio as RadioView
import ru.voboost.components.screen.Screen
import ru.voboost.components.section.Section
import ru.voboost.components.section.addButton
import ru.voboost.components.section.addButtons
import ru.voboost.components.section.addCheckbox
import ru.voboost.components.section.addRadio
import ru.voboost.components.section.addSelect
import ru.voboost.components.tabs.Tabs
import ru.voboost.components.theme.Theme
import ru.voboost.components.toast.ToastTheme

/**
 * Demo Kotlin Activity showcasing voboost-components in a pure Kotlin project.
 *
 * Mirrors demo-java one-to-one (eight tabs: settings, button, buttons, checkbox,
 * radio, select, dialog, toast) using idiomatic Kotlin: apply blocks, when
 * expressions, null safety and the library Section extension functions
 * (addRadio/addButton/addCheckbox/addButtons/addSelect). All localized content
 * comes from DemoContent; theme and language propagate through Screen.
 */
class MainActivity : Activity() {

    companion object {
        private const val TAG = "KotlinDemo"

        private val TAB_VALUES = listOf(
            "settings", "button", "buttons", "checkbox",
            "radio", "select", "dialog", "toast",
        )
    }

    private lateinit var screen: Screen
    private lateinit var demoState: DemoState

    // Settings radios kept for state application in tests
    private var languageRadio: RadioView? = null
    private var themeRadio: RadioView? = null
    private var carTypeRadio: RadioView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        demoState = DemoState()

        setupFullScreenMode()
        setupComponentHierarchy()
        setupDemoComponents()
        updateAllComponents()
    }

    /**
     * Enables full-screen immersive mode for the automotive display.
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
            // DecorView is not fully initialized in Robolectric unit tests
            Log.d(TAG, "Ignoring NullPointerException in setupFullScreenMode (Robolectric)", e)
        }
    }

    /**
     * Builds the Screen -> Tabs -> Panels hierarchy.
     */
    private fun setupComponentHierarchy() {
        screen = Screen(this)
        setContentView(screen)

        val tabs = Tabs(this)
        screen.setTabs(tabs)

        screen.setPanels(createAllPanels())
    }

    /**
     * Configures tabs content, initial selection and listeners.
     */
    private fun setupDemoComponents() {
        val tabs = screen.getTabs()
        tabs.setItems(DemoContent.getTabItems())
        tabs.setTheme(Theme.fromValue(demoState.combinedTheme))
        tabs.setLanguage(Language.fromCode(demoState.currentLanguage))
        tabs.setSelectedValue(demoState.selectedTab, false)

        screen.setOnScreenLiftListener { state ->
            demoState.screenLiftState = state
            updateAllComponents()
        }

        tabs.setOnValueChangeListener { selectedTab ->
            demoState.selectedTab = selectedTab
            updateAllComponents()
        }
    }

    private fun createAllPanels(): Array<Panel> =
        TAB_VALUES.map { createPanelForTab(it) }.toTypedArray()

    private fun createPanelForTab(tabValue: String): Panel {
        val theme = Theme.fromValue(demoState.combinedTheme)
        val language = Language.fromCode(demoState.currentLanguage)

        return when (tabValue) {
            "settings" -> createSettingsPanel(theme, language)
            "button" -> createButtonPanel(theme, language)
            "buttons" -> createButtonsPanel(theme, language)
            "checkbox" -> createCheckboxPanel(theme, language)
            "radio" -> createRadioPanel(theme, language)
            "select" -> createSelectPanel(theme, language)
            "dialog" -> createDialogPanel(theme, language)
            "toast" -> createToastPanel(theme, language)
            else -> throw IllegalStateException("Unknown tab: $tabValue")
        }
    }

    /**
     * Propagates current state to the whole component tree.
     */
    private fun updateAllComponents() {
        if (!::screen.isInitialized) return
        val combinedTheme = demoState.combinedTheme
        screen.setBackgroundColor(DemoHelpers.getBackgroundColor(combinedTheme))
        screen.setTheme(Theme.fromValue(combinedTheme))
        screen.setLanguage(Language.fromCode(demoState.currentLanguage))
    }

    /**
     * Applies a full settings state. Used by visual tests to render combinations.
     */
    fun applyState(language: String, theme: String, carType: String) {
        demoState.currentLanguage = language
        demoState.currentTheme = theme
        demoState.currentCarType = carType
        languageRadio?.setSelectedValue(language)
        themeRadio?.setSelectedValue(theme)
        carTypeRadio?.setSelectedValue(carType)
        updateAllComponents()
    }

    // Getters for testing
    internal fun getScreen(): Screen = screen
    internal fun getDemoState(): DemoState = demoState
    internal fun getTabs(): Tabs = screen.getTabs()

    // ============================================================
    // Panel creators
    // ============================================================

    private fun createSettingsPanel(theme: Theme, language: Language): Panel {
        val panel = Panel(this)

        val section = Section(this).apply {
            setTitle(DemoContent.getSectionTitle("settings"))
            setTheme(theme)
            setLanguage(language)
        }

        languageRadio = section.addRadio(
            buttons = DemoContent.getRadioButtons("language"),
            selectedValue = demoState.currentLanguage,
            theme = theme,
            language = language,
            onValueChange = { newValue ->
                demoState.currentLanguage = newValue
                updateAllComponents()
            },
        )

        themeRadio = section.addRadio(
            buttons = DemoContent.getRadioButtons("theme"),
            selectedValue = demoState.currentTheme,
            theme = theme,
            language = language,
            onValueChange = { newValue ->
                demoState.currentTheme = newValue
                updateAllComponents()
            },
        )

        carTypeRadio = section.addRadio(
            buttons = DemoContent.getRadioButtons("car_type"),
            selectedValue = demoState.currentCarType,
            theme = theme,
            language = language,
            onValueChange = { newValue ->
                demoState.currentCarType = newValue
                updateAllComponents()
            },
        )

        panel.addView(section)
        return panel
    }

    private fun createButtonPanel(theme: Theme, language: Language): Panel {
        val panel = Panel(this)

        for (i in 0 until DemoContent.getButtonSectionCount()) {
            val section = Section(this).apply {
                setTitle(DemoContent.getButtonSectionTitle(i))
                setTheme(theme)
                setLanguage(language)
            }

            if (i == 0) {
                val buttonRow = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(0, 10, 0, 10)
                }

                val primaryBtn = ButtonView(this).apply {
                    setTheme(theme)
                    setStyle(ButtonStyle.PRIMARY)
                    setText(DemoContent.getButtonPrimaryText())
                    layoutParams = LinearLayout.LayoutParams(0, 80, 1f)
                }

                val secondaryBtn = ButtonView(this).apply {
                    setTheme(theme)
                    setStyle(ButtonStyle.SECONDARY)
                    setText(DemoContent.getButtonSecondaryText())
                    layoutParams = LinearLayout.LayoutParams(0, 80, 1f).also { it.leftMargin = 20 }
                }

                buttonRow.addView(primaryBtn)
                buttonRow.addView(secondaryBtn)
                section.addView(buttonRow)
            } else {
                section.addButton(
                    text = DemoContent.getButtonWithDescriptionText(),
                    style = ButtonStyle.PRIMARY,
                    theme = theme,
                    language = language,
                    description = DemoContent.getButtonWithDescriptionDescription(),
                )
            }

            panel.addView(section)
        }

        return panel
    }

    private fun createButtonsPanel(theme: Theme, language: Language): Panel {
        val panel = Panel(this)

        for (i in 0 until DemoContent.getButtonsSectionCount()) {
            val section = Section(this).apply {
                setTitle(DemoContent.getButtonsSectionTitle(i))
                setTheme(theme)
                setLanguage(language)
            }

            val rightText = DemoContent.getButtonsRightText(i)
            section.addButtons(
                buttons = DemoContent.getButtonsConfig(i),
                selectedValue = DemoContent.getButtonsDefaultValue(i),
                theme = theme,
                language = language,
                rightText = if (rightText.isEmpty()) null else rightText,
            )

            panel.addView(section)
        }

        return panel
    }

    private fun createCheckboxPanel(theme: Theme, language: Language): Panel {
        val panel = Panel(this)

        for (i in 0 until DemoContent.getCheckboxSectionCount()) {
            val section = Section(this).apply {
                setTitle(DemoContent.getCheckboxSectionTitle(i))
                setTheme(theme)
                setLanguage(language)
            }

            if (i == 2) {
                section.addCheckbox(
                    checked = DemoContent.getCheckboxChecked(i),
                    theme = theme,
                    language = language,
                    label = DemoContent.getCheckboxLabel(i),
                )
                section.addCheckbox(
                    checked = DemoContent.getCheckboxExtraChecked(),
                    theme = theme,
                    language = language,
                    label = DemoContent.getCheckboxExtraLabel(),
                )
            } else {
                val description = DemoContent.getCheckboxDescription(i)
                section.addCheckbox(
                    checked = DemoContent.getCheckboxChecked(i),
                    theme = theme,
                    language = language,
                    label = DemoContent.getCheckboxLabel(i),
                    description = if (description.isEmpty()) null else description,
                )
            }

            panel.addView(section)
        }

        return panel
    }

    private fun createRadioPanel(theme: Theme, language: Language): Panel {
        val panel = Panel(this)

        for (i in 0 until DemoContent.getRadioSectionCount()) {
            val section = Section(this).apply {
                setTitle(DemoContent.getRadioSectionTitle(i))
                setTheme(theme)
                setLanguage(language)
            }

            val descriptionAbove = DemoContent.getRadioSubDescriptionAbove(i)
            val descriptionBelow = DemoContent.getRadioSubDescriptionBelow(i)
            section.addRadio(
                buttons = DemoContent.getRadioSubRadioButtons(i),
                selectedValue = DemoContent.getRadioSubDefaultValue(i),
                theme = theme,
                language = language,
                title = DemoContent.getRadioSubTitle(i),
                descriptionAbove = if (descriptionAbove.isEmpty()) null else descriptionAbove,
                descriptionBelow = if (descriptionBelow.isEmpty()) null else descriptionBelow,
            )

            panel.addView(section)
        }

        return panel
    }

    private fun createSelectPanel(theme: Theme, language: Language): Panel {
        val panel = Panel(this)

        val section = Section(this).apply {
            setTitle(DemoContent.getSelectSectionTitle())
            setTheme(theme)
            setLanguage(language)
        }

        section.addSelect(
            options = DemoContent.getSelectOptions(),
            selectedValue = "auto",
            theme = theme,
            language = language,
        )

        panel.addView(section)
        return panel
    }

    private fun createDialogPanel(theme: Theme, language: Language): Panel {
        val panel = Panel(this)

        val section = Section(this).apply {
            setTitle(DemoContent.getDialogSectionTitle())
            setTheme(theme)
            setLanguage(language)
        }

        val dialogContent = DemoContent.getDialogContent()

        section.addButton(
            text = dialogContent["title"]?.getOrDefault(demoState.currentLanguage, "Show Dialog") ?: "Show Dialog",
            style = ButtonStyle.PRIMARY,
            theme = theme,
            language = language,
            onClick = {
                val lang = demoState.currentLanguage
                val dialog = Dialog(this)
                dialog.setTheme(Theme.fromValue(demoState.combinedTheme))
                dialog.setTitle(dialogContent["title"]?.getOrDefault(lang, "Reset") ?: "Reset")
                dialog.setMessage(dialogContent["message"]?.getOrDefault(lang, "Are you sure?") ?: "Are you sure?")
                dialog.setConfirmButton(
                    dialogContent["confirm"]?.getOrDefault(lang, "OK") ?: "OK",
                ) { Log.d(TAG, "Dialog confirmed") }
                dialog.setCancelButton(
                    dialogContent["cancel"]?.getOrDefault(lang, "Cancel") ?: "Cancel",
                ) { Log.d(TAG, "Dialog cancelled") }
                dialog.show()
            },
        )

        panel.addView(section)
        return panel
    }

    private fun createToastPanel(theme: Theme, language: Language): Panel {
        val panel = Panel(this)

        for (i in 0 until DemoContent.getToastSectionCount()) {
            val section = Section(this).apply {
                setTitle(DemoContent.getToastSectionTitle(i))
                setTheme(theme)
                setLanguage(language)
            }

            val duration = if (i == 0) ToastTheme.DURATION_SHORT else ToastTheme.DURATION_LONG
            val sectionIndex = i
            section.addButton(
                text = DemoContent.getToastButtonText(i).getOrDefault(demoState.currentLanguage, "Show Toast"),
                style = ButtonStyle.SECONDARY,
                theme = theme,
                language = language,
                onClick = {
                    val message = DemoContent.getToastMessage(sectionIndex)
                    screen.showToast(message.getOrDefault(demoState.currentLanguage, ""), duration)
                },
            )

            panel.addView(section)
        }

        return panel
    }
}
