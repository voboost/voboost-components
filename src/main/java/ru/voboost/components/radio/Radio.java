package ru.voboost.components.radio;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import ru.voboost.components.font.Font;
import ru.voboost.components.i18n.ILocalizable;
import ru.voboost.components.i18n.Language;
import ru.voboost.components.theme.IThemable;
import ru.voboost.components.theme.Theme;

/**
 * Radio component — segmented control with optional title and descriptions.
 *
 * <p>
 * Layout (vertical):
 *
 * <pre>
 * [Title text]               (optional, 32px)
 * [Description above]        (optional, 24px, gap 5px from title)
 * [RadioPrimitive]           (canvas-based segmented control)
 * [Description below]        (optional, 24px, gap 14px from primitive)
 * </pre>
 *
 * <p>
 * When no text is set, behaves identically to the previous canvas-only Radio.
 */
public class Radio extends LinearLayout implements IThemable, ILocalizable {

    private Theme currentTheme = null;
    private Language currentLanguage = null;
    private RadioTextColors textColors;

    // Child views
    private TextView titleView;
    private TextView descAboveView;
    private RadioPrimitive primitive;
    private TextView descBelowView;

    // Text data
    private Map<String, String> title;
    private Map<String, String> descriptionAbove;
    private Map<String, String> description;

    // Per-option handlers (UI changes on selection)
    private Map<String, Consumer<Radio>> optionHandlers;

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

    public Radio(Context context) {
        super(context);
        init();
    }

    public Radio(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Radio(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setClipChildren(false);
        setClipToPadding(false);
        setPadding(0, 0, 0, 0);

        Context ctx = getContext();
        Typeface typeface = Font.getRegular(ctx);

        // Title
        titleView = new TextView(ctx);
        titleView.setTextSize(0, RadioTextDimensions.TITLE_TEXT_SIZE_PX);
        titleView.setTypeface(typeface);
        titleView.setVisibility(GONE);
        addView(titleView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        // Description above
        descAboveView = new TextView(ctx);
        descAboveView.setTextSize(0, RadioTextDimensions.DESCRIPTION_TEXT_SIZE_PX);
        descAboveView.setTypeface(typeface);
        descAboveView.setVisibility(GONE);
        LayoutParams descAboveParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        descAboveParams.topMargin = RadioTextDimensions.TITLE_TO_DESCRIPTION_GAP_PX;
        descAboveParams.bottomMargin = RadioTextDimensions.DESCRIPTION_ABOVE_TO_PRIMITIVE_GAP_PX;
        addView(descAboveView, descAboveParams);

        // RadioPrimitive (canvas-based segmented control)
        primitive = new RadioPrimitive(ctx);
        addView(primitive, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        // Description below
        descBelowView = new TextView(ctx);
        descBelowView.setTextSize(0, RadioTextDimensions.DESCRIPTION_TEXT_SIZE_PX);
        descBelowView.setTypeface(typeface);
        descBelowView.setVisibility(GONE);
        LayoutParams descBelowParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        descBelowParams.topMargin = RadioTextDimensions.PRIMITIVE_TO_DESCRIPTION_GAP_PX;
        descBelowParams.bottomMargin = RadioTextDimensions.DESCRIPTION_BELOW_BOTTOM_GAP_PX;
        addView(descBelowView, descBelowParams);
    }

    // --- Text API (optional) ---

    /** Sets the title text (localized). */
    public void setTitle(Map<String, String> title) {
        this.title = title;
        updateTexts();
    }

    /** Sets the description text above the radio (localized). */
    public void setDescriptionAbove(Map<String, String> text) {
        this.descriptionAbove = text;
        updateTexts();
    }

    /** Sets the description text below the radio control (localized). */
    public void setDescription(Map<String, String> text) {
        this.description = text;
        updateTexts();
    }

    // --- Delegated to RadioPrimitive ---

    /** Sets the list of radio button options. */
    public void setButtons(List<RadioButton> buttons) {
        primitive.setButtons(buttons);
    }

    /** Sets the selected value without animation. */
    public void setSelectedValue(String value) {
        primitive.setSelectedValue(value);
    }

    /** Sets the selected value with optional callback trigger. */
    public void setSelectedValue(String value, boolean isTriggerCallback) {
        primitive.setSelectedValue(value, isTriggerCallback);
    }

    /** Returns the currently selected value. */
    public String getSelectedValue() {
        return primitive.getSelectedValue();
    }

    private OnValueChangeListener userListener;

    /** Sets the value change listener. Fires after per-option handler. */
    public void setOnValueChangeListener(OnValueChangeListener listener) {
        this.userListener = listener;
        ensurePrimitiveListener();
    }

    /** Registers a per-option handler for the given value. */
    public void setOptionHandler(String value, Consumer<Radio> handler) {
        if (optionHandlers == null) optionHandlers = new HashMap<>();
        optionHandlers.put(value, handler);
        ensurePrimitiveListener();
    }

    /** Installs primitive listener if option handlers or user listener exist. */
    private void ensurePrimitiveListener() {
        if (optionHandlers != null || userListener != null) {
            primitive.setOnValueChangeListener(newValue -> {
                applyOptionHandler(newValue);
                if (userListener != null) userListener.onValueChange(newValue);
            });
        }
    }

    /** Applies the per-option handler for the given value. */
    private void applyOptionHandler(String value) {
        if (optionHandlers == null) return;
        Consumer<Radio> handler = optionHandlers.get(value);
        if (handler != null) {
            handler.accept(this);
        }
    }

    /** Sets fixed total width for the radio items area (0 = auto by text). */
    public void setWidth(int widthPx) {
        primitive.setWidth(widthPx);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Delegate to primitive
        return primitive.onTouchEvent(event);
    }

    @Override
    public int getLayerType() {
        return primitive.getLayerType();
    }

    // --- Theme & Language ---

    @Override
    public void setTheme(Theme theme) {
        if (theme == null)
            throw new IllegalArgumentException("Theme cannot be null");
        this.currentTheme = theme;
        this.textColors = RadioTheme.getTextColors(theme);
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

    private void updateColors() {
        if (textColors == null)
            return;
        titleView.setTextColor(textColors.titleColor);
        descAboveView.setTextColor(textColors.descriptionColor);
        descBelowView.setTextColor(textColors.descriptionColor);
    }

    private void updateTexts() {
        String langCode = currentLanguage != null ? currentLanguage.getCode() : "en";

        if (title != null && !title.isEmpty()) {
            titleView.setText(title.getOrDefault(langCode, title.values().iterator().next()));
            titleView.setVisibility(VISIBLE);
        } else {
            titleView.setVisibility(GONE);
        }

        boolean hasDescAbove = descriptionAbove != null && !descriptionAbove.isEmpty();
        if (hasDescAbove) {
            descAboveView.setText(descriptionAbove.getOrDefault(langCode, descriptionAbove.values().iterator().next()));
            descAboveView.setVisibility(VISIBLE);
        } else {
            descAboveView.setVisibility(GONE);
        }

        // Gap after title: applied via primitive topMargin when descAbove is GONE
        LayoutParams primitiveParams = (LayoutParams) primitive.getLayoutParams();
        if (hasDescAbove) {
            primitiveParams.topMargin = 0;
        } else if (title != null) {
            primitiveParams.topMargin = RadioTextDimensions.TITLE_TO_DESCRIPTION_GAP_PX;
        } else {
            primitiveParams.topMargin = 0;
        }

        if (description != null && !description.isEmpty()) {
            descBelowView.setText(description.getOrDefault(langCode, description.values().iterator().next()));
            descBelowView.setVisibility(VISIBLE);
        } else {
            descBelowView.setVisibility(GONE);
        }
    }

    // ============================================================
    // BUILDER API
    // ============================================================

    @NonNull
    public static Builder create(@NonNull android.content.Context context,
                                 @NonNull Theme theme,
                                 @NonNull Language language,
                                 @NonNull List<RadioButton> buttons,
                                 @NonNull String selectedValue) {
        return new Builder(context, theme, language, buttons, selectedValue);
    }

    public static class Builder {
        private final android.content.Context context;
        private final Theme theme;
        private final Language language;
        private final List<RadioButton> buttons;
        private final String selectedValue;
        private Map<String, String> title;
        private Map<String, String> descriptionAbove;
        private Map<String, String> description;
        private int marginTop = 0;
        private int marginBottom = 0;
        private int marginLeft = 0;
        private int marginRight = 0;
        private boolean marginSet = false;
        private int width = 0;
        private OnValueChangeListener onValueChange;
        private final List<Map.Entry<String, Consumer<Radio>>> optionEntries = new ArrayList<>();

        private Builder(android.content.Context context, Theme theme, Language language,
                       List<RadioButton> buttons, String selectedValue) {
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
        public Builder on(String value, Consumer<Radio> handler) {
            optionEntries.add(new java.util.AbstractMap.SimpleEntry<>(value, handler));
            return this;
        }

        @NonNull
        public Builder width(int widthPx) {
            this.width = widthPx;
            return this;
        }

        @NonNull
        public Radio build() {
            Radio radio = new Radio(context);
            radio.setButtons(buttons);
            radio.setSelectedValue(selectedValue);
            radio.setTheme(theme);
            radio.setLanguage(language);
            if (title != null) {
                radio.setTitle(title);
            }
            if (descriptionAbove != null) {
                radio.setDescriptionAbove(descriptionAbove);
            }
            if (description != null) {
                radio.setDescription(description);
            }
            if (marginSet) {
                radio.setMargin(marginLeft, marginTop, marginRight, marginBottom);
            }
            if (width > 0) {
                radio.setWidth(width);
            }
            if (onValueChange != null) {
                radio.setOnValueChangeListener(onValueChange);
            }
            for (Map.Entry<String, Consumer<Radio>> entry : optionEntries) {
                radio.setOptionHandler(entry.getKey(), entry.getValue());
            }
            radio.applyOptionHandler(selectedValue);
            return radio;
        }
    }
}
