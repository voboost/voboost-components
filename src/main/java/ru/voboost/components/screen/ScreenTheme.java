package ru.voboost.components.screen;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import ru.voboost.components.theme.Theme;

/**
 * Theme constants for the Screen component.
 *
 * <p>Contains all visual styling parameters including:
 * <ul>
 *   <li>Dimensions (in pixels)</li>
 *   <li>Colors for each theme variant</li>
 * </ul>
 *
 * <p>All dimensions are in PIXELS, not dp. This is a critical automotive requirement.
 */
public final class ScreenTheme {

    private ScreenTheme() {
        // Prevent instantiation
    }

    // ============================================================
    // DIMENSIONS (all values in PIXELS)
    // ============================================================

    /** Padding inside the screen */
    public static final int PADDING = 32;

    /**
     * Available content height in pixels (the vertical space the Screen lays
     * out Tabs and Panels into). Fixed for the target automotive screen.
     * Exposed so host apps can size full-height children (e.g. a diagnostic
     * section that must fill the panel viewport) without relying on
     * DisplayMetrics, which can differ from this fixed design height on real
     * devices.
     */
    public static final int AVAILABLE_HEIGHT = 720;

    /** Default horizontal offset for screen content */
    public static final int DEFAULT_OFFSET_X = 145;

    /** Default vertical offset for screen content */
    public static final int DEFAULT_OFFSET_Y = 50;

    /** Default horizontal gap between Tabs and Panel */
    public static final int DEFAULT_GAP_X = 0;

    /** Panel width in compact mode */
    public static final int PANEL_WIDTH = 705;

    // ============================================================
    // COLORS - FREE LIGHT THEME
    // ============================================================

    /** Background color for the screen - Free Light */
    public static final int FREE_LIGHT_BACKGROUND = Color.parseColor("#ffffff");

    // ============================================================
    // COLORS - FREE DARK THEME
    // ============================================================

    /** Background color for the screen - Free Dark */
    public static final int FREE_DARK_BACKGROUND = Color.parseColor("#000000");

    // ============================================================
    // COLORS - DREAMER LIGHT THEME
    // ============================================================

    /** Background color for the screen - Dreamer Light */
    public static final int DREAMER_LIGHT_BACKGROUND = Color.parseColor("#fafafa");

    // ============================================================
    // COLORS - DREAMER DARK THEME
    // ============================================================

    /** Background color for the screen - Dreamer Dark */
    public static final int DREAMER_DARK_BACKGROUND = Color.parseColor("#000000");

    // ============================================================
    // ANIMATION CONSTANTS
    // ============================================================

    /** Duration of panel transition animation in milliseconds */
    public static final int PANEL_TRANSITION_DURATION = 300;

    // ============================================================
    // COLOR GETTERS
    // ============================================================

    private static volatile Bitmap lightBackgroundBitmap;

    public static Bitmap getLightBackgroundBitmap() {
        Bitmap b = lightBackgroundBitmap;
        if (b == null) {
            synchronized (ScreenTheme.class) {
                b = lightBackgroundBitmap;
                if (b == null) {
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inScaled = false;
                    try (java.io.InputStream in = ScreenTheme.class
                            .getResourceAsStream("Screen_theme_light.png")) {
                        if (in != null) {
                            b = BitmapFactory.decodeStream(in, null, opts);
                        }
                    } catch (Exception ignored) {
                    }
                    lightBackgroundBitmap = b;
                }
            }
        }
        return b;
    }

    public static Drawable getBackgroundDrawable(android.content.Context context, Theme theme) {
        if (theme == Theme.FREE_LIGHT || theme == Theme.DREAMER_LIGHT) {
            Bitmap bmp = getLightBackgroundBitmap();
            if (bmp != null) {
                return new BitmapDrawable(context.getResources(), bmp);
            }
        }
        return new ColorDrawable(getBackgroundColor(theme));
    }

    private static int getBackgroundColor(Theme theme) {
        switch (theme) {
            case FREE_DARK:
                return FREE_DARK_BACKGROUND;
            case DREAMER_DARK:
                return DREAMER_DARK_BACKGROUND;
            default:
                return FREE_DARK_BACKGROUND;
        }
    }

    /**
     * Returns the background color for the specified theme.
     *
     * @param theme the current theme
     * @return the background color
     */
    public static int getBackground(Theme theme) {
        switch (theme) {
            case FREE_LIGHT:
                return FREE_LIGHT_BACKGROUND;
            case FREE_DARK:
                return FREE_DARK_BACKGROUND;
            case DREAMER_LIGHT:
                return DREAMER_LIGHT_BACKGROUND;
            case DREAMER_DARK:
                return DREAMER_DARK_BACKGROUND;
            default:
                return FREE_LIGHT_BACKGROUND;
        }
    }
}

