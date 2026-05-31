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
 * Pixel comparison test for checkbox-01 screenshot.
 *
 * RUN:
 * ./gradlew :demo-pixel:testDebugUnitTest --tests="*Checkbox01TestVisual*"
 */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = { 33 }, qualifiers = "w1920dp-h720dp-land-mdpi")
public class Checkbox01TestVisual {

    private static final int SCREEN_WIDTH = 1920;
    private static final int SCREEN_HEIGHT = 720;
    private static final int COMPARE_START_X = 485;
    private static final int COMPARE_START_Y = 50;
    private static final String OUTPUT_DIR = "java/ru/voboost/components/demo/pixel/MainActivity.screenshots";

    private ActivityController<Checkbox01> controller;
    private Checkbox01 activity;

    @Before
    public void setUp() {
        controller = Robolectric.buildActivity(Checkbox01.class);
        controller.create().start().resume();
        activity = controller.get();
    }

    @Test
    public void compareWithReferenceScreenshot() throws Exception {
        String variant = System.getProperty("variant", "");
        String suffix = variant.isEmpty() ? "" : "_" + variant;

        Bitmap actual = renderScreenToBitmap();
        assertNotNull("Failed to render screen to bitmap", actual);

        File actualFile = new File(OUTPUT_DIR, "checkbox-01_2actual" + suffix + ".png");
        PixelComparator.savePng(actual, actualFile);
        System.out.println("Saved actual rendering: " + actualFile.getAbsolutePath());
        System.out.println("Actual size: " + actual.getWidth() + "x" + actual.getHeight());

        Bitmap reference = loadReferenceImage();
        if (reference == null) {
            System.out.println("");
            System.out.println("=== WARNING: Reference image not found ===");
            System.out.println("Looked for: /checkbox-01_1original.png");
            System.out.println("");
            System.out.println("Place your reference image at:");
            System.out.println(
                    "  src/demo-pixel/java/ru/voboost/components/demo/pixel/"
                            + "MainActivity.screenshots/checkbox-01_1original.png");
            System.out.println("");
            System.out.println("Skipping comparison. Actual rendering saved for inspection.");
            return;
        }

        System.out.println("Reference size: " + reference.getWidth() + "x" + reference.getHeight());

        PixelComparator.ComparisonResult result = PixelComparator.compare(
                actual,
                reference,
                COMPARE_START_X,
                COMPARE_START_Y,
                SCREEN_WIDTH,
                SCREEN_HEIGHT,
                DemoRenderUtils.PIXEL_TOLERANCE);

        File diffFile = new File(OUTPUT_DIR, "checkbox-01_3diff" + suffix + ".png");
        PixelComparator.savePng(result.diffBitmap, diffFile);
        File magentaFile = new File(OUTPUT_DIR, "checkbox-01_4magenta" + suffix + ".png");
        PixelComparator.savePng(result.magentaBitmap, magentaFile);

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
        String testName = "checkbox-01";
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
        File reportFile = new File(OUTPUT_DIR, "checkbox-01" + suffix + ".txt");
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
            writer.write("  checkbox-01_1original.png  - original reference\n");
            writer.write("  checkbox-01_2actual.png    - our rendering\n");
            writer.write("  checkbox-01_3diff.png      - diff (semi-transparent magenta overlay)\n");
            writer.write(
                    "  checkbox-01_4magenta.png   - diff (pure magenta channel with transparent bg)\n\n");
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

    private Bitmap renderScreenToBitmap() {
        View screen = activity.getScreen();
        if (screen == null)
            return null;

        int widthSpec = View.MeasureSpec.makeMeasureSpec(SCREEN_WIDTH, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(SCREEN_HEIGHT, View.MeasureSpec.EXACTLY);
        screen.measure(widthSpec, heightSpec);
        screen.layout(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        Bitmap bitmap = Bitmap.createBitmap(SCREEN_WIDTH, SCREEN_HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        screen.draw(canvas);

        return bitmap;
    }

    private Bitmap loadReferenceImage() {
        try {
            File referenceFile = new File(OUTPUT_DIR, "checkbox-01_1original.png");
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
