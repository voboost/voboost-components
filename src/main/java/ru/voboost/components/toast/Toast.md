# Toast Component

## Architecture

- **[Toast.java](Toast.java)** — Java Custom View: canvas rendering, animations, touch, state
- **[Toast.kt](Toast.kt)** — Kotlin Compose wrapper: lightweight AndroidView integration
- **[ToastTheme.java](ToastTheme.java)** — Theme colors, dimensions, and animation constants

Java handles all logic. Kotlin wrapper only bridges to Compose.

## Overview

Toast is a notification component that appears at the top of the screen with a slide-in animation. It automatically dismisses after a configurable duration and supports an optional close button.

### Features

- Slide-in animation from top with fade effect
- Auto-dismiss after configurable duration (3s or 5s by default)
- Optional close button for manual dismissal
- Theme support (FREE_DARK, FREE_LIGHT, DREAMER_DARK, DREAMER_LIGHT)
- Text truncation with ellipsis for long messages
- Hardware-accelerated rendering for smooth animations

## Usage

### Java (Standalone)

```java
Toast toast = new Toast(context);
toast.setTheme(Theme.FREE_DARK);
toast.setContent("Settings saved");
toast.setDuration(ToastTheme.DURATION_SHORT);
toast.show();

// With close button
toast.setShowCloseButton(true);

// With dismiss callback
toast.setOnDismissListener(() -> {
    // Handle dismiss
});
```

### Java (via Screen)

```java
Screen screen = new Screen(context);
screen.setTheme(Theme.FREE_DARK);

// Show short toast (3 seconds)
screen.showToast("Settings saved", ToastTheme.DURATION_SHORT);

// Show long toast (5 seconds)
screen.showToast("Operation completed successfully", ToastTheme.DURATION_LONG);

// Dismiss current toast
screen.dismissToast();

// Get current toast
Toast currentToast = screen.getCurrentToast();
```

### Kotlin

```kotlin
val toast = Toast(context).apply {
    setTheme(Theme.FREE_DARK)
    setContent("Settings saved")
    setDuration(ToastTheme.DURATION_SHORT)
    setShowCloseButton(false)
    setOnDismissListener {
        // Handle dismiss
    }
}
toast.show()
```

### Compose

```kotlin
Toast(
    theme = Theme.FREE_DARK,
    text = "Settings saved",
    duration = ToastTheme.DURATION_SHORT,
    showCloseButton = false
)
```

## API

### Java Custom View

```java
// Theme
void setTheme(Theme theme)
Theme getCurrentTheme()

// Content
void setContent(String text)
String getContent()

// Duration
void setDuration(long millis)
long getDuration()

// Close button
void setShowCloseButton(boolean show)
boolean isShowCloseButton()

// State
boolean isShowing()

// Actions
void show()
void dismiss()

// Events
void setOnDismissListener(OnDismissListener listener)

interface OnDismissListener {
    void onDismiss();
}
```

### Compose Wrapper

```kotlin
@Composable
fun Toast(
    theme: Theme,
    text: String = "",
    duration: Long = ToastTheme.DURATION_SHORT,
    showCloseButton: Boolean = false,
    modifier: Modifier = Modifier
)
```

### Screen Integration

```java
// Show toast
void showToast(String text, long duration)

// Dismiss toast
void dismissToast()

// Get current toast
Toast getCurrentToast()
```

## Themes

| Theme | Background | Text Color | Close Icon |
|-------|------------|------------|------------|
| `Theme.FREE_DARK` | `#23272f` | `#cacaca` | `#cacaca` |
| `Theme.FREE_LIGHT` | `#f9faff` | `#2d3543` | `#2d3543` |
| `Theme.DREAMER_DARK` | `#23272f` | `#cacaca` | `#cacaca` |
| `Theme.DREAMER_LIGHT` | `#f9faff` | `#2d3543` | `#2d3543` |

Theme colors defined in [ToastTheme.java](ToastTheme.java): `ToastColors`, `ToastColorSchemes`.

## Dimensions

All dimensions in **pixels** (automotive requirement):

| Property | Value | Description |
|----------|-------|-------------|
| `WIDTH` | 1260px | Toast width |
| `HEIGHT` | 100px | Toast height |
| `CORNER_RADIUS` | 20px | Corner radius |
| `TEXT_SIZE` | 36px | Text size |
| `CLOSE_BUTTON_SIZE` | 50px | Close button touch area |
| `CLOSE_ICON_SIZE` | 24px | Close icon visual size |
| `TOP_OFFSET` | 10px | Distance from top of screen |

## Animation

### Entrance Animation

- **Slide**: Translates from `-HEIGHT` to `0` (slides down from top)
- **Fade**: Alpha from `0` to `1`
- **Duration**: 400ms (slide), 100ms (fade)
- **Interpolator**: DecelerateInterpolator(factor=3)

### Exit Animation

- **Slide**: Translates from `0` to `-HEIGHT` (slides up to top)
- **Fade**: Alpha from `1` to `0`
- **Duration**: 400ms (slide), 100ms (fade)
- **Interpolator**: DecelerateInterpolator(factor=3)

### Auto-Dismiss Durations

```java
ToastTheme.DURATION_SHORT  // 3000ms (3 seconds)
ToastTheme.DURATION_LONG   // 5000ms (5 seconds)
```

Set duration to `0` or negative to disable auto-dismiss.

## Thread Safety

Toast is designed to be used from the main thread only. All public methods should be called from the UI thread.

- `show()` and `dismiss()` are protected against concurrent calls
- `dismissInProgress` flag prevents multiple dismiss operations
- Lifecycle observer ensures cleanup when Activity is destroyed

**Important**: Do not call `show()` or `dismiss()` from background threads.

## Implementation Details

### Rendering

Canvas-based with 3 elements:
1. **Background** — rounded rectangle with theme color
2. **Text** — centered, truncated with ellipsis if too long
3. **Close button** (optional) — cross icon on the left side

### Touch Handling

When close button is enabled:
- Touch within close button area (50px × 50px) triggers dismiss
- Touch outside close button area is ignored

### Text Handling

- Uses `Font.getBold()` for proper ASCII/Unicode font selection
- Automatically truncates long text with ellipsis
- Accounts for close button width when calculating available text space

### Hardware Acceleration

Uses `LAYER_TYPE_HARDWARE` for smooth animations.

### Memory Management

- Cancels animations on `onDetachedFromWindow()`
- Removes Handler callbacks to prevent leaks
- Cleans up dismiss listener after callback
- Lifecycle observer auto-dismisses on Activity destroy
- Auto-dismiss runnable is cleared on detach

## Integration with Screen

Toast is positioned at the top center of the Screen:
- Horizontal: centered
- Vertical: `TOP_OFFSET` (10px) from top

Screen manages:
- Adding/removing toast from view hierarchy
- Measuring and laying out toast
- Propagating theme changes
- Ensuring only one toast is shown at a time

## Testing

### Unit Tests

[ToastTestUnit.java](Toast.test/ToastTestUnit.java) — 40+ tests covering:
- Initialization
- Theme management
- Content handling
- Duration configuration
- Close button behavior
- Show/dismiss lifecycle
- Touch handling
- Callbacks
- Measurement
- Memory management

Run with:
```bash
./gradlew test --tests "ru.voboost.components.toast.ToastTestUnit"
```

### Visual Tests

[ToastTestVisual.java](Toast.test/ToastTestVisual.java) — 19 screenshot tests covering:
- All 4 themes
- Short and long text
- Close button variants
- Unicode text (Cyrillic)
- Edge cases (empty, very long text)

Generate screenshots:
```bash
./gradlew recordRoborazziDebug
```

Compare screenshots:
```bash
./gradlew compareRoborazziDebug
```

## Examples

### Basic Toast

```java
screen.showToast("Settings saved", ToastTheme.DURATION_SHORT);
```

### Long Duration Toast

```java
screen.showToast("Your changes have been saved successfully", ToastTheme.DURATION_LONG);
```

### Toast with Close Button

```java
Toast toast = new Toast(context);
toast.setTheme(Theme.FREE_DARK);
toast.setContent("Loading data...");
toast.setShowCloseButton(true);
toast.setDuration(0); // No auto-dismiss
toast.show();
```

### Toast with Callback

```java
Toast toast = new Toast(context);
toast.setTheme(Theme.FREE_DARK);
toast.setContent("Processing...");
toast.setOnDismissListener(() -> {
    Log.d("Toast", "Toast dismissed");
});
toast.show();
```

### Custom Duration

```java
screen.showToast("Custom message", 10000); // 10 seconds
```

### Manual Dismiss

```java
screen.showToast("Message", ToastTheme.DURATION_LONG);
// Later...
screen.dismissToast();
```

## Best Practices

1. **Keep messages short** — Toast has limited width and truncates long text
2. **Use appropriate duration** — SHORT for confirmations, LONG for important messages
3. **Avoid close button for auto-dismiss** — Use close button only for persistent toasts
4. **One toast at a time** — Screen automatically dismisses previous toast when showing new one
5. **Set theme before showing** — Toast requires theme to render properly
6. **Clean up listeners** — Set listener to null when no longer needed

## Comparison with Original

The original implementation used `WindowManager.addView()` to show toast as a system overlay. Our implementation:

- Uses standard View hierarchy (child of Screen)
- No system permissions required
- Easier to test with Robolectric
- Consistent with project architecture
- Same visual appearance and animation
- Same API surface (show/dismiss/duration)

## Files

- [Toast.java](Toast.java) — Main component (450+ lines)
- [Toast.kt](Toast.kt) — Compose wrapper (40 lines)
- [ToastTheme.java](ToastTheme.java) — Theme constants (110 lines)
- [Toast.test/ToastTestUnit.java](Toast.test/ToastTestUnit.java) — Unit tests (400+ lines)
- [Toast.test/ToastTestVisual.java](Toast.test/ToastTestVisual.java) — Visual tests (250+ lines)
- [Toast.screenshots/](Toast.screenshots/) — Visual regression screenshots
- [Toast.md](Toast.md) — This documentation
