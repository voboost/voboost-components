# Dialog Component

## Architecture

- **[Dialog.java](Dialog.java)** — Java: builds content layout, uses Popup as container
- **[Dialog.kt](Dialog.kt)** — Kotlin Compose wrapper: lifecycle management

Dialog composes our [Popup](../popup/Popup.md) for overlay and our [Button](../button/Button.md) for actions.

## Usage

### Java

```java
Dialog dialog = new Dialog(context);
dialog.setTheme(Theme.FREE_DARK);
dialog.setTitle("Reset Settings");
dialog.setMessage("Are you sure?");
dialog.setConfirmButton("Reset", () -> { resetSettings(); });
dialog.setCancelButton("Cancel", () -> {});
dialog.show();
```

### Compose

```kotlin
var showDialog by remember { mutableStateOf(false) }

if (showDialog) {
    Dialog(
        theme = Theme.FREE_DARK,
        title = "Reset Settings",
        message = "Are you sure?",
        confirmText = "Reset",
        cancelText = "Cancel",
        onConfirm = { resetSettings(); showDialog = false },
        onCancel = { showDialog = false },
        onDismiss = { showDialog = false }
    )
}
```

## API

### Java

```java
void setTheme(Theme theme)
void setTitle(String title)
void setMessage(String message)
void setConfirmButton(String text, Runnable action)
void setCancelButton(String text, Runnable action)
void setOnDismissListener(Runnable onDismiss)
void show()
void dismiss()
boolean isShowing()
void rebuildContent()
```

### Content Updates

Dialog content (title, message, buttons) is built once on first `show()` for performance.
If you need to change content after the first show(), call `rebuildContent()` before
calling `show()` again.

Example:
```java
dialog.setTitle("Initial Title");
dialog.show();

// Later...
dialog.setTitle("Updated Title");
dialog.rebuildContent(); // Required!
dialog.show();
```

## Thread Safety

All Dialog methods must be called from the main UI thread. Calling methods from background threads may cause race conditions and undefined behavior.

```java
// Correct - called from UI thread
runOnUiThread(() -> {
    dialog.setTitle("Updated");
    dialog.show();
});

// Incorrect - may cause issues
new Thread(() -> {
    dialog.setTitle("Updated"); // Don't do this
    dialog.show(); // Don't do this
}).start();
```

## Content Updates

Dialog content can be updated at any time before or after `show()`. When content properties (title, message, buttons) change, the dialog will rebuild its content on the next `show()` call.

```java
Dialog dialog = new Dialog(context);
dialog.setTheme(Theme.FREE_DARK);
dialog.setTitle("Step 1");
dialog.show();

// Later, update content
dialog.setTitle("Step 2");
dialog.show(); // Content is rebuilt with new title
```

## Animation Duration

Dialog enter/exit animations use `PopupDimensions.ANIMATION_DURATION` (typically ~300ms). Animation timing is handled by the Popup component.

## Validation

Dialog validates input parameters:
- `setTheme()` throws `IllegalArgumentException` if theme is null
- `setConfirmButton()` throws `IllegalArgumentException` if text is null or empty
- `setCancelButton()` throws `IllegalArgumentException` if text is null or empty
- `setTitle()` and `setMessage()` accept null (treated as empty)

```java
// Valid
dialog.setTitle("Title");
dialog.setTitle(null); // OK - no title shown
dialog.setMessage("Message");
dialog.setMessage(null); // OK - no message shown

// Invalid - throws IllegalArgumentException
dialog.setConfirmButton("", () -> {}); // Empty text
dialog.setConfirmButton(null, () -> {}); // Null text
```

### Compose

```kotlin
@Composable
fun Dialog(
    theme: Theme,
    title: String = "",
    message: String = "",
    confirmText: String? = null,
    cancelText: String? = null,
    onConfirm: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null
)
```

## File Structure

```
dialog/
├── Dialog.java            # Core implementation
├── Dialog.kt              # Compose wrapper
├── DialogTheme.java       # Colors, dimensions
├── Dialog.md              # This doc
├── Dialog.test/
│   ├── DialogTestUnit.java
│   └── DialogTestVisual.java
└── Dialog.screenshots/
```
