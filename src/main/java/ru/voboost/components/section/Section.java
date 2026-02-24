package ru.voboost.components.section;

import java.util.Map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import ru.voboost.components.font.Font;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.Theme;

/**
 * Section component - A titled container with rounded corners.
 *
 * <p>This is a ViewGroup that can contain child views (like Radio).
 * It draws:
 * <ul>
 *   <li>A full-width title bar with top-only rounded corners</li>
 *   <li>A content area with bottom-only rounded corners</li>
 *   <li>Title text inside the title bar</li>
 * </ul>
 *
 * <p>The vendor design has:
 * <ul>
 *   <li>Section background: #1a1e28 (dark theme)</li>
 *   <li>Corner radius: 20px</li>
 *   <li>Title: 32sp, bold, white text</li>
 *   <li>Title margin: 30px left, 25px top</li>
 *   <li>Content padding bottom: 25px</li>
 *   <li>Horizontal margin: 30px on each side</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>
 * Section section = new Section(context);
 * section.setTheme(Theme.FREE_DARK);
 * section.setLanguage(Language.EN);
 * section.setTitle(Map.of("en", "Language Selection", "ru", "Выбор языка"));
 * section.addView(radioComponent);
 * </pre>
 */
public class Section extends ViewGroup {

    private static final String TAG = "Section";

    // Data
    private Map<String, String> title;

    // Theme and Language
    private Theme currentTheme;
    private Language currentLanguage;

    // Paints
    private Paint backgroundPaint;
    private Paint titleGradientPaint;
    private Paint titlePaint;

    // Drawing paths for rounded corners
    private Path titleBarPath;
    private Path contentAreaPath;

    // Calculated dimensions
    private int titleBarHeight;

    // ============================================================
    // CONSTRUCTORS
    // ============================================================

    public Section(Context context) {
        super(context);
        init(context);
    }

    public Section(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Section(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public Section(
            Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    // ============================================================
    // INITIALIZATION
    // ============================================================

    private void init(Context context) {
        // Enable drawing for ViewGroup
        setWillNotDraw(false);

        // Enable hardware acceleration for better performance
        setLayerType(LAYER_TYPE_HARDWARE, null);

        // Initialize paints
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titleGradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        titlePaint.setTextSize(SectionTheme.TITLE_TEXT_SIZE);
        titlePaint.setTextAlign(Paint.Align.LEFT);
        titlePaint.setTypeface(Font.getBold(context, ""));

        // Initialize paths
        titleBarPath = new Path();
        contentAreaPath = new Path();
    }

    // ============================================================
    // PUBLIC API
    // ============================================================

    /**
     * Sets the title for the section with localized labels.
     *
     * @param title Map of language code to localized title text
     * @throws IllegalArgumentException if title is null or empty
     */
    public void setTitle(Map<String, String> title) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }

        this.title = title;
        calculateTitleBarHeight();
        requestLayout();
        invalidate();
    }

    /**
     * Returns the Map of localized titles.
     *
     * @return Map of language code to title text
     */
    public Map<String, String> getTitle() {
        return title;
    }

    /**
     * Returns the title text for the current language.
     *
     * @return the title text, or empty string if no title is set
     */
    public String getTitleText() {
        if (title == null || currentLanguage == null) {
            return "";
        }

        String text = title.get(currentLanguage.getCode());
        return text != null ? text : "";
    }

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

    /**
     * Sets the language for the component.
     *
     * @param language the language to apply
     * @throws IllegalArgumentException if language is null
     */
    public void setLanguage(Language language) {
        if (language == null) {
            throw new IllegalArgumentException("Language cannot be null");
        }

        this.currentLanguage = language;
        calculateTitleBarHeight();
        requestLayout();
        invalidate();
    }

    /**
     * Returns the current language.
     *
     * @return the current language
     */
    public Language getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * Propagates the theme to all child components.
     *
     * @param theme the theme to propagate
     */
    public void propagateTheme(Theme theme) {
        if (theme == null) {
            return;
        }

        // Propagate theme to all child views
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof ru.voboost.components.radio.Radio) {
                ((ru.voboost.components.radio.Radio) child).setTheme(theme);
            }
        }
    }

    /**
     * Propagates the language to all child components.
     *
     * @param language the language to propagate
     */
    public void propagateLanguage(Language language) {
        if (language == null) {
            return;
        }

        // Propagate language to all child views
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof ru.voboost.components.radio.Radio) {
                ((ru.voboost.components.radio.Radio) child).setLanguage(language);
            }
        }
    }

    // ============================================================
    // MEASUREMENT
    // ============================================================

    private void calculateTitleBarHeight() {
        // Title bar height is fixed at 98dp (98 pixels) to match original implementation
        titleBarHeight = 98;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        // Section uses fixed width
        int sectionWidth = SectionTheme.SECTION_WIDTH;

        // Calculate title bar height
        calculateTitleBarHeight();

        // Total height starts with title bar + title spacing
        int totalHeight = titleBarHeight + SectionTheme.TITLE_SPACING;

        // Content width = section width minus internal horizontal padding
        int contentWidth = sectionWidth - 2 * SectionTheme.CONTENT_PADDING_HORIZONTAL;

        // Measure children
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;

            // Give children the content width (section width minus horizontal padding)
            int childWidthSpec = MeasureSpec.makeMeasureSpec(contentWidth, MeasureSpec.EXACTLY);
            int childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

            child.measure(childWidthSpec, childHeightSpec);
            totalHeight += child.getMeasuredHeight();
        }

        // Add content padding bottom + bottom margin (spacing after section)
        totalHeight += SectionTheme.CONTENT_PADDING_BOTTOM + SectionTheme.BOTTOM_MARGIN;

        // The measured width includes horizontal margins (so parent knows total space needed)
        setMeasuredDimension(
                resolveSize(sectionWidth + 2 * SectionTheme.HORIZONTAL_MARGIN, widthMeasureSpec),
                resolveSize(totalHeight, heightMeasureSpec));
    }

    // ============================================================
    // LAYOUT
    // ============================================================

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int sectionLeft = SectionTheme.HORIZONTAL_MARGIN;
        int sectionRight = sectionLeft + SectionTheme.SECTION_WIDTH;

        // Children start below the title bar with title spacing, with horizontal content padding
        int currentTop = titleBarHeight + SectionTheme.TITLE_SPACING;
        int childLeft = sectionLeft + SectionTheme.CONTENT_PADDING_HORIZONTAL;

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) continue;

            int childRight = childLeft + child.getMeasuredWidth();
            int childBottom = currentTop + child.getMeasuredHeight();

            child.layout(childLeft, currentTop, childRight, childBottom);
            currentTop = childBottom;
        }
    }

    // ============================================================
    // DRAWING
    // ============================================================

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (currentTheme == null || currentLanguage == null) {
            return;
        }

        int height = getHeight();

        float sectionLeft = SectionTheme.HORIZONTAL_MARGIN;
        float sectionRight = sectionLeft + SectionTheme.SECTION_WIDTH;
        float sectionTop = 0;
        float sectionBottom = height - SectionTheme.BOTTOM_MARGIN;
        float radius = SectionTheme.CORNER_RADIUS;

        // Draw full section background with all corners rounded
        backgroundPaint.setColor(SectionTheme.getBackground(currentTheme));
        RectF sectionRect = new RectF(sectionLeft, sectionTop, sectionRight, sectionBottom);
        canvas.drawRoundRect(sectionRect, radius, radius, backgroundPaint);

        // Draw title gradient overlay (only for dark themes)
        if (SectionTheme.hasTitleGradient(currentTheme)) {
            drawTitleGradient(canvas, sectionLeft, sectionRight, sectionTop, radius);
        }

        // Draw title text
        drawTitle(canvas, sectionLeft);
    }

    private void drawTitle(Canvas canvas, float sectionLeft) {
        String titleText = getTitleText();
        if (titleText.isEmpty()) {
            return;
        }

        // Set the correct bold font variant based on title text content
        titlePaint.setTypeface(Font.getBold(getContext(), titleText));

        float titleX = sectionLeft + SectionTheme.TITLE_MARGIN_START;
        float titleY = SectionTheme.TITLE_MARGIN_TOP + getTextVerticalOffset() + 4f;

        titlePaint.setColor(SectionTheme.getTitleTextColor(currentTheme));
        canvas.drawText(titleText, titleX, titleY, titlePaint);
    }

    private float getTextVerticalOffset() {
        // Position text baseline correctly
        Paint.FontMetrics fm = titlePaint.getFontMetrics();
        return -fm.ascent;
    }

    private void drawTitleGradient(
            Canvas canvas, float sectionLeft, float sectionRight, float sectionTop, float radius) {
        // Horizontal gradient: darker on the left, fading to section background on the right
        int gradientStart = SectionTheme.getTitleGradientStart(currentTheme);
        int bgColor = SectionTheme.getBackground(currentTheme);

        float gradientBottom = sectionTop + titleBarHeight;

        titleGradientPaint.setShader(
                new LinearGradient(
                        sectionLeft,
                        0,
                        sectionRight,
                        0,
                        gradientStart,
                        bgColor,
                        Shader.TileMode.CLAMP));

        // Draw gradient only in the title area with top rounded corners
        Path titlePath = new Path();
        float[] radii = new float[] {radius, radius, radius, radius, 0, 0, 0, 0};
        titlePath.addRoundRect(
                new RectF(sectionLeft, sectionTop, sectionRight, gradientBottom),
                radii,
                Path.Direction.CW);
        canvas.drawPath(titlePath, titleGradientPaint);
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================

    private void updateColors() {
        if (currentTheme == null) {
            return;
        }

        backgroundPaint.setColor(SectionTheme.getBackground(currentTheme));
        titlePaint.setColor(SectionTheme.getTitleTextColor(currentTheme));
    }

    // ============================================================
    // STATE PERSISTENCE
    // ============================================================

    @Override
    protected android.os.Parcelable onSaveInstanceState() {
        android.os.Bundle bundle = new android.os.Bundle();

        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putString(
                "currentLanguage", currentLanguage != null ? currentLanguage.getCode() : null);
        bundle.putString("currentTheme", currentTheme != null ? currentTheme.getValue() : null);
        bundle.putSerializable("title", (java.io.Serializable) title);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(android.os.Parcelable state) {
        if (state instanceof android.os.Bundle) {
            android.os.Bundle bundle = (android.os.Bundle) state;

            String langCode = bundle.getString("currentLanguage");
            currentLanguage = langCode != null ? Language.fromCode(langCode) : null;

            String themeValue = bundle.getString("currentTheme");
            currentTheme = themeValue != null ? Theme.fromValue(themeValue) : null;

            @SuppressWarnings("unchecked")
            Map<String, String> titleFromBundle =
                    (Map<String, String>) bundle.getSerializable("title");
            title = titleFromBundle;

            super.onRestoreInstanceState(bundle.getParcelable("superState"));

            if (currentTheme != null) {
                updateColors();
            }

            if (title != null) {
                calculateTitleBarHeight();
            }

            invalidate();
        } else {
            super.onRestoreInstanceState(state);
        }
    }
}
