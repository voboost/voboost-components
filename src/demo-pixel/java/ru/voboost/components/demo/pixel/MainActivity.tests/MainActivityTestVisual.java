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
 * 3. Loads the reference screenshot (interface-2-display_1original.png)
 * 4. Compares pixel-by-pixel (excluding system UI area)
 * 5. Generates a diff image highlighting differences in MAGENTA
 * 6. Saves everything to MainActivity.screenshots/
 *
 * RUN:
 *   ./gradlew :demo-pixel:testDebugUnitTest --tests="*MainActivityTestVisual*"
 *
 * OUTPUT (in src/demo-pixel/java/.../pixel/MainActivity.screenshots/):
 *   interface-2-display_1original.png  - original reference screenshot
 *   interface-2-display_2actual.png    - what our components rendered
 *   interface-2-display_3diff.png      - diff (matching=dimmed, different=magenta)
 *   interface-2-display.txt            - text report with match percentage
 */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
        sdk = {33},
        qualifiers = "w1920dp-h720dp-land-mdpi")
public class MainActivityTestVisual {

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
    private static final int PIXEL_TOLERANCE = 5;

    // Output directory (BEM co-located screenshots)
    // Path is relative to module root (src/demo-pixel/) since Gradle runs tests from there
    private static final String OUTPUT_DIR =
            "java/ru/voboost/components/demo/pixel/MainActivity.screenshots";

    private ActivityController<MainActivity> controller;
    private MainActivity activity;

    @Before
    public void setUp() {
        controller = Robolectric.buildActivity(MainActivity.class);
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
        File actualFile = new File(OUTPUT_DIR, "interface-2-display_2actual" + suffix + ".png");
        PixelComparator.savePng(actual, actualFile);
        System.out.println("Saved actual rendering: " + actualFile.getAbsolutePath());
        System.out.println("Actual size: " + actual.getWidth() + "x" + actual.getHeight());

        // ---- Step 2: Load reference image ----
        Bitmap reference = loadReferenceImage();
        if (reference == null) {
            System.out.println("");
            System.out.println("=== WARNING: Reference image not found ===");
            System.out.println("Looked for: /interface-2-display_1original.png");
            System.out.println("");
            System.out.println("Place your reference image at:");
            System.out.println(
                    "  src/demo-pixel/java/ru/voboost/components/demo/pixel/"
                            + "MainActivity.resources/interface-2-display_1original.png");
            System.out.println("");
            System.out.println("Skipping comparison. Actual rendering saved for inspection.");
            return;
        }

        System.out.println("Reference size: " + reference.getWidth() + "x" + reference.getHeight());

        // ---- Step 3: Compare pixels ----
        PixelComparator.ComparisonResult result =
                PixelComparator.compare(
                        actual,
                        reference,
                        COMPARE_START_X,
                        COMPARE_START_Y,
                        SCREEN_WIDTH,
                        SCREEN_HEIGHT,
                        PIXEL_TOLERANCE);

        // ---- Step 4: Save diff image ----
        File diffFile = new File(OUTPUT_DIR, "interface-2-display_3diff" + suffix + ".png");
        PixelComparator.savePng(result.diffBitmap, diffFile);

        // ---- Step 5: Print and save report ----
        System.out.println("");
        System.out.println("=== PIXEL COMPARISON REPORT ===");
        System.out.println(result.toString());
        System.out.println("Diff image: " + diffFile.getAbsolutePath());
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
        System.out.println("Tolerance: " + PIXEL_TOLERANCE + " per channel");
        System.out.println("");

        // Save text report
        File reportFile = new File(OUTPUT_DIR, "interface-2-display" + suffix + ".txt");
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
            writer.write("Tolerance: " + PIXEL_TOLERANCE + " per channel\n\n");
            writer.write("Files:\n");
            writer.write("  interface-2-display_1original.png  - original reference\n");
            writer.write("  interface-2-display_2actual.png    - our rendering\n");
            writer.write("  interface-2-display_3diff.png      - diff (magenta = different)\n\n");
            writer.write("How to read the diff:\n");
            writer.write("  Magenta pixels = our rendering differs from reference\n");
            writer.write("  Dimmed pixels  = our rendering matches the reference\n");
            writer.write("  Dark area      = system UI zone, excluded from comparison\n");
        }
        System.out.println("Report saved: " + reportFile.getAbsolutePath());
    }

    /**
     * Renders the Screen component to a 1920x720 bitmap.
     */
    private Bitmap renderScreenToBitmap() {
        View screen = activity.getScreen();
        if (screen == null) return null;

        // Force measure at automotive resolution
        int widthSpec = View.MeasureSpec.makeMeasureSpec(SCREEN_WIDTH, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(SCREEN_HEIGHT, View.MeasureSpec.EXACTLY);
        screen.measure(widthSpec, heightSpec);
        screen.layout(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // Draw to bitmap
        Bitmap bitmap = Bitmap.createBitmap(SCREEN_WIDTH, SCREEN_HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        screen.draw(canvas);

        return bitmap;
    }

    /**
     * Loads the reference image from the screenshots directory.
     * Only PNG format is supported.
     */
    private Bitmap loadReferenceImage() {
        try {
            File referenceFile = new File(OUTPUT_DIR, "interface-2-display_1original.png");
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
