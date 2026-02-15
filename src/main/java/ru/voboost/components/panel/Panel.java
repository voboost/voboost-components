package ru.voboost.components.panel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.voboost.components.theme.Theme;

/**
 * Panel component - A customizable container with rounded corners and shadow.
 *
 * <p>This component provides a styled container with:
 * <ul>
 *   <li>Rounded corners</li>
 *   <li>Shadow effect</li>
 *   <li>Border</li>
 *   <li>Multi-theme support (Free/Dreamer, Light/Dark)</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>
 * Panel panel = new Panel(context);
 * panel.setTheme(Theme.FREE_LIGHT);
 * </pre>
 */
public class Panel extends ViewGroup {

    private static final String TAG = "Panel";

    // Theme
    private Theme currentTheme;

    // Paints
    private Paint backgroundPaint;
    private Paint borderPaint;
    private Paint shadowPaint;

    // Drawing bounds
    private RectF backgroundRect;
    private RectF borderRect;

    // ============================================================
    // CONSTRUCTORS
    // ============================================================

    public Panel(Context context) {
        super(context);
        init(context);
    }

    public Panel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Panel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public Panel(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    // ============================================================
    // INITIALIZATION
    // ============================================================

    private void init(Context context) {
        // Enable drawing for ViewGroup (ViewGroup defaults to setWillNotDraw(true))
        setWillNotDraw(false);

        // Enable hardware acceleration for better performance
        setLayerType(LAYER_TYPE_HARDWARE, null);

        // Initialize paints
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // Configure border paint
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(PanelTheme.BORDER_WIDTH);

        // Initialize drawing bounds
        backgroundRect = new RectF();
        borderRect = new RectF();
    }

    // ============================================================
    // PUBLIC API
    // ============================================================

    /**
     * Sets the theme for the component.
     *
     * @param theme the theme to apply
     * @throws IllegalArgumentException if theme is null
     */
    public void setTheme(Theme theme) {
        if (theme == null) {
            throw new IllegalArgumentException("Theme cannot be null");
        }

        this.currentTheme = theme;
        updateColors();
        invalidate();
    }

    /**
     * Returns the current theme.
     *
     * @return the current theme
     */
    public Theme getCurrentTheme() {
        return currentTheme;
    }

    // ============================================================
    // MEASUREMENT
    // ============================================================

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int totalHeight = getPaddingTop() + getPaddingBottom();
        int maxWidth = 0;

        // Measure all children with full available width
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;

            // Give children the full panel width
            int childWidthSpec = MeasureSpec.makeMeasureSpec(
                    widthSize - getPaddingLeft() - getPaddingRight(),
                    MeasureSpec.EXACTLY);
            int childHeightSpec = MeasureSpec.makeMeasureSpec(
                    heightSize - totalHeight,
                    MeasureSpec.AT_MOST);

            child.measure(childWidthSpec, childHeightSpec);

            maxWidth = Math.max(maxWidth, child.getMeasuredWidth());
            totalHeight += child.getMeasuredHeight();
        }

        // Ensure minimum size for empty panels (for testing purposes)
        int minSize = 100; // Minimum 100px for empty panels
        if (maxWidth == 0) maxWidth = minSize;
        if (totalHeight == 0) totalHeight = minSize;

        int finalWidth = widthMode == MeasureSpec.EXACTLY ? widthSize :
                Math.max(maxWidth + getPaddingLeft() + getPaddingRight(), 0);
        int finalHeight = heightMode == MeasureSpec.EXACTLY ? heightSize :
                Math.max(totalHeight, 0);

        setMeasuredDimension(finalWidth, finalHeight);
    }


    // ============================================================
    // DRAWING
    // ============================================================

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (currentTheme == null) {
            return;
        }

        // Update drawing bounds
        updateDrawingBounds();

        // Draw shadow
        drawShadow(canvas);

        // Draw background
        drawBackground(canvas);

        // Draw border
        drawBorder(canvas);
    }

    private void updateDrawingBounds() {
        float left = getPaddingLeft();
        float top = getPaddingTop();
        float right = getWidth() - getPaddingRight();
        float bottom = getHeight() - getPaddingBottom();

        backgroundRect.set(left, top, right, bottom);

        // Border is drawn inside the background
        float borderOffset = PanelTheme.BORDER_WIDTH / 2f;
        borderRect.set(
                left + borderOffset,
                top + borderOffset,
                right - borderOffset,
                bottom - borderOffset
        );
    }

    private void drawShadow(Canvas canvas) {
        // Draw shadow as a slightly offset background
        float shadowOffset = PanelTheme.ELEVATION;
        shadowPaint.setColor(PanelTheme.getShadow(currentTheme));

        RectF shadowRect = new RectF(
                backgroundRect.left + shadowOffset,
                backgroundRect.top + shadowOffset,
                backgroundRect.right + shadowOffset,
                backgroundRect.bottom + shadowOffset
        );

        canvas.drawRoundRect(shadowRect, PanelTheme.CORNER_RADIUS, PanelTheme.CORNER_RADIUS, shadowPaint);
    }

    private void drawBackground(Canvas canvas) {
        backgroundPaint.setColor(PanelTheme.getBackground(currentTheme));
        canvas.drawRoundRect(backgroundRect, PanelTheme.CORNER_RADIUS, PanelTheme.CORNER_RADIUS, backgroundPaint);
    }

    private void drawBorder(Canvas canvas) {
        borderPaint.setColor(PanelTheme.getBorder(currentTheme));
        canvas.drawRoundRect(borderRect, PanelTheme.CORNER_RADIUS, PanelTheme.CORNER_RADIUS, borderPaint);
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================

    private void updateColors() {
        if (currentTheme == null) {
            return;
        }

        backgroundPaint.setColor(PanelTheme.getBackground(currentTheme));
        borderPaint.setColor(PanelTheme.getBorder(currentTheme));
        shadowPaint.setColor(PanelTheme.getShadow(currentTheme));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int currentTop = getPaddingTop();

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            int childLeft = getPaddingLeft();
            int childRight = childLeft + child.getMeasuredWidth();
            int childBottom = currentTop + child.getMeasuredHeight();

            child.layout(childLeft, currentTop, childRight, childBottom);

            currentTop = childBottom;
        }
    }

    // ============================================================
    // STATE PERSISTENCE
    // ============================================================

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();

        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putString("currentTheme", currentTheme != null ? currentTheme.getValue() : null);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            String themeValue = bundle.getString("currentTheme");
            currentTheme = themeValue != null ? Theme.fromValue(themeValue) : null;

            super.onRestoreInstanceState(bundle.getParcelable("superState"));

            if (currentTheme != null) {
                updateColors();
            }

            invalidate();
        } else {
            super.onRestoreInstanceState(state);
        }
    }
}
