package ru.voboost.components.buttons;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ru.voboost.components.button.Button;
import ru.voboost.components.button.ButtonStyle;
import ru.voboost.components.font.Font;
import ru.voboost.components.i18n.ILocalizable;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.IThemable;
import ru.voboost.components.theme.Theme;

/**
 * Buttons — multiple Buttons in a row + optional title/description above + optional right text +
 * optional description below.
 *
 * <p>Layout:
 * <pre>
 * [Title]                 (optional, 32px)
 * [Description above]     (optional, 24px)
 * ┌───────┐ ┌─────────┐  Right text (optional)
 * │ Btn 1 │ │ Btn 2   │
 * └───────┘ └─────────┘
 * Description text below (optional, marginTop 14px)
 * </pre>
 *
 * Supports {@code selectedValue} — the selected button gets PRIMARY style, others SECONDARY.
 */
public class Buttons extends LinearLayout implements IThemable, ILocalizable {

    private Theme currentTheme = null;
    private Language currentLanguage = null;
    private ButtonsColors colors;

    // Title and description-above (optional)
    private TextView titleView;
    private TextView descAboveView;
    // Top row: buttons + optional right text
    private LinearLayout topRow;
    private LinearLayout buttonsContainer;
    private TextView rightTextView;
    // Description below
    private TextView descView;

    // Data
    private List<ButtonConfig> buttonConfigs = new ArrayList<>();
    List<Button> buttonViews = new ArrayList<>();
    private Map<String, String> titleData;
    private Map<String, String> descAboveData;
    private Map<String, String> rightTextData;
    private Map<String, String> descData;
    private String selectedValue = null;

    // Listener
    public interface OnValueChangeListener {
        void onValueChange(String newValue);
    }
    private OnValueChangeListener valueChangeListener;

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

    public Buttons(Context context) {
        super(context);
        init();
    }

    public Buttons(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Buttons(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);

        Context ctx = getContext();
        Typeface typeface = Font.getRegular(ctx);

        // Title (optional, above everything)
        titleView = new TextView(ctx);
        titleView.setTextSize(0, 32f);
        titleView.setTypeface(typeface);
        titleView.setVisibility(GONE);
        addView(titleView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        // Description above (optional)
        descAboveView = new TextView(ctx);
        descAboveView.setTextSize(0, 24f);
        descAboveView.setTypeface(typeface);
        descAboveView.setVisibility(GONE);
        LayoutParams descAboveParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        descAboveParams.topMargin = 37;
        descAboveParams.bottomMargin = 47;
        addView(descAboveView, descAboveParams);

        // Top row: horizontal layout for buttons + right text
        topRow = new LinearLayout(ctx);
        topRow.setOrientation(HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);
        addView(topRow, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        // Buttons container
        buttonsContainer = new LinearLayout(ctx);
        buttonsContainer.setOrientation(HORIZONTAL);
        topRow.addView(buttonsContainer, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        // Right text (optional)
        rightTextView = new TextView(ctx);
        rightTextView.setTextSize(0, ButtonsDimensions.TEXT_SIZE_PX);
        rightTextView.setTypeface(typeface);
        rightTextView.setVisibility(GONE);
        LayoutParams rightTextParams = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
        rightTextParams.leftMargin = ButtonsDimensions.BUTTONS_TO_TEXT_GAP_PX;
        topRow.addView(rightTextView, rightTextParams);

        // Description below
        descView = new TextView(ctx);
        descView.setTextSize(0, ButtonsDimensions.TEXT_SIZE_PX);
        descView.setTypeface(typeface);
        descView.setVisibility(GONE);
        LayoutParams descParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        descParams.topMargin = ButtonsDimensions.BUTTONS_TO_DESCRIPTION_GAP_PX;
        addView(descView, descParams);
    }

    // --- Public API ---

    public void setButtons(List<ButtonConfig> buttons) {
        this.buttonConfigs = buttons != null ? buttons : new ArrayList<>();
        rebuildButtons();
    }

    public void setRightText(Map<String, String> text) {
        this.rightTextData = text;
        updateTexts();
    }

    public void setDescription(Map<String, String> text) {
        this.descData = text;
        updateTexts();
    }

    /** Sets the title text above the buttons (localized). */
    public void setTitle(Map<String, String> text) {
        this.titleData = text;
        updateTexts();
    }

    /** Sets the description text above the buttons, below the title (localized). */
    public void setDescriptionAbove(Map<String, String> text) {
        this.descAboveData = text;
        updateTexts();
    }

    public void setSelectedValue(String value) {
        this.selectedValue = value;
        updateButtonStyles();
    }

    public String getSelectedValue() {
        return selectedValue;
    }

    public void setOnValueChangeListener(OnValueChangeListener listener) {
        this.valueChangeListener = listener;
    }

    // --- Theme & Language ---

    @Override
    public void setTheme(Theme theme) {
        if (theme == null) throw new IllegalArgumentException("Theme cannot be null");
        this.currentTheme = theme;
        this.colors = ButtonsTheme.getColors(theme);
        updateColors();
        propagateTheme(theme);
    }

    @Override
    public void propagateTheme(Theme theme) {
        for (Button btn : buttonViews) {
            btn.setTheme(theme);
        }
    }

    @Override
    public void setLanguage(Language language) {
        if (language == null) throw new IllegalArgumentException("Language cannot be null");
        this.currentLanguage = language;
        updateTexts();
    }

    @Override
    public void propagateLanguage(Language language) {
        // Buttons have no localization
    }

    public Theme getCurrentTheme() { return currentTheme; }
    public Language getCurrentLanguage() { return currentLanguage; }

    // --- Internal ---

    private void rebuildButtons() {
        Context ctx = getContext();
        int oldSize = buttonViews.size();
        int newSize = buttonConfigs.size();

        // Reuse existing buttons, create new ones if needed, remove excess
        for (int i = 0; i < newSize; i++) {
            Button btn;
            ButtonConfig config = buttonConfigs.get(i);

            if (i < oldSize) {
                // Reuse existing button
                btn = buttonViews.get(i);
                btn.setText(config.getText());

                // Update click listener with new value
                final String value = config.getValue();
                btn.setOnClickListener(v -> {
                    selectedValue = value;
                    updateButtonStyles();
                    if (valueChangeListener != null) {
                        valueChangeListener.onValueChange(value);
                    }
                });
            } else {
                // Create new button
                btn = new Button(ctx);
                btn.setText(config.getText());

                if (currentTheme != null) {
                    btn.setTheme(currentTheme);
                }

                LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.leftMargin = ButtonsDimensions.BUTTON_GAP_PX;
                btn.setLayoutParams(params);

                final String value = config.getValue();
                btn.setOnClickListener(v -> {
                    selectedValue = value;
                    updateButtonStyles();
                    if (valueChangeListener != null) {
                        valueChangeListener.onValueChange(value);
                    }
                });

                buttonViews.add(btn);
                buttonsContainer.addView(btn);
            }
        }

        // Remove excess buttons
        for (int i = newSize; i < oldSize; i++) {
            buttonsContainer.removeView(buttonViews.get(i));
        }
        // Trim the list
        if (newSize < oldSize) {
            buttonViews.subList(newSize, oldSize).clear();
        }

        updateButtonStyles();
    }

    private void updateButtonStyles() {
        // Buttons are independent secondary actions — there is no selected/primary state.
        for (int i = 0; i < buttonViews.size(); i++) {
            buttonViews.get(i).setStyle(ButtonStyle.SECONDARY);
        }
    }

    private void updateColors() {
        titleView.setTextColor(colors.textColor);
        descAboveView.setTextColor(colors.textColor);
        rightTextView.setTextColor(colors.textColor);
        descView.setTextColor(colors.textColor);
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

        // Gap from title to the buttons row mirrors Radio: 37px when only a title is set,
        // 0 when a description-above is present (its own bottom margin provides the gap).
        boolean hasTitle = titleData != null && !titleData.isEmpty();
        boolean hasDescAbove = descAboveData != null && !descAboveData.isEmpty();
        LayoutParams topRowParams = (LayoutParams) topRow.getLayoutParams();
        topRowParams.topMargin = (hasTitle && !hasDescAbove) ? 37 : 0;

        if (rightTextData != null && !rightTextData.isEmpty()) {
            rightTextView.setText(rightTextData.getOrDefault(langCode, rightTextData.values().iterator().next()));
            rightTextView.setVisibility(VISIBLE);
        } else {
            rightTextView.setVisibility(GONE);
        }

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
                                 @NonNull List<ButtonConfig> buttons,
                                 @NonNull String selectedValue) {
        return new Builder(context, theme, language, buttons, selectedValue);
    }

    public static class Builder {
        private final android.content.Context context;
        private final Theme theme;
        private final Language language;
        private final List<ButtonConfig> buttons;
        private final String selectedValue;
        private Map<String, String> title;
        private Map<String, String> descriptionAbove;
        private Map<String, String> rightText;
        private Map<String, String> description;
        private int marginTop = 0;
        private int marginBottom = 0;
        private int marginLeft = 0;
        private int marginRight = 0;
        private boolean marginSet = false;
        private OnValueChangeListener onValueChange;

        private Builder(android.content.Context context, Theme theme, Language language,
                       List<ButtonConfig> buttons, String selectedValue) {
            this.context = context;
            this.theme = theme;
            this.language = language;
            this.buttons = buttons;
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
        public Builder rightText(@Nullable Map<String, String> rightText) {
            this.rightText = rightText;
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
        public Buttons build() {
            Buttons buttonsView = new Buttons(context);
            buttonsView.setButtons(buttons);
            buttonsView.setSelectedValue(selectedValue);
            buttonsView.setTheme(theme);
            buttonsView.setLanguage(language);
            if (title != null) {
                buttonsView.setTitle(title);
            }
            if (descriptionAbove != null) {
                buttonsView.setDescriptionAbove(descriptionAbove);
            }
            if (rightText != null) {
                buttonsView.setRightText(rightText);
            }
            if (description != null) {
                buttonsView.setDescription(description);
            }
            if (marginSet) {
                buttonsView.setMargin(marginLeft, marginTop, marginRight, marginBottom);
            }
            if (onValueChange != null) {
                buttonsView.setOnValueChangeListener(onValueChange);
            }
            return buttonsView;
        }
    }
}
