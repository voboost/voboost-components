package ru.voboost.components.demo.pixel;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.widget.ScrollView;

import ru.voboost.components.panel.Panel;
import ru.voboost.components.screen.Screen;
import ru.voboost.components.screen.ScreenTheme;

/**
 * Shared rendering utilities for pixel-perfect demo tests.
 */
public final class DemoRenderUtils {

    /** Scroll to the very end. */
    public static final int SCROLL_END = -1;

    /** Per-channel pixel tolerance for all visual comparison tests. */
    public static final int PIXEL_TOLERANCE = 5;

    private static final Paint bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    private DemoRenderUtils() {}

    /**
     * Renders a Screen component to a bitmap with specified scroll offsets.
     *
     * @param tabsScrollY  scroll Y for tabs ScrollView, {@link #SCROLL_END} for max, 0 for no scroll
     * @param panelScrollY scroll Y for panel ScrollView, {@link #SCROLL_END} for max, 0 for no scroll
     */
    public static Bitmap renderScreen(Screen screen, int width, int height,
                                      int tabsScrollY, int panelScrollY) {
        if (screen == null) return null;

        int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        screen.measure(widthSpec, heightSpec);
        screen.layout(0, 0, width, height);

        scrollTo(screen.getTabsScrollView(), tabsScrollY);

        if (panelScrollY != 0) {
            Panel activePanel = screen.getActivePanel();
            if (activePanel != null && activePanel.getParent() instanceof ScrollView) {
                scrollTo((ScrollView) activePanel.getParent(), panelScrollY);
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        screen.draw(canvas);

        return bitmap;
    }

    /**
     * Renders the Screen, then composes a popup overlay view on top.
     * Used by tests that need to capture a Dialog-backed popup which is
     * not part of the regular Activity view hierarchy.
     */
    public static Bitmap renderScreenWithPopupOverlay(Screen screen, View overlay,
                                                      int width, int height,
                                                      int tabsScrollY, int panelScrollY) {
        Bitmap bitmap = renderScreen(screen, width, height, tabsScrollY, panelScrollY);
        if (bitmap == null || overlay == null) {
            return bitmap;
        }

        int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        overlay.measure(widthSpec, heightSpec);
        overlay.layout(0, 0, width, height);

        Canvas canvas = new Canvas(bitmap);
        overlay.draw(canvas);
        return bitmap;
    }

    private static void scrollTo(ScrollView scrollView, int scrollY) {
        if (scrollView == null || scrollView.getChildCount() == 0) return;

        scrollView.setVerticalScrollBarEnabled(false);

        if (scrollY == SCROLL_END) {
            View child = scrollView.getChildAt(0);
            int viewportHeight = scrollView.getMeasuredHeight()
                    - scrollView.getPaddingTop() - scrollView.getPaddingBottom();
            scrollY = Math.max(0, child.getMeasuredHeight() - viewportHeight);
        }

        setScrollYForce(scrollView, scrollY);
    }

    private static void setScrollYForce(ScrollView scrollView, int scrollY) {
        scrollView.scrollTo(0, scrollY);
    }
}
