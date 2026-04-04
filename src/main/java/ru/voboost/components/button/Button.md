# Button Component

## Architecture

- **[Button.java](Button.java)** — Java Custom View: canvas rendering, touch, state
- **[Button.kt](Button.kt)** — Kotlin Compose wrapper: lightweight AndroidView integration

Java handles all logic. Kotlin wrapper only bridges to Compose.

## Usage

### Java

```java
Button button = new Button(context);
button.setText("Confirm");
button.setStyle(ButtonStyle.PRIMARY);
button.setTheme(Theme.FREE_DARK);
button.setEnabled(true);
button.setOnClickListener(() -> {
    // Handle click
});
parentLayout.addView(button);
```

### Kotlin

```kotlin
val button = Button(context).apply {
    setText("Confirm")
    setStyle(ButtonStyle.PRIMARY)
    setTheme(Theme.FREE_DARK)
    isEnabled = true
    setOnClickListener { /* Handle click */ }
}
parentLayout.addView(button)
```

### Compose

```kotlin
Button(
    text = "Confirm",
    style = ButtonStyle.PRIMARY,
    theme = Theme.FREE_DARK,
    enabled = true,
    onClick = { /* Handle click */ }
)
```

## API

### Java Custom View

```java
// Configuration
void setText(String text)
String getText()
void setStyle(ButtonStyle style)     // ButtonStyle.PRIMARY, ButtonStyle.SECONDARY
ButtonStyle getStyle()
void setTheme(Theme theme)
Theme getCurrentTheme()

// State
void setEnabled(boolean enabled)
boolean isEnabled()

// Events
void setOnClickListener(OnClickListener listener)

interface OnClickListener {
    void onClick();
}
```

### Compose Wrapper

```kotlin
@Composable
fun Button(
    text: String,
    style: ButtonStyle = ButtonStyle.PRIMARY,
    theme: Theme,
    enabled: Boolean = true,
    onClick: () -> Unit,
    onViewCreated: ((Button) -> Unit)? = null
)
```

## Styles

| Style | Description |
|-------|-------------|
| `ButtonStyle.PRIMARY` | Accent-colored (blue/brown), white text |
| `ButtonStyle.SECONDARY` | Neutral background (gray), theme text |

## Themes

| Enum | Primary BG | Secondary BG |
|------|-----------|-------------|
| `Theme.FREE_LIGHT` | Blue `#4099f3` | Light gray `#e8ecf0` |
| `Theme.FREE_DARK` | Blue `#4099f3` | Dark gray `#373f4a` |
| `Theme.DREAMER_LIGHT` | Brown `#9c8069` | Dark gray `#40444a` |
| `Theme.DREAMER_DARK` | Brown `#9c8069` | Dark gray `#40444a` |

Colors defined in [ButtonTheme.java](ButtonTheme.java).

## States

- **Normal** — default appearance
- **Pressed** — instant color change on touch
- **Disabled** — reduced opacity (25% alpha)

## Dimensions

All sizes in **pixels** (automotive requirement). Constants in `ButtonDimensions`.

| Property | Value |
|----------|-------|
| Height | 70px |
| Corner radius | 35px (pill shape) |
| Text size | 28px |
| Horizontal padding | 40px |
| Min width | 200px |

## File Structure

```
button/
├── Button.java             # Core implementation
├── Button.kt               # Compose wrapper
├── ButtonStyle.java         # Style enum
├── ButtonTheme.java         # Colors, dimensions, schemes
├── Button.md               # This doc
├── Button.test/
│   ├── ButtonTestUnit.java  # Unit tests
│   └── ButtonTestVisual.java # Visual regression tests
└── Button.screenshots/     # Reference images
```
