package ru.voboost.components.demo.compose

import android.content.Context
import android.widget.LinearLayout
import ru.voboost.components.button.Button
import ru.voboost.components.button.ButtonStyle
import ru.voboost.components.demo.shared.DemoContent
import ru.voboost.components.dialog.Dialog
import ru.voboost.components.i18n.Language
import ru.voboost.components.panel.Panel
import ru.voboost.components.section.Section
import ru.voboost.components.section.addButton
import ru.voboost.components.section.addButtons
import ru.voboost.components.section.addCheckbox
import ru.voboost.components.section.addRadio
import ru.voboost.components.section.addSelect
import ru.voboost.components.theme.Theme
import ru.voboost.components.toast.ToastTheme

/**
 * Panel factories for the Compose demo. Each tab maps one-to-one to demo-java
 * and demo-kotlin; all localized content comes from DemoContent. Panels are
 * created once and reused; theme/language propagate through the Screen wrapper,
 * while dialog and toast read the latest values via providers.
 */
fun createPanelForTab(
    context: Context,
    tabValue: String,
    theme: Theme,
    language: Language,
    onValueChange: (String, String) -> Unit,
    themeProvider: () -> Theme,
    languageProvider: () -> Language,
    showToast: (String, Long) -> Unit,
): Panel = when (tabValue) {
    "settings" -> createSettingsPanel(context, theme, language, onValueChange)
    "button" -> createButtonPanel(context, theme, language)
    "buttons" -> createButtonsPanel(context, theme, language)
    "checkbox" -> createCheckboxPanel(context, theme, language)
    "radio" -> createRadioPanel(context, theme, language)
    "select" -> createSelectPanel(context, theme, language)
    "dialog" -> createDialogPanel(context, theme, language, themeProvider, languageProvider)
    "toast" -> createToastPanel(context, theme, language, languageProvider, showToast)
    else -> throw IllegalStateException("Unknown tab: $tabValue")
}

private fun createSettingsPanel(
    context: Context,
    theme: Theme,
    language: Language,
    onValueChange: (String, String) -> Unit,
): Panel {
    val panel = Panel(context)

    val section = Section(context).apply {
        setTitle(DemoContent.getSectionTitle("settings"))
        setTheme(theme)
        setLanguage(language)
    }

    section.addRadio(
        buttons = DemoContent.getRadioButtons("language"),
        selectedValue = DemoContent.getDefaultValue("language"),
        theme = theme,
        language = language,
        onValueChange = { onValueChange("language", it) },
    )
    section.addRadio(
        buttons = DemoContent.getRadioButtons("theme"),
        selectedValue = DemoContent.getDefaultValue("theme"),
        theme = theme,
        language = language,
        onValueChange = { onValueChange("theme", it) },
    )
    section.addRadio(
        buttons = DemoContent.getRadioButtons("car_type"),
        selectedValue = DemoContent.getDefaultValue("car_type"),
        theme = theme,
        language = language,
        onValueChange = { onValueChange("car_type", it) },
    )

    panel.addView(section)
    return panel
}

private fun createButtonPanel(context: Context, theme: Theme, language: Language): Panel {
    val panel = Panel(context)

    for (i in 0 until DemoContent.getButtonSectionCount()) {
        val section = Section(context).apply {
            setTitle(DemoContent.getButtonSectionTitle(i))
            setTheme(theme)
            setLanguage(language)
        }

        if (i == 0) {
            val buttonRow = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(0, 10, 0, 10)
            }

            val primaryBtn = Button(context).apply {
                setTheme(theme)
                setStyle(ButtonStyle.PRIMARY)
                setText(DemoContent.getButtonPrimaryText())
                layoutParams = LinearLayout.LayoutParams(0, 80, 1f)
            }

            val secondaryBtn = Button(context).apply {
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

private fun createButtonsPanel(context: Context, theme: Theme, language: Language): Panel {
    val panel = Panel(context)

    for (i in 0 until DemoContent.getButtonsSectionCount()) {
        val section = Section(context).apply {
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

private fun createCheckboxPanel(context: Context, theme: Theme, language: Language): Panel {
    val panel = Panel(context)

    for (i in 0 until DemoContent.getCheckboxSectionCount()) {
        val section = Section(context).apply {
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

private fun createRadioPanel(context: Context, theme: Theme, language: Language): Panel {
    val panel = Panel(context)

    for (i in 0 until DemoContent.getRadioSectionCount()) {
        val section = Section(context).apply {
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

private fun createSelectPanel(context: Context, theme: Theme, language: Language): Panel {
    val panel = Panel(context)

    val section = Section(context).apply {
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

private fun createDialogPanel(
    context: Context,
    theme: Theme,
    language: Language,
    themeProvider: () -> Theme,
    languageProvider: () -> Language,
): Panel {
    val panel = Panel(context)

    val section = Section(context).apply {
        setTitle(DemoContent.getDialogSectionTitle())
        setTheme(theme)
        setLanguage(language)
    }

    val dialogContent = DemoContent.getDialogContent()

    section.addButton(
        text = dialogContent["title"]?.getOrDefault(language.code, "Show Dialog") ?: "Show Dialog",
        style = ButtonStyle.PRIMARY,
        theme = theme,
        language = language,
        onClick = {
            val lang = languageProvider().code
            val dialog = Dialog(context)
            dialog.setTheme(themeProvider())
            dialog.setTitle(dialogContent["title"]?.getOrDefault(lang, "Reset") ?: "Reset")
            dialog.setMessage(dialogContent["message"]?.getOrDefault(lang, "Are you sure?") ?: "Are you sure?")
            dialog.setConfirmButton(
                dialogContent["confirm"]?.getOrDefault(lang, "OK") ?: "OK",
            ) { }
            dialog.setCancelButton(
                dialogContent["cancel"]?.getOrDefault(lang, "Cancel") ?: "Cancel",
            ) { }
            dialog.show()
        },
    )

    panel.addView(section)
    return panel
}

private fun createToastPanel(
    context: Context,
    theme: Theme,
    language: Language,
    languageProvider: () -> Language,
    showToast: (String, Long) -> Unit,
): Panel {
    val panel = Panel(context)

    for (i in 0 until DemoContent.getToastSectionCount()) {
        val section = Section(context).apply {
            setTitle(DemoContent.getToastSectionTitle(i))
            setTheme(theme)
            setLanguage(language)
        }

        val duration = if (i == 0) ToastTheme.DURATION_SHORT else ToastTheme.DURATION_LONG
        val sectionIndex = i
        section.addButton(
            text = DemoContent.getToastButtonText(i).getOrDefault(language.code, "Show Toast"),
            style = ButtonStyle.SECONDARY,
            theme = theme,
            language = language,
            onClick = {
                val message = DemoContent.getToastMessage(sectionIndex)
                showToast(message.getOrDefault(languageProvider().code, ""), duration)
            },
        )

        panel.addView(section)
    }

    return panel
}
