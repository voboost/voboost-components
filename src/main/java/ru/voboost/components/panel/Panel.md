# Panel Component

## Architecture

- **[Panel.java](Panel.java)** — Java ViewGroup: rounded container with shadow and border
- **[Panel.kt](Panel.kt)** — Kotlin Compose wrapper

Styled container for grouping child views with visual hierarchy.

## Usage

### Java

```java
Panel panel = new Panel(context);
panel.setTheme(Theme.FREE_LIGHT);

// Add child views
Section section = new Section(context);
panel.addView(section);
```

### Kotlin

```kotlin
val panel = Panel(context).apply {
    setTheme(Theme.FREE_LIGHT)
    addView(section)
}
```

### Compose

```kotlin
Panel(theme = Theme.FREE_LIGHT)
```

## API

### Java

```java
// Theme
void setTheme(Theme theme)
Theme getCurrentTheme()

// Propagation (to child components)
void propagateTheme(Theme theme)
void propagateLanguage(Language language)
```

### Compose Wrapper

```kotlin
@Composable
fun Panel(theme: Theme)
```

## Implementation Details

Uses native Android features: `ViewOutlineProvider` and `elevation` for rendering shadows and clipping edges. Provides an internal `ScrollView` to automatically manage vertical overflow. Automatically propagates theme and language to child views that support them (Section, Radio, etc.).

## File Structure

```
panel/
├── Panel.java           # Core implementation
├── Panel.kt             # Compose wrapper
├── PanelTheme.java      # Theme colors
├── Panel.md             # This doc
└── Panel.test/          # Tests
```
