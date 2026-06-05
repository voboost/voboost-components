package ru.voboost.components.demo.pixel;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

import ru.voboost.components.theme.Theme;

/**
 * Single parameterized pixel test for the consolidated MainActivity.
 *
 * One Screen per theme holds all demo content distributed across tabs; each shot
 * selects a tab, scrolls and renders. Tabs are pixel-validated only for the two
 * shots that compare from x=145 (set A: interface-2-display, set B: button-01);
 * other shots compare from x=485. Animations are disabled for deterministic frames.
 */
@RunWith(ParameterizedRobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = { 33 }, qualifiers = "w1920dp-h720dp-land-mdpi")
public class MainActivityTestVisual {

    private static final int SCREEN_WIDTH = 1920;
    private static final int SCREEN_HEIGHT = 720;
    private static final String OUTPUT_DIR =
            "java/ru/voboost/components/demo/pixel/MainActivity.screenshots";

    private static final class Shot {
        final String base;
        final String tab;
        final int tabScroll;
        final int panelScroll;
        final int startX;
        final int endX;
        final int startY;
        final int endY;
        final boolean popup;

        Shot(String base, String tab, int tabScroll, int panelScroll,
             int startX, int endX, int startY, int endY, boolean popup) {
            this.base = base;
            this.tab = tab;
            this.tabScroll = tabScroll;
            this.panelScroll = panelScroll;
            this.startX = startX;
            this.endX = endX;
            this.startY = startY;
            this.endY = endY;
            this.popup = popup;
        }
    }

    private static final List<Shot> SHOTS = Arrays.asList(
            new Shot("interface-2-display", "display",
                    0, 0, 145, SCREEN_WIDTH, 50, SCREEN_HEIGHT, false),
            new Shot("button-01", "system",
                    DemoRenderUtils.SCROLL_END, DemoRenderUtils.SCROLL_END, 145, SCREEN_WIDTH, 50, SCREEN_HEIGHT, false),
            new Shot("interface-2-display-checkbox", "voice",
                    0, 0, 485, SCREEN_WIDTH, 50, SCREEN_HEIGHT, false),
            new Shot("checkbox-01", "network",
                    0, 0, 485, SCREEN_WIDTH, 50, SCREEN_HEIGHT, false),
            new Shot("checkbox-02", "privacy",
                    0, 0, 485, SCREEN_WIDTH, 50, SCREEN_HEIGHT, false),
            new Shot("button-checkbox-radio", "device",
                    0, 0, 485, SCREEN_WIDTH, 50, SCREEN_HEIGHT, false),
            new Shot("section-info-short-01", "sound",
                    420, 0, 485, 1190, 50, SCREEN_HEIGHT, false),
            new Shot("section-info-short-02", "sound",
                    425, 0, 310, 1610, 50, 650, true)
    );

    @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
    public static Collection<Object[]> variants() {
        return Arrays.asList(new Object[][] {
                { "free-dark",     Theme.FREE_DARK     },
                { "free-light",    Theme.FREE_LIGHT    },
                { "dreamer-dark",  Theme.DREAMER_DARK  },
                { "dreamer-light", Theme.DREAMER_LIGHT },
        });
    }

    private final String themeSlug;
    private final Theme theme;

    private ActivityController<MainActivity> controller;
    private MainActivity activity;

    public MainActivityTestVisual(String themeSlug, Theme theme) {
        this.themeSlug = themeSlug;
        this.theme = theme;
    }

    @Before
    public void setUp() {
        Intent intent = new Intent();
        intent.putExtra(MainActivity.EXTRA_THEME, theme.getValue());
        controller = Robolectric.buildActivity(MainActivity.class, intent);
        controller.create().start().resume();
        activity = controller.get();
        activity.getScreen().setAnimationsEnabled(false);
    }

    @Test
    public void compareAllShots() throws Exception {
        List<AssertionError> failures = new ArrayList<>();
        for (Shot shot : SHOTS) {
            try {
                runShot(shot);
            } catch (AssertionError e) {
                failures.add(e);
            }
        }
        if (!failures.isEmpty()) {
            throw failures.get(0);
        }
    }

    private void runShot(Shot shot) throws Exception {
        activity.getScreen().getTabs().setSelectedValue(shot.tab, false);

        Bitmap actual;
        if (shot.popup) {
            activity.getSection2().showPopup();
            View overlay = activity.getSection2().getPopupOverlayView();
            actual = DemoRenderUtils.renderScreenWithPopupOverlay(
                    activity.getScreen(), overlay, SCREEN_WIDTH, SCREEN_HEIGHT,
                    shot.tabScroll, shot.panelScroll);
        } else {
            actual = DemoRenderUtils.renderScreen(
                    activity.getScreen(), SCREEN_WIDTH, SCREEN_HEIGHT,
                    shot.tabScroll, shot.panelScroll);
        }
        assertNotNull("Failed to render screen to bitmap", actual);

        String variantBase = shot.base + "-" + themeSlug;
        File actualFile = new File(OUTPUT_DIR, variantBase + "_2actual.png");
        PixelComparator.savePng(actual, actualFile);

        File reportFile = new File(OUTPUT_DIR, variantBase + ".txt");
        File referenceFile = new File(OUTPUT_DIR, variantBase + "_1original.png");
        if (!referenceFile.exists()) {
            writeMissingReference(reportFile, variantBase, referenceFile);
            return;
        }
        Bitmap reference = BitmapFactory.decodeFile(referenceFile.getAbsolutePath());

        PixelComparator.ComparisonResult result = PixelComparator.compare(
                actual, reference, shot.startX, shot.startY, shot.endX, shot.endY,
                DemoRenderUtils.PIXEL_TOLERANCE);

        PixelComparator.savePng(result.diffBitmap, new File(OUTPUT_DIR, variantBase + "_3diff.png"));
        PixelComparator.savePng(result.magentaBitmap, new File(OUTPUT_DIR, variantBase + "_4magenta.png"));

        PixelComparator.ExpectedDiff expected = PixelComparator.ExpectedDiff.load(variantBase);
        String comparisonMsg;
        boolean needsSave = (expected == null);
        AssertionError assertionError = null;
        if (expected != null) {
            try {
                comparisonMsg = expected.compareWithCurrent(variantBase, result);
            } catch (AssertionError e) {
                comparisonMsg = e.getMessage();
                assertionError = e;
            }
        } else {
            comparisonMsg = "No baseline (first run - saving current as baseline)\n";
        }
        if (needsSave) {
            new PixelComparator.ExpectedDiff(
                    result.matchPercentage, result.totalPixels, result.differentPixels)
                    .save(variantBase);
        }

        writeReport(reportFile, shot, variantBase, result, comparisonMsg);

        if (assertionError != null) {
            throw assertionError;
        }
    }

    private void writeReport(File reportFile, Shot shot, String variantBase,
            PixelComparator.ComparisonResult result, String comparisonMsg) throws Exception {
        try (FileWriter writer = new FileWriter(reportFile, false)) {
            writer.write("Pixel Comparison Report\n");
            writer.write("=======================\n\n");
            writer.write(result.toString() + "\n\n");
            writer.write("Compare area: x=[" + shot.startX + ".." + shot.endX
                    + "], y=[" + shot.startY + ".." + shot.endY + "]\n");
            writer.write("Tolerance: " + DemoRenderUtils.PIXEL_TOLERANCE + " per channel\n\n");
            writer.write("Files:\n");
            writer.write("  " + variantBase + "_1original.png  - original reference\n");
            writer.write("  " + variantBase + "_2actual.png    - our rendering\n");
            writer.write("  " + variantBase + "_3diff.png      - diff (semi-transparent magenta overlay)\n");
            writer.write("  " + variantBase + "_4magenta.png   - diff (pure magenta channel with transparent bg)\n\n");
            writer.write("=== BASELINE COMPARISON ===\n");
            writer.write(comparisonMsg);
            if (!comparisonMsg.endsWith("\n")) {
                writer.write("\n");
            }
        }
    }

    private void writeMissingReference(File reportFile, String variantBase, File referenceFile)
            throws Exception {
        try (FileWriter writer = new FileWriter(reportFile, false)) {
            writer.write("Pixel Comparison Report\n");
            writer.write("=======================\n\n");
            writer.write("Reference image not found: " + referenceFile.getName() + "\n");
            writer.write("Place reference image at: " + referenceFile.getAbsolutePath() + "\n");
            writer.write("Actual rendering saved as: " + variantBase + "_2actual.png\n");
        }
    }
}
