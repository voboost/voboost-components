package ru.voboost.components.radio;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import ru.voboost.components.font.Font;
import ru.voboost.components.i18n.ILocalizable;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.IThemable;
import ru.voboost.components.theme.Theme;

/**
 * RadioPrimitive — internal canvas-based segmented control.
 * Package-private. Use Radio (LinearLayout container) for public API.
 */
class RadioPrimitive extends View implements IThemable, ILocalizable {

    // Data and state
    private List<RadioButton> buttons = new ArrayList<>();
    private Language currentLanguage = null;
    private Theme currentTheme = null;
    private String selectedValue = "";
    private OnValueChangeListener onValueChangeListener;

    // Theme and dimensions
    private RadioColors colors;

    // Fixed total width (0 = auto by text)
    private int fixedWidth = 0;

    // Layout measurements
    private List<Float> itemWidths = new ArrayList<>();
    private List<Float> itemPositions = new ArrayList<>();
    private float totalWidth = 0f;
    private float totalHeight = 0f;
    private float contentWidth = 0f;

    // Animation state
    private ValueAnimator positionAnimator;
    private ValueAnimator widthAnimator;
    private float animatedX = 0f;
    private float animatedWidth = 0f;

    // Paint objects for drawing
    private Paint backgroundPaint;
    private Paint selectedBackgroundPaint;
    private Paint textPaint;

    // NinePatch slider (FREE theme)
    private NinePatchDrawable sliderDrawable;
    private static volatile Bitmap sliderBitmap;

    // Reusable drawing objects (performance optimization)
    private final RectF backgroundRect = new RectF();
    private final RectF selectedRect = new RectF();

    // Cached gradient (performance optimization)
    private LinearGradient cachedSelectionGradient;
    private float cachedGradientLeft = Float.NaN;
    private float cachedGradientRight = Float.NaN;
    private float cachedGradientTop = Float.NaN;
    private float cachedGradientBottom = Float.NaN;

    // Dimensions in pixels
    private float heightPx;
    private float cornerRadiusPx;
    private float borderWidthPx;
    private float textSizePx;
    private float itemPaddingHorizontalPx;
    private float itemMinWidthPx;

    // Font cache (performance optimization)
    private final java.util.Map<String, android.graphics.Typeface> boldFontCache = new java.util.HashMap<>();

    /**
     * Callback for value selection changes.
     */
    public interface OnValueChangeListener {
        void onValueChange(String newValue);
    }

    /**
     * Checks if the component is fully initialized.
     *
     * @return true if both theme and language are set
     */
    private boolean isInitialized() {
        return currentTheme != null && currentLanguage != null;
    }

    public RadioPrimitive(Context context) {
        super(context);
        init();
    }

    public RadioPrimitive(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RadioPrimitive(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RadioPrimitive(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        // Initialize paint objects with base settings
        initPaintsWithDefaults();

        // Set pixel dimensions
        setDimensions();

        setLayerType(LAYER_TYPE_NONE, null);

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

            // Invalidate cached gradient when theme changes
            cachedSelectionGradient = null;
            cachedGradientLeft = Float.NaN;
            cachedGradientRight = Float.NaN;
            cachedGradientTop = Float.NaN;
            cachedGradientBottom = Float.NaN;
        }
    }

    private void initPaints() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (colors != null) {
            backgroundPaint.setColor(colors.background);
        }

        selectedBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        if (textPaint == null) {
            textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setTypeface(Font.getRegular(getContext()));
            textPaint.setTextAlign(Paint.Align.CENTER);
        }
    }

    private void initPaintsWithDefaults() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        selectedBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

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
    }

    /**
     * Sets the list of radio button options.
     *
     * @param buttons list of RadioButton objects
     */
    public void setButtons(List<RadioButton> buttons) {
        // Equality check to avoid unnecessary work on recomposition
        List<RadioButton> newButtons = buttons != null ? new ArrayList<>(buttons) : new ArrayList<>();
        if (java.util.Objects.equals(this.buttons, newButtons)) {
            return;
        }
        
        this.buttons = newButtons;

        // Populate bold font cache for all button texts
        boldFontCache.clear();
        if (this.buttons != null && currentLanguage != null) {
            for (RadioButton button : this.buttons) {
                if (button != null) {
                    String text = button.getText(currentLanguage.getCode());
                    if (text != null && !boldFontCache.containsKey(text)) {
                        boldFontCache.put(text, Font.getBold(getContext(), text));
                    }
                }
            }
        }

        measureItems();
        requestLayout(); // Force layout recalculation for dynamic width
        invalidate();
    }

    /**
     * Sets the display language.
     *
     * @param language language enum value
     * @throws IllegalArgumentException if language is null
     */
    public void setLanguage(Language language) {
        if (language == null) {
            throw new IllegalArgumentException("Language cannot be null");
        }

        if (!language.equals(this.currentLanguage)) {
            this.currentLanguage = language;

            // Repopulate bold font cache for new language
            boldFontCache.clear();
            if (buttons != null) {
                for (RadioButton button : buttons) {
                    if (button != null) {
                        String text = button.getText(language.getCode());
                        if (text != null && !boldFontCache.containsKey(text)) {
                            boldFontCache.put(text, Font.getBold(getContext(), text));
                        }
                    }
                }
            }

            // Store current selected index
            int selectedIndex = findSelectedIndex();

            // Cancel any running animations to prevent conflicts
            cancelAnimations();

            measureItemsForLanguageChange();
            requestLayout(); // Force layout recalculation for dynamic width

            // Immediately snap to correct position for selected item - no animation
            if (selectedIndex >= 0
                    && itemPositions != null
                    && itemWidths != null
                    && buttons != null
                    && selectedIndex < itemPositions.size()
                    && selectedIndex < itemWidths.size()
                    && selectedIndex < buttons.size()) {
                animatedX = itemPositions.get(selectedIndex);
                animatedWidth = itemWidths.get(selectedIndex);
            }

            invalidate();
        }
    }

    /**
     * Sets the visual theme.
     *
     * @param theme theme enum value
     * @throws IllegalArgumentException if theme is null
     */
    @Override
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

    @Override
    public void propagateTheme(Theme theme) {
        // Leaf component, no children to propagate to
    }

    @Override
    public void propagateLanguage(Language language) {
        // Leaf component, no children to propagate to
    }

    /**
     * Sets the selected value without animation.
     *
     * @param value             the value to select
     * @param isTriggerCallback whether to trigger onValueChangeListener
     */
    public void setSelectedValue(String value, boolean isTriggerCallback) {
        if (value != null && !value.equals(this.selectedValue)) {
            this.selectedValue = value;

            int selectedIndex = findSelectedIndex();
            if (selectedIndex >= 0
                    && itemPositions != null
                    && itemWidths != null
                    && buttons != null
                    && selectedIndex < itemPositions.size()
                    && selectedIndex < itemWidths.size()
                    && selectedIndex < buttons.size()) {
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
     * Sets the selected value without animation or callback.
     *
     * @param value the value to select
     */
    public void setSelectedValue(String value) {
        if (value != null) {
            setSelectedValue(value, false);
        }
    }

    /**
     * Returns the currently selected value.
     *
     * @return the selected value
     */
    public String getSelectedValue() {
        return selectedValue;
    }

    /**
     * Returns the current theme.
     *
     * @return the current theme, or null if not set
     */
    public Theme getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Returns the current language.
     *
     * @return the current language, or null if not set
     */
    public Language getCurrentLanguage() {
        return currentLanguage;
    }

    /**
     * Sets the value change listener.
     *
     * @param listener callback for value changes
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
            if (button != null
                    && selectedValue != null
                    && selectedValue.equals(button.getValue())) {
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
     *
     * @param updateAnimationPosition Whether to update animation position after
     *                                measurement
     */
    public void setWidth(int widthPx) {
        this.fixedWidth = widthPx;
        requestLayout();
    }

    public int getFixedWidth() {
        return fixedWidth;
    }

    private void measureItemsInternal(boolean updateAnimationPosition) {
        if (buttons == null || buttons.isEmpty()) {
            return;
        }

        itemWidths.clear();
        itemPositions.clear();

        // Calculate equal width for all items (like layout_weight="1")
        int itemCount = buttons.size();
        float currentX = 0f;

        float equalItemWidth;

        if (fixedWidth > 0) {
            // Fixed width = visible content width, no animation padding
            equalItemWidth = (float) fixedWidth / itemCount;
        } else {
            // Auto: based on widest text
            float maxTextWidth = 0f;
            for (RadioButton button : buttons) {
                if (button == null)
                    continue;

                String text = button.getText(currentLanguage != null ? currentLanguage.getCode() : "en");

                // Measure with bold typeface to ensure enough space for selected state
                textPaint.setTypeface(Font.getBold(getContext(), text));
                float boldTextWidth = textPaint != null ? textPaint.measureText(text) : 0;
                maxTextWidth = Math.max(maxTextWidth, boldTextWidth);
            }

            // Calculate equal width for all items based on the widest text
            equalItemWidth = Math.max(maxTextWidth + 2 * itemPaddingHorizontalPx, itemMinWidthPx);
        }

        // Assign equal width to all items
        for (int i = 0; i < itemCount; i++) {
            itemWidths.add(equalItemWidth);
            itemPositions.add(currentX);
            currentX += equalItemWidth;
        }

        contentWidth = currentX;
        totalHeight = heightPx;
        totalWidth = contentWidth;

        if (updateAnimationPosition) {
            updateAnimationPosition();
        }
    }

    private void updateAnimationPosition() {
        int selectedIndex = findSelectedIndex();

        if (selectedIndex >= 0
                && itemPositions != null
                && itemWidths != null
                && buttons != null
                && selectedIndex < itemPositions.size()
                && selectedIndex < itemWidths.size()
                && selectedIndex < buttons.size()) {
            animatedX = itemPositions.get(selectedIndex);
            animatedWidth = itemWidths.get(selectedIndex);
        }
    }

    private void animateToPosition(float targetX, float targetWidth) {
        cancelAnimations();

        final OvershootInterpolator interpolator = new OvershootInterpolator(RadioDimensions.OVERSHOOT_TENSION);

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

        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);

        // If parent constrains width and our calculated width exceeds it, redistribute items
        // (only for auto-width mode; fixed-width mode reports totalWidth including animation padding)
        if (fixedWidth <= 0
                && (specMode == MeasureSpec.AT_MOST || specMode == MeasureSpec.EXACTLY)
                && totalWidth > specSize && buttons != null && !buttons.isEmpty()) {
            fitItemsToWidth(specSize);
        }

        int width = fixedWidth > 0 ? fixedWidth : (int) Math.ceil(totalWidth);
        int height = (int) Math.ceil(totalHeight);

        if (fixedWidth > 0) {
            setMeasuredDimension(width, height);
        } else {
            setMeasuredDimension(resolveSize(width, widthMeasureSpec), height);
        }
    }

    /**
     * Redistributes items equally to fit within the given width constraint.
     */
    private void fitItemsToWidth(float availableWidth) {
        if (availableWidth <= 0 || buttons == null || buttons.isEmpty()) return;

        int itemCount = buttons.size();
        float constrainedItemWidth = availableWidth / itemCount;

        itemWidths.clear();
        itemPositions.clear();
        float currentX = 0f;
        for (int i = 0; i < itemCount; i++) {
            itemWidths.add(constrainedItemWidth);
            itemPositions.add(currentX);
            currentX += constrainedItemWidth;
        }

        contentWidth = currentX;
        totalWidth = contentWidth;

        updateAnimationPosition();
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

            // Find which item was touched
            if (itemPositions != null && itemWidths != null && buttons != null) {
                for (int i = 0; i < itemPositions.size(); i++) {
                    float itemStart = itemPositions.get(i); // correct for current mode
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

            if (selectedIndex >= 0
                    && itemPositions != null
                    && itemWidths != null
                    && buttons != null
                    && selectedIndex < itemPositions.size()
                    && selectedIndex < itemWidths.size()
                    && selectedIndex < buttons.size()) {
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

    private void drawBackgroundLayer(Canvas canvas) {
        // LAYER 1: Control background - size exactly by content
        if (backgroundPaint == null)
            return;

        backgroundRect.set(0, 0, contentWidth, totalHeight);
        canvas.drawRoundRect(backgroundRect, cornerRadiusPx, cornerRadiusPx, backgroundPaint);
    }

    private void drawSelectionLayer(Canvas canvas) {
        if (selectedBackgroundPaint == null)
            return;

        if (currentTheme != null && currentTheme.isFree()) {
            drawSliderNinePatch(canvas);
            return;
        }
        drawSelectionParametric(canvas);
    }

    private void drawSliderNinePatch(Canvas canvas) {
        NinePatchDrawable npd = ensureSliderDrawable();
        if (npd == null) {
            drawSelectionParametric(canvas);
            return;
        }
        int w = Math.round(animatedWidth);
        int h = (int) totalHeight;
        npd.setBounds(0, 0, w, h);
        canvas.save();
        canvas.translate((int) animatedX, 0f);
        npd.draw(canvas);
        canvas.restore();
    }

    private NinePatchDrawable ensureSliderDrawable() {
        if (sliderDrawable == null) {
            Bitmap bmp = getSliderBitmap();
            if (bmp == null) return null;
            byte[] chunk = bmp.getNinePatchChunk();
            if (chunk == null) return null;
            sliderDrawable = new NinePatchDrawable(
                    getResources(), bmp, chunk, new Rect(), null);
        }
        return sliderDrawable;
    }

    private static Bitmap getSliderBitmap() {
        Bitmap b = sliderBitmap;
        if (b == null) {
            synchronized (RadioPrimitive.class) {
                b = sliderBitmap;
                if (b == null) {
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inScaled = false;
                    try (InputStream in = RadioPrimitive.class
                            .getResourceAsStream("RadioPrimitive.png")) {
                        if (in == null) return null;
                        b = BitmapFactory.decodeStream(in, null, opts);
                    } catch (IOException e) {
                        return null;
                    }
                    sliderBitmap = b;
                }
            }
        }
        return b;
    }

    private void drawSelectionParametric(Canvas canvas) {
        selectedRect.set(
                animatedX + 1f,
                1f,
                animatedX + animatedWidth,
                totalHeight - 1f);

        LinearGradient gradient = createSelectionGradient(selectedRect);
        selectedBackgroundPaint.setShader(gradient);
        canvas.drawRoundRect(selectedRect, cornerRadiusPx, cornerRadiusPx, selectedBackgroundPaint);
    }

    private LinearGradient createSelectionGradient(RectF rect) {
        if (colors == null) {
            return null;
        }

        if (currentTheme != null && currentTheme.isDreamer()) {
            if (cachedSelectionGradient == null || cachedGradientLeft != rect.left || cachedGradientRight != rect.right) {
                cachedSelectionGradient = new LinearGradient(
                        rect.left,
                        0,
                        rect.right,
                        0,
                        colors.selectedGradientStart,
                        colors.selectedGradientEnd,
                        Shader.TileMode.CLAMP);
                cachedGradientLeft = rect.left;
                cachedGradientRight = rect.right;
            }
            return cachedSelectionGradient;
        } else {
            if (cachedSelectionGradient == null || cachedGradientTop != rect.top || cachedGradientBottom != rect.bottom) {
                cachedSelectionGradient = new LinearGradient(
                        0,
                        rect.top,
                        0,
                        rect.bottom,
                        colors.selectedGradientStart,
                        colors.selectedGradientEnd,
                        Shader.TileMode.CLAMP);
                cachedGradientTop = rect.top;
                cachedGradientBottom = rect.bottom;
            }
            return cachedSelectionGradient;
        }
    }

    private void drawTextLayer(Canvas canvas) {
        // LAYER 3: Text - width same as background layer
        if (textPaint == null)
            return;

        // Base text Y position (for unselected items)
        float textY = totalHeight / 2f - (textPaint.descent() + textPaint.ascent()) / 2f - 1f;

        // Find the target index (where animation is heading to)
        int targetIndex = findSelectedIndex();

        if (buttons != null && itemPositions != null && itemWidths != null) {
            for (int i = 0; i < buttons.size(); i++) {
                RadioButton button = buttons.get(i);
                if (button == null)
                    continue;

                // Get text FIRST — needed for font selection
                String text = button.getText(currentLanguage != null ? currentLanguage.getCode() : "en");

                // Calculate text position - positions are correct for current mode
                float itemWidth = itemWidths.get(i);
                float itemX = itemPositions.get(i); // includes offset in CHILD_MARGIN mode
                float textX = itemX + itemWidth / 2f;

                // FINAL LOGIC: Only the target element can change color, and only when
                // animation
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
                android.graphics.Typeface typeface = boldFontCache.get(text);
                if (typeface != null) {
                    textPaint.setTypeface(typeface);
                } else {
                    textPaint.setTypeface(Font.getBold(getContext(), text));
                }

                // Set text size
                textPaint.setTextSize(textSizePx);

                float itemTextY = textY + 1f;

                // Draw text (text variable already available)
                canvas.drawText(text, textX, itemTextY, textPaint);
            }
        }
    }

    /**
     * Determines if text is covered by animated background
     *
     * @param textCenterX text center X coordinate
     * @param itemWidth   item width
     * @return true if text is covered by animation
     */
    private boolean isTextCoveredByAnimation(float textCenterX, float itemWidth) {
        if (animatedWidth <= 0)
            return false;

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
