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
 * Button component — pill-shaped button with optional title, description above, and description
 * text to the right.
 *
 * <p>Layout (vertical container holding a horizontal row):
 * <pre>
 * [Title]                 (optional, 32px)
 * [Description above]     (optional, 24px)
 * ┌──────────┐  Description to the right (optional, multi-line)
 * │  Button  │
 * └──────────┘
 * </pre>
 *
 * <p>When no text is set, only the pill button row is shown (70px tall).
 */
public class Button extends LinearLayout implements IThemable, ILocalizable {

    private Theme currentTheme = null;
    private Language currentLanguage = null;
    private ButtonDescriptionColors descColors;

    // Child views
    private TextView titleView;
    private TextView descAboveView;
    private LinearLayout row;
    private ButtonPrimitive primitive;
    private TextView descView;

    // Data
    private Map<String, String> titleData;
    private Map<String, String> descAboveData;
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
        setOrientation(VERTICAL);

        Context ctx = getContext();
        Typeface typeface = Font.getRegular(ctx);

        // Title (optional, above)
        titleView = new TextView(ctx);
        titleView.setTextSize(0, 32f);
        titleView.setTypeface(typeface);
        titleView.setVisibility(GONE);
        addView(titleView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        // Description above (optional, below title)
        descAboveView = new TextView(ctx);
        descAboveView.setTextSize(0, 24f);
        descAboveView.setTypeface(typeface);
        descAboveView.setVisibility(GONE);
        LayoutParams descAboveParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        descAboveParams.topMargin = 37;
        descAboveParams.bottomMargin = 47;
        addView(descAboveView, descAboveParams);

        // Horizontal row: ButtonPrimitive + description to the right
        row = new LinearLayout(ctx);
        row.setOrientation(HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        // ButtonPrimitive (canvas-based pill button)
        primitive = new ButtonPrimitive(ctx);
        row.addView(primitive, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // Description text (optional, to the right)
        descView = new TextView(ctx);
        descView.setTextSize(0, ButtonDescriptionDimensions.TEXT_SIZE_PX);
        descView.setTypeface(typeface);
        descView.setVisibility(GONE);
        LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        descParams.leftMargin = ButtonDescriptionDimensions.BUTTON_TO_TEXT_GAP_PX;
        row.addView(descView, descParams);

        addView(row, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

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

    /** Sets the title text above the button (localized). */
    public void setTitle(Map<String, String> title) {
        this.titleData = title;
        updateTexts();
    }

    /** Sets the description text above the button, below the title (localized). */
    public void setDescriptionAbove(Map<String, String> description) {
        this.descAboveData = description;
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
        titleView.setTextColor(descColors.textColor);
        descAboveView.setTextColor(descColors.textColor);
        descView.setTextColor(descColors.textColor);
    }

    private void updateTexts() {
        String langCode = currentLanguage != null ? currentLanguage.getCode() : "en";

        if (titleData != null && !titleData.isEmpty()) {
            titleView.setText(titleData.getOrDefault(langCode, titleData.values().iterator().next()));
            titleView.setVisibility(VISIBLE);
        } else {
            titleView.setVisibility(GONE);
        }

        if (descAboveData != null && !descAboveData.isEmpty()) {
            descAboveView.setText(descAboveData.getOrDefault(langCode, descAboveData.values().iterator().next()));
            descAboveView.setVisibility(VISIBLE);
        } else {
            descAboveView.setVisibility(GONE);
        }

        // Gap from title to the button row mirrors Radio: 37px when only a title is set,
        // 0 when a description-above is present (its own bottom margin provides the gap).
        boolean hasTitle = titleData != null && !titleData.isEmpty();
        boolean hasDescAbove = descAboveData != null && !descAboveData.isEmpty();
        LinearLayout.LayoutParams rowParams = (LinearLayout.LayoutParams) row.getLayoutParams();
        rowParams.topMargin = (hasTitle && !hasDescAbove) ? 37 : 0;

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
        private Map<String, String> title;
        private Map<String, String> descriptionAbove;
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
            if (title != null) {
                button.setTitle(title);
            }
            if (descriptionAbove != null) {
                button.setDescriptionAbove(descriptionAbove);
            }
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
