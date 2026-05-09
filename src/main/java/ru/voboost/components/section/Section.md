# Section Component

## Architecture

- **[Section.java](Section.java)** — Java ViewGroup: titled container with gradient header
- **[Section.kt](Section.kt)** — Kotlin Compose wrapper

Titled container with rounded corners for grouping related elements (e.g. Radio inside Section inside Panel).

## Usage

### Java

```java
Section section = new Section(context);
section.setTheme(Theme.FREE_DARK);
section.setLanguage(Language.EN);
section.setTitle(Map.of("en", "Language Selection", "ru", "Выбор языка"));

// Add child views
section.addView(radioComponent);
```

### Kotlin

```kotlin
val section = Section(context).apply {
    setTheme(Theme.FREE_DARK)
    setLanguage(Language.EN)
    setTitle(mapOf("en" to "Language Selection", "ru" to "Выбор языка"))
    addView(radioComponent)
}
```

### Compose

```kotlin
Section(
    title = mapOf("en" to "Settings", "ru" to "Настройки"),
    lang = Language.EN,
    theme = Theme.FREE_LIGHT,
    content = { sectionView ->
        sectionView.addView(radioComponent)
    }
)
```

## Using Section in Compose

The Section component can be used in Jetpack Compose through the `Section` composable function or through extension functions.

### Option 1: Using the Section Composable Wrapper

```kotlin
import ru.voboost.components.section.Section
import ru.voboost.components.i18n.Language
import ru.voboost.components.theme.Theme

@Composable
fun MyScreen() {
    val theme = Theme.FREE_LIGHT
    val language = Language.EN

    Section(
        title = mapOf("en" to "Settings", "ru" to "Настройки"),
        lang = language,
        theme = theme
    ) { section ->
        // Use extension functions to add children
        section.addRadio(
            buttons = listOf(
                RadioButton("en", mapOf("en" to "English")),
                RadioButton("ru", mapOf("en" to "Russian"))
            ),
            selectedValue = "en",
            theme = theme,
            language = language,
            onValueChange = { newValue -> /* handle change */ }
        )
    }
}
```

### Option 2: Using Panel.addSection() Extension (Recommended)

```kotlin
import ru.voboost.components.panel.Panel
import ru.voboost.components.panel.addSection
import ru.voboost.components.section.addRadio

@Composable
fun MyPanel() {
    val theme = Theme.FREE_LIGHT
    val language = Language.EN

    val panel = Panel(context).apply {
        setTheme(theme)
        setLanguage(language)
    }

    panel.addSection(
        title = mapOf("en" to "Settings"),
        theme = theme,
        language = language
    ) { section ->
        section.addRadio(
            buttons = listOf(
                RadioButton("en", mapOf("en" to "English")),
                RadioButton("ru", mapOf("en" to "Russian"))
            ),
            selectedValue = "en",
            theme = theme,
            language = language
        )
    }
}
```

### Option 3: Direct Java View Creation

For migration or special cases, you can create the Java View directly:

```kotlin
val section = ru.voboost.components.section.Section(context).apply {
    setTheme(Theme.FREE_LIGHT)
    setLanguage(Language.EN)
    setTitle(mapOf("en" to "Settings"))
}

// Add children using Java API
val radio = ru.voboost.components.radio.Radio(context).apply {
    setTheme(Theme.FREE_LIGHT)
    setLanguage(Language.EN)
    setButtons(listOf(...))
}
section.addView(radio)
```

### Integration with Panel

When using Section with Panel components, the Panel provides built-in ScrollView:

```kotlin
// Panel automatically handles scrolling
val panel = Panel(context).apply {
    setTheme(theme)
    setLanguage(language)
}

// All sections added to panel will be scrollable
panel.addSection(title = "Section 1", theme = theme, language = language) { ... }
panel.addSection(title = "Section 2", theme = theme, language = language) { ... }
panel.addSection(title = "Section 3", theme = theme, language = language) { ... }
```

### Extension Functions Reference

Section provides extension functions for common child components:

- `addRadio()` - Add a Radio component
- `addButton()` - Add a Button component
- `addCheckbox()` - Add a Checkbox component
- `addSelect()` - Add a Select component
- `addButtons()` - Add a Buttons component

Example:

```kotlin
Section(
    title = mapOf("en" to "My Section"),
    lang = Language.EN,
    theme = Theme.FREE_LIGHT
) { section ->
    // Add Radio
    section.addRadio(
        buttons = listOf(...),
        selectedValue = "option1",
        theme = Theme.FREE_LIGHT,
        language = Language.EN
    )

    // Add Button
    section.addButton(
        text = "Click Me",
        style = ButtonStyle.PRIMARY,
        theme = Theme.FREE_LIGHT,
        language = Language.EN
    ) { /* handle click */ }

    // Add Checkbox
    section.addCheckbox(
        checked = true,
        theme = Theme.FREE_LIGHT,
        language = Language.EN,
        label = mapOf("en" to "Enable")
    )
}
```

### Theme and Language Propagation

Section automatically propagates theme and language to child components:

```kotlin
Section(
    title = mapOf("en" to "Settings"),
    lang = Language.EN,
    theme = Theme.FREE_LIGHT
) { section ->
    // Radio will automatically receive theme and language from Section
    // but you can also specify explicitly:
    section.addRadio(
        buttons = listOf(...),
        selectedValue = "en",
        theme = Theme.FREE_LIGHT,
        language = Language.EN
    )
}
```

### Title Checkbox (Collapsible Section)

When enabled, the title bar shows a `Checkbox` whose label is the section title. When unchecked, the section collapses to the title bar only.

```java
section.setTitle(Map.of("en", "Schedule charging"));
section.setTitleCheckbox(false, isChecked -> {
    // collapsed when isChecked == false
});
```

```kotlin
Section(
    title = mapOf("en" to "Schedule charging"),
    lang = Language.EN,
    theme = Theme.FREE_DARK,
    titleChecked = false,
    onTitleCheckedChange = { /* handle toggle */ }
) { /* children — hidden while titleChecked == false */ }
```

### Info Icon and Popup

Set `infoText` (single block) or `infoBlocks` (multiple blocks with titles) to show a circular `(i)` icon after the title. Tapping the icon opens a popup.

```java
// Single text block (no title)
section.setPopupText(Map.of("en", "Generally, the range extender is not started..."));

// Multiple blocks with titles
section.setPopupText(List.of(
    new PopupBlock(
        Map.of("en", "Pure EV"),
        Map.of("en", "Description of pure EV mode...")
    ),
    new PopupBlock(
        Map.of("en", "HEV"),
        Map.of("en", "Description of HEV mode...")
    )
));
```

```kotlin
// Single block
Section(
    title = mapOf("en" to "Power mode"),
    lang = Language.EN,
    theme = Theme.FREE_DARK,
    infoText = mapOf("en" to "Generally, the range extender..."),
)

// Multiple blocks
Section(
    title = mapOf("en" to "Power mode"),
    lang = Language.EN,
    theme = Theme.FREE_DARK,
    infoBlocks = listOf(
        SectionPopupBlock(
            title = mapOf("en" to "Pure EV"),
            text = mapOf("en" to "Description...")
        ),
        SectionPopupBlock(
            title = mapOf("en" to "HEV"),
            text = mapOf("en" to "Description...")
        )
    ),
)
```

## API

### Java

```java
// Title
void setTitle(Map<String, String> title)
Map<String, String> getTitle()
String getTitleText()                   // current language

// Title checkbox (collapsible)
void setTitleCheckbox(boolean checked)
void setTitleCheckbox(boolean checked, OnTitleCheckedChangeListener listener)
void clearTitleCheckbox()
boolean hasTitleCheckbox()
boolean isTitleChecked()
boolean isCollapsed()
void setOnTitleCheckedChangeListener(OnTitleCheckedChangeListener listener)

// Info popup (content blocks)
static class PopupBlock {
    PopupBlock(@Nullable Map<String, String> title, Map<String, String> text)
    @Nullable Map<String, String> getTitle()
    Map<String, String> getText()
}
void setPopupText(@Nullable List<PopupBlock> blocks)
void setPopupText(@Nullable Map<String, String> singleText)  // convenience
@Nullable List<PopupBlock> getPopupBlocks()
boolean hasPopupText()
void clearPopupText()
void showPopup()

// Theme and language
void setTheme(Theme theme)
Theme getCurrentTheme()
void setLanguage(Language language)
Language getCurrentLanguage()

// Propagation (to child components)
void propagateTheme(Theme theme)
void propagateLanguage(Language language)
```

### Compose Wrapper

```kotlin
data class SectionPopupBlock(
    val title: Map<String, String>? = null,
    val text: Map<String, String>
)

@Composable
fun Section(
    title: Map<String, String>,
    lang: Language,
    theme: Theme,
    titleChecked: Boolean? = null,
    onTitleCheckedChange: ((Boolean) -> Unit)? = null,
    infoText: Map<String, String>? = null,              // single block without title
    infoBlocks: List<SectionPopupBlock>? = null,        // multiple blocks
    content: ((Section) -> Unit)? = null
)
```

## Implementation Details

Canvas-based rendering: gradient title bar with top-only rounded corners, border, background. No state persistence (state is managed externally). Propagates theme and language to children.

## File Structure

```
section/
├── Section.java         # Core implementation
├── Section.kt           # Compose wrapper
├── SectionTheme.java    # Theme colors
├── Section.md           # This doc
└── Section.test/        # Tests
```
