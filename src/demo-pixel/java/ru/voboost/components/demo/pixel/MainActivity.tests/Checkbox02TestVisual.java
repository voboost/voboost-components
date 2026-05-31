package ru.voboost.components.demo.pixel;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileWriter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

/**
 * Pixel comparison test for the pixel demo.
 *
 * This test:
 * 1. Launches MainActivity via Robolectric in native graphics mode
 * 2. Renders the Screen component to a 1920x720 bitmap
 * 3. Loads the reference screenshot
 * (interface-2-display-checkbox_1original.png)
 * 4. Compares pixel-by-pixel (excluding system UI area)
 * 5. Generates a diff image highlighting differences in MAGENTA
 * 6. Saves everything to MainActivity.screenshots/
 *
 * RUN:
 * ./gradlew :demo-pixel:testDebugUnitTest
 * --tests="*MainActivityCheckboxTestVisual*"
 *
 * OUTPUT (in src/demo-pixel/java/.../pixel/MainActivity.screenshots/):
 * interface-2-display-checkbox_1original.png - original reference screenshot
 * interface-2-display-checkbox_2actual.png - what our components rendered
 * interface-2-display-checkbox_3diff.png - diff (matching=dimmed,
 * different=magenta)
 */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = { 33 }, qualifiers = "w1920dp-h720dp-land-mdpi")
public class Checkbox02TestVisual {

    // Automotive display dimensions
    private static final int SCREEN_WIDTH = 1920;
    private static final int SCREEN_HEIGHT = 720;

    // System UI boundaries (excluded from pixel comparison)
    // Left 145px = system launcher sidebar
    // Top 50px = status bar
    private static final int COMPARE_START_X = 145;
    private static final int COMPARE_START_Y = 50;

    // Per-channel pixel tolerance
    // 0 = exact match only
    // 5 = allow +-5 difference per R/G/B/A channel (for anti-aliasing)

    // Output directory (BEM co-located screenshots)
    // Path is relative to module root (src/demo-pixel/) since Gradle runs tests
    // from there
    private static final String OUTPUT_DIR = "java/ru/voboost/components/demo/pixel/MainActivity.screenshots";

    private ActivityController<Checkbox02> controller;
    private Checkbox02 activity;

    @Before
    public void setUp() {
        controller = Robolectric.buildActivity(Checkbox02.class);
        controller.create().start().resume();
        activity = controller.get();
    }

    @Test
    public void compareWithReferenceScreenshot() throws Exception {
        // Get variant name from system property (e.g., "v1", "v2", etc.)
        String variant = System.getProperty("variant", "");
        String suffix = variant.isEmpty() ? "" : "_" + variant;

        // ---- Step 1: Render our components to bitmap ----
        Bitmap actual = renderScreenToBitmap();
        assertNotNull("Failed to render screen to bitmap", actual);

        // Save our rendering with variant suffix
        File actualFile = new File(OUTPUT_DIR, "checkbox-02_2actual" + suffix + ".png");
        PixelComparator.savePng(actual, actualFile);
        System.out.println("Saved actual rendering: " + actualFile.getAbsolutePath());
        System.out.println("Actual size: " + actual.getWidth() + "x" + actual.getHeight());

        // ---- Step 2: Load reference image ----
        Bitmap reference = loadReferenceImage();
        if (reference == null) {
            System.out.println("");
            System.out.println("=== WARNING: Reference image not found ===");
            System.out.println("Looked for: /checkbox-02_1original.png");
            System.out.println("");
            System.out.println("Place your reference image at:");
            System.out.println(
                    "  src/demo-pixel/java/ru/voboost/components/demo/pixel/"
                            + "MainActivity.screenshots/checkbox-02_1original.png");
            System.out.println("");
            System.out.println("Skipping comparison. Actual rendering saved for inspection.");
            return;
        }

        System.out.println("Reference size: " + reference.getWidth() + "x" + reference.getHeight());

        // ---- Step 3: Compare pixels ----
        PixelComparator.ComparisonResult result = PixelComparator.compare(
                actual,
                reference,
                COMPARE_START_X,
                COMPARE_START_Y,
                SCREEN_WIDTH,
                SCREEN_HEIGHT,
                DemoRenderUtils.PIXEL_TOLERANCE);

        // ---- Step 4: Save diff images ----
        File diffFile = new File(OUTPUT_DIR, "checkbox-02_3diff" + suffix + ".png");
        PixelComparator.savePng(result.diffBitmap, diffFile);
        File magentaFile = new File(OUTPUT_DIR, "checkbox-02_4magenta" + suffix + ".png");
        PixelComparator.savePng(result.magentaBitmap, magentaFile);

        // ---- Step 5: Print and save report ----
        System.out.println("");
        System.out.println("=== PIXEL COMPARISON REPORT ===");
        System.out.println(result.toString());
        System.out.println("Diff image: " + diffFile.getAbsolutePath());
        System.out.println("Magenta image: " + magentaFile.getAbsolutePath());
        System.out.println(
                "Compare area: x=["
                        + COMPARE_START_X
                        + ".."
                        + SCREEN_WIDTH
                        + "], y=["
                        + COMPARE_START_Y
                        + ".."
                        + SCREEN_HEIGHT
                        + "]");
        System.out.println("Tolerance: " + DemoRenderUtils.PIXEL_TOLERANCE + " per channel");

        // Compare with baseline
        String testName = "checkbox-02";
        PixelComparator.ExpectedDiff expected = PixelComparator.ExpectedDiff.load(testName);
        String comparisonMsg = "";
        boolean needsSave = (expected == null);
        AssertionError assertionError = null;

        if (expected != null) {
            try {
                comparisonMsg = expected.compareWithCurrent(testName, result);
            } catch (AssertionError e) {
                comparisonMsg = e.getMessage();
                assertionError = e;
            }
        } else {
            comparisonMsg = "No baseline (first run - saving current as baseline)";
        }

        System.out.println("");
        System.out.println("=== BASELINE COMPARISON ===");
        System.out.print(comparisonMsg);
        System.out.println("");

        // Save text report (always, even on error)
        File reportFile = new File(OUTPUT_DIR, "checkbox-02" + suffix + ".txt");
        try (FileWriter writer = new FileWriter(reportFile)) {
            writer.write("Pixel Comparison Report\n");
            writer.write("=======================\n\n");
            writer.write(result.toString() + "\n\n");
            writer.write(
                    "Compare area: x=["
                            + COMPARE_START_X
                            + ".."
                            + SCREEN_WIDTH
                            + "], y=["
                            + COMPARE_START_Y
                            + ".."
                            + SCREEN_HEIGHT
                            + "]\n");
            writer.write("Tolerance: " + DemoRenderUtils.PIXEL_TOLERANCE + " per channel\n\n");
            writer.write("Files:\n");
            writer.write("  checkbox-02_1original.png  - original reference\n");
            writer.write("  checkbox-02_2actual.png    - our rendering\n");
            writer.write("  checkbox-02_3diff.png      - diff (semi-transparent magenta overlay)\n");
            writer.write(
                    "  checkbox-02_4magenta.png   - diff (pure magenta channel with transparent bg)\n\n");
            writer.write("How to read the diff:\n");
            writer.write("  Magenta pixels = our rendering differs from reference\n");
            writer.write("  Dimmed pixels  = our rendering matches the reference\n");
            writer.write("  Dark area      = system UI zone, excluded from comparison\n\n");
            writer.write("=== BASELINE COMPARISON ===\n");
            writer.write(comparisonMsg);

        }
        System.out.println("Report saved: " + reportFile.getAbsolutePath());

        // Save baseline if first run
        if (needsSave) {
            new PixelComparator.ExpectedDiff(result.matchPercentage, result.totalPixels, result.differentPixels)
                .save(testName);
            System.out.println("Baseline saved for: " + testName);
        }

        // Throw exception after saving report
        if (assertionError != null) {
            throw assertionError;
        }
    }

    /**
     * Renders the Screen component to a 1920x720 bitmap.
     */
    private Bitmap renderScreenToBitmap() {
        return DemoRenderUtils.renderScreen(activity.getScreen(), SCREEN_WIDTH, SCREEN_HEIGHT,
                DemoRenderUtils.SCROLL_END, 0);
    }

    /**
     * Loads the reference image from the screenshots directory.
     * Only PNG format is supported.
     */
    private Bitmap loadReferenceImage() {
        try {
            File referenceFile = new File(OUTPUT_DIR, "checkbox-02_1original.png");
            if (!referenceFile.exists()) {
                return null;
            }
            Bitmap bitmap = BitmapFactory.decodeFile(referenceFile.getAbsolutePath());
            return bitmap;
        } catch (Exception e) {
            System.out.println("Error loading reference image: " + e.getMessage());
            return null;
        }
    }
}
