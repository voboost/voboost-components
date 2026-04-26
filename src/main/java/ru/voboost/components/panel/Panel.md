# Panel Component

## Architecture

- **[Panel.java](Panel.java)** — Java ViewGroup: invisible container for Section components
- **[Panel.kt](Panel.kt)** — Kotlin Compose wrapper

Invisible container for grouping Section components with support for panel transition animations.

## Purpose

Panel is a **transparent container** designed to:
- Group one or more `Section` components
- Enable smooth panel transitions in `Screen` component (via `Screen.setActivePanel()`)
- Provide automatic scrolling when content exceeds panel dimensions

**Important:** Panel has no visual styling (all styles are 0 or TRANSPARENT) because all visual presentation is handled by child `Section` components.

## Usage

### Java

```java
// Simple case (single Section)
Panel panel = new Panel(context);
panel.setTheme(Theme.FREE_LIGHT);

Section section = new Section(context);
section.setTitle(DemoContent.getSectionTitle("language"));
section.addView(radio);
panel.addView(section);  // Automatically in ScrollView

// Complex case (multiple Sections with scrolling)
Panel panel = new Panel(context);
for (int i = 0; i < sectionCount; i++) {
    Section section = new Section(context);
    section.setTitle(DemoContent.getClimateSectionTitle(i));
    section.addView(radio);
    panel.addView(section);  // All sections in built-in ScrollView
}
// DO NOT create your own ScrollView — Panel has one!
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
Panel(theme = Theme.FREE_LIGHT, lang = Language.EN)
```

## API

### Java

```java
// Theme
void setTheme(Theme theme)
Theme getCurrentTheme()

// Language
void setLanguage(Language language)
Language getCurrentLanguage()

// Propagation (to child components)
void propagateTheme(Theme theme)
void propagateLanguage(Language language)
```

### Compose Wrapper

```kotlin
@Composable
fun Panel(theme: Theme, lang: Language = Language.EN)
```

## Implementation Details

### Container Architecture

Panel extends `FrameLayout` and contains:
- Built-in `ScrollView` for automatic vertical scrolling
- Internal `LinearLayout` (contentLayout) as the ScrollView's only child
- All child views added via `addView()` are automatically placed in contentLayout
- Panel has no visual styling (all TRANSPARENT by design)

### Usage Patterns

**Simple case** (single Section):
```java
Panel panel = new Panel(context);
panel.setTheme(Theme.FREE_LIGHT);
Section section = new Section(context);
section.setTitle(title);
section.addView(radio);
panel.addView(section);  // Automatically in ScrollView
```

**Complex case** (multiple Sections with scrolling):
```java
Panel panel = new Panel(context);
for (int i = 0; i < sectionCount; i++) {
    Section section = new Section(context);
    section.setTitle(title);
    section.addView(radio);
    panel.addView(section);  // All sections in built-in ScrollView
}
// DO NOT create your own ScrollView — Panel has one!
```

### Important Notes

- **NEVER create your own ScrollView** when using Panel — Panel has a built-in ScrollView
- Add child views (Section, etc.) directly to Panel via `addView()`
- Panel automatically handles scrolling when content exceeds available space
- Panel is transparent — all visual styling is in child Section components

### Panel Transitions

Panel is designed to work with `Screen.setActivePanel()` for animated transitions:
- Transition duration: 300ms (defined in `ScreenTheme.PANEL_TRANSITION_DURATION`)
- Animation uses `translationY` for smooth slide up/down effects
- Old panel slides out while new panel slides in simultaneously

### Theme and Language Propagation

Panel automatically propagates theme and language to all child views that implement `IThemable` and `ILocalizable` interfaces (Section, Radio, Button, etc.).

### Visual Styling

Panel intentionally has no visual styling:
- All theme values are 0 or TRANSPARENT (this is correct!)
- All visual presentation is handled by child `Section` components
- This allows Section components to maintain their own styling without interference

## File Structure

```
panel/
├── Panel.java           # Core implementation
├── Panel.kt             # Compose wrapper
├── PanelTheme.java      # Theme colors
├── Panel.md             # This doc
└── Panel.test/          # Tests
```


### Panel Transitions

Panel is designed to work with `Screen.setActivePanel()` for animated transitions:
- Transition duration: 300ms (defined in `ScreenTheme.PANEL_TRANSITION_DURATION`)
- Animation uses `translationY` for smooth slide up/down effects
- Old panel slides out while new panel slides in simultaneously

### Theme and Language Propagation

Panel automatically propagates theme and language to all child views that implement `IThemable` and `ILocalizable` interfaces (Section, Radio, Button, etc.).

### Visual Styling

Panel intentionally has no visual styling:
- All theme values are 0 or TRANSPARENT (this is correct!)
- All visual presentation is handled by child `Section` components
- This allows Section components to maintain their own styling without interference

## File Structure

```
panel/
├── Panel.java           # Core implementation
├── Panel.kt             # Compose wrapper
├── PanelTheme.java      # Theme colors
├── Panel.md             # This doc
└── Panel.test/          # Tests
```
