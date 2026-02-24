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
        theme = Theme.FREE_LIGHT
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
