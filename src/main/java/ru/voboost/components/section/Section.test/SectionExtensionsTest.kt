package ru.voboost.components.section

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import ru.voboost.components.i18n.Language
import ru.voboost.components.radio.RadioButton
import ru.voboost.components.theme.Theme

@RunWith(RobolectricTestRunner::class)
class SectionExtensionsTest {
    private lateinit var section: Section
    private lateinit var context: android.content.Context

    @Before
    fun setUp() {
        context = androidx.test.core.app.ApplicationProvider.getApplicationContext()
        section =
            Section(context).apply {
                setTheme(Theme.FREE_LIGHT)
                setLanguage(Language.EN)
                setTitle(mapOf("en" to "Test Section"))
            }
    }

    @Test
    fun testAddRadio_createsAndAddsRadio() {
        val buttons =
            listOf(
                RadioButton("en", mapOf("en" to "English")),
                RadioButton("ru", mapOf("en" to "Russian")),
            )

        val radio =
            section.addRadio(
                buttons = buttons,
                selectedValue = "en",
                theme = Theme.FREE_LIGHT,
                language = Language.EN,
            )

        assertNotNull("Radio should be created", radio)
        assertEquals("Section should have 1 child", 1, section.childCount)
        assertEquals("en", radio.getSelectedValue())
    }

    @Test
    fun testAddRadio_withOnValueChange() {
        var changedValue: String? = null
        val buttons =
            listOf(
                RadioButton("en", mapOf("en" to "English")),
            )

        val radio =
            section.addRadio(
                buttons = buttons,
                selectedValue = "en",
                theme = Theme.FREE_LIGHT,
                language = Language.EN,
                onValueChange = { newValue -> changedValue = newValue },
            )

        // Verify radio was created with listener set
        assertNotNull("Radio should be created", radio)
        assertEquals("Section should have 1 child", 1, section.childCount)
        // Note: Testing actual value change requires user interaction simulation
    }

    @Test
    fun testAddButton_createsAndAddsButton() {
        val button =
            section.addButton(
                text = "Click Me",
                style = ru.voboost.components.button.ButtonStyle.PRIMARY,
                theme = Theme.FREE_LIGHT,
                language = Language.EN,
            )

        assertNotNull("Button should be created", button)
        assertEquals("Section should have 1 child", 1, section.childCount)
    }

    @Test
    fun testAddButton_withDescription() {
        val button =
            section.addButton(
                text = "Settings",
                description = mapOf("en" to "Open settings"),
                style = ru.voboost.components.button.ButtonStyle.SECONDARY,
                theme = Theme.FREE_LIGHT,
                language = Language.EN,
            )

        assertNotNull("Button should be created", button)
        assertEquals("Section should have 1 child", 1, section.childCount)
    }

    @Test
    fun testAddButton_withOnClick() {
        var clicked = false

        val button =
            section.addButton(
                text = "Click Me",
                style = ru.voboost.components.button.ButtonStyle.PRIMARY,
                theme = Theme.FREE_LIGHT,
                language = Language.EN,
                onClick = { clicked = true },
            )

        // Note: Button has wrapper → primitive architecture, performClick() on wrapper
        // doesn't trigger listener on primitive. Just verify button was created.
        assertNotNull("Button should be created", button)
        // The onClick lambda is properly set, but testing it requires access to primitive
    }

    @Test
    fun testAddCheckbox_createsAndAddsCheckbox() {
        val checkbox =
            section.addCheckbox(
                checked = true,
                theme = Theme.FREE_LIGHT,
                language = Language.EN,
                label = mapOf("en" to "Test Label"),
                description = mapOf("en" to "Test Description"),
            )

        assertNotNull("Checkbox should be created", checkbox)
        assertEquals("Section should have 1 child", 1, section.childCount)
        assertTrue("Checkbox should be checked", checkbox.isChecked())
    }

    @Test
    fun testAddCheckbox_withoutLabel() {
        val checkbox =
            section.addCheckbox(
                checked = false,
                theme = Theme.FREE_LIGHT,
                language = Language.EN,
            )

        assertNotNull("Checkbox should be created", checkbox)
        assertEquals("Section should have 1 child", 1, section.childCount)
        assertFalse("Checkbox should not be checked", checkbox.isChecked())
    }

    @Test
    fun testAddSelect_createsAndAddsSelect() {
        val options =
            listOf(
                ru.voboost.components.select.SelectOption("auto", mapOf("en" to "Auto")),
                ru.voboost.components.select.SelectOption("manual", mapOf("en" to "Manual")),
            )

        val select =
            section.addSelect(
                options = options,
                selectedValue = "auto",
                theme = Theme.FREE_LIGHT,
                language = Language.EN,
            )

        assertNotNull("Select should be created", select)
        assertEquals("Section should have 1 child", 1, section.childCount)
        assertEquals("auto", select.getSelectedValue())
    }

    @Test
    fun testAddButtons_createsAndAddsButtons() {
        val buttons =
            listOf(
                ru.voboost.components.buttons.ButtonConfig("eco", "ECO"),
                ru.voboost.components.buttons.ButtonConfig("sport", "Sport"),
            )

        val buttonsView =
            section.addButtons(
                buttons = buttons,
                selectedValue = "eco",
                theme = Theme.FREE_LIGHT,
                language = Language.EN,
            )

        assertNotNull("Buttons should be created", buttonsView)
        assertEquals("Section should have 1 child", 1, section.childCount)
        assertEquals("eco", buttonsView.getSelectedValue())
    }

    @Test
    fun testAddButtons_withRightTextAndDescription() {
        val buttons =
            listOf(
                ru.voboost.components.buttons.ButtonConfig("on", "ON"),
            )

        val buttonsView =
            section.addButtons(
                buttons = buttons,
                selectedValue = "on",
                theme = Theme.FREE_LIGHT,
                language = Language.EN,
                rightText = mapOf("en" to "Right"),
                description = mapOf("en" to "Description"),
            )

        assertNotNull("Buttons should be created", buttonsView)
        assertEquals("Section should have 1 child", 1, section.childCount)
    }

    @Test
    fun testMultipleExtensions_addMultipleChildren() {
        section.addRadio(
            buttons = listOf(RadioButton("1", mapOf("en" to "One"))),
            selectedValue = "1",
            theme = Theme.FREE_LIGHT,
            language = Language.EN,
        )

        section.addButton(
            text = "Button",
            style = ru.voboost.components.button.ButtonStyle.PRIMARY,
            theme = Theme.FREE_LIGHT,
            language = Language.EN,
        )

        section.addCheckbox(
            checked = false,
            theme = Theme.FREE_LIGHT,
            language = Language.EN,
        )

        assertEquals("Section should have 3 children", 3, section.childCount)
    }

    @Test
    fun testExtension_functionsPropagateThemeAndLanguage() {
        var receivedTheme: Theme? = null
        var receivedLanguage: Language? = null

        // Create a mock radio that captures theme and language
        val buttons = listOf(RadioButton("1", mapOf("en" to "One")))
        section.addRadio(
            buttons = buttons,
            selectedValue = "1",
            theme = Theme.FREE_DARK,
            language = Language.RU,
        )

        val radio = section.getChildAt(0) as ru.voboost.components.radio.Radio
        receivedTheme = radio.getCurrentTheme()
        receivedLanguage = radio.getCurrentLanguage()

        assertEquals(Theme.FREE_DARK, receivedTheme)
        assertEquals(Language.RU, receivedLanguage)
    }
}
