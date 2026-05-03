# Tabs Component

## Architecture

- **[Tabs.java](Tabs.java)** — Java Custom View: vertical sidebar, animated selection, canvas rendering
- **[Tabs.kt](Tabs.kt)** — Kotlin Compose wrapper

Vertical navigation sidebar with sliding selection indicator.

## Usage

### Java

```java
Tabs tabs = new Tabs(context);
tabs.setTheme(Theme.FREE_LIGHT);
tabs.setLanguage(Language.EN);

tabs.setItems(Arrays.asList(
    new TabItem("store", Map.of("en", "Store", "ru", "Магазин")),
    new TabItem("settings", Map.of("en", "Settings", "ru", "Настройки"))
));
tabs.setSelectedValue("store");

tabs.setOnValueChangeListener(value -> {
    // Handle tab selection
});
```

### Kotlin

```kotlin
val tabs = Tabs(context).apply {
    setTheme(Theme.FREE_LIGHT)
    setLanguage(Language.EN)
    setItems(listOf(
        TabItem("store", mapOf("en" to "Store", "ru" to "Магазин")),
        TabItem("settings", mapOf("en" to "Settings", "ru" to "Настройки"))
    ))
    setSelectedValue("store")
    setOnValueChangeListener { value -> /* handle */ }
}
```

### Compose

```kotlin
var selectedTab by remember { mutableStateOf("store") }

Tabs(
    items = listOf(
        TabItem("store", mapOf("en" to "Store", "ru" to "Магазин")),
        TabItem("settings", mapOf("en" to "Settings", "ru" to "Настройки"))
    ),
    lang = Language.EN,
    theme = Theme.FREE_LIGHT,
    value = selectedTab,
    onValueChange = { selectedTab = it }
)
```

## API

### Java

```java
// Configuration
void setItems(List<TabItem> items)
void setTheme(Theme theme)
void setLanguage(Language language)

// Value
void setSelectedValue(String value)
void setSelectedValue(String value, boolean triggerCallback)
String getSelectedValue()

// State
Theme getCurrentTheme()
Language getCurrentLanguage()
int getSidebarWidth()

// Events
void setOnValueChangeListener(OnValueChangeListener listener)
void setOnTabChangeListener(OnTabChangeListener listener)

interface OnValueChangeListener {
    void onValueChange(String value);
}

interface OnTabChangeListener {
    void onTabChanged(int newIndex);
}
```

### TabItem

```java
TabItem(String value, Map<String, String> label)
TabItem(String value, Map<String, String> label, boolean enabled)
TabItem(String value, Map<String, String> label, boolean enabled, int marginTop)
TabItem(String value, Map<String, String> label, boolean enabled, int marginTop, boolean more)

String getValue()
Map<String, String> getLabel()
String getText(String langCode)
boolean isEnabled()
int getMarginTop()
boolean hasMore()
```

#### Builder

```java
TabItem item = TabItem.create("vehicle", labels)
    .enabled(true)
    .selected(false)
    .marginTop(30)
    .more(true)
    .build();
```

A tab with `more(true)` renders a trailing ">" indicator on the right side of the tab. The
indicator is drawn from 30x30 PNG assets, with a separate pair per theme brightness:
dark themes use `Tabs_theme_dark.png` / `Tabs_theme_dark.Tabs_pressed.png` (copied byte-for-byte
from the original Voyah `icon_menu_more_nor.png` / `icon_menu_more_pre.png`, gray+alpha);
light themes use `Tabs_theme_light.png` / `Tabs_theme_light.Tabs_pressed.png` (from the Voyah
`resc-defaulttheme-simple` light skin, RGBA with `#2D3442` matching the unselected text color).
When the user touches such a tab the indicator switches to the `*_pressed` variant — matches
the original `state_pressed` behavior on the trailing icon. PNGs are loaded lazily from
classpath alongside `Tabs.class`. The text and background of the tab itself do not change on
press — only the more-indicator does.

### Compose Wrapper

```kotlin
@Composable
fun Tabs(
    items: List<TabItem>,
    lang: Language,
    theme: Theme,
    value: String,
    onValueChange: (String) -> Unit
)
```

## Implementation Details

Animated selection indicator using `ValueAnimator`. State persistence via `onSaveInstanceState`/`onRestoreInstanceState`. Sizes in pixels (automotive requirement), defined in `TabsDimensions`.

## File Structure

```
tabs/
├── Tabs.java            # Core implementation
├── Tabs.kt              # Compose wrapper
├── TabItem.java         # Data model
├── TabsTheme.java       # Theme colors and dimensions
├── Tabs.md              # This doc
└── Tabs.test/           # Tests
```

## Error Handling

The Tabs component validates input and throws exceptions for invalid arguments:

- `setTheme(null)` → throws `IllegalArgumentException`
- `setLanguage(null)` → throws `IllegalArgumentException`
- `setItems(List<TabItem>)` with null elements → throws `IllegalArgumentException`
- `TabItem(null, label)` → throws `IllegalArgumentException`
- `TabItem(value, null)` → throws `IllegalArgumentException`

Disabled tabs are ignored when selected via touch or `setSelectedValue()`.

## Themes

| Enum | Style |
|------|-------|
| `Theme.FREE_LIGHT` | Light background + white selection |
| `Theme.FREE_DARK` | Transparent background + dark selection |
| `Theme.DREAMER_LIGHT` | Light gray background + white selection |
| `Theme.DREAMER_DARK` | Dark background + dark gray selection |

## Disabled Tabs

Tabs can be disabled to prevent user interaction:

```java
TabItem enabledTab = new TabItem("settings", labels, true);
TabItem disabledTab = new TabItem("admin", labels, false);

tabs.setItems(Arrays.asList(enabledTab, disabledTab));
```

Disabled tabs:
- Are displayed with gray text
- Do not respond to touch events
- Are ignored when selected via `setSelectedValue()`
- Do not trigger `OnValueChangeListener`
