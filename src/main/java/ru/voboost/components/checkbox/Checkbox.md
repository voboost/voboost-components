# Checkbox Component

## Architecture

- **[Checkbox.java](Checkbox.java)** — Java Custom View: canvas rendering, animations, touch, state
- **[Checkbox.kt](Checkbox.kt)** — Kotlin Compose wrapper: lightweight AndroidView integration

## Usage

### Java

```java
Checkbox checkbox = new Checkbox(context);
checkbox.setTheme(Theme.FREE_DARK);
checkbox.setChecked(true);
checkbox.setOnCheckedChangeListener(isChecked -> {
    // Handle change
});
parentLayout.addView(checkbox);
```

### Compose

```kotlin
var checked by remember { mutableStateOf(false) }

Checkbox(
    theme = Theme.FREE_DARK,
    checked = checked,
    onCheckedChange = { checked = it }
)
```

## API

### Java Custom View

```java
void setTheme(Theme theme)
Theme getCurrentTheme()

void setChecked(boolean checked)        // instant, no animation
void setCheckedAnimated(boolean checked) // with animation
boolean isChecked()

void setEnabled(boolean enabled)
boolean isEnabled()

void setOnCheckedChangeListener(OnCheckedChangeListener listener)

interface OnCheckedChangeListener {
    void onCheckedChange(boolean isChecked);
}
```

### Compose Wrapper

```kotlin
@Composable
fun Checkbox(
    theme: Theme,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    onViewCreated: ((Checkbox) -> Unit)? = null
)
```

## Rendering

3-layer canvas rendering:
1. **Track background** — rounded rect, OFF color
2. **Gradient mask** — rounded rect, color interpolated via ArgbEvaluator
3. **Knob** — white circle, X position animated with OvershootInterpolator

## Dimensions

| Property | Value |
|----------|-------|
| Width | 83px (TRACK_WIDTH_PX) |
| Height | 42px (TRACK_HEIGHT_PX) |
| Corner radius | 21px |
| Knob size | 34px |
| Knob padding | 5px |
| Animation duration | 400ms |

## File Structure

```
checkbox/
├── Checkbox.java            # Core implementation
├── Checkbox.kt              # Compose wrapper
├── CheckboxTheme.java       # Colors, dimensions, schemes
├── Checkbox.md              # This doc
├── Checkbox.test/
│   ├── CheckboxTestUnit.java
│   └── CheckboxTestVisual.java
└── Checkbox.screenshots/
```
