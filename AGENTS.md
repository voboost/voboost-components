# Voboost Components — Rules

Inherits ../voboost-codestyle/AGENTS.md
[Detailed README](README.md) | [Reference component](src/main/java/ru/voboost/components/radio/Radio.md)

## Critical Rules

- **Emulator launch command**: `~/Library/Android/sdk/emulator/emulator -avd free`

- **NEVER modify `_1original.png` screenshots** — These are reference images from real vehicle hardware. They must never be changed. Only update `_2actual.png` by re-running tests `./gradlew :demo-pixel:test`
- **ALWAYS run `./gradlew :demo-pixel:test` after ANY changes** — This runs ALL visual tests and ensures no regressions. Don't rely on running individual tests. To rerun a single class use `./gradlew :demo-pixel:testDebugUnitTest --rerun-tasks --tests "ru.voboost.components.demo.pixel.<TestClassName>"`.

## Project Context

This is an **automotive UI component library** for a specific vehicle system with **fixed screen dimensions**.

- **Target**: Single automotive infotainment system
- **Screen size**: 1920x720 pixels (fixed, not responsive)
- **Design goal**: Match native vehicle settings appearance exactly
- **No adaptation**: Components are NOT designed to adapt to different screen sizes
- **Pixel-perfect**: All sizes are exact pixels to match vehicle design specs

Components must replicate the vehicle's native settings UI to feel "at home" in the system.

## Build Versions
- Java: **11 only**
- AGP: **8.2.2 only**

## Coding Standards
- ALL sizes in **pixels**, not dp
- ALL hex colors **lowercase**

## Architecture
- Each component is **self-contained** — no shared Component.kt, i18n, theme, or utils
- Strings via `Map<String, String>`, theme as parameter
- **Java Custom View** = core (logic, rendering, animations)
- **Kotlin wrapper** = AndroidView integration only

## Automotive-Specific Constraints

DO NOT implement:
- State persistence (onSaveInstanceState/onRestoreInstanceState) - fixed screen, no rotation
- Accessibility (contentDescription, announceForAccessibility) - no screen readers in vehicle

## BEM Tests
- Co-located in `src/main/java/ru/voboost/components/[component]/`
- `[Component].test/[Component]TestUnit.java`, `[Component]TestVisual.java`
- `[Component].screenshots/`
- No `src/test/java/` or `__tests__/`

## Visual Tests Validation

**IMPORTANT**: `./gradlew :demo-pixel:test` tests NEVER fail — they always pass regardless of visual changes.

To verify visual correctness:
1. Check the `.txt` files in `src/demo-pixel/java/ru/voboost/components/demo/pixel/MainActivity.screenshots/`
2. Look for `PixelComparison: XX.XX% match` at the top of each file
3. **PASS condition**: match percentage is >= 95% (or unchanged from baseline)
4. **FAIL condition**: match percentage drops significantly (indicates visual regression)

Example from `button-01.txt`:
```
PixelComparison: 98.87% match (1175788/1189250 pixels, 13462 different)
```

Baseline comparison section shows if match improved, stayed same, or degraded:
```
=== BASELINE COMPARISON ===
Diff comparison for button-checkbox-radio:
  PASS: Match IMPROVED! Was 99.28%, now 99.45%
```
