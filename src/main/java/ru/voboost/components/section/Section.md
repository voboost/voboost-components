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

## API

### Java

```java
// Title
void setTitle(Map<String, String> title)
Map<String, String> getTitle()
String getTitleText()                   // current language

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
@Composable
fun Section(
    title: Map<String, String>,
    lang: Language,
    theme: Theme,
    content: ((Section) -> Unit)? = null
)
```

## Implementation Details

Canvas-based rendering: gradient title bar with top-only rounded corners, border, background. State persistence via `onSaveInstanceState`/`onRestoreInstanceState`. Propagates theme and language to children.

## File Structure

```
section/
├── Section.java         # Core implementation
├── Section.kt           # Compose wrapper
├── SectionTheme.java    # Theme colors
├── Section.md           # This doc
└── Section.test/        # Tests
```
