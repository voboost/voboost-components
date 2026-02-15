package ru.voboost.components.radio;

import java.util.ArrayList;
import java.util.List;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;

import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import ru.voboost.components.i18n.Language;
import ru.voboost.components.font.Font;
import ru.voboost.components.theme.Theme;

/**
 * Radio component with internal theming and external localization
 */
public class Radio extends View {
    // Data and state
    private List<RadioButton> buttons = new ArrayList<>();
    private Language currentLanguage = null;
    private Theme currentTheme = null;
    private String selectedValue = "";
    private OnValueChangeListener onValueChangeListener;

    // Theme and dimensions
    private RadioColors colors;

    // Layout measurements
    private List<Float> itemWidths = new ArrayList<>();
    private List<Float> itemPositions = new ArrayList<>();
    private float totalWidth = 0f;
    private float totalHeight = 0f;
    private float contentWidth = 0f; // Content width only (for background)
    private float animationPadding = 0f; // Additional space for animation
    private float contentOffsetX = 0f; // Content offset inside View (centering)

    // Animation state
    private ValueAnimator positionAnimator;
    private ValueAnimator widthAnimator;
    private float animatedX = 0f;
    private float animatedWidth = 0f;

    // Paint objects for drawing
    private Paint backgroundPaint;
    private Paint selectedBackgroundPaint;
    private Paint selectedBorderPaint;
    private Paint textPaint;

    // Dimensions in pixels (as required by .roorules)
    private float heightPx;
    private float cornerRadiusPx;
    private float borderWidthPx;
    private float textSizePx;
    private float itemPaddingHorizontalPx;
    private float itemMinWidthPx;

    /**
     * Interface for value change callbacks
     */
    public interface OnValueChangeListener {
        void onValueChange(String newValue);
    }

    /**
     * Check if component is properly initialized
     * @return true if both theme and language are set
     */
    private boolean isInitialized() {
        return currentTheme != null && currentLanguage != null;
    }

    public Radio(Context context) {
        super(context);
        init();
    }

    public Radio(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Radio(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public Radio(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        // Initialize paint objects with default colors (will be updated when theme is set)
        initPaintsWithDefaults();

        // Set pixel dimensions
        setDimensions();

        // Enable hardware acceleration for better performance
        setLayerType(LAYER_TYPE_HARDWARE, null);

        // Load custom font AFTER paints are initialized (requires context)
        loadFont();
    }

    /**
     * Loads the project font and applies it to text paint.
     * Must be called after initPaintsWithDefaults() creates textPaint.
     */
    private void loadFont() {
        if (textPaint != null) {
            textPaint.setTypeface(Font.getRegular(getContext()));
        }
    }

    private void updateTheme() {
        if (currentTheme != null) {
            colors = RadioTheme.getColors(currentTheme);
        }
    }

    private void initPaints() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (colors != null) {
            backgroundPaint.setColor(colors.background);
        }

        selectedBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        selectedBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectedBorderPaint.setStyle(Paint.Style.STROKE);

        if (textPaint == null) {
            textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setTypeface(Font.getRegular(getContext()));
            textPaint.setTextAlign(Paint.Align.CENTER);
        }
    }

    private void initPaintsWithDefaults() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(0xFF373F4A); // Default gray

        selectedBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        selectedBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectedBorderPaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // Typeface set in loadFont() which runs immediately after
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    private void setDimensions() {
        cornerRadiusPx = RadioDimensions.CORNER_RADIUS_PX;
        heightPx = RadioDimensions.HEIGHT_PX;
        borderWidthPx = RadioDimensions.BORDER_WIDTH_PX;
        textSizePx = RadioDimensions.TEXT_SIZE_PX;
        itemPaddingHorizontalPx = RadioDimensions.ITEM_PADDING_HORIZONTAL_PX;
        itemMinWidthPx = RadioDimensions.ITEM_MIN_WIDTH_PX;

        if (textPaint != null) {
            textPaint.setTextSize(textSizePx);
        }

        if (selectedBorderPaint != null) {
            selectedBorderPaint.setStrokeWidth(borderWidthPx);
        }
    }

    /**
     * Set the list of radio button options
     * @param buttons List of RadioButton objects
     */
    public void setButtons(List<RadioButton> buttons) {
        if (buttons != null) {
            this.buttons = new ArrayList<>(buttons);
        } else {
            this.buttons = new ArrayList<>();
        }
        measureItems();
        requestLayout(); // Force layout recalculation for dynamic width
        invalidate();
    }

    /**
     * Set the current language using Language enum
     * @param language Language enum value (required, cannot be null)
     * @throws IllegalArgumentException if language is null
     */
    public void setLanguage(Language language) {
        if (language == null) {
            throw new IllegalArgumentException("Language cannot be null");
        }

        if (!language.equals(this.currentLanguage)) {
            this.currentLanguage = language;

            // Store current selected index
            int selectedIndex = findSelectedIndex();

            // Cancel any running animations to prevent conflicts
            cancelAnimations();

            measureItemsForLanguageChange();
            requestLayout(); // Force layout recalculation for dynamic width

            // Immediately snap to correct position for selected item - no animation
            if (selectedIndex >= 0 && itemPositions != null && itemWidths != null && buttons != null &&
                selectedIndex < itemPositions.size() && selectedIndex < itemWidths.size() &&
                selectedIndex < buttons.size()) {
                animatedX = itemPositions.get(selectedIndex);
                animatedWidth = itemWidths.get(selectedIndex);
            }

            invalidate();
        }
    }

    /**
     * Set the theme using Theme enum
     * @param theme Theme enum value (required, cannot be null)
     * @throws IllegalArgumentException if theme is null
     */
    public void setTheme(Theme theme) {
        if (theme == null) {
            throw new IllegalArgumentException("Theme cannot be null");
        }

        if (!theme.equals(this.currentTheme)) {
            this.currentTheme = theme;
            updateTheme();
            initPaints();
            invalidate();
        }
    }

    /**
     * Set the currently selected value
     * @param value The value to select
     * @param isTriggerCallback Whether to trigger onValueChangeListener
     */
    public void setSelectedValue(String value, boolean isTriggerCallback) {
        if (value != null && !value.equals(this.selectedValue)) {
            this.selectedValue = value;

            int selectedIndex = findSelectedIndex();
            if (selectedIndex >= 0 && itemPositions != null && itemWidths != null && buttons != null &&
                selectedIndex < itemPositions.size() && selectedIndex < itemWidths.size() &&
                selectedIndex < buttons.size()) {
                float targetX = itemPositions.get(selectedIndex);
                float targetWidth = itemWidths.get(selectedIndex);

                // Always snap to position immediately - no animation on initial setup
                animatedX = targetX;
                animatedWidth = targetWidth;
            }

            // Trigger callback if requested (useful for testing)
            if (isTriggerCallback && onValueChangeListener != null) {
                onValueChangeListener.onValueChange(value);
            }

            invalidate();
        }
    }

    /**
     * Set the currently selected value (backward compatible)
     * @param value The value to select
     */
    public void setSelectedValue(String value) {
        if (value != null) {
            setSelectedValue(value, false);
        }
    }

    /**
     * Get the currently selected value
     * @return The selected value
     */
    public String getSelectedValue() {
        return selectedValue;
    }

    /**
     * Get the current theme
     * @return The current theme, or null if not set
     */
    public Theme getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Get the current language
     * @return The current language, or null if not set
     */
    public Language getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * Set the value change listener
     * @param listener Callback for value changes
     */
    public void setOnValueChangeListener(OnValueChangeListener listener) {
        this.onValueChangeListener = listener;
    }

    private int findSelectedIndex() {
        if (buttons == null || selectedValue == null) {
            return -1;
        }

        for (int i = 0; i < buttons.size(); i++) {
            RadioButton button = buttons.get(i);
            if (button != null && selectedValue != null && selectedValue.equals(button.getValue())) {
                return i;
            }
        }

        return -1; // Return -1 if not found to avoid jitter
    }

    private void measureItems() {
        if (!isInitialized()) {
            return;
        }
        measureItemsInternal(true);
    }

    /**
     * Measure items for language change without updating animation position
     */
    private void measureItemsForLanguageChange() {
        if (!isInitialized()) {
            return;
        }
        measureItemsInternal(false);
    }

    /**
     * Internal method to measure items and calculate positions
     * @param updateAnimationPosition Whether to update animation position after measurement
     */
    private void measureItemsInternal(boolean updateAnimationPosition) {
        if (buttons == null || buttons.isEmpty()) {
            return;
        }

        itemWidths.clear();
        itemPositions.clear();

        // Calculate equal width for all items (like layout_weight="1")
        int itemCount = buttons.size();
        float currentX = 0f;

        // First, measure all texts to find the maximum width needed
        float maxTextWidth = 0f;
        for (RadioButton button : buttons) {
            if (button == null) continue;

            String text = button.getText(currentLanguage != null ? currentLanguage.getCode() : "en");

            // Measure with bold typeface to ensure enough space for selected state
            textPaint.setTypeface(Font.getBold(getContext(), text));
            float boldTextWidth = textPaint != null ? textPaint.measureText(text) : 0;
            maxTextWidth = Math.max(maxTextWidth, boldTextWidth);
        }

        // Calculate equal width for all items based on the widest text
        float equalItemWidth = Math.max(maxTextWidth + 2 * itemPaddingHorizontalPx, itemMinWidthPx);

        // Assign equal width to all items
        for (int i = 0; i < itemCount; i++) {
            itemWidths.add(equalItemWidth);
            itemPositions.add(currentX);
            currentX += equalItemWidth;
        }

        contentWidth = currentX;

        // Animation padding in pixels (as required by .roorules)
        animationPadding = RadioDimensions.ANIMATION_PADDING_PX;

        // View dimensions with animation padding
        totalWidth = contentWidth + 2 * animationPadding;
        totalHeight = heightPx;
        contentOffsetX = animationPadding;

        // Adjust item positions accounting for padding
        for (int i = 0; i < itemPositions.size(); i++) {
            itemPositions.set(i, itemPositions.get(i) + contentOffsetX);
        }

        setTranslationX(-animationPadding);

        if (updateAnimationPosition) {
            updateAnimationPosition();
        }
    }

    private void updateAnimationPosition() {
        int selectedIndex = findSelectedIndex();

        if (selectedIndex >= 0 && itemPositions != null && itemWidths != null && buttons != null &&
            selectedIndex < itemPositions.size() && selectedIndex < itemWidths.size() &&
            selectedIndex < buttons.size()) {
            animatedX = itemPositions.get(selectedIndex);
            animatedWidth = itemWidths.get(selectedIndex);
        }
    }

    private void animateToPosition(float targetX, float targetWidth) {
        cancelAnimations();

        final OvershootInterpolator interpolator =
                new OvershootInterpolator(RadioDimensions.OVERSHOOT_TENSION);

        // Position animation
        positionAnimator = ValueAnimator.ofFloat(animatedX, targetX);
        positionAnimator.setDuration(RadioDimensions.ANIMATION_DURATION);
        positionAnimator.setInterpolator(interpolator);
        positionAnimator.addUpdateListener(
                animation -> {
                    animatedX = (Float) animation.getAnimatedValue();
                    invalidate();
                });
        positionAnimator.start();

        // Width animation
        widthAnimator = ValueAnimator.ofFloat(animatedWidth, targetWidth);
        widthAnimator.setDuration(RadioDimensions.ANIMATION_DURATION);
        widthAnimator.setInterpolator(interpolator);
        widthAnimator.addUpdateListener(
                animation -> {
                    animatedWidth = (Float) animation.getAnimatedValue();
                    invalidate();
                });
        widthAnimator.start();
    }

    private void cancelAnimations() {
        if (positionAnimator != null) {
            positionAnimator.cancel();
        }

        if (widthAnimator != null) {
            widthAnimator.cancel();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureItems();

        // totalWidth now includes animation padding
        int width = (int) Math.ceil(totalWidth);
        int height = (int) Math.ceil(totalHeight);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Do not draw if not initialized
        if (!isInitialized()) {
            return;
        }

        if (buttons == null || buttons.isEmpty()) {
            return;
        }

        // LAYER 1: Bottom background - size exactly by content (e.g., 500px)
        drawBackgroundLayer(canvas);

        // LAYER 2: Selection layer - wider by animation size on left and right
        // (e.g., 20px + 500px + 20px = 540px, offset 20px to the left)
        if (animatedWidth > 0) {
            drawSelectionLayer(canvas);
        }

        // LAYER 3: Text layer - width same as bottom layer
        drawTextLayer(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Ignore touch events if not initialized
        if (!isInitialized()) {
            return false;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = event.getX();

            // Find which item was touched - accounting for content offset
            if (itemPositions != null && itemWidths != null && buttons != null) {
                for (int i = 0; i < itemPositions.size(); i++) {
                    float itemStart = itemPositions.get(i); // already includes contentOffsetX
                    float itemEnd = itemStart + itemWidths.get(i);

                    if (touchX >= itemStart && touchX <= itemEnd) {
                        RadioButton button = buttons.get(i);
                        if (button != null) {
                            String newValue = button.getValue();

                            if (selectedValue != null && !selectedValue.equals(newValue)) {
                                // This is a user click - animate the transition
                                setSelectedValueWithAnimation(newValue);

                                if (onValueChangeListener != null) {
                                    onValueChangeListener.onValueChange(newValue);
                                }
                            }
                        }

                        return true;
                    }
                }
            }
        }

        return super.onTouchEvent(event);
    }

    private void setSelectedValueWithAnimation(String value) {
        if (value != null && !value.equals(this.selectedValue)) {
            this.selectedValue = value;

            int selectedIndex = findSelectedIndex();

            if (selectedIndex >= 0 && itemPositions != null && itemWidths != null && buttons != null &&
                selectedIndex < itemPositions.size() && selectedIndex < itemWidths.size() &&
                selectedIndex < buttons.size()) {
                float targetX = itemPositions.get(selectedIndex);
                float targetWidth = itemWidths.get(selectedIndex);

                animateToPosition(targetX, targetWidth);
            }

            invalidate();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelAnimations();
        positionAnimator = null;
        widthAnimator = null;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();

        bundle.putParcelable("superState", super.onSaveInstanceState());
        bundle.putString("selectedValue", selectedValue);
        bundle.putString("currentLanguage", currentLanguage != null ? currentLanguage.getCode() : null);
        bundle.putString("currentTheme", currentTheme != null ? currentTheme.getValue() : null);
        // Save animation state
        bundle.putFloat("animatedX", animatedX);
        bundle.putFloat("animatedWidth", animatedWidth);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;

            selectedValue = bundle.getString("selectedValue", "");

            String langCode = bundle.getString("currentLanguage");
            currentLanguage = langCode != null ? Language.fromCode(langCode) : null;

            String themeValue = bundle.getString("currentTheme");
            currentTheme = themeValue != null ? Theme.fromValue(themeValue) : null;

            animatedX = bundle.getFloat("animatedX", 0f);
            animatedWidth = bundle.getFloat("animatedWidth", 0f);

            super.onRestoreInstanceState(bundle.getParcelable("superState"));

            if (isInitialized()) {
                updateTheme();
                initPaints();
            }

            invalidate();
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    private void drawBackgroundLayer(Canvas canvas) {
        // LAYER 1: Control background - size exactly by content
        if (backgroundPaint == null) return;

        RectF backgroundRect =
                new RectF(contentOffsetX, 0, contentOffsetX + contentWidth, totalHeight);
        canvas.drawRoundRect(backgroundRect, cornerRadiusPx, cornerRadiusPx, backgroundPaint);
    }

    private void drawSelectionLayer(Canvas canvas) {
        if (selectedBackgroundPaint == null) return;

        // Selection rectangle with 1px inset from top and bottom
        RectF selectedRect = new RectF(
                animatedX + 1f,  // 1px inset from left
                1f,              // 1px inset from top
                animatedX + animatedWidth,
                totalHeight - 1f);  // 1px inset from bottom (symmetric with top)

        // Create gradient depending on theme
        LinearGradient gradient = createSelectionGradient(selectedRect);
        selectedBackgroundPaint.setShader(gradient);
        canvas.drawRoundRect(selectedRect, cornerRadiusPx, cornerRadiusPx, selectedBackgroundPaint);

        // Note: Original implementation has a transparent stroke, but testing showed
        // it doesn't improve pixel-perfect matching. The corner radius fix is sufficient.
    }

    private LinearGradient createSelectionGradient(RectF rect) {
        if (colors == null) {
            // Return a simple gray gradient if colors are not available
            return new LinearGradient(
                    rect.left, rect.top, rect.right, rect.bottom,
                    0xFF373F4A, 0xFF373F4A, Shader.TileMode.CLAMP);
        }

        if (currentTheme != null && currentTheme.isDreamer()) {
            // Horizontal gradient for dreamer theme
            return new LinearGradient(
                    rect.left,
                    0,
                    rect.right,
                    0,
                    colors.selectedGradientStart,
                    colors.selectedGradientEnd,
                    Shader.TileMode.CLAMP);
        } else {
            // Vertical gradient for free theme
            return new LinearGradient(
                    0,
                    rect.top,
                    0,
                    rect.bottom,
                    colors.selectedGradientStart,
                    colors.selectedGradientEnd,
                    Shader.TileMode.CLAMP);
        }
    }

    private void drawSelectionBorder(Canvas canvas, RectF selectedRect) {
        if (selectedBorderPaint == null) return;

        // Border is drawn 1px inside the selected rect (half of 2px stroke width)
        float borderInset = 1f;
        RectF borderRect =
                new RectF(
                        selectedRect.left + borderInset,
                        selectedRect.top + borderInset,
                        selectedRect.right - borderInset,
                        selectedRect.bottom - borderInset);

        LinearGradient borderGradient = null;
        if (colors != null) {
            borderGradient = new LinearGradient(
                    0,
                    borderRect.top,
                    0,
                    borderRect.bottom,
                    new int[] {
                        colors.selectedBorderTop,
                        colors.selectedBorderSide,
                        colors.selectedBorderBottom
                    },
                    new float[] {0f, 0.5f, 1f},
                    Shader.TileMode.CLAMP);
        }

        if (borderGradient != null) {
            selectedBorderPaint.setShader(borderGradient);
            float adjustedCornerRadius = Math.max(0, cornerRadiusPx - borderInset);
            canvas.drawRoundRect(
                    borderRect, adjustedCornerRadius, adjustedCornerRadius, selectedBorderPaint);
        }
    }

    private void drawTextLayer(Canvas canvas) {
        // LAYER 3: Text - width same as background layer
        if (textPaint == null) return;

        // Base text Y position (for unselected items)
        float textY = totalHeight / 2f - (textPaint.descent() + textPaint.ascent()) / 2f;

        // Find the target index (where animation is heading to)
        int targetIndex = findSelectedIndex();

        if (buttons != null && itemPositions != null && itemWidths != null) {
            for (int i = 0; i < buttons.size(); i++) {
                RadioButton button = buttons.get(i);
                if (button == null) continue;

                // Get text FIRST — needed for font selection
                String text = button.getText(currentLanguage != null ? currentLanguage.getCode() : "en");

                // Calculate text position - positions already include centering
                float itemWidth = itemWidths.get(i);
                float itemX = itemPositions.get(i); // already includes contentOffsetX
                float textX = itemX + itemWidth / 2f;

                // FINAL LOGIC: Only the target element can change color, and only when animation
                // touches it
                boolean isTargetElement = (i == targetIndex);
                boolean isAnimationTouchingThisText = isTextCoveredByAnimation(textX, itemWidth);

                // Color changes ONLY for target element AND ONLY when animation touches it
                boolean shouldUseSelectedColor = isTargetElement && isAnimationTouchingThisText;

                // Set text color - only target element changes color when animation touches it
                if (colors != null) {
                    textPaint.setColor(
                            shouldUseSelectedColor ? colors.selectedText : colors.unselectedText);
                }

                // Set font: bold for selected, regular for unselected
                if (shouldUseSelectedColor) {
                    textPaint.setTypeface(Font.getBold(getContext(), text));
                } else {
                    textPaint.setTypeface(Font.getRegular(getContext()));
                }

                // Set text size
                textPaint.setTextSize(textSizePx);

                // Unselected text needs +1px offset for visual centering compensation
                float itemTextY = shouldUseSelectedColor ? textY : textY + 1f;

                // Draw text (text variable already available)
                canvas.drawText(text, textX, itemTextY, textPaint);
            }
        }
    }

    /**
     * Determines if text is covered by animated background
     * @param textCenterX text center X coordinate
     * @param itemWidth item width
     * @return true if text is covered by animation
     */
    private boolean isTextCoveredByAnimation(float textCenterX, float itemWidth) {
        if (animatedWidth <= 0) return false;

        // Animated background bounds
        float animationLeft = animatedX;
        float animationRight = animatedX + animatedWidth;

        // Text bounds (with small margin for smoothness)
        float textLeftBound = textCenterX - itemWidth * 0.4f;
        float textRightBound = textCenterX + itemWidth * 0.4f;

        // Text is considered covered when animation overlaps with text bounds
        return animationRight >= textLeftBound && animationLeft <= textRightBound;
    }
}
