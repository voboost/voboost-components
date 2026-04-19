package ru.voboost.components.popup;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;

import java.io.IOException;
import java.io.InputStream;

import ru.voboost.components.theme.Theme;

/**
 * Color scheme for the Popup component.
 */
class PopupColors {
    public final int overlay;
    public final int panelBackground;
    public final android.graphics.drawable.Drawable panelBackgroundDrawable;

    public PopupColors(int overlay, int panelBackground) {
        this(overlay, panelBackground, null);
    }

    public PopupColors(int overlay, int panelBackground,
                       android.graphics.drawable.Drawable panelBackgroundDrawable) {
        this.overlay = overlay;
        this.panelBackground = panelBackground;
        this.panelBackgroundDrawable = panelBackgroundDrawable;
    }
}

/**
 * Dimension constants for the Popup component.
 * All values are in pixels (automotive requirement).
 */
class PopupDimensions {
    public static final float PANEL_WIDTH_PX = 1300f;
    public static final float PANEL_HEIGHT_PX = 580f;
    public static final float CORNER_RADIUS_PX = 20f;
    public static final float PADDING_PX = 0f;
    public static final int ANIMATION_DURATION = 300;
    public static final float SCALE_FROM = 0.8f;
    public static final float SCALE_TO = 1.0f;
}

/**
 * Gradient overlay colors — one gradient for all themes.
 *
 * <p>
 * Reproduces original dialog_window.xml from Voyah 6.11.1:
 * {@code startColor=#00000000, centerColor=#59000000, endColor=#83000000, centerX=0.3}
 *
 * <p>
 * The gradient is horizontal (left to right), shifted towards the left edge.
 * Standard Android dim is disabled in Popup (setDimAmount=0.0f).
 */
class PopupOverlayGradient {
    public static final int START_COLOR = Color.parseColor("#99000000");   // 60% black
    public static final int CENTER_COLOR = Color.parseColor("#99000000");  // 60% black
    public static final int END_COLOR = Color.parseColor("#99000000");     // 60% black

    /**
     * Creates the gradient drawable for overlay background.
     * Orientation: LEFT_RIGHT, centerX=0.3
     */
    public static GradientDrawable createOverlayDrawable() {
        GradientDrawable gradient = new GradientDrawable();
        gradient.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);
        gradient.setColors(new int[]{START_COLOR, CENTER_COLOR, END_COLOR});
        gradient.setGradientCenter(0.3f, 0.5f); // centerX=0.3 — shifted to left
        return gradient;
    }
}

/**
 * Predefined color schemes for all supported themes.
 */
class PopupColorSchemes {
    private static final java.util.Map<Theme, PopupColors> SCHEMES =
            new java.util.EnumMap<>(Theme.class);

    static {
        SCHEMES.put(Theme.FREE_DARK, new PopupColors(
                Color.parseColor("#83000000"),
                Color.parseColor("#23282e"),
                buildSolid(Color.parseColor("#23282e"))));

        SCHEMES.put(Theme.FREE_LIGHT, new PopupColors(
                Color.parseColor("#83000000"),
                Color.parseColor("#e8edf4"),
                loadPanelPng("Popup_panel_free_light.png")));

        SCHEMES.put(Theme.DREAMER_LIGHT, new PopupColors(
                Color.parseColor("#83000000"),
                Color.parseColor("#e8edf4"),
                loadPanelPng("Popup_theme_dreamer-light.png")));

        SCHEMES.put(Theme.DREAMER_DARK, new PopupColors(
                Color.parseColor("#83000000"),
                Color.parseColor("#1a1c24"),
                buildVerticalGradient(
                        Color.parseColor("#22252e"),
                        Color.parseColor("#101218"))));


    }

    private static BitmapDrawable loadPanelPng(String name) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled = false;
        try (InputStream in = PopupColorSchemes.class.getResourceAsStream(name)) {
            if (in == null) return null;
            Bitmap bmp = BitmapFactory.decodeStream(in, null, opts);
            if (bmp == null) return null;
            return new BitmapDrawable((android.content.res.Resources) null, bmp);
        } catch (IOException e) {
            return null;
        }
    }

    private static GradientDrawable buildVerticalGradient(int topColor, int bottomColor) {
        GradientDrawable d = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] { topColor, bottomColor });
        d.setCornerRadius(PopupDimensions.CORNER_RADIUS_PX);
        return d;
    }

    private static GradientDrawable buildSolid(int color) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(PopupDimensions.CORNER_RADIUS_PX);
        return d;
    }

    public static PopupColors get(Theme theme) {
        return SCHEMES.getOrDefault(theme, SCHEMES.get(Theme.FREE_DARK));
    }
}

/**
 * Unified theme management for the Popup component.
 */
public class PopupTheme {
    public static PopupColors getColors(Theme theme) {
        return PopupColorSchemes.get(theme);
    }
}
