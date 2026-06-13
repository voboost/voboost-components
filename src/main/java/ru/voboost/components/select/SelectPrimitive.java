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

import ru.voboost.components.font.Font;
import ru.voboost.components.i18n.ILocalizable;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.IThemable;
import ru.voboost.components.theme.Theme;

/**
 * SelectPrimitive — internal canvas-based trigger (pill + text + chevron).
 * Package-private. Use {@link Select} (container) for the public API.
 */
class SelectPrimitive extends View implements IThemable, ILocalizable {
    private List<SelectOption> options = new ArrayList<>();
    private Language currentLanguage = null;
    private Theme currentTheme = null;
    private String selectedValue = "";
    private Select.OnValueChangeListener onValueChangeListener;

    private SelectColors colors;

    private SelectPopup selectPopup;

    private Paint backgroundPaint;
    private Paint textPaint;
    private Paint chevronPaint;

    private final RectF drawRectF = new RectF();

    public SelectPrimitive(Context context) {
        super(context);
        init();
    }

    public SelectPrimitive(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SelectPrimitive(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

    public void setOptions(List<SelectOption> options) {
        List<SelectOption> newOptions = options != null ? new ArrayList<>(options) : new ArrayList<>();
        if (java.util.Objects.equals(this.options, newOptions)) {
            return;
        }
        this.options = newOptions;
        requestLayout();
        invalidate();
    }

    @Override
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

    public void setSelectedValue(String value) {
        if (value == null || value.trim().isEmpty()) {
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

    public void setOnValueChangeListener(Select.OnValueChangeListener listener) {
        this.onValueChangeListener = listener;
    }

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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInitialized() || colors == null) return;

        float width = getWidth();
        float height = getHeight();

        backgroundPaint.setColor(colors.triggerBackground);
        drawRectF.set(0, 0, width, height);
        canvas.drawRoundRect(drawRectF,
                SelectDimensions.TRIGGER_CORNER_RADIUS_PX,
                SelectDimensions.TRIGGER_CORNER_RADIUS_PX,
                backgroundPaint);

        String displayText = getSelectedDisplayText();
        textPaint.setColor(colors.triggerText);
        textPaint.setTextSize(SelectDimensions.TRIGGER_TEXT_SIZE_PX);
        float textX = SelectDimensions.TRIGGER_PADDING_HORIZONTAL_PX;
        float textY = height / 2f - (textPaint.descent() + textPaint.ascent()) / 2f;
        canvas.drawText(displayText, textX, textY, textPaint);

        chevronPaint.setColor(colors.triggerChevron);
        chevronPaint.setTextSize(SelectDimensions.CHEVRON_SIZE_PX);
        float chevronX = width - SelectDimensions.TRIGGER_PADDING_HORIZONTAL_PX;
        float chevronY = height / 2f - (chevronPaint.descent() + chevronPaint.ascent()) / 2f;
        canvas.drawText("▾", chevronX, chevronY, chevronPaint);
    }

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
            return;
        }

        if (selectPopup == null) {
            selectPopup = new SelectPopup(getContext());
        }

        selectPopup.setTheme(currentTheme);

        int selectedIndex = findSelectedIndex();
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
        });

        selectPopup.show();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (selectPopup != null && selectPopup.isShowing()) {
            selectPopup.dismiss();
        }
        selectPopup = null;
    }
}
