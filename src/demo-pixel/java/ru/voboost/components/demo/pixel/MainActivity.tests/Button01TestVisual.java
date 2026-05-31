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

@RunWith(RobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = { 33 }, qualifiers = "w1920dp-h720dp-land-mdpi")
public class Button01TestVisual {

    private static final int SCREEN_WIDTH = 1920;
    private static final int SCREEN_HEIGHT = 720;
    private static final int COMPARE_START_X = 145;
    private static final int COMPARE_START_Y = 50;
    private static final String OUTPUT_DIR = "java/ru/voboost/components/demo/pixel/MainActivity.screenshots";

    private ActivityController<Button01> controller;
    private Button01 activity;

    @Before
    public void setUp() {
        controller = Robolectric.buildActivity(Button01.class);
        controller.create().start().resume();
        activity = controller.get();
    }

    @Test
    public void compareWithReferenceScreenshot() throws Exception {
        Bitmap actual = renderScreenToBitmap();
        assertNotNull("Failed to render screen to bitmap", actual);

        File actualFile = new File(OUTPUT_DIR, "button-01_2actual.png");
        PixelComparator.savePng(actual, actualFile);

        Bitmap reference = loadReferenceImage();
        if (reference == null) {
            System.out.println("Reference image not found. Skipping comparison.");
            return;
        }

        PixelComparator.ComparisonResult result = PixelComparator.compare(
                actual, reference, COMPARE_START_X, COMPARE_START_Y,
                SCREEN_WIDTH, SCREEN_HEIGHT, DemoRenderUtils.PIXEL_TOLERANCE);

        File diffFile = new File(OUTPUT_DIR, "button-01_3diff.png");
        PixelComparator.savePng(result.diffBitmap, diffFile);

        String testName = "button-01";
        PixelComparator.ExpectedDiff expected = PixelComparator.ExpectedDiff.load(testName);
        if (expected == null) {
            new PixelComparator.ExpectedDiff(result.matchPercentage, result.totalPixels, result.differentPixels)
                .save(testName);
        } else {
            expected.compareWithCurrent(testName, result);
        }

        File reportFile = new File(OUTPUT_DIR, "button-01.txt");
        try (FileWriter writer = new FileWriter(reportFile)) {
            writer.write(result.toString());
        }
    }

    private Bitmap renderScreenToBitmap() {
        return DemoRenderUtils.renderScreen(activity.getScreen(), SCREEN_WIDTH, SCREEN_HEIGHT,
                DemoRenderUtils.SCROLL_END, DemoRenderUtils.SCROLL_END);
    }

    private Bitmap loadReferenceImage() {
        File referenceFile = new File(OUTPUT_DIR, "button-01_1original.png");
        if (!referenceFile.exists()) return null;
        return BitmapFactory.decodeFile(referenceFile.getAbsolutePath());
    }
}
