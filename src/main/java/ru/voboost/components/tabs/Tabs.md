# Tabs Component

A vertical navigation sidebar component with animated selection indicator for automotive applications.

## Overview

The Tabs component displays a list of navigation items vertically with a sliding selection indicator that animates between tabs. It supports multiple themes and languages.

## Features

- Vertical tab layout
- Animated selection indicator with smooth transitions
- Multi-theme support (Free/Dreamer, Light/Dark)
- Multi-language support (EN/RU)
- Touch event handling
- State persistence across configuration changes
- Hardware-accelerated rendering

## Usage

### Java

```java
// Create tabs
Tabs tabs = new Tabs(context);

// Configure theme and language
tabs.setTheme(Theme.FREE_LIGHT);
tabs.setLanguage(Language.EN);

// Create tab items
List<TabItem> items = new ArrayList<>();

Map<String, String> storeLabels = new HashMap<>();
storeLabels.put("en", "Store");
storeLabels.put("ru", "Магазин");
items.add(new TabItem("store", storeLabels));

Map<String, String> settingsLabels = new HashMap<>();
settingsLabels.put("en", "Settings");
settingsLabels.put("ru", "Настройки");
items.add(new TabItem("settings", settingsLabels));

// Set items and selection
tabs.setItems(items);
tabs.setSelectedValue("store");

// Handle selection changes
tabs.setOnValueChangeListener(value -> {
    Log.d("Tabs", "Selected: " + value);
});
```

### Kotlin (Compose)

```kotlin
@Composable
fun MyScreen() {
    var selectedTab by remember { mutableStateOf("store") }

    val items = listOf(
        TabItem("store", mapOf("en" to "Store", "ru" to "Магазин")),
        TabItem("settings", mapOf("en" to "Settings", "ru" to "Настройки"))
    )

    Tabs(
        items = items,
        lang = "en",
        theme = "free-light",
        value = selectedTab,
        onValueChange = { selectedTab = it }
    )
}
```

## API Reference

### TabItem

| Property | Type | Description |
|----------|------|-------------|
| value | String | Unique identifier for the tab |
| label | Map<String, String> | Localized labels (language code → text) |

### Tabs (Java)

| Method | Description |
|--------|-------------|
| setItems(List<TabItem>) | Sets the list of tab items |
| setSelectedValue(String) | Sets the selected tab |
| setTheme(Theme) | Sets the visual theme |
| setLanguage(Language) | Sets the display language |
| setOnValueChangeListener(OnValueChangeListener) | Sets selection callback |

### Tabs (Compose)

| Parameter | Type | Description |
|-----------|------|-------------|
| items | List<TabItem> | Tab items to display |
| lang | String | Language code ("en", "ru") |
| theme | String | Theme identifier |
| value | String | Selected tab value |
| onValueChange | (String) -> Unit | Selection callback |

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
| SIDEBAR_WIDTH | 280px | Total sidebar width |
| TAB_ITEM_HEIGHT | 72px | Height of each tab |
| TAB_ITEM_WIDTH | 248px | Width of each tab |
| TAB_ITEM_SPACING | 8px | Vertical spacing between tabs |
| CORNER_RADIUS | 36px | Selection indicator corner radius |
| TEXT_SIZE | 28px | Tab label text size |
