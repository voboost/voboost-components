# Section Component

A titled container component with rounded corners and border for automotive applications.

## Overview

The Section component provides a styled container with a localized title, rounded corners, and border. It's designed to group related UI elements and provide visual hierarchy with clear section headers in automotive interfaces.

## Features

- Localized title text with multi-language support
- Rounded corners with customizable radius
- Border with configurable width
- Multi-theme support (Free/Dreamer, Light/Dark)
- Hardware-accelerated rendering
- Child view support

## Usage

### Java

```java
// Create section
Section section = new Section(context);

// Configure theme and language
section.setTheme(Theme.FREE_LIGHT);
section.setLanguage(Language.EN);

// Set localized title
Map<String, String> title = new HashMap<>();
title.put("en", "Settings");
title.put("ru", "Настройки");
section.setTitle(title);

// Add child views
TextView textView = new TextView(context);
textView.setText("Content inside section");
section.addView(textView);
```

### Kotlin (Compose)

```kotlin
@Composable
fun MyScreen() {
    Section(
        title = mapOf("en" to "Settings", "ru" to "Настройки"),
        lang = "en",
        theme = "free-light"
    ) {
        Text("Content inside section")
        Button(onClick = { /* Handle click */ }) {
            Text("Button")
        }
    }
}
```

## API Reference

### Section (Java)

| Method | Description |
|--------|-------------|
| setTitle(Map<String, String>) | Sets the localized title |
| getTitle() | Returns the Map of localized titles |
| getTitleText() | Returns the title text for current language |
| setTheme(Theme) | Sets the visual theme |
| getCurrentTheme() | Returns the current theme |
| setLanguage(Language) | Sets the display language |
| getCurrentLanguage() | Returns the current language |

### Section (Compose)

| Parameter | Type | Description |
|-----------|------|-------------|
| title | Map<String, String> | Localized title text |
| lang | String | Language code ("en", "ru") |
| theme | String | Theme identifier |
| content | @Composable () -> Unit | Content to display inside the section |

## Themes

| Theme | Description |
|-------|-------------|
| free-light | Free car model, light theme |
| free-dark | Free car model, dark theme |
| dreamer-light | Dreamer car model, light theme |
| dreamer-dark | Dreamer car model, dark theme |

## Dimensions

All dimensions are in pixels (automotive requirement):

| Dimension | Value | Description |
|-----------|-------|-------------|
| CORNER_RADIUS | 16px | Corner radius for the section |
| PADDING | 20px | Default padding inside the section |
| TITLE_SPACING | 16px | Spacing between title and content |
| TITLE_TEXT_SIZE | 32px | Title text size |
| BORDER_WIDTH | 1px | Border width |

## Colors

### Free Light Theme
- Background: #f8f9fa (very light gray)
- Border: #e9ecef (light gray)
- Title Text: #212529 (dark gray)

### Free Dark Theme
- Background: #343a40 (dark gray)
- Border: #495057 (medium gray)
- Title Text: #f8f9fa (very light gray)

### Dreamer Light Theme
- Background: #fafbfc (almost white)
- Border: #e1e4e8 (light gray)
- Title Text: #24292e (dark gray)

### Dreamer Dark Theme
- Background: #2d3748 (dark gray)
- Border: #4a5568 (medium gray)
- Title Text: #f7fafc (very light gray)
