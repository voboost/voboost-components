package ru.voboost.components.section

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import ru.voboost.components.i18n.Language
import ru.voboost.components.theme.Theme

/**
 * Data class for a single content block in the info popup.
 */
data class SectionPopupBlock(
    val title: Map<String, String>? = null,
    val text: Map<String, String>,
)

/**
 * Section component for Jetpack Compose.
 *
 * A titled container with rounded corners that can contain child views.
 * Wraps the Java ViewGroup-based Section component.
 *
 * ## Usage Examples
 *
 * ### Option 1: Using the Section Compose Wrapper
 * ```kotlin
 * Section(
 *     title = mapOf("en" to "Settings", "ru" to "Настройки"),
 *     lang = Language.EN,
 *     theme = Theme.FREE_LIGHT
 * ) { section ->
 *     // Add child using extension functions (recommended)
 *     section.addRadio(
 *         buttons = listOf(...),
 *         selectedValue = "en",
 *         theme = theme,
 *         language = Language.EN
 *     ) { newValue -> /* handle change */ }
 *
 *     // Or add child directly
 *     val button = Button(context).apply { ... }
 *     section.addView(button)
 * }
 * ```
 *
 * ### Option 2: Using Panel.addSection() Extension (Recommended)
 * ```kotlin
 * panel.addSection(
 *     title = mapOf("en" to "Settings"),
 *     theme = Theme.FREE_LIGHT,
 *     language = Language.EN
 * ) { section ->
 *     section.addRadio(
 *         buttons = listOf(...),
 *         selectedValue = "en",
 *         theme = theme,
 *         language = Language.EN
 *     )
 * }
 * ```
 *
 * ### Option 3: Direct Java View Creation (for special cases)
 * ```kotlin
 * val section = ru.voboost.components.section.Section(context).apply {
 *     setTheme(theme)
 *     setLanguage(language)
 *     setTitle(title)
 * }
 * section.addView(childView)
 * ```
 *
 * ## Integration with Panel
 *
 * Section components are typically added to Panel containers:
 * ```kotlin
 * Panel(context).apply {
 *     setTheme(theme)
 *     setLanguage(language)
 *     // Panel has built-in ScrollView, all sections will be scrollable
 *     addSection(title = "Section 1", theme = theme, language = language) { ... }
 *     addSection(title = "Section 2", theme = theme, language = language) { ... }
 * }
 * ```
 *
 * ## Theme and Language Propagation
 *
 * Section automatically propagates theme and language to child components:
 * ```kotlin
 * Section(
 *     title = mapOf("en" to "Settings"),
 *     lang = Language.EN,
 *     theme = Theme.FREE_LIGHT
 * ) { section ->
 *     // Child components will receive theme and language from Section
 *     section.addRadio(
 *         buttons = listOf(...),
 *         selectedValue = "en",
 *         theme = Theme.FREE_LIGHT,
 *         language = Language.EN
 *     )
 * }
 * ```
 *
 * @param title Map of language code to localized title text
 * @param lang Language enum value for localization (default: EN)
 * @param theme Theme enum value
 * @param titleChecked Initial checked state for title-checkbox (null = disabled)
 * @param onTitleCheckedChange Callback when title-checkbox is toggled
 * @param infoText Single info text block (null = no info icon)
 * @param infoBlocks Multiple info text blocks (overrides infoText)
 * @param content Lambda that receives the Section ViewGroup to add child views
 */
@Composable
fun Section(
    title: Map<String, String>,
    lang: Language,
    theme: Theme,
    titleChecked: Boolean? = null,
    onTitleCheckedChange: ((Boolean) -> Unit)? = null,
    infoText: Map<String, String>? = null,
    infoBlocks: List<SectionPopupBlock>? = null,
    content: ((ru.voboost.components.section.Section) -> Unit)? = null,
) {
    AndroidView(
        factory = { context ->
            ru.voboost.components.section.Section(context).apply {
                setTheme(theme)
                setLanguage(lang)
                setTitle(title)
                if (infoText != null) {
                    setPopupText(infoText)
                } else if (infoBlocks != null) {
                    val blocks =
                        infoBlocks.map { block ->
                            ru.voboost.components.section.Section.PopupBlock(
                                block.title,
                                block.text,
                            )
                        }
                    setPopupText(blocks)
                }
                if (titleChecked != null) {
                    setTitleCheckbox(
                        titleChecked,
                        if (onTitleCheckedChange != null) {
                            {
                                onTitleCheckedChange(it)
                            }
                        } else {
                            null
                        },
                    )
                }
                content?.invoke(this)
            }
        },
        update = { sectionView ->
            sectionView.setTheme(theme)
            sectionView.setLanguage(lang)
            sectionView.setTitle(title)
            if (infoText != null) {
                sectionView.setPopupText(infoText)
            } else if (infoBlocks != null) {
                val blocks =
                    infoBlocks.map { block ->
                        ru.voboost.components.section.Section.PopupBlock(
                            block.title,
                            block.text,
                        )
                    }
                sectionView.setPopupText(blocks)
            } else {
                sectionView.clearPopupText()
            }
            if (titleChecked != null) {
                if (!sectionView.hasTitleCheckbox()) {
                    sectionView.setTitleCheckbox(
                        titleChecked,
                        if (onTitleCheckedChange != null) {
                            {
                                onTitleCheckedChange(it)
                            }
                        } else {
                            null
                        },
                    )
                } else if (sectionView.isTitleChecked() != titleChecked) {
                    sectionView.setTitleCheckbox(titleChecked)
                }
            } else if (sectionView.hasTitleCheckbox()) {
                sectionView.clearTitleCheckbox()
            }
        },
    )
}

// ============================================================
// EXTENSION FUNCTIONS FOR SECTION
// ============================================================

/**
 * Extension function to add a Radio component to Section.
 */
fun ru.voboost.components.section.Section.addRadio(
    buttons: List<ru.voboost.components.radio.RadioButton>,
    selectedValue: String,
    theme: ru.voboost.components.theme.Theme,
    language: ru.voboost.components.i18n.Language,
    title: Map<String, String>? = null,
    descriptionAbove: Map<String, String>? = null,
    descriptionBelow: Map<String, String>? = null,
    marginLeft: Int = 0,
    marginTop: Int = 0,
    marginRight: Int = 0,
    marginBottom: Int = 0,
    onValueChange: ((String) -> Unit)? = null,
): ru.voboost.components.radio.Radio {
    val builder =
        ru.voboost.components.radio.Radio.create(
            context,
            theme,
            language,
            buttons,
            selectedValue,
        )
    if (title != null) {
        builder.title(title)
    }
    if (descriptionAbove != null) {
        builder.descriptionAbove(descriptionAbove)
    }
    if (descriptionBelow != null) {
        builder.description(descriptionBelow)
    }
    builder.margin(marginLeft, marginTop, marginRight, marginBottom)
    if (onValueChange != null) {
        builder.onValueChange { onValueChange(it) }
    }
    return addRadio(builder.build())
}

/**
 * Extension function to add a Button component to Section.
 */
fun ru.voboost.components.section.Section.addButton(
    text: String,
    style: ru.voboost.components.button.ButtonStyle,
    theme: ru.voboost.components.theme.Theme,
    language: ru.voboost.components.i18n.Language,
    description: Map<String, String>? = null,
    marginLeft: Int = 0,
    marginTop: Int = 0,
    marginRight: Int = 0,
    marginBottom: Int = 0,
    onClick: (() -> Unit)? = null,
): ru.voboost.components.button.Button {
    val builder =
        ru.voboost.components.button.Button.create(
            context,
            theme,
            language,
            text,
            style,
        )
    if (description != null) {
        builder.description(description)
    }
    builder.margin(marginLeft, marginTop, marginRight, marginBottom)
    if (onClick != null) {
        builder.onClick { onClick() }
    }
    return addButton(builder.build())
}

/**
 * Extension function to add a Checkbox component to Section.
 */
fun ru.voboost.components.section.Section.addCheckbox(
    checked: Boolean,
    theme: ru.voboost.components.theme.Theme,
    language: ru.voboost.components.i18n.Language,
    label: Map<String, String>? = null,
    description: Map<String, String>? = null,
    marginLeft: Int = 0,
    marginTop: Int = 0,
    marginRight: Int = 0,
    marginBottom: Int = 0,
    onCheckedChange: ((Boolean) -> Unit)? = null,
): ru.voboost.components.checkbox.Checkbox {
    val builder =
        ru.voboost.components.checkbox.Checkbox.create(
            context,
            theme,
            language,
            checked,
        )
    if (label != null) {
        builder.label(label)
    }
    if (description != null) {
        builder.description(description)
    }
    builder.margin(marginLeft, marginTop, marginRight, marginBottom)
    if (onCheckedChange != null) {
        builder.onCheckedChange { onCheckedChange(it) }
    }
    return addCheckbox(builder.build())
}

/**
 * Extension function to add a Select component to Section.
 */
fun ru.voboost.components.section.Section.addSelect(
    options: List<ru.voboost.components.select.SelectOption>,
    selectedValue: String,
    theme: ru.voboost.components.theme.Theme,
    language: ru.voboost.components.i18n.Language,
    marginLeft: Int = 0,
    marginTop: Int = 0,
    marginRight: Int = 0,
    marginBottom: Int = 0,
    onValueChange: ((String) -> Unit)? = null,
): ru.voboost.components.select.Select {
    val builder =
        ru.voboost.components.select.Select.create(
            context,
            theme,
            language,
            options,
            selectedValue,
        )
    builder.margin(marginLeft, marginTop, marginRight, marginBottom)
    if (onValueChange != null) {
        builder.onValueChange { onValueChange(it) }
    }
    return addSelect(builder.build())
}

/**
 * Extension function to add a Buttons component to Section.
 */
fun ru.voboost.components.section.Section.addButtons(
    buttons: List<ru.voboost.components.buttons.ButtonConfig>,
    selectedValue: String,
    theme: ru.voboost.components.theme.Theme,
    language: ru.voboost.components.i18n.Language,
    rightText: Map<String, String>? = null,
    description: Map<String, String>? = null,
    marginLeft: Int = 0,
    marginTop: Int = 0,
    marginRight: Int = 0,
    marginBottom: Int = 0,
    onValueChange: ((String) -> Unit)? = null,
): ru.voboost.components.buttons.Buttons {
    val builder =
        ru.voboost.components.buttons.Buttons.create(
            context,
            theme,
            language,
            buttons,
            selectedValue,
        )
    if (rightText != null) {
        builder.rightText(rightText)
    }
    if (description != null) {
        builder.description(description)
    }
    builder.margin(marginLeft, marginTop, marginRight, marginBottom)
    if (onValueChange != null) {
        builder.onValueChange { onValueChange(it) }
    }
    return addButtons(builder.build())
}
