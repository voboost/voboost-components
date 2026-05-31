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
 * Pixel comparison test for button-checkbox-radio screenshot.
 *
 * RUN:
 * ./gradlew :demo-pixel:testDebugUnitTest --tests="*ButtonCheckboxRadioTestVisual*"
 */
@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = { 33 }, qualifiers = "w1920dp-h720dp-land-mdpi")
public class ButtonCheckboxRadioTestVisual {

    private static final int SCREEN_WIDTH = 1920;
    private static final int SCREEN_HEIGHT = 720;
    private static final int COMPARE_START_X = 145;
    private static final int COMPARE_START_Y = 50;
    private static final String OUTPUT_DIR = "java/ru/voboost/components/demo/pixel/MainActivity.screenshots";

    private ActivityController<ButtonCheckboxRadio> controller;
    private ButtonCheckboxRadio activity;

    @Before
    public void setUp() {
        controller = Robolectric.buildActivity(ButtonCheckboxRadio.class);
        controller.create().start().resume();
        activity = controller.get();
    }

    @Test
    public void compareWithReferenceScreenshot() throws Exception {
        String variant = System.getProperty("variant", "");
        String suffix = variant.isEmpty() ? "" : "_" + variant;

        Bitmap actual = renderScreenToBitmap();
        assertNotNull("Failed to render screen to bitmap", actual);

        File actualFile = new File(OUTPUT_DIR, "button-checkbox-radio_2actual" + suffix + ".png");
        PixelComparator.savePng(actual, actualFile);
        System.out.println("Saved actual rendering: " + actualFile.getAbsolutePath());
        System.out.println("Actual size: " + actual.getWidth() + "x" + actual.getHeight());

        Bitmap reference = loadReferenceImage();
        if (reference == null) {
            System.out.println("");
            System.out.println("=== WARNING: Reference image not found ===");
            System.out.println("Looked for: /button-checkbox-radio_1original.png");
            System.out.println("");
            System.out.println("Place your reference image at:");
            System.out.println(
                    "  src/demo-pixel/java/ru/voboost/components/demo/pixel/"
                            + "MainActivity.screenshots/button-checkbox-radio_1original.png");
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

        File diffFile = new File(OUTPUT_DIR, "button-checkbox-radio_3diff" + suffix + ".png");
        PixelComparator.savePng(result.diffBitmap, diffFile);
        File magentaFile = new File(OUTPUT_DIR, "button-checkbox-radio_4magenta" + suffix + ".png");
        PixelComparator.savePng(result.magentaBitmap, magentaFile);

        // Compare with expected diff
        String testName = "button-checkbox-radio";
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
        System.out.println("");
        System.out.println("=== BASELINE COMPARISON ===");
        System.out.print(comparisonMsg);
        System.out.println("");

        // Save baseline if first run
        if (needsSave) {
            new PixelComparator.ExpectedDiff(result.matchPercentage, result.totalPixels, result.differentPixels)
                .save(testName);
            System.out.println("Baseline saved for: " + testName);
        }

        // Save text report (always, even on error) - after baseline save
        File reportFile = new File(OUTPUT_DIR, "button-checkbox-radio" + suffix + ".txt");
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
            writer.write("  button-checkbox-radio_1original.png  - original reference\n");
            writer.write("  button-checkbox-radio_2actual.png    - our rendering\n");
            writer.write("  button-checkbox-radio_3diff.png      - diff (semi-transparent magenta overlay)\n");
            writer.write(
                    "  button-checkbox-radio_4magenta.png   - diff (pure magenta channel with transparent bg)\n\n");
            writer.write("How to read the diff:\n");
            writer.write("  Magenta pixels = our rendering differs from reference\n");
            writer.write("  Dimmed pixels  = our rendering matches the reference\n");
            writer.write("  Dark area      = system UI zone, excluded from comparison\n\n");
            writer.write("=== BASELINE COMPARISON ===\n");
            writer.write(comparisonMsg);

        }
        System.out.println("Report saved: " + reportFile.getAbsolutePath());

        // Throw exception after saving report
        if (assertionError != null) {
            throw assertionError;
        }
    }

    private Bitmap renderScreenToBitmap() {
        return DemoRenderUtils.renderScreen(activity.getScreen(), SCREEN_WIDTH, SCREEN_HEIGHT, 10, 0);
    }

    private Bitmap loadReferenceImage() {
        try {
            File referenceFile = new File(OUTPUT_DIR, "button-checkbox-radio_1original.png");
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
