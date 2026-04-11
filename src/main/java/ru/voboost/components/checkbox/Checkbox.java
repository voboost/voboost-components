package ru.voboost.components.checkbox;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

import ru.voboost.components.font.Font;
import ru.voboost.components.i18n.ILocalizable;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.IThemable;
import ru.voboost.components.theme.Theme;

/**
 * Checkbox component — toggle switch with optional label and description.
 *
 * <p>
 * Layout:
 *
 * <pre>
 * ⬜  Label text (30px)              — checkbox & label centerVertical
 *     Description text (24px)        — below, aligned with label
 * </pre>
 *
 * <p>
 * When no label/description is set, only the toggle switch is shown.
 * Clicking the row toggles the checkbox.
 */
public class Checkbox extends LinearLayout implements IThemable, ILocalizable {

    private Theme currentTheme = null;
    private Language currentLanguage = null;
    private CheckboxTextColors textColors;

    // Child views
    private CheckboxPrimitive primitive;
    private LinearLayout textContainer;
    private TextView labelView;
    private TextView descView;

    // Data
    private Map<String, String> labelData;
    private Map<String, String> descData;
    private boolean labelBold = false;

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
     * Returns whether this checkbox has description text set.
     * Used by Section to compute default bottom margin.
     */
    public boolean hasDescription() {
        return descData != null && !descData.isEmpty();
    }

    /**
     * Callback for checked state changes.
     */
    public interface OnCheckedChangeListener {
        void onCheckedChange(boolean isChecked);
    }

    public Checkbox(Context context) {
        super(context);
        init();
    }

    public Checkbox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Checkbox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setPadding(0, 0, 0, 0);

        Context ctx = getContext();
        Typeface typeface = Font.getRegular(ctx);

        // CheckboxPrimitive (canvas toggle switch)
        primitive = new CheckboxPrimitive(ctx);
        LayoutParams cbParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        cbParams.leftMargin = CheckboxTextDimensions.CHECKBOX_MARGIN_START_PX;
        cbParams.topMargin = CheckboxTextDimensions.CHECKBOX_MARGIN_TOP_PX;
        addView(primitive, cbParams);

        // Vertical container for texts
        textContainer = new LinearLayout(ctx);
        textContainer.setOrientation(VERTICAL);
        LayoutParams containerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        containerParams.leftMargin = CheckboxTextDimensions.CHECKBOX_TO_LABEL_GAP_PX;
        containerParams.topMargin = CheckboxTextDimensions.CHECKBOX_MARGIN_TOP_PX;
        addView(textContainer, containerParams);

        // Label
        labelView = new TextView(ctx);
        labelView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, CheckboxTextDimensions.LABEL_TEXT_SIZE_PX);
        labelView.setTypeface(typeface);
        labelView.setVisibility(GONE);
        LayoutParams labelParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        labelParams.rightMargin = CheckboxTextDimensions.LABEL_MARGIN_END_PX;
        textContainer.addView(labelView, labelParams);

        // Description below
        descView = new TextView(ctx);
        descView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, CheckboxTextDimensions.DESCRIPTION_TEXT_SIZE_PX);
        descView.setTypeface(typeface);
        descView.setVisibility(GONE);
        LayoutParams descParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        descParams.topMargin = CheckboxTextDimensions.LABEL_TO_DESCRIPTION_GAP_PX;
        descParams.rightMargin = CheckboxTextDimensions.DESCRIPTION_MARGIN_END_PX;
        textContainer.addView(descView, descParams);

        // Click on entire row toggles checkbox
        setClickable(true);
    }

    @Override
    public boolean performClick() {
        boolean handled = super.performClick();
        if (!handled) {
            primitive.setCheckedAnimated(!primitive.isChecked());
            return true;
        }
        return handled;
    }

    // --- Public API ---

    /** Sets the label text (localized). */
    public void setLabel(Map<String, String> label) {
        this.labelData = label;
        updateTexts();
        updateLabelTypeface();
    }

    /** Sets the description text below (localized). */
    public void setDescription(Map<String, String> description) {
        this.descData = description;
        updateTexts();
    }

    /** Sets whether the label should be bold (for title-checkbox mode). */
    public void setLabelBold(boolean bold) {
        this.labelBold = bold;
        updateLabelTypeface();
    }

    private void updateLabelTypeface() {
        if (labelBold) {
            labelView.setTypeface(Font.getBold(getContext(), labelView.getText().toString()));
            // Increase text size by 2px for title-checkbox
            labelView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,
                CheckboxTextDimensions.LABEL_TEXT_SIZE_PX + 2);
            // Move text down by 2px and left by 1px
            labelView.setTranslationY(2f);
            labelView.setTranslationX(0f);
        } else {
            labelView.setTypeface(Font.getRegular(getContext()));
            labelView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX,
                CheckboxTextDimensions.LABEL_TEXT_SIZE_PX);
            labelView.setTranslationY(0f);
        }
    }

    // --- Delegated to CheckboxPrimitive ---

    /** Sets the checked state without animation. */
    public void setChecked(boolean checked) {
        primitive.setChecked(checked);
    }

    /** Sets the checked state with animation. */
    public void setCheckedAnimated(boolean checked) {
        primitive.setCheckedAnimated(checked);
    }

    /** Returns whether the checkbox is checked. */
    public boolean isChecked() {
        return primitive.isChecked();
    }

    /**
     * Sets a frozen mid-transition visual state for pixel-perfect testing.
     *
     * @param knobPosition 0.0=OFF, 1.0=ON
     * @param maskAlpha    0.0=no mask, 1.0=full mask
     */
    public void setTransitionState(float knobPosition, float maskAlpha) {
        primitive.setVisualStateForTest(knobPosition, maskAlpha);
    }

    /** Sets the checked change listener. */
    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        if (listener != null) {
            primitive.setOnCheckedChangeListener(listener::onCheckedChange);
        } else {
            primitive.setOnCheckedChangeListener(null);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        primitive.setEnabled(enabled);
    }

    @Override
    public boolean isEnabled() {
        return primitive.isEnabled();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Delegate to primitive
        return primitive.onTouchEvent(event);
    }

    /** Sets the alpha of the toggle switch only. */
    public void setPrimitiveAlpha(float alpha) {
        primitive.setAlpha(alpha);
    }

    // --- Theme & Language ---

    @Override
    public void setTheme(Theme theme) {
        if (theme == null)
            throw new IllegalArgumentException("Theme cannot be null");
        this.currentTheme = theme;
        this.textColors = CheckboxTheme.getTextColors(theme);
        updateColors();
        propagateTheme(theme);
    }

    @Override
    public void propagateTheme(Theme theme) {
        primitive.setTheme(theme);
    }

    @Override
    public void setLanguage(Language language) {
        if (language == null)
            throw new IllegalArgumentException("Language cannot be null");
        this.currentLanguage = language;
        updateTexts();
    }

    @Override
    public void propagateLanguage(Language language) {
        // No localizable children beyond texts
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public Language getCurrentLanguage() {
        return currentLanguage;
    }

    // --- Internal ---

    private void updateColors() {
        if (textColors == null)
            return;
        labelView.setTextColor(textColors.labelColor);
        descView.setTextColor(textColors.descriptionColor);
    }

    private void updateTexts() {
        String langCode = currentLanguage != null ? currentLanguage.getCode() : "en";

        if (labelData != null && !labelData.isEmpty()) {
            String labelText = labelData.get(langCode);
            if (labelText == null) {
                // Fallback to English, then first available
                labelText = labelData.get("en");
                if (labelText == null && !labelData.isEmpty()) {
                    labelText = labelData.values().iterator().next();
                }
            }
            labelView.setText(labelText != null ? labelText : "");
            labelView.setVisibility(VISIBLE);
        } else {
            labelView.setVisibility(GONE);
        }

        if (descData != null && !descData.isEmpty()) {
            String descText = descData.get(langCode);
            if (descText == null) {
                // Fallback to English, then first available
                descText = descData.get("en");
                if (descText == null && !descData.isEmpty()) {
                    descText = descData.values().iterator().next();
                }
            }
            descView.setText(descText != null ? descText : "");
            descView.setVisibility(VISIBLE);
            // No padding - padding is managed by parent Section
        } else {
            descView.setVisibility(GONE);
            // No padding - padding is managed by parent Section
        }
    }

    // ============================================================
    // BUILDER API
    // ============================================================

    @NonNull
    public static Builder create(@NonNull android.content.Context context,
                                 @NonNull Theme theme,
                                 @NonNull Language language,
                                 boolean checked) {
        return new Builder(context, theme, language, checked);
    }

    public static class Builder {
        private final android.content.Context context;
        private final Theme theme;
        private final Language language;
        private final boolean checked;
        private Map<String, String> label;
        private Map<String, String> description;
        private int marginTop = 0;
        private int marginBottom = 0;
        private int marginLeft = 0;
        private int marginRight = 0;
        private boolean marginSet = false;
        private OnCheckedChangeListener onCheckedChange;

        private Builder(android.content.Context context, Theme theme, Language language, boolean checked) {
            this.context = context;
            this.theme = theme;
            this.language = language;
            this.checked = checked;
        }

        @NonNull
        public Builder label(@Nullable Map<String, String> label) {
            this.label = label;
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
        public Builder onCheckedChange(@Nullable OnCheckedChangeListener listener) {
            this.onCheckedChange = listener;
            return this;
        }

        @NonNull
        public Checkbox build() {
            Checkbox checkbox = new Checkbox(context);
            checkbox.setChecked(checked);
            checkbox.setTheme(theme);
            checkbox.setLanguage(language);
            if (label != null) {
                checkbox.setLabel(label);
            }
            if (description != null) {
                checkbox.setDescription(description);
            }
            if (marginSet) {
                checkbox.setMargin(marginLeft, marginTop, marginRight, marginBottom);
            }
            if (onCheckedChange != null) {
                checkbox.setOnCheckedChangeListener(onCheckedChange);
            }
            return checkbox;
        }
    }
}

