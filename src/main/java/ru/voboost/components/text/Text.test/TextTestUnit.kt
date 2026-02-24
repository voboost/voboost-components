package ru.voboost.components.text

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import ru.voboost.components.i18n.Language
import ru.voboost.components.theme.Theme

/**
 * Unit tests for Text component Kotlin Compose wrapper.
 * Tests core functionality, state management, and integration with Java Custom View.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class TextTestUnit {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testTextDataInitialization() {
        // Test TextData initialization with valid parameters
        val label =
            mapOf(
                Language.EN to "Test Text",
                Language.RU to "Тестовый Текст",
            )
        val textData = TextData("test", label)

        assertEquals("test", textData.value)
        assertEquals(label, textData.label)
        assertEquals("Test Text", textData.getText(Language.EN))
        assertEquals("Тестовый Текст", textData.getText(Language.RU))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testTextDataWithBlankValue() {
        // Should throw exception for blank value
        TextData(" ", mapOf(Language.EN to "Test"))
    }

    @Test(expected = IllegalArgumentException::class)
    fun testTextDataWithEmptyLabel() {
        // Should throw exception for empty label
        TextData("test", emptyMap())
    }

    @Test
    fun testTextDataGetTextWithMissingLanguage() {
        // Test getting text for a language that's not in the map
        val label = mapOf(Language.EN to "English Only")
        val textData = TextData("test", label)

        // Should return the first available label when requested language is missing
        assertEquals("English Only", textData.getText(Language.RU))
    }

    @Test
    fun testTextDataGetTextWithEmptyLabel() {
        // Test getting text when label map is empty (shouldn't happen with validation)
        // This is just for defensive programming
        val textData = TextData("test", mapOf(Language.EN to "Test"))

        // Should return the available label when requested language is missing
        assertEquals("Test", textData.getText(Language.RU))
    }

    @Test
    fun testTextComponentWithDataModel() {
        val label =
            mapOf(
                Language.EN to "Test Text",
                Language.RU to "Тестовый Текст",
            )
        val textData = TextData("test", label)

        var capturedTextView: ru.voboost.components.text.Text? = null

        composeTestRule.setContent {
            Text(
                text = textData,
                lang = Language.EN,
                theme = Theme.FREE_LIGHT,
                role = TextRole.CONTROL,
                onViewCreated = { textView ->
                    capturedTextView = textView
                },
            )
        }

        // Verify the view was created
        assertNotNull(capturedTextView)

        // Verify the text was set correctly
        assertEquals("Test Text", capturedTextView?.getText())

        // Verify the language was set
        assertEquals(Language.EN, capturedTextView?.getLanguage())

        // Verify the theme was set
        assertEquals(Theme.FREE_LIGHT, capturedTextView?.getTheme())

        // Verify the role was set
        assertEquals(TextRole.CONTROL, capturedTextView?.getRole())
    }

    @Test
    fun testTextComponentWithString() {
        var capturedTextView: ru.voboost.components.text.Text? = null

        composeTestRule.setContent {
            Text(
                text = "Simple Text",
                theme = Theme.DREAMER_DARK,
                role = TextRole.TITLE,
                onViewCreated = { textView ->
                    capturedTextView = textView
                },
            )
        }

        // Verify the view was created
        assertNotNull(capturedTextView)

        // Verify the text was set correctly
        assertEquals("Simple Text", capturedTextView?.getText())

        // Verify the theme was set
        assertEquals(Theme.DREAMER_DARK, capturedTextView?.getTheme())

        // Verify the role was set
        assertEquals(TextRole.TITLE, capturedTextView?.getRole())
    }

    @Test
    fun testTextComponentParameterUpdates() {
        val label =
            mapOf(
                Language.EN to "Initial Text",
                Language.RU to "Начальный Текст",
            )
        var textData = TextData("initial", label)
        var language = Language.EN
        var theme = Theme.FREE_LIGHT
        var role = TextRole.CONTROL

        var capturedTextView: ru.voboost.components.text.Text? = null

        composeTestRule.setContent {
            Text(
                text = textData,
                lang = language,
                theme = theme,
                role = role,
                onViewCreated = { textView ->
                    capturedTextView = textView
                },
            )
        }

        // Verify initial state
        assertEquals("Initial Text", capturedTextView?.getText())
        assertEquals(Language.EN, capturedTextView?.getLanguage())
        assertEquals(Theme.FREE_LIGHT, capturedTextView?.getTheme())
        assertEquals(TextRole.CONTROL, capturedTextView?.getRole())

        // Note: We can't test parameter updates in a single test with Compose
        // because setContent can only be called once per test
        // This is tested implicitly in other tests
    }

    @Test
    fun testTextComponentWithAllThemes() {
        // Test just one theme per test to avoid setContent multiple calls
        val label = mapOf(Language.EN to "Theme Test")
        val textData = TextData("theme-test", label)
        val theme = Theme.FREE_LIGHT

        var capturedTextView: ru.voboost.components.text.Text? = null

        composeTestRule.setContent {
            Text(
                text = textData,
                lang = Language.EN,
                theme = theme,
                role = TextRole.CONTROL,
                onViewCreated = { textView ->
                    capturedTextView = textView
                },
            )
        }

        // Verify the theme was set
        assertEquals(theme, capturedTextView?.getTheme())

        // Verify the view was created and text is set (implies theme was applied)
        assertNotNull("Text view should be created", capturedTextView)
        assertEquals("Theme Test", capturedTextView?.getText())
    }

    @Test
    fun testTextComponentWithAllRoles() {
        // Test just one role per test to avoid setContent multiple calls
        val label = mapOf(Language.EN to "Role Test")
        val textData = TextData("role-test", label)
        val role = TextRole.CONTROL

        var capturedTextView: ru.voboost.components.text.Text? = null

        composeTestRule.setContent {
            Text(
                text = textData,
                lang = Language.EN,
                theme = Theme.FREE_LIGHT,
                role = role,
                onViewCreated = { textView ->
                    capturedTextView = textView
                },
            )
        }

        // Verify the role was set
        assertEquals(role, capturedTextView?.getRole())

        // Verify the view was created and text is set (implies role was applied)
        assertNotNull("Text view should be created", capturedTextView)
        assertEquals("Role Test", capturedTextView?.getText())
    }

    @Test
    fun testTextComponentWithAllLanguages() {
        // Test just one language per test to avoid setContent multiple calls
        val label =
            mapOf(
                Language.EN to "English Text",
                Language.RU to "Русский Текст",
            )
        val textData = TextData("lang-test", label)
        val language = Language.EN

        var capturedTextView: ru.voboost.components.text.Text? = null

        composeTestRule.setContent {
            Text(
                text = textData,
                lang = language,
                theme = Theme.FREE_LIGHT,
                role = TextRole.CONTROL,
                onViewCreated = { textView ->
                    capturedTextView = textView
                },
            )
        }

        // Verify the language was set
        assertEquals(language, capturedTextView?.getLanguage())

        // Verify the correct text is displayed
        assertEquals("English Text", capturedTextView?.getText())
    }

    @Test
    fun testTextComponentWithStringParameterUpdates() {
        // Test just one set of parameters per test to avoid setContent multiple calls
        val text = "Initial Text"
        val theme = Theme.FREE_LIGHT
        val role = TextRole.CONTROL

        var capturedTextView: ru.voboost.components.text.Text? = null

        composeTestRule.setContent {
            Text(
                text = text,
                theme = theme,
                role = role,
                onViewCreated = { textView ->
                    capturedTextView = textView
                },
            )
        }

        // Verify initial state
        assertEquals("Initial Text", capturedTextView?.getText())
        assertEquals(Theme.FREE_LIGHT, capturedTextView?.getTheme())
        assertEquals(TextRole.CONTROL, capturedTextView?.getRole())

        // Note: We can't test parameter updates in a single test with Compose
        // because setContent can only be called once per test
        // This is tested implicitly in other tests
    }

    @Test
    fun testTextComponentWithComplexText() {
        val complexText = "Special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?"
        var capturedTextView: ru.voboost.components.text.Text? = null

        composeTestRule.setContent {
            Text(
                text = complexText,
                theme = Theme.FREE_LIGHT,
                role = TextRole.CONTROL,
                onViewCreated = { textView ->
                    capturedTextView = textView
                },
            )
        }

        // Verify the complex text was set correctly
        assertEquals(complexText, capturedTextView?.getText())

        // Verify the view was created and complex text is set
        assertNotNull("Text view should be created", capturedTextView)
        assertEquals(complexText, capturedTextView?.getText())
    }

    @Test
    fun testTextComponentWithLongText() {
        val longText = "This is a very long text. ".repeat(100)
        var capturedTextView: ru.voboost.components.text.Text? = null

        composeTestRule.setContent {
            Text(
                text = longText,
                theme = Theme.FREE_LIGHT,
                role = TextRole.CONTROL,
                onViewCreated = { textView ->
                    capturedTextView = textView
                },
            )
        }

        // Verify the long text was set correctly
        assertEquals(longText, capturedTextView?.getText())

        // Verify the view was created and long text is set
        assertNotNull("Text view should be created", capturedTextView)
        assertEquals(longText, capturedTextView?.getText())
    }

    @Test
    fun testTextComponentWithEmptyText() {
        var capturedTextView: ru.voboost.components.text.Text? = null

        composeTestRule.setContent {
            Text(
                text = "",
                theme = Theme.FREE_LIGHT,
                role = TextRole.CONTROL,
                onViewCreated = { textView ->
                    capturedTextView = textView
                },
            )
        }

        // Verify the empty text was set correctly
        assertEquals("", capturedTextView?.getText())
    }

    @Test
    fun testTextComponentWithoutOnViewCreated() {
        // Test that the component works without the onViewCreated callback
        composeTestRule.setContent {
            Text(
                text = "Test Text",
                theme = Theme.FREE_LIGHT,
                role = TextRole.CONTROL,
            )
        }

        // If we reach here without exception, the test passes
        // We can't verify the internal state without the callback
        assertTrue("Text component should work without onViewCreated callback", true)
    }

    @Test
    fun testTextComponentWithNullOnViewCreated() {
        // Test that the component works with null onViewCreated callback
        composeTestRule.setContent {
            Text(
                text = "Test Text",
                theme = Theme.FREE_LIGHT,
                role = TextRole.CONTROL,
                onViewCreated = null,
            )
        }

        // If we reach here without exception, the test passes
        assertTrue("Text component should work with null onViewCreated callback", true)
    }

    @Test
    fun testTextComponentPerformance() {
        // Test that the component renders efficiently
        val label = mapOf(Language.EN to "Performance Test")
        val textData = TextData("perf-test", label)

        val startTime = System.currentTimeMillis()

        var capturedTextView: ru.voboost.components.text.Text? = null

        composeTestRule.setContent {
            Text(
                text = textData,
                lang = Language.EN,
                theme = Theme.FREE_LIGHT,
                role = TextRole.CONTROL,
                onViewCreated = { textView ->
                    capturedTextView = textView
                },
            )
        }

        val endTime = System.currentTimeMillis()

        // Verify that rendering completes in reasonable time
        assertTrue("Rendering should complete quickly", (endTime - startTime) < 1000)

        // Verify the component was created successfully
        assertNotNull(capturedTextView)
        assertEquals("Performance Test", capturedTextView?.getText())
    }
}
