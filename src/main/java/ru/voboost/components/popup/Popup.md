# Popup Component

## Architecture

- **[Popup.java](Popup.java)** — Base Dialog overlay with animations
- **No Compose wrapper** — base infrastructure used by [Dialog](../dialog/Dialog.md) and [Select](../select/Select.md)

## Purpose

Popup is the **base overlay container** providing:
- Semi-transparent overlay background
- Rounded content panel with theme colors
- Scale + fade enter/exit animations (300ms)
- Dismiss on touch outside (configurable)

Dialog and Select use Popup as their container, injecting their own content.

## API

```java
Popup popup = new Popup(context);
popup.setTheme(Theme.FREE_DARK);
popup.setPopupContentView(myView);
popup.setDismissOnTouchOutside(true);
popup.show();
popup.dismissWithAnimation();
```

### Lifecycle Management

Popup automatically dismisses when the owning Activity is destroyed to prevent memory leaks.
The popup will also clean up its LifecycleObserver when dismissed manually.

### Best Practices

- Always call `dismissWithAnimation()` instead of `dismiss()` for consistent UX
- The popup manages its own lifecycle - don't keep references after showing
- Content view is added to the popup container - don't add it to other parent views

## Dimensions

| Property | Value |
|----------|-------|
| Panel width | 1020px |
| Corner radius | 20px |
| Padding | 40px |
| Animation duration | 300ms |
| Scale range | 0.8 → 1.0 |

## File Structure

```
popup/
├── Popup.java         # Base implementation
├── PopupTheme.java    # Colors, dimensions
├── Popup.md           # This doc
├── Popup.test/
│   ├── PopupTestUnit.java
│   └── PopupTestVisual.java
└── Popup.screenshots/
```
