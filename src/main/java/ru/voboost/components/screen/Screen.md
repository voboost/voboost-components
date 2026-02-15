# Screen Component

A full-screen container component for automotive applications.

## Overview

The Screen component provides a full-screen container for automotive interfaces. It's designed as the root container that manages layout for Tabs and Panel components, providing a consistent structure for all content.

## Features

- Full-screen container with layout management
- Multi-theme support (Free/Dreamer, Light/Dark)
- Tabs and Panel component integration
- Screen lift functionality for automotive use

## Usage

### Java

```java
// Create screen
Screen screen = new Screen(context);

// Configure theme
screen.setTheme(Theme.FREE_LIGHT);

// Add Tabs and Panel components
Tabs tabs = new Tabs(context);
Panel panel = new Panel(context);
screen.setTabs(tabs);
screen.setPanel(panel);
```

### Kotlin (Compose)

```kotlin
@Composable
fun MyScreen() {
    AndroidView(
        factory = { context ->
            Screen(context).apply {
                setTheme(Theme.FREE_LIGHT)
                setTabs(Tabs(context))
                setPanel(Panel(context))
            }
        }
    )
}
```

## API Reference

### Screen (Java)

| Method | Description |
|--------|-------------|
| setTheme(Theme) | Sets the visual theme |
| getCurrentTheme() | Returns the current theme |
| setTabs(Tabs) | Sets the Tabs component |
| getTabs() | Returns the Tabs component |
| setPanel(Panel) | Sets the Panel component |
| getPanel() | Returns the Panel component |
| onScreenLift(int) | Sets screen lift state |
| setOnScreenLiftListener(OnScreenLiftListener) | Sets screen lift listener |

## Themes

| Theme | Description |
|-------|-------------|
| free-light | Free car model, light theme |
| free-dark | Free car model, dark theme |
| dreamer-light | Dreamer car model, light theme |
| dreamer-dark | Dreamer car model, dark theme |

## Layout Structure

The Screen component manages the following layout:
- Tabs component positioned at offsetX with full height
- Panel component positioned after Tabs with offsetY from top
- Automatic measurement and layout of child components

## Screen Lift Functionality

The Screen component supports automotive screen lift functionality:
- SCREEN_RAISED: Normal position
- SCREEN_LOWERED: Lowered position for better visibility
- OnScreenLiftListener: Callback for state changes
