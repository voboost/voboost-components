package ru.voboost.components.select;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import ru.voboost.components.font.Font;
import ru.voboost.components.i18n.ILocalizable;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.IThemable;
import ru.voboost.components.theme.Theme;

/**
 * Select component — titled trigger + popup with 3D WheelView.
 *
 * <p>Layout (vertical), mirroring Radio/Button:
 * <pre>
 * [Title]                 (optional, 32px)
 * [Description above]     (optional, 24px)
 * [SelectPrimitive ▾]  [Description right]   (horizontal row; right text optional, 24px)
 * </pre>
 *
 * <p>When no text is set, behaves as the bare trigger (70px tall).
 */
public class Select extends LinearLayout implements IThemable, ILocalizable {

    /** Text size of the title (matches Radio title). */
    private static final float TITLE_TEXT_SIZE_PX = 32f;
    /** Text size of descriptions above and to the right (matches hint text). */
    private static final float DESCRIPTION_TEXT_SIZE_PX = 24f;
    /** Gap between trigger and right description. */
    private static final int TRIGGER_TO_DESC_GAP_PX = 21;

    private Theme currentTheme = null;
    private Language currentLanguage = null;

    private TextView titleView;
    private TextView descAboveView;
    private LinearLayout row;
    private SelectPrimitive primitive;
    private TextView descRightView;

    private Mode mode = Mode.CURVED;

    private Map<String, String> title;
    private Map<String, String> descriptionAbove;
    private Map<String, String> description;

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

    /**
     * Callback for value selection changes.
     */
    public interface OnValueChangeListener {
        void onValueChange(String newValue);
    }

    /**
     * Wheel rendering mode for the popup picker.
     */
    public enum Mode {
        /** Flat wheel (production-faithful WheelDefault: no 3D, no fade). */
        FLAT,
        /** Curved 3D wheel with atmospheric fade on side items. */
        CURVED
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

    private void init() {
        setOrientation(VERTICAL);
        setLayerType(LAYER_TYPE_HARDWARE, null);

        Context ctx = getContext();
        Typeface typeface = Font.getRegular(ctx);

        titleView = new TextView(ctx);
        titleView.setTextSize(0, TITLE_TEXT_SIZE_PX);
        titleView.setTypeface(typeface);
        titleView.setVisibility(GONE);
        addView(titleView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        descAboveView = new TextView(ctx);
        descAboveView.setTextSize(0, DESCRIPTION_TEXT_SIZE_PX);
        descAboveView.setTypeface(typeface);
        descAboveView.setVisibility(GONE);
        LayoutParams descAboveParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        descAboveParams.topMargin = 37;
        descAboveParams.bottomMargin = 47;
        addView(descAboveView, descAboveParams);

        row = new LinearLayout(ctx);
        row.setOrientation(HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        primitive = new SelectPrimitive(ctx);
        row.addView(primitive, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        descRightView = new TextView(ctx);
        descRightView.setTextSize(0, DESCRIPTION_TEXT_SIZE_PX);
        descRightView.setTypeface(typeface);
        descRightView.setVisibility(GONE);
        LayoutParams descRightParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
        descRightParams.leftMargin = TRIGGER_TO_DESC_GAP_PX;
        row.addView(descRightView, descRightParams);

        addView(row, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    // --- Text API (optional) ---

    /** Sets the title text (localized). */
    public void setTitle(Map<String, String> title) {
        this.title = title;
        updateTexts();
    }

    /** Sets the description text above the trigger (localized). */
    public void setDescriptionAbove(Map<String, String> text) {
        this.descriptionAbove = text;
        updateTexts();
    }

    /** Sets the description text to the right of the trigger (localized). */
    public void setDescription(Map<String, String> text) {
        this.description = text;
        updateTexts();
    }

    // --- Delegated to SelectPrimitive ---

    /** Sets the list of select options. Null is treated as empty list. */
    public void setOptions(List<SelectOption> options) {
        primitive.setOptions(options);
    }

    /** Sets the selected value. Empty strings are ignored. */
    public void setSelectedValue(String value) {
        primitive.setSelectedValue(value);
    }

    /** Returns the currently selected value. */
    public String getSelectedValue() {
        return primitive.getSelectedValue();
    }

    public void setOnValueChangeListener(OnValueChangeListener listener) {
        primitive.setOnValueChangeListener(listener);
    }

    /** Sets the wheel rendering mode (FLAT or CURVED). Default is CURVED. */
    public void setMode(Mode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("Mode cannot be null");
        }
        this.mode = mode;
        primitive.setMode(mode);
    }

    /** Sets the popup confirm-button text (localized). Null resets to the default. */
    public void setConfirmText(Map<String, String> text) {
        primitive.setConfirmText(text);
    }

    /** Sets the popup cancel-button text (localized). Null resets to the default. */
    public void setCancelText(Map<String, String> text) {
        primitive.setCancelText(text);
    }

    // --- Theme & Language ---

    @Override
    public void setTheme(Theme theme) {
        if (theme == null) {
            throw new IllegalArgumentException("Theme cannot be null");
        }
        this.currentTheme = theme;
        updateColors();
        propagateTheme(theme);
        invalidate();
    }

    @Override
    public void propagateTheme(Theme theme) {
        primitive.setTheme(theme);
    }

    @Override
    public void setLanguage(Language language) {
        if (language == null) {
            throw new IllegalArgumentException("Language cannot be null");
        }
        this.currentLanguage = language;
        updateTexts();
        propagateLanguage(language);
    }

    @Override
    public void propagateLanguage(Language language) {
        primitive.setLanguage(language);
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public Language getCurrentLanguage() {
        return currentLanguage;
    }

    // --- Internal ---

    private boolean isDark() {
        return currentTheme != null && currentTheme.getValue() != null && currentTheme.getValue().endsWith("-dark");
    }

    private void updateColors() {
        int titleColor = isDark() ? Color.parseColor("#ffffff") : Color.parseColor("#1a1e28");
        int descColor = isDark() ? Color.parseColor("#80ffffff") : Color.parseColor("#801a1e28");
        titleView.setTextColor(titleColor);
        descAboveView.setTextColor(descColor);
        descRightView.setTextColor(descColor);
    }

    private void updateTexts() {
        String langCode = currentLanguage != null ? currentLanguage.getCode() : "en";

        if (title != null && !title.isEmpty()) {
            titleView.setText(title.getOrDefault(langCode, title.values().iterator().next()));
            titleView.setVisibility(VISIBLE);
        } else {
            titleView.setVisibility(GONE);
        }

        if (descriptionAbove != null && !descriptionAbove.isEmpty()) {
            descAboveView.setText(descriptionAbove.getOrDefault(langCode, descriptionAbove.values().iterator().next()));
            descAboveView.setVisibility(VISIBLE);
        } else {
            descAboveView.setVisibility(GONE);
        }

        // Gap from title to the trigger row mirrors Radio: 37px when only a title is set,
        // 0 when a description-above is present (its own bottom margin provides the gap).
        boolean hasTitle = title != null && !title.isEmpty();
        boolean hasDescAbove = descriptionAbove != null && !descriptionAbove.isEmpty();
        LayoutParams rowParams = (LayoutParams) row.getLayoutParams();
        rowParams.topMargin = (hasTitle && !hasDescAbove) ? 37 : 0;

        if (description != null && !description.isEmpty()) {
            descRightView.setText(description.getOrDefault(langCode, description.values().iterator().next()));
            descRightView.setVisibility(VISIBLE);
        } else {
            descRightView.setVisibility(GONE);
        }
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
        private Map<String, String> title;
        private Map<String, String> descriptionAbove;
        private Map<String, String> description;
        private int marginTop = 0;
        private int marginBottom = 0;
        private int marginLeft = 0;
        private int marginRight = 0;
        private boolean marginSet = false;
        private OnValueChangeListener onValueChange;
        private Mode mode = Mode.CURVED;
        private Map<String, String> confirmText;
        private Map<String, String> cancelText;

        private Builder(android.content.Context context, Theme theme, Language language,
                       List<SelectOption> options, String selectedValue) {
            this.context = context;
            this.theme = theme;
            this.language = language;
            this.options = options;
            this.selectedValue = selectedValue;
        }

        @NonNull
        public Builder title(@Nullable Map<String, String> title) {
            this.title = title;
            return this;
        }

        @NonNull
        public Builder descriptionAbove(@Nullable Map<String, String> descriptionAbove) {
            this.descriptionAbove = descriptionAbove;
            return this;
        }

        @NonNull
        public Builder description(@Nullable Map<String, String> description) {
            this.description = description;
            return this;
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
        public Builder mode(@NonNull Mode mode) {
            this.mode = mode;
            return this;
        }

        @NonNull
        public Builder confirmText(@Nullable Map<String, String> text) {
            this.confirmText = text;
            return this;
        }

        @NonNull
        public Builder cancelText(@Nullable Map<String, String> text) {
            this.cancelText = text;
            return this;
        }

        @NonNull
        public Select build() {
            Select select = new Select(context);
            select.setOptions(options);
            select.setSelectedValue(selectedValue);
            select.setTheme(theme);
            select.setLanguage(language);
            if (title != null) {
                select.setTitle(title);
            }
            if (descriptionAbove != null) {
                select.setDescriptionAbove(descriptionAbove);
            }
            if (description != null) {
                select.setDescription(description);
            }
            if (marginSet) {
                select.setMargin(marginLeft, marginTop, marginRight, marginBottom);
            }
            if (onValueChange != null) {
                select.setOnValueChangeListener(onValueChange);
            }
            select.setMode(mode);
            if (confirmText != null) {
                select.setConfirmText(confirmText);
            }
            if (cancelText != null) {
                select.setCancelText(cancelText);
            }
            return select;
        }
    }
}
