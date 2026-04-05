# Buttons Component Screenshots

This directory contains reference screenshots for Buttons component visual regression testing.

## Screenshot Naming Convention

`buttons_{feature}_{theme}_{language}.png`

- `feature`: basic, right_text, description, both, russian
- `theme`: free_light, free_dark, dreamer_light, dreamer_dark
- `language`: en, ru

## Test Coverage

- 4 themes (FREE_LIGHT, FREE_DARK, DREAMER_LIGHT, DREAMER_DARK)
- Combinations: basic, right text, description, both
- Russian language localization
- Total: 10+ reference images

## Updating Screenshots

To regenerate all screenshots:
```bash
./gradlew recordRoborazziDebug
```

Then copy generated images from `build/outputs/roborazzi/` to this directory.
