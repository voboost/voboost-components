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

String getValue()
Map<String, String> getLabel()
String getText(String langCode)
```

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
