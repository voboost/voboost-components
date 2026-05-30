package ru.voboost.components.demo.kotlin

import ru.voboost.components.radio.Radio
import ru.voboost.components.radio.RadioButton
import ru.voboost.components.section.Section
import ru.voboost.components.i18n.Language
import ru.voboost.components.theme.Theme

/**
 * Extension function to add a Radio component to a Section with Builder pattern.
 *
 * @param buttons List of radio buttons to display
 * @param selectedValue Initially selected value
 * @param theme Theme to apply
 * @param language Language to apply
 * @param onValueChange Optional callback for value changes
 */
fun Section.addRadio(
    buttons: List<RadioButton>,
    selectedValue: String,
    theme: Theme,
    language: Language,
    onValueChange: ((String) -> Unit)? = null
) {
    val builder = Radio.create(context, theme, language, buttons, selectedValue)
    if (onValueChange != null) {
        builder.onValueChange(onValueChange)
    }
    val radio = builder.build()
    this.addRadio(radio)
}
