# Button Component Screenshots

This directory contains reference screenshots for Button component visual regression testing.

## Screenshot Naming Convention

`button_{style}_{theme}_{state}.png`

- `style`: primary or secondary
- `theme`: free_light, free_dark, dreamer_light, dreamer_dark
- `state`: normal, pressed, disabled

## Test Coverage

- 4 themes × 2 styles × 3 states = 24 screenshots
- Additional tests for description text (single-line and multi-line)
- Total: 26+ reference images

## Updating Screenshots

To regenerate all screenshots:
```bash
./gradlew recordRoborazziDebug
```

Then copy generated images from `build/outputs/roborazzi/` to this directory.
