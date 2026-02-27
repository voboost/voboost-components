# Screen Component

## Architecture

- **[Screen.java](Screen.java)** — Java ViewGroup: full-screen container, layout management
- **[Screen.kt](Screen.kt)** — Kotlin Compose wrapper

Root container that manages Tabs (sidebar) and Panel (content area) layout.

## Usage

### Java

```java
Screen screen = new Screen(context);
screen.setTheme(Theme.FREE_LIGHT);
screen.setOffsetX(175);
screen.setOffsetY(50);
screen.setGapX(42);

// Add tabs
Tabs tabs = new Tabs(context);
screen.setTabs(tabs);

// Add panels
Panel[] panels = new Panel[] { panel1, panel2 };
screen.setPanels(panels);
screen.setActivePanel(0);

// Screen lift
screen.setOnScreenLiftListener(state -> {
    // SCREEN_LOWERED (1) or SCREEN_RAISED (2)
});
```

### Kotlin

```kotlin
val screen = Screen(context).apply {
    setTheme(Theme.FREE_LIGHT)
    setTabs(tabs)
    setPanels(arrayOf(panel1, panel2))
    setActivePanel(0)
}
```

### Compose

```kotlin
Screen(
    theme = Theme.FREE_LIGHT,
    tabs = listOf(
        TabItem("store", mapOf("en" to "Store", "ru" to "Магазин")),
        TabItem("settings", mapOf("en" to "Settings", "ru" to "Настройки"))
    ),
    panels = arrayOf(storePanel, settingsPanel),
    offsetX = 175,
    offsetY = 50,
    gapX = 42,
    screenLiftState = 2,
    onScreenLift = { state -> /* handle */ }
)
```

## API

### Java

```java
// Theme
void setTheme(Theme theme)
Theme getCurrentTheme()

// Layout
void setOffsetX(int offsetX)         // content X offset (px)
void setOffsetY(int offsetY)         // content Y offset (px)
void setGapX(int gapX)              // gap between Tabs and Panel (px)
int getOffsetX()
int getOffsetY()
int getGapX()

// Components
void setTabs(Tabs tabs)
Tabs getTabs()
void setPanels(Panel[] panels)
Panel[] getPanels()
void setActivePanel(int index)
Panel getActivePanel()

// Screen lift
void onScreenLift(int state)         // SCREEN_LOWERED=1, SCREEN_RAISED=2
int getScreenLiftState()
void setOnScreenLiftListener(OnScreenLiftListener listener)

// Propagation
void propagateTheme(Theme theme)
void propagateLanguage(Language language)
```

### Compose Wrapper

```kotlin
@Composable
fun Screen(
    theme: Theme,
    tabs: List<TabItem>? = null,
    panels: Array<Panel>? = null,
    offsetX: Int = 175,
    offsetY: Int = 50,
    gapX: Int = 42,
    screenLiftState: Int = 2,
    onScreenLift: ((Int) -> Unit)? = null
)
```

## Layout Structure

Screen positions children:
1. **Tabs** — at `offsetX`, full height, inside ScrollView
2. **Panel** — after Tabs + `gapX`, at `offsetY` from top

Theme and language propagate to all children automatically.

## File Structure

```
screen/
├── Screen.java          # Core implementation
├── Screen.kt            # Compose wrapper
├── ScreenTheme.java     # Theme colors
├── Screen.md            # This doc
└── Screen.test/         # Tests
```
