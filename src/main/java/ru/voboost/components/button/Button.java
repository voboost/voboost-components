package ru.voboost.components.button;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
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
import ru.voboost.components.button.ButtonContainerDimensions;

/**
 * Button component — pill-shaped button with optional description text.
 *
 * <p>Layout (horizontal, centerVertical):
 * <pre>
 * ┌──────────┐  Description line 1
 * │  Button  │  Description line 2 (multi-line)
 * └──────────┘
 * </pre>
 *
 * <p>When no description is set, only the button is shown.
 */
public class Button extends LinearLayout implements IThemable, ILocalizable {

    private Theme currentTheme = null;
    private Language currentLanguage = null;
    private ButtonDescriptionColors descColors;

    // Child views
    private ButtonPrimitive primitive;
    private TextView descView;

    // Data
    private Map<String, String> descData;

    // Margin (managed by parent Section)
    private int marginLeft = 0;
    private int marginTop = 0;
    private int marginRight = 0;
    private int marginBottom = 0;
    private boolean marginSet = false;

    // Custom padding (optional, overrides default)
    private Integer paddingTop = null;
    private Integer paddingBottom = null;
    private Integer paddingLeft = null;
    private Integer paddingRight = null;

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
     * Sets the padding for all sides.
     *
     * @param left   left padding in pixels
     * @param top    top padding in pixels
     * @param right  right padding in pixels
     * @param bottom bottom padding in pixels
     */
    public void padding(int left, int top, int right, int bottom) {
        this.paddingLeft = left;
        this.paddingTop = top;
        this.paddingRight = right;
        this.paddingBottom = bottom;
        primitive.padding(left, top, right, bottom);
    }

    public Button(Context context) {
        super(context);
        init();
    }

    public Button(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Button(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        Context ctx = getContext();
        Typeface typeface = Font.getRegular(ctx);

        // ButtonPrimitive (canvas-based pill button)
        primitive = new ButtonPrimitive(ctx);
        addView(primitive, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        // Description text (optional, to the right)
        descView = new TextView(ctx);
        descView.setTextSize(0, ButtonDescriptionDimensions.TEXT_SIZE_PX);
        descView.setTypeface(typeface);
        descView.setVisibility(GONE);
        LayoutParams descParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
        descParams.leftMargin = ButtonDescriptionDimensions.BUTTON_TO_TEXT_GAP_PX;
        addView(descView, descParams);

        // No padding - padding is managed by parent Section
    }

    // --- Public API ---

    /** Sets the button label text. */
    public void setText(String text) {
        primitive.setText(text);
    }

    /** Returns the button label text. */
    public String getText() {
        return primitive.getText();
    }

    /** Sets whether the button text should use bold font. */
    public void setBoldText(boolean bold) {
        primitive.setBoldText(bold);
    }

    /** Sets the description text to the right (localized, multi-line). */
    public void setDescription(Map<String, String> description) {
        this.descData = description;
        updateTexts();
    }

    /** Sets the button style (PRIMARY or SECONDARY). */
    public void setStyle(ButtonStyle style) {
        primitive.setStyle(style);
    }

    /** Returns the current button style. */
    public ButtonStyle getStyle() {
        return primitive.getStyle();
    }

    /** Sets the click listener. */
    public void setOnClickListener(android.view.View.OnClickListener listener) {
        primitive.setOnClickListener(listener);
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
    public boolean dispatchTouchEvent(MotionEvent event) {
        return primitive.dispatchTouchEvent(event);
    }

    // --- Theme & Language ---

    @Override
    public void setTheme(Theme theme) {
        if (theme == null) throw new IllegalArgumentException("Theme cannot be null");
        this.currentTheme = theme;
        this.descColors = ButtonTheme.getDescriptionColors(theme);
        updateColors();
        propagateTheme(theme);
    }

    @Override
    public void propagateTheme(Theme theme) {
        primitive.setTheme(theme);
    }

    @Override
    public void setLanguage(Language language) {
        if (language == null) throw new IllegalArgumentException("Language cannot be null");
        this.currentLanguage = language;
        updateTexts();
    }

    @Override
    public void propagateLanguage(Language language) {
        // No localizable children beyond description text
    }

    public Theme getCurrentTheme() { return currentTheme; }
    public Language getCurrentLanguage() { return currentLanguage; }

    // --- Internal ---

    private void updateColors() {
        descView.setTextColor(descColors.textColor);
    }

    private void updateTexts() {
        String langCode = currentLanguage != null ? currentLanguage.getCode() : "en";
        if (descData != null && !descData.isEmpty()) {
            descView.setText(descData.getOrDefault(langCode, descData.values().iterator().next()));
            descView.setVisibility(VISIBLE);
        } else {
            descView.setVisibility(GONE);
        }
    }

    // ============================================================
    // BUILDER API
    // ============================================================

    @NonNull
    public static Builder create(@NonNull android.content.Context context,
                                 @NonNull Theme theme,
                                 @NonNull Language language,
                                 @NonNull String text,
                                 @NonNull ButtonStyle style) {
        return new Builder(context, theme, language, text, style);
    }

    public static class Builder {
        private final android.content.Context context;
        private final Theme theme;
        private final Language language;
        private final String text;
        private final ButtonStyle style;
        private Map<String, String> description;
        private int marginTop = 0;
        private int marginBottom = 0;
        private int marginLeft = 0;
        private int marginRight = 0;
        private boolean marginSet = false;
        private Integer paddingTop = null;
        private Integer paddingBottom = null;
        private Integer paddingLeft = null;
        private Integer paddingRight = null;
        private boolean boldText = false;
        private android.view.View.OnClickListener onClickListener;

        private Builder(android.content.Context context, Theme theme, Language language,
                       String text, ButtonStyle style) {
            this.context = context;
            this.theme = theme;
            this.language = language;
            this.text = text;
            this.style = style;
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
        public Builder onClick(@Nullable android.view.View.OnClickListener listener) {
            this.onClickListener = listener;
            return this;
        }

        @NonNull
        public Builder padding(int left, int top, int right, int bottom) {
            this.paddingLeft = left;
            this.paddingTop = top;
            this.paddingRight = right;
            this.paddingBottom = bottom;
            return this;
        }

        @NonNull
        public Builder boldText(boolean bold) {
            this.boldText = bold;
            return this;
        }

        @NonNull
        public Button build() {
            Button button = new Button(context);
            button.setText(text);
            button.setStyle(style);
            button.setTheme(theme);
            button.setLanguage(language);
            if (description != null) {
                button.setDescription(description);
            }
            if (marginSet) {
                button.setMargin(marginLeft, marginTop, marginRight, marginBottom);
            }
            if (paddingTop != null || paddingBottom != null || paddingLeft != null || paddingRight != null) {
                button.padding(
                    paddingLeft != null ? paddingLeft : 0,
                    paddingTop != null ? paddingTop : 0,
                    paddingRight != null ? paddingRight : 0,
                    paddingBottom != null ? paddingBottom : 0
                );
            }
            if (onClickListener != null) {
                button.setOnClickListener(onClickListener);
            }
            button.setBoldText(boldText);
            return button;
        }
    }
}
