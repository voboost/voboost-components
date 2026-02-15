# Panel Component

A customizable container component with rounded corners and shadow for automotive applications.

## Overview

The Panel component provides a styled container with rounded corners, shadow effect, and border. It's designed to group related UI elements and provide visual hierarchy in automotive interfaces.

## Features

- Rounded corners with customizable radius
- Shadow effect for depth
- Border with configurable width
- Multi-theme support (Free/Dreamer, Light/Dark)
- Hardware-accelerated rendering
- Child view support

## Usage

### Java

```java
// Create panel
Panel panel = new Panel(context);

// Configure theme
panel.setTheme(Theme.FREE_LIGHT);

// Add child views
TextView textView = new TextView(context);
textView.setText("Content inside panel");
panel.addView(textView);
```

### Kotlin (Compose)

```kotlin
@Composable
fun MyScreen() {
    Panel(
        theme = "free-light"
    ) {
        Text("Content inside panel")
        Button(onClick = { /* Handle click */ }) {
            Text("Button")
        }
    }
}
```

## API Reference

### Panel (Java)

| Method | Description |
|--------|-------------|
| setTheme(Theme) | Sets the visual theme |
| getCurrentTheme() | Returns the current theme |

### Panel (Compose)

| Parameter | Type | Description |
|-----------|------|-------------|
| theme | String | Theme identifier |
| content | @Composable () -> Unit | Content to display inside the panel |

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
| CORNER_RADIUS | 24px | Corner radius for the panel |
| PADDING | 24px | Default padding inside the panel |
| ELEVATION | 8px | Shadow elevation |
| BORDER_WIDTH | 1px | Border width |

## Colors

### Free Light Theme
- Background: #ffffff (white)
- Border: #e0e0e0 (light gray)
- Shadow: #1a000000 (semi-transparent black)

### Free Dark Theme
- Background: #2a2a2a (dark gray)
- Border: #404040 (medium gray)
- Shadow: #1a000000 (semi-transparent black)

### Dreamer Light Theme
- Background: #ffffff (white)
- Border: #d0d0d0 (light gray)
- Shadow: #1a000000 (semi-transparent black)

### Dreamer Dark Theme
- Background: #1a1a1a (very dark gray)
- Border: #333333 (dark gray)
- Shadow: #1a000000 (semi-transparent black)
