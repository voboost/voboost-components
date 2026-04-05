package ru.voboost.components.buttons;

import androidx.annotation.NonNull;
import ru.voboost.components.button.ButtonStyle;

/**
 * Configuration for a single button within Buttons.
 */
public class ButtonConfig {
    @NonNull
    private final String value;
    @NonNull
    private final String text;
    @NonNull
    private final ButtonStyle style;

    public ButtonConfig(@NonNull String value, @NonNull String text, @NonNull ButtonStyle style) {
        if (value == null) throw new IllegalArgumentException("ButtonConfig value cannot be null");
        if (text == null) throw new IllegalArgumentException("ButtonConfig text cannot be null");
        if (style == null) throw new IllegalArgumentException("ButtonConfig style cannot be null");
        this.value = value;
        this.text = text;
        this.style = style;
    }

    public ButtonConfig(@NonNull String value, @NonNull String text) {
        this(value, text, ButtonStyle.SECONDARY);
    }

    @NonNull
    public String getValue() { return value; }
    @NonNull
    public String getText() { return text; }
    @NonNull
    public ButtonStyle getStyle() { return style; }
}
