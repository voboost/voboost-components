# Demo Pixel Tests

Visual regression tests that verify component rendering correctness through pixel-by-pixel comparison with reference screenshots.

## How it works

Tests do NOT compare screenshots directly. Instead:

1. **Render**: Components are rendered to a 1920x720 bitmap (automotive display size)
2. **Compare**: Current render is compared pixel-by-pixel with reference screenshot (`_1original.png`)
3. **Baseline**: Comparison result (match percentage) is compared against saved baseline in `expected-diff.json`
4. **Verdict**:
   - If current match percentage is **worse** than baseline → test fails (AssertionError)
   - If current match percentage is **better or equal** to baseline → test passes
   - If baseline doesn't exist → current result is saved as baseline

## Output files

After test run in `MainActivity.screenshots/`:

- `*_1original.png` — reference screenshot (from real device, NEVER modify)
- `*_2actual.png` — current component render
- `*_3diff.png` — diff image (dimmed = match, magenta = differs)
- `*_4magenta.png` — pure magenta channel (transparent background)
- `*.txt` — text report with match percentage
- `expected-diff.json` — baseline values for all tests

## Running

```bash
# All demo-pixel tests
./gradlew :demo-pixel:test

# Specific test
./gradlew :demo-pixel:test --tests="*MainActivityCheckboxTestVisual*"
```

## Updating baseline

When component changes are intentional and visually correct:

1. Remove old baseline from `expected-diff.json` for the test
2. Run test — it will save new values as baseline
3. Or: edit `expected-diff.json` manually

**Important**: Never modify `_1original.png`! It's the reference from real device.

## Interpreting results

- **100% match** — perfect match
- **95-99% match** — minor anti-aliasing differences (normal)
- **<95% match** — visual differences present, check diff image

## Comparison parameters

- **COMPARE_START_X** = 485 (for checkbox) or 145 (for main) — excludes system UI zone on left
- **COMPARE_START_Y** = 50 — excludes status bar at top
- **PIXEL_TOLERANCE** = 5 — allowed per-channel R/G/B difference (for anti-aliasing)

## Test structure

```java
@Test
public void compareWithReferenceScreenshot() throws Exception {
    // 1. Render components to bitmap
    Bitmap actual = renderScreenToBitmap();

    // 2. Save actual rendering
    PixelComparator.savePng(actual, actualFile);

    // 3. Load reference image
    Bitmap reference = loadReferenceImage();

    // 4. Compare pixels
    ComparisonResult result = PixelComparator.compare(actual, reference, ...);

    // 5. Load baseline
    ExpectedDiff expected = ExpectedDiff.load(testName);

    // 6. Compare against baseline
    if (expected != null) {
        expected.compareWithCurrent(testName, result); // Throws AssertionError if worse
    }
}
```
