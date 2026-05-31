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
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.GraphicsMode;

import ru.voboost.components.theme.Theme;

@RunWith(ParameterizedRobolectricTestRunner.class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = { 33 }, qualifiers = "w1920dp-h720dp-land-mdpi")
public class SectionInfoShortTestVisual {

    private static final int SCREEN_WIDTH = 1920;
    private static final int SCREEN_HEIGHT = 720;
    private static final String OUTPUT_DIR =
            "java/ru/voboost/components/demo/pixel/MainActivity.screenshots";
    private static final String BASE_NAME = "section-info-short";

    /**
     * Describes a single capture-and-compare step within a test scenario.
     * Each step has its own slug (used in screenshot filenames), compare area,
     * preparation action (mutating the Activity) and capture function.
     */
    private interface Step {
        String slug();
        int compareStartX();
        int compareEndX();
        int compareStartY();
        int compareEndY();
        void prepare(SectionInfoShort activity);
        Bitmap capture(SectionInfoShort activity);
    }

    private static final List<Step> STEPS = Arrays.asList(
            new Step() {
                public String slug() { return "01"; }
                public int compareStartX() { return 145; }
                public int compareEndX() { return 1190; }
                public int compareStartY() { return 50; }
                public int compareEndY() { return 720; }
                public void prepare(SectionInfoShort activity) {
                    // initial state — nothing to mutate
                }
                public Bitmap capture(SectionInfoShort activity) {
                    return DemoRenderUtils.renderScreen(
                            activity.getScreen(), SCREEN_WIDTH, SCREEN_HEIGHT,
                            DemoRenderUtils.SCROLL_END, 0);
                }
            },
            new Step() {
                public String slug() { return "02"; }
                public int compareStartX() { return 145; }
                public int compareEndX() { return SCREEN_WIDTH - 310; }
                public int compareStartY() { return 50; }
                public int compareEndY() { return SCREEN_HEIGHT - 70; }
                public void prepare(SectionInfoShort activity) {
                    activity.getSection2().showPopup();
                }
                public Bitmap capture(SectionInfoShort activity) {
                    View overlay = activity.getSection2().getPopupOverlayView();
                    return DemoRenderUtils.renderScreenWithPopupOverlay(
                            activity.getScreen(), overlay, SCREEN_WIDTH, SCREEN_HEIGHT,
                            DemoRenderUtils.SCROLL_END, 0);
                }
            }
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

    private final String variantSlug;
    private final Theme theme;

    private ActivityController<SectionInfoShort> controller;
    private SectionInfoShort activity;

    public SectionInfoShortTestVisual(String variantSlug, Theme theme) {
        this.variantSlug = variantSlug;
        this.theme = theme;
    }

    @BeforeClass
    public static void resetReports() throws Exception {
        for (Step step : STEPS) {
            File reportFile = new File(OUTPUT_DIR, BASE_NAME + "-" + step.slug() + ".txt");
            File parent = reportFile.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }
            if (reportFile.exists()) {
                continue;
            }
            try (FileWriter writer = new FileWriter(reportFile, false)) {
                writer.write("Pixel Comparison Report\n");
                writer.write("=======================\n\n");
                writer.write("Test: " + BASE_NAME + "-" + step.slug() + " (theme variants)\n");
                writer.write("Compare area: x=[" + step.compareStartX() + ".." + step.compareEndX()
                        + "], y=[" + step.compareStartY() + ".." + step.compareEndY() + "]\n");
                writer.write("Tolerance: " + DemoRenderUtils.PIXEL_TOLERANCE + " per channel\n\n");
            }
        }
    }

    @Before
    public void setUp() {
        Intent intent = new Intent();
        intent.putExtra(SectionInfoShort.EXTRA_THEME, theme.getValue());
        controller = Robolectric.buildActivity(SectionInfoShort.class, intent);
        controller.create().start().resume();
        activity = controller.get();
    }

    @Test
    public void compareAllSteps() throws Exception {
        List<AssertionError> failures = new ArrayList<>();
        for (Step step : STEPS) {
            try {
                runStep(step);
            } catch (AssertionError e) {
                failures.add(e);
            }
        }
        if (!failures.isEmpty()) {
            throw failures.get(0);
        }
    }

    private void runStep(Step step) throws Exception {
        step.prepare(activity);

        String variantBase = BASE_NAME + "-" + step.slug() + "-" + variantSlug;
        File reportFile = new File(OUTPUT_DIR, BASE_NAME + "-" + step.slug() + ".txt");

        Bitmap actual = step.capture(activity);
        assertNotNull("Failed to render screen to bitmap", actual);

        File actualFile = new File(OUTPUT_DIR, variantBase + "_2actual.png");
        PixelComparator.savePng(actual, actualFile);

        File referenceFile = new File(OUTPUT_DIR, variantBase + "_1original.png");
        Bitmap reference = referenceFile.exists()
                ? BitmapFactory.decodeFile(referenceFile.getAbsolutePath())
                : null;

        if (reference == null) {
            appendMissingReferenceSection(reportFile, variantBase, referenceFile);
            return;
        }

        PixelComparator.ComparisonResult result = PixelComparator.compare(
                actual,
                reference,
                step.compareStartX(),
                step.compareStartY(),
                step.compareEndX(),
                step.compareEndY(),
                DemoRenderUtils.PIXEL_TOLERANCE);

        File diffFile = new File(OUTPUT_DIR, variantBase + "_3diff.png");
        PixelComparator.savePng(result.diffBitmap, diffFile);
        File magentaFile = new File(OUTPUT_DIR, variantBase + "_4magenta.png");
        PixelComparator.savePng(result.magentaBitmap, magentaFile);

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

        appendComparisonSection(reportFile, step, variantBase, result, comparisonMsg);

        if (assertionError != null) {
            throw assertionError;
        }
    }

    private void appendComparisonSection(
            File reportFile,
            Step step,
            String variantBase,
            PixelComparator.ComparisonResult result,
            String comparisonMsg) throws Exception {
        try (FileWriter writer = new FileWriter(reportFile, true)) {
            writer.write("=== VARIANT: " + variantSlug + " ===\n");
            writer.write(result.toString() + "\n\n");
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
            writer.write("\n");
        }
    }

    private void appendMissingReferenceSection(File reportFile, String variantBase, File referenceFile)
            throws Exception {
        try (FileWriter writer = new FileWriter(reportFile, true)) {
            writer.write("=== VARIANT: " + variantSlug + " ===\n");
            writer.write("Reference image not found: " + referenceFile.getName() + "\n");
            writer.write("Place reference image at: " + referenceFile.getAbsolutePath() + "\n");
            writer.write("Actual rendering saved as: " + variantBase + "_2actual.png\n\n");
        }
    }
}
