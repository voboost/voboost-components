# Toast Screenshots

Visual regression test screenshots for the Toast component.

Generated automatically by running:
```bash
./gradlew recordRoborazziDebug
```

## Screenshot Naming Convention

Screenshots follow the pattern: `toast_<theme>_<variant>.png`

### Themes
- `freeDark` - Free theme, dark mode
- `freeLight` - Free theme, light mode
- `dreamerDark` - Dreamer theme, dark mode
- `dreamerLight` - Dreamer theme, light mode

### Variants
- `short` - Short message text
- `long` - Long message text (tests text wrapping/truncation)
- `closeButton` - Toast with close button enabled
- `unicode` - Unicode text (Cyrillic characters)
- `empty` - Empty message (edge case)
- `veryLong` - Very long message (tests ellipsis)
- `closeButton_longText` - Close button with long text

## Test Configuration

- Screen size: 1920x720 (automotive landscape)
- SDK: 33
- Graphics mode: NATIVE
- Qualifiers: w1920dp-h720dp-land-mdpi
