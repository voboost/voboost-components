# Buttons Component

## Architecture

- **[Buttons.java](Buttons.java)** — Java Custom View: canvas rendering, touch, state
- **[Buttons.kt](Buttons.kt)** — Kotlin Compose wrapper: lightweight AndroidView integration

Java handles all logic. Kotlin wrapper only bridges to Compose.

## Usage

### Java

```java
List<ButtonConfig> configs = Arrays.asList(
    new ButtonConfig("value1", "Option 1"),
    new ButtonConfig("value2", "Option 2")
);

Buttons buttons = new Buttons(context);
buttons.setButtons(configs);
buttons.setTheme(Theme.FREE_DARK);
buttons.setSelectedValue("value1");
buttons.setOnValueChangeListener(newValue -> {
    // Handle selection change
});
parentLayout.addView(buttons);
```

### Kotlin

```kotlin
val configs = listOf(
    ButtonConfig("value1", "Option 1"),
    ButtonConfig("value2", "Option 2")
)

val buttons = Buttons(context).apply {
    setButtons(configs)
    setTheme(Theme.FREE_DARK)
    setSelectedValue("value1")
    setOnValueChangeListener { newValue ->
        // Handle selection change
    }
}
parentLayout.addView(buttons)
```

### Compose

```kotlin
val configs = listOf(
    ButtonConfig("value1", "Option 1"),
    ButtonConfig("value2", "Option 2")
)

Buttons(
    buttons = configs,
    theme = Theme.FREE_DARK,
    selectedValue = "value1",
    onValueChange = { newValue ->
        // Handle selection change
    }
)
```

## API

### Java Custom View

```java
// Configuration
void setButtons(List<ButtonConfig> buttons)
void setSelectedValue(String value)
String getSelectedValue()

// Optional text
void setRightText(Map<String, String> text)  // Localized text to the right
void setDescription(Map<String, String> text)  // Localized description below

// Theme & Language
void setTheme(Theme theme)
Theme getCurrentTheme()
void setLanguage(Language language)
Language getCurrentLanguage()

// Events
void setOnValueChangeListener(OnValueChangeListener listener)

interface OnValueChangeListener {
    void onValueChange(String newValue);
}
```

### Compose Wrapper

```kotlin
@Composable
fun Buttons(
    buttons: List<ButtonConfig>,
    theme: Theme,
    selectedValue: String? = null,
    onValueChange: ((String) -> Unit)? = null,
    rightText: Map<String, String>? = null,
    description: Map<String, String>? = null,
    lang: Language? = null,
    onViewCreated: ((Buttons) -> Unit)? = null
)
```

### ButtonConfig

```java
public class ButtonConfig {
    @NonNull
    private final String value;  // Unique identifier
    @NonNull
    private final String text;  // Display text
    @NonNull
    private final ButtonStyle style;  // ButtonStyle.PRIMARY or ButtonStyle.SECONDARY

    public ButtonConfig(@NonNull String value, @NonNull String text, @NonNull ButtonStyle style)
    public ButtonConfig(@NonNull String value, @NonNull String text)  // Defaults to SECONDARY
}
```

## Layout

```
┌───────┐ ┌─────────┐  Right text (optional)
│ Btn 1 │ │ Btn 2   │
└───────┘ └─────────┘
Description text below (optional, marginTop 14px)
```

## Styles

| Style | Description |
|-------|-------------|
| `ButtonStyle.PRIMARY` | Accent-colored (blue/brown), white text |
| `ButtonStyle.SECONDARY` | Neutral background (gray), theme text |

## Themes

| Enum | Primary BG | Secondary BG |
|------|-----------|-------------|
| `Theme.FREE_LIGHT` | Blue `#4099f3` | Light gray `#e8ecf0` |
| `Theme.FREE_DARK` | Blue `#4099f3` | Dark gray `#373f4a` |
| `Theme.DREAMER_LIGHT` | Brown `#9c8069` | Dark gray `#40444a` |
| `Theme.DREAMER_DARK` | Brown `#9c8069` | Dark gray `#40444a` |

Colors defined in [ButtonsTheme.java](ButtonsTheme.java).

## Selection

- The selected button gets `ButtonStyle.PRIMARY` style
- Other buttons get `ButtonStyle.SECONDARY` style
- Selection is managed via `selectedValue` property

## Dimensions

All sizes in **pixels** (automotive requirement). Constants in `ButtonsDimensions`.

| Property | Value |
|----------|-------|
| Button height | 70px |
| Button corner radius | 35px (pill shape) |
| Button text size | 28px |
| Button horizontal padding | 40px |
| Button min width | 200px |
| Button gap | 20px |
| Buttons to text gap | 20px |
| Buttons to description gap | 14px |
| Text size | 28px |

## Performance

The `rebuildButtons()` method optimizes performance by reusing existing Button instances:
- When updating with the same number of buttons, existing views are reused
- When increasing the number of buttons, new views are created only for the additional buttons
- When decreasing the number of buttons, excess views are removed

This optimization reduces view creation overhead and improves performance when updating button lists.

## File Structure

```
buttons/
├── Buttons.java             # Core implementation
├── Buttons.kt               # Compose wrapper
├── ButtonConfig.java         # Button configuration
├── ButtonsTheme.java         # Colors, dimensions, schemes
├── Buttons.md               # This doc
├── Buttons.test/
│   ├── ButtonsTestUnit.java  # Unit tests
│   └── ButtonsTestVisual.java # Visual regression tests
└── Buttons.screenshots/     # Reference images
```
