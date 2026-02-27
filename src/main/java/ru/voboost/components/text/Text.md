# Text Component

## Architecture

- **[Text.java](Text.java)** — Extended `AppCompatTextView`: native text rendering, robust sizing, and theming integration
- **[Text.kt](Text.kt)** — Kotlin Compose wrapper + `TextData` model

Java handles all logic. Kotlin wrapper bridges to Compose.

## Usage

### Java

```java
// Static text
Text text = new Text(context);
text.setText("Hello World");
text.setTheme(Theme.FREE_LIGHT);
text.setRole(TextRole.CONTROL);
parentLayout.addView(text);

// Localized text
Text localized = new Text(context);
localized.setText(Map.of(Language.EN, "Settings", Language.RU, "Настройки"));
localized.setLanguage(Language.EN);
localized.setTheme(Theme.FREE_LIGHT);
localized.setRole(TextRole.TITLE);
```

### Kotlin

```kotlin
val text = Text(context).apply {
    setText(mapOf(Language.EN to "Settings", Language.RU to "Настройки"))
    setLanguage(Language.EN)
    setTheme(Theme.FREE_LIGHT)
    setRole(TextRole.TITLE)
}
parentLayout.addView(text)
```

### Compose

```kotlin
// Simple string
Text(
    text = "Hello World",
    theme = Theme.FREE_LIGHT,
    role = TextRole.CONTROL
)

// Localized
Text(
    text = TextData("settings", mapOf(Language.EN to "Settings", Language.RU to "Настройки")),
    lang = Language.EN,
    theme = Theme.FREE_LIGHT,
    role = TextRole.TITLE
)
```

## API

### Java Custom View

```java
// Text content
void setText(String text)
void setText(Map<Language, String> localizedText)
String getText()

// Theme and role
void setTheme(Theme theme)
Theme getTheme()
void setRole(TextRole role)          // TextRole.CONTROL, TextRole.TITLE
TextRole getRole()

// Localization
void setLanguage(Language language)
Language getLanguage()
```

### Compose Wrapper

```kotlin
// Localized version
@Composable
fun Text(
    text: TextData,
    lang: Language,
    theme: Theme,
    role: TextRole = TextRole.CONTROL,
    onViewCreated: ((Text) -> Unit)? = null
)

// Simple string version
@Composable
fun Text(
    text: String,
    theme: Theme,
    role: TextRole = TextRole.CONTROL,
    onViewCreated: ((Text) -> Unit)? = null
)
```

### TextData (Kotlin)

```kotlin
data class TextData(
    val value: String,
    val label: Map<Language, String>
) {
    fun getText(lang: Language): String
}
```

### TextRole

```java
enum TextRole {
    CONTROL,  // UI controls — determines size, weight, and color
    TITLE;    // Section headers — determines size, weight, and color

    int getSizePx()
    int getWeight()
}
```

Role determines both visual appearance (size + weight) and color. Colors are defined per role × theme in [TextTheme.java](TextTheme.java).

## Themes

| Enum                  | Style                  |
|-----------------------|------------------------|
| `Theme.FREE_LIGHT`    | Dark text on light bg  |
| `Theme.FREE_DARK`     | Light text on dark bg  |
| `Theme.DREAMER_LIGHT` | Dark text on light bg  |
| `Theme.DREAMER_DARK`  | Light text on dark bg  |

## Rendering

Native Android text rendering via `AppCompatTextView`. Avoids custom Canvas clipping bugs. Fonts are loaded and applied natively via `Font.getRegular(context)` and `Font.getBold(context, text)` depending on the role.

## File Structure

```
text/
├── Text.java              # Core implementation
├── Text.kt                # Compose wrapper + TextData
├── TextRole.java          # CONTROL / TITLE enum
├── TextTheme.java         # Theme colors per role
├── Text.md                # This doc
└── Text.test/             # Tests
```

## Testing

```bash
./gradlew test --tests "*TextTest*"
```
