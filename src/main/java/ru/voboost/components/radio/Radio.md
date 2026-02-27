# Radio Component

## Architecture

- **[Radio.java](Radio.java)** — Java Custom View: canvas rendering, animations, touch, state
- **[Radio.kt](Radio.kt)** — Kotlin Compose wrapper: lightweight AndroidView integration

Java handles all logic. Kotlin wrapper only bridges to Compose.

## Usage

### Java

```java
Radio radio = new Radio(context);

radio.setButtons(Arrays.asList(
    new RadioButton("en", Map.of("en", "English", "ru", "Английский")),
    new RadioButton("ru", Map.of("en", "Russian", "ru", "Русский"))
));
radio.setLanguage(Language.EN);
radio.setTheme(Theme.FREE_LIGHT);
radio.setSelectedValue("en");
radio.setOnValueChangeListener(newValue -> {
    // Handle selection change
});

parentLayout.addView(radio);
```

### Kotlin

```kotlin
val radio = Radio(context).apply {
    setButtons(listOf(
        RadioButton("en", mapOf("en" to "English", "ru" to "Английский")),
        RadioButton("ru", mapOf("en" to "Russian", "ru" to "Русский"))
    ))
    setLanguage(Language.EN)
    setTheme(Theme.FREE_LIGHT)
    setSelectedValue("en")
    setOnValueChangeListener { newValue ->
        // Handle selection change
    }
}

parentLayout.addView(radio)
```

### Compose

```kotlin
var selectedValue by remember { mutableStateOf("en") }

Radio(
    buttons = listOf(
        RadioButton("en", mapOf("en" to "English", "ru" to "Английский")),
        RadioButton("ru", mapOf("en" to "Russian", "ru" to "Русский"))
    ),
    lang = Language.EN,
    theme = Theme.FREE_LIGHT,
    value = selectedValue,
    onValueChange = { selectedValue = it }
)
```

## API

### Java Custom View

```java
// Configuration
void setButtons(List<RadioButton> buttons)
void setLanguage(Language language)    // Language.EN, Language.RU
void setTheme(Theme theme)            // Theme.FREE_LIGHT, Theme.FREE_DARK, etc.

// Value
void setSelectedValue(String value)
void setSelectedValue(String value, boolean isTriggerCallback)
String getSelectedValue()

// State
Theme getCurrentTheme()
Language getCurrentLanguage()
boolean isInitialized()               // true when both theme and language are set

// Events
void setOnValueChangeListener(OnValueChangeListener listener)

interface OnValueChangeListener {
    void onValueChange(String newValue);
}
```

### Compose Wrapper

```kotlin
@Composable
fun Radio(
    buttons: List<RadioButton>,
    lang: Language,
    theme: Theme,
    value: String,
    onValueChange: (String) -> Unit,
    onViewCreated: ((Radio) -> Unit)? = null  // for testing
)
```

### RadioButton

```java
RadioButton(String value, Map<String, String> label)

String getText(String lang)       // localized text with fallback
String getValue()
Map<String, String> getLabel()
RadioButton copy(String value, Map<String, String> label)
```

## Themes

| Enum | Style |
|------|-------|
| `Theme.FREE_LIGHT` | Light + blue gradient |
| `Theme.FREE_DARK` | Dark + blue gradient |
| `Theme.DREAMER_LIGHT` | Light + brown gradient |
| `Theme.DREAMER_DARK` | Dark + brown gradient |

Theme colors defined in [RadioTheme.java](RadioTheme.java): `RadioColors`, `RadioColorSchemes`, `RadioDimensions`.

## Localization

External via `Map<String, String>` — no shared i18n system:

```java
new RadioButton("value", Map.of("en", "English", "ru", "Русский"));
```

Language set via `setLanguage(Language.EN)` / `lang = Language.EN`. Fallback: first available label.

## Implementation Details

### Rendering

Canvas-based with 3 layers:
1. **Background** — rounded rect with theme color
2. **Selection** — animated gradient fill with border (gradient direction varies by theme)
3. **Text** — centered, color changes based on selection coverage

### Animation

`ValueAnimator` + `OvershootInterpolator`. Animates X position and width of selection background. Hardware acceleration enabled.

### Touch

`onTouchEvent` → calculate touched item by X → animate to new position → trigger callback.

### Dimensions

All sizes in **pixels** (automotive requirement). Constants in `RadioDimensions`.

## File Structure

```
radio/
├── Radio.java              # Core implementation
├── Radio.kt                # Compose wrapper
├── RadioButton.java        # Data model
├── RadioTheme.java         # Colors, dimensions, schemes
├── Radio.md                # This doc
├── Radio.test/
│   ├── RadioTestUnit.java  # Unit tests
│   └── RadioTestVisual.java # Visual regression tests
└── Radio.screenshots/      # Reference images
```

## Testing

- **Unit**: `Radio.test/RadioTestUnit.java` — initialization, state, callbacks, edge cases, touch, accessibility
- **Visual**: `Radio.test/RadioTestVisual.java` — all theme/language combinations, animation frames
- **Screenshots**: `Radio.screenshots/` — reference images for visual regression

```bash
./gradlew test --tests "*RadioTest*"   # All Radio tests
./gradlew record                       # Record new screenshots
```

## Demo Applications

4 demos in [`src/demo-*/`](../../demo-java/):
- **demo-java** — Pure Java
- **demo-kotlin** — Kotlin + Java Custom View
- **demo-compose** — Jetpack Compose
- **demo-pixel** — Pixel-perfect visual testing
