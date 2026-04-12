# Select Component

## Architecture

- **[Select.java](Select.java)** — Trigger button (canvas View) + popup management
- **[Select.kt](Select.kt)** — Compose wrapper
- **[SelectWheel.java](SelectWheel.java)** — 3D cylindrical picker (Camera/Matrix transforms)
- **[SelectPopup.java](SelectPopup.java)** — Internal: Popup + WheelView integration
- **[SelectOption.java](SelectOption.java)** — Data model (value + localized label)

## Usage

### Java

```java
Select select = new Select(context);
select.setOptions(Arrays.asList(
    new SelectOption("auto", Map.of("en", "Automatic", "ru", "Авто")),
    new SelectOption("manual", Map.of("en", "Manual", "ru", "Ручной"))
));
select.setLanguage(Language.EN);
select.setTheme(Theme.FREE_DARK);
select.setSelectedValue("auto");
select.setOnValueChangeListener(newValue -> {
    // Handle selection
});
```

### Compose

```kotlin
var selected by remember { mutableStateOf("auto") }

Select(
    options = listOf(
        SelectOption("auto", mapOf("en" to "Automatic", "ru" to "Авто")),
        SelectOption("manual", mapOf("en" to "Manual", "ru" to "Ручной"))
    ),
    lang = Language.EN,
    theme = Theme.FREE_DARK,
    value = selected,
    onValueChange = { selected = it }
)
```

## 3D WheelView

SelectWheel matches the original WheelView implementation:

| Feature | Implementation |
|---------|---------------|
| **3D curve** | Camera + Matrix: rotateX + translate Z |
| **Scrolling** | Scroller + VelocityTracker: fling, snap, inertia |
| **Cyclic mode** | Infinite scrolling with wrap-around |
| **Atmospheric** | Alpha fade by distance from center |
| **Indicator** | Horizontal lines above/below selected |
| **Curtain** | Semi-transparent highlight on selected |
| **Selected style** | Larger font, bold, distinct color |

## File Structure

```
select/
├── Select.java             # Trigger button
├── Select.kt               # Compose wrapper
├── SelectOption.java        # Data model
├── SelectWheel.java         # 3D WheelView
├── SelectPopup.java         # Internal popup
├── SelectTheme.java         # Colors, dimensions
├── Select.md               # This doc
├── Select.test/
│   ├── SelectTestUnit.java
│   └── SelectTestVisual.java
└── Select.screenshots/
```
