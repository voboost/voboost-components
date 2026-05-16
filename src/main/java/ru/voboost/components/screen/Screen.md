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
screen.setGapX(0);

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
    gapX = 0,
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
    gapX: Int = 0,
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

## Limitations

- Screen must be measured before animations work correctly
- Panel transitions require valid Screen dimensions (width > 0, height > 0)
- Rapid panel switching (faster than 300ms) may cancel previous animations
- Toast messages are not persistent across configuration changes
- Maximum number of panels is limited by available memory

## Performance Considerations

- Panel transition animations run on the UI thread for 300ms
- Each panel is measured and laid out on every transition
- Theme and language propagation traverses all child components recursively
- For large numbers of child components, consider lazy loading
- Toast views are added to the Screen view hierarchy and removed on dismiss

## Troubleshooting

### Panel transitions don't animate
- Ensure Screen has been measured before calling setActivePanel
- Check that panel dimensions are valid (width > 0, height > 0)
- Verify that Screen has valid offsetX and offsetY values

### Panels not visible
- Ensure setActivePanel was called with a valid index
- Check that panels array was set via setPanels()
- Verify that theme has been set before setting panels

### Toast not showing
- Ensure theme has been set before showing toast
- Check that toast duration is valid (DURATION_SHORT or DURATION_LONG)
- Verify that Screen is attached to window

### Memory leaks
- Ensure tabs listeners are properly removed when replacing tabs
- Check that panel references are cleared when no longer needed
- Verify that toast views are removed after dismiss

## Thread Safety

The Screen component is **not thread-safe**. All methods must be called on the UI (main) thread.

### Thread-safe methods
- `getCurrentTheme()` - returns cached value
- `getCurrentLanguage()` - returns cached value
- `getOffsetX()`, `getOffsetY()`, `getGapX()` - return cached values
- `getScreenLiftState()` - returns cached value
- `getActivePanel()` - returns cached reference
- `getPanels()` - returns cached array reference
- `getTabs()` - returns cached reference
- `getCurrentToast()` - returns cached reference

### Non-thread-safe methods (must be called on UI thread)
- `setTheme(Theme)` - modifies view hierarchy
- `setLanguage(Language)` - modifies view hierarchy
- `setOffsetX(int)`, `setOffsetY(int)`, `setGapX(int)` - trigger layout
- `setTabs(Tabs)` - modifies view hierarchy
- `setPanels(Panel[])` - modifies internal state
- `setActivePanel(int)` - triggers animations and layout changes
- `onScreenLift(int)` - modifies state and triggers layout
- `setOnScreenLiftListener(OnScreenLiftListener)` - modifies listener reference
- `showToast(String, long)` - modifies view hierarchy
- `dismissToast()` - modifies view hierarchy

### Animation thread-safety
Panel transition animations run on the UI thread using ObjectAnimator. Rapid calls to `setActivePanel()` are safe - the current animation will be cancelled before starting a new one.

## Compose Usage

Screen provides a modern Compose-friendly API:

```kotlin
@Composable
fun MyScreen() {
    var selectedTab by remember { mutableStateOf("settings") }

    Screen(
        tabs = listOf(
            TabItem("settings", mapOf("en" to "Settings")),
            TabItem("profile", mapOf("en" to "Profile"))
        ),
        panels = arrayOf(createSettingsPanel(), createProfilePanel()),
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        theme = Theme.FREE_LIGHT
    )
}
```

The Compose wrapper automatically:
- Creates Tabs component
- Manages active panel based on selectedTab
- Cleans up listeners to prevent memory leaks
- Optimizes recomposition with memoization
