package ru.voboost.components.select;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.voboost.components.font.Font;
import ru.voboost.components.i18n.ILocalizable;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.IThemable;
import ru.voboost.components.theme.Theme;

/**
 * Select component — trigger button + popup with 3D WheelView.
 *
 * <p>
 * The trigger is a canvas-based View showing the currently selected
 * value with a chevron. Tapping opens a Popup with a 3D wheel picker.
 */
public class Select extends View implements IThemable, ILocalizable {
    // Data and state
    private List<SelectOption> options = new ArrayList<>();
    private Language currentLanguage = null;
    private Theme currentTheme = null;
    private String selectedValue = "";
    private OnValueChangeListener onValueChangeListener;

    // Margin (managed by parent Section)
    private int marginLeft = 0;
    private int marginTop = 0;
    private int marginRight = 0;
    private int marginBottom = 0;
    private boolean marginSet = false;

    public int getMarginLeft() { return marginLeft; }
    public int getMarginTop() { return marginTop; }
    public int getMarginRight() { return marginRight; }
    public int getMarginBottom() { return marginBottom; }
    public boolean isMarginSet() { return marginSet; }

    public void setMargin(int left, int top, int right, int bottom) {
        this.marginLeft = left;
        this.marginTop = top;
        this.marginRight = right;
        this.marginBottom = bottom;
        this.marginSet = true;
    }

    // Theme colors
    private SelectColors colors;

    // Popup
    private SelectPopup selectPopup;

    // Paint
    private Paint backgroundPaint;
    private Paint textPaint;
    private Paint chevronPaint;

    // Pre-allocated RectF for drawing (reduces GC pressure)
    private final RectF drawRectF = new RectF();

    /**
     * Callback for value selection changes.
     */
    public interface OnValueChangeListener {
        void onValueChange(String newValue);
    }

    public Select(Context context) {
        super(context);
        init();
    }

    public Select(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Select(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public Select(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(SelectDimensions.TRIGGER_TEXT_SIZE_PX);
        textPaint.setTypeface(Font.getRegular(getContext()));

        chevronPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        chevronPaint.setTextAlign(Paint.Align.RIGHT);
        chevronPaint.setTextSize(SelectDimensions.CHEVRON_SIZE_PX);
        chevronPaint.setTypeface(Font.getRegular(getContext()));

        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    private void updateColors() {
        if (currentTheme != null) {
            colors = SelectTheme.getColors(currentTheme);
        }
    }

    private boolean isInitialized() {
        return currentTheme != null && currentLanguage != null;
    }

    // --- Public API ---

    /**
     * Sets the list of select options.
     * Null values are treated as empty list.
     *
     * @param options list of SelectOption items, or null to clear
     */
    public void setOptions(List<SelectOption> options) {
        // Equality check to avoid unnecessary work on recomposition
        List<SelectOption> newOptions = options != null ? new ArrayList<>(options) : new ArrayList<>();
        if (java.util.Objects.equals(this.options, newOptions)) {
            return;
        }

        // Validate each option (defensive copy already validates in constructor)
        this.options = newOptions;
        requestLayout();
        invalidate();
    }

    /**
     * Sets the display language.
     */
    public void setLanguage(Language language) {
        if (language == null) {
            throw new IllegalArgumentException("Language cannot be null");
        }
        if (!language.equals(this.currentLanguage)) {
            this.currentLanguage = language;
            invalidate();
        }
    }

    @Override
    public Language getCurrentLanguage() {
        return currentLanguage;
    }

    @Override
    public void setTheme(Theme theme) {
        if (theme == null) {
            throw new IllegalArgumentException("Theme cannot be null");
        }
        if (!theme.equals(this.currentTheme)) {
            this.currentTheme = theme;
            updateColors();
            invalidate();
        }
    }

    @Override
    public void propagateTheme(Theme theme) {
        // Leaf component
    }

    @Override
    public void propagateLanguage(Language language) {
        // Leaf component
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Sets the selected value.
     * Empty strings are treated as unset (no change).
     *
     * @param value the value to select, or null/empty to clear selection
     */
    public void setSelectedValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            // Empty values are ignored - use explicit null if you want to clear
            return;
        }
        if (!value.equals(this.selectedValue)) {
            this.selectedValue = value;
            invalidate();
        }
    }

    public String getSelectedValue() {
        return selectedValue;
    }

    public void setOnValueChangeListener(OnValueChangeListener listener) {
        this.onValueChangeListener = listener;
    }

    // --- Helpers ---

    private String getSelectedDisplayText() {
        if (options == null || currentLanguage == null) return selectedValue;
        for (SelectOption option : options) {
            if (option.getValue().equals(selectedValue)) {
                return option.getText(currentLanguage.getCode());
            }
        }
        return selectedValue;
    }

    private int findSelectedIndex() {
        if (options == null) return -1;
        for (int i = 0; i < options.size(); i++) {
            if (options.get(i).getValue().equals(selectedValue)) {
                return i;
            }
        }
        return -1;
    }

    private List<String> getDisplayTexts() {
        List<String> texts = new ArrayList<>();
        String lang = currentLanguage != null ? currentLanguage.getCode() : "en";
        for (SelectOption option : options) {
            texts.add(option.getText(lang));
        }
        return texts;
    }

    // --- Measurement ---

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float textWidth = textPaint != null ? textPaint.measureText(getSelectedDisplayText()) : 0;
        float desiredWidth = Math.max(
                textWidth + 2 * SelectDimensions.TRIGGER_PADDING_HORIZONTAL_PX
                        + SelectDimensions.CHEVRON_SIZE_PX + SelectDimensions.CHEVRON_MARGIN_PX,
                SelectDimensions.TRIGGER_MIN_WIDTH_PX);
        float desiredHeight = SelectDimensions.TRIGGER_HEIGHT_PX;

        int width = resolveSize((int) Math.ceil(desiredWidth), widthMeasureSpec);
        int height = resolveSize((int) Math.ceil(desiredHeight), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    // --- Drawing ---

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInitialized() || colors == null) return;

        float width = getWidth();
        float height = getHeight();

        // Background
        backgroundPaint.setColor(colors.triggerBackground);
        drawRectF.set(0, 0, width, height);
        canvas.drawRoundRect(drawRectF,
                SelectDimensions.TRIGGER_CORNER_RADIUS_PX,
                SelectDimensions.TRIGGER_CORNER_RADIUS_PX,
                backgroundPaint);

        // Text
        String displayText = getSelectedDisplayText();
        textPaint.setColor(colors.triggerText);
        textPaint.setTextSize(SelectDimensions.TRIGGER_TEXT_SIZE_PX);
        float textX = SelectDimensions.TRIGGER_PADDING_HORIZONTAL_PX;
        float textY = height / 2f - (textPaint.descent() + textPaint.ascent()) / 2f;
        canvas.drawText(displayText, textX, textY, textPaint);

        // Chevron ▾
        chevronPaint.setColor(colors.triggerChevron);
        chevronPaint.setTextSize(SelectDimensions.CHEVRON_SIZE_PX);
        float chevronX = width - SelectDimensions.TRIGGER_PADDING_HORIZONTAL_PX;
        float chevronY = height / 2f - (chevronPaint.descent() + chevronPaint.ascent()) / 2f;
        canvas.drawText("▾", chevronX, chevronY, chevronPaint);
    }

    // --- Touch ---

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isInitialized()) return false;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            float x = event.getX();
            float y = event.getY();
            if (x >= 0 && x <= getWidth() && y >= 0 && y <= getHeight()) {
                showPopup();
            }
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return true;
        }

        return super.onTouchEvent(event);
    }

    private void showPopup() {
        if (options == null || options.isEmpty()) {
            return; // Don't show popup if no options
        }

        // Reuse existing popup or create new one
        if (selectPopup == null) {
            selectPopup = new SelectPopup(getContext());
        }

        selectPopup.setTheme(currentTheme);

        int selectedIndex = findSelectedIndex();
        // Default to 0 if no valid selection, otherwise use found index
        int initialPosition = (selectedIndex >= 0) ? selectedIndex : 0;

        selectPopup.setData(getDisplayTexts(), initialPosition);
        selectPopup.setOnSelectionListener((position, displayValue) -> {
            if (position >= 0 && position < options.size()) {
                String newValue = options.get(position).getValue();
                if (!newValue.equals(selectedValue)) {
                    selectedValue = newValue;
                    invalidate();
                    if (onValueChangeListener != null) {
                        onValueChangeListener.onValueChange(newValue);
                    }
                }
            }
            selectPopup.dismiss();
            // NOTE: Don't null out selectPopup here - reuse it for next show
        });

        selectPopup.show();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // Cleanup popup when view is detached
        if (selectPopup != null && selectPopup.isShowing()) {
            selectPopup.dismiss();
        }
        selectPopup = null;
    }

    // ============================================================
    // BUILDER API
    // ============================================================

    @NonNull
    public static Builder create(@NonNull android.content.Context context,
                                 @NonNull Theme theme,
                                 @NonNull Language language,
                                 @NonNull List<SelectOption> options,
                                 @NonNull String selectedValue) {
        return new Builder(context, theme, language, options, selectedValue);
    }

    public static class Builder {
        private final android.content.Context context;
        private final Theme theme;
        private final Language language;
        private final List<SelectOption> options;
        private final String selectedValue;
        private int marginTop = 0;
        private int marginBottom = 0;
        private int marginLeft = 0;
        private int marginRight = 0;
        private boolean marginSet = false;
        private OnValueChangeListener onValueChange;

        private Builder(android.content.Context context, Theme theme, Language language,
                       List<SelectOption> options, String selectedValue) {
            this.context = context;
            this.theme = theme;
            this.language = language;
            this.options = options;
            this.selectedValue = selectedValue;
        }

        @NonNull
        public Builder marginTop(int top) {
            this.marginTop = top;
            this.marginSet = true;
            return this;
        }

        @NonNull
        public Builder marginBottom(int bottom) {
            this.marginBottom = bottom;
            this.marginSet = true;
            return this;
        }

        @NonNull
        public Builder marginLeft(int left) {
            this.marginLeft = left;
            this.marginSet = true;
            return this;
        }

        @NonNull
        public Builder marginRight(int right) {
            this.marginRight = right;
            this.marginSet = true;
            return this;
        }

        @NonNull
        public Builder margin(int left, int top, int right, int bottom) {
            this.marginLeft = left;
            this.marginTop = top;
            this.marginRight = right;
            this.marginBottom = bottom;
            this.marginSet = true;
            return this;
        }

        @NonNull
        public Builder onValueChange(@Nullable OnValueChangeListener listener) {
            this.onValueChange = listener;
            return this;
        }

        @NonNull
        public Select build() {
            Select select = new Select(context);
            select.setOptions(options);
            select.setSelectedValue(selectedValue);
            select.setTheme(theme);
            select.setLanguage(language);
            if (marginSet) {
                select.setMargin(marginLeft, marginTop, marginRight, marginBottom);
            }
            if (onValueChange != null) {
                select.setOnValueChangeListener(onValueChange);
            }
            return select;
        }
    }
}
