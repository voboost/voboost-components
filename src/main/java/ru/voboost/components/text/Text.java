package ru.voboost.components.text;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Parcelable;

import ru.voboost.components.font.Font;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Map;

import ru.voboost.components.i18n.Language;

/**
 * Text component provides unified text rendering for the Voboost component library.
 * Works both as standalone view and for custom positioning within parent components.
 *
 * Features:
 * - Support for multiple text roles (CONTROL, TITLE)
 * - Dynamic theming with four theme variants
 * - Localization support using Map<Language, String>
 * - Custom positioning and drawing capabilities
 * - Canvas-based rendering for optimal performance
 */
public class Text extends View {

    // Text properties
    private String text;
    private Map<Language, String> localizedText;
    private TextRole role = TextRole.CONTROL;
    private String theme = TextTheme.FREE_LIGHT;
    private Language language = Language.EN;
    private Paint.Align textAlign = Paint.Align.LEFT;

    // Positioning properties
    private float positionX = 0;
    private float positionY = 0;
    private boolean useCustomPosition = false;

    // Drawing properties
    private Paint textPaint;
    private Rect textBounds = new Rect();
    private int currentColor;
    private boolean useCustomColor = false;

    // Font
    private Typeface typeface;

    // State for saving/restoring
    private static final String STATE_TEXT = "text";
    private static final String STATE_ROLE = "role";
    private static final String STATE_THEME = "theme";
    private static final String STATE_LANGUAGE = "language";
    private static final String STATE_POSITION_X = "positionX";
    private static final String STATE_POSITION_Y = "positionY";

    /**
     * Standard constructor called when view is created from code.
     */
    public Text(Context context) {
        super(context);
        init();
    }

    /**
     * Standard constructor called when view is inflated from XML.
     */
    public Text(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Constructor called when view is inflated from XML with a style.
     */
    public Text(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Constructor called when view is inflated from XML with a style and a style resource.
     */
    public Text(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * Initialize the Text component.
     */
    private void init() {
        // Initialize paint
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(textAlign);

        // Load font via Font component (shared across all components, no fallback)
        typeface = Font.getRegular(getContext());
        textPaint.setTypeface(typeface);

        // Apply initial role and theme
        applyRole();
        applyTheme();

        // Set default text
        text = "";
    }

    /**
     * Apply the text role (font size and weight).
     */
    private void applyRole() {
        if (role != null) {
            textPaint.setTextSize(role.getSizePx());

            // Set font weight using fake bold if needed
            // Note: Variable fonts would be better, but this provides compatibility
            boolean isBold = role.getWeight() >= 600;
            textPaint.setFakeBoldText(isBold);
        }
    }

    /**
     * Apply the theme color.
     */
    private void applyTheme() {
        if (!useCustomColor) {
            currentColor = TextTheme.getColor(role, theme);
            textPaint.setColor(currentColor);
        }
    }

    /**
     * Update the current text based on language and localization.
     */
    private void updateCurrentText() {
        if (localizedText != null && localizedText.containsKey(language)) {
            text = localizedText.get(language);
        }
        // If no localized text is available for the current language, keep the existing text
    }

    /**
     * Set static text.
     *
     * @param text The text to display
     */
    public void setText(String text) {
        this.text = text;
        this.localizedText = null;
        invalidate();
    }

    /**
     * Set localized text.
     *
     * @param localizedText Map of language codes to text strings
     */
    public void setText(Map<Language, String> localizedText) {
        this.localizedText = localizedText;
        updateCurrentText();
        invalidate();
    }

    /**
     * Get the current text.
     *
     * @return The current text string
     */
    public String getText() {
        return text;
    }

    /**
     * Set a custom color (for animation support).
     *
     * @param color The color to use
     */
    public void setColor(int color) {
        this.currentColor = color;
        this.useCustomColor = true;
        textPaint.setColor(color);
        invalidate();
    }

    /**
     * Reset to theme color.
     */
    public void useThemeColor() {
        this.useCustomColor = false;
        applyTheme();
        invalidate();
    }

    /**
     * Set the text role.
     *
     * @param role The text role (CONTROL or TITLE)
     */
    public void setRole(TextRole role) {
        this.role = role;
        applyRole();
        applyTheme();
        invalidate();
    }

    /**
     * Get the current text role.
     *
     * @return The current text role
     */
    public TextRole getRole() {
        return role;
    }

    /**
     * Set the theme.
     *
     * @param theme The theme identifier
     */
    public void setTheme(String theme) {
        if (TextTheme.isValidTheme(theme)) {
            this.theme = theme;
            applyTheme();
            invalidate();
        }
    }

    /**
     * Get the current theme.
     *
     * @return The current theme identifier
     */
    public String getTheme() {
        return theme;
    }

    /**
     * Set the language.
     *
     * @param language The language enum
     */
    public void setLanguage(Language language) {
        this.language = language;
        updateCurrentText();
        invalidate();
    }

    /**
     * Get the current language.
     *
     * @return The current language enum
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Set the draw position for custom positioning.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public void setPosition(float x, float y) {
        this.positionX = x;
        this.positionY = y;
        this.useCustomPosition = true;
        invalidate();
    }

    /**
     * Set text alignment.
     *
     * @param align The text alignment
     */
    public void setTextAlign(Paint.Align align) {
        this.textAlign = align;
        textPaint.setTextAlign(align);
        invalidate();
    }

    /**
     * Get the text width.
     *
     * @return The width of the text in pixels
     */
    public float getTextWidth() {
        if (text == null) return 0;
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
        return textBounds.width();
    }

    /**
     * Get the text height.
     *
     * @return The height of the text in pixels
     */
    public float getTextHeight() {
        if (text == null) return 0;
        textPaint.getTextBounds(text, 0, text.length(), textBounds);
        return textBounds.height();
    }

    /**
     * Get the baseline offset for vertical centering.
     *
     * @return The baseline offset in pixels
     */
    public float getBaselineOffset() {
        return textPaint.getFontMetrics().descent;
    }

    /**
     * Draw the text at the specified position.
     * This method is used when the Text component is drawn by a parent.
     *
     * @param canvas The canvas to draw on
     */
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (text == null || text.isEmpty()) return;

        float x = useCustomPosition ? positionX : 0;
        float y = useCustomPosition ? positionY : getBaselineOffset();

        canvas.drawText(text, x, y, textPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!useCustomPosition && text != null && !text.isEmpty()) {
            // Draw at the top-left corner when used as a standalone view
            float y = getTextHeight();
            canvas.drawText(text, 0, y, textPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (text == null || text.isEmpty()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        // Measure text dimensions
        textPaint.getTextBounds(text, 0, text.length(), textBounds);

        int width = textBounds.width() + getPaddingLeft() + getPaddingRight();
        int height = textBounds.height() + getPaddingTop() + getPaddingBottom();

        // Apply measure specs
        width = resolveSize(width, widthMeasureSpec);
        height = resolveSize(height, heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        // Create a custom state to save our properties
        SavedState savedState = new SavedState(superState);
        savedState.text = this.text;
        savedState.role = this.role;
        savedState.theme = this.theme;
        savedState.language = this.language;
        savedState.positionX = this.positionX;
        savedState.positionY = this.positionY;

        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        // Restore our properties
        this.text = savedState.text;
        this.role = savedState.role;
        this.theme = savedState.theme;
        this.language = savedState.language;
        this.positionX = savedState.positionX;
        this.positionY = savedState.positionY;

        // Apply restored properties
        applyRole();
        applyTheme();
        invalidate();
    }

    /**
     * Custom saved state class for preserving Text component state.
     */
    public static class SavedState extends BaseSavedState {
        String text;
        TextRole role;
        String theme;
        Language language;
        float positionX;
        float positionY;

        SavedState(Parcelable superState) {
            super(superState);
        }
    }
}
