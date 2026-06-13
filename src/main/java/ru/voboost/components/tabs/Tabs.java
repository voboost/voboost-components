package ru.voboost.components.tabs;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.voboost.components.font.Font;
import ru.voboost.components.i18n.ILocalizable;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.IThemable;
import ru.voboost.components.theme.Theme;

/**
 * Tabs component - A vertical navigation sidebar with animated selection
 * indicator.
 *
 * <p>
 * This component displays a list of tab items vertically with a sliding
 * selection indicator that animates between tabs.
 *
 * <p>
 * Features:
 * <ul>
 * <li>Vertical tab layout</li>
 * <li>Animated selection indicator</li>
 * <li>Multi-theme support (Free/Dreamer, Light/Dark)</li>
 * <li>Multi-language support (EN/RU)</li>
 * <li>Touch event handling</li>
 * </ul>
 *
 * <p>
 * Usage:
 *
 * <pre>
 * Tabs tabs = new Tabs(context);
 * tabs.setTheme(Theme.FREE_LIGHT);
 * tabs.setLanguage(Language.EN);
 * tabs.setItems(tabItems);
 * tabs.setSelectedValue("settings");
 * tabs.setOnValueChangeListener(value -> {
 *     // Handle tab selection
 * });
 * </pre>
 */
public class Tabs extends View implements IThemable, ILocalizable {

    // Data
    private List<TabItem> items = new ArrayList<>();
    private String selectedValue = "";

    // Pressed item index (-1 = no item pressed). Used only for more-indicator color in drawTabItems.
    private int pressedIndex = -1;

    // Theme and Language
    private Theme currentTheme;
    private Language currentLanguage;

    // Padding (internal — separate from View.getPaddingTop/Bottom)
    private int topPadding = TabsTheme.DEFAULT_TOP_PADDING;
    private int paddingBottom = 5;

    /** Bottom padding when at least one tab has more=true */
    private static final int PADDING_BOTTOM_MORE = 25;
    /** Bottom padding when no tab has more=true */
    private static final int PADDING_BOTTOM_DEFAULT = 5;

    // Paints
    private Paint sidebarBackgroundPaint;
    private Paint selectedBackgroundPaint;
    private TextPaint selectedTextPaint;
    private TextPaint unselectedTextPaint;
    private TextPaint disabledTextPaint;
    private TextPaint pressedTextPaint;

    // More-indicator bitmaps (lazy-loaded from classpath alongside Tabs.class), per-theme variant
    private static volatile Bitmap moreBitmapDark;
    private static volatile Bitmap morePressedBitmapDark;
    private static volatile Bitmap moreBitmapLight;
    private static volatile Bitmap morePressedBitmapLight;

    // Animation
    private float animatedY = 0f;
    private boolean animatedYInitialized = false;
    private boolean animationsEnabled = true;
    private ValueAnimator selectionAnimator;

    // Calculated positions
    private List<Float> itemPositions = new ArrayList<>();

    // Font cache (performance optimization)
    private final java.util.Map<String, android.graphics.Typeface> boldFontCache = new java.util.HashMap<>();

    // Callbacks
    private OnValueChangeListener onValueChangeListener;
    private OnTabChangeListener onTabChangeListener;

    /**
     * Callback interface for tab selection changes.
     */
    public interface OnValueChangeListener {
        /**
         * Called when the selected tab changes.
         *
         * @param value the value of the newly selected tab
         */
        void onValueChange(String value);
    }

    /**
     * Callback interface for tab selection changes.
     */
    public interface OnTabChangeListener {
        /**
         * Called when the selected tab changes.
         *
         * @param newIndex the index of the newly selected tab
         */
        void onTabChanged(int newIndex);
    }

    // ============================================================
    // CONSTRUCTORS
    // ============================================================

    public Tabs(Context context) {
        super(context);
        init(context);
    }

    public Tabs(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Tabs(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public Tabs(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    // ============================================================
    // INITIALIZATION
    // ============================================================

    private void init(Context context) {
        // Enable hardware acceleration for better performance
        setLayerType(LAYER_TYPE_HARDWARE, null);

        // Initialize paints
        sidebarBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectedBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectedTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        unselectedTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        disabledTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        pressedTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

        // Configure text paints
        selectedTextPaint.setTextSize(TabsTheme.TEXT_SIZE);
        unselectedTextPaint.setTextSize(TabsTheme.TEXT_SIZE);
        disabledTextPaint.setTextSize(TabsTheme.TEXT_SIZE);
        pressedTextPaint.setTextSize(TabsTheme.TEXT_SIZE);

        // All tabs use bold font to match original implementation
        selectedTextPaint.setTypeface(Font.getBold(context, ""));
        unselectedTextPaint.setTypeface(Font.getBold(context, ""));
        disabledTextPaint.setTypeface(Font.getBold(context, ""));
        pressedTextPaint.setTypeface(Font.getBold(context, ""));
    }

    // ============================================================
    // PUBLIC API
    // ============================================================

    /**
     * Sets the list of tab items.
     *
     * @param items the list of TabItem objects
     */
    public void setItems(List<TabItem> items) {
        // Create defensive copy
        List<TabItem> newItems = items != null ? new ArrayList<>(items) : new ArrayList<>();

        // Validate no null items in the list
        for (TabItem item : newItems) {
            if (item == null) {
                throw new IllegalArgumentException("TabItem list cannot contain null elements");
            }
        }

        // Equality check to avoid unnecessary work on recomposition
        if (java.util.Objects.equals(this.items, newItems)) {
            return;
        }

        this.items = newItems;

        // Auto-set bottom padding based on whether any tab has more=true
        boolean hasMore = false;
        for (TabItem item : this.items) {
            if (item.hasMore()) {
                hasMore = true;
                break;
            }
        }
        paddingBottom = hasMore ? PADDING_BOTTOM_MORE : PADDING_BOTTOM_DEFAULT;

        // Populate bold font cache for all item texts
        boldFontCache.clear();
        if (currentLanguage != null) {
            for (TabItem item : this.items) {
                String text = item.getText(currentLanguage.getCode());
                if (text != null && !boldFontCache.containsKey(text)) {
                    boldFontCache.put(text, Font.getBold(getContext(), text));
                }
            }
        }

        animatedYInitialized = false;
        calculateItemPositions();
        requestLayout();
        invalidate();
    }

    /**
     * Sets the currently selected tab value.
     *
     * @param value the value of the tab to select
     */
    public void setSelectedValue(String value) {
        setSelectedValue(value, false);
    }

    /**
     * Sets the currently selected tab value with optional callback trigger.
     *
     * @param value           the value of the tab to select
     * @param triggerCallback whether to trigger the onValueChangeListener
     */
    public void setSelectedValue(String value, boolean triggerCallback) {
        if (value == null) {
            value = "";
        }

        String oldValue = this.selectedValue;

        // Validate and animate BEFORE changing state
        int newIndex = getIndexForValue(value);
        if (newIndex >= 0) {
            TabItem item = items.get(newIndex);
            if (item == null || !item.isEnabled()) {
                return; // Exit without changing state
            }

            float targetY = itemPositions.get(newIndex);

            if (!animatedYInitialized) {
                animatedY = targetY;
                animatedYInitialized = true;
            } else if (animatedY != targetY) {
                if (animationsEnabled) {
                    animateToPosition(newIndex);
                } else {
                    animatedY = targetY;
                }
            }

            if (onTabChangeListener != null) {
                onTabChangeListener.onTabChanged(newIndex);
            }
        }

        // Change state AFTER validation
        this.selectedValue = value;

        if (triggerCallback && !value.equals(oldValue) && onValueChangeListener != null) {
            onValueChangeListener.onValueChange(value);
        }

        invalidate();
    }

    /**
     * Returns the currently selected tab value.
     *
     * @return the selected tab value
     */
    public String getSelectedValue() {
        return selectedValue;
    }

    /**
     * Returns the index of the currently selected tab.
     *
     * @return the index of the selected tab, or -1 if no tab is selected
     */
    public int getSelectedIndex() {
        return getIndexForValue(selectedValue);
    }

    /**
     * Sets the theme for the component.
     *
     * @param theme the theme to apply
     * @throws IllegalArgumentException if theme is null
     */
    @Override
    public void setTheme(Theme theme) {
        if (theme == null) {
            throw new IllegalArgumentException("Theme cannot be null");
        }

        this.currentTheme = theme;
        updateColors();
        invalidate();
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

        // Clear cache FIRST
        boldFontCache.clear();

        this.currentLanguage = language;

        // Repopulate bold font cache for new language
        if (items != null) {
            for (TabItem item : items) {
                if (item != null) {
                    String text = item.getText(language.getCode());
                    if (text != null && !boldFontCache.containsKey(text)) {
                        boldFontCache.put(text, Font.getBold(getContext(), text));
                    }
                }
            }
        }

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
     * Returns the bottom padding of the sidebar.
     */
    public int getPaddingBottom() {
        return paddingBottom;
    }

    /**
     * Returns the top padding inside the Tabs view.
     */
    public int getTopPadding() {
        return topPadding;
    }

    /**
     * Sets the top padding inside the Tabs view.
     */
    public void setTopPadding(int topPadding) {
        this.topPadding = topPadding;
        calculateItemPositions();
        requestLayout();
        invalidate();
    }

    /**
     * Enables/disables the selection indicator slide animation.
     * Disabled in pixel tests so a single rendered frame is deterministic.
     */
    public void setAnimationsEnabled(boolean enabled) {
        this.animationsEnabled = enabled;
    }

    /** Returns whether the selection indicator animation is enabled. */
    public boolean isAnimationsEnabled() {
        return animationsEnabled;
    }

    /**
     * Sets the callback for tab selection changes.
     *
     * @param listener the callback listener
     */
    public void setOnValueChangeListener(OnValueChangeListener listener) {
        this.onValueChangeListener = listener;
    }

    /**
     * Sets the callback for tab selection changes.
     *
     * @param listener the callback listener
     */
    public void setOnTabChangeListener(OnTabChangeListener listener) {
        this.onTabChangeListener = listener;
    }

    /**
     * Returns the total width of the Tabs sidebar.
     * Screen and other layout containers use this to position content.
     *
     * @return the sidebar width in pixels
     */
    public static int getSidebarWidth() {
        return TabsTheme.SIDEBAR_WIDTH;
    }

    // ============================================================
    // MEASUREMENT
    // ============================================================

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = TabsTheme.SIDEBAR_WIDTH;
        int height = calculateTotalHeight();

        // Report natural height without constraint resolution
        setMeasuredDimension(width, height);
    }

    private int calculateTotalHeight() {
        if (items.isEmpty()) {
            return 0;
        }

        int height = topPadding + items.size() * TabsTheme.TAB_ITEM_HEIGHT;
        // Sum marginTop of all items except first (first item has no top margin)
        for (int i = 1; i < items.size(); i++) {
            height += items.get(i).getMarginTop();
        }
        height += getPaddingBottom();
        return height;
    }

    // ============================================================
    // DRAWING
    // ============================================================

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (currentTheme == null || currentLanguage == null || items.isEmpty()) {
            return;
        }

        // Draw sidebar background
        canvas.drawRect(0, 0, getWidth(), getHeight(), sidebarBackgroundPaint);

        // Draw selection indicator
        drawSelectionIndicator(canvas);

        // Draw tab items
        drawTabItems(canvas);
    }

    private void drawSelectionIndicator(Canvas canvas) {
        if (selectedValue.isEmpty()) {
            return;
        }

        float left = TabsTheme.SIDEBAR_PADDING_LEFT;
        float right = TabsTheme.SIDEBAR_PADDING_LEFT + TabsTheme.TAB_ITEM_WIDTH;
        float top = animatedY;
        float bottom = top + TabsTheme.TAB_ITEM_HEIGHT;

        RectF rect = new RectF(left, top, right, bottom);
        canvas.drawRoundRect(
                rect, TabsTheme.CORNER_RADIUS, TabsTheme.CORNER_RADIUS, selectedBackgroundPaint);
    }

    private void drawTabItems(Canvas canvas) {
        String langCode = currentLanguage.getCode();

        for (int i = 0; i < items.size(); i++) {
            TabItem item = items.get(i);
            float y = itemPositions.get(i);

            String text = item.getText(langCode);
            boolean isSelected = item.getValue().equals(selectedValue);

            TextPaint textPaint;
            if (!item.isEnabled()) {
                textPaint = disabledTextPaint;
            } else if (i == pressedIndex && item.hasMore()) {
                textPaint = pressedTextPaint;
            } else if (isSelected) {
                textPaint = selectedTextPaint;
            } else {
                textPaint = unselectedTextPaint;
            }

            // Set the correct bold font variant based on text content (use cache)
            android.graphics.Typeface cachedTypeface = boldFontCache.get(text);
            if (cachedTypeface != null) {
                textPaint.setTypeface(cachedTypeface);
            } else {
                textPaint.setTypeface(Font.getBold(getContext(), text));
            }

            // Calculate float-precision horizontal centering
            float textWidth = textPaint.measureText(text);
            float x = TabsTheme.SIDEBAR_PADDING_LEFT + (TabsTheme.TAB_ITEM_WIDTH - textWidth) / 2f;

            // Calculate vertical position to center text in tab item
            // Using baseline-based positioning with manual offset for visual alignment
            float textY = y + (TabsTheme.TAB_ITEM_HEIGHT + TabsTheme.TEXT_SIZE) / 2f + TabsTheme.TEXT_BASELINE_OFFSET;

            // Draw text with Paint.Align.LEFT (default)
            canvas.drawText(text, x, textY, textPaint);

            // Draw trailing ">" indicator if requested
            if (item.hasMore()) {
                drawMore(canvas, y, i == pressedIndex);
            }
        }
    }

    private void drawMore(Canvas canvas, float tabTop, boolean pressed) {
        boolean light = currentTheme.isLight();
        Bitmap bitmap = pressed ? getMorePressedBitmap(light) : getMoreBitmap(light);
        if (bitmap == null) {
            return;
        }

        float right = TabsTheme.SIDEBAR_PADDING_LEFT + TabsTheme.TAB_ITEM_WIDTH
                - TabsTheme.MORE_MARGIN_END;
        float left = right - TabsTheme.MORE_SIZE;
        float top = tabTop + (TabsTheme.TAB_ITEM_HEIGHT - TabsTheme.MORE_SIZE) / 2f;

        canvas.drawBitmap(bitmap, left, top, null);
    }

    private static Bitmap getMoreBitmap(boolean light) {
        if (light) {
            Bitmap b = moreBitmapLight;
            if (b == null) {
                synchronized (Tabs.class) {
                    b = moreBitmapLight;
                    if (b == null) {
                        b = loadBitmap("Tabs_theme_light.png");
                        moreBitmapLight = b;
                    }
                }
            }
            return b;
        }
        Bitmap b = moreBitmapDark;
        if (b == null) {
            synchronized (Tabs.class) {
                b = moreBitmapDark;
                if (b == null) {
                    b = loadBitmap("Tabs_theme_dark.png");
                    moreBitmapDark = b;
                }
            }
        }
        return b;
    }

    private static Bitmap getMorePressedBitmap(boolean light) {
        if (light) {
            Bitmap b = morePressedBitmapLight;
            if (b == null) {
                synchronized (Tabs.class) {
                    b = morePressedBitmapLight;
                    if (b == null) {
                        b = loadBitmap("Tabs_theme_light.Tabs_pressed.png");
                        morePressedBitmapLight = b;
                    }
                }
            }
            return b;
        }
        Bitmap b = morePressedBitmapDark;
        if (b == null) {
            synchronized (Tabs.class) {
                b = morePressedBitmapDark;
                if (b == null) {
                    b = loadBitmap("Tabs_theme_dark.Tabs_pressed.png");
                    morePressedBitmapDark = b;
                }
            }
        }
        return b;
    }

    private static Bitmap loadBitmap(String name) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inScaled = false;
        try (InputStream in = Tabs.class.getResourceAsStream(name)) {
            if (in == null) {
                return null;
            }
            return BitmapFactory.decodeStream(in, null, opts);
        } catch (IOException e) {
            return null;
        }
    }

    // ============================================================
    // ANIMATION
    // ============================================================

    private void animateToPosition(int index) {
        if (itemPositions == null || itemPositions.isEmpty()) {
            return;
        }
        if (index < 0 || index >= itemPositions.size()) {
            return;
        }

        float targetY = itemPositions.get(index);

        if (selectionAnimator != null && selectionAnimator.isRunning()) {
            selectionAnimator.cancel();
        }

        selectionAnimator = ValueAnimator.ofFloat(animatedY, targetY);
        selectionAnimator.setDuration(TabsTheme.ANIMATION_DURATION);
        selectionAnimator.setInterpolator(new OvershootInterpolator(1.0f));
        selectionAnimator.addUpdateListener(
                animation -> {
                    animatedY = (float) animation.getAnimatedValue();
                    invalidate();
                });
        selectionAnimator.start();
    }

    // ============================================================
    // TOUCH HANDLING
    // ============================================================

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float y = event.getY();

        if (action == MotionEvent.ACTION_DOWN) {
            int index = getItemIndexAtPosition(y);
            if (index >= 0 && index < items.size()) {
                TabItem item = items.get(index);
                if (!item.isEnabled()) {
                    return true;
                }
                pressedIndex = index;
                invalidate();
                return true;
            }
            return super.onTouchEvent(event);
        }

        if (action == MotionEvent.ACTION_MOVE) {
            if (pressedIndex >= 0) {
                int index = getItemIndexAtPosition(y);
                if (index != pressedIndex) {
                    pressedIndex = -1;
                    invalidate();
                }
            }
            return true;
        }

        if (action == MotionEvent.ACTION_UP) {
            if (pressedIndex >= 0) {
                int index = getItemIndexAtPosition(y);
                int pressed = pressedIndex;
                pressedIndex = -1;
                invalidate();

                if (index == pressed && pressed < items.size()) {
                    TabItem item = items.get(pressed);
                    if (item.isEnabled()) {
                        String newValue = item.getValue();
                        if (item.hasMore()) {
                            if (onValueChangeListener != null) {
                                onValueChangeListener.onValueChange(newValue);
                            }
                        } else if (!newValue.equals(selectedValue)) {
                            setSelectedValue(newValue, true);
                        }
                    }
                }
                return true;
            }
            return super.onTouchEvent(event);
        }

        if (action == MotionEvent.ACTION_CANCEL) {
            if (pressedIndex >= 0) {
                pressedIndex = -1;
                invalidate();
            }
            return true;
        }

        return super.onTouchEvent(event);
    }

    private int getItemIndexAtPosition(float y) {
        for (int i = 0; i < itemPositions.size(); i++) {
            float itemTop = itemPositions.get(i);
            float itemBottom = itemTop + TabsTheme.TAB_ITEM_HEIGHT;

            if (y >= itemTop && y <= itemBottom) {
                return i;
            }
        }

        return -1;
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================

    private void calculateItemPositions() {
        if (items == null) {
            return;
        }

        itemPositions.clear();

        float y = topPadding;
        for (int i = 0; i < items.size(); i++) {
            TabItem item = items.get(i);
            if (i > 0) {
                y += item.getMarginTop();
            }
            itemPositions.add(y);
            y += TabsTheme.TAB_ITEM_HEIGHT;
        }

        // Set initial animated position
        int selectedIndex = getIndexForValue(selectedValue);
        if (selectedIndex >= 0 && selectedIndex < itemPositions.size()) {
            animatedY = itemPositions.get(selectedIndex);
        }
    }

    private int getIndexForValue(String value) {
        if (items == null || items.isEmpty()) {
            return -1;
        }

        for (int i = 0; i < items.size(); i++) {
            TabItem item = items.get(i);
            if (item != null && item.getValue().equals(value)) {
                return i;
            }
        }

        return -1;
    }

    private void updateColors() {
        if (currentTheme == null) {
            return;
        }

        sidebarBackgroundPaint.setColor(TabsTheme.getSidebarBackground(currentTheme));
        selectedBackgroundPaint.setColor(TabsTheme.getSelectedBackground(currentTheme));
        selectedTextPaint.setColor(TabsTheme.getSelectedTextColor(currentTheme));
        unselectedTextPaint.setColor(TabsTheme.getUnselectedTextColor(currentTheme));
        disabledTextPaint.setColor(TabsTheme.getDisabledTextColor(currentTheme));
        pressedTextPaint.setColor(TabsTheme.getPressedTextColor(currentTheme));
    }

    // ============================================================
    // LIFECYCLE
    // ============================================================

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (selectionAnimator != null) {
            selectionAnimator.cancel();
            selectionAnimator = null;
        }
    }

    // ============================================================
    // BUILDER API
    // ============================================================

    /**
     * Creates a new Builder for Tabs.
     *
     * @param context the Android context
     * @param theme the theme to apply
     * @param language the language to apply
     * @param items the list of TabItem objects
     * @return a new Builder instance
     */
    @NonNull
    public static Builder create(@NonNull android.content.Context context,
                                 @NonNull Theme theme,
                                 @NonNull Language language,
                                 @NonNull List<TabItem> items) {
        return new Builder(context, theme, language, items);
    }

    /**
     * Builder for creating Tabs instances with a fluent API.
     */
    public static class Builder {
        private final android.content.Context context;
        private final Theme theme;
        private final Language language;
        private final List<TabItem> items;
        private OnValueChangeListener onValueChangeListener;
        private OnTabChangeListener onTabChangeListener;

        private Builder(android.content.Context context, Theme theme, Language language, List<TabItem> items) {
            this.context = context;
            this.theme = theme;
            this.language = language;
            this.items = items;
        }

        /**
         * Sets the callback for tab selection value changes.
         *
         * @param listener the callback listener
         * @return this Builder instance
         */
        @NonNull
        public Builder onValueChange(@Nullable OnValueChangeListener listener) {
            this.onValueChangeListener = listener;
            return this;
        }

        /**
         * Sets the callback for tab selection index changes.
         *
         * @param listener the callback listener
         * @return this Builder instance
         */
        @NonNull
        public Builder onTabChange(@Nullable OnTabChangeListener listener) {
            this.onTabChangeListener = listener;
            return this;
        }

        /**
         * Builds and returns the Tabs instance.
         *
         * @return a new Tabs instance
         */
        @NonNull
        public Tabs build() {
            Tabs tabs = new Tabs(context);
            tabs.setTheme(theme);
            tabs.setLanguage(language);
            tabs.setItems(items);

            // Find selected TabItem
            for (TabItem item : items) {
                if (item.isSelected()) {
                    tabs.setSelectedValue(item.getValue(), false);
                    break;
                }
            }

            if (onValueChangeListener != null) {
                tabs.setOnValueChangeListener(onValueChangeListener);
            }
            if (onTabChangeListener != null) {
                tabs.setOnTabChangeListener(onTabChangeListener);
            }

            return tabs;
        }
    }

}
