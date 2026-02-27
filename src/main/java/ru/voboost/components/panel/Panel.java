package ru.voboost.components.panel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;

import ru.voboost.components.i18n.ILocalizable;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.IThemable;
import ru.voboost.components.theme.Theme;

/**
 * Panel component - A customizable container with rounded corners and shadow.
 *
 * <p>
 * This component provides a styled container with:
 * <ul>
 * <li>Rounded corners</li>
 * <li>Native shadow effect</li>
 * <li>Border</li>
 * <li>Multi-theme support (Free/Dreamer, Light/Dark)</li>
 * <li>Internal vertical scrolling logic</li>
 * </ul>
 */
public class Panel extends FrameLayout implements IThemable, ILocalizable {

    // Theme & Language
    private Theme currentTheme;
    private Language currentLanguage;

    // Paints
    private Paint backgroundPaint;
    private Paint borderPaint;

    // Drawing bounds
    private RectF backgroundRect;
    private RectF borderRect;

    // Internal views
    private ScrollView scrollView;
    private LinearLayout contentLayout;

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
        // Enable custom drawing
        setWillNotDraw(false);

        // Initialize paints
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // Configure border paint
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(PanelTheme.BORDER_WIDTH);

        // Initialize drawing bounds
        backgroundRect = new RectF();
        borderRect = new RectF();

        // Setup OutlineProvider for native shadow and clipping
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                int left = view.getPaddingLeft();
                int top = view.getPaddingTop();
                int right = view.getWidth() - view.getPaddingRight();
                int bottom = view.getHeight() - view.getPaddingBottom();
                outline.setRoundRect(left, top, right, bottom, PanelTheme.CORNER_RADIUS);
            }
        });
        setClipToOutline(true);
        setElevation(PanelTheme.ELEVATION);

        // Setup internal ScrollView
        scrollView = new ScrollView(context);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        // ScrollView has its own padding handling, Panel padding is for borders

        contentLayout = new LinearLayout(context);
        contentLayout.setOrientation(LinearLayout.VERTICAL);

        scrollView.addView(contentLayout, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        // Let the scrollview respect panel paddings so borders are drawn properly
        FrameLayout.LayoutParams scrollParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        super.addView(scrollView, -1, scrollParams);
    }

    // ============================================================
    // CHILD MANAGEMENT
    // ============================================================

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (contentLayout == null) {
            super.addView(child, index, params);
        } else {
            contentLayout.addView(child, index, params);
        }
    }

    @Override
    public void removeView(View view) {
        if (contentLayout != null && view != scrollView) {
            contentLayout.removeView(view);
        } else {
            super.removeView(view);
        }
    }

    @Override
    public void removeAllViews() {
        if (contentLayout != null) {
            contentLayout.removeAllViews();
        } else {
            super.removeAllViews();
        }
    }

    // ============================================================
    // PUBLIC API
    // ============================================================

    @Override
    public void setTheme(Theme theme) {
        if (theme == null) {
            throw new IllegalArgumentException("Theme cannot be null");
        }
        this.currentTheme = theme;
        updateColors();
        invalidate();
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    @Override
    public void setLanguage(Language language) {
        if (language == null) {
            throw new IllegalArgumentException("Language cannot be null");
        }
        this.currentLanguage = language;
        invalidate();
    }

    public Language getCurrentLanguage() {
        return currentLanguage;
    }

    @Override
    public void propagateTheme(Theme theme) {
        if (theme == null || contentLayout == null) {
            return;
        }

        for (int i = 0; i < contentLayout.getChildCount(); i++) {
            View child = contentLayout.getChildAt(i);
            if (child instanceof IThemable) {
                ((IThemable) child).setTheme(theme);
                ((IThemable) child).propagateTheme(theme);
            }
        }
    }

    @Override
    public void propagateLanguage(Language language) {
        if (language == null || contentLayout == null) {
            return;
        }

        for (int i = 0; i < contentLayout.getChildCount(); i++) {
            View child = contentLayout.getChildAt(i);
            if (child instanceof ILocalizable) {
                ((ILocalizable) child).setLanguage(language);
                ((ILocalizable) child).propagateLanguage(language);
            }
        }
    }

    // ============================================================
    // DRAWING
    // ============================================================

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // Update drawing bounds to respect padding
        float padLeft = getPaddingLeft();
        float padTop = getPaddingTop();
        float padRight = getWidth() - getPaddingRight();
        float padBottom = getHeight() - getPaddingBottom();

        backgroundRect.set(padLeft, padTop, padRight, padBottom);

        // Border is drawn inside the background
        float borderOffset = PanelTheme.BORDER_WIDTH / 2f;
        borderRect.set(
                padLeft + borderOffset,
                padTop + borderOffset,
                padRight - borderOffset,
                padBottom - borderOffset);

        // Adjust scroll view to be inside the padding layout
        scrollView.layout((int) padLeft, (int) padTop, (int) padRight, (int) padBottom);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (currentTheme != null) {
            drawBackground(canvas);
        }

        // Draw ScrollView and children
        super.dispatchDraw(canvas);

        if (currentTheme != null) {
            drawBorder(canvas);
        }
    }

    private void drawBackground(Canvas canvas) {
        backgroundPaint.setColor(PanelTheme.getBackground(currentTheme));
        canvas.drawRoundRect(
                backgroundRect,
                PanelTheme.CORNER_RADIUS,
                PanelTheme.CORNER_RADIUS,
                backgroundPaint);
    }

    private void drawBorder(Canvas canvas) {
        borderPaint.setColor(PanelTheme.getBorder(currentTheme));
        canvas.drawRoundRect(
                borderRect, PanelTheme.CORNER_RADIUS, PanelTheme.CORNER_RADIUS, borderPaint);
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
    }
}
