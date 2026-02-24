package ru.voboost.components.demo.pixel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Color;

/**
 * Pixel-by-pixel image comparison utility.
 *
 * Compares two bitmaps and produces a diff image where:
 * - Matching pixels are shown dimmed (30-50% opacity)
 * - Different pixels are shown in MAGENTA (#ff00ff)
 *
 * Also calculates the match percentage.
 */
public class PixelComparator {

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

        public ComparisonResult(
                int totalPixels,
                int matchingPixels,
                int differentPixels,
                double matchPercentage,
                Bitmap diffBitmap) {
            this.totalPixels = totalPixels;
            this.matchingPixels = matchingPixels;
            this.differentPixels = differentPixels;
            this.matchPercentage = matchPercentage;
            this.diffBitmap = diffBitmap;
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
     * @param tolerance Per-channel tolerance (0=exact, 5=allow +-5 per R/G/B/A channel)
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

        // Fill areas OUTSIDE the comparison region with dimmed actual pixels
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x < startX || x >= endX || y < startY || y >= endY) {
                    int pixel = actual.getPixel(x, y);
                    diff.setPixel(x, y, dimPixel(pixel, 0.2f));
                }
            }
        }

        // Compare pixels INSIDE the region
        int totalPixels = 0;
        int matchingPixels = 0;
        int differentPixels = 0;

        int compareEndX = Math.min(endX, Math.min(actual.getWidth(), expected.getWidth()));
        int compareEndY = Math.min(endY, Math.min(actual.getHeight(), expected.getHeight()));

        for (int y = startY; y < compareEndY; y++) {
            for (int x = startX; x < compareEndX; x++) {
                totalPixels++;

                int actualPixel = actual.getPixel(x, y);
                int expectedPixel = expected.getPixel(x, y);

                if (pixelsMatch(actualPixel, expectedPixel, tolerance)) {
                    matchingPixels++;
                    diff.setPixel(x, y, dimPixel(actualPixel, 0.5f));
                } else {
                    differentPixels++;
                    diff.setPixel(x, y, Color.MAGENTA);
                }
            }
        }

        double matchPercentage = totalPixels > 0 ? (matchingPixels * 100.0 / totalPixels) : 0.0;

        return new ComparisonResult(
                totalPixels, matchingPixels, differentPixels, matchPercentage, diff);
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
        if (pixel1 == pixel2) return true;
        if (tolerance == 0) return false;

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
}
