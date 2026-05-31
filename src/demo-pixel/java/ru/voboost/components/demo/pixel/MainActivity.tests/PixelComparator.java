package ru.voboost.components.demo.pixel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Color;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Pixel-by-pixel image comparison utility.
 */
public class PixelComparator {

    /**
     * Represents a single pixel difference.
     */
    public static class DiffPixel {
        public final int x;
        public final int y;
        public final int actualColor;
        public final int expectedColor;
        public final int intensity; // The alpha of the magenta diff

        public DiffPixel(int x, int y, int actualColor, int expectedColor, int intensity) {
            this.x = x;
            this.y = y;
            this.actualColor = actualColor;
            this.expectedColor = expectedColor;
            this.intensity = intensity;
        }

        @Override
        public String toString() {
            return String.format("Pixel at (%d, %d): Actual=#%08X, Expected=#%08X, Intensity=%d",
                    x, y, actualColor, expectedColor, intensity);
        }
    }

    /**
     * Result of a pixel comparison.
     */
    public static class ComparisonResult {
        /** Total pixels compared */
        public final int totalPixels;

        /** Number of matching pixels */
        public final int matchingPixels;

        /** Number of different pixels */
        public final int differentPixels;

        /** Match percentage (0.0 to 100.0) */
        public final double matchPercentage;

        /** Diff bitmap */
        public final Bitmap diffBitmap;

        /** Magenta proportional bitmap */
        public final Bitmap magentaBitmap;

        /** List of all differing pixels */
        public final List<DiffPixel> diffPixels;

        public ComparisonResult(
                int totalPixels,
                int matchingPixels,
                int differentPixels,
                double matchPercentage,
                Bitmap diffBitmap,
                Bitmap magentaBitmap,
                List<DiffPixel> diffPixels) {
            this.totalPixels = totalPixels;
            this.matchingPixels = matchingPixels;
            this.differentPixels = differentPixels;
            this.matchPercentage = matchPercentage;
            this.diffBitmap = diffBitmap;
            this.magentaBitmap = magentaBitmap;
            this.diffPixels = diffPixels;
        }

        @Override
        public String toString() {
            return String.format(
                    "PixelComparison: %.2f%% match (%d/%d pixels, %d different)",
                    matchPercentage, matchingPixels, totalPixels, differentPixels);
        }
    }

    /**
     * Compares two bitmaps pixel-by-pixel within a specified region.
     *
     * @param actual    Our rendered bitmap
     * @param expected  The reference bitmap
     * @param startX    Left boundary of comparison region (inclusive)
     * @param startY    Top boundary of comparison region (inclusive)
     * @param endX      Right boundary of comparison region (exclusive)
     * @param endY      Bottom boundary of comparison region (exclusive)
     * @param tolerance Per-channel tolerance (0=exact, 5=allow +-5 per R/G/B/A
     *                  channel)
     * @return ComparisonResult with statistics and diff image
     */
    public static ComparisonResult compare(
            Bitmap actual,
            Bitmap expected,
            int startX,
            int startY,
            int endX,
            int endY,
            int tolerance) {

        int width = actual.getWidth();
        int height = actual.getHeight();
        Bitmap diff = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Bitmap magentaOnly = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // Fill areas OUTSIDE the comparison region with dimmed actual pixels
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x < startX || x >= endX || y < startY || y >= endY) {
                    int pixel = actual.getPixel(x, y);
                    diff.setPixel(x, y, dimPixel(pixel, 0.2f));
                    magentaOnly.setPixel(x, y, Color.TRANSPARENT);
                }
            }
        }

        // Compare pixels INSIDE the region
        int totalPixels = 0;
        int matchingPixels = 0;
        int differentPixels = 0;

        int compareEndX = Math.min(endX, Math.min(actual.getWidth(), expected.getWidth()));
        int compareEndY = Math.min(endY, Math.min(actual.getHeight(), expected.getHeight()));

        comparePixels:
        for (int y = startY; y < compareEndY; y++) {
            for (int x = startX; x < compareEndX; x++) {
                totalPixels++;

                int actualPixel = actual.getPixel(x, y);
                int expectedPixel = expected.getPixel(x, y);

                if (pixelsMatch(actualPixel, expectedPixel, tolerance)) {
                    matchingPixels++;
                    diff.setPixel(x, y, dimPixel(actualPixel, 0.5f));
                    magentaOnly.setPixel(x, y, Color.TRANSPARENT);
                } else {
                    differentPixels++;
                    // Calculate proportional difference
                    int dr = Math.abs(Color.red(actualPixel) - Color.red(expectedPixel));
                    int dg = Math.abs(Color.green(actualPixel) - Color.green(expectedPixel));
                    int db = Math.abs(Color.blue(actualPixel) - Color.blue(expectedPixel));
                    int maxDiff = Math.max(dr, Math.max(dg, db));

                    // Map diff [tolerance+1, 255] to alpha [50, 255] for visibility
                    // To make small differences visible, we linearly interpolate from roughly 10
                    // offset to 255 offset
                    // Using direct distance to prevent hard thresholds
                    int diffAlpha = (int) (maxDiff * (255.0 / 255.0)); // Could apply nonlinear mapping if requested.
                    // Scale it so that minor diffs (which were > tolerance) show up
                    int baseAlpha = 50;
                    float factor = maxDiff / 255f;
                    int alpha = baseAlpha + (int) ((255 - baseAlpha) * factor * 2); // magnify slightly
                    alpha = Math.min(255, Math.max(0, alpha));

                    int magentaColor = Color.argb(alpha, 255, 0, 255);
                    magentaOnly.setPixel(x, y, magentaColor);

                    // For diff bitmap, also blend the actual dimmed pixel with magenta
                    int dimmedActual = dimPixel(actualPixel, 0.5f);
                    diff.setPixel(x, y, blendColors(dimmedActual, magentaColor));
                }
            }
        }

        double matchPercentage = totalPixels > 0 ? (matchingPixels * 100.0 / totalPixels) : 0.0;

        // Fill area to the right of expected bitmap with dimmed actual pixels
        if (compareEndX < width) {
            for (int y = startY; y < compareEndY; y++) {
                for (int x = compareEndX; x < width; x++) {
                    int pixel = actual.getPixel(x, y);
                    diff.setPixel(x, y, dimPixel(pixel, 0.5f));
                    magentaOnly.setPixel(x, y, Color.TRANSPARENT);
                }
            }
        }

        return new ComparisonResult(
                totalPixels, matchingPixels, differentPixels, matchPercentage, diff, magentaOnly, Collections.emptyList());
    }

    /**
     * Saves a bitmap to a PNG file.
     *
     * @param bitmap     The bitmap to save
     * @param outputFile The output file path
     */
    public static void savePng(Bitmap bitmap, File outputFile) throws IOException {
        if (outputFile.getParentFile() != null) {
            outputFile.getParentFile().mkdirs();
        }
        try (FileOutputStream out = new FileOutputStream(outputFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        }
    }

    // ================================================================
    // PRIVATE HELPERS
    // ================================================================

    private static boolean pixelsMatch(int pixel1, int pixel2, int tolerance) {
        if (pixel1 == pixel2)
            return true;
        if (tolerance == 0)
            return false;

        int dr = Math.abs(Color.red(pixel1) - Color.red(pixel2));
        int dg = Math.abs(Color.green(pixel1) - Color.green(pixel2));
        int db = Math.abs(Color.blue(pixel1) - Color.blue(pixel2));
        int da = Math.abs(Color.alpha(pixel1) - Color.alpha(pixel2));

        return dr <= tolerance && dg <= tolerance && db <= tolerance && da <= tolerance;
    }

    private static int dimPixel(int pixel, float factor) {
        int r = (int) (Color.red(pixel) * factor);
        int g = (int) (Color.green(pixel) * factor);
        int b = (int) (Color.blue(pixel) * factor);
        return Color.argb(255, r, g, b);
    }

    /** Blends a foreground color with alpha over an opaque background color */
    private static int blendColors(int bg, int fg) {
        float alpha = Color.alpha(fg) / 255f;
        int r = (int) (Color.red(fg) * alpha + Color.red(bg) * (1 - alpha));
        int g = (int) (Color.green(fg) * alpha + Color.green(bg) * (1 - alpha));
        int b = (int) (Color.blue(fg) * alpha + Color.blue(bg) * (1 - alpha));
        return Color.argb(255, r, g, b);
    }

    // ================================================================
    // EXPECTED DIFF TRACKING
    // ================================================================

    /**
     * Expected diff values for each test.
     * These are the baseline values that should not get worse.
     */
    public static class ExpectedDiff {
        public final double matchPercent;
        public final int totalPixels;
        public final int differentPixels;

        public ExpectedDiff(double matchPercent, int totalPixels, int differentPixels) {
            this.matchPercent = matchPercent;
            this.totalPixels = totalPixels;
            this.differentPixels = differentPixels;
        }

        /**
         * Loads expected diff from JSON file.
         * Returns null if file doesn't exist (first run).
         */
        public static ExpectedDiff load(String testName) {
            try {
                File file = new File("java/ru/voboost/components/demo/pixel/MainActivity.screenshots/expected-diff.json");
                if (!file.exists()) {
                    return null;
                }
                String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
                JSONObject json = new JSONObject(new JSONTokener(content));
                if (!json.has(testName)) {
                    return null;
                }
                JSONObject test = json.getJSONObject(testName);
                return new ExpectedDiff(
                    test.getDouble("matchPercent"),
                    test.getInt("totalPixels"),
                    test.getInt("differentPixels")
                );
            } catch (Exception e) {
                System.out.println("Warning: Could not load expected diff for " + testName + ": " + e.getMessage());
                return null;
            }
        }

        /**
         * Compares current result with expected and returns status message.
         * Throws AssertionError if diff got worse.
         */
        public String compareWithCurrent(String testName, ComparisonResult current) {
            if (this == null) {
                return "No baseline (first run - saving current as baseline)";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Diff comparison for ").append(testName).append(":\n");

            // Check if total pixels match (test setup might have changed)
            if (this.totalPixels != current.totalPixels) {
                sb.append(String.format("  WARNING: Total pixels changed! Expected %d, got %d\n",
                    this.totalPixels, current.totalPixels));
            }

            // Check if match percentage got worse
            double matchDiff = current.matchPercentage - this.matchPercent;
            int pixelDiff = current.differentPixels - this.differentPixels;

            if (matchDiff < 0) {
                sb.append(String.format("  FAIL: Match got WORSE! Was %.2f%%, now %.2f%% (%.2f%% change, %d more diff pixels)\n",
                    this.matchPercent, current.matchPercentage, matchDiff, pixelDiff));
                throw new AssertionError(sb.toString());
            } else if (matchDiff > 0) {
                sb.append(String.format("  PASS: Match IMPROVED! Was %.2f%%, now %.2f%% (%.2f%% change, %d fewer diff pixels)\n",
                    this.matchPercent, current.matchPercentage, matchDiff, -pixelDiff));
            } else {
                sb.append(String.format("  PASS: Match unchanged at %.2f%% (%d diff pixels)\n",
                    current.matchPercentage, current.differentPixels));
            }

            return sb.toString();
        }

        /**
         * Saves this expected diff as baseline for the test.
         */
        public void save(String testName) throws IOException, JSONException {
            File file = new File("java/ru/voboost/components/demo/pixel/MainActivity.screenshots/expected-diff.json");
            JSONObject json;

            if (file.exists()) {
                try {
                    String content = new String(Files.readAllBytes(Paths.get(file.getPath())));
                    json = new JSONObject(new JSONTokener(content));
                } catch (Exception e) {
                    json = new JSONObject();
                }
            } else {
                json = new JSONObject();
            }

            JSONObject testData = new JSONObject();
            testData.put("matchPercent", matchPercent);
            testData.put("totalPixels", totalPixels);
            testData.put("differentPixels", differentPixels);
            json.put(testName, testData);

            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(json.toString(2));
            }
        }
    }
}
