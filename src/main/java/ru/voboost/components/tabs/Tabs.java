package ru.voboost.components.tabs;

import java.util.ArrayList;
import java.util.List;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

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

    // Theme and Language
    private Theme currentTheme;
    private Language currentLanguage;

    // Paints
    private Paint sidebarBackgroundPaint;
    private Paint selectedBackgroundPaint;
    private TextPaint selectedTextPaint;
    private TextPaint unselectedTextPaint;

    // Animation
    private float animatedY = 0f;
    private boolean animatedYInitialized = false;
    private ValueAnimator selectionAnimator;

    // Calculated positions
    private List<Float> itemPositions = new ArrayList<>();

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

        // Configure text paints
        selectedTextPaint.setTextSize(TabsTheme.TEXT_SIZE);
        unselectedTextPaint.setTextSize(TabsTheme.TEXT_SIZE);

        // All tabs use bold font to match original implementation
        selectedTextPaint.setTypeface(Font.getBold(context, ""));
        unselectedTextPaint.setTypeface(Font.getBold(context, ""));
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
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
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
        this.selectedValue = value;

        // Animate to new position
        int newIndex = getIndexForValue(value);
        if (newIndex >= 0) {
            float targetY = itemPositions.get(newIndex);

            if (!animatedYInitialized) {
                // First render: set position immediately without animation
                animatedY = targetY;
                animatedYInitialized = true;
            } else if (animatedY != targetY) {
                // Subsequent selections: animate from current position to target
                animateToPosition(newIndex);
            }
            // If animatedY == targetY, no animation needed (same tab re-selected)

            // Notify listener if set
            if (onTabChangeListener != null) {
                onTabChangeListener.onTabChanged(newIndex);
            }
        }

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

        this.currentLanguage = language;
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

        return items.size() * TabsTheme.TAB_ITEM_HEIGHT
                + (items.size() - 1) * TabsTheme.TAB_ITEM_SPACING
                + TabsTheme.SIDEBAR_PADDING_BOTTOM;
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

            TextPaint textPaint = isSelected ? selectedTextPaint : unselectedTextPaint;

            // Set the correct bold font variant based on text content
            textPaint.setTypeface(Font.getBold(getContext(), text));

            // Calculate float-precision horizontal centering
            float textWidth = textPaint.measureText(text);
            float x = TabsTheme.SIDEBAR_PADDING_LEFT + (TabsTheme.TAB_ITEM_WIDTH - textWidth) / 2f;

            // Calculate vertical position to center text in tab item
            // Using baseline-based positioning with manual offset for visual alignment
            float textY = y + (TabsTheme.TAB_ITEM_HEIGHT + TabsTheme.TEXT_SIZE) / 2f - 6f;

            // Draw text with Paint.Align.LEFT (default)
            canvas.drawText(text, x, textY, textPaint);
        }
    }

    // ============================================================
    // ANIMATION
    // ============================================================

    private void animateToPosition(int index) {
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
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int index = getItemIndexAtPosition(event.getY());

            if (index >= 0 && index < items.size()) {
                String newValue = items.get(index).getValue();

                if (!newValue.equals(selectedValue)) {
                    setSelectedValue(newValue, true);
                }

                return true;
            }
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
        itemPositions.clear();

        float y = 0;
        for (int i = 0; i < items.size(); i++) {
            itemPositions.add(y);
            y += TabsTheme.TAB_ITEM_HEIGHT + TabsTheme.TAB_ITEM_SPACING;
        }

        // Set initial animated position
        int selectedIndex = getIndexForValue(selectedValue);
        if (selectedIndex >= 0 && selectedIndex < itemPositions.size()) {
            animatedY = itemPositions.get(selectedIndex);
        }
    }

    private int getIndexForValue(String value) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getValue().equals(value)) {
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

}
